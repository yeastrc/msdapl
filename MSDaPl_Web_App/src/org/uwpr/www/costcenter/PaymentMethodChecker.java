/**
 * PaymentMethodChecker.java
 * @author Vagisha Sharma
 * Jun 23, 2011
 */
package org.uwpr.www.costcenter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 */
public class PaymentMethodChecker {

	private static final PaymentMethodChecker instance = new PaymentMethodChecker();
	
	private PaymentMethodChecker() {}
	
	public static PaymentMethodChecker getInstance() {
		return instance;
	}
	
	private static final Pattern uwbudgetNumberPattern = Pattern.compile("\\d{2}-\\d{4}");
	
	public boolean checkUwbudgetNumber(String budgetNumberString) {
		Matcher m = uwbudgetNumberPattern.matcher(budgetNumberString.trim());
		return m.matches();
	}
	
	public boolean checkPonumber(String ponumber) {
		return ponumber.trim().length() >= 4;
	}
	
}
