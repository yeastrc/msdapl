package org.uwpr.instrumentlog;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateUtils {

	private static final long MILLSEC_IN_DAY = 1000*60*60*24;
	private static final long MILLSEC_IN_HOUR = 1000*60*60;
	private static final long MILLSEC_IN_MIN = 1000*60;
	
	private DateUtils(){}
	
	/**
	 * Returns the current day of the month.
	 * @return
	 */
	public static int getCurrentDate() {
		Calendar calendar = GregorianCalendar.getInstance();
		return calendar.get(Calendar.DATE);
	}
	
	/**
	 * Returns the current day of the week
	 * @return
	 */
	public static int getCurrentDay() {
	    Calendar calendar = GregorianCalendar.getInstance();
	    return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Returns a number representing the current month (number between 1 and 12).
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar calendar = GregorianCalendar.getInstance();
		return calendar.get(Calendar.MONTH) +1;
	}
	
	/**
	 * Returns the current year
	 * @return
	 */
	public static int getCurrentYear() {
		Calendar calendar = GregorianCalendar.getInstance();
		return calendar.get(Calendar.YEAR);
	}
	
	
	/**
     * Returns the AM/PM represented by the given Date object.
     * @param date
     * @return
     */
    public static int getAmPm(java.util.Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.AM_PM);
    }
    
    /**
     * Returns the minutes represented by the given Date object.
     * @param date
     * @return
     */
    public static int getSeconds(java.util.Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }
    
	/**
     * Returns the minutes represented by the given Date object.
     * @param date
     * @return
     */
    public static int getMinutes(java.util.Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }
    
    
	/**
     * Returns the hour represented by the given Date object.
     * @param date
     * @return
     */
    public static int getHour(java.util.Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour > 12)
            hour = hour -12;
        if(hour == 0)
            hour = 12;
        return hour;
    }
    
    /**
     * Returns the hour (24-hr clock) represented by the given Date object.
     * @param date
     * @return
     */
    public static int getHour24(java.util.Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }
    
	/**
	 * Returns the day of the month represented by the given Date object.
	 * @param date
	 * @return
	 */
	public static int getDay(java.util.Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DATE);
	}
	
	/**
	 * Returns the Calendar class' integer representation of the day of the week 
	 * for the given date
	 * @param day
	 * @param month (1 to 12)
	 * @param year
	 * @return
	 */
	public static int getDayOfWeek(int day, int month, int year) {
		Calendar cal = new GregorianCalendar(year, month-1, day);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * Returns the month represented by the given Date object.
	 * @param date
	 * @return
	 */
	public static int getMonth(java.util.Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH)+1;
	}
	
	/**
	 * Returns the year represented by the given Date object.
	 * @param date
	 * @return
	 */
	public static int getYear(java.util.Date date) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	 * Returns the number of days between the given start and end dates (inclusive). 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	static int getNumDays(java.util.Date startDate, java.util.Date endDate) {
		long startTime = zeroedTimePlusOffset(startDate);
		long endTime = zeroedTimePlusOffset(endDate);
		
		return (int)((endTime - startTime)/(MILLSEC_IN_DAY)) + 1;
	}
	
	/**
     * Returns the number of hours between the given start and end dates (inclusive). 
     * @param startDate
     * @param endDate
     * @return
     */
    public static float getNumHours(java.util.Date startDate, java.util.Date endDate) {
        
        if(startDate.after(endDate))
            return 0;
        
        long startTime = timePlusOffset(startDate);
        long endTime = timePlusOffset(endDate);
        
        long diff = endTime - startTime;
        int numHours = (int)((diff) / MILLSEC_IN_HOUR);
        int numMin = (int)((diff - (numHours * MILLSEC_IN_HOUR)) / MILLSEC_IN_MIN);
        
        
        return (float)(numHours + ((float)numMin / (60.0)));
        
    }
    
    public static long getTimeDiffInMilis(java.util.Date startDate, java.util.Date endDate) {
        
        if(startDate.after(endDate))
            return 0;
        
        long startTime = timePlusOffset(startDate);
        long endTime = timePlusOffset(endDate);
        
        return endTime - startTime;
    }

