/**
 * 
 */
package org.yeastrc.ms.parser.unimod;

/**
 * UnimodRepositoryException.java
 * @author Vagisha Sharma
 * Aug 2, 2011
 * 
 */
public class UnimodRepositoryException extends Exception {

	public UnimodRepositoryException (String message) {
		super(message);
	}
	
	public UnimodRepositoryException (String message, Throwable t) {
		super(message, t);
	}
}
