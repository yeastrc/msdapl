/**
 * RoundingUtils.java
 * @author Vagisha Sharma
 * Mar 2, 2010
 * @version 1.0
 */
package org.yeastrc.www.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 *
 */
public class RoundingUtils {

//  Not safe to use Singleton single instance since DecimalFormat is not thread safe
//	private static RoundingUtils instance = null;

	//  Use the internal getters for these only
	private DecimalFormat roundOneFmt;
	private DecimalFormat roundTwoFmt;
	private DecimalFormat roundFourFmt;

	private RoundingUtils() {

	}


//	public static synchronized RoundingUtils getInstance() {
//		if(instance == null) {
//			instance = new RoundingUtils();
//		}
//		return instance;
//	}

	public static RoundingUtils getInstance() {
		return new RoundingUtils();
	}


	private DecimalFormat getRoundOneFmt() {

		if ( roundOneFmt == null ) {

			roundOneFmt = new DecimalFormat("#0.0");
			roundOneFmt.setRoundingMode(RoundingMode.HALF_UP);
		}

		return roundOneFmt;
	}


	private DecimalFormat getRoundTwoFmt() {

		if ( roundTwoFmt == null ) {

			roundTwoFmt = new DecimalFormat("#0.00");
			roundTwoFmt.setRoundingMode(RoundingMode.HALF_UP);
		}

		return roundTwoFmt;
	}


	private DecimalFormat getRoundFourFmt() {

		if ( roundFourFmt == null ) {

			roundFourFmt = new DecimalFormat("#0.0000");
			roundFourFmt.setRoundingMode(RoundingMode.HALF_UP);
		}

		return roundFourFmt;
	}


	public double roundOne(BigDecimal number) {
        return roundOne(number.doubleValue());
    }

    public double roundOne(double num) {
        return Math.round(num*10.0)/10.0;
    }

    public String roundOneFormat(double num) {
    	return getRoundOneFmt().format(num);
    }

    public String roundOneFormat(BigDecimal number) {
    	return getRoundOneFmt().format(number.doubleValue());
    }

	public double roundTwo(BigDecimal number) {
        return roundTwo(number.doubleValue());
    }
    public double roundTwo(double num) {
        return Math.round(num*100.0)/100.0;
    }

    public String roundTwoFormat(double num) {
    	return getRoundTwoFmt().format(num);
    }

    public String roundTwoFormat(BigDecimal number) {
    	return getRoundTwoFmt().format(number.doubleValue());
    }

    public double roundFour(BigDecimal number) {
        return roundTwo(number.doubleValue());
    }
    public double roundFour(double num) {
        return Math.round(num*10000.0)/10000.0;
    }

    public String roundFourFormat(double num) {
    	return getRoundFourFmt().format(num);
    }

    public String roundFourFormat(BigDecimal number) {
    	return getRoundFourFmt().format(number.doubleValue());
    }



}
