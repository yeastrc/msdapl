/**
 * SQTParserException.java
 * @author Vagisha Sharma
 * Jul 31, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile;

/**
 * 
 */
public class SQTParseException extends Exception {

    public static final byte FATAL = 0;
    public static final byte NON_FATAL = 1;
    
    private final byte errCode;
    
    public SQTParseException(String message) {
        super(message);
        errCode = FATAL;
    }
    
    public SQTParseException(String message, Exception e) {
        super(message, e);
        errCode = FATAL;
    }
    
    public SQTParseException(String message, byte errorCode) {
        super(message);
        this.errCode = errorCode;
    }
    
    public SQTParseException(String message, byte errorCode, Exception e) {
        super(message, e);
        this.errCode = errorCode;
    }
    
    public boolean isFatal() {
        return errCode == FATAL;
    }
}
