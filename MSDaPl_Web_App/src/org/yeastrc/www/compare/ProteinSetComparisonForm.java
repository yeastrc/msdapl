/**
 * ProteinferRunComparisionForm.java
 * @author Vagisha Sharma
 * Apr 10, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.www.compare.clustering.ClusteringConstants;
import org.yeastrc.www.compare.clustering.ClusteringConstants.GRADIENT;

/**
 * 
 */
public class ProteinSetComparisonForm extends DatasetFiltersForm {

    private int pageNum = 1;
    private int numPerPage = 50;
    private int rowIndex = -1;
    
    private ComparisonCommand action;
    
    // DOWNLOAD options
    private boolean download = false;
    private boolean collapseProteinGroups = false; // used only for downloading results
    private boolean includeDescription = false; // used only when downloading results
    private boolean includePeptides = false; // used only when downloading results
    
    // GO ENRICHMENT
    private int goAspect = GOUtils.BIOLOGICAL_PROCESS;
    private int speciesId;
    private String goEnrichmentPVal = "0.01";
    private int goSlimTermId;
    
    
    private SORT_BY sortBy = SORT_BY.NUM_PEPT;
    private SORT_ORDER sortOrder = SORT_ORDER.DESC;

    // CLUSTER
	private String clusteringToken = null;
	private boolean newToken = false;
	private boolean useLogScale = false;
	private int logBase = 10;
	private String replaceMissingWithValue = "-1";
	private GRADIENT heatMapGradient = ClusteringConstants.GRADIENT.BY;
	private boolean clusterColumns = false;
	private boolean scaleRows = true;
	
	// WHICH COLUMNS TO DISPLAY
	private boolean showPresent = true;
	private boolean showFastaId = true;
	private boolean showCommonName = true;
	private boolean showDescription = true;
	private boolean showMolWt = true;
	private boolean showPi = true;
	private boolean showTotalSeq = true;
	private boolean showNumSeq = true;
	private boolean showNumIons = true;
	private boolean showNumUniqIons = true;
	private boolean showSpectrumCount = true;
	private boolean showNsaf = false;
    
    
	public void reset(ActionMapping mapping, HttpServletRequest request) {
        
		if(request.getAttribute("comparisonFormReset") == null) {
			super.reset(mapping, request);
			showPresent = false;
			showFastaId = false;
			showCommonName = false;
			showDescription = false;
			showMolWt = false;
			showPi = false;
			showTotalSeq = false;
			showNumSeq = false;
			showNumIons = false;
			showNumUniqIons = false;
			showSpectrumCount = false;
			showNsaf = false;
			scaleRows = false;
		}
		request.setAttribute("comparisonFormReset", true);
    }

	public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
    
    public int getNumPerPage() {
		return numPerPage;
	}

