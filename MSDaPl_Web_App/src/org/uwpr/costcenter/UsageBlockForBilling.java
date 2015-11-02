/**
 * 
 */
package org.uwpr.costcenter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.project.Project;
import org.yeastrc.project.Researcher;
import org.yeastrc.project.payment.PaymentMethod;

/**
 * UsageBlockForBilling.java
 * @author Vagisha Sharma
 * Sep 3, 2011
 * 
 */
public class UsageBlockForBilling {

	private List<UsageBlockBase> blocks = null;
	private Project project;
	private Researcher user;
	private MsInstrument instrument;
	private PaymentMethod paymentMethod;
	private BigDecimal percent;
	private int totalHours;
	private BigDecimal cost;
	private BigDecimal rate;
	
	public UsageBlockForBilling() {
		blocks = new ArrayList<UsageBlockBase>();
		cost = BigDecimal.ZERO;
	}
	
	public void add(UsageBlockBase block, InstrumentRate rate) {
		
		this.blocks.add(block);
		
		this.rate = rate.getRate();
		this.cost = cost.add(rate.getRate().multiply(new BigDecimal(block.getNumHours())));
		this.totalHours += block.getNumHours();
		
	}
	
	public List<UsageBlockBase> getBlocks() {
		return blocks;
	}

	public int getFirstUsageBlockId() {
		
		if(blocks.size() == 0)
			return -1;
		return blocks.get(0).getID();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Researcher getUser() {
		return user;
	}

	public void setUser(Researcher user) {
		this.user = user;
	}

	public MsInstrument getInstrument() {
		return instrument;
	}

	public void setInstrument(MsInstrument instrument) {
		this.instrument = instrument;
	}

	public int getTotalHours() {
		return totalHours;
	}

	public BigDecimal getCost() {
		return cost;
	}
	
	public BigDecimal getRate()
	{
		return rate;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getBillingPercent() {
		return percent;
	}

	public void setBillingPercent(BigDecimal percent) {
		this.percent = percent;
	}
	
	public Date getStartDate() {
		if(blocks.size() == 0)
			return null;
		return blocks.get(0).getStartDate();
	}
	
	public Date getEndDate() {
		if(blocks.size() == 0)
			return null;
		return blocks.get(blocks.size() - 1).getEndDate();
	}
	
	public String getStartDateFormated() {
		if(blocks.size() == 0)
			return null;
		return blocks.get(0).getStartDateFormated();
	}
	
	public String getEndDateFormated() {
		if(blocks.size() == 0)
			return null;
		return blocks.get(blocks.size() - 1).getEndDateFormated();
	}
}
