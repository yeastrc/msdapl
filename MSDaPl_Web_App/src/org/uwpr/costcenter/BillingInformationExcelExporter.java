/**
 * BillingInformationExporter.java
 * @author Vagisha Sharma
 * Jun 17, 2011
 */
package org.uwpr.costcenter;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.uwpr.instrumentlog.DateUtils;
import org.uwpr.instrumentlog.InstrumentUsagePayment;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.Researcher;
import org.yeastrc.project.payment.PaymentMethod;

/**
 * 
 */
public class BillingInformationExcelExporter extends BillingInformationExporter {


	private static final Logger log = Logger.getLogger(BillingInformationExcelExporter.class);

	private int rowNum = 0;

	public void exportToXls(OutputStream outStream) throws BillingInformationExporterException {
		

		// create a new workbook
		Workbook wb = new HSSFWorkbook();
		// create a new sheet
		Sheet sheet = wb.createSheet();
		
//		if(this.isSummarize())
//			writeHeaderSummarized(sheet);
//		else
		writeHeaderDetailed(sheet);
		
		// get a list of all projects (includes billed, subsidized and maintenance projects)
		List<Project> projects = getAllProjects();
		
		// write details for each project
		for(Project project: projects) {
			
//			if(this.isSummarize())
//				exportSummarized(project.getID(), sheet, false);
//			else
			exportDetailed(project.getID(), sheet, false);
			
		}
		
		try {
			wb.write(outStream);
		} catch (IOException e) {
			throw new BillingInformationExporterException("Error writing data.", e);
		}
	}
	

	public void exportToXls(int projectId, OutputStream outStream) throws BillingInformationExporterException {
		
		// create a new workbook
		Workbook wb = new HSSFWorkbook();
		// create a new sheet
		Sheet sheet = wb.createSheet();
		
//		if(this.isSummarize())
//			exportSummarized(projectId, sheet, true);
//		else
		exportDetailed(projectId, sheet, true);
		
		try {
			wb.write(outStream);
		} catch (IOException e) {
			throw new BillingInformationExporterException("Error writing data.", e);
		}
	}
	
	private void exportDetailed(int projectId, Sheet sheet, boolean writeHeader) throws BillingInformationExporterException {

		if(writeHeader) {
			// write the header
//			if(this.isSummarize())
//				writeHeaderSummarized(sheet);
//			else
			writeHeaderDetailed(sheet);
		}
		
		// get the project
		Project project = getProject(projectId);

		// make sure the dates are alright
		super.checkDates();

		// get the usage blocks for this project between the start and end dates
		List<UsageBlockBase> usageBlocks = getSortedUsageBlocksForProject_byStartDate(project);

		if(usageBlocks.size() == 0) {
			log.info("No usage found for project ID: "+project.getID()+" between the dates: "+getStartDate()+" - "+getEndDate());

			return;
		}

		// write out the cost for each block
		// there may be multiple payment methods for each block
		try {
			for(UsageBlockBase block: usageBlocks) {
				writeBlockDetails(project, block, sheet);
			}
		} catch (SQLException e) {
			throw new BillingInformationExporterException("Error reading usage details from database", e);
		}
	}
	
//	private void exportSummarized(int projectId, Sheet sheet, boolean writeHeader) 
//		throws BillingInformationExporterException {
//
//		if(writeHeader) {
//			// write the header
////			if(this.isSummarize())
////				writeHeaderSummarized(sheet);
////			else
//				writeHeaderDetailed(sheet);
//		}
//		
//		// get the project
//		Project project = getProject(projectId);
//
//		// make sure the dates are alright
//		super.checkDates();
//
//		// get the usage blocks for this project between the start and end dates
//		List<UsageBlockBase> usageBlocks = getSortedUsageBlocksForProject_byStartDate(project);
//
//		
//		if(usageBlocks.size() == 0) {
//			log.debug("No usage found for project ID: "+project.getID()+" between the dates: "+getStartDate()+" - "+getEndDate());
//
//			return;
//		}
//
//		ProjectUsageBlockSummarizer summarizer = new ProjectUsageBlockSummarizer(project);
//		
//		for(UsageBlockBase block: usageBlocks) {
//			
//			if(block.getEndDate().after(this.getEndDate())) {
//				continue; // don't add if this block ends after the end time of the given range.  
//				          // It will be billed in the next cycle.  
//			}
//			
//			// get the instrument rate
//			InstrumentRate rate;
//			try {
//				rate = InstrumentRateDAO.getInstance().getInstrumentRate(block.getInstrumentRateID());
//			} catch (SQLException e) {
//				throw new BillingInformationExporterException("Error getting instrument rate for block; block ID: "
//						+block.getID()+"; instrument rate ID: "+block.getInstrumentRateID()
//						+"; ");
//			}
//
//			// get the name of the time block
//			TimeBlock timeBlock = rate.getTimeBlock();
//			
//			try {
//				summarizer.add(block, timeBlock, rate);
//			} catch (SQLException e) {
//				throw new BillingInformationExporterException("Error adding block to summarizer", e);
//			}
//		}
//		
//		// write out the cost for each summarized block
//		// there may be multiple payment methods for each block
//		for(UsageBlockForBilling block: summarizer.getSummarizedBlocks()) {
//			
//			boolean exported = writeBlockPaymentMethodDetails(sheet, block, true);
//			
//			if(exported) {
//				
//				for(UsageBlockBase ublock: block.getBlocks()) {
//					informListener(ublock);
//				}
//			}
//		}
//		
//	}

