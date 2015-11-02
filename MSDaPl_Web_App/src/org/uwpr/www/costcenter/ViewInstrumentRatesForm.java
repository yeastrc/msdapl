/**
 * ViewInstrumentRatesForm.java
 * @author Vagisha Sharma
 * Aug 3, 2012
 */
package org.uwpr.www.costcenter;

import org.apache.struts.action.ActionForm;

/**
 * 
 */
public class ViewInstrumentRatesForm extends ActionForm {

	public int instrumentId = 0;
	public int timeBlockId = 0;
	public int rateTypeId = 0;
	public int current = -1;
	
	public int getInstrumentId() {
		return instrumentId;
	}
	public void setInstrumentId(int instrumentId) {
		this.instrumentId = instrumentId;
	}
	public int getTimeBlockId() {
		return timeBlockId;
	}
	public void setTimeBlockId(int timeBlockId) {
		this.timeBlockId = timeBlockId;
	}
	public int getRateTypeId() {
		return rateTypeId;
	}
	public void setRateTypeId(int rateTypeId) {
		this.rateTypeId = rateTypeId;
	}
	public int getCurrent() {
		return current;
	}
	public void setCurrent(int current) {
		this.current = current;
	}
}
