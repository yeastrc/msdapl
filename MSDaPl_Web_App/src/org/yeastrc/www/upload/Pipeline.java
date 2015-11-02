/**
 * Pipeline.java
 * @author Vagisha Sharma
 * Aug 20, 2009
 * @version 1.0
 */
package org.yeastrc.www.upload;

/**
 * 
 */
public enum Pipeline {
    MACOSS ("MacCoss Lab Pipeline"), TPP("Trans-Proteomic Pipeline");
    
    private String longName;
    
    private Pipeline(String longName) {
        this.longName = longName;
    }
    
    public String getLongName() {
        return longName;
    }
    
    public static Pipeline forName(String name) {
        if(MACOSS.name().equals(name))
            return MACOSS;
        else if(TPP.name().equals(name))
            return TPP;
        else
            return null;
    }
}
