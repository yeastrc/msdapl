/**
 * 
 */
package org.uwpr.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.uwpr.costcenter.TimeBlock;
import org.uwpr.instrumentlog.DateUtils;

/**
 * TimeRangeSplitter.java
 * @author Vagisha Sharma
 * Jul 19, 2011
 * 
 */
public class TimeRangeSplitter {

	private static final TimeRangeSplitter instance = new TimeRangeSplitter();
	
	private static final Logger log = Logger.getLogger(TimeRangeSplitter.class);
	
	private Calendar calendar = Calendar.getInstance();
	
	public static TimeRangeSplitter getInstance() {
		return instance;
	}
	
	public List<TimeBlock> split(Date startDate, Date endDate, List<TimeBlock> blocks) throws SchedulerException {
		
		if(startDate.after(endDate))
		{
			throw new SchedulerException("Start date cannot be after end date.");
		}
		
		if(blocks == null || blocks.size() == 0) {
			throw new SchedulerException("Cannot split date range into time blocks. No time blocks were given");
		}
		// sort the blocks by time (ascending) and then length (descending)
		Collections.sort(blocks, Collections.reverseOrder(new TimeBlockComparatorByLength()));
		
		List<TimeBlock> rangeBlocks = new ArrayList<TimeBlock>();
		
		
		Date newStartDate = startDate;
		
		while(newStartDate.before(endDate)) {
			
			float hoursInCurrentRange = DateUtils.getNumHours(newStartDate, endDate);
			
			for(TimeBlock block: blocks) {
				
				// If this block has start and end times we can use this block only 
				// if the current start time matches the start time of the block
				if(!matchesBlockStarTime(newStartDate, block)) {
					continue;
				}
				
				if(block.getNumHours() <= hoursInCurrentRange) {
					rangeBlocks.add(block);
					
					calendar.setTime(newStartDate);
					calendar.add(Calendar.HOUR_OF_DAY, block.getNumHours());
					newStartDate = calendar.getTime();
					
					break;
				}
			}
		}
		
		return rangeBlocks;
	}

	
	private boolean matchesBlockStarTime(Date startDate, TimeBlock block) {
		
		// If this block has no start and end time we return true
		if(block.getHasNoStartEndTime() || block.getNumHours() == 24)
			return true;
		
		calendar.setTime(startDate);
		int startHour = calendar.get(Calendar.HOUR_OF_DAY);
		int startMin = calendar.get(Calendar.MINUTE);
		
		calendar.setTime(block.getStartTime());
		int blockStartHour = calendar.get(Calendar.HOUR_OF_DAY);
		int blockStartMin = calendar.get(Calendar.MINUTE);
		
		return (startHour == blockStartHour) && (startMin == blockStartMin);
	}
}
