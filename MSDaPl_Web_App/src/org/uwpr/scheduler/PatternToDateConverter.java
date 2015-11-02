/**
 * 
 */
package org.uwpr.scheduler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * PatternToDateConverter.java
 * @author Vagisha Sharma
 * Jul 19, 2011
 * 
 */
public class PatternToDateConverter {

	//private static final Pattern timePattern = Pattern.compile("(\\d+):(\\d+)([a|p]m)");
	
	private static final DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	
	public static Date convert(String dateString, String timeString) throws SchedulerException {
		
		if(dateString == null) {
			throw new SchedulerException("Date cannot be null");
		}
		if(timeString == null) {
			throw new SchedulerException("Time cannot be null");
		}
		
		Calendar calendar = Calendar.getInstance();
		
		// convert the date string
		try {
			Date date = format.parse(dateString);
			calendar.setTime(date);
			
		} catch (ParseException e) {
			throw new SchedulerException("Could not parse the given date: "+dateString);
		}
		
    	int time = 0;
    	try {
    		time = Integer.parseInt(timeString);
    	}
    	catch(NumberFormatException e) {
    		throw new SchedulerException("Error parsing time: "+timeString);
    	}
    	if(time < 0 || time > 23) {
    		throw new SchedulerException("Invalid time: "+time);
    	}
    	
    	calendar.set(Calendar.HOUR_OF_DAY, time);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
	
	public static Date parseDate(String dateString) throws ParseException {
		
		return format.parse(dateString);
	}
}
