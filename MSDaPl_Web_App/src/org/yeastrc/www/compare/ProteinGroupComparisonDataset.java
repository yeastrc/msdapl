/**
 * ProteinGroupComparisonDataset.java
 * @author Vagisha Sharma
 * May 16, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.util.ProteinUtils;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.nr_seq.listing.ProteinListingBuilder;
import org.yeastrc.nr_seq.listing.ProteinReference;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.util.FastaDatabaseLookupUtil;
import org.yeastrc.www.misc.Pageable;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.misc.TableCell;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.project.SORT_CLASS;

/**
 * 
 */
public class ProteinGroupComparisonDataset implements Tabular, Pageable, Serializable {

    private List<? extends Dataset> datasets;
    private List<Integer> fastaDatabaseIds; // for protein name lookup
    
    // FILTERED proteins
    private List<ComparisonProteinGroup> proteinGroups;
    
    private List<ComparisonProtein> proteins;
    
    // counts BEFORE filtering
    private int[][] proteinCounts;
    private int[][] proteinGroupCounts;
    
	private float minNormalizedSpectrumCount;
    private float maxNormalizedSpectrumCount;
    
    private int rowCount = 50;
    private int currentPage = 1;
    private int pageCount = 1;
    private List<Integer> displayPageNumbers;
    int startIndex = -1;
    int endIndex = -1;
    
    private int currentGroupId = -1;  // used in the getRow method
    private Map<Integer, Integer> groupMemberCount;
    
    private String rowCssClass = "tr_even";
    
    private SORT_BY sortBy = SORT_BY.NUM_PEPT;
    private SORT_ORDER sortOrder = SORT_ORDER.DESC;
    
    private boolean proteinsInitialized = false;
    
    private String[] spectrumCountColors = null;
    private float minHeatMapSpectrumCount;
    private float maxHeatMapSpectrumCount;
    
    private DisplayColumns displayColumns; 
    
	private static final Logger log = Logger.getLogger(ProteinComparisonDataset.class.getName());
    
    
	public void setProteinsInitialized(boolean initialized) {
		this.proteinsInitialized = initialized;
	}
	
	public String[] getSpectrumCountColors() {
		return spectrumCountColors;
	}

	public void setSpectrumCountColors(String[] spectrumCountColors) {
		this.spectrumCountColors = spectrumCountColors;
	}
	
	public void setMinHeatMapSpectrumCount(float count) {
		this.minHeatMapSpectrumCount = count;
	}
	
	public float getMinHeatMapSpectrumCount() {
		return this.minHeatMapSpectrumCount;
	}

	private int getStartIndex() {
        
        if(startIndex != -1)
            return startIndex;
        
        startIndex = (this.currentPage - 1)*rowCount;
        // if this is not the first page and the protein at offset is in a indistinguishable group
        // it should have been displayed in the previous page already. Skip over it....
        if(startIndex > 0) {
            int myGrp = proteins.get(startIndex - 1).getGroupId();
            while(startIndex < this.getTotalProteinCount() && (myGrp == proteins.get(startIndex).getGroupId())) {
                startIndex++;
            }
        }
        return startIndex;
    }
    
    private int getEndIndex() {
        
        if(endIndex != -1)
            return endIndex;
        
        endIndex = Math.min((this.getStartIndex() + rowCount), this.getTotalProteinCount());
        // If the protein at the last index is in a indistinguishable protein group we want to display
        // all the members of the group
        if(endIndex < getTotalProteinCount()) {
            int myGrp = proteins.get(endIndex-1).getGroupId();
            while(endIndex < this.getTotalProteinCount() && (proteins.get(endIndex).getGroupId() == myGrp)) {
                endIndex++;
                if(endIndex >= this.getTotalProteinCount())
                    break;
            }
        }
        return endIndex;
    }

//    public void setRowCount(int count) {
//        this.rowCount = count;
//    }
    
    public ProteinGroupComparisonDataset() {
        this.datasets = new ArrayList<Dataset>();
        this.proteinGroups = new ArrayList<ComparisonProteinGroup>();
        this.proteins = new ArrayList<ComparisonProtein>();
        groupMemberCount = new HashMap<Integer, Integer>();
        this.displayPageNumbers = new ArrayList<Integer>();
        this.displayColumns = new DisplayColumns();
    }

    
    public DisplayColumns getDisplayColumns() {
		return displayColumns;
	}

	public void setDisplayColumns(DisplayColumns displayColumns) {
		this.displayColumns = displayColumns;
	}

	public List<? extends Dataset> getDatasets() {
        return datasets;
    }
	
	public boolean isAllDatasetsHaveNames() {
		for(Dataset ds: this.datasets) {
			if(ds.getDatasetName() == null || ds.getDatasetName().length() == 0) {
				return false;
			}
		}
		return true;
	}

    public List<ComparisonProteinGroup> getProteinsGroups() {
        return proteinGroups;
    }
    
