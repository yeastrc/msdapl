/**
 * DatabaseCopyException.java
 * @author Vagisha Sharma
 * Sep 29, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.database;

import java.sql.SQLException;

/**
 * 
 */
public class DatabaseCopyException extends Exception {

    public DatabaseCopyException(String message) {
        super(message);
    }

    public DatabaseCopyException(String string, SQLException e) {
        super(string, e);
    }
}
