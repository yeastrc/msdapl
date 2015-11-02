package edu.uwpr.protinfer.infer;

import java.util.ArrayList;
import java.util.List;

public class Protein {

    private String accession;
    private String description = "";
    private int id; // could be a database id
    private boolean isDecoy = false;
    private boolean isAccepted = false;
    private boolean isSubset = false;
    private int proteinClusterLabel;
    private int proteinGroupLabel;
    private List<Integer> superProteinGroupLabels;
    
    /**
     * @param accession
     * @param id unique id for this protein
     */
    public Protein(String accession, int id) {
        this.accession = accession;
        this.id = id;
        this.superProteinGroupLabels = new ArrayList<Integer>();
    }
    
    public String getAccession() {
        return accession;
    }
    
    public void setAccession(String accession) {
        this.accession = accession;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public void setDecoy() {
        this.isDecoy = true;
    }
    
    public boolean isDecoy() {
        return isDecoy;
    }
    
    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
    
    public boolean isSubset() {
		return isSubset;
	}

	public void setSubset(boolean isSubset) {
		this.isSubset = isSubset;
	}

	public List<Integer> getSuperProteinGroupLabels() {
		return superProteinGroupLabels;
	}

	public void setSuperProteinGroupLabels(List<Integer> superProteinGroupLabels) {
		this.superProteinGroupLabels = superProteinGroupLabels;
	}

	public void setProteinClusterLabel(int proteinClusterId) {
        this.proteinClusterLabel = proteinClusterId;
    }

    public int getProteinClusterLabel() {
        return proteinClusterLabel;
    }
    
    public int getProteinGroupLabel() {
        return proteinGroupLabel;
    }

    public void setProteinGroupLabel(int proteinGroupLabel) {
        this.proteinGroupLabel = proteinGroupLabel;
    }
    
    public String toString() {
        return accession+"\tID:"+id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        if(description != null)
            this.description = description;
    }
}
