package org.uwpr.costcenter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.log4j.Logger;


public class BillingInformationExporterTest extends TestCase {


	private static final int DAY_RANGE = 30;
	private static final Logger log = Logger.getLogger(BillingInformationExporterTest.class);
	private static final SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss:SS a");
	
	
	private Calendar getEnd() {
		return getCalendarRelativeToToday(DAY_RANGE, 24); // add 30 days; HOUR_OF_DAY = 24; 12:00 am; this will be 12:00 am of the next day
	}
	private Calendar getStart() {
		return getCalendarRelativeToToday(0, 0); // same day as today; HOUR_OF_DAY = 0; 12:00am today
	}
	private Calendar getCalendarRelativeToToday(int addDaysToCurrent, int hourOfDay) {
		Calendar calendar = Calendar.getInstance(); 
		calendar.add(Calendar.DAY_OF_MONTH, addDaysToCurrent); // add days
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); // 12:00 am
		return calendar;
	}

	public void testGetPercentCost() {
		
		BillingInformationExporter exporter = new BillingInformationExporter();
		BigDecimal cost =  new BigDecimal(120.0);
		BigDecimal percent = new BigDecimal(100.0);
		assertEquals(cost, exporter.getPercentCost(cost, percent));
		
		percent = new BigDecimal("50.0");
		assertEquals(new BigDecimal("60.0"),exporter.getPercentCost(cost, percent));
		
		percent = new BigDecimal("25.0");
		assertEquals(new BigDecimal("30.00"),exporter.getPercentCost(cost, percent));
		
	}
	
	public void testGetBilledCost() {
		
		Calendar start = getStart();
		Calendar end = getEnd();
		
		Date startTime = new Date(start.getTimeInMillis());
		Date endTime = new Date(end.getTimeInMillis());
		log.info(dateformat.format(startTime));
		log.info(dateformat.format(endTime));
		
		BillingInformationExporter exporter = new BillingInformationExporter();
		exporter.setStartDate(startTime);
		exporter.setEndDate(endTime);
		
		Calendar blockStart = getCalendarRelativeToToday(5,9); // add 5 days to current day; 9:00am
		Calendar blockEnd = getCalendarRelativeToToday(6,9); // add 6 days to current; 9:00am
	
		// The difference between blockStart and blockEnd should be 24 hours
		long numhrs = (blockEnd.getTimeInMillis() - blockStart.getTimeInMillis()) / (1000 * 60 * 60);
		assertEquals(24, numhrs);
		
		BigDecimal blockCost = new BigDecimal(70.0).setScale(2, RoundingMode.HALF_UP); // we need two decimal places.
		
		// This block lies with the time range. Cost should the same as the cost of block
		BigDecimal toBill = exporter.getBilledCost(blockCost, BillingInformationExporter.ONE_HUNDRED,
				new Timestamp(blockStart.getTimeInMillis()), new Timestamp(blockEnd.getTimeInMillis()));
		assertEquals(blockCost, toBill);
	}
	
	public void testGetBilledCost_StartNotInRange() {
		
		Calendar start = getStart();
		Calendar end = getEnd();
		
		// default: partial blocks are not billed
		BillingInformationExporter exporter = new BillingInformationExporter();
		exporter.setStartDate(new Date(start.getTimeInMillis()));
		exporter.setEndDate(new Date(end.getTimeInMillis()));
		
		Calendar blockStart = getCalendarRelativeToToday(-1,9); // a day BEFORE current day; 9:00am
		Calendar blockEnd = getCalendarRelativeToToday(0,9); // add 0 days to current; 9:00am TODAY
	
		// The difference between blockStart and blockEnd should be 24 hours
		long numhrs = (blockEnd.getTimeInMillis() - blockStart.getTimeInMillis()) / (1000 * 60 * 60);
		assertEquals(24, numhrs);
		
		// The start time lies outside the time range. Cost should be zero
		BigDecimal blockCost = new BigDecimal(80.0).setScale(2, RoundingMode.HALF_UP); // we need two decimal places.;
		
		// Block start is before the requested start date.
		// This is a 24 hour block that costs 80 dollars
		// Actual hours within range are 9 
		BigDecimal toBill = exporter.getBilledCost(blockCost, BillingInformationExporter.ONE_HUNDRED,
				new Timestamp(blockStart.getTimeInMillis()), new Timestamp(blockEnd.getTimeInMillis()));
		BigDecimal fractionCost = new BigDecimal(30.0).setScale(2, RoundingMode.HALF_UP); // we need two decimal places.;
		assertEquals(fractionCost, toBill);
		
	}
	
	public void testGetBilledCost_EndNotInRange() {
		
		Calendar start = getStart();
		Calendar end = getEnd();
		
		// default: partial blocks are not billed
		BillingInformationExporter exporter = new BillingInformationExporter();
		exporter.setStartDate(new Date(start.getTimeInMillis()));
		exporter.setEndDate(new Date(end.getTimeInMillis()));
		
		Calendar blockStart = getCalendarRelativeToToday(DAY_RANGE, 17); // same as end day; 5:00pm
		Calendar blockEnd = getCalendarRelativeToToday(DAY_RANGE + 1,9); // day AFTER the end day; 9:00am
	
		// The difference between blockStart and blockEnd should be 16 hours
		long numhrs = (blockEnd.getTimeInMillis() - blockStart.getTimeInMillis()) / (1000 * 60 * 60);
		assertEquals(16, numhrs);
		
		// The start time lies outside the time range. Cost should be zero
		BigDecimal blockCost = new BigDecimal(90.0).setScale(2, RoundingMode.HALF_UP); // we need two decimal places.;
		
		// Block end is after the requested end date.
		// This is a 16 hour block that costs 90 dollars
		// Actual hours within range are 7
		BigDecimal toBill = exporter.getBilledCost(blockCost, BillingInformationExporter.ONE_HUNDRED,
				new Timestamp(blockStart.getTimeInMillis()), new Timestamp(blockEnd.getTimeInMillis()));
		
		double d = (90.0 * 7) / 16.0; // 39.375
		BigDecimal fractionCost = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP); // we need two decimal places.;
		assertEquals(fractionCost, toBill);
	}
	
	public void testGetBilledCost_StartAndEndNotInRange() {
		
		Calendar start = getStart();
		Calendar end = getCalendarRelativeToToday(0, 24);
		
		// default: partial blocks are not billed
		BillingInformationExporter exporter = new BillingInformationExporter();
		exporter.setStartDate(new Date(start.getTimeInMillis()));
		exporter.setEndDate(new Date(end.getTimeInMillis()));
		
		Calendar blockStart = getCalendarRelativeToToday(-1, 23); // 11:00pm the day before start date
		Calendar blockEnd = getCalendarRelativeToToday(1,1); // 1:00pm the day after start date
	
		// The difference between blockStart and blockEnd should be 26 hours
		long numhrs = (blockEnd.getTimeInMillis() - blockStart.getTimeInMillis()) / (1000 * 60 * 60);
		assertEquals(26, numhrs);
		
		// The start and end times lies outside the time range. Cost should be a fraction of the total cost
		BigDecimal blockCost = new BigDecimal(100.0);
		BigDecimal toBill = exporter.getBilledCost(blockCost, BillingInformationExporter.ONE_HUNDRED,
				new Timestamp(blockStart.getTimeInMillis()), new Timestamp(blockEnd.getTimeInMillis()));
		
		// This is a 26 hour block that costs 100 dollars
		// Actual hours within range are 24
		double d = (100.0 * 24) / 26.0;
		BigDecimal fractionCost = new BigDecimal(d).setScale(2, RoundingMode.HALF_UP);
		log.info(fractionCost);
		
		toBill = exporter.getBilledCost(blockCost, BillingInformationExporter.ONE_HUNDRED,
				new Timestamp(blockStart.getTimeInMillis()), new Timestamp(blockEnd.getTimeInMillis()));
		assertEquals(fractionCost, toBill);
	}

}
