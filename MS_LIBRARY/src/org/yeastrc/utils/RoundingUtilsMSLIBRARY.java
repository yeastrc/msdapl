/**
 * RoundingUtilsMSLIBRARY.java copied from MSDaPl RoundingUtils.java
 * @version 1.0
 */
package org.yeastrc.utils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;

/**
 *
 */
public class RoundingUtilsMSLIBRARY {


	private RoundingUtilsMSLIBRARY() {

	}


	public static RoundingUtilsMSLIBRARY getInstance() {
		return new RoundingUtilsMSLIBRARY();
	}

    /**
     * Round to 3 signicant digits and return a string
     * @param num
     * @return
     */
    public String roundThreeSignificantDigits( double num ) {

    	final int significantDigitCount = 3;
    	
    	if ( num == 0 ) {
    		
    		return "0";
    	}

    	BigDecimal bd = new BigDecimal( num, new MathContext( significantDigitCount ) );

		DecimalFormat formatter = new DecimalFormat( "0.##E0");

		String rounded = formatter.format( bd );

    	return rounded;
    }

}
