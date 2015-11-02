/**
 * BillingInformationExporter.java
 * @author Vagisha Sharma
 * Jun 17, 2011
 */
package org.uwpr.costcenter;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.uwpr.instrumentlog.InstrumentUsagePayment;
import org.uwpr.instrumentlog.MsInstrument;
import org.uwpr.instrumentlog.MsInstrumentUtils;
import org.uwpr.instrumentlog.ProjectInstrumentUsageDAO;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.data.InvalidIDException;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectFactory;
import org.yeastrc.project.ProjectsSearcher;
import org.yeastrc.project.Researcher;
import org.yeastrc.project.payment.PaymentMethod;

/**
 * 
 */
public class BillingInformationExporter {


	private java.util.Date startDate;
	private java.util.Date endDate;
	// private boolean billPartialBlocks = false;
	
	private boolean summarize = false;

	public static BigDecimal ONE_HUNDRED = new BigDecimal(100);
	
	private static DecimalFormat NUM_HRS_FORMAT = new DecimalFormat("#.##");

	private static final Logger log = Logger.getLogger(BillingInformationExporter.class);

	private BillingInformationExporterListener listener = null;
	
	public void setStartDate(java.util.Date startDate) {
		this.startDate = startDate;
	}
	
	java.util.Date getStartDate() {
		return this.startDate;
	}

	public void setEndDate(java.util.Date endDate) {
		this.endDate = endDate;
	}
	
	java.util.Date getEndDate() {
		return this.endDate;
	}

//	public void setBillPartialBlocks(boolean billPartialBlocks) {
//		this.billPartialBlocks = billPartialBlocks;
//	}
//	
//	public boolean isBillPartialBlocks() {
//		return billPartialBlocks;
//	}

	public void setBillinInformationExporterListener(BillingInformationExporterListener listener) {
		this.listener = listener;
	}
	
	private boolean isSummarize() {
		return summarize;
	}
//
//	public void setSummarize(boolean summarize) {
//		this.summarize = summarize;
//	}

	public void export(Writer output) throws BillingInformationExporterException {

		// get a list of all projects
		List<Project> projects = getAllProjects();

		// write the header
		try {
			output.write(getHeaderDetailed());
			output.write("\n");
		} catch (IOException e) {
			throw new BillingInformationExporterException("Error writing usage details header", e);
		}
		
		
		// write details for each project
		for(Project project: projects) {
			export(project.getID(), output, false);
		}

	}

	public void export(int projectId, Writer output) throws BillingInformationExporterException {
		export(projectId, output, true);
	}
	
	private void export(int projectId, Writer output, boolean writeHeader) throws BillingInformationExporterException {

		
		if(writeHeader) {
			// write the header
			try {
				output.write(getHeaderDetailed());
				output.write("\n");
			} catch (IOException e) {
				throw new BillingInformationExporterException("Error writing usage details header", e);
			}
		}
		
		// get the project
		Project project = getProject(projectId);

		// make sure the dates are alright
		checkDates();

		// get the usage blocks for this project between the start and end dates
		List<UsageBlockBase> usageBlocks = getSortedUsageBlocksForProject_byStartDate(project);

		if(usageBlocks.size() == 0) {
			log.info("No usage found for project ID: "+project.getID()+" between the dates: "+startDate+" - "+endDate);

			/*try {
				output.write(project.getID()+"\t");
				output.write("No usage found");
				output.write("\n");
			} catch (IOException e) {
				throw new BillingInformationExporterException("Error writing usage details", e);
			}*/
			return;
		}

		// write out the cost for each block
		// there may be multiple payment methods for each block
		try {
			for(UsageBlockBase block: usageBlocks) {
				StringBuilder buf = getBlockDetails(project, block);
				
				if(buf != null) {
					output.write(buf.toString());
					// output.write("\n");

					informListener(block);
				}
			}
		}
		catch(IOException e) {
			throw new BillingInformationExporterException("Error writing usage details", e);
		} catch (SQLException e) {
			throw new BillingInformationExporterException("Error reading usage details from database", e);
		}
	}

	void informListener(UsageBlockBase block) throws BillingInformationExporterException {
		if(this.listener != null)
			listener.blockExported(block);
	}

