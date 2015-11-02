package org.uwpr.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import junit.framework.TestCase;

import org.uwpr.costcenter.TimeBlock;

public class TimeRangeSplitterTest extends TestCase {

	public void testSplit_noTimeBlocks() {
		
		List<TimeBlock> timeBlocks = new ArrayList<TimeBlock>();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 9); // 9:00am
		
		Calendar endCal = getCalendar();
		endCal.set(Calendar.DAY_OF_MONTH, 4);
		endCal.set(Calendar.HOUR_OF_DAY, 9);  // 9:00am
		
		try {
			
			splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
			fail("Should have failed. No time blocks were given as input");
			
		} catch (SchedulerException e) {
			assertEquals("Cannot split date range into time blocks. No time blocks were given", e.getMessage());
		}
	}
	
	/*
	 * 1. 8am to 8pm 
            8 to 9 (1hr)
            9 to 5 (8hr)
            5 to 6, 6 to 7, 7 to 8 (1hr blocks)
	 */
	public void testSplit_8am_8pm() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 8); // 8:00am
		
		Calendar endCal = getCalendar();
		endCal.set(Calendar.DAY_OF_MONTH, 2);
		endCal.set(Calendar.HOUR_OF_DAY, 20); // 8:00pm
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(5, rangeBlocks.size());
		
		assertEquals(1, rangeBlocks.get(0).getNumHours());
		assertEquals(8, rangeBlocks.get(1).getNumHours());
		assertEquals(1, rangeBlocks.get(2).getNumHours());
		assertEquals(1, rangeBlocks.get(3).getNumHours());
		assertEquals(1, rangeBlocks.get(4).getNumHours());
	}
	
	/*
	 * 1. 8am to 8am  (net day)
            24 hr block
	 */
	public void testSplit_8am_8am() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 8); // 8:00am
		
		Calendar endCal = getCalendar();
		endCal.set(Calendar.DAY_OF_MONTH, 3);
		endCal.set(Calendar.HOUR_OF_DAY, 8); // 8:00am (next day)
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(1, rangeBlocks.size());
		
		assertEquals(24, rangeBlocks.get(0).getNumHours());
	}

	/*
	 3. 3pm to 8am (next day)
           17 1hr blocks
	 */
	public void testSplit_3pm_8am() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 15); // 3:00pm
		
		Calendar endCal = getCalendar();
		endCal.set(Calendar.DAY_OF_MONTH, 3);
		endCal.set(Calendar.HOUR_OF_DAY, 8); // 8:00am
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(17, rangeBlocks.size());
		
		for(TimeBlock block: rangeBlocks) {
			assertEquals(1, block.getNumHours());
		}
	}
	
	/*
	 4. 6pm to 9am (next day)
           15 1hr blocks
	 */
	public void testSplit_6pm_9am() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 18); // 6:00pm
		
		Calendar endCal = getCalendar();
		endCal.set(Calendar.DAY_OF_MONTH, 3);
		endCal.set(Calendar.HOUR_OF_DAY, 9); // 9:00am
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(15, rangeBlocks.size());
		
		for(TimeBlock block: rangeBlocks) {
			assertEquals(1, block.getNumHours());
		}
	}
	
	/*
	 5. 2pm to 10am (next day)
        	2 to 3, 3 to 4, 4 to 5 (1hr blocks)
			5 to 9 (16 hrs overnight)
        	9 to 10 (1hr)
	 */
	public void testSplit_2pm_10am() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 14); // 2:00pm
		
		Calendar endCal = getCalendar();
		endCal.set(Calendar.DAY_OF_MONTH, 3);
		endCal.set(Calendar.HOUR_OF_DAY, 10); // 10:00am
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(5, rangeBlocks.size());
		
		assertEquals(1, rangeBlocks.get(0).getNumHours());
		assertEquals(1, rangeBlocks.get(1).getNumHours());
		assertEquals(1, rangeBlocks.get(2).getNumHours());
		assertEquals(16, rangeBlocks.get(3).getNumHours());
		assertEquals(1, rangeBlocks.get(4).getNumHours());
	}
	
	public void testSplit_47hrs() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 9); // 9:00am
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startCal.getTime());
		endCal.add(Calendar.HOUR_OF_DAY, 47); // + 47 hours
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(17, rangeBlocks.size());
		
		assertEquals(24, rangeBlocks.get(0).getNumHours());
		assertEquals(8, rangeBlocks.get(1).getNumHours());
		assertEquals(1, rangeBlocks.get(2).getNumHours());
		assertEquals(1, rangeBlocks.get(3).getNumHours());
		assertEquals(1, rangeBlocks.get(4).getNumHours());
		assertEquals(1, rangeBlocks.get(5).getNumHours());
	}

	public void testSplit_48hrs() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 9); // 9:00am
		
		Calendar endCal = getCalendar();
		endCal.set(Calendar.DAY_OF_MONTH, 4);
		endCal.set(Calendar.HOUR_OF_DAY, 9);  // 9:00am
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(2, rangeBlocks.size());
		
		for(TimeBlock block: rangeBlocks) {
			assertEquals(24, block.getNumHours());
		}
	}
	
	public void testSplit_24hrs() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 13); // 1:00pm
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startCal.getTime());
		endCal.add(Calendar.HOUR_OF_DAY, 24); // + 24 hours
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(1, rangeBlocks.size());
		
		assertEquals(24, rangeBlocks.get(0).getNumHours());
	}
	
	public void testSplit_20hrs() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 9); // 9:00am
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startCal.getTime());
		endCal.add(Calendar.HOUR_OF_DAY, 20); // + 20 hours
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(13, rangeBlocks.size());
		
		assertEquals(8, rangeBlocks.get(0).getNumHours());
		assertEquals(1, rangeBlocks.get(1).getNumHours());
	}
	
	public void testSplit_20hrs_startAt8() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 8); // 8:00am
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startCal.getTime());
		endCal.add(Calendar.HOUR_OF_DAY, 20); // + 20 hours
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(13, rangeBlocks.size());
		
		assertEquals(1, rangeBlocks.get(0).getNumHours());
		assertEquals(8, rangeBlocks.get(1).getNumHours());
		assertEquals(1, rangeBlocks.get(2).getNumHours());
	}
	
	public void testSplit_5hrs() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 9); // 9:00am
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startCal.getTime());
		endCal.add(Calendar.HOUR_OF_DAY, 5); // + 5 hours
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(2, rangeBlocks.size());
		
		assertEquals(4, rangeBlocks.get(0).getNumHours());
		assertEquals(1, rangeBlocks.get(1).getNumHours());
	}
	
	public void testSplit_7hrs() {
		
		List<TimeBlock> timeBlocks = makeTimeBlocks();
		
		TimeRangeSplitter splitter = TimeRangeSplitter.getInstance();
		
		Calendar startCal = getCalendar();
		startCal.set(Calendar.DAY_OF_MONTH, 2);
		startCal.set(Calendar.HOUR_OF_DAY, 9); // 9:00am
		
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(startCal.getTime());
		endCal.add(Calendar.HOUR_OF_DAY, 7); // + 7 hours
		
		List<TimeBlock> rangeBlocks = null;
		try {
			rangeBlocks = splitter.split(startCal.getTime(), endCal.getTime(), timeBlocks);
		} catch (SchedulerException e) {
			fail("Go an exception where it was not expected. Error was: "+e.getMessage());
		}
		assertEquals(4, rangeBlocks.size());
		
		assertEquals(4, rangeBlocks.get(0).getNumHours());
		assertEquals(1, rangeBlocks.get(1).getNumHours());
		assertEquals(1, rangeBlocks.get(2).getNumHours());
		assertEquals(1, rangeBlocks.get(3).getNumHours());
	}
	
	

	private Calendar getCalendar() {
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.MILLISECOND, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MINUTE, 0);
		return startCal;
	}
	
	private List<TimeBlock> makeTimeBlocks() {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		List<TimeBlock> timeBlocks = new ArrayList<TimeBlock>();
		
		TimeBlock block = new TimeBlock();
		block.setNumHours(1);
		timeBlocks.add(block);
		
		block = new TimeBlock();
		block.setNumHours(4);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		block.setStartTime(calendar.getTime());
		timeBlocks.add(block);
		
		block = new TimeBlock();
		block.setNumHours(4);
		calendar.set(Calendar.HOUR_OF_DAY, 13);
		block.setStartTime(calendar.getTime());
		timeBlocks.add(block);
		
		block = new TimeBlock();
		block.setNumHours(8);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		block.setStartTime(calendar.getTime());
		timeBlocks.add(block);
		
		block = new TimeBlock();
		block.setNumHours(16);
		calendar.set(Calendar.HOUR_OF_DAY, 17);
		block.setStartTime(calendar.getTime());
		timeBlocks.add(block);
		
		block = new TimeBlock();
		block.setNumHours(24);
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		block.setStartTime(calendar.getTime());
		timeBlocks.add(block);
		
		return timeBlocks;
	}

}