	private void writeHeaderDetailed(Sheet sheet) {
		
		int cellnum = 0;
		sheet.createRow(rowNum++).createCell(cellnum).setCellValue("Billing information exported on "+new java.util.Date());
		sheet.createRow(rowNum++).createCell(cellnum).setCellValue("Start Date: "+this.getStartDate()+"\n");
		sheet.createRow(rowNum++).createCell(cellnum).setCellValue("End Date: "+this.getEndDate()+"\n");
		// sheet.createRow(rowNum++).createCell(cellnum).setCellValue("* Only blocks ending in a billing period are billed in that period.\n");

		
		Row row = sheet.createRow(rowNum++);
		row.createCell(cellnum++).setCellValue("ProjectID");
		row.createCell(cellnum++).setCellValue("Lab_Director");
		row.createCell(cellnum++).setCellValue("Researcher");
		row.createCell(cellnum++).setCellValue("Instrument");
		row.createCell(cellnum++).setCellValue("UsageBlockID");
		row.createCell(cellnum++).setCellValue("Start");
		row.createCell(cellnum++).setCellValue("End");
		row.createCell(cellnum++).setCellValue("Hours");
		row.createCell(cellnum++).setCellValue("HoursInRange");
		row.createCell(cellnum++).setCellValue("Rate");
		if(CostCenterConstants.ADD_SETUP_COST)
		{
			row.createCell(cellnum++).setCellValue("Setup_Cost");
		}
		row.createCell(cellnum++).setCellValue("Cost");
		row.createCell(cellnum++).setCellValue("Payment_Method");
		row.createCell(cellnum++).setCellValue("Payment_Method_Name");
		row.createCell(cellnum++).setCellValue("%Billed");
		row.createCell(cellnum++).setCellValue("AmountBilled");
		row.createCell(cellnum++).setCellValue("ContactFirstName");
		row.createCell(cellnum++).setCellValue("ContactLastName");
		row.createCell(cellnum++).setCellValue("ContactEmail");
		row.createCell(cellnum++).setCellValue("ContactPhone");
		
	}
	
//	private void writeHeaderSummarized(Sheet sheet) {
//		
//		int cellnum = 0;
//		sheet.createRow(rowNum++).createCell(cellnum).setCellValue("Billing information exported on "+new java.util.Date());
//		sheet.createRow(rowNum++).createCell(cellnum).setCellValue("Start Date: "+this.getStartDate()+"\n");
//		sheet.createRow(rowNum++).createCell(cellnum).setCellValue("End Date: "+this.getEndDate()+"\n");
//		sheet.createRow(rowNum++).createCell(cellnum).setCellValue("* Only blocks ending in a billing period are billed in that period.\n");
//
//		
//		Row row = sheet.createRow(rowNum++);
//		row.createCell(cellnum++).setCellValue("ProjectID");
//		row.createCell(cellnum++).setCellValue("Lab_Director");
//		row.createCell(cellnum++).setCellValue("Researcher");
//		row.createCell(cellnum++).setCellValue("Instrument");
//		row.createCell(cellnum++).setCellValue("Start");
//		row.createCell(cellnum++).setCellValue("End");
//		row.createCell(cellnum++).setCellValue("Rate");
//		if(CostCenterConstants.ADD_SETUP_COST)
//		{
//			row.createCell(cellnum++).setCellValue("Setup_Cost");
//		}
//		row.createCell(cellnum++).setCellValue("Hours_Used");
//		row.createCell(cellnum++).setCellValue("Cost");
//		row.createCell(cellnum++).setCellValue("Payment_Method");
//		row.createCell(cellnum++).setCellValue("%Billed");
//		row.createCell(cellnum++).setCellValue("AmountBilled");
//		row.createCell(cellnum++).setCellValue("ContactFirstName");
//		row.createCell(cellnum++).setCellValue("ContactLastName");
//		row.createCell(cellnum++).setCellValue("ContactEmail");
//		row.createCell(cellnum++).setCellValue("ContactPhone");
//		
//	}
	
