/**
 * WIdPickerResultSummary.java
 * @author Vagisha Sharma
 * Jan 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

/**
 * 
 */
public class WIdPickerResultSummary {

	private int allProteinCount;
	private int allParsimoniousProteinCount;
	private int allNonSubsetProteinCount;
	
	private int allProteinGroupCount;
	private int allParsimoniousProteinGroupCount;
	private int allNonSubsetProteinGroupCount;
	
    private int filteredProteinCount;
    private int filteredParsimoniousProteinCount;
    private int filteredNonSubsetProteinCount;
    
    private int filteredProteinGroupCount;
    private int filteredParsimoniousProteinGroupCount;
    private int filteredNonSubsetProteinGroupCount;
    
    private boolean hasSubsetInformation = false;
    
    public int getFilteredProteinCount() {
        return filteredProteinCount;
    }
    public void setFilteredProteinCount(int filteredProteinCount) {
        this.filteredProteinCount = filteredProteinCount;
    }
    public int getFilteredParsimoniousProteinCount() {
        return filteredParsimoniousProteinCount;
    }
    public void setFilteredParsimoniousProteinCount(
            int filteredParsimoniousProteinCount) {
        this.filteredParsimoniousProteinCount = filteredParsimoniousProteinCount;
    }
    public int getFilteredProteinGroupCount() {
        return filteredProteinGroupCount;
    }
    public void setFilteredProteinGroupCount(int filteredProteinGroupCount) {
        this.filteredProteinGroupCount = filteredProteinGroupCount;
    }
    public int getFilteredParsimoniousProteinGroupCount() {
        return filteredParsimoniousProteinGroupCount;
    }
    public void setFilteredParsimoniousProteinGroupCount(
            int filteredParsimoniousProteinGroupCount) {
        this.filteredParsimoniousProteinGroupCount = filteredParsimoniousProteinGroupCount;
    }
	public int getAllProteinCount() {
		return allProteinCount;
	}
	public void setAllProteinCount(int allProteinCount) {
		this.allProteinCount = allProteinCount;
	}
	public int getAllParsimoniousProteinCount() {
		return allParsimoniousProteinCount;
	}
	public void setAllParsimoniousProteinCount(int allParsimoniousProteinCount) {
		this.allParsimoniousProteinCount = allParsimoniousProteinCount;
	}
	public int getAllNonSubsetProteinCount() {
		return allNonSubsetProteinCount;
	}
	public void setAllNonSubsetProteinCount(int allNonSubsetProteinCount) {
		this.allNonSubsetProteinCount = allNonSubsetProteinCount;
	}
	public int getAllProteinGroupCount() {
		return allProteinGroupCount;
	}
	public void setAllProteinGroupCount(int allProteinGroupCount) {
		this.allProteinGroupCount = allProteinGroupCount;
	}
	public int getAllParsimoniousProteinGroupCount() {
		return allParsimoniousProteinGroupCount;
	}
	public void setAllParsimoniousProteinGroupCount(
			int allParsimoniousProteinGroupCount) {
		this.allParsimoniousProteinGroupCount = allParsimoniousProteinGroupCount;
	}
	public int getAllNonSubsetProteinGroupCount() {
		return allNonSubsetProteinGroupCount;
	}
	public void setAllNonSubsetProteinGroupCount(int allNonSubsetProteinGroupCount) {
		this.allNonSubsetProteinGroupCount = allNonSubsetProteinGroupCount;
	}
	public int getFilteredNonSubsetProteinCount() {
		return filteredNonSubsetProteinCount;
	}
	public void setFilteredNonSubsetProteinCount(int filteredNonSubsetProteinCount) {
		this.filteredNonSubsetProteinCount = filteredNonSubsetProteinCount;
	}
	public int getFilteredNonSubsetProteinGroupCount() {
		return filteredNonSubsetProteinGroupCount;
	}
	public void setFilteredNonSubsetProteinGroupCount(
			int filteredNonSubsetProteinGroupCount) {
		this.filteredNonSubsetProteinGroupCount = filteredNonSubsetProteinGroupCount;
	}
	public boolean isHasSubsetInformation() {
		return hasSubsetInformation;
	}
	public void setHasSubsetInformation(boolean hasSubsetInformation) {
		this.hasSubsetInformation = hasSubsetInformation;
	}
}
