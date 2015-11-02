/**
 * TimeBlock.java
 * @author Vagisha Sharma
 * Apr 29, 2011
 */
package org.uwpr.costcenter;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Represents a block of time that a user can reserve an instrument for.
 * e.g. 4 hour blocks: 9am to 1pm; 1pm to 5pm
 *      8 hour blocks: 9am to 5pm
 *     16 hour blocks: 5pm to 9am  (next day)
 *     24 hour blocks: 9am to 9am  (next day) 
 */
public class TimeBlock {

	private int id;
	private Date startTime;
	private int numHours;
	private String name;
	private Date createDate;
	
	private static final DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public String getStartTimeString() {
		if(startTime == null) 
			return "-";
		else return timeFormat.format(startTime.getTime());
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		if(startTime == null)
			return null;
		else {
			Date endTime = new Date(startTime.getTime() + getNumHoursAsMilis());
			return endTime;
		}
	}
	
	public String getEndTimeString() {
		Date endTime = getEndTime();
		
		if(endTime == null)
			return "-";
		else return timeFormat.format(endTime.getTime());
	}
	
	public int getNumHours() {
		return numHours;
	}
	
	public void setNumHours(int numHours) {
		this.numHours = numHours;
	}
	
	public long getNumHoursAsMilis() {
		return this.numHours * 60 * 60 * 1000;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getCreateDate() {
		return createDate;
	}
	
	public String getCreateDateString() {
		return dateFormat.format(createDate);
	}
	
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getDisplayString() {
		StringBuilder buf = new StringBuilder();
		buf.append(getNumHours()+"hrs");
		if(getStartTime() != null) {
			buf.append("; "+getStartTimeString()+" to "+getEndTimeString());
		}
		
		return buf.toString();
	}
	
	public String getDisplayStringLong() {
		StringBuilder buf = new StringBuilder();
		buf.append(getNumHours()+"hrs");
		if(getStartTime() != null) {
			buf.append("; "+getStartTimeString()+" to "+getEndTimeString());
		}
		buf.append("; "+getName());
		
		return buf.toString();
	}
	
	public String getHtmlDisplayString() {
		StringBuilder buf = new StringBuilder();
		buf.append("<b>"+getNumHours()+"hrs</b>");
		if(getStartTime() != null) {
			buf.append("; "+getStartTimeString()+" to "+getEndTimeString());
		}
		
		return buf.toString();
	}
	
	public boolean getHasNoStartEndTime() {
		return (getStartTime() == null);
	}
	
}