	public void setNumPerPage(int numPerPage) {
		this.numPerPage = numPerPage;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public ComparisonCommand getComparisonAction() {
		return this.action;
	}
	
	public void setComparisonActionId(int id) {
		this.action = ComparisonCommand.forId(id);
	}
	
	public int getComparisonActionId() {
		if(this.action == null)
			return 0;
		return action.getId();
	}
	
	/**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();

        if(request.getAttribute("comparisonFormReset") == null) {
        	// we need atleast two datasets runs to compare
        	if (getSelectedRunCount() < 2) {
        		errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more experiments to compare."));
        	}
        }
        return errors;
    }

    //-----------------------------------------------------------------------------
    // Download
    //-----------------------------------------------------------------------------
    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }
    
    public boolean isCollapseProteinGroups() {
        return this.collapseProteinGroups;
    }
    
    public void setCollapseProteinGroups(boolean collapse) {
        this.collapseProteinGroups = collapse;
    }
    
    public boolean isIncludeDescriptions() {
        return this.includeDescription;
    }
    
    public void setIncludeDescriptions(boolean include) {
        this.includeDescription = include;
    }
    
    public boolean isIncludePeptides() {
        return this.includePeptides;
    }
    
    public void setIncludePeptides(boolean include) {
        this.includePeptides = include;
    }

    //-----------------------------------------------------------------------------
    // GO Enrichment
    //-----------------------------------------------------------------------------
    public int getGoAspect() {
        return goAspect;
    }

    public void setGoAspect(int goAspect) {
        this.goAspect = goAspect;
    }

    public String getGoEnrichmentPVal() {
        return goEnrichmentPVal;
    }
    
    public double getGoEnrichmentPValDouble() {
    	if(goEnrichmentPVal == null || goEnrichmentPVal.trim().length() == 0)
    		return 0.0;
    	return Double.parseDouble(goEnrichmentPVal);
    }

    public void setGoEnrichmentPVal(String goEnrichmentPVal) {
        this.goEnrichmentPVal = goEnrichmentPVal;
    }

    public int getSpeciesId() {
        return speciesId;
    }

    public void setSpeciesId(int speciesId) {
        this.speciesId = speciesId;
    }

    public int getGoSlimTermId() {
		return goSlimTermId;
	}

	public void setGoSlimTermId(int goSlimTermId) {
		this.goSlimTermId = goSlimTermId;
	}
	
    public boolean isDoGoSlimAnalysis() {
		return (action == ComparisonCommand.GO_SLIM);
	}
    
    public boolean isDoGoSlimAnalysisTree() {
    	return (action == ComparisonCommand.GO_SLIM_TREE);
    }

	public boolean isDoGoEnrichAnalysis() {
		return (action == ComparisonCommand.GO_ENRICH);
	}
	
	public boolean isDoGoEnrichAnalysisTree() {
		return (action == ComparisonCommand.GO_ENRICH_TREE);
	}
	
	public boolean isDoGoAnalysis() {
		return isDoGoSlimAnalysis() || isDoGoEnrichAnalysis()
				|| isDoGoSlimAnalysisTree() || isDoGoEnrichAnalysisTree();
	}

    //-----------------------------------------------------------------------------
    // Cluster
    //-----------------------------------------------------------------------------
    public boolean isCluster() {
        return (action == ComparisonCommand.CLUSTER);
    }

    public String getClusteringToken() {
		return clusteringToken;
	}

	public void setClusteringToken(String clusteringToken) {
		this.clusteringToken = clusteringToken;
	}
	
	public boolean isNewToken() {
		return newToken;
	}

	public void setNewToken(boolean newToken) {
		this.newToken = newToken;
	}
	
	public boolean isUseLogScale() {
		return useLogScale;
	}

	public void setUseLogScale(boolean useLogScale) {
		this.useLogScale = useLogScale;
	}

	public int getLogBase() {
		return logBase;
	}

	public void setLogBase(int logBase) {
		this.logBase = logBase;
	}
	
	public String getReplaceMissingWithValue() {
		return replaceMissingWithValue;
	}
	
	public double getReplaceMissingWithValueDouble() {
		if(replaceMissingWithValue == null || replaceMissingWithValue.trim().length() == 0) {
			return -1.0;
		}
		return Double.parseDouble(replaceMissingWithValue);
	}

	public void setReplaceMissingWithValue(String replaceMissingWithValue) {
		this.replaceMissingWithValue = replaceMissingWithValue;
	}
	
	public boolean isClusterColumns() {
		return clusterColumns;
	}

	public void setClusterColumns(boolean clusterColumns) {
		this.clusterColumns = clusterColumns;
	}
	
	public boolean isScaleRows() {
		return scaleRows;
	}

	public void setScaleRows(boolean scaleRows) {
		this.scaleRows = scaleRows;
	}

	public String getHeatMapGradientString() {
		return heatMapGradient.getDisplayName();
	}
	
	public void setHeatMapGradientString(String gradientString) {
		this.heatMapGradient = GRADIENT.getGradient(gradientString);
	}
	
	public GRADIENT getHeatMapGradient() {
		return this.heatMapGradient;
	}

	public void setHeatMapGradient(GRADIENT gradient) {
		this.heatMapGradient = gradient;
	}

    
    //-----------------------------------------------------------------------------
    // Sorting
    //-----------------------------------------------------------------------------
    public SORT_BY getSortBy() {
        return this.sortBy;
    }
    public String getSortByString() {
        if(sortBy == null)  return null;
        return this.sortBy.name();
    }
    
    public void setSortBy(SORT_BY sortBy) {
        this.sortBy = sortBy;
    }
    public void setSortByString(String sortBy) {
        this.sortBy = SORT_BY.getSortByForString(sortBy);
    }
    
    public SORT_ORDER getSortOrder() {
        return this.sortOrder;
    }
    public String getSortOrderString() {
        if(sortOrder == null)   return null;
        return this.sortOrder.name();
    }
    
    public void setSortOrder(SORT_ORDER sortOrder) {
        this.sortOrder = sortOrder;
    }
    public void setSortOrderString(String sortOrder) {
        this.sortOrder = SORT_ORDER.getSortByForName(sortOrder);
    }
    
    //-----------------------------------------------------------------------------
    // Columns we will display
    //-----------------------------------------------------------------------------
    public boolean isShowPresent() {
		return showPresent;
	}
	public void setShowPresent(boolean showPresent) {
		this.showPresent = showPresent;
	}
	public boolean isShowFastaId() {
		return showFastaId;
	}
	public void setShowFastaId(boolean showFastaId) {
		this.showFastaId = showFastaId;
	}
	public boolean isShowCommonName() {
		return showCommonName;
	}
	public void setShowCommonName(boolean showCommonName) {
		this.showCommonName = showCommonName;
	}
	public boolean isShowDescription() {
		return showDescription;
	}
	public void setShowDescription(boolean showDescription) {
		this.showDescription = showDescription;
	}
	public boolean isShowMolWt() {
		return showMolWt;
	}
	public void setShowMolWt(boolean showMolWt) {
		this.showMolWt = showMolWt;
	}
	public boolean isShowPi() {
		return showPi;
	}
	public void setShowPi(boolean showPi) {
		this.showPi = showPi;
	}
	public boolean isShowTotalSeq() {
		return showTotalSeq;
	}
	public void setShowTotalSeq(boolean showTotalSeq) {
		this.showTotalSeq = showTotalSeq;
	}
	public boolean isShowNumSeq() {
		return showNumSeq;
	}
	public void setShowNumSeq(boolean showNumSeq) {
		this.showNumSeq = showNumSeq;
	}
	public boolean isShowNumIons() {
		return showNumIons;
	}
	public void setShowNumIons(boolean showNumIons) {
		this.showNumIons = showNumIons;
	}
	public boolean isShowNumUniqIons() {
		return showNumUniqIons;
	}
	public void setShowNumUniqIons(boolean showNumUniqIons) {
		this.showNumUniqIons = showNumUniqIons;
	}
	public boolean isShowSpectrumCount() {
		return showSpectrumCount;
	}
	public void setShowSpectrumCount(boolean showSpectrumCount) {
		this.showSpectrumCount = showSpectrumCount;
	}
	
	public boolean isShowNsaf() {
		return showNsaf;
	}
	public void setShowNsaf(boolean showNsaf) {
		this.showNsaf = showNsaf;
	}
	
	public void resetDisplayColumns() {
		this.setShowPresent(true);
		this.setShowFastaId(true);
		this.setShowCommonName(true);
		this.setShowDescription(true);
		this.setShowMolWt(true);
		this.setShowPi(true);
		this.setShowTotalSeq(true);
		this.setShowNumSeq(true);
		this.setShowNumIons(true);
		this.setShowNumUniqIons(true);
		this.setShowSpectrumCount(true);
		this.setShowNsaf(false);
	}
	
	public void setDisplayColumns(DisplayColumns displayColumns) {
		this.setShowPresent(displayColumns.isShowPresent());
		this.setShowFastaId(displayColumns.isShowFastaId());
		this.setShowCommonName(displayColumns.isShowCommonName());
		this.setShowDescription(displayColumns.isShowDescription());
		this.setShowMolWt(displayColumns.isShowMolWt());
		this.setShowPi(displayColumns.isShowPi());
		this.setShowTotalSeq(displayColumns.isShowTotalSeq());
		this.setShowNumSeq(displayColumns.isShowNumSeq());
		this.setShowNumIons(displayColumns.isShowNumIons());
		this.setShowNumUniqIons(displayColumns.isShowNumUniqIons());
		this.setShowSpectrumCount(displayColumns.isShowSpectrumCount());
		this.setShowNsaf(displayColumns.isShowNsaf());
	}
	
	public DisplayColumns getDisplayColumns () {
		DisplayColumns colFilters = new DisplayColumns();
		colFilters.setShowPresent(this.isShowPresent());
		colFilters.setShowFastaId(this.isShowFastaId());
		colFilters.setShowCommonName(this.isShowCommonName());
		colFilters.setShowDescription(this.isShowDescription());
		colFilters.setShowMolWt(this.isShowMolWt());
		colFilters.setShowPi(this.isShowPi());
		colFilters.setShowTotalSeq(this.isShowTotalSeq());
		colFilters.setShowNumSeq(this.isShowNumSeq());
		colFilters.setShowNumIons(this.isShowNumIons());
		colFilters.setShowNumUniqIons(this.isShowNumUniqIons());
		colFilters.setShowSpectrumCount(this.isShowSpectrumCount());
		colFilters.setShowNsaf(this.isShowNsaf());
		return colFilters;
	}
}