	public StringBuilder export() throws BillingInformationExporterException {

		List<Project> projects = getAllProjects();

		StringBuilder buffer = new StringBuilder();
		
		// write the header
		buffer.append(getHeaderDetailed());
		buffer.append("\n");
		
		// write details for each project
		for(Project project: projects) {
			
			if(this.isSummarize())
				exportSummarized(project.getID(), buffer, false);
			else
				exportDetailed(project.getID(), buffer, false);
		}
		
		return buffer;
	}

	public StringBuilder export(int projectId) throws BillingInformationExporterException {
		
		StringBuilder buffer = new StringBuilder();
		
		if(this.isSummarize())
			exportSummarized(projectId, buffer, true);
		else
			exportDetailed(projectId, buffer, true);
		return buffer;
	}

	private void exportDetailed(int projectId, StringBuilder buffer, boolean writeHeader) throws BillingInformationExporterException {

		if(writeHeader) {
			// write the header
			buffer.append(getHeaderDetailed());
			buffer.append("\n");
		}
		// make sure the project exists
		Project project = getProject(projectId);

		// make sure the dates are alright
		checkDates();

		// get the usage blocks for this project between the start and end dates
		List<UsageBlockBase> usageBlocks = getSortedUsageBlocksForProject_byStartDate(project);

		if(usageBlocks.size() == 0) {
			log.info("No usage found for project ID: "+project.getID()+" between the dates: "+startDate+" - "+endDate);

			//buffer.append(project.getID()+"\t");
			//buffer.append("No usage found");
			//buffer.append("\n");
			return;
		}

		// write out the cost for each block
		try {
			for(UsageBlockBase block: usageBlocks) {
				StringBuilder buf = getBlockDetails(project, block);
				
				if(buf != null) {
					buffer.append(buf.toString());
					//buffer.append("\n");
					informListener(block);
				}
			}
		}
		catch (SQLException e) {
			throw new BillingInformationExporterException("Error reading usage details from database", e);
		}
	}
	
	private void exportSummarized(int projectId, StringBuilder buffer, boolean writeHeader) throws BillingInformationExporterException {

		throw new BillingInformationExporterException("summarized export is not yet supported by the text exporter");
		
//		if(writeHeader) {
//			// write the header
//			buffer.append(getHeaderDetailed());
//			buffer.append("\n");
//		}
//		// make sure the project exists
//		Project project = getProject(projectId);
//
//		// make sure the dates are alright
//		checkDates();
//
//		// get the usage blocks for this project between the start and end dates
//		List<UsageBlockBase> usageBlocks = getSortedUsageBlocksForProject_byStartDate(project);
//
//		if(usageBlocks.size() == 0) {
//			log.info("No usage found for project ID: "+project.getID()+" between the dates: "+startDate+" - "+endDate);
//
//			//buffer.append(project.getID()+"\t");
//			//buffer.append("No usage found");
//			//buffer.append("\n");
//			return;
//		}
//
//		// write out the cost for each block
//		try {
//			for(UsageBlockBase block: usageBlocks) {
//				StringBuilder buf = getBlockDetails(project, block);
//				
//				if(buf != null) {
//					buffer.append(buf.toString());
//					//buffer.append("\n");
//					informListener(block);
//				}
//			}
//		}
//		catch (SQLException e) {
//			throw new BillingInformationExporterException("Error reading usage details from database", e);
//		}
	}
	

	protected List<Project> getAllProjects() throws BillingInformationExporterException {
		
		ProjectsSearcher searcher = new ProjectsSearcher();
		// searcher.addType(new BilledProject().getShortType());

		List<Project> projects = null;
		try {
			projects = searcher.search();
		} catch (SQLException e) {
			throw new BillingInformationExporterException("Error searching for projects", e);
		}
		return projects;
	}
	
	protected Project getProject(int projectId) throws BillingInformationExporterException {
		Project project = null;
		try {
			project = ProjectFactory.getProject(projectId);
		}  catch (SQLException e) {
			throw new BillingInformationExporterException("Error loading the project from database. ProjectId: "+projectId, e);
		} catch (InvalidIDException e) {
			throw new BillingInformationExporterException("No project found for ID "+projectId, e);
		}
		return project;
	}