    /** Added for serialization */
    public void setProteinGroups(List<ComparisonProteinGroup> groups) {
    	if(this.proteinGroups == null)
    		this.proteinGroups = new ArrayList<ComparisonProteinGroup>();
    	else
    		this.proteinGroups.clear();
    	
    	if(this.proteins == null) 
    		this.proteins = new ArrayList<ComparisonProtein>();
    	else
    		this.proteins.clear();
    	
    	if(groupMemberCount == null)
    		groupMemberCount = new HashMap<Integer, Integer>();
    	else
    		groupMemberCount.clear();
    	
    	for(ComparisonProteinGroup grp: groups) {
    		addProteinGroup(grp);
    	}
    }
    
    public void setDatasets(List<? extends Dataset> datasets) {
        this.datasets = datasets;
    }
    
    public void setDatasetOrder(List<Integer> piRunIds) {
    	
    	List<Dataset> newOrder = new ArrayList<Dataset>();
    	for(Integer piRunId: piRunIds) {
    		for(Dataset ds: datasets) {
    			if(ds.getDatasetId() == piRunId.intValue())
    				newOrder.add(ds);
    		}
    	}
    	this.datasets = newOrder;
    }
    
    public List<Integer> getDatasetOrder() {
    	List<Integer> piRunIds = new ArrayList<Integer>();
    	for(Dataset ds: datasets)
    		piRunIds.add(ds.getDatasetId());
    	return piRunIds;
    }
    
    public void addProteinGroup(ComparisonProteinGroup proteinGroup) {
        this.proteinGroups.add(proteinGroup);
        for(ComparisonProtein protein: proteinGroup.getProteins())
            proteins.add(protein);
        groupMemberCount.put(proteinGroup.getGroupId(), proteinGroup.getProteins().size());
    }
    
    public int getDatasetCount() {
        return datasets.size();
    }
    
    public int getTotalProteinCount() {
        return proteins.size();
    }
    
    public int getTotalProteinGroupCount() {
        return proteinGroups.size();
    }
    
//    public void setMinNormalizedSpectrumCount(int minNormalizedSpectrumCount) {
//        this.minNormalizedSpectrumCount = minNormalizedSpectrumCount;
//    }
//
//    public void setMaxNormalizedSpectrumCount(int maxNormalizedSpectrumCount) {
//        this.maxNormalizedSpectrumCount = maxNormalizedSpectrumCount;
//    }
    
    public void initSummary() {
        initProteinCounts();
        initProteinGroupCounts();
        calculateSpectrumCountNormalization();
        getMinMaxSpectrumCounts();
    }
    
    private void getMinMaxSpectrumCounts() {
    	
    	float minCount = Float.MAX_VALUE;
		float maxCount = 1.0f;
		
		float maxHeatMapCount = -1.0f;
		float minHeatMapCount = this.minHeatMapSpectrumCount;
		
    	if(proteinsInitialized) {  // this means spectrum counts for all proteins have been initialized
    		for(ComparisonProtein protein: this.proteins) {
    			for(Dataset dataset: datasets) {
    				DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
    				if(dpi != null && dpi.isPresent()) {
    					minCount = Math.min(minCount, dpi.getNormalizedSpectrumCount());
    					maxCount = Math.max(maxCount, dpi.getNormalizedSpectrumCount());
    					
    					// TODO This is ugly
    					maxHeatMapCount = Math.max(maxHeatMapCount, dpi.getHeatMapSpectrumCount());
    					minHeatMapCount = Math.min(minHeatMapCount, dpi.getHeatMapSpectrumCount());
    				}
    			}
    		}
    	}
    	else {
    		
    		for(Dataset dataset: datasets) {
    			minCount = Math.min(minCount, dataset.getNormMinProteinSpectrumCount());
    			maxCount = Math.max(maxCount, dataset.getNormMaxProteinSpectrumCount());
    		}
    	}
    	this.minNormalizedSpectrumCount = minCount;
		this.maxNormalizedSpectrumCount = maxCount;
		
		this.maxHeatMapSpectrumCount = maxHeatMapCount;
		this.minHeatMapSpectrumCount = minHeatMapCount;
    }

    private void calculateSpectrumCountNormalization() {
        Dataset maxDataset = null;
        for(Dataset dataset: datasets) {
            if(maxDataset == null || maxDataset.getSpectrumCount() < dataset.getSpectrumCount())
                maxDataset = dataset;
        }
        for(Dataset dataset: datasets) {
            float normFactor = (float)maxDataset.getSpectrumCount() / (float)dataset.getSpectrumCount();
            dataset.setSpectrumCountNormalizationFactor(normFactor);
        }
    }

