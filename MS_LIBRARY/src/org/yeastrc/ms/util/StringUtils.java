/**
 * StringUtils.java
 * @author Vagisha Sharma
 * Sep 11, 2009
 * @version 1.0
 */
package org.yeastrc.ms.util;

import java.util.Collection;

/**
 * 
 */
public class StringUtils {

    public static <T extends Object>  String makeCommaSeparated(Collection<T> entries) {
        StringBuilder buf = new StringBuilder();
        for(T entry: entries)
            buf.append(","+entry.toString());
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        
        return buf.toString();
    }
    
    public static <T extends Object>  String makeQuotedCommaSeparated(Collection<T> entries) {
        StringBuilder buf = new StringBuilder();
        for(T entry: entries)
            buf.append(",'"+entry.toString()+"'");
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        
        return buf.toString();
    }
}
