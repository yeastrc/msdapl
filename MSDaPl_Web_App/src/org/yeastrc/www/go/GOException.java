/**
 * GOException.java
 * @author Vagisha Sharma
 * May 19, 2010
 * @version 1.0
 */
package org.yeastrc.www.go;

/**
 * 
 */
public class GOException extends Exception {

	public GOException (String message, Throwable cause) {
        super(message, cause);
    }
	
	public GOException (String message) {
		super(message);
	}
}
