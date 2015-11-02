package org.uwpr.instrumentlog;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.uwpr.costcenter.CostCenterConstants;


public class UsageBlock extends UsageBlockBase {

	private String piName;
	private int piID;
	private String instrumentName;
	private String projectTitle;
	private List<InstrumentUsagePayment> payments;
	private BigDecimal rate;
	private Date invoiceDate;
	
	public UsageBlock() {
	    super();
	}
	
	public String getProjectPI() {
		return this.piName;
	}
	
	public void setProjectPI(String piName) {
	    this.piName = piName;
	}
	
	public int getPIID() {
		return piID;
	}
	
	public void setPIID(int piID) {
	    this.piID = piID;
	}
	
	public List<InstrumentUsagePayment> getPayments() {
		if(payments == null)
			return new ArrayList<InstrumentUsagePayment>(0);
		else
			return payments;
	}

	public void setPayments(List<InstrumentUsagePayment> payments) {
		this.payments = payments;
	}
	
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
        buf.append("UsageBlock:\n");
        buf.append("projectId: "+getProjectID()+" "+getProjectTitle()+"\n");
        buf.append("piId: "+getPIID()+" "+getProjectPI()+"\n");
        buf.append("; instrumentId: "+getInstrumentID()+" "+getInstrumentName()+"\n");
        //buf.append("; paymentMethodId: "+getPaymentMethodID()+" "+getPaymentMethodName()+"\n");
        if(payments != null) {
        	for(InstrumentUsagePayment payment: payments) {
        		buf.append("Payment ID: "+payment.getPaymentMethod().getId()+
        				   "; Name: "+payment.getPaymentMethod().getDisplayString()+
        				   "; Percent: "+payment.getPercent()+"\n");
        	}
        }
        else {
        	buf.append("No payments associated with usage\n");
        }
        buf.append("; "+getStartDate().toString()+" - "+getEndDate().toString());
        return buf.toString();
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	public String getProjectTitle() {
		return projectTitle;
	}

	public void setProjectTitle(String projectTitle) {
		this.projectTitle = projectTitle;
	}
	
	public boolean isBilled() {
		return invoiceDate != null;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}
	
	public String getInvoiceDateFormatted() {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
        return df.format(getInvoiceDate());
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public UsageBlockBase copy() {
        
        UsageBlock blk = new UsageBlock();
        blk.setID(getID());
        blk.setInstrumentID(getInstrumentID());
        blk.setInstrumentRateID(getInstrumentRateID());
        blk.setPayments(getPayments());
        blk.setProjectID(getProjectID());
        blk.setResearcherID(getResearcherID());
        blk.setStartDate(getStartDate());
        blk.setEndDate(getEndDate());
        blk.setDateCreated(getDateCreated());
        blk.setDateChanged(getDateChanged());
        blk.setNotes(getNotes());
        blk.setInstrumentName(getInstrumentName());
        blk.setPIID(getPIID());
        blk.setProjectPI(getProjectPI());
        blk.setProjectTitle(getProjectTitle());
        blk.setInvoiceDate(getInvoiceDate());
        return blk;
    }
	
	public UsageBlock newBlock() {
        return new UsageBlock();
    }
	
	public BigDecimal getFee()
	{
		BigDecimal fee = rate.multiply(new BigDecimal(getNumHours()));
		if(CostCenterConstants.ADD_SETUP_COST)
			fee = fee.add(CostCenterConstants.SETUP_COST);
		return fee;
	}
}
