/**
 * SQTHeaderDb.java
 * @author Vagisha Sharma
 * Jul 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.sqtfile;


/**
 * 
 */
public interface SQTHeaderItem {

    /**
     * @return the name of the field
     */
    public abstract String getName();

    /**
     * @return the value of the field
     */
    public abstract String getValue();
}
