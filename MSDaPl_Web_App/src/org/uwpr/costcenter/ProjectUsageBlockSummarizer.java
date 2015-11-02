/**
 * 
 */
package org.uwpr.costcenter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uwpr.instrumentlog.InstrumentUsagePayment;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.Researcher;

/**
 * ProjectUsageBlockSummarizer.java
 * @author Vagisha Sharma
 * Sep 3, 2011
 * 
 */
public class ProjectUsageBlockSummarizer {

	private Project project;
	private Map<String, UsageBlockForBilling> summarizedBlocks;
	
	public ProjectUsageBlockSummarizer(Project project) {
		
		this.project = project;
		summarizedBlocks = new HashMap<String, UsageBlockForBilling>();
	}
	
	public void add(UsageBlockBase block, TimeBlock timeBlock, InstrumentRate rate) throws SQLException, BillingInformationExporterException {
		
		List<InstrumentUsagePayment> usagePayments = InstrumentUsagePaymentGetter.get(project, block);
		
		// get the name of the researcher that scheduled the instrument time
		int researcherId = block.getResearcherID();
		Researcher researcher = new Researcher();
		try {
			researcher.load(researcherId);
		} catch (InvalidIDException e) {
			throw new BillingInformationExporterException("Error getting researcher with ID: "+researcherId+"; Error message was: "+e.getMessage());
		}
		
		// get the instrument
		MsInstrument instrument = MsInstrumentUtils.instance().getMsInstrument(block.getInstrumentID());
		
		
		for(InstrumentUsagePayment usagePayment: usagePayments) {
			
			String key = getKey(block, usagePayment);
			
			UsageBlockForBilling blockForKey = summarizedBlocks.get(key);
			
			if(blockForKey == null) {
				
				blockForKey = new UsageBlockForBilling();
				blockForKey.setProject(this.project);
				blockForKey.setUser(researcher);
				blockForKey.setInstrument(instrument);
				blockForKey.setPaymentMethod(usagePayment.getPaymentMethod());
				blockForKey.setBillingPercent(usagePayment.getPercent());
				summarizedBlocks.put(key, blockForKey);
				
			}
			
			blockForKey.add(block, /*timeBlock, */rate);
		}
	}
	
	public List<UsageBlockForBilling> getSummarizedBlocks() {
		
		return new ArrayList<UsageBlockForBilling>(this.summarizedBlocks.values());
	}
	
	// Key = intrumentID_researcherID_paymentMethodID_billPercent
	// There can be multiple keys if the block is associated with multiple payment methods
	private String getKey(UsageBlockBase block, InstrumentUsagePayment usagePayment) throws SQLException {
		
		String key = block.getInstrumentID()+"_"+
					 block.getResearcherID()+"_";
		
		key += usagePayment.getPaymentMethod().getId()+"_"+usagePayment.getPercent().toString();
		
		return key;			 
	}
}
