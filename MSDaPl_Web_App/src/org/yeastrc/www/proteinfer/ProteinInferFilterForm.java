package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.www.proteinfer.idpicker.DisplayColumn;
import org.yeastrc.www.proteinfer.idpicker.DisplayColumns;

public class ProteinInferFilterForm extends ActionForm {

    private int pinferId;
    
    private String minCoverage = "0.0";
    private String maxCoverage = "100.0";
    private String minMolecularWt = "0.0";
    private String maxMolecularWt;
    private String minPi;
    private String maxPi;
    private String minPeptides = "1";
    private String maxPeptides;
    private String minUniquePeptides = "0";
    private String maxUniquePeptides;
    private String minSpectrumMatches = "1";
    private String maxSpectrumMatches;
    
    private String goTerms = null;
    private boolean matchAllGoTerms = false;
    private boolean exactGoAnnotation = false;
    private boolean excludeIea = false;
    private boolean excludeNd = false;
    private boolean excludeCompAnalCodes = false;
    
    private String commonNameLike = null;
	private String accessionLike = null;
    private String descriptionLike = null;
    private String descriptionNotLike = null;
    private boolean searchAllDescriptions = false;
    private String[] validationStatus = new String[]{"All"};
    
    private String[] chargeStates = new String[]{"All"};
    
    private boolean excludeIndistinGroups = false;
    
    private String peptide = null;
    private boolean exactMatch = false;
    
    // GO analysis options
    private int goAspect = GOUtils.BIOLOGICAL_PROCESS; 
    private int goSlimTermId;
    private int speciesId;
    private boolean exactAnnotations = true; 			// Used for GO enrichment only
    private String goEnrichmentPVal = "0.01";           // Used for GO enrichment only
    private boolean applyMultiTestCorrection = true; 	// Used for GO enrichment only
    
    private boolean doDownload = false;
    private boolean downloadGOAnnotations = false;
    private boolean doGoSlimAnalysis = false;
    private boolean getGoSlimTree = false;
    private boolean doGoEnrichAnalysis = false;
    
    
    private List<DisplayColumn> displayColumnList;
    
	public ProteinInferFilterForm () {
		displayColumnList = new ArrayList<DisplayColumn>();
	}
    

	/**
     * Validate the properties that have been sent from the HTTP request,
     * and return an ActionErrors object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * an empty ActionErrors object.
     */
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = super.validate(mapping, request);
        
