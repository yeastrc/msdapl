/**
 * UsageBlockDeletableDecider.java
 * @author Vagisha Sharma
 * Jun 2, 2011
 */
package org.uwpr.scheduler;

import java.sql.SQLException;

import org.uwpr.costcenter.InvoiceInstrumentUsage;
import org.uwpr.costcenter.InvoiceInstrumentUsageDAO;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.www.user.User;

/**
 * 
 */
public class UsageBlockDeletableDecider {

	private static final UsageBlockDeletableDecider instance = new UsageBlockDeletableDecider();
	
	private UsageBlockDeletableDecider () {}
	
	public static UsageBlockDeletableDecider getInstance() {
		return instance;
	}
	
	public boolean isBlockDeletable(UsageBlockBase block, User user, StringBuilder errorMessage) throws SQLException  {
		
		// If the user is an admin return true
//		Groups groupsMan = Groups.getInstance();
		
		// If this block has already been billed it cannot be deleted even by admins
		InvoiceInstrumentUsage billedBlock = InvoiceInstrumentUsageDAO.getInstance().getInvoiceBlock(block.getID());
		if(billedBlock != null) {
			errorMessage.append("Block cannot be deleted. It has already been billed.");
			return false;
		}
		
		return true; // All users can delete blocks if they have not already been invoiced.
		
//		if (groupsMan.isMember(user.getResearcher().getID(), "administrators"))
//			return true;
//		
//		
//		// If the block was created before the current date it cannot be deleted
//		if(block.getStartDate().before(new Date(System.currentTimeMillis()))) {
//			errorMessage.append("Scheduled instrument time cannot be deleted less than 48 hours prior to the start time.");
//			return false;
//		}
//		
//		// Usage block can be deleted by a non-admin user only if the start time is atleast 
//		// 48 hours after the current time
//		// UNLESS it was also created within 48 hours of the start time.
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.HOUR_OF_DAY, 48);
//		
//		
//		// start time of the block is 48 hours after the current time
//		if(block.getStartDate().after(new Date(calendar.getTimeInMillis()))) {
//			return true;
//		}
//		else {
//			// If the block was created within the last one hour user should be able to delete it.  They may have made a mistake.
//			long numHoursSinceCreate = (System.currentTimeMillis() - block.getDateCreated().getTime()) / (1000*60*60);
//			if(numHoursSinceCreate <= 1) {
//				return true;
//			}
//			else {
//				errorMessage.append("Scheduled instrument time cannot be deleted less than 48 hours prior to the start time.");
//				return false;
//			}
//		}
	}
}