	protected List<UsageBlockBase> getSortedUsageBlocksForProject_byStartDate(Project project)
				throws BillingInformationExporterException {

		// get the usage blocks for this project between the start and end dates
		List<UsageBlockBase> usageBlocks = null;
		try {
			usageBlocks = ProjectInstrumentUsageDAO.getInstance().getUsageBlocksForProject(project.getID(), startDate, endDate);
		} catch (SQLException e) {
			throw new BillingInformationExporterException("Error loading usage blocks for project ID: "+project.getID(), e);
		}

		// sort the blocks by start dates
		Collections.sort(usageBlocks, new Comparator<UsageBlockBase>() {
			@Override
			public int compare(UsageBlockBase o1, UsageBlockBase o2) {
				return o1.getStartDate().compareTo(o2.getStartDate());
			}
		});
		return usageBlocks;
	}

	protected void checkDates() throws BillingInformationExporterException {
		if(startDate == null) {
			throw new BillingInformationExporterException("No start date was specified");
		}
		if(endDate == null) {
			throw new BillingInformationExporterException("No end date was specified");
		}
		if(startDate.equals(endDate) || startDate.after(endDate)) {
			throw new BillingInformationExporterException("Start date is after end date");
		}
	}

	private String getHeaderDetailed() {
		
		StringBuilder buf = new StringBuilder();
		
		buf.append("Billing information exported on "+new java.util.Date());
		buf.append("\n");
		buf.append("Start Date: "+this.startDate+"\n");
		buf.append("End Date: "+this.endDate+"\n");
		buf.append("\n");
		buf.append("* Only blocks ending in a billing period are billed in that period.\n");
		
		buf.append("ProjectID\t");
		buf.append("Lab_Director\t");
		buf.append("Instrument\t");
		buf.append("UsageBlockID\t");
		buf.append("Start\t");
		buf.append("End\t");
		buf.append("TimeBlock_Hours\t");
		buf.append("TimeBlock_Cost\t");
		buf.append("Payment_Method\t");
		buf.append("%Billed\t");
		buf.append("AmountBilled\t");
		buf.append("ContactFirstName\t");
		buf.append("ContactLastName\t");
		buf.append("ContactEmail\t");
		buf.append("ContactPhone");
		return buf.toString();
	}
	
	// Format:
	// ProjectID	PI	Instrument	Block	Start	End	PaymentMethod	%Charged	Cost	
	private StringBuilder getBlockDetails(Project project, UsageBlockBase block) throws BillingInformationExporterException, SQLException {
		
		// get the name of the PI
		Researcher labDirector = project.getPI();
		String labDirectorName = labDirector.getLastName();

		// get the instrument
		MsInstrument instrument = MsInstrumentUtils.instance().getMsInstrument(block.getInstrumentID());

		// get the instrument rate
		InstrumentRate rate = InstrumentRateDAO.getInstance().getInstrumentRate(block.getInstrumentRateID());
		BigDecimal fee = rate.getRate().multiply(new BigDecimal(block.getNumHours()));
		if(CostCenterConstants.ADD_SETUP_COST)
			fee = fee.add(CostCenterConstants.SETUP_COST);

		// get the name of the time block
		TimeBlock timeBlock = rate.getTimeBlock();


		// get the payment method(s) for this block
		List<InstrumentUsagePayment> usagePayments = InstrumentUsagePaymentGetter.get(project, block);


		StringBuilder buffer = new StringBuilder();
		for(InstrumentUsagePayment usagePayment: usagePayments) {

			StringBuilder blockBuf = getBlockPaymentMethodDetails(project, labDirectorName, instrument, block, timeBlock, usagePayment, fee);
			if(blockBuf != null) {
				buffer.append(blockBuf.toString());
				buffer.append("\n");
			}
		}
		if(buffer.length() == 0)
			return null;
		else
			return buffer;
	}
	
	// Get the details for a block and payment method combination
	// There may be multiple payment methods associated with each usage block
	private StringBuilder getBlockPaymentMethodDetails(Project project, String labDirectorName, MsInstrument instrument, UsageBlockBase block,
			TimeBlock timeBlock, InstrumentUsagePayment usagePayment, BigDecimal cost)