    private void initProteinCounts() {
        
        proteinCounts = new int[datasets.size()][datasets.size()];
        for(int i = 0; i < datasets.size(); i++) {
            for(int j = 0; j < datasets.size(); j++)
                proteinCounts[i][j] = 0;
        }
        
        for(ComparisonProtein protein: proteins) {
            
            for(int i = 0; i < datasets.size(); i++) {
                
                Dataset dsi = datasets.get(i);
                if(protein.isInDataset(dsi)) {
                
                    proteinCounts[i][i]++;
                    
                    for(int j = i+1; j < datasets.size(); j++) {

                        Dataset dsj = datasets.get(j);
                        if(protein.isInDataset(dsj)) {
                            proteinCounts[i][j]++;
                            proteinCounts[j][i]++;
                        }
                    }
                }
            }
        }
    }
    
    private void initProteinGroupCounts() {
        
        proteinGroupCounts = new int[datasets.size()][datasets.size()];
        for(int i = 0; i < datasets.size(); i++) {
            for(int j = 0; j < datasets.size(); j++)
                proteinGroupCounts[i][j] = 0;
        }
        
        
        for(ComparisonProteinGroup proteinGroup: proteinGroups) {
            
            for(int i = 0; i < datasets.size(); i++) {
                
                Dataset dsi = datasets.get(i);
                
                if(proteinGroup.isInDataset(dsi)) {
                	proteinGroupCounts[i][i]++;
                	
                	for(int j = i+1; j < datasets.size(); j++) {

                        Dataset dsj = datasets.get(j);
                        if(proteinGroup.isInDataset(dsj)) {
                            proteinGroupCounts[i][j]++;
                            proteinGroupCounts[j][i]++;
                        }
                    }
                }
            }
        }
    }
    
