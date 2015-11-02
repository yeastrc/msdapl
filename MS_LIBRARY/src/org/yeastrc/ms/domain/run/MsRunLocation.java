/**
 * MsRunLocationDb.java
 * @author Vagisha Sharma
 * Aug 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.run;

import java.sql.Date;


/**
 * 
 */
public interface MsRunLocation extends MsRunLocationIn {
    /**
     * @return the database id for the location
     */
    public abstract int getId();
    
    /**
     * @return the database id for the run.
     */
    public abstract int getRunId();
    
    /**
     * @return the createDate
     */
    public abstract Date getCreateDate();
}
