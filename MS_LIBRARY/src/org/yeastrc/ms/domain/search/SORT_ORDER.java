/**
 * SORT_ORDER.java
 * @author Vagisha Sharma
 * Apr 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search;

/**
 * 
 */
public enum SORT_ORDER {

    ASC, DESC;
    public static SORT_ORDER getSortByForName(String sortOrder) {
        if(sortOrder == null || sortOrder.equalsIgnoreCase("ASC")) return ASC;
        else return DESC;
    }
    public static SORT_ORDER defaultSortOrder() {
        return ASC;
    }
    
}
