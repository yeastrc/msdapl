package org.uwpr.www.scheduler;

import org.apache.struts.action.ActionForm;

/**
 * User: vsharma
 * Date: 9/6/13
 * Time: 10:51 PM
 */
public class ViewTimeScheduledForInstrumentForm extends ActionForm {

    private int instrumentId = 0;
    private int paymentMethodId = 0;
    private String startDateString;
    private String endDateString;;

    public int getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }

    public int getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(int paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getStartDateString() {
        return startDateString;
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public String getEndDateString() {
        return endDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }
}
