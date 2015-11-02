/**
 * ComparisonDataset.java
 * @author Vagisha Sharma
 * Apr 11, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
public class ProteinComparisonDataset implements Tabular, Pageable, Serializable {

	private List<? extends Dataset> datasets;
	private List<Integer> fastaDatabaseIds; // for protein name lookup

	// FILTERED proteins
	private List<ComparisonProtein> proteins;

	// counts BEFORE filtering
	private int[][] proteinCounts;
	private int totalProteinCount;

	private float minNormalizedSpectrumCount;
	private float maxNormalizedSpectrumCount;

	private int rowCount = 50;
	private int currentPage = 1;
	private int pageCount = 1;
	private List<Integer> displayPageNumbers;

	private SORT_BY sortBy = SORT_BY.NUM_PEPT;
	private SORT_ORDER sortOrder = SORT_ORDER.DESC;

	
	private boolean proteinsInitialized = false;
    
    private String[] spectrumCountColors = null;
    private float minHeatMapSpectrumCount;
    private float maxHeatMapSpectrumCount;
    
    private DisplayColumns displayColumns; 
    
	private static final Logger log = Logger.getLogger(ProteinComparisonDataset.class.getName());


	public ProteinComparisonDataset() {
		this.datasets = new ArrayList<Dataset>();
		this.proteins = new ArrayList<ComparisonProtein>();
		this.displayPageNumbers = new ArrayList<Integer>();
		this.displayColumns = new DisplayColumns();
	}

	public DisplayColumns getDisplayColumns() {
		return displayColumns;
	}

	public void setDisplayColumns(DisplayColumns displayColumns) {
		this.displayColumns = displayColumns;
	}
	
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
	
	private int  getOffset() {
		return (this.currentPage - 1)*rowCount;
	}

	public void setRowCount(int count) {
		this.rowCount = count;
	}

	public boolean isAllDatasetsHaveNames() {
		for(Dataset ds: this.datasets) {
			if(ds.getDatasetName() == null || ds.getDatasetName().length() == 0) {
				return false;
			}
		}
		return true;
	}

	public List<? extends Dataset> getDatasets() {
		return datasets;
	}

	public List<ComparisonProtein> getProteins() {
		return proteins;
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

	public void addProtein(ComparisonProtein protein) {
		this.proteins.add(protein);
	}

	public int getDatasetCount() {
		return datasets.size();
	}

	public int getTotalProteinCount() {
		return totalProteinCount;
	}

	public int getFilteredProteinCount() {
		return proteins.size();
	}

	public void setMinNormalizedSpectrumCount(int minNormalizedSpectrumCount) {
		this.minNormalizedSpectrumCount = minNormalizedSpectrumCount;
	}

	public void setMaxNormalizedSpectrumCount(int maxNormalizedSpectrumCount) {
		this.maxNormalizedSpectrumCount = maxNormalizedSpectrumCount;
	}

	public void initSummary() {
		initProteinCounts();
		this.totalProteinCount = proteins.size();
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

	public int getProteinCount(int datasetIndex) {

		if(proteinCounts == null) {
			initProteinCounts();
		}
		return proteinCounts[datasetIndex][datasetIndex];
	}

	public int getCommonProteinCount(int datasetIndex1, int datasetIndex2) {

		if(proteinCounts == null) {
			initProteinCounts();
		}
		return proteinCounts[datasetIndex1][datasetIndex2];
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

	private static float calculatePercent(int num1, int num2) {
		return (float) (Math.round(((float)(num1*100.0)/(float)num2) * 10.0)/10.0);
	}

	@Override
	public int columnCount() {
		return datasets.size() + 6 + datasets.size()*4;
	}

	@Override
	public TableRow getRow(int index) {

		ComparisonProtein protein = proteins.get(index + this.getOffset());

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

		List<Integer> pinferIds = new ArrayList<Integer>(this.getDatasetCount());
        for(Dataset ds: this.getDatasets()) 
        	pinferIds.add(ds.getDatasetId());
        String pinferIdsCommaSeparated = StringUtils.makeCommaSeparated(pinferIds);
        
		// Protein accession
		if(displayColumns.isShowFastaId()) {
			TableCell protName = new TableCell("<A name='"+(index + this.getOffset())+"'></A> "
					+getAccessionContents(protein, pinferIdsCommaSeparated));
			//protName.setHyperlink("viewProtein.do?id="+protein.getNrseqId());
			protName.setClassName("prot_accession");
			row.addCell(protName);
		}

		// Protein common name
		if(displayColumns.isShowCommonName()) {
			TableCell protCommonName = new TableCell(getCommonNameContents(protein, pinferIdsCommaSeparated));
			row.addCell(protCommonName);
		}

		// Protein molecular wt.
		if(displayColumns.isShowMolWt()) {
			TableCell molWt = new TableCell();
			molWt.setData(protein.getMolecularWeight()+"");
			row.addCell(molWt);
		}

		// Protein pI
		if(displayColumns.isShowPi()) {
			TableCell pi = new TableCell();
			pi.setData(protein.getPi()+"");
			row.addCell(pi);
		}

		// Protein description
		if(displayColumns.isShowDescription()) {
			TableCell protDescr = new TableCell();
			protDescr.setClassName("prot_descr");
			protDescr.setData(getDescriptionContents(protein));
			row.addCell(protDescr);
		}

		// Peptide count
		if(displayColumns.isShowTotalSeq()) {
			TableCell peptCount = new TableCell(String.valueOf(protein.getTotalPeptideSeqCount()));
			peptCount.setClassName("pept_count clickable underline");
			peptCount.setId(String.valueOf(protein.getNrseqId()));
			row.addCell(peptCount);
		}

		// Spectrum counts in each dataset
		for(Dataset dataset: datasets) {
			DatasetProteinInformation dpi = protein.getDatasetProteinInformation(dataset);
			

			if(dpi == null || !dpi.isPresent()) { // dataset does not contain this protein
				TableCell cell = new TableCell();
				if(displayColumns.isShowNumSeq()) {
					cell.setClassName("prot-not-found"); // # seq
					row.addCell(cell);
				}
    			
				if(displayColumns.isShowNumIons()) {
					cell = new TableCell();
					cell.setClassName("prot-not-found"); // # ions
					row.addCell(cell);
				}
    			
				if(displayColumns.isShowNumUniqIons()) {
					cell = new TableCell();
					cell.setClassName("prot-not-found"); // # unique ions
					row.addCell(cell);
				}
    			
				if(displayColumns.isShowSpectrumCount()) {
					cell = new TableCell();
					cell.setClassName("prot-not-found"); // SC
					row.addCell(cell);
				}
				// Are we printing NSAF?
        		if(displayColumns.isShowNsaf()) {
        			cell = new TableCell();
        			cell.setClassName("prot-not-found"); // NSAF
        			row.addCell(cell);
        		}
			}
			else {
				String className = "prot-found";
    			
				// Peptide sequence count
				if(displayColumns.isShowNumSeq()) {
					String contents = dpi.getSequenceCount()+"";
					TableCell seqCount = new TableCell(contents);
					row.addCell(seqCount);
				}
            
            	// Peptide ion count
				if(displayColumns.isShowNumIons()) {
					String contents = dpi.getIonCount()+"";
					TableCell ionCount = new TableCell(contents);
					row.addCell(ionCount);
				}
            	
            	// Unique Peptide ion count
				if(displayColumns.isShowNumUniqIons()) {
					String contents = dpi.getUniqueIonCount()+"";
					TableCell uIonCount = new TableCell(contents);
					row.addCell(uIonCount);
				}
            	
				if(displayColumns.isShowSpectrumCount()) {
					TableCell cell = new TableCell();
					cell.setClassName(className);
					cell.setData(dpi.getSpectrumCount()+"("+dpi.getNormalizedSpectrumCountRounded()+")");
					cell.setTextColor("#FFFFFF");
					
					float scaledCount = getScaledSpectrumCount(dpi.getNormalizedSpectrumCount());
					if(this.spectrumCountColors == null) {
    					cell.setBackgroundColor(getScaledColor(scaledCount));
    					// cell.setTextColor("#000000");
					}
    				else
    					cell.setBackgroundColor(getHeatMapColor(dpi.getHeatMapSpectrumCount()));
					row.addCell(cell);
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
        	fullContents += "<span style=\"color:#000080;\"<b>";//["+dbName+"]</b></span>&nbsp;&nbsp;"+ref.getDescriptionEscaped();
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
	
	public String getScaledColor(float scaledSpectrumCount) {
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

	private String hexValue(int r, int g, int b) {
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
		return Math.min(rowCount, this.getFilteredProteinCount() - this.getOffset());
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
        
        if(displayColumns.isShowFastaId()) {
        	header = new TableHeader("Fasta ID");
        	//header.setWidth(8);
        	header.setSortable(true);
        	header.setSortClass(SORT_CLASS.SORT_ALPHA);
        	header.setDefaultSortOrder(SORT_ORDER.ASC);
        	header.setHeaderId(SORT_BY.ACCESSION.name());
        	if(this.sortBy == SORT_BY.ACCESSION) {
        		header.setSorted(true);
        		header.setSortOrder(this.sortOrder);
        	}
        	header.setRowspan(2);
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
        int colspan = 4;
        if(!displayColumns.isShowNumSeq()) colspan--;
        if(!displayColumns.isShowNumIons()) colspan--;
        if(!displayColumns.isShowNumUniqIons()) colspan--;
        if(!displayColumns.isShowSpectrumCount()) colspan--;
        
        
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
		
		if(!this.proteinsInitialized) {
			int max = Math.min((this.getOffset() + rowCount), this.getFilteredProteinCount());
			initializeInfo(this.getOffset(), max);
		}
	}

	public void initializeInfo(int startIndex, int endIndex) {

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

		// get the (max)number of peptides identified for this protein
		if(this.sortBy != SORT_BY.NUM_PEPT)
			protein.setTotalPeptideSeqCount(DatasetPeptideComparer.instance().getTotalPeptSeqForProtein(protein));

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
		if(!protein.molWtAndPiSet()) {
			String sequence = NrSeqLookupUtil.getProteinSequence(protein.getNrseqId());
			protein.setMolecularWeight( (float) (Math.round(ProteinUtils.calculateMolWt(sequence)*100) / 100.0));
			protein.setPi((float) (Math.round(ProteinUtils.calculatePi(sequence)*100) / 100.0));
		}
	}


	List<Integer> getFastaDatabaseIds() {
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

	@Override
	public void setDisplayPageNumbers(List<Integer> pageList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setLastPage(int pageCount) {
		throw new UnsupportedOperationException();
	}

}
