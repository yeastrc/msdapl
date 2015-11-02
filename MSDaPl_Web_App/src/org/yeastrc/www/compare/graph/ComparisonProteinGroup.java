/**
 * ComparisonProteinGroup.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.graph;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.ComparisonProtein;
import org.yeastrc.www.compare.dataset.Dataset;

import edu.uwpr.protinfer.infer.graph.Vertex;


/**
 * 
 */
public class ComparisonProteinGroup extends Vertex<ComparisonProteinGroup> {

    private int groupId;
    private List<ComparisonProtein> proteins;
    
    public ComparisonProteinGroup(ComparisonProtein protein) {
        super(String.valueOf(protein.getNrseqId()));
        proteins = new ArrayList<ComparisonProtein>();
        proteins.add(protein);
    }
    
    public ComparisonProteinGroup(List<ComparisonProtein> proteins) {
        super(makeLabel(proteins));
        this.proteins = proteins;
    }
    
    @Override
    public int getMemberCount() {
        return proteins.size();
    }
    
    public List<ComparisonProtein> getProteins() {
        return this.proteins;
    }
    
    /** Added for de-serialization */
    public void setProteins(List<ComparisonProtein> proteins) {
		this.proteins = proteins;
	}

	@Override
    public ComparisonProteinGroup combineWith(ComparisonProteinGroup v) {
        List<ComparisonProtein> allProteins = new ArrayList<ComparisonProtein>(proteins.size() + v.getProteins().size());
        allProteins.addAll(proteins);
        allProteins.addAll(v.getProteins());
        
        ComparisonProteinGroup newVertex = new ComparisonProteinGroup(allProteins);
        return newVertex;
    }
    @Override
    public ComparisonProteinGroup combineWith(List<ComparisonProteinGroup> vertices) {
        List<ComparisonProtein> allProteins = new ArrayList<ComparisonProtein>();
        allProteins.addAll(proteins);
        for(ComparisonProteinGroup vertex: vertices) {
            allProteins.addAll(vertex.getProteins());
        }
        ComparisonProteinGroup newVertex = new ComparisonProteinGroup(allProteins);
        return newVertex;
    }
   
    private static String makeLabel(List<ComparisonProtein> proteins) {
        StringBuilder buf = new StringBuilder();
        for(ComparisonProtein protein: proteins) 
            buf.append(protein.getNrseqId()+"_");
        buf.deleteCharAt(buf.length() - 1);
        return buf.toString();
    }
    
    public boolean hasParsimoniousProtein() {
        for(ComparisonProtein protein: proteins) {
            if(protein.isParsimonious())
                return true;
        }
        return false;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
        for(ComparisonProtein protein: proteins)
            protein.setGroupId(groupId);
    }
    
    public int getTotalPeptideSeqCount() {
        return proteins.get(0).getTotalPeptideSeqCount();
    }
    
    /**
     * Returns true if any of the proteins in this group was found in the given dataset
     * @param dataset
     * @return
     */
    public boolean isInDataset(Dataset dataset) {
    	
    	for(ComparisonProtein protein: this.proteins) {
    		if(protein.isInDataset(dataset))
    			return true;
    	}
        return false;
    }
}
