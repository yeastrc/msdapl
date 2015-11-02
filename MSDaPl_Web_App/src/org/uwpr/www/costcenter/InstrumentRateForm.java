/**
 * InstrumentRateForm.java
 * @author Vagisha Sharma
 * May 2, 2011
 */
package org.uwpr.www.costcenter;

import java.math.BigDecimal;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

/**
 * 
 */
public class InstrumentRateForm extends ActionForm {

	private int instrumentRateId;
	private int instrumentId;
	private int timeBlockId;
	private int rateTypeId;
	private String rateString;
	private boolean isCurrent;
	
	
	//private static final Logger log = Logger.getLogger(InstrumentRateForm.class);
	
	/** 
	 * Method validate
	 * @param mapping
	 * @param request
	 * @return ActionErrors
	 */
	public ActionErrors validate(ActionMapping mapping,
			HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();
		
		try {
			new BigDecimal(rateString);
		}
		catch(NumberFormatException e) {
			errors.add("costcenter", new ActionMessage("error.costcenter.invaliddata", "Invalid rate: "+rateString));
		}
		
		return errors;
	}

	public int getInstrumentRateId() {
		return instrumentRateId;
	}

	public void setInstrumentRateId(int instrumentRateId) {
		this.instrumentRateId = instrumentRateId;
	}

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

	public String getRateString() {
		return rateString;
	}

	public void setRateString(String rateString) {
		this.rateString = rateString;
	}
	
	public BigDecimal getRate() {
		return new BigDecimal(rateString);
	}

	public void setRate(BigDecimal rate) {
		this.rateString = rate.toPlainString();
	}

	public boolean isCurrent() {
		return isCurrent;
	}

	public void setCurrent(boolean isCurrent) {
		this.isCurrent = isCurrent;
	}
	
}
