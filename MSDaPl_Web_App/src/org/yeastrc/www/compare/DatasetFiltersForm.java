/**
 * DatasetFiltersForm.java
 * @author Vagisha Sharma
 * Sep 7, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.FilterableDataset;
import org.yeastrc.www.compare.dataset.ProteinProphetDataset;
import org.yeastrc.www.compare.dataset.SelectableDataset;

/**
 * 
 */
public class DatasetFiltersForm extends ActionForm {

    private List<SelectableDataset> andList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> orList  = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> notList = new ArrayList<SelectableDataset>();
    private List<SelectableDataset> xorList = new ArrayList<SelectableDataset>();
    
    private int parsimoniousParam = ProteinDatasetComparer.PARSIM.PARSIM_ONE.getNumericValue(); // parsimonious in at least one
    
    private boolean groupIndistinguishableProteins = false;
    
    private String minMolecularWt;
    private String maxMolecularWt;
    private String minPi;
    private String maxPi;
    
    private String minPeptides = "1";
    private String maxPeptides;
    private String minUniquePeptides = "0";
    private String maxUniquePeptides;
    private boolean peptideUniqueSequence = false;
    
    private String minSpectrumCount = "1";
    private String maxSpectrumCount;
    
    private String commonNameLike = null;
    private String accessionLike = null;
    private String descriptionLike = null;
    private String descriptionNotLike = null;
    private boolean searchAllDescriptions = false;
    
    private String goTerms = null;
    private boolean matchAllGoTerms = false;
    private boolean exactGoAnnotation = false;
    private boolean excludeIea = false;
    private boolean excludeNd = false;
    private boolean excludeCompAnalCodes = false;
    
    private boolean keepProteinGroups = true;

    // FOR PROTEIN-PROPHET
    private boolean hasProteinProphetDatasets = false;
    private String errorRate = "0.01";
    private boolean useProteinGroupProbability = true;
    
    private String minPeptideProbability = "0.0";
    private boolean applyProbToPept = true;
    private boolean applyProbToUniqPept = false;
    
    
    
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        accessionLike = null;
        descriptionLike = null;
        errorRate = "0.01";
        
        groupIndistinguishableProteins = false;
        useProteinGroupProbability = false;
        
        keepProteinGroups = false;
        peptideUniqueSequence = false;
        