//    /**
//     * Returns the number of hours used between the given start and end dates (inclusive). 
//     * Full days will be counted as USABLE_HRS_IN_DAY
//     * @param startDate
//     * @param endDate
//     * @return
//     */
//    public static float getNumHoursUsed(java.util.Date startDate, java.util.Date endDate) {
//        
//        if(startDate.after(endDate))
//            return 0.0f;
        
//        Calendar calendar_s = GregorianCalendar.getInstance();
//        calendar_s.setTimeInMillis(startDate.getTime());
//        
//        Calendar calendar_e = GregorianCalendar.getInstance();
//        calendar_e.setTimeInMillis(endDate.getTime());
        
        // same day
//        if(calendar_s.get(Calendar.YEAR) == calendar_e.get(Calendar.YEAR)  && //same year
//           calendar_s.get(Calendar.MONTH) == calendar_e.get(Calendar.MONTH) && // same month
//           calendar_s.get(Calendar.DATE) == calendar_e.get(Calendar.DATE))  { // same date
//            return getNumHours(startDate, endDate); // do not limit to usable time
//        }
        
//        float hoursUsed = getNumHours(startDate, defaultEndTime(calendar_s)) + 
//                          getNumHours(defaultStartTime(calendar_e), endDate);
//        
//        // consecutive days
//        if(calendar_s.get(Calendar.YEAR) == calendar_e.get(Calendar.YEAR)  && //same year
//           calendar_s.get(Calendar.MONTH) == calendar_e.get(Calendar.MONTH) && // same month
//           calendar_s.get(Calendar.DATE) + 1 == calendar_e.get(Calendar.DATE)) { // consecutive days
//            return hoursUsed;
//        }
//        
//        calendar_s.set(Calendar.HOUR_OF_DAY, 0);
//        calendar_s.set(Calendar.MINUTE, 0);
//        calendar_s.set(Calendar.SECOND, 0);
//        calendar_s.set(Calendar.MILLISECOND, 0);
//        calendar_s.add(Calendar.DATE, 1); // add a day
//        
//        
//        calendar_e.set(Calendar.HOUR_OF_DAY, 0);
//        calendar_e.set(Calendar.MINUTE, 0);
//        calendar_e.set(Calendar.SECOND, 0);
//        calendar_e.set(Calendar.MILLISECOND, 0);
//        calendar_e.add(Calendar.DATE, -1); // subtract a day
//        
//        
//        hoursUsed += getNumDays(new Date(calendar_s.getTimeInMillis()), 
//                                   new Date(calendar_e.getTimeInMillis()))*USABLE_HRS_IN_DAY;
//        
//        return hoursUsed;
        
//    }
    
	/**
     * Returns the number of usable hours between the given start and end dates (inclusive). 
     * @param startDate
     * @param endDate
     * @return
     */
    public static float getNumUsableHours(java.util.Date startDate, java.util.Date endDate) {
        
        if(startDate.after(endDate))
            return 0.0f;
        
//        Calendar calendar_s = GregorianCalendar.getInstance();
//        calendar_s.setTimeInMillis(startDate.getTime());
//        
//        Calendar calendar_e = GregorianCalendar.getInstance();
//        calendar_e.setTimeInMillis(endDate.getTime());
        
        return getNumHours(startDate, endDate);
        
        // same day
//        if(calendar_s.get(Calendar.YEAR) == calendar_e.get(Calendar.YEAR)  && //same year
//           calendar_s.get(Calendar.MONTH) == calendar_e.get(Calendar.MONTH) && // same month
//           calendar_s.get(Calendar.DATE) == calendar_e.get(Calendar.DATE))  { // same date
//            return usableHoursStarting(calendar_s);
//        }
        
//        float usableHours = usableHoursStarting(calendar_s) + usableHoursEnding(calendar_e);
//        
//        // consecutive days
//        if(calendar_s.get(Calendar.YEAR) == calendar_e.get(Calendar.YEAR)  && //same year
//           calendar_s.get(Calendar.MONTH) == calendar_e.get(Calendar.MONTH) && // same month
//           calendar_s.get(Calendar.DATE) + 1 == calendar_e.get(Calendar.DATE)) { // consecutive days
//            return usableHours;
//        }
//        
//        calendar_s.set(Calendar.HOUR_OF_DAY, 0);
//        calendar_s.set(Calendar.MINUTE, 0);
//        calendar_s.set(Calendar.SECOND, 0);
//        calendar_s.set(Calendar.MILLISECOND, 0);
//        calendar_s.add(Calendar.DATE, 1); // add a day
//        
//        
//        calendar_e.set(Calendar.HOUR_OF_DAY, 0);
//        calendar_e.set(Calendar.MINUTE, 0);
//        calendar_e.set(Calendar.SECOND, 0);
//        calendar_e.set(Calendar.MILLISECOND, 0);
//        calendar_e.add(Calendar.DATE, -1); // subtract a day
//        
//        
//        usableHours += getNumDays(new Date(calendar_s.getTimeInMillis()), 
//                                   new Date(calendar_e.getTimeInMillis()))*USABLE_HRS_IN_DAY;
//        
//        return usableHours;
    }
    
