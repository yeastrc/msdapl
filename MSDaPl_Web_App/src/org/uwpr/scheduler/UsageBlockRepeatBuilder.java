/**
 * UsageBlockRepeatBuilder.java
 * @author Vagisha Sharma
 * Jul 15, 2011
 */
package org.uwpr.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 */
public class UsageBlockRepeatBuilder {

	private static UsageBlockRepeatBuilder instance;
	
	private UsageBlockRepeatBuilder() {}
	
	public static synchronized UsageBlockRepeatBuilder getInstance() {
		if(instance == null)
			instance = new UsageBlockRepeatBuilder();
		return instance;
	}
	
	public List<UsageBlockBaseWithRate> repeatDaily(UsageBlockBaseWithRate block, Date endDate) throws SchedulerException {
		
		
		List<UsageBlockBaseWithRate> allBlocks = new ArrayList<UsageBlockBaseWithRate>();
		allBlocks.add(block);
		
		Calendar begin = Calendar.getInstance();
		begin.setTime(block.getStartDate());
		begin.add(Calendar.HOUR_OF_DAY, 24);
		if(block.getEndDate().after(begin.getTime())) {
			throw new SchedulerException("Block is longer than 24 hours. It cannot be repeated daily");
		}
			
		
		Calendar end = Calendar.getInstance();
		end.setTime(endDate);
		end.set(Calendar.MILLISECOND, 0);
		end.set(Calendar.SECOND, 0);
		end.set(Calendar.MINUTE, 0);
		end.set(Calendar.HOUR_OF_DAY, 0); // 12:00 am
		end.add(Calendar.DAY_OF_MONTH, 1); // add 1 day (This will be 12:00 am the next day)
		
		long numHrsAsMilis = block.getTimeInMillis();
		
		while(end.after(begin)) {
			
			UsageBlockBaseWithRate newBlk = block.copy();
			
			newBlk.setID(0);
			newBlk.setStartDate(begin.getTime());
			
			Date blkEnd = new Date(begin.getTimeInMillis() + numHrsAsMilis);
			newBlk.setEndDate(blkEnd);
			
			allBlocks.add(newBlk);
			
			begin.add(Calendar.HOUR_OF_DAY, 24);
		}
		return allBlocks;
	}
}
