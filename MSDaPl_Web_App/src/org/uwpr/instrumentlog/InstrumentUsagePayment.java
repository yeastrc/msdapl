/**
 * 
 */
package org.uwpr.instrumentlog;

import java.math.BigDecimal;

import org.yeastrc.project.payment.PaymentMethod;

/**
 * InstrumentUsagePayment.java
 * @author Vagisha Sharma
 * Jun 2, 2011
 * 
 */
public class InstrumentUsagePayment {

	private int instrumentUsageId;
	private PaymentMethod paymentMethod;
	private BigDecimal percent;
	
	public int getInstrumentUsageId() {
		return instrumentUsageId;
	}
	
	public void setInstrumentUsageId(int instrumentUsageId) {
		this.instrumentUsageId = instrumentUsageId;
	}
	
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public BigDecimal getPercent() {
		return percent;
	}
	
	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}
}
