package org.yeastrc.www.proteinfer.idpicker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerInputDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerPeptideBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerPeptideDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerRunDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerIon;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptide;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptideBase;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.database.fasta.FastaInMemorySuffixCreator;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.nr_seq.listing.ProteinListingBuilder;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.philius.dao.PhiliusDAOFactory;
import org.yeastrc.philius.dao.PhiliusResultDAO;
import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.protein.ProteinAbundanceDao;
import org.yeastrc.www.proteinfer.MsResultLoader;
import org.yeastrc.www.proteinfer.ProteinAccessionFilter;
import org.yeastrc.www.proteinfer.ProteinCommonNameFilter;
import org.yeastrc.www.proteinfer.ProteinDescriptionFilter;
import org.yeastrc.www.proteinfer.ProteinGoTermsFilter;
import org.yeastrc.www.proteinfer.ProteinInferPhiliusResultChecker;
import org.yeastrc.www.proteinfer.ProteinInferToSpeciesMapper;
import org.yeastrc.www.proteinfer.ProteinProperties;
import org.yeastrc.www.proteinfer.ProteinPropertiesFilter;
import org.yeastrc.www.proteinfer.ProteinPropertiesSorter;
import org.yeastrc.www.proteinfer.ProteinPropertiesStore;

public class IdPickerResultsLoader {

    private static final ProteinferDAOFactory pinferDaoFactory = ProteinferDAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    private static final MsScanDAO scanDao = msDataDaoFactory.getMsScanDAO();
    //private static final MsRunDAO runDao = msDataDaoFactory.getMsRunDAO();
    private static final MS2ScanDAO ms2ScanDao = msDataDaoFactory.getMS2FileScanDAO();
    private static final MsRunSearchDAO rsDao = msDataDaoFactory.getMsRunSearchDAO();
    private static final MsRunSearchAnalysisDAO rsaDao = msDataDaoFactory.getMsRunSearchAnalysisDAO();
    
    private static final MsResultLoader resLoader = MsResultLoader.getInstance();
    
    private static final ProteinferSpectrumMatchDAO psmDao = pinferDaoFactory.getProteinferSpectrumMatchDao();
    private static final IdPickerSpectrumMatchDAO idpPsmDao = pinferDaoFactory.getIdPickerSpectrumMatchDao();
    private static final IdPickerPeptideDAO idpPeptDao = pinferDaoFactory.getIdPickerPeptideDao();
    private static final IdPickerPeptideBaseDAO idpPeptBaseDao = pinferDaoFactory.getIdPickerPeptideBaseDao();
    private static final IdPickerProteinBaseDAO idpProtBaseDao = pinferDaoFactory.getIdPickerProteinBaseDao();
    private static final ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
    private static final IdPickerInputDAO inputDao = pinferDaoFactory.getIdPickerInputDao();
    private static final IdPickerRunDAO idpRunDao = pinferDaoFactory.getIdPickerRunDao();
    private static final ProteinferRunDAO piRunDao = pinferDaoFactory.getProteinferRunDao();
    
    private static final PhiliusResultDAO philiusDao = PhiliusDAOFactory.getInstance().getPhiliusResultDAO();
    
    private static final Logger log = Logger.getLogger(IdPickerResultsLoader.class);
    
