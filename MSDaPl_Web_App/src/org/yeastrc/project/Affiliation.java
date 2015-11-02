/**
 * Affiliation.java
 * @author Vagisha Sharma
 * May 20, 2011
 */
package org.yeastrc.project;

import java.util.Arrays;
import java.util.List;

/**
 * 
 */
public enum Affiliation {

	internal("University of Washington"),
    external("External");
    
    private String longName;
    
    private Affiliation(String longName) {
        this.longName = longName;
    }
    public String getLongName() {
        return longName;
    }
    public String getName() { // for Struts
    	return name();
    }
    public String toString() {
        return longName;
    }
    
    public static List<Affiliation> getList() {
    	return Arrays.asList(Affiliation.values());
    }
    public static Affiliation forName(String name) {
    	
    	for(Affiliation aff: Affiliation.values()) {
    		if(aff.name().equals(name))
    			return aff;
    	}
    	return null;
    }
}
