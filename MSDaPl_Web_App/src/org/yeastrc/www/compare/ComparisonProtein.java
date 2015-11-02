/**
 * ComparisonProtein.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.nr_seq.listing.ProteinReference;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;

/**
 * 
 */
public class ComparisonProtein implements Serializable {

    private int nrseqId;
    private int groupId;
	private ProteinListing proteinListing;
	private float molecularWeight = -1.0f;
    private float pi = -1.0f;
    private int totalPeptideSeqCount; 
    //private int maxPeptideIonCount;
    
    private List<DatasetProteinInformation> datasetInformation;
    
    public ComparisonProtein(int nrseqId) {
        this.nrseqId = nrseqId;
        datasetInformation = new ArrayList<DatasetProteinInformation>();
    }
    
    public void setProteinListing(ProteinListing listing) {
    	this.proteinListing = listing;
    }
    
    public ProteinListing getProteinListing() {
    	return this.proteinListing;
    }
    
    public void setNrseqId(int nrseqId) {
		this.nrseqId = nrseqId;
	}
    
    public int getNrseqId() {
        return nrseqId;
    }

    public List<DatasetProteinInformation> getDatasetInformation() {
        return datasetInformation;
    }
    
    public void setDatasetInformation(List<DatasetProteinInformation> infoList) {
        this.datasetInformation = infoList;
    }
    
    public void addDatasetInformation(DatasetProteinInformation info) {
        datasetInformation.add(info);
    }
    
    public DatasetProteinInformation getDatasetProteinInformation(Dataset dataset) {
        
        for(DatasetProteinInformation dsInfo: datasetInformation) {
            if(dataset.equals(dsInfo.getDataset())) {
                return dsInfo;
            }
        }
        return null;
    }
    
    public boolean isInDataset(Dataset dataset) {
        DatasetProteinInformation dpi = getDatasetProteinInformation(dataset);
        if(dpi != null)
            return dpi.isPresent();
        return false;
    }

    public int getTotalPeptideSeqCount() {
        return totalPeptideSeqCount;
    }

    public void setTotalPeptideSeqCount(int totalPeptideSeqCount) {
        this.totalPeptideSeqCount = totalPeptideSeqCount;
    }
    
//    public int getMaxPeptideIonCount() {
//        return maxPeptideIonCount;
//    }
//
//    public void setMaxPeptideIonCount(int maxPeptideIonCount) {
//        this.maxPeptideIonCount = maxPeptideIonCount;
//    }

    public boolean isParsimonious() {
        for(DatasetProteinInformation dpi: this.datasetInformation) {
            if(dpi.isParsimonious())
                return true;
        }
        return false;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
    
    public void setMolecularWeight(float weight) {
        this.molecularWeight = weight;
    }
    
    public float getMolecularWeight() {
        return this.molecularWeight;
    }
    
    public float getPi() {
        return pi;
    }
    
    public void setPi(float pi) {
        this.pi = pi;
    }
    
    public boolean molWtAndPiSet() {
        return molecularWeight != -1.0f && pi != -1.0;
    }
    
    public String getAccessionsCommaSeparated() throws SQLException {
    	List<String> accessions = proteinListing.getFastaAccessions();
    	return StringUtils.makeCommaSeparated(accessions);
    }
    
    public ProteinReference getOneDescriptionReference() throws SQLException {
    	if(proteinListing.getDescriptionReferences().size() > 0)
    		return proteinListing.getDescriptionReferences().get(0);
    	return null;
    }
    
    public String getCommonNamesCommaSeparated() throws SQLException {
    	List<String> names = proteinListing.getCommonNames();
    	return StringUtils.makeCommaSeparated(names);
    }
    
}
