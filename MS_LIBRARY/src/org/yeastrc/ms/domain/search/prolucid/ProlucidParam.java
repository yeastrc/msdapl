/**
 * ProlucidParamDb.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.prolucid;

/**
 * 
 */
public interface ProlucidParam {

    public abstract int getId();
    
    public abstract String getParamElementName();
    
    public abstract String getParamElementValue();

    public abstract int getParentParamElementId();
}