        applyProbToPept = false;
        applyProbToUniqPept = false;
    }
    
    public boolean getApplyProbToPept() {
		return applyProbToPept;
	}

	public void setApplyProbToPept(boolean applyProbToPept) {
		this.applyProbToPept = applyProbToPept;
	}

	public boolean getApplyProbToUniqPept() {
		return applyProbToUniqPept;
	}

	public void setApplyProbToUniqPept(boolean applyProbToUniqPept) {
		this.applyProbToUniqPept = applyProbToUniqPept;
	}

	public boolean getPeptideUniqueSequence() {
		return peptideUniqueSequence;
	}

	public void setPeptideUniqueSequence(boolean peptideUniqueSequence) {
		this.peptideUniqueSequence = peptideUniqueSequence;
	}

	public boolean isKeepProteinGroups() {
        return keepProteinGroups;
    }

    public void setKeepProteinGroups(boolean keepProteinGroups) {
        this.keepProteinGroups = keepProteinGroups;
    }
    
    // ------------------------------------------------------------------------------------
    // FILTERING OPTIONS
    // ------------------------------------------------------------------------------------
    
    // USE PARSIMONIOUS AND NON-PARSIMONIOUS PROTEINS
    public int getParsimoniousParam() {
        return parsimoniousParam;
    }
    
    public void setParsimoniousParam(int parsimParam) {
        this.parsimoniousParam = parsimParam;
    }
    

    // GROUP INDISTINGUISHABLE PROTEINS
    public boolean getGroupIndistinguishableProteins() {
        return groupIndistinguishableProteins;
    }

    public void setGroupIndistinguishableProteins(boolean groupProteins) {
        this.groupIndistinguishableProteins = groupProteins;
    }
    
    // COMMON NAME FILTERS
    public String getCommonNameLike() {
        if(commonNameLike == null || commonNameLike.trim().length() == 0)
            return null;
        else
            return commonNameLike.trim();
            
    }
    
    public void setCommonNameLike(String commonNameLike) {
        this.commonNameLike = commonNameLike;
    }
    
    // ACCESSION STRING FILTERS
    public String getAccessionLike() {
        if(accessionLike == null || accessionLike.trim().length() == 0)
            return null;
        else
            return accessionLike.trim();
            
    }
    
    public void setAccessionLike(String accessionLike) {
        this.accessionLike = accessionLike;
    }
    
    // DESCRIPTION STRING FILTERS
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
    
    
    // MIN SPECTRUM COUNT
    public String getMinSpectrumCount() {
		return minSpectrumCount;
	}
    public int getMinSpectrumCountInteger() {
        if(minSpectrumCount == null || minSpectrumCount.trim().length() == 0)
            return 1;
        else
            return Integer.parseInt(minSpectrumCount);
    }
    public void setMinSpectrumCount(String minSpectrumCount) {
		this.minSpectrumCount = minSpectrumCount;
	}

    
    // MAX SPECTRUM COUNT
    public String getMaxSpectrumCount() {
		return maxSpectrumCount;
	}
    public int getMaxSpectrumCountInteger() {
        if(maxSpectrumCount == null || maxSpectrumCount.trim().length() == 0)
            return Integer.MAX_VALUE;
        else
            return Integer.parseInt(maxSpectrumCount);
    }
	public void setMaxSpectrumCount(String maxSpectrumCount) {
		this.maxSpectrumCount = maxSpectrumCount;
	}
    
    //-----------------------------------------------------------------------------
    // Molecular Weight
    //-----------------------------------------------------------------------------
    public String getMinMolecularWt() {
        return minMolecularWt;
    }

	public Double getMinMolecularWtDouble() {
        if(minMolecularWt != null && minMolecularWt.trim().length() > 0)
            return Double.parseDouble(minMolecularWt);
        return 0.0;
    }
    public void setMinMolecularWt(String molWt) {
        this.minMolecularWt = molWt;
    }
    
    public String getMaxMolecularWt() {
        return maxMolecularWt;
    }
    public Double getMaxMolecularWtDouble() {
        if(maxMolecularWt != null && maxMolecularWt.trim().length() > 0)
            return Double.parseDouble(maxMolecularWt);
        return Double.MAX_VALUE;
    }
    public void setMaxMolecularWt(String molWt) {
        this.maxMolecularWt = molWt;
    }
    
    //-----------------------------------------------------------------------------
    // pI
    //-----------------------------------------------------------------------------
    public String getMinPi() {
        return minPi;
    }
    public Double getMinPiDouble() {
        if(minPi != null && minPi.trim().length() > 0)
            return Double.parseDouble(minPi);
        return 0.0;
    }
    public void setMinPi(String pi) {
        this.minPi = pi;
    }
    
    public String getMaxPi() {
        return maxPi;
    }
    public Double getMaxPiDouble() {
        if(maxPi != null && maxPi.trim().length() > 0)
            return Double.parseDouble(maxPi);
        return Double.MAX_VALUE;
    }
    public void setMaxPi(String pi) {
        this.maxPi = pi;
    }


    // -------------------------------------------------------------------------------
    // DATASET FILTERS
    // -------------------------------------------------------------------------------
    //-----------------------------------------------------------------------------
    // AND list
    //-----------------------------------------------------------------------------
    public SelectableDataset getAndDataset(int index) {
        while(index >= andList.size()) {
            andList.add(new SelectableDataset());
        }
        return andList.get(index);
    }
    
    public void setAndList(List<SelectableDataset> andList) {
        this.andList = andList;
    }
    
    public List<SelectableDataset> getAndList() {
        Collections.sort(andList, new SelectableDatasetComparator());
        return andList;
    }
    
    //-----------------------------------------------------------------------------
    // OR list
    //-----------------------------------------------------------------------------
    public SelectableDataset getOrDataset(int index) {
        while(index >= orList.size()) {
            orList.add(new SelectableDataset());
        }
        return orList.get(index);
    }
    
    public void setOrList(List<SelectableDataset> orList) {
        this.orList = orList;
    }
    
    public List<SelectableDataset> getOrList() {
    	Collections.sort(orList, new SelectableDatasetComparator());
        return orList;
    }
    
    //-----------------------------------------------------------------------------
    // NOT list
    //-----------------------------------------------------------------------------
    public SelectableDataset getNotDataset(int index) {
        while(index >= notList.size()) {
            notList.add(new SelectableDataset());
        }
        return notList.get(index);
    }
    
    public void setNotList(List<SelectableDataset> notList) {
        this.notList = notList;
    }
    
    public List<SelectableDataset> getNotList() {
    	Collections.sort(notList, new SelectableDatasetComparator());
        return notList;
    }
    
    //-----------------------------------------------------------------------------
    // XOR list
    //-----------------------------------------------------------------------------
    public SelectableDataset getXorDataset(int index) {
        while(index >= xorList.size()) {
            xorList.add(new SelectableDataset());
        }
        return xorList.get(index);
    }
    
    public void setXorList(List<SelectableDataset> xorList) {
        this.xorList = xorList;
    }
    
    public List<SelectableDataset> getXorList() {
    	Collections.sort(xorList, new SelectableDatasetComparator());
        return xorList;
    }

    public boolean setDatasetOrder(List<Integer> piRunIds) {
    	
    	if(piRunIds.size() > andList.size())
    		return false;
    	
    	
    	int dsIndex = 0;
    	for(Integer piRunId: piRunIds) {
    		for(SelectableDataset ds: andList) {
    			if(ds.getDatasetId() == piRunId.intValue())
    				ds.setDatasetIndex(dsIndex);
    		}
    		dsIndex++;
    	}
    	dsIndex = 0;
    	for(Integer piRunId: piRunIds) {
    		for(SelectableDataset ds: orList) {
    			if(ds.getDatasetId() == piRunId.intValue())
    				ds.setDatasetIndex(dsIndex);
    		}
    		dsIndex++;
    	}
    	dsIndex = 0;
    	for(Integer piRunId: piRunIds) {
    		for(SelectableDataset ds: notList) {
    			if(ds.getDatasetId() == piRunId.intValue())
    				ds.setDatasetIndex(dsIndex);
    		}
    		dsIndex++;
    	}
    	dsIndex = 0;
    	for(Integer piRunId: piRunIds) {
    		for(SelectableDataset ds: xorList) {
    			if(ds.getDatasetId() == piRunId.intValue())
    				ds.setDatasetIndex(dsIndex);
    		}
    		dsIndex++;
    	}
    	
    	List<Integer> myIds = new ArrayList<Integer>(andList.size());
    	for(SelectableDataset ds: andList)
    		myIds.add(ds.getDatasetId());
    	
    	Collections.sort(piRunIds);
    	Collections.sort(myIds);
    	for(int i = 0; i < myIds.size(); i++)
    		if(myIds.get(i).intValue() != piRunIds.get(i).intValue())
    			return false;
    	
    	return true;
    }
    
    //-----------------------------------------------------------------------------
    // ProteinProphet datasets
    //-----------------------------------------------------------------------------
    public boolean getHasProteinProphetDatasets() {
        return hasProteinProphetDatasets ;
    }
    
    public void setHasProteinProphetDatasets(boolean hasProteinProphetDatasets) {
        this.hasProteinProphetDatasets = hasProteinProphetDatasets;
    }
    
    //-----------------------------------------------------------------------------
    // ProteinProphet error rate
    //-----------------------------------------------------------------------------
    public String getErrorRate() {
        return errorRate;
    }
    public double getErrorRateDouble() {
        if(errorRate == null || errorRate.trim().length() == 0)
            return 0.01;
        else
            return Double.parseDouble(errorRate);
    }
    public void setErrorRate(String errorRate) {
        this.errorRate = errorRate;
    }
    
    public boolean getUseProteinGroupProbability() {
        return this.useProteinGroupProbability;
    }
    
    public void setUseProteinGroupProbability(boolean useProteinGroupProbability) {
        this.useProteinGroupProbability = useProteinGroupProbability;
    }
    
    //-----------------------------------------------------------------------------
    // ProteinProphet peptide probability
    //-----------------------------------------------------------------------------
    public String getMinPeptideProbability() {
        return minPeptideProbability;
    }
    public double getMinPeptideProbabilityDouble() {
        if(minPeptideProbability == null || minPeptideProbability.trim().length() == 0)
            return 0.0;
        else
            return Double.parseDouble(minPeptideProbability);
    }
    public void setMinPeptideProbability(String peptideProbability) {
        this.minPeptideProbability = peptideProbability;
    }
    
    
    //-----------------------------------------------------------------------------
    // Total
    //-----------------------------------------------------------------------------
    public int getSelectedRunCount() {
        return andList.size();
    }
    
    public List<Integer> getAllSelectedRunIdsOrdered() {
        
    	List<SelectableDataset> orderedDatasets = new ArrayList<SelectableDataset>(andList.size());
    	for(SelectableDataset dataset: andList)
    		orderedDatasets.add(dataset);
    	// order by dataset Index
    	Collections.sort(orderedDatasets, new SelectableDatasetComparator());
    	
        List<Integer> all = new ArrayList<Integer>();
        for (SelectableDataset dataset: orderedDatasets) {
            all.add(dataset.getDatasetId());
        }
        return all;
    }
    
    // -------------------------------------------------------------------------------
    // BOOLEAN FILTERS
    // -------------------------------------------------------------------------------
    public DatasetBooleanFilters getSelectedBooleanFilters() {
        
        List<SelectableDataset> andDataset = getAndList();
        List<SelectableDataset> orDataset = getOrList();
        List<SelectableDataset> notDataset = getNotList();
        List<SelectableDataset> xorDataset = getXorList();
        
        List<Dataset> andFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: andDataset) {
            if(sds.isSelected())    andFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        List<Dataset> orFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: orDataset) {
            if(sds.isSelected())    orFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        List<Dataset> notFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: notDataset) {
            if(sds.isSelected())    notFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        List<Dataset> xorFilters = new ArrayList<Dataset>();
        for(SelectableDataset sds: xorDataset) {
            if(sds.isSelected())    xorFilters.add(new Dataset(sds.getDatasetId(), sds.getSource()));
        }
        
        DatasetBooleanFilters filters = new DatasetBooleanFilters();
        filters.setAndFilters(andFilters);
        filters.setOrFilters(orFilters);
        filters.setNotFilters(notFilters);
        filters.setXorFilters(xorFilters);
        return filters;
    }
    
    // -------------------------------------------------------------------------------
    // PROTEIN PROPERTIES FILTERS
    // -------------------------------------------------------------------------------
    public ProteinPropertiesFilters getProteinPropertiesFilters() {
    	
        ProteinPropertiesFilters filters = new ProteinPropertiesFilters();
        
        filters.setMinMolecularWt(this.getMinMolecularWtDouble());
        filters.setMaxMolecularWt(this.getMaxMolecularWtDouble());
        filters.setMinPi(this.getMinPiDouble());
        filters.setMaxPi(this.getMaxPiDouble());
        
        filters.setCommonNameLike(this.getCommonNameLike());
        filters.setAccessionLike(this.getAccessionLike());
        filters.setDescriptionLike(this.getDescriptionLike());
        filters.setDescriptionNotLike(this.getDescriptionNotLike());
        filters.setSearchAllDescriptions(this.isSearchAllDescriptions());
        
        filters.setMinPeptideCount(this.getMinPeptidesInteger());
        filters.setMaxPeptideCount(this.getMaxPeptidesInteger());
        filters.setMinUniqPeptideCount(this.getMinUniquePeptidesInteger());
        filters.setMaxUniqPeptideCount(this.getMaxUniquePeptidesInteger());
        
        filters.setMinSpectrumCount(this.getMinSpectrumCountInteger());
        filters.setMaxSpectrumCount(this.getMaxSpectrumCountInteger());
        
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
        	
        	filters.setGoFilter(goFilters);
        }
        
        if (this.hasProteinProphetDatasets) {
        	filters.setHasProteinProphetFilters(true);
        	filters.setProteinProphetError(this.getErrorRateDouble());
        	filters.setUseGroupProbability(this.getUseProteinGroupProbability());
        	filters.setPeptideProbability(this.getMinPeptideProbabilityDouble());
        	filters.setApplyToPeptide(this.getApplyProbToPept());
        	filters.setApplyToUniqPeptide(this.getApplyProbToUniqPept());
        }
        
        return filters;
    }
    
    // -------------------------------------------------------------------------------
    // FILTER CRITERIA
    // -------------------------------------------------------------------------------
    public ProteinFilterCriteria getFilterCriteria() {
        ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
        
        filterCriteria.setNumPeptides(this.getMinPeptidesInteger());
        filterCriteria.setNumMaxPeptides(this.getMaxPeptidesInteger());
        filterCriteria.setNumUniquePeptides(this.getMinUniquePeptidesInteger());
        filterCriteria.setNumMaxUniquePeptides(this.getMaxUniquePeptidesInteger());
        filterCriteria.setNumSpectra(this.getMinSpectrumCountInteger());
        filterCriteria.setNumMaxSpectra(this.getMaxSpectrumCountInteger());
        
        PeptideDefinition peptDef = new PeptideDefinition();
        if(!this.peptideUniqueSequence) {
        	peptDef.setUseCharge(true);
        	peptDef.setUseMods(true);
        }
        filterCriteria.setPeptideDefinition(peptDef);
        
        filterCriteria.setParsimonious(true);
        filterCriteria.setNonParsimonious(false);
        filterCriteria.setSubset(true);
        filterCriteria.setNonSubset(true);
        
        //filterCriteria.setSearchAllDescriptions(this.isSearchAllDescriptions());
       
        return filterCriteria;
    }
    
    public ProteinProphetFilterCriteria getProteinProphetFilterCriteria(FilterableDataset dataset) {
    	
        ProteinProphetFilterCriteria filterCriteria = new ProteinProphetFilterCriteria(this.getFilterCriteria());
        
        double minProbability = ((ProteinProphetDataset)dataset).getRoc().getMinProbabilityForError(this.getErrorRateDouble());
        if(this.getUseProteinGroupProbability())
            filterCriteria.setMinGroupProbability(minProbability);
        else filterCriteria.setMinProteinProbability(minProbability);
        
        if(applyProbToPept && getMinPeptideProbabilityDouble() > 0.0) {
        	filterCriteria.setMinPeptideProbability(getMinPeptideProbabilityDouble());
        }
        if(applyProbToUniqPept && getMinPeptideProbabilityDouble() > 0.0) {
        	filterCriteria.setMinUniqPeptideProbability(getMinPeptideProbabilityDouble());
        }
        ((ProteinProphetDataset)dataset).setProteinFilterCriteria(filterCriteria);
        
        return filterCriteria;
    }
    
    private static final class SelectableDatasetComparator implements Comparator<SelectableDataset> {

		@Override
		public int compare(SelectableDataset o1, SelectableDataset o2) {
			return Integer.valueOf(o1.getDatasetIndex()).compareTo(o2.getDatasetIndex());
		}
    }
}