        return errors;
    }

    public int getPinferId() {
        return pinferId;
    }

    public void setPinferId(int pinferId) {
        this.pinferId = pinferId;
    }

    public boolean isDoDownload() {
		return doDownload;
	}

	public void setDoDownload(boolean doDownload) {
		this.doDownload = doDownload;
	}
	
	public boolean isDownloadGOAnnotations() {
		return downloadGOAnnotations;
	}

	public void setDownloadGOAnnotations(boolean downloadGOAnnotations) {
		this.downloadGOAnnotations = downloadGOAnnotations;
	}


	// MIN COVERAGE
    public String getMinCoverage() {
        return minCoverage;
    }
    public double getMinCoverageDouble() {
        if(minCoverage == null || minCoverage.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minCoverage);
    }
    public void setMinCoverage(String minCoverage) {
        this.minCoverage = minCoverage;
    }
    
    // MAX COVERAGE
    public String getMaxCoverage() {
        return maxCoverage;
    }
    public double getMaxCoverageDouble() {
        if(maxCoverage == null || maxCoverage.trim().length() == 0)
            return 100.0;
        else
            return Double.parseDouble(maxCoverage);
    }
    public void setMaxCoverage(String maxCoverage) {
        this.maxCoverage = maxCoverage;
    }
    
    // MIN MOLECULAR WT.
    public String getMinMolecularWt() {
        return minMolecularWt;
    }
    public double getMinMolecularWtDouble() {
        if(minMolecularWt == null || minMolecularWt.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minMolecularWt);
    }
    public void setMinMolecularWt(String minMolecularWt) {
        this.minMolecularWt = minMolecularWt;
    }
    
    // MAX MOLECULAR WT.
    public String getMaxMolecularWt() {
        return maxMolecularWt;
    }
    public double getMaxMolecularWtDouble() {
        if(maxMolecularWt == null || maxMolecularWt.trim().length() == 0)
            return Double.MAX_VALUE;
        else
            return Double.parseDouble(maxMolecularWt);
    }
    public void setMaxMolecularWt(String maxMolecularWt) {
        this.maxMolecularWt = maxMolecularWt;
    }
    
    
    // MIN PI
    public String getMinPi() {
        return minPi;
    }
    public double getMinPiDouble() {
        if(minPi == null || minPi.trim().length() == 0)
            return 0;
        else
            return Double.parseDouble(minPi);
    }
    public void setMinPi(String minPi) {
        this.minPi = minPi;
    }
    
    // MAX PI
    public String getMaxPi() {
        return maxPi;
    }
    public double getMaxPiDouble() {
        if(maxPi == null || maxPi.trim().length() == 0)
            return Double.MAX_VALUE;
        else
            return Double.parseDouble(maxPi);
    }
    public void setMaxPi(String maxPi) {
        this.maxPi = maxPi;
    }
    
    // MIN PEPTIDES
    public String getMinPeptides() {
        return minPeptides;
    }
    public int getMinPeptidesInteger() {
        if(minPeptides == null || minPeptides.trim().length() == 0)
            return 1;
        return Integer.parseInt(minPeptides);
    }
    public void setMinPeptides(String minPeptides) {
        this.minPeptides = minPeptides;
    }

    // MAX PEPTIDES
    public String getMaxPeptides() {
        return maxPeptides;
    }
    public int getMaxPeptidesInteger() {
        if(maxPeptides == null || maxPeptides.trim().length() == 0)
            return Integer.MAX_VALUE;
        return Integer.parseInt(maxPeptides);
    }
    public void setMaxPeptides(String maxPeptides) {
        this.maxPeptides = maxPeptides;
    }
    
    // MIN UNIQUE PEPTIDES
    public String getMinUniquePeptides() {
        return minUniquePeptides;
    }
    public int getMinUniquePeptidesInteger() {
        if(minUniquePeptides == null || minUniquePeptides.trim().length() == 0)
            return 0;
        else
            return Integer.parseInt(minUniquePeptides);
    }
    public void setMinUniquePeptides(String minUniquePeptides) {
        this.minUniquePeptides = minUniquePeptides;
    }

    // MAX UNIQUE PEPTIDES
    public String getMaxUniquePeptides() {
        return maxUniquePeptides;
    }
    public int getMaxUniquePeptidesInteger() {
        if(maxUniquePeptides == null || maxUniquePeptides.trim().length() == 0)
            return Integer.MAX_VALUE;
        else
            return Integer.parseInt(maxUniquePeptides);
    }
    public void setMaxUniquePeptides(String maxUniquePeptides) {
        this.maxUniquePeptides = maxUniquePeptides;
    }

    // MIN SPECTRUM MATCHES
    public String getMinSpectrumMatches() {
        return minSpectrumMatches;
    }
    public int getMinSpectrumMatchesInteger() {
        if(minSpectrumMatches == null || minSpectrumMatches.trim().length() == 0)
            return 1;
        else
            return Integer.parseInt(minSpectrumMatches);
    }
    public void setMinSpectrumMatches(String minSpectrumMatches) {
        this.minSpectrumMatches = minSpectrumMatches;
    }
    
    // MAX SPECTRUM MATCHES
    public String getMaxSpectrumMatches() {
        return maxSpectrumMatches;
    }
    public int getMaxSpectrumMatchesInteger() {
        if(maxSpectrumMatches == null || maxSpectrumMatches.trim().length() == 0)
            return Integer.MAX_VALUE;
        return Integer.parseInt(maxSpectrumMatches);
    }
    public void setMaxSpectrumMatches(String maxSpectrumMatches) {
        this.maxSpectrumMatches = maxSpectrumMatches;
    }


    public boolean isExcludeIndistinProteinGroups() {
        return this.excludeIndistinGroups;
    }
    
    public void setExcludeIndistinProteinGroups(boolean exclude) {
        this.excludeIndistinGroups = exclude;
    }
    
    // GO TERMS
    public String getGoTerms() {
    	if(goTerms == null || goTerms.trim().length() == 0)
            return null;
        else
            return goTerms.trim();
    }
    
    public void setGoTerms(String goTerms) {
    	this.goTerms = goTerms;
    }
    
    public boolean isMatchAllGoTerms() {
		return matchAllGoTerms;
	}

	public void setMatchAllGoTerms(boolean matchAllGoTerms) {
		this.matchAllGoTerms = matchAllGoTerms;
	}
    
    public boolean isExactGoAnnotation() {
		return exactGoAnnotation;
	}

	public void setExactGoAnnotation(boolean exactGoAnnotation) {
		this.exactGoAnnotation = exactGoAnnotation;
	}
	
	public boolean isExcludeIea() {
		return excludeIea;
	}

	public void setExcludeIea(boolean excludeIEa) {
		this.excludeIea = excludeIEa;
	}

	public boolean isExcludeNd() {
		return excludeNd;
	}

	public void setExcludeNd(boolean excludeNd) {
		this.excludeNd = excludeNd;
	}

	public boolean isExcludeCompAnalCodes() {
		return excludeCompAnalCodes;
	}

	public void setExcludeCompAnalCodes(boolean excludeCompAnalCodes) {
		this.excludeCompAnalCodes = excludeCompAnalCodes;
	}

	// COMMON NAME
	public String getCommonNameLike() {
        if(commonNameLike == null || commonNameLike.trim().length() == 0)
            return null;
        else
            return commonNameLike.trim();
            
    }
    
    public void setCommonNameLike(String commonNameLike) {
        this.commonNameLike = commonNameLike;
    }
    
	// ACCESSION
    public String getAccessionLike() {
        if(accessionLike == null || accessionLike.trim().length() == 0)
            return null;
        else
            return accessionLike.trim();
            
    }
    
    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    // DESCRIPTION
    public String getDescriptionLike() {
        if(descriptionLike == null || descriptionLike.trim().length() == 0)
            return null;
        else
            return descriptionLike.trim();
            
    }
    
    public void setDescriptionLike(String descriptionLike) {
        this.descriptionLike = descriptionLike;
    }
    
    public String getDescriptionNotLike() {
        if(descriptionNotLike == null || descriptionNotLike.trim().length() == 0)
            return null;
        else
            return descriptionNotLike.trim();
    }
    
    public void setDescriptionNotLike(String descriptionNotLike) {
        this.descriptionNotLike = descriptionNotLike;
    }
    
    public boolean isSearchAllDescriptions() {
		return searchAllDescriptions;
	}

	public void setSearchAllDescriptions(boolean searchAllDescriptions) {
		this.searchAllDescriptions = searchAllDescriptions;
	}

    // PEPTIDE 
    public String getPeptide() {
        return peptide;
    }
    public void setPeptide(String peptide) {
        if(peptide != null && peptide.trim().length() == 0)
            this.peptide = null;
        else
            this.peptide = peptide;
    }
    
    public boolean getExactPeptideMatch() {
        return exactMatch;
    }
    public void setExactPeptideMatch(boolean exact) {
        this.exactMatch = exact;
    }
    
    // VALIDATION STATUS
    public String[] getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String[] validationStatus) {
        this.validationStatus = validationStatus;
    }
    
    public void setValidationStatusString(String validationStatus) {
        if(validationStatus == null)
            this.validationStatus = new String[0];
        validationStatus = validationStatus.trim();
        String tokens[] = validationStatus.split(",");
        this.validationStatus = new String[tokens.length];
        int idx = 0;
        for(String tok: tokens) {
            this.validationStatus[idx++] = tok.trim();
        }
    }
    
    public String getValidationStatusString() {
        if(this.validationStatus == null)
            return null;
        StringBuilder buf = new StringBuilder();
        for(String status: validationStatus) {
            buf.append(",");
            buf.append(status);
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
    }
    
    // CHARGE STATES
    public String[] getChargeStates() {
        return chargeStates;
    }

    public void setChargeStates(String[] chargeStates) {
        this.chargeStates = chargeStates;
    }
    
    public List<Integer> getChargeStateList() {
        List<Integer> chgList = new ArrayList<Integer>(chargeStates.length);
        for(String chg: chargeStates) {
            if(chg.equals("All"))
                return new ArrayList<Integer>(0);
            if(!chg.startsWith(">")) {
                chgList.add(Integer.parseInt(chg));
            }
        }
        return chgList;
    }
    
    public int getChargeGreaterThan() {
        for(String chg: chargeStates) {
            if(chg.startsWith(">")) {
                return Integer.parseInt(chg.substring(1));
            }
        }
        return -1;
    }
    
    public String getChargeStatesString() {
        if(this.chargeStates == null)
            return null;
        StringBuilder buf = new StringBuilder();
        for(String chg: chargeStates) {
            buf.append(",");
            buf.append(chg);
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0);
        return buf.toString();
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

    public void setGoEnrichmentPVal(String goEnrichmentPVal) {
        this.goEnrichmentPVal = goEnrichmentPVal;
    }
    
    public double getGoEnrichmentPValDouble() {
    	if(goEnrichmentPVal == null || goEnrichmentPVal.trim().length() == 0)
    		return 0.0;
    	return Double.parseDouble(goEnrichmentPVal);
    }

    public boolean isApplyMultiTestCorrection() {
		return applyMultiTestCorrection;
	}


	public void setApplyMultiTestCorrection(boolean applyMultiTestCorrection) {
		this.applyMultiTestCorrection = applyMultiTestCorrection;
	}
	
    public boolean isExactAnnotations() {
		return exactAnnotations;
	}

	public void setExactAnnotations(boolean exactAnnotations) {
		this.exactAnnotations = exactAnnotations;
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
		return doGoSlimAnalysis;
	}

	public void setDoGoSlimAnalysis(boolean doGoSlimAnalysis) {
		this.doGoSlimAnalysis = doGoSlimAnalysis;
	}

	public boolean isGetGoSlimTree() {
		return getGoSlimTree;
	}

	public void setGetGoSlimTree(boolean getGoSlimTree) {
		this.getGoSlimTree = getGoSlimTree;
	}

	public boolean isDoGoEnrichAnalysis() {
		return doGoEnrichAnalysis;
	}

	public void setDoGoEnrichAnalysis(boolean doGoEnrichAnalysis) {
		this.doGoEnrichAnalysis = doGoEnrichAnalysis;
	}

    public ProteinFilterCriteria getFilterCriteria(PeptideDefinition peptideDef) {
    	
    	ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        filterCriteria.setCoverage(getMinCoverageDouble());
        filterCriteria.setMaxCoverage(getMaxCoverageDouble());
        filterCriteria.setNumPeptides(getMinPeptidesInteger());
        filterCriteria.setNumMaxPeptides(getMaxPeptidesInteger());
        filterCriteria.setNumUniquePeptides(getMinUniquePeptidesInteger());
        filterCriteria.setNumMaxUniquePeptides(getMaxUniquePeptidesInteger());
        filterCriteria.setNumSpectra(getMinSpectrumMatchesInteger());
        filterCriteria.setNumMaxSpectra(getMaxSpectrumMatchesInteger());
        filterCriteria.setMinMolecularWt(getMinMolecularWtDouble());
        filterCriteria.setMaxMolecularWt(getMaxMolecularWtDouble());
        filterCriteria.setMinPi(getMinPiDouble());
        filterCriteria.setMaxPi(getMaxPiDouble());
        filterCriteria.setPeptideDefinition(peptideDef);
        filterCriteria.setValidationStatus(getValidationStatus());
        filterCriteria.setCommonNameLike(getCommonNameLike());
        filterCriteria.setAccessionLike(getAccessionLike());
        filterCriteria.setDescriptionLike(getDescriptionLike());
        filterCriteria.setDescriptionNotLike(getDescriptionNotLike());
        filterCriteria.setSearchAllDescriptions(isSearchAllDescriptions());
        filterCriteria.setExcludeIndistinGroups(isExcludeIndistinProteinGroups());
        filterCriteria.setPeptide(getPeptide());
        filterCriteria.setExactPeptideMatch(getExactPeptideMatch());
        filterCriteria.setChargeStates(this.getChargeStateList());
        int chgGreaterThan = this.getChargeGreaterThan();
        if(chgGreaterThan != -1)
        	filterCriteria.setChargeGreaterThan(chgGreaterThan);
        
        filterCriteria.setSortBy(ProteinFilterCriteria.defaultSortBy());
        filterCriteria.setSortOrder(ProteinFilterCriteria.defaultSortOrder());
        
        if(this.getGoTerms() != null && this.getGoTerms().trim().length() > 0) {
        	GOProteinFilterCriteria goFilters = new GOProteinFilterCriteria();
        	
        	goFilters.setExactAnnotation(this.isExactGoAnnotation());
        	goFilters.setMatchAllGoTerms(this.isMatchAllGoTerms());
        	
        	// evidence codes
        	List<String> evCodes = new ArrayList<String>();
        	goFilters.setExcludeEvidenceCodes(evCodes);
        	if(this.isExcludeIea())
        		evCodes.add("IEA");
        	if(this.isExcludeNd())
        		evCodes.add("ND");
        	if(this.isExcludeCompAnalCodes()) {
        		evCodes.add("ISS");
        		evCodes.add("ISO");
        		evCodes.add("ISA");
        		evCodes.add("ISM");
        		evCodes.add("IGC");
        		evCodes.add("RCA");
        	}
        	
        	// accessions
        	Set<String> goAccessions = new HashSet<String>();
        	String[] tokens = this.getGoTerms().split(",");
        	for(String token: tokens) {
        		goAccessions.add(token.trim());
        	}
        	goFilters.setGoAccessions(new ArrayList<String>(goAccessions));
        	
        	filterCriteria.setGoFilterCriteria(goFilters);
        }
        
        return filterCriteria;
    }
    
    public void setDisplayColumnList(List<DisplayColumn> list) {
    	this.displayColumnList = list;
    }
    
    public List<DisplayColumn> getDisplayColumnList() {
    	return this.displayColumnList;
    }
    
    public DisplayColumns getDisplayColumns() {
    	if(this.displayColumnList != null)
    		return DisplayColumns.initDisplayColumns(displayColumnList);
    	else
    		return new DisplayColumns();
    }
    
    
    public DisplayColumn getDisplayColumn(int index) {// indexed properties
    	while(index >= displayColumnList.size())
    		displayColumnList.add(new DisplayColumn());
        return displayColumnList.get(index);
    }
}
