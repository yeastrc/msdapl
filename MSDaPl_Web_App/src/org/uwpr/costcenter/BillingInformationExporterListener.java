/**
 * BillingInformationExporterListener.java
 * @author Vagisha Sharma
 * Jul 16, 2011
 */
package org.uwpr.costcenter;

import org.uwpr.instrumentlog.UsageBlockBase;

/**
 * 
 */
public interface BillingInformationExporterListener {

	public void blockExported(UsageBlockBase block) throws BillingInformationExporterException;
}