    public int getCommonProteinCount(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex1][datasetIndex2];
    }
    
    public int getCommonProteinGroupCount(int datasetIndex1, int datasetIndex2) {
        
        if(proteinGroupCounts == null) {
            initProteinGroupCounts();
        }
        return proteinGroupCounts[datasetIndex1][datasetIndex2];
    }
    

    public int getProteinGroupCount(int datasetIndex) {
        
        if(proteinGroupCounts == null) {
            initProteinGroupCounts();
        }
        return proteinGroupCounts[datasetIndex][datasetIndex];
    }

    public int getProteinCount(int datasetIndex) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        return proteinCounts[datasetIndex][datasetIndex];
    }

    /**
     * Fraction of dataset_1 proteins that were also found in dataset_2
     * @param datasetIndex1
     * @param datasetIndex2
     * @return
     */
    public float getCommonProteinsPerc(int datasetIndex1, int datasetIndex2) {
        
        if(proteinCounts == null) {
            initProteinCounts();
        }
        
        int ds1Count = proteinCounts[datasetIndex1][datasetIndex1];
        int commonCount = proteinCounts[datasetIndex1][datasetIndex2];
        
        if(ds1Count <= 0)
            return 0;
        return calculatePercent(commonCount, ds1Count);
    }
    
    public float getCommonProteinGroupsPerc(int datasetIndex1, int datasetIndex2) {
        
        if(proteinGroupCounts == null) {
            initProteinGroupCounts();
        }
        
        int ds1Count = proteinGroupCounts[datasetIndex1][datasetIndex1];
        int commonCount = proteinGroupCounts[datasetIndex1][datasetIndex2];
        
        if(ds1Count <= 0)
            return 0;
        return calculatePercent(commonCount, ds1Count);
    }

    
    private static float calculatePercent(int num1, int num2) {
        return (float) (Math.round(((float)(num1*100.0)/(float)num2) * 10.0)/10.0);
    }

    @Override
    public int columnCount() {
        return datasets.size() + 6 + datasets.size()*4;
    }

    @Override
    public TableRow getRow(int index) {
        
        ComparisonProtein protein = proteins.get(index + this.getStartIndex());
        
        TableRow row = new TableRow();
        
        // Present / not present in each dataset
        int dsIndex = 0;
        
        if(displayColumns.isShowPresent()) {
        	for(Dataset dataset: datasets) {
        		DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
        		TableCell cell = new TableCell();
        		cell.setId(String.valueOf(dsIndex));
        		dsIndex++;

        		if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein
        			cell.setClassName("prot-not-found");
        		}
        		else {
        			String className = "prot-found";
        			if(dpi.isParsimonious())
        				className += "  prot-parsim centered ";
        			if(dpi.isGrouped()) {
        				className += " faded prot-group clickable ";
        				cell.setName(String.valueOf(protein.getNrseqId()));
        			}

        			cell.setClassName(className);

        			if(dpi.isParsimonious()) {
        				cell.setData("*");
        			}
        		}
        		row.addCell(cell);
        	}
        }
        
        boolean newRow = false;
        if(currentGroupId == -1 || currentGroupId != protein.getGroupId()) {
            currentGroupId = protein.getGroupId();
            
            newRow = true;
//            TableCell groupId = new TableCell(String.valueOf(currentGroupId));
//            groupId.setRowSpan(groupMemberCount.get(currentGroupId));
//            row.addCell(groupId);
            
            rowCssClass = rowCssClass.equals("tr_even") ? "tr_odd" : "tr_even";
            
            row.setStyleClass(rowCssClass+" top_row ");
        }
        else
            row.setStyleClass(rowCssClass);
        
        List<Integer> pinferIds = new ArrayList<Integer>(this.getDatasetCount());
        for(Dataset ds: this.getDatasets()) 
        	pinferIds.add(ds.getDatasetId());
        String pinferIdsCommaSeparated = StringUtils.makeCommaSeparated(pinferIds);
        
        // Protein name
        if(displayColumns.isShowFastaId()) {
        	TableCell protName = new TableCell("<A name='"+(index + this.getStartIndex())+"'></A> "
        			+getAccessionContents(protein, pinferIdsCommaSeparated));
        	//TableCell protName = new TableCell(getAccessionContents(protein));
        	protName.setClassName("prot_accession");
        	row.addCell(protName);
        }
        
        if(displayColumns.isShowCommonName()) {
        	// Protein common name
        	TableCell protCommonName = new TableCell(getCommonNameContents(protein, pinferIdsCommaSeparated));
        	row.addCell(protCommonName);
        }
        
        if(displayColumns.isShowMolWt()) {
        	// Protein molecular wt.
        	TableCell molWt = new TableCell();
        	molWt.setData(protein.getMolecularWeight()+"");
        	row.addCell(molWt);
        }
        
        if(displayColumns.isShowPi()) {
        	// Protein pI
        	TableCell pi = new TableCell();
        	pi.setData(protein.getPi()+"");
        	row.addCell(pi);
        }
        
        if(displayColumns.isShowDescription()) {
        	// Protein description
        	TableCell protDescr = new TableCell();
        	protDescr.setClassName("prot_descr");
        	protDescr.setData(getDescriptionContents(protein));
        	row.addCell(protDescr);
        }
        
        
        // These values are identical for all members of a protein group
        if(newRow) {
        	
        	// Peptide sequence count
        	if(displayColumns.isShowTotalSeq()) {
        		TableCell peptCount = new TableCell(String.valueOf(protein.getTotalPeptideSeqCount()));
        		peptCount.setRowSpan(groupMemberCount.get(currentGroupId));
        		peptCount.setClassName("pept_count clickable underline");
        		peptCount.setId(String.valueOf(protein.getNrseqId()));
        		row.addCell(peptCount);
        	}
        }
        
        // Sequence counts, ion counts, Spectrum counts in each dataset
        for(Dataset dataset: datasets) {
        	DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);

        	if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein

        		// These values are identical for all members of a protein group
        		if(newRow) {
        			TableCell cell = new TableCell();

        			if(displayColumns.isShowNumSeq()) {
        				cell.setClassName("prot-not-found"); // # seq
        				cell.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(cell);
        			}

        			if(displayColumns.isShowNumIons()) {
        				cell = new TableCell();
        				cell.setClassName("prot-not-found"); // # ions
        				cell.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(cell);
        			}

        			if(displayColumns.isShowNumUniqIons()) {
        				cell = new TableCell();
        				cell.setClassName("prot-not-found"); // # unique ions
        				cell.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(cell);
        			}

        			if(displayColumns.isShowSpectrumCount()) {
        				cell = new TableCell();
        				cell.setClassName("prot-not-found"); // SC
        				cell.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(cell);
        			}
        		}
        		// Are we printing NSAF?
        		if(displayColumns.isShowNsaf()) {
        			TableCell cell = new TableCell();
        			cell.setClassName("prot-not-found"); // NSAF
        			row.addCell(cell);
        		}

        	}
        	else {

        		// These values are identical for all members of a protein group
        		if(newRow) {
        			// Peptide sequence count
        			if(displayColumns.isShowNumSeq()) {
        				String contents = dpi.getSequenceCount()+"";
        				TableCell seqCount = new TableCell(contents);
        				seqCount.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(seqCount);
        			}

        			// Peptide ion count
        			if(displayColumns.isShowNumIons()) {
        				String contents = dpi.getIonCount()+"";
        				TableCell ionCount = new TableCell(contents);
        				ionCount.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(ionCount);
        			}

        			// Unique Peptide ion count
        			if(displayColumns.isShowNumUniqIons()) {
        				String contents = dpi.getUniqueIonCount()+"";
        				TableCell uIonCount = new TableCell(contents);
        				uIonCount.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(uIonCount);
        			}

        			if(displayColumns.isShowSpectrumCount()) {
        				TableCell cell = new TableCell();
        				cell.setClassName("prot-found");
        				cell.setData(dpi.getSpectrumCount()+"("+dpi.getNormalizedSpectrumCountRounded()+")");
        				cell.setTextColor("#FFFFFF");
        				float scaledCount = getScaledSpectrumCount(dpi.getNormalizedSpectrumCount());
        				
        				String bgColor = null;
        				if(this.spectrumCountColors == null) {
        					bgColor = getScaledColor(scaledCount);
        					cell.setBackgroundColor(bgColor);
        					//cell.setTextColor("#000000");
        					
        				}
        				else {
        					bgColor = getHeatMapColor(dpi.getHeatMapSpectrumCount());
        					cell.setBackgroundColor(bgColor);
        					if((bgColor.charAt(1) == 'F' || bgColor.charAt(1) == 'E') &&
        						(bgColor.charAt(3) == 'F' || bgColor.charAt(3) == 'E')) {
        						cell.setTextColor("#000000");
        					}
        				}
        				if(bgColor != null) {
        					
        				}
        				cell.setRowSpan(groupMemberCount.get(currentGroupId));
        				row.addCell(cell);
        			}
        		}
        		// Are we printing NSAF?
        		if(displayColumns.isShowNsaf()) {
        			TableCell cell = new TableCell(dpi.getNsafFormatted());
        			row.addCell(cell);
        		}
        	}
        		
        }
        return row;
    }
    
    private String getDescriptionContents(ComparisonProtein protein) {
		
    	String shortId = "short_desc_"+protein.getNrseqId();
    	String fullId = "full_desc_"+protein.getNrseqId();
    	
		String fullContents = "";
        fullContents += "<span style=\"display:none;\" class=\"full_description\" id=\""+fullId+"\">";
        String shortContents = "";
        shortContents += "<span class=\"short_description\" id=\""+shortId+"\">";
        List<ProteinReference> allReferences;
        // List<ProteinReference> uniqueDbRefs;
        ProteinReference oneRef;
		try {
			allReferences = protein.getProteinListing().getDescriptionReferences();
			oneRef = protein.getOneDescriptionReference();
		} catch (SQLException e) {
			log.error("Error getting description", e);
			return "ERROR";
		}
        for(ProteinReference ref: allReferences) {
        	String dbName = null;
        	try {dbName = ref.getDatabaseName();}
        	catch(SQLException e){log.error("Error getting database name"); dbName="ERROR";}
        	fullContents += "<span style=\"color:#000080;\"<b>";//["+dbName+"]</b></span>&nbsp;&nbsp;"+ref.getDescription();
        	if(ref.getHasExternalLink()) {
        		fullContents += "<a href=\""+ref.getUrl()+"\" style=\"font-size:8pt;\" target=\"External Link\">["+dbName+"]</a>";
        	}
        	else {
        		fullContents += "["+dbName+"]";
        	}
        	fullContents += "</b></span>&nbsp;&nbsp;"+ref.getDescriptionEscaped();
        	fullContents += "<br>";
        }
        if(allReferences.size() > 1) { // uniqueDbRefs.size()) {
        	fullContents += "<b><span class=\"clickable\" onclick=\"hideAllDescriptionsForProtein("+
        	protein.getNrseqId()+")\">[-]</span></b>";
        }
        if(oneRef != null)
        	shortContents += oneRef.getShortDescriptionEscaped();
        shortContents += "<br>";
        	
        if(allReferences.size() > 1) {
        	shortContents += "<b><span class=\"clickable\" onclick=\"showAllDescriptionsForProtein("+
        	protein.getNrseqId()+")\">[+]</span></b>";
        }
        
        fullContents += "</span>";
        shortContents += "</span>";
        return fullContents+"\n"+shortContents;
	}

	private String getCommonNameContents(ComparisonProtein protein, String pinferIdsCommaSeparated) {
		
		String contents = "<a href=\"viewProteinDetails.do?id="+protein.getNrseqId()+"&pinferIds="+pinferIdsCommaSeparated+"\">";
        contents += "<span>";
        List<ProteinReference> commonRefs = protein.getProteinListing().getCommonReferences();
        for(ProteinReference ref: commonRefs) {
        	contents += ref.getCommonReference().getName()+"<br>";
        }
        contents += "</span></a>";
		return contents;
	}
	
	private String getAccessionContents(ComparisonProtein protein, String pinferIdsCommaSeparated) {
		
		String fullContents = "<a href=\"viewProteinDetails.do?id="+protein.getNrseqId()+"&pinferIds="+pinferIdsCommaSeparated+"\">";
        fullContents += "<span";
    	fullContents += " style=\"display:none;\" class=\"full_name\">";
    	
        String shortContents = "<a href=\"viewProteinDetails.do?id="+protein.getNrseqId()+"&pinferIds="+pinferIdsCommaSeparated+"\">";
        shortContents += "<span";
        shortContents += " class=\"short_name\">";
        List<ProteinReference> references;
        references = protein.getProteinListing().getFastaReferences();
        
        for(ProteinReference ref: references) {
        	fullContents += ref.getAccession()+"<br>";
        	shortContents += ref.getShortAccession()+"<br>";
        }
        fullContents += "</span></a>";
        shortContents += "</span></a>";
        return fullContents+"\n"+shortContents;
	}
	
    public float getScaledSpectrumCount(float count) {
        return ((count - minNormalizedSpectrumCount)/(maxNormalizedSpectrumCount - minNormalizedSpectrumCount))*100.0f;
    }
    
    public String getHeatMapColor(float heatMapSpectrumCount) {
    	int numbins = spectrumCountColors.length;
    	float range = maxHeatMapSpectrumCount - minHeatMapSpectrumCount;
    	float scaledCount = ((heatMapSpectrumCount - minHeatMapSpectrumCount)/range)*100.0f;
    	int bin = (int)Math.ceil((((double) numbins / 100.0) * scaledCount));
		if(bin == 0) bin = 1;
    	return spectrumCountColors[bin-1];
    }
    
    public  String getScaledColor(float scaledSpectrumCount) {
    	
    	if(this.spectrumCountColors != null) {
    		int bin = (int)Math.ceil((((double) spectrumCountColors.length / 100.0) * scaledSpectrumCount));
    		if(bin == 0) bin = 1;
    		return spectrumCountColors[bin-1];
    	}
    	else {
    		int rounded = (int)Math.ceil(scaledSpectrumCount);
    		int green = 255;
    		green -= 255.0/100.0 * rounded;
    		int red = 0;
    		red  += 255.0/100.0 * rounded;
    		return "#"+hexValue(red, green, 0);
    	}
    }
    
    public static String hexValue(int r, int g, int b) {
        String red = Integer.toHexString(r);
        if(red.length() == 1)
            red = "0"+red;
        String green = Integer.toHexString(g);
        if(green.length() == 1)
            green = "0"+green;
        String blue = Integer.toHexString(b);
        if(blue.length() == 1)
            blue = "0"+blue;
        return red+green+blue;
    }

    @Override
    public int rowCount() {
//        int cnt = Math.min(rowCount, this.getTotalProteinCount() - this.getOffset());
        int start = getStartIndex();
        int end = getEndIndex();
        return end - start;
    }

    @Override
    public List<TableHeader> tableHeaders() {
        List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
        TableHeader header = null;
        
        boolean allDatasetsHaveNames = this.isAllDatasetsHaveNames();
        
        if(displayColumns.isShowPresent()) {
        	for(Dataset dataset: datasets) {
        		
        		header = new TableHeader();
        		header.setWidth(2);
        		header.setRowspan(2);
        		header.setSortable(false);
        		
        		String contents = "";
        		
        		if(allDatasetsHaveNames) {
        			//header.addStyle("height", "70px");
        			contents += "<span class=\"rotated_text font_9 dsname\">"+dataset.getDatasetName()+"</span>";
        			contents += "<span class=\"font_9 dsid\" style=\"display:none;\">"+dataset.getDatasetId()+"</span>";
        		}
        		else {
        			contents += "<span class=\"font_9\">"+dataset.getDatasetId()+"</span>";
        		}
        		
        		header.setHeaderName(contents);
        		headers.add(header);
        	}
        }
        
//        header = new TableHeader("GID");
//        header.setWidth(5);
//        header.setSortable(false);
//        headers.add(header);
        
        
        if(displayColumns.isShowFastaId()) {
        	header = new TableHeader("Fasta ID");
        	//header.setWidth(8);
        	header.setRowspan(2);
        	header.setSortable(true);
        	header.setSortClass(SORT_CLASS.SORT_ALPHA);
        	header.setDefaultSortOrder(SORT_ORDER.ASC);
        	header.setHeaderId(SORT_BY.ACCESSION.name());
        	if(this.sortBy == SORT_BY.ACCESSION) {
        		header.setSorted(true);
        		header.setSortOrder(this.sortOrder);
        	}
        	headers.add(header);
        }
        
        if(displayColumns.isShowCommonName()) {
        	header = new TableHeader("Common Name");
        	//header.setWidth(8);
        	header.setSortable(false);
        	header.setRowspan(2);
        	headers.add(header);
        }
        
        if(displayColumns.isShowMolWt()) {
        	header = new TableHeader("Mol. Wt.");
        	//header.setWidth(8);
        	header.setRowspan(2);
        	header.setSortable(true);
        	header.setSortClass(SORT_CLASS.SORT_FLOAT);
        	header.setDefaultSortOrder(SORT_ORDER.ASC);
        	header.setHeaderId(SORT_BY.MOL_WT.name());
        	if(this.sortBy == SORT_BY.MOL_WT) {
        		header.setSorted(true);
        		header.setSortOrder(this.sortOrder);
        	}
        	headers.add(header);
        }
        
        if(displayColumns.isShowPi()) {
        	header = new TableHeader("pI");
        	//header.setWidth(5);
        	header.setRowspan(2);
        	header.setSortable(true);
        	header.setSortClass(SORT_CLASS.SORT_FLOAT);
        	header.setDefaultSortOrder(SORT_ORDER.ASC);
        	header.setHeaderId(SORT_BY.PI.name());
        	if(this.sortBy == SORT_BY.PI) {
        		header.setSorted(true);
        		header.setSortOrder(this.sortOrder);
        	}
        	headers.add(header);
        }
        
        if(displayColumns.isShowDescription()) {
        	header = new TableHeader("Description");
        	int width = Math.max(20, 100 - (15 + datasets.size()*2));
        	header.setWidth(width);
        	header.setSortable(false);
        	header.setRowspan(2);
        	headers.add(header);
        }
        
        if(displayColumns.isShowTotalSeq()) {
        	// Peptide sequence count
        	header = new TableHeader("#Seq.");
        	//header.setWidth(5);
        	header.setRowspan(2);
        	header.setStyleClass("small_font");
        	header.setSortable(true);
        	header.setSortClass(SORT_CLASS.SORT_INT);
        	header.setDefaultSortOrder(SORT_ORDER.DESC);
        	header.setHeaderId(SORT_BY.NUM_PEPT.name());
        	if(this.sortBy == SORT_BY.NUM_PEPT) {
        		header.setSorted(true);
        		header.setSortOrder(this.sortOrder);
        	}
        	headers.add(header);
        }
        
        // sequence counts, ion counts and spectrum counts
        int colspan = 5;
        if(!displayColumns.isShowNumSeq()) colspan--;
        if(!displayColumns.isShowNumIons()) colspan--;
        if(!displayColumns.isShowNumUniqIons()) colspan--;
        if(!displayColumns.isShowSpectrumCount()) colspan--;
        if(!displayColumns.isShowNsaf()) colspan--;
        
        if(colspan > 0) {
        	for(Dataset dataset: datasets) {
        		
        		header = new TableHeader();
        		header.setColspan(colspan);
        		header.setSortable(false);
        		
        		String contents = "";
        		
        		if(allDatasetsHaveNames) {
        			if(colspan == 1) {
        				contents += "<span class=\"rotated_text font_9 dsname\">"+dataset.getDatasetName()+"</span>";
        				contents += "<span class=\"font_9 dsid\" style=\"display:none;\">"+dataset.getDatasetId()+"</span>";
        			}
        			else {
        				contents += "<span class=\"font_9 dsname\">"+dataset.getDatasetName()+"</span>";
        				contents += "<span class=\"font_9 dsid\" style=\"display:none;\">"+dataset.getDatasetId()+"</span>";
        			}
        		}
        		else {
        			contents += "<span class=\"font_9\">"+dataset.getDatasetId()+"</span>";
        		}
        		
        		header.setHeaderName(contents);
        		headers.add(header);
        		
        	}

        	for(Dataset dataset: datasets) {
        		
        		if(displayColumns.isShowNumSeq()) {
        			header = new TableHeader("S");
        			header.setRowIndex(2);
        			header.setStyleClass("small_font");
        			header.setWidth(2);
        			header.setSortable(false);
        			headers.add(header);
        		}

        		if(displayColumns.isShowNumIons()) {
        			header = new TableHeader("I");
        			header.setRowIndex(2);
        			header.setWidth(2);
        			header.setStyleClass("small_font");
        			header.setSortable(false);
        			headers.add(header);
        		}

        		if(displayColumns.isShowNumUniqIons()) {
        			header = new TableHeader("U.I");
        			header.setRowIndex(2);
        			header.setWidth(2);
        			header.setStyleClass("small_font");
        			header.setSortable(false);
        			headers.add(header);
        		}

        		if(displayColumns.isShowSpectrumCount()) {
        			header = new TableHeader("SC");
        			header.setRowIndex(2);
        			header.setWidth(2);
        			header.setStyleClass("small_font");
        			header.setSortable(false);
        			headers.add(header);
        		}
        		
        		if(displayColumns.isShowNsaf()) {
        			header = new TableHeader("N");
        			header.setRowIndex(2);
        			header.setWidth(2);
        			header.setStyleClass("small_font");
        			header.setSortable(false);
        			headers.add(header);
        		}

        	}
        }
        return headers;
    }

    @Override
    public void tabulate() {
    	if(!this.proteinsInitialized)
    		initializeInfo(this.getStartIndex(), this.getEndIndex());
    }
    
    private void initializeInfo(int startIndex, int endIndex) {
        
        for(int i = startIndex; i < endIndex; i++) {
            ComparisonProtein protein = proteins.get(i);
            initializeProteinInfo(protein);
        }
    }

    public void initializeProteinInfo(ComparisonProtein protein) {
        
        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        IdPickerProteinBaseDAO idpProtDao = ProteinferDAOFactory.instance().getIdPickerProteinBaseDao();
        ProteinProphetProteinDAO ppProtDao = ProteinferDAOFactory.instance().getProteinProphetProteinDao();
        
        // Get the common name and description
        ProteinListing listing = ProteinListingBuilder.getInstance().build(protein.getNrseqId(), getFastaDatabaseIds());
        protein.setProteinListing(listing);

        // This query takes a long time so if these columns are not being displayed, don't 
        // get this information.
        if(displayColumns.isShowPresent()) {
        	// Get the group information for the different datasets
        	for(DatasetProteinInformation dpi: protein.getDatasetInformation()) {
        		if(dpi.getDatasetSource() == DatasetSource.PROTINFER) {
        			boolean grouped = idpProtDao.isNrseqProteinGrouped(dpi.getDatasetId(), protein.getNrseqId());
        			dpi.setGrouped(grouped);
        		}
        		else if(dpi.getDatasetSource() == DatasetSource.PROTEIN_PROPHET) {
        			boolean grouped = ppProtDao.isNrseqProteinGrouped(dpi.getDatasetId(), protein.getNrseqId());
        			dpi.setGrouped(grouped);
        		}
        		// TODO for DTASelect
        	}
        }
        
        // Get the spectrum count information for the protein in the different datasets
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(protein.getNrseqId());
        for(DatasetProteinInformation dpi: protein.getDatasetInformation()) {
            if(dpi.getDatasetSource() != DatasetSource.DTA_SELECT) {
                List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dpi.getDatasetId(), nrseqIds);
                if(piProteinIds.size() == 1) {
                    ProteinferProtein prot = protDao.loadProtein(piProteinIds.get(0));
                    dpi.setSequenceCount(prot.getSequenceCount());
                    //dpi.setUniqueSequenceCount(prot.getUniqueSequenceCount());
					dpi.setIonCount(prot.getIonCount());
					dpi.setUniqueIonCount(prot.getUniqueIonCount());
                    dpi.setSpectrumCount(prot.getSpectrumCount());
                }
                else {
                    log.error("Number of proteinIds for nrseqId: "+protein.getNrseqId()+
                            " is "+piProteinIds.size()+"; Expected only one.");
                }
            }
        }
        
        // Get the NSAF information for the protein in the different datasets
        // NSAF is available only for ProteinInference proteins
        for(DatasetProteinInformation dpi: protein.getDatasetInformation()) {
            if(dpi.getDatasetSource() == DatasetSource.PROTINFER) {
                List<Integer> piProteinIds = idpProtDao.getProteinIdsForNrseqIds(dpi.getDatasetId(), nrseqIds);
                if(piProteinIds.size() == 1) {
                    IdPickerProteinBase prot = idpProtDao.loadProtein(piProteinIds.get(0));
                    dpi.setNsafFormatted(prot.getNsafFormatted());
                }
            }
        }
        
        // get the protein properties
        String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
        protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
        protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
    }
    
    
    private List<Integer> getFastaDatabaseIds() {
    	if(this.fastaDatabaseIds == null) {
    		fastaDatabaseIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(this.datasets);
    	}
    	return fastaDatabaseIds;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
        ResultsPager pager = ResultsPager.instance();
        this.pageCount = pager.getPageCount(this.proteins.size(), rowCount);
        this.displayPageNumbers = pager.getPageList(this.proteins.size(), currentPage, rowCount);
    }
    
    public void setRowCount(int rowCount) {
    	this.rowCount = rowCount;
    }
    
    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public List<Integer> getDisplayPageNumbers() {
        return this.displayPageNumbers;
    }

    @Override
    public int getLastPage() {
        return this.pageCount;
    }

    @Override
    public int getPageCount() {
        return this.pageCount;
    }
    
    @Override
    public void setDisplayPageNumbers(List<Integer> pageList) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLastPage(int pageCount) {
        throw new UnsupportedOperationException();
    }
    
    @Override
	public int getNumPerPage() {
		return rowCount;
	}

	@Override
	public void setNumPerPage(int num) {
		if(num > 0)
			this.rowCount = num;
	}
    
    public SORT_BY getSortBy() {
        return sortBy;
    }

    public void setSortBy(SORT_BY sortBy) {
        this.sortBy = sortBy;
    }

    public SORT_ORDER getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    private void writeObject(ObjectOutputStream out) throws IOException {
    	
    	out.defaultWriteObject();
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    	
    	 this.datasets = new ArrayList<Dataset>();
         this.proteinGroups = new ArrayList<ComparisonProteinGroup>();
         this.proteins = new ArrayList<ComparisonProtein>();
         this.groupMemberCount = new HashMap<Integer, Integer>();
         this.displayPageNumbers = new ArrayList<Integer>();
         
         this.startIndex = -1;
         this.endIndex = -1;
         
         this.currentGroupId = -1;
         this.rowCssClass = "tr_even";
         
         this.sortBy = SORT_BY.NUM_PEPT;
         this.sortOrder = SORT_ORDER.DESC;
    	
         // deserialize
         in.defaultReadObject();
    	
    	this.setRowCount(rowCount);
    	this.setCurrentPage(currentPage); // this will re-set the display page numbers
    									  // based on row count and number of proteins.
    	
    	initSummary(); 
    }
}
