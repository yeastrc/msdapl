/**
 * ProteinDatasetComparisonFilters.java
 * @author Vagisha Sharma
 * Apr 14, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.yeastrc.www.compare.dataset.Dataset;

/**
 * 
 */
public class DatasetBooleanFilters {

    private List<Dataset> andFilters;
    private List<Dataset> orFilters;
    private List<Dataset> notFilters;
    private List<Dataset> xorFilters;
    
    //private static final Logger log = Logger.getLogger(DatasetBooleanFilters.class.getName());
    
    public DatasetBooleanFilters() {
        andFilters = new ArrayList<Dataset>();
        orFilters = new ArrayList<Dataset>();
        notFilters = new ArrayList<Dataset>();
        xorFilters = new ArrayList<Dataset>();
    }
    
    public boolean equals(Object o) {
    	if(this == o)
    		return true;
    	if(!(o instanceof DatasetBooleanFilters))
    		return false;
    	DatasetBooleanFilters that = (DatasetBooleanFilters)o;
    	
    	if(!compareFilters(andFilters, that.getAndFilters()))
    		return false;
    	if(!compareFilters(orFilters, that.getOrFilters()))
    		return false;
    	if(!compareFilters(notFilters, that.getNotFilters()))
    		return false;
    	if(!compareFilters(xorFilters, that.getXorFilters()))
    		return false;
    	
    	return true;
    }
    
    private boolean compareFilters(List<Dataset> myFilters, List<Dataset> theirFilters) {
    	
    	List<Integer> myIds = new ArrayList<Integer>();
    	List<Integer> theirIds = new ArrayList<Integer>();
    	
    	for(Dataset ds: myFilters)
    		myIds.add(ds.getDatasetId());
    	
    	for(Dataset ds: theirFilters)
    		theirIds.add(ds.getDatasetId());
    	
    	Collections.sort(myIds);
    	Collections.sort(theirIds);
    	
    	if(myIds.size() != theirIds.size())
    		return false;
    	
    	//log.info("Comparing boolean filters (myIds): "+myIds);
    	//log.info("Comparing boolean filters (theirIds): "+theirIds);
    	for(int i = 0; i < myIds.size(); i++) {
    		if(myIds.get(i).intValue() != theirIds.get(i).intValue()) {
    			return false;
    		}
    	}
    	
    	return true;
    }

    public List<Dataset> getAndFilters() {
        return andFilters;
    }

    public void setAndFilters(List<Dataset> andFilters) {
        this.andFilters = andFilters;
    }

    public List<Dataset> getOrFilters() {
        return orFilters;
    }

    public void setOrFilters(List<Dataset> orFilters) {
        this.orFilters = orFilters;
    }

    public List<Dataset> getNotFilters() {
        return notFilters;
    }

    public void setNotFilters(List<Dataset> notFilters) {
        this.notFilters = notFilters;
    }

    public List<Dataset> getXorFilters() {
        return xorFilters;
    }
    
    public void setXorFilters(List<Dataset> xorFilters) {
        this.xorFilters = xorFilters;
    }
}
