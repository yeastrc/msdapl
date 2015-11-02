/**
 * ProjectInstrumentTimeApprover.java
 * @author Vagisha Sharma
 * Jun 15, 2011
 */
package org.uwpr.scheduler;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.uwpr.instrumentlog.ProjectInstrumentUsageDAO;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.www.user.Groups;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class ProjectInstrumentTimeApprover {

	private static final ProjectInstrumentTimeApprover instance = new ProjectInstrumentTimeApprover();
	
	private static final Logger log = Logger.getLogger(ProjectInstrumentTimeApprover.class);
	
	public static int HOURS_QUOTA_BILLED_PROJECT = 24 * 7;
	public static int HOURS_QUOTA_FREE_PROJECT = 24 * 5;
	
	private ProjectInstrumentTimeApprover() {}
	
	public static ProjectInstrumentTimeApprover getInstance() {
		return instance;
	}
	
	public boolean billedProjectExceedsQuota(int projectId, int instrumentId, User user,
			List<? extends UsageBlockBase> blocksBeingScheduled) throws SQLException {
		
		return billedProjectExceedsQuota(projectId, instrumentId, user, blocksBeingScheduled, null);
	}
	
	public boolean billedProjectExceedsQuota(int projectId, int instrumentId, User user,
			List<? extends UsageBlockBase> blocksBeingScheduled,
			List<? extends UsageBlockBase> ignoreBlocks) throws SQLException {
		
		// If the user is an admin return true
		Groups groupsMan = Groups.getInstance();
		
		if (groupsMan.isMember(user.getResearcher().getID(), "administrators"))
			return false;
		
		// The total requested time for this project beyond the current date
		// should not exceed 24 * 7 hours
		
		Set<Integer> ignoreBlockIds = new HashSet<Integer>();
		if(ignoreBlocks != null) {
			for(UsageBlockBase blk: ignoreBlocks) {
				ignoreBlockIds.add(blk.getID());
			}
		}
		
		// get the time scheduled for this project beyond the current date
		Date now = new Date();
		List<UsageBlockBase> usageBlocks = ProjectInstrumentUsageDAO.getInstance().getUsageBlocksForProjectInRange(projectId, now, null);
		int totalhours = 0;
		for(UsageBlockBase block: usageBlocks) {
			
			if(ignoreBlockIds.contains(block.getID()))
				continue;
			
			if(block.getInstrumentID() != instrumentId)
				continue;
			Date sDate = block.getStartDate();
			Date eDate = block.getEndDate();
			totalhours += (eDate.getTime() - sDate.getTime()) / (1000 * 60 * 60);
		}
		
		// add up the time for the blocks being scheduled now
		for(UsageBlockBase block: blocksBeingScheduled) {
			
			if(block.getInstrumentID() != instrumentId)
				continue;
			Date sDate = block.getStartDate();
			Date eDate = block.getEndDate();
			totalhours += (eDate.getTime() - sDate.getTime()) / (1000 * 60 * 60);
		}

		log.info("Total hours scheduled for project beyond "+now+": "+totalhours);
		if(totalhours >= HOURS_QUOTA_BILLED_PROJECT)
			return true;
		
		return false;
	}
	
	public boolean subsidizedProjectExceedsQuota(int projectId, User user,
			List<? extends UsageBlockBase> blocksBeingScheduled) throws SQLException {
		
		return subsidizedProjectExceedsQuota(projectId, user, blocksBeingScheduled, null);
	}
	
	public boolean subsidizedProjectExceedsQuota(int projectId, User user,
			List<? extends UsageBlockBase> blocksBeingScheduled, List<? extends UsageBlockBase> ignoreBlocks) throws SQLException {
		
		// If the user is an admin return true
		Groups groupsMan = Groups.getInstance();
		
		if (groupsMan.isMember(user.getResearcher().getID(), "administrators"))
			return false;
		
		// The total requested time for this project
		// should not exceed 24 * 5 hours
		
		Set<Integer> ignoreBlockIds = new HashSet<Integer>();
		if(ignoreBlocks != null) {
			for(UsageBlockBase blk: ignoreBlocks) {
				ignoreBlockIds.add(blk.getID());
			}
		}
		
		// get all the time scheduled for this project
		List<UsageBlockBase> usageBlocks = ProjectInstrumentUsageDAO.getInstance().getUsageBlocksForProjectInRange(projectId, null, null);
		int totalhours = 0;
		for(UsageBlockBase block: usageBlocks) {
			
			if(ignoreBlockIds.contains(block.getID()))
				continue;
			Date sDate = block.getStartDate();
			Date eDate = block.getEndDate();
			totalhours += (eDate.getTime() - sDate.getTime()) / (1000 * 60 * 60);
		}
		
		// add up the time for the blocks being scheduled now
		for(UsageBlockBase block: blocksBeingScheduled) {
			
			Date sDate = block.getStartDate();
			Date eDate = block.getEndDate();
			totalhours += (eDate.getTime() - sDate.getTime()) / (1000 * 60 * 60);
		}
		
		log.info("Total hours scheduled for free project: "+totalhours);
		if(totalhours >= HOURS_QUOTA_FREE_PROJECT)
			return true;
		
		return false;
	}
	
	public boolean startDateApproved(Date startDate, User user) {
		
		// If the user is an admin return true
		Groups groupsMan = Groups.getInstance();
		
		if (groupsMan.isMember(user.getResearcher().getID(), "administrators"))
			return true;
		
		Date now = new Date(System.currentTimeMillis());
    	if(now.after(startDate)) {
    		return false;
    	}
    	
    	return true;
	}
}
