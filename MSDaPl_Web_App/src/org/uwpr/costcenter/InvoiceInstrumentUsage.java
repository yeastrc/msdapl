/**
 * InvoiceInstrumentUsage.java
 * @author Vagisha Sharma
 * Jul 16, 2011
 */
package org.uwpr.costcenter;

/**
 * 
 */
public class InvoiceInstrumentUsage {

	private int id;
	private int invoiceId;
	private int instrumentUsageId;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}
	public int getInstrumentUsageId() {
		return instrumentUsageId;
	}
	public void setInstrumentUsageId(int instrumentUsageId) {
		this.instrumentUsageId = instrumentUsageId;
	}
}