	// Format:
	// ProjectID	PI	Researcher	Instrument	Block	Start	End	Hours	Rate	Setup_Cost	PaymentMethod	PaymentMethodName %Billed	AmountBilled	
	private void writeBlockDetails(Project project, UsageBlockBase block, Sheet sheet) throws BillingInformationExporterException, SQLException {
		
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

		// get the instrument rate
		InstrumentRate rate = InstrumentRateDAO.getInstance().getInstrumentRate(block.getInstrumentRateID());


		// get the payment method(s) for this block
		List<InstrumentUsagePayment> usagePayments = InstrumentUsagePaymentGetter.get(project, block);
		
		
		boolean blockExported = false;
		for(InstrumentUsagePayment usagePayment: usagePayments) {

			UsageBlockForBilling billBlock = new UsageBlockForBilling();
			billBlock.add(block, rate);
			billBlock.setProject(project);
			billBlock.setUser(researcher);
			billBlock.setInstrument(instrument);
			billBlock.setPaymentMethod(usagePayment.getPaymentMethod());
			billBlock.setBillingPercent(usagePayment.getPercent());
			
			blockExported = writeBlockPaymentMethodDetails(sheet, billBlock, false);
		}
		
		if(blockExported)
			informListener(block);
	}
	
	private boolean writeBlockPaymentMethodDetails(Sheet sheet, UsageBlockForBilling block, boolean summarize)

	throws BillingInformationExporterException {


		PaymentMethod paymentMethod = block.getPaymentMethod();
		BigDecimal percent = block.getBillingPercent();
		
		BigDecimal finalCost = block.getCost();
		if(CostCenterConstants.ADD_SETUP_COST)
		{
			finalCost = finalCost.add(CostCenterConstants.SETUP_COST);
		}
		
		BigDecimal billedCost = getBilledCost(finalCost, percent, block.getStartDate(), block.getEndDate());
		
		// If we are not billing anything ignore this block
		if(BigDecimal.ZERO.equals(billedCost))
			return false;
		
		Row row = sheet.createRow(rowNum++);
		
		int cellnum = 0;
		
		row.createCell(cellnum++).setCellValue(block.getProject().getID());

		row.createCell(cellnum++).setCellValue(block.getProject().getPI().getLastName());
		
		row.createCell(cellnum++).setCellValue(block.getUser().getLastName());

		row.createCell(cellnum++).setCellValue(block.getInstrument().getName());
		
//		if(!summarize)
		row.createCell(cellnum++).setCellValue(block.getFirstUsageBlockId());
		
		
		row.createCell(cellnum++).setCellValue(block.getStartDateFormated());
		row.createCell(cellnum++).setCellValue(block.getEndDateFormated());
		
		
		row.createCell(cellnum++).setCellValue(block.getTotalHours());
		
		java.util.Date start = block.getStartDate().before(this.getStartDate()) ? this.getStartDate() : block.getStartDate();
		java.util.Date end = block.getEndDate().after(this.getEndDate()) ? this.getEndDate() : block.getEndDate();
		row.createCell(cellnum++).setCellValue(DateUtils.getNumHours(start, end));
		
		row.createCell(cellnum++).setCellValue(block.getRate().toString());
		
		if(CostCenterConstants.ADD_SETUP_COST)
		{
			row.createCell(cellnum++).setCellValue(CostCenterConstants.SETUP_COST.toString());
		}
		
		row.createCell(cellnum++).setCellValue(finalCost.toString());
		
		
		String uwBudgetNumber = paymentMethod.getUwbudgetNumber();
		String ponumber = paymentMethod.getPonumber();
		if(uwBudgetNumber != null && uwBudgetNumber.trim().length() > 0) {
			row.createCell(cellnum++).setCellValue(uwBudgetNumber);
		}
		else if(ponumber != null && ponumber.trim().length() > 0) {
			row.createCell(cellnum++).setCellValue(ponumber);
		}
		else {
			throw new BillingInformationExporterException("Did not find a UW Budget number or a PO numer for payment method ID: "
					+paymentMethod.getId());
		}

		String paymentMethodName = paymentMethod.getPaymentMethodName();
		if(StringUtils.isBlank(paymentMethodName))
		{
			paymentMethodName = "";
		}
		row.createCell(cellnum++).setCellValue(paymentMethodName);
		
		row.createCell(cellnum++).setCellValue(percent+"%");

		
		// If his block starts before the requested start date flag it.
		// Blocks that end after the requested end date will not be billed.
		if(block.getStartDate().before(this.getStartDate())) 
			row.createCell(cellnum++).setCellValue(billedCost.toString()+"*");
		else
			row.createCell(cellnum++).setCellValue(billedCost.toString());
		
		
		// contact details of the person associated with the payment method
		row.createCell(cellnum++).setCellValue(paymentMethod.getContactFirstName());
		row.createCell(cellnum++).setCellValue(paymentMethod.getContactLastName());
		row.createCell(cellnum++).setCellValue(paymentMethod.getContactEmail());
		row.createCell(cellnum++).setCellValue(paymentMethod.getContactPhone());
		
		return true;
	}
	
}
