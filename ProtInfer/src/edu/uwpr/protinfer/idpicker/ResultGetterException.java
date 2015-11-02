/**
 * 
 */
package edu.uwpr.protinfer.idpicker;

/**
 * PercolatorResultGetterException.java
 * @author Vagisha Sharma
 * Nov 10, 2010
 * 
 */
public class ResultGetterException extends Exception {

	public ResultGetterException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ResultGetterException(String message, Throwable cause) {
		super(message, cause);
	}

}
