/**
 * ModifiedSequenceBuilderException.java
 * @author Vagisha Sharma
 * Jul 30, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

/**
 * 
 */
public class ModifiedSequenceBuilderException extends Exception {

    public ModifiedSequenceBuilderException() {
        super();
    }

    /**
     * @param message
     */
    public ModifiedSequenceBuilderException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ModifiedSequenceBuilderException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ModifiedSequenceBuilderException(String message, Throwable cause) {
        super(message, cause);
    }

}
