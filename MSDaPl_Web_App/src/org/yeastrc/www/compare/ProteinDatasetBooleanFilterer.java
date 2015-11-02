/**
 * ProteinDatasetBooleanFilterer.java
 * @author Vagisha Sharma
 * Mar 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.Iterator;
import java.util.List;

import org.yeastrc.www.compare.dataset.Dataset;

/**
 * 
 */
public class ProteinDatasetBooleanFilterer {

	private static ProteinDatasetBooleanFilterer instance = null;
	
	private ProteinDatasetBooleanFilterer() {}
	
	public static ProteinDatasetBooleanFilterer getInstance() {
		if(instance == null) {
			instance = new ProteinDatasetBooleanFilterer();
		}
		return instance;
	}
	
	public void applyBooleanFilters(ProteinComparisonDataset dataset, DatasetBooleanFilters filters) {
        
        List<ComparisonProtein> proteins = dataset.getProteins();
        // Apply the AND filters
        applyAndFilter(proteins, filters.getAndFilters());
        
        // Apply the OR filters
        applyOrFilter(proteins, filters.getOrFilters());
        
        // Apply the NOT filters
        applyNotFilter(proteins, filters.getNotFilters());
        
        // Apply the XOR filters
        applyXorFilter(proteins, filters.getXorFilters());
        
    }
	
	private void applyAndFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            for(Dataset dataset: datasets) {
                if(!protein.isInDataset(dataset)) {
                    iter.remove();
                    break;
                }
            }
        }
    }
    
    private void applyOrFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            boolean reject = true;
            for(Dataset dataset: datasets) {
                if(protein.isInDataset(dataset)) {
                    reject = false;
                    break;
                }
            }
            if(reject)  iter.remove();
        }
    }
    
    private void applyNotFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            for(Dataset dataset: datasets) {
                if(protein.isInDataset(dataset)) {
                    iter.remove();
                    break;
                }
            }
        }
    }
    
    private void applyXorFilter(List<ComparisonProtein> proteins, List<? extends Dataset> datasets) {
        
        if(datasets.size() ==0)
            return;
        
        Iterator<ComparisonProtein> iter = proteins.iterator();
        while(iter.hasNext()) {
            ComparisonProtein protein = iter.next();
            
            int numOccur = 0;
            boolean reject = false;
            for(Dataset dataset: datasets) {
                if(protein.isInDataset(dataset)) {
                    numOccur++;
                    if(numOccur > 1) {
                        reject = true;
                        break;
                    }
                }
            }
            if(reject)  iter.remove();
        }
    }

	
}
