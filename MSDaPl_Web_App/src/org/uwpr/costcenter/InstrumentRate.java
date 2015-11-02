/**
 * InstrumentRate.java
 * @author Vagisha Sharma
 * Apr 29, 2011
 */
package org.uwpr.costcenter;

import java.math.BigDecimal;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.uwpr.instrumentlog.MsInstrument;

/**
 * 
 */
public class InstrumentRate {

	private int id;
	private MsInstrument instrument;
	private RateType rateType;
	private TimeBlock timeBlock;
	private BigDecimal rate;
	private Date createDate;
	private boolean isCurrent = false;
	
	
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public MsInstrument getInstrument() {
		return instrument;
	}
	public void setInstrument(MsInstrument instrument) {
		this.instrument = instrument;
	}
	public RateType getRateType() {
		return rateType;
	}
	public void setRateType(RateType rateType) {
		this.rateType = rateType;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public TimeBlock getTimeBlock() {
		return timeBlock;
	}
	public void setTimeBlock(TimeBlock timeBlock) {
		this.timeBlock = timeBlock;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getCreateDateString() {
		return dateFormat.format(createDate);
	}
	public boolean isCurrent() {
		return isCurrent;
	}
	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}
}
