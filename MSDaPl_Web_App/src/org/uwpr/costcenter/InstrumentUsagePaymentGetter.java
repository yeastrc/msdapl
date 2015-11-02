/**
 * 
 */
package org.uwpr.costcenter;

import java.sql.SQLException;
import java.util.List;

import org.uwpr.instrumentlog.InstrumentUsagePayment;
import org.uwpr.instrumentlog.InstrumentUsagePaymentDAO;
import org.uwpr.instrumentlog.UsageBlockBase;
import org.yeastrc.project.Project;

/**
 * InstrumentUsagePaymentGetter.java
 * @author Vagisha Sharma
 * Sep 3, 2011
 * 
 */
public class InstrumentUsagePaymentGetter {

	private InstrumentUsagePaymentGetter() {}
	
	public static List<InstrumentUsagePayment> get(Project project, UsageBlockBase block) throws SQLException {
		
		List<InstrumentUsagePayment> usagePayments = null; 
		
		usagePayments = InstrumentUsagePaymentDAO.getInstance().getPaymentsForUsage(block.getID());
		
		return usagePayments;
		
	}
}