//    static float usableHoursStarting(Calendar start) {
//        
//        int hr = start.get(Calendar.HOUR_OF_DAY);
//        if(hr < USABLE_START_HR)
//            return USABLE_HRS_IN_DAY;
//        else if(hr > USABLE_END_HR)
//            return 0;
//        
//        Date six_pm = defaultEndTime((Calendar) start.clone());
//        
//        return getNumHours(new java.util.Date(start.getTimeInMillis()), six_pm);
//    }
//    
//    static float usableHoursEnding(Calendar end) {
//        
//        int hr = end.get(Calendar.HOUR_OF_DAY);
//        if(hr > USABLE_END_HR)
//            return USABLE_HRS_IN_DAY;
//        else if(hr < USABLE_START_HR)
//            return 0;
//        
//        Date eight_am = defaultStartTime((Calendar) end.clone());
//        
//        return getNumHours(eight_am, new java.util.Date(end.getTimeInMillis()));
//    }
	
	/**
	 * Returns the number of days in the given month
	 * @param month (1 to 12)
	 * @param year
	 * @return
	 */
	public static int getDaysInMonth(int month, int year) {
		Calendar cal = new GregorianCalendar(year, month-1, 1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	// get the time in milliseconds. Set the time for the date instance to 00:00:00
	private static long zeroedTimePlusOffset(java.util.Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		Calendar thisCal = new GregorianCalendar(cal.get(Calendar.YEAR), 
										cal.get(Calendar.MONTH), 
										cal.get(Calendar.DAY_OF_MONTH));
		thisCal.set(Calendar.HOUR, 0);
		thisCal.set(Calendar.MINUTE, 0);
		thisCal.set(Calendar.SECOND, 0);
		thisCal.set(Calendar.MILLISECOND, 0);
		// take care of daylight savings offset
		return thisCal.getTimeInMillis() + thisCal.getTimeZone().getOffset(thisCal.getTimeInMillis());
	}
	
	private static long timePlusOffset(java.util.Date date) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(date);
        // take care of daylight savings offset
        return cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());
    }
	
	/**
	 * Returns a Date object representing the given date, month and year.
	 * Returns null if the date is invalid. If the day exceeds the max. 
	 * number of days in the given month, the day in the returned Date
	 * object is the last day of the month.
	 * @param day
	 * @param month
	 * @param year
	 * @return java.sql.Date
	 */
	public static Date getDate(int day, int month, int year) {
		return getDate(day, month, year, false);
	}
	
