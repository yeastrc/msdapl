/**
 * ColumnFilters.java
 * @author Vagisha Sharma
 * Apr 26, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class DisplayColumns {

	
	
	public static final char groupId 		= 'G';
	public static final char validation 	= 'V';
	public static final char fasta 			= 'F';
	public static final char commonName 	= 'N';
	public static final char description 	= 'D';
	public static final char molWt 			= 'W';
	public static final char pi 			= 'I';
	public static final char philiusAnnot 	= 'H';
	public static final char coverage 		= 'C';
	public static final char nsaf 			= 'A';
	public static final char yeastAbundance = 'Y';
	public static final char numPept 		= 'P';
	public static final char numUniqPept	= 'U';
	public static final char numSpectra 	= 'S';
	public static final char clusterLabel 	= 'L';
	
	private DisplayColumn showGroupId = new DisplayColumn(groupId, "Group ID", false, false);
	private DisplayColumn showValidation = new DisplayColumn(validation, "Validation Status", false, false);
	private DisplayColumn showFastaId = new DisplayColumn(fasta, "Fasta ID", false, false);
	private DisplayColumn showCommonName = new DisplayColumn(commonName, "Common Name", false, false);
	private DisplayColumn showDescription = new DisplayColumn(description, "Description", false, false);
	private DisplayColumn showMolWt = new DisplayColumn(molWt, "Molecular Wt.", false, false);
	private DisplayColumn showPi = new DisplayColumn(pi, "pI", false, false);
	private DisplayColumn showPhiliusAnnotations = new DisplayColumn(philiusAnnot, "Philius Annotations", false, false);
	private DisplayColumn showCoverage = new DisplayColumn(coverage, "Coverage", false, false);
	private DisplayColumn showNsaf = new DisplayColumn(nsaf, "NSAF", false, false);
	private DisplayColumn showYeastCopiesPerCell = new DisplayColumn(yeastAbundance, "# Protein copies / cell (Yeast only)", false, false);
	private DisplayColumn showNumPept = new DisplayColumn(numPept, "# Peptides", false, false);
	private DisplayColumn showNumUniqPept = new DisplayColumn(numUniqPept, "# Unique Peptides", false, false);
	private DisplayColumn showNumSpectra = new DisplayColumn(numSpectra, "# Spectra", false, false);
	private DisplayColumn showClusterLabel = new DisplayColumn(clusterLabel, "Cluster ID", false, false);
	
	
	private Map<Character, DisplayColumn> map;
	
	public DisplayColumns() {
		
		map = new HashMap<Character, DisplayColumn>();
		map.put(groupId, showGroupId);
		map.put(validation, showValidation);
		map.put(fasta, showFastaId);
		map.put(commonName, showCommonName);
		map.put(description, showDescription);
		map.put(molWt, showMolWt);
		map.put(pi, showPi);
		map.put(philiusAnnot, showPhiliusAnnotations);
		map.put(coverage, showCoverage);
		map.put(nsaf, showNsaf);
		map.put(yeastAbundance, showYeastCopiesPerCell);
		map.put(numPept, showNumPept);
		map.put(numUniqPept, showNumUniqPept);
		map.put(numSpectra, showNumSpectra);
		map.put(clusterLabel, showClusterLabel);
	}
	
	public List<DisplayColumn> getColumnList() {
		
		List<DisplayColumn> list = new ArrayList<DisplayColumn>();
		list.add(showGroupId);
		list.add(showValidation);
		list.add(showFastaId);
		list.add(showCommonName);
		list.add(showDescription);
		list.add(showMolWt);
		list.add(showPi);
		list.add(showPhiliusAnnotations);
		list.add(showCoverage);
		list.add(showNsaf);
		list.add(showYeastCopiesPerCell);
		list.add(showNumPept);
		list.add(showNumUniqPept);
		list.add(showNumSpectra);
		list.add(showClusterLabel);
		return list;
	}
	
	public static DisplayColumns initDisplayColumns(List<DisplayColumn> columnList) {
		DisplayColumns dColumns = new DisplayColumns();
		for(DisplayColumn col: columnList) {
			DisplayColumn myColumn = dColumns.map.get(col.getColumnCode());
			if(myColumn != null) {
				myColumn.setDisabled(col.isDisabled());
				myColumn.setSelected(col.isSelected());
			}
		}
		return dColumns;
	}
	
	public void setAllSelected() {
		
		for(DisplayColumn col: map.values()) {
			col.setSelected(true);
			col.setDisabled(false);
		}
	}
	
	public void setDisabled(char columnCode) {
		DisplayColumn col = map.get(columnCode);
		if(col != null) {
			col.setDisabled(true);
			col.setSelected(false);
		}
	}
	
	public void setNotSelected(char columnCode) {
		DisplayColumn col = map.get(columnCode);
		if(col != null) {
			col.setSelected(false);
		}
	}
	
	public void setSelected(char columnCode) {
		DisplayColumn col = map.get(columnCode);
		if(col != null) {
			col.setSelected(true);
			col.setDisabled(false);
		}
	}

	public Boolean getShowGroupId() {
		return showGroupId.isSelected();
	}

	public Boolean getShowValidation() {
		return showValidation.isSelected();
	}

	public Boolean getShowFastaId() {
		return showFastaId.isSelected();
	}

	public Boolean getShowCommonName() {
		return showCommonName.isSelected();
	}

	public Boolean getShowDescription() {
		return showDescription.isSelected();
	}

	public Boolean getShowMolWt() {
		return showMolWt.isSelected();
	}

	public Boolean getShowPi() {
		return showPi.isSelected();
	}

	public Boolean getShowPhiliusAnnotations() {
		return showPhiliusAnnotations.isSelected();
	}

	public Boolean getShowCoverage() {
		return showCoverage.isSelected();
	}

	public Boolean getShowNsaf() {
		return showNsaf.isSelected();
	}

	public Boolean getShowYeastCopiesPerCell() {
		return showYeastCopiesPerCell.isSelected();
	}

	public Boolean getShowNumPept() {
		return showNumPept.isSelected();
	}

	public Boolean getShowNumUniqPept() {
		return showNumUniqPept.isSelected();
	}

	public Boolean getShowNumSpectra() {
		return showNumSpectra.isSelected();
	}

	public Boolean getShowClusterId() {
		return showClusterLabel.isSelected();
	}

}