    private IdPickerResultsLoader(){}
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given filtering criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getProteinIds(int pinferId, ProteinFilterCriteria filterCriteria) {
    	
        long start = System.currentTimeMillis();
        List<Integer> proteinIds = idpProtBaseDao.getFilteredSortedProteinIds(pinferId, filterCriteria);
        log.info("Returned "+proteinIds.size()+" protein IDs for protein inference ID: "+pinferId);
        
        // filter by common name, if required
        if(filterCriteria.getCommonNameLike() != null) {
        	log.info("Filtering by common name: "+filterCriteria.getCommonNameLike());
        	proteinIds = ProteinCommonNameFilter.getInstance().filterForProtInferByCommonName(pinferId, proteinIds, 
        			filterCriteria.getCommonNameLike());
        }
        
        // filter by accession, if required
        if(filterCriteria.getAccessionLike() != null) {
            log.info("Filtering by accession: "+filterCriteria.getAccessionLike());
            proteinIds = ProteinAccessionFilter.getInstance().filterForProtInferByProteinAccession(pinferId, proteinIds, filterCriteria.getAccessionLike());
        }
        
        // filter by description, if required
        if(filterCriteria.getDescriptionLike() != null) {
            log.info("Filtering by description (like): "+filterCriteria.getDescriptionLike());
            proteinIds = ProteinDescriptionFilter.getInstance().filterPiProteinsByDescriptionLike(pinferId, proteinIds, 
            		filterCriteria.getDescriptionLike(), filterCriteria.isSearchAllDescriptions());
        }
        
        if(filterCriteria.getDescriptionNotLike() != null) {
        	log.info("Filtering by description (NOT like): "+filterCriteria.getDescriptionLike());
            proteinIds = ProteinDescriptionFilter.getInstance().filterPiProteinsByDescriptionNotLike(pinferId, proteinIds, 
            		filterCriteria.getDescriptionNotLike(), filterCriteria.isSearchAllDescriptions());
        }
        
        // filter by molecular wt, if required
        if(filterCriteria.hasMolecularWtFilter()) {
        	log.info("Filtering by molecular wt.");
            proteinIds = ProteinPropertiesFilter.getInstance().filterForProtInferByMolecularWt(pinferId, proteinIds,
                    filterCriteria.getMinMolecularWt(), filterCriteria.getMaxMolecularWt());
        }
        
        // filter by pI, if required
        if(filterCriteria.hasPiFilter()) {
        	log.info("Filtering by pI");
            proteinIds = ProteinPropertiesFilter.getInstance().filterForProtInferByPi(pinferId, proteinIds,
                    filterCriteria.getMinPi(), filterCriteria.getMaxPi());
        }
        
        // filter by GO terms, if required
        if(filterCriteria.getGoFilterCriteria() != null) {
        	GOProteinFilterCriteria goFilters = filterCriteria.getGoFilterCriteria();
        	log.info("Filtering by GO terms: "+goFilters.toString());
        	try {
				proteinIds = ProteinGoTermsFilter.getInstance().filterPinferProteins(proteinIds, goFilters);
			} catch (Exception e1) {
				log.error("Exception filtering proteins on GO terms", e1);
			}
        }
        
        long s = System.currentTimeMillis();
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.MOL_WT) {
            proteinIds = ProteinPropertiesSorter.sortIdsByMolecularWt(proteinIds, pinferId, 
            		filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
            
            long e = System.currentTimeMillis();
        	log.info("Time for resorting filtered IDs by Mol. Wt.: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        	
        }
        
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.ACCESSION) {
        	proteinIds = ProteinPropertiesSorter.sortIdsByAccession(proteinIds, pinferId,
        			filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
        	long e = System.currentTimeMillis();
        	log.info("Time for resorting filtered IDs by accession: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        }
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.PI) {
            proteinIds = ProteinPropertiesSorter.sortIdsByPi(proteinIds, pinferId, 
            		filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs by pI: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        }
        
        
        long e = System.currentTimeMillis();
        log.info("Time: "+TimeUtils.timeElapsedSeconds(start, e)+" seconds");
        return proteinIds;
    }
    

    //---------------------------------------------------------------------------------------------------
    // Sort the list of given protein IDs according to the given sorting criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getSortedProteinIds(int pinferId, PeptideDefinition peptideDef, 
            List<Integer> proteinIds, SORT_BY sortBy, SORT_ORDER sortOrder, boolean groupProteins) {
        
        long s = System.currentTimeMillis();
        List<Integer> allIds = null;
        if(sortBy == SORT_BY.ACCESSION) {
            List<Integer> sortedIds = ProteinPropertiesSorter.sortIdsByAccession(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        if(sortBy == SORT_BY.MOL_WT) {
            List<Integer> sortedIds = ProteinPropertiesSorter.sortIdsByMolecularWt(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        if(sortBy == SORT_BY.PI) {
            List<Integer> sortedIds = ProteinPropertiesSorter.sortIdsByPi(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        
        if(sortBy == SORT_BY.CLUSTER_ID) {
            allIds = idpProtBaseDao.sortProteinIdsByCluster(pinferId);
        }
        else if (sortBy == SORT_BY.GROUP_ID) {
            allIds = idpProtBaseDao.sortProteinIdsByGroup(pinferId);
        }
        else if (sortBy == SORT_BY.COVERAGE) {
            allIds = idpProtBaseDao.sortProteinIdsByCoverage(pinferId, groupProteins, sortOrder);
        }
        else if(sortBy == SORT_BY.NSAF) {
            allIds = idpProtBaseDao.sortProteinsByNSAF(pinferId, groupProteins, sortOrder);
        }
        else if(sortBy == SORT_BY.VALIDATION_STATUS) {
            allIds = idpProtBaseDao.sortProteinIdsByValidationStatus(pinferId);
        }
        else if (sortBy == SORT_BY.NUM_PEPT) {
            allIds = idpProtBaseDao.sortProteinIdsByPeptideCount(pinferId, peptideDef, groupProteins);
        }
        else if (sortBy == SORT_BY.NUM_UNIQ_PEPT) {
            allIds = idpProtBaseDao.sortProteinIdsByUniquePeptideCount(pinferId, peptideDef, groupProteins);
        }
        else if (sortBy == SORT_BY.NUM_SPECTRA) {
            allIds = idpProtBaseDao.sortProteinIdsBySpectrumCount(pinferId, groupProteins);
        }
        if(allIds == null) {
            log.warn("Could not get sorted order for all protein IDs for protein inference run: "+pinferId);
        }
        
        // we want the sorted order from allIds but only want to keep the ids in the current 
        // filtered list.
        // remove the ones from allIds that are not in the current filtered list. 
        Set<Integer> currentOrder = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        currentOrder.addAll(proteinIds);
        Iterator<Integer> iter = allIds.iterator();
        while(iter.hasNext()) {
            Integer protId = iter.next();
            if(!currentOrder.contains(protId))
                iter.remove();
        }
        
        long e = System.currentTimeMillis();
        log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return allIds;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of proteins
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getProteins(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WIdPickerProtein> proteins = new ArrayList<WIdPickerProtein>(proteinIds.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        boolean getPhiliusResults = ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(pinferId);
        
        for(int id: proteinIds) 
            proteins.add(getIdPickerProtein(id, peptideDef, fastaDatabaseIds, getPhiliusResults));
        long e = System.currentTimeMillis();
        log.info("Time to get WIdPickerProteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return proteins;
    }
    
    public static WIdPickerProtein getIdPickerProtein(int pinferProteinId, 
            PeptideDefinition peptideDef, List<Integer> databaseIds, boolean getPhiliusResults) {
        IdPickerProteinBase protein = idpProtBaseDao.loadProtein(pinferProteinId);
        protein.setPeptideDefinition(peptideDef);
        return getWIdPickerProtein(protein, databaseIds, getPhiliusResults);
    }
    
    public static WIdPickerProtein getIdPickerProtein(int pinferId, int pinferProteinId, 
            PeptideDefinition peptideDef) {
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        boolean getPhiliusResults = ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(pinferId);
        return getIdPickerProtein(pinferProteinId, peptideDef, fastaDatabaseIds, getPhiliusResults);
    }
    
    private static WIdPickerProtein getWIdPickerProtein(IdPickerProteinBase protein, List<Integer> databaseIds, boolean getPhiliusResults) {
        return getWIdPickerProtein(protein, databaseIds, true, getPhiliusResults);
    }
    
    private static WIdPickerProtein getWIdPickerProtein(IdPickerProteinBase protein, List<Integer> databaseIds, 
    		boolean assignProteinProperties,
    		boolean getPhiliusResults) {
    	
        WIdPickerProtein wProt = new WIdPickerProtein(protein);
        // set the accession and description for the proteins.  
        // This requires querying the NRSEQ database
        assignProteinListing(wProt, databaseIds);
        
        // get the molecular weight for the protein
        if(assignProteinProperties)
        	assignProteinProperties(wProt);
        
        if(getPhiliusResults) {
        	int sequenceId = wProt.getProteinListing().getSequenceId();
        	PhiliusResult res = philiusDao.loadForSequence(sequenceId);
        	if(res != null) {
        		wProt.setTransMembrane(res.isTransMembrane());
        		wProt.setSignalPeptide(res.isSignalPeptide());
        	}
        }
        
        return wProt;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get all proteins that the given protein is a subset of
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getSuperProteins(IdPickerProteinBase protein, int pinferId) {
        
    	long s = System.currentTimeMillis();
        
    	List<WIdPickerProtein> proteins = getWIdPickerProteins(protein.getSuperProteinIds(), pinferId);
    	
    	// return one representative of each protein group
    	Collections.sort(proteins, new Comparator<WIdPickerProtein>() {
			@Override
			public int compare(WIdPickerProtein o1, WIdPickerProtein o2) {
				int o1_grp = o1.getProtein().getProteinGroupLabel();
				int o2_grp = o2.getProtein().getProteinGroupLabel();
				if(o1_grp == o2_grp)
					return Integer.valueOf(o1.getProtein().getId()).compareTo(o2.getProtein().getId());
				
				return o1_grp < o2_grp ? -1 : 1;
			}
		});
    	
    	List<WIdPickerProtein> toReturn = new ArrayList<WIdPickerProtein>();
    	int lastGrpId = -1;
    	for(WIdPickerProtein prot: proteins) {
    		if(prot.getProtein().getProteinGroupLabel() != lastGrpId) {
    			toReturn.add(prot);
    		}
    		lastGrpId = prot.getProtein().getProteinGroupLabel();
    	}
    	
    	
        long e = System.currentTimeMillis();
        log.info("Time to get SUPER proteins for piProtenID: "+protein.getId()+" was "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return toReturn;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get all proteins that are a subset of this protein	
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getSubsetProteins(IdPickerProteinBase protein, int pinferId) {
        
    	long s = System.currentTimeMillis();

    	List<WIdPickerProtein> proteins = getWIdPickerProteins(protein.getSubsetProteinIds(), pinferId);
        
    	// return one representative of each protein group
    	Collections.sort(proteins, new Comparator<WIdPickerProtein>() {
			@Override
			public int compare(WIdPickerProtein o1, WIdPickerProtein o2) {
				int o1_grp = o1.getProtein().getProteinGroupLabel();
				int o2_grp = o2.getProtein().getProteinGroupLabel();
				if(o1_grp == o2_grp)
					return Integer.valueOf(o1.getProtein().getId()).compareTo(o2.getProtein().getId());
				
				return o1_grp < o2_grp ? -1 : 1;
			}
		});
    	
    	List<WIdPickerProtein> toReturn = new ArrayList<WIdPickerProtein>();
    	int lastGrpId = -1;
    	for(WIdPickerProtein prot: proteins) {
    		if(prot.getProtein().getProteinGroupLabel() != lastGrpId) {
    			toReturn.add(prot);
    		}
    		lastGrpId = prot.getProtein().getProteinGroupLabel();
    	}
    	
        long e = System.currentTimeMillis();
        log.info("Time to get SUBSET proteins for piProtenID: "+protein.getId()+" was "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return toReturn;
    }
    
    private static List<WIdPickerProtein> getWIdPickerProteins(List<Integer> piProteinIds, int pinferId) {
    	
    	if(piProteinIds.size() == 0)
    		return new ArrayList<WIdPickerProtein>(0);
    	
    	 List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
         
         List<WIdPickerProtein> proteins = new ArrayList<WIdPickerProtein>(piProteinIds.size());
         for(int id: piProteinIds) {
         	IdPickerProteinBase prot = idpProtBaseDao.loadProtein(id);
         	prot.setPeptideDefinition(new PeptideDefinition());
             proteins.add(getWIdPickerProtein(prot, fastaDatabaseIds, false, false));
         }
         
         return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get all the proteins in a group
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerProtein> getGroupProteins(int pinferId, int groupLabel, 
            PeptideDefinition peptideDef) {
        
        long s = System.currentTimeMillis();
        
        List<IdPickerProteinBase> groupProteins = idpProtBaseDao.loadIdPickerGroupProteins(pinferId, groupLabel);
        
        List<WIdPickerProtein> proteins = new ArrayList<WIdPickerProtein>(groupProteins.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        boolean getPhiliusResults = false;
        
        for(IdPickerProteinBase prot: groupProteins) {
            prot.setPeptideDefinition(peptideDef);
            proteins.add(getWIdPickerProtein(prot, fastaDatabaseIds, getPhiliusResults));
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get proteins in a group: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein groups
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getPageSublist(List<Integer> allProteinIds, int[] pageIndices, 
    		boolean completeGroups, boolean descending) {
    	
    	int firstIndex = descending ? pageIndices[1] : pageIndices[0];
    	int lastIndex = descending ? pageIndices[0] : pageIndices[1];
    	
    	//log.info("firstIndex: "+firstIndex+"; lastIndex: "+lastIndex);
    	if(completeGroups) {
    		firstIndex = getStartIndexToCompleteFirstGroup(allProteinIds, firstIndex);
    		lastIndex = getEndIndexToCompleteFirstGroup(allProteinIds, lastIndex);
    		//log.info("AFTER completeGroups firstIndex: "+firstIndex+"; lastIndex: "+lastIndex);
    	}
    	
    	//log.info("ALL protein ID size: "+allProteinIds);
    	
    	// sublist
        List<Integer> proteinIds = new ArrayList<Integer>();
        if(descending) {
        	for(int i = lastIndex; i >= firstIndex; i--)	
        		proteinIds.add(allProteinIds.get(i));
        }
        else {
        	for(int i = firstIndex; i <= lastIndex; i++)
        		proteinIds.add(allProteinIds.get(i));
        }
        return proteinIds;
    }
    
    private static int getStartIndexToCompleteFirstGroup(List<Integer> allProteinIds, int startIndex) {
    	
    	if(startIndex == 0)
    		return startIndex;
    	if(startIndex < 0) {
    		log.error("startIndex < 0 in getStartIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("startIndex < 0 in getStartIndexToCompleteFirstGroup");
    	}
    	if(startIndex >= allProteinIds.size()) {
    		log.error("startIndex >= list size in getStartIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("startIndex >= list size in getStartIndexToCompleteFirstGroup");
    	}
    	IdPickerProteinBase protein = idpProtBaseDao.loadProtein(allProteinIds.get(startIndex));
    	int groupLabel = protein.getProteinGroupLabel();
    	int idx = startIndex - 1;
    	while(idx >= 0) {
    		protein = idpProtBaseDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getProteinGroupLabel() != groupLabel) {
    			break;
    		}
    		idx--;
    	}
    	idx = idx+1;
    	return idx;
    }
    
    private static int getEndIndexToCompleteFirstGroup(List<Integer> allProteinIds, int endIndex) {
    	
    	if(endIndex == allProteinIds.size() - 1)
    		return endIndex;
    	if(endIndex >= allProteinIds.size()) {
    		log.error("endIndex >= list size in getEndIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("endIndex >= list size in getEndIndexToCompleteFirstGroup");
    	}
    	if(endIndex < 0) {
    		log.error("endInded < 0 in getEndIndexToCompleteFirstGroup");
    		throw new IllegalArgumentException("endInded < 0 in getEndIndexToCompleteFirstGroup");
    	}
    	IdPickerProteinBase protein = idpProtBaseDao.loadProtein(allProteinIds.get(endIndex));
    	int groupLabel = protein.getProteinGroupLabel();
    	int idx = endIndex + 1;
    	while(idx < allProteinIds.size()) {
    		protein = idpProtBaseDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getProteinGroupLabel() != groupLabel) {
    			break;
    		}
    		idx++;
    	}
    	idx = idx-1;
    	return idx;
    }
    
    
    public static List<WIdPickerProteinGroup> getProteinGroups(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WIdPickerProtein> proteins = getProteins(pinferId, proteinIds, peptideDef);
        
        if(proteins.size() == 0) {
            return new ArrayList<WIdPickerProteinGroup>(0);
        }
        
        if(proteins.size() == 0)
            return new ArrayList<WIdPickerProteinGroup>(0);
        
        int currGrpLabel = -1;
        List<WIdPickerProtein> grpProteins = null;
        List<WIdPickerProteinGroup> groups = new ArrayList<WIdPickerProteinGroup>();
        for(WIdPickerProtein prot: proteins) {
            if(prot.getProtein().getProteinGroupLabel() != currGrpLabel) {
                if(grpProteins != null && grpProteins.size() > 0) {
                    WIdPickerProteinGroup grp = new WIdPickerProteinGroup(grpProteins);
                    groups.add(grp);
                }
                currGrpLabel = prot.getProtein().getProteinGroupLabel();
                grpProteins = new ArrayList<WIdPickerProtein>();
            }
            grpProteins.add(prot);
        }
        if(grpProteins != null && grpProteins.size() > 0) {
            WIdPickerProteinGroup grp = new WIdPickerProteinGroup(grpProteins);
            groups.add(grp);
        }
        
        // If this protein inference ID is associated with yeast species
        // get the yeast protein abundances.
        if(ProteinInferToSpeciesMapper.isSpeciesYeast(pinferId)) {
        	ProteinAbundanceDao aDao = ProteinAbundanceDao.getInstance();
        	for(WIdPickerProteinGroup grp: groups) {
        		for(WIdPickerProtein protein: grp.getProteins()) {
        			int nrseqId = protein.getProtein().getNrseqProteinId();
        			try {
						protein.setYeastProteinAbundance(aDao.getAbundance(nrseqId));
					} catch (SQLException e1) {
						log.error("Exception getting yeast protein abundance", e1);
					}
        		}
        	}
        }
        long e = System.currentTimeMillis();
        
        
        log.info("Time to get WIdPickerProteinsGroups: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return groups;
    }
    
    //---------------------------------------------------------------------------------------------------
    // NR_SEQ lookup 
    //---------------------------------------------------------------------------------------------------
    private static void assignProteinListing(WIdPickerProtein wProt, List<Integer> databaseIds) {
        
    	ProteinListing listing = ProteinListingBuilder.getInstance().build(wProt.getProtein().getNrseqProteinId(), databaseIds);
    	wProt.setProteinListing(listing);
    }
    
    //---------------------------------------------------------------------------------------------------
    // Protein properties
    //---------------------------------------------------------------------------------------------------
    private static void assignProteinProperties(WIdPickerProtein wProt) {
        
        ProteinProperties props = ProteinPropertiesStore.getInstance().getProteinMolecularWtPi(wProt.getProtein().getProteinferId(), wProt.getProtein());
        if(props != null) {
            wProt.setMolecularWeight( (float) (Math.round(props.getMolecularWt()*100) / 100.0));
            wProt.setPi( (float) (Math.round(props.getPi()*100) / 100.0));
        }
    }
    
    //---------------------------------------------------------------------------------------------------
    // IDPicker input summary
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerInputSummary> getIDPickerInputSummary(int pinferId) {
        
        ProteinferRun run = piRunDao.loadProteinferRun(pinferId);
        List<IdPickerInput> inputSummary = inputDao.loadProteinferInputList(pinferId);
        List<WIdPickerInputSummary> wInputList = new ArrayList<WIdPickerInputSummary>(inputSummary.size());
        
        for(IdPickerInput input: inputSummary) {
            String filename = "";
            if(Program.isSearchProgram(run.getInputGenerator()))
                filename = rsDao.loadFilenameForRunSearch(input.getInputId());
            else if(Program.isAnalysisProgram(run.getInputGenerator()))
                filename = rsaDao.loadFilenameForRunSearchAnalysis(input.getInputId());
            else
                log.error("Unknown program type: "+run.getInputGenerator().name());
            
            WIdPickerInputSummary winput = new WIdPickerInputSummary(input);
            winput.setFileName(filename);
            wInputList.add(winput);
        }
        Collections.sort(wInputList, new Comparator<WIdPickerInputSummary>() {
            @Override
            public int compare(WIdPickerInputSummary o1,
                    WIdPickerInputSummary o2) {
                return o1.getFileName().compareTo(o2.getFileName());
            }});
        return wInputList;
    }
    
    public static int getUniquePeptideCount(int pinferId) {
        return idpPeptBaseDao.getUniquePeptideSequenceCountForRun(pinferId);
    }
    public static int getUniqueIonCount(int pinferId) {
    	return idpPeptBaseDao.getUniqueIonCountForRun(pinferId);
    }
    //---------------------------------------------------------------------------------------------------
    // IDPicker result summary
    //---------------------------------------------------------------------------------------------------
    public static WIdPickerResultSummary getIdPickerResultSummary(int pinferId, List<Integer> proteinIds) {
        
        long s = System.currentTimeMillis();
        
        WIdPickerResultSummary summary = new WIdPickerResultSummary();
        
        // TODO remove this later
        //if(pinferId > 256)
        summary.setHasSubsetInformation(true);
        
        // protein counts before filtering
        summary.setAllProteinCount(idpProtBaseDao.getProteinCount(pinferId));
        // parsimonious protein IDs
        List<Integer> parsimProteinIds = idpProtBaseDao.getIdPickerParsimoniousProteinIds(pinferId);
        // non-subset protein IDs
        List<Integer> nonSubsetProteinIds = idpProtBaseDao.getIdPickerNonSubsetProteinIds(pinferId);
        
        summary.setAllParsimoniousProteinCount(parsimProteinIds.size());
        summary.setAllNonSubsetProteinCount(nonSubsetProteinIds.size());
        
        summary.setAllProteinGroupCount(idpProtBaseDao.getIdPickerGroupCount(pinferId));
        summary.setAllParsimoniousProteinGroupCount(idpProtBaseDao.getIdPickerParsimoniousGroupCount(pinferId));
        summary.setAllNonSubsetProteinGroupCount(idpProtBaseDao.getIdPickerNonSubsetGroupCount(pinferId));
        
        // protein counts after filtering
        summary.setFilteredProteinCount(proteinIds.size());
        
        Map<Integer, Integer> protGroupMap = idpProtBaseDao.getProteinGroupLabels(pinferId);
        // group labels for filtered proteins
        Set<Integer> groupLabels = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        for(int id: proteinIds) {
            groupLabels.add(protGroupMap.get(id));
        }
        summary.setFilteredProteinGroupCount(groupLabels.size());
        
        // filtered parsimonious protein and protein group count
        groupLabels.clear();
        int filteredProteinCount = 0;
        Set<Integer> myIds = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        myIds.addAll(proteinIds);
        for(int id: parsimProteinIds) {
            if(myIds.contains(id))  {
                filteredProteinCount++;
                groupLabels.add(protGroupMap.get(id));
            }
        }
        summary.setFilteredParsimoniousProteinCount(filteredProteinCount);
        summary.setFilteredParsimoniousProteinGroupCount(groupLabels.size());
        
        // filtered non-subset protein and protein group count
        groupLabels.clear();
        filteredProteinCount = 0;
        for(int id: nonSubsetProteinIds) {
            if(myIds.contains(id))  {
                filteredProteinCount++;
                groupLabels.add(protGroupMap.get(id));
            }
        }
        summary.setFilteredNonSubsetProteinCount(filteredProteinCount);
        summary.setFilteredNonSubsetProteinGroupCount(groupLabels.size());
        
        long e = System.currentTimeMillis();
        log.info("Time to get WIdPickerResultSummary: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return summary;
    }

    //---------------------------------------------------------------------------------------------------
    // Cluster Ids in the given protein inference run
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getClusterLabels(int pinferId) {
        return idpProtBaseDao.getClusterLabels(pinferId);
    }

    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a indistinguishable protein group 
    // (sorted by sequence, modification state and charge)
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerIon> getPeptideIonsForProteinGroup(int pinferId, int pinferProteinGroupLabel) {
        
        // get the id of one of the proteins in the group. All proteins in a group match the same peptides
        int proteinId = idpProtBaseDao.getIdPickerGroupProteinIds(pinferId, pinferProteinGroupLabel).get(0);
        return getPeptideIonsForProtein(pinferId, proteinId);
    }
    
    public static List<WIdPickerIon> getPeptideIonsForProtein(int pinferId, int proteinId) {
        
        long s = System.currentTimeMillis();
        
        List<WIdPickerIon> ionList = new ArrayList<WIdPickerIon>();
        
        IdPickerRun run = idpRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        
        if(pinferProgram == ProteinInferenceProgram.PROTINFER_SEQ ||
           pinferProgram == ProteinInferenceProgram.PROTINFER_PLCID) {
            List<IdPickerPeptide> peptides = idpPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptide peptide: peptides) {
                List<IdPickerIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(IdPickerIon ion: ions) {
                    WIdPickerIon wIon = makeWIdPickerIon(ion, inputGenerator);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else if (pinferProgram == ProteinInferenceProgram.PROTINFER_PERC ||
        		 pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_PEPT ||
                pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_OLD) {
            List<IdPickerPeptideBase> peptides = idpPeptBaseDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptideBase peptide: peptides) {
                List<ProteinferIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinferIon ion: ions) {
                    WIdPickerIon wIon = makeWIdPickerIon(ion, inputGenerator);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Unknow version of IDPicker: "+pinferProgram);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get peptide ions for pinferID: "+pinferId+
                ", proteinId: "+proteinId+
                " -- "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return ionList;
    }

    private static <I extends GenericProteinferIon<? extends ProteinferSpectrumMatch>>
            WIdPickerIon makeWIdPickerIon(I ion, Program inputGenerator) {
        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
        MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
        // If this scan was processed with Bullseye it will have extra information in the scan headers.
        if(ms2ScanDao.isGeneratedByBullseye(origResult.getScanId())) {
            MS2Scan scan = ms2ScanDao.loadScanLite(origResult.getScanId());
            return new WIdPickerIon(ion, origResult, scan);
        }
        else {
            MsScan scan = scanDao.loadScanLite(origResult.getScanId());
            return new WIdPickerIon(ion, origResult, scan);
        }
    }

    private static void sortIonList(List<? extends GenericProteinferIon<?>> ions) {
        Collections.sort(ions, new Comparator<GenericProteinferIon<?>>() {
            public int compare(GenericProteinferIon<?> o1, GenericProteinferIon<?> o2) {
                if(o1.getModificationStateId() < o2.getModificationStateId())   return -1;
                if(o1.getModificationStateId() > o2.getModificationStateId())   return 1;
                if(o1.getCharge() < o2.getCharge())                             return -1;
                if(o2.getCharge() > o2.getCharge())                             return 1;
                return 0;
            }});
    }
    
    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a protein (sorted by sequence, modification state and charge)
    //---------------------------------------------------------------------------------------------------
    public static List<WIdPickerIonForProtein> getPeptideIonsWithTermResiduesForProtein(int pinferId, int proteinId) {
        
        long s = System.currentTimeMillis();
        
        List<WIdPickerIonForProtein> ionList = new ArrayList<WIdPickerIonForProtein>();
        
        IdPickerRun run = idpRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        ProteinferProtein protein = protDao.loadProtein(proteinId);
        String proteinSeq = getProteinSequence(protein);
        
        if(pinferProgram == ProteinInferenceProgram.PROTINFER_SEQ ||
           pinferProgram == ProteinInferenceProgram.PROTINFER_PLCID) {
            List<IdPickerPeptide> peptides = idpPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptide peptide: peptides) {
                List<Character>[] termResidues = getTerminalresidues(proteinSeq, peptide.getSequence());
                
                List<IdPickerIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(IdPickerIon ion: ions) {
                    WIdPickerIonForProtein wIon = makeWIdPickerIonForProtein(ion, inputGenerator);
                    for(int i = 0; i < termResidues[0].size(); i++) {
                        wIon.addTerminalResidues(termResidues[0].get(i), termResidues[1].get(i));
                    }
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else if (pinferProgram == ProteinInferenceProgram.PROTINFER_PERC ||
        		 pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_PEPT || 
                 pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_OLD) {
            List<IdPickerPeptideBase> peptides = idpPeptBaseDao.loadPeptidesForProteinferProtein(proteinId);
            for(IdPickerPeptideBase peptide: peptides) {
                List<Character>[] termResidues = getTerminalresidues(proteinSeq, peptide.getSequence());
                
                List<ProteinferIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinferIon ion: ions) {
                    WIdPickerIonForProtein wIon = makeWIdPickerIonForProtein(ion, inputGenerator);
                    for(int i = 0; i < termResidues[0].size(); i++) {
                        wIon.addTerminalResidues(termResidues[0].get(i), termResidues[1].get(i));
                    }
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Unknow version of IDPicker: "+pinferProgram);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get peptide ions (with ALL spectra) for pinferID: "+pinferId+
                " -- "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return ionList;
    }
    
    private static List<Character>[] getTerminalresidues(String proteinSeq,
            String sequence) {
        List<Character> nterm = new ArrayList<Character>(2);
        List<Character> cterm = new ArrayList<Character>(2);
        
        // Remove any '*' characters from the sequence
        proteinSeq = proteinSeq.replaceAll("\\*", "");
    	sequence = sequence.replaceAll("\\*", "");
    	
    	// This will substitute I and L with 1 so that we can look for matches
    	// with I/L substitutions.
        String seqWSubstitution = FastaInMemorySuffixCreator.format(sequence);
        String protSeqWSubstitution = FastaInMemorySuffixCreator.format(proteinSeq);
        
        int idx = protSeqWSubstitution.indexOf(seqWSubstitution);
        while(idx != -1) {
        	
        	// nterm residue
            if(idx == 0)    nterm.add('-');
            else            nterm.add(proteinSeq.charAt(idx-1));
            
            // cterm residue
            if(idx+seqWSubstitution.length() >= protSeqWSubstitution.length())
                cterm.add('-');
            else            
            	cterm.add(proteinSeq.charAt(idx+seqWSubstitution.length()));
            
            idx = protSeqWSubstitution.indexOf(seqWSubstitution, idx+seqWSubstitution.length());
        }
        return new List[]{nterm, cterm};
    }

    private static String getProteinSequence(ProteinferProtein protein) {
    	
    	return NrSeqLookupUtil.getProteinSequence(protein.getNrseqProteinId());
    }
    
    private static <I extends GenericProteinferIon<? extends ProteinferSpectrumMatch>>
            WIdPickerIonForProtein makeWIdPickerIonForProtein(I ion, Program inputGenerator) {
        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
        MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
        MsScan scan = scanDao.loadScanLite(origResult.getScanId());
        return new WIdPickerIonForProtein(ion, origResult, scan);
    }

    public static List<WIdPickerSpectrumMatch> getHitsForIon(int pinferIonId, Program inputGenerator, ProteinInferenceProgram pinferProgram) {
        
        List<? extends ProteinferSpectrumMatch> psmList = null;
        if(pinferProgram == ProteinInferenceProgram.PROTINFER_SEQ ||
           pinferProgram == ProteinInferenceProgram.PROTINFER_PLCID) {
            psmList = idpPsmDao.loadSpectrumMatchesForIon(pinferIonId);
        }
        else if (pinferProgram == ProteinInferenceProgram.PROTINFER_PERC ||
        		 pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_PEPT || 
                 pinferProgram == ProteinInferenceProgram.PROTINFER_PERC_OLD) {
            psmList = psmDao.loadSpectrumMatchesForIon(pinferIonId);
        }
        else {
            log.error("Unknow version of IDPicker: "+pinferProgram);
        }
        
        List<WIdPickerSpectrumMatch> wPsmList = new ArrayList<WIdPickerSpectrumMatch>(psmList.size());
        for(ProteinferSpectrumMatch psm: psmList) {
            MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
            WIdPickerSpectrumMatch wPsm = null;
            int scanId = origResult.getScanId();
            if(ms2ScanDao.isGeneratedByBullseye(scanId)) {
                MS2Scan scan = ms2ScanDao.load(scanId);
                wPsm = new WIdPickerSpectrumMatch(psm, origResult, scan);
            }
            else {
                MsScan scan = scanDao.load(origResult.getScanId());
                wPsm = new WIdPickerSpectrumMatch(psm, origResult, scan);
            }
            wPsmList.add(wPsm);
        }
        
        return wPsmList;
    }
    

    
    //---------------------------------------------------------------------------------------------------
    // Protein and Peptide groups for a cluster
    //--------------------------------------------------------------------------------------------------- 
    public static WIdPickerCluster getIdPickerCluster(int pinferId, int clusterLabel, 
            PeptideDefinition peptideDef) {
       
        List<Integer> protGroupLabels = idpProtBaseDao.getGroupLabelsForCluster(pinferId, clusterLabel);
        
        Map<Integer, WIdPickerProteinGroup> proteinGroups = new HashMap<Integer, WIdPickerProteinGroup>(protGroupLabels.size()*2);
        
        // map of peptide groupLabel and peptide group
        Map<Integer, WIdPickerPeptideGroup> peptideGroups = new HashMap<Integer, WIdPickerPeptideGroup>();
        
        // get a list of protein groups
        for(int protGrpLabel: protGroupLabels) {
            List<WIdPickerProtein> grpProteins = getGroupProteins(pinferId, protGrpLabel, peptideDef);
            WIdPickerProteinGroup grp = new WIdPickerProteinGroup(grpProteins);
            proteinGroups.put(protGrpLabel, grp);
            
            List<Integer> peptideGroupLabels =  idpPeptBaseDao.getMatchingPeptGroupLabels(pinferId, protGrpLabel);
            
            for(int peptGrpLabel: peptideGroupLabels) {
                WIdPickerPeptideGroup peptGrp = peptideGroups.get(peptGrpLabel);
                if(peptGrp == null) {
                    List<IdPickerPeptideBase> groupPeptides = idpPeptBaseDao.loadIdPickerGroupPeptides(pinferId, peptGrpLabel);
                    peptGrp = new WIdPickerPeptideGroup(groupPeptides);
                    peptideGroups.put(peptGrpLabel, peptGrp);
                }
                peptGrp.addMatchingProteinGroupLabel(protGrpLabel);
            }
        }
        
        for(WIdPickerPeptideGroup peptGrp: peptideGroups.values()) {
            List<Integer> protGrpLabels = peptGrp.getMatchingProteinGroupLabels();
            if(protGrpLabels.size() == 1) {
                proteinGroups.get(protGrpLabels.get(0)).addUniqPeptideGrpLabel(peptGrp.getPeptideGroupLabel());
            }
            else {
                for(int protGrpLabel: protGrpLabels)
                    proteinGroups.get(protGrpLabel).addNonUniqPeptideGrpLabel(peptGrp.getPeptideGroupLabel());
            }
        }
        
        WIdPickerCluster wCluster = new WIdPickerCluster(pinferId, clusterLabel);
        wCluster.setProteinGroups(new ArrayList<WIdPickerProteinGroup>(proteinGroups.values()));
        wCluster.setPeptideGroups(new ArrayList<WIdPickerPeptideGroup>(peptideGroups.values()));
        
        return wCluster;
    }
 
}
