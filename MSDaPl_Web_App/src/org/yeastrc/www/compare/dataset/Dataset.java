/**
 * Dataset.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import java.io.Serializable;


/**
 * 
 */
public class Dataset implements Serializable {

    private int datasetId;
    private String datasetComments;
    private String datasetName;
    private DatasetSource source;
    private float spectrumCountNormalizationFactor = 1.0f;
    private int totalSpectrumCount;
    private int maxProteinSpectrumCount = 1;
    private int minProteinSpectrumCount = 1;
    
    public Dataset() {}
    
    public Dataset(int datasetId, DatasetSource source) {
        this.datasetId = datasetId;
        this.source = source;
    }
    
    public int getDatasetId() {
        return datasetId;
    }
    
    public DatasetSource getSource() {
        return source;
    }
    public String getSourceString() {
        return source.name();
    }
    
    public float getSpectrumCountNormalizationFactor() {
        return spectrumCountNormalizationFactor;
    }

    public void setSpectrumCountNormalizationFactor(
            float spectrumCountNormalizationFactor) {
        this.spectrumCountNormalizationFactor = spectrumCountNormalizationFactor;
    }
    
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(!(o instanceof Dataset))
            return false;
        
        Dataset that = (Dataset)o;
        return (this.datasetId == that.datasetId && this.source == that.source);
    }
    
    public int hashCode() {
        return source.hashCode() + Integer.valueOf(datasetId).hashCode();
    }

    public void setDatasetId(int datasetId) {
        this.datasetId = datasetId;
    }

    public void setSource(DatasetSource source) {
        this.source = source;
    }
    
    public void setSourceString(String sourceStr) {
        this.source = DatasetSource.instance(sourceStr);
    }

    public int getSpectrumCount() {
        return totalSpectrumCount;
    }

    public void setSpectrumCount(int spectrumCount) {
        this.totalSpectrumCount = spectrumCount;
    }

    public int getMaxProteinSpectrumCount() {
        return maxProteinSpectrumCount;
    }
    
    public float getNormMaxProteinSpectrumCount() {
        return maxProteinSpectrumCount * this.spectrumCountNormalizationFactor;
    }

    public void setMaxProteinSpectrumCount(int maxProteinSpectrumCount) {
        this.maxProteinSpectrumCount = maxProteinSpectrumCount;
    }

    public int getMinProteinSpectrumCount() {
        return minProteinSpectrumCount;
    }

    public float getNormMinProteinSpectrumCount() {
        return minProteinSpectrumCount * this.spectrumCountNormalizationFactor;
    }
    
    public void setMinProteinSpectrumCount(int minProteinSpectrumCount) {
        this.minProteinSpectrumCount = minProteinSpectrumCount;
    }

    public String getDatasetComments() {
        return datasetComments;
    }

    public void setDatasetComments(String datasetComments) {
        this.datasetComments = datasetComments;
    }

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
}