	throws BillingInformationExporterException {


		StringBuilder buffer = new StringBuilder();

		PaymentMethod paymentMethod = usagePayment.getPaymentMethod();
		BigDecimal percent = usagePayment.getPercent();
		
		BigDecimal billedCost = getBilledCost(cost, percent, block.getStartDate(), block.getEndDate());
		
		// If we are not billing anything ignore this block
		if((BigDecimal.ZERO).equals(billedCost))
			return null;
		
		
		buffer.append(project.getID()+"\t");

		buffer.append(labDirectorName);
		buffer.append("\t");

		buffer.append(instrument.getName());
		buffer.append("\t");

		buffer.append(block.getID()+"\t");
		
		buffer.append(block.getStartDateFormated());
		
		buffer.append("\t");

		buffer.append(block.getEndDateFormated());
		
		buffer.append("\t");
		
		buffer.append(timeBlock.getNumHours()+"\t");
		buffer.append(cost+"\t");
		
		
		String uwBudgetNumber = paymentMethod.getUwbudgetNumber();
		String ponumber = paymentMethod.getPonumber();
		if(uwBudgetNumber != null && uwBudgetNumber.trim().length() > 0) {
			buffer.append(uwBudgetNumber);
		}
		else if(ponumber != null && ponumber.trim().length() > 0) {
			buffer.append(ponumber);
		}
		else {
			throw new BillingInformationExporterException("Did not find a UW Budget number or a PO numer for payment method ID: "
					+paymentMethod.getId());
		}
		buffer.append("\t");

		buffer.append(percent+"%");
		buffer.append("\t");

		
		buffer.append(getBilledCost(cost, percent, block.getStartDate(), block.getEndDate()).toString());
		
		// contact details of the person associated with the payment method
		buffer.append("\t");
		buffer.append(paymentMethod.getContactFirstName());
		buffer.append("\t");
		buffer.append(paymentMethod.getContactLastName());
		buffer.append("\t");
		buffer.append(paymentMethod.getContactEmail());
		buffer.append("\t");
		buffer.append(paymentMethod.getContactPhone());
		
		return buffer;
	}
	
	BigDecimal getBilledCost(BigDecimal blockCost, BigDecimal percent, Date blockStartTime, Date blockEndTime) {
		
		BigDecimal costForTimeUsed = blockCost;
		
		// If this block is within the given startDate and endDate return the original cost of the block
		if(!blockInRange(blockStartTime, blockEndTime)) {
			
			long originalBlockDuration = blockEndTime.getTime() - blockStartTime.getTime();
			
			long blockUsedDuration = getDurationUsedInRange(blockStartTime,blockEndTime);
			
			costForTimeUsed = blockCost.multiply(new BigDecimal(blockUsedDuration)).divide(new BigDecimal(originalBlockDuration), 2, RoundingMode.HALF_UP);
		}
		
		return getPercentCost(costForTimeUsed, percent).setScale(2, RoundingMode.HALF_UP);
	}
	

	protected String getHours(long milliseconds) {
		
		return NUM_HRS_FORMAT.format((double)milliseconds / (1000*60*60));
	}
	
	protected long getDurationUsedInRange(Date blockStartTime,Date blockEndTime) {
		
		long newBlockStartTime = blockStartTime.getTime();
		if(blockStartTime.before(this.startDate)) {
			newBlockStartTime = this.startDate.getTime();
		}
		
		long newBlockEndTime = blockEndTime.getTime();
		if(blockEndTime.after(this.endDate)) {
			newBlockEndTime = this.endDate.getTime();
		}
		
		long blockUsedDuration = newBlockEndTime - newBlockStartTime;
		return blockUsedDuration;
	}

	private boolean blockInRange(Date blockStartTime, Date blockEndTime) {
		return blockStartInRange(blockStartTime) && blockEndInRange(blockEndTime);
	}
	
	protected boolean blockEndInRange(Date blockEndTime) {
		return (blockEndTime.equals(this.endDate) || blockEndTime.before(this.endDate));
	}

	protected boolean blockStartInRange(Date blockStartTime) {
		return (blockStartTime.equals(this.startDate) || blockStartTime.after(this.startDate));
	}
	
	BigDecimal getPercentCost(BigDecimal blockCost, BigDecimal percent) {
		
		if(percent.doubleValue() == 100.0)
			return blockCost;
		else
			return blockCost.multiply(percent.divide(ONE_HUNDRED));
	}
}