//	public static Date getUsableDate(int day, int month, int year, boolean end) {
//        if (year < 1900)
//            return null;
//        if (month < 1 || month > 12)
//            return null;
//        if (day < 1)
//            return null;
//        
//        Calendar calendar = null;
//        if(end)
//        	calendar = new GregorianCalendar(year, month-1, 1, USABLE_END_HR, 0);
//        else
//        	calendar = new GregorianCalendar(year, month-1, 1, USABLE_START_HR, 0);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
//        
//        day = Math.min(day, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
//        calendar.set(Calendar.DATE, day);
//        return new Date(calendar.getTimeInMillis());
//    }
	
	public static Date getDate(int day, int month, int year, boolean endOfDay) {
        if (year < 1900)
            return null;
        if (month < 1 || month > 12)
            return null;
        if (day < 1)
            return null;
        Calendar calendar = new GregorianCalendar(year, month-1, 1, 0, 0);
        if(endOfDay) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        }
        else {
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
        day = Math.min(day, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.DATE, day);
        return new Date(calendar.getTimeInMillis());
    }
	
    /**
     * Returns a Date object representing the default start time (12:00am)
     * @return
     */
    public static Date defaultStartTime() {
        Calendar calendar = GregorianCalendar.getInstance();
        return defaultStartTime(calendar);
    }
    
//    public static Date defaultStartTime(java.util.Date startDate) {
//        Calendar calendar = GregorianCalendar.getInstance();
//        calendar.setTimeInMillis(startDate.getTime());
//        return defaultStartTime(calendar);
//    }
    
    private static Date defaultStartTime(Calendar calendar) {
        calendar.set(Calendar.HOUR, 0 /*USABLE_START_HR*/);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }
    
    /**
     * Returns a Date object representing the default end time (12:00pm)
     * @return
     */
    public static Date defaultEndTime() {
        Calendar calendar = GregorianCalendar.getInstance();
        return defaultEndTime(calendar);
    }

//    public static Date defaultEndTime(java.util.Date endDate) {
//        Calendar calendar = GregorianCalendar.getInstance();
//        calendar.setTimeInMillis(endDate.getTime());
//        return defaultEndTime(calendar);
//    }
    
    private static Date defaultEndTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0 /*USABLE_END_HR*/);
        calendar.set(Calendar.AM_PM, Calendar.PM);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Date(calendar.getTimeInMillis());
    }
	
	/**
	 * Returns a Date object representing the current time MINUS the given number of days. 
	 * @param numDays
	 * @return
	 */
	public static Date currentMinusDays(int numDays) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.AM_PM, Calendar.AM);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
		return addDaysToCalendar(calendar, -numDays);
	}
	
	/**
	 * Returns a Date object representing the current time PLUS the given number of days. 
	 * @return
	 */
	public static Date currentPlusDays(int numDays) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        
		return addDaysToCalendar(calendar, numDays+1);
	}
	
	public static Date getDateMinusDays(Date date, int minusDays) {
		return addDaysToDate(date, -minusDays);
	}
	
	public static Date getDatePlusDays(Date date, int plusDays) {
		return addDaysToDate(date, plusDays);
	}
	
	private static Date addDaysToDate(Date date, int days) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(date);
		return addDaysToCalendar(calendar, days);
	}

	private static Date addDaysToCalendar(Calendar calendar, int days) {
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return new Date(calendar.getTimeInMillis());
	}
	
	
	
	/**
	 * Returns the later date
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date maxTimestamp(Date date1, Date date2) {
		if (date1.compareTo(date2) > 0) // returns 1 if date1 > date2
			return date1;
		else return date2;
	}
	
	/**
	 * Returns the earlier date
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date minTimestamp(Date date1, Date date2) {
		if (date1.compareTo(date2) == -1) // returns -1 if date1 < date2
			return date1;
		else return date2;
	}
	
	/**
	 * Returns a date in the previous month
	 * @param month (1 to 12)
	 * @param year
	 * @return
	 */
	public static Date getPreviousMonth(int month, int year) {
		Calendar cal = new GregorianCalendar(year, month-1, 1); // set the day to 1
		cal.add(Calendar.DAY_OF_MONTH, -1); // this will take it to the previous month
		return new Date(cal.getTimeInMillis());
	}
	
	/**
	 * Returns a date in the next month
	 * @param month (1 to 12)
	 * @param year
	 * @return
	 */
	public static Date getNextMonth(int month, int year) {
		Calendar cal = new GregorianCalendar(year, month-1, 1);
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, daysInMonth);
		cal.add(Calendar.DAY_OF_MONTH, 1); // this will take it to the next month
		return new Date(cal.getTimeInMillis());
	}
}
