package org.yeastrc.ms.domain.protinfer;

public class ProteinferSpectrumMatch {

    private int id;
    private int pinferIonId;
    private int resultId;
//    private int searchResultId;
    private int scanId;
    private int rank; // rank of this spectrum match for the peptide (not the ion).
    

    public ProteinferSpectrumMatch() {}
    
    public ProteinferSpectrumMatch(int pinferIonId, int resultId) {
        this.pinferIonId = pinferIonId;
        this.resultId = resultId;
//        this.searchResultId = searchResultId;
    }
    
    public int getProteinferIonId() {
        return pinferIonId;
    }
    
    public void setProteinferIonId(int pinferIonId) {
        this.pinferIonId = pinferIonId;
    }
    
    public int getResultId() {
        return resultId;
    }
    
    public void setResultId(int resultId) {
        this.resultId = resultId;
    }
    
//    public int getSearchResultId() {
//        return searchResultId;
//    }
//    
//    public void setSearchResultId(int searchResultId) {
//        this.searchResultId = searchResultId;
//    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

	public int getScanId() {
		return scanId;
	}

	public void setScanId(int scanId) {
		this.scanId = scanId;
	}
}
