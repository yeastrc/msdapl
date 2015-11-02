/**
 * ProteinProphetResultsLoader.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferInputDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetPeptideDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinGroupDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptide;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProteinPeptideIon;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.database.fasta.FastaInMemorySuffixCreator;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.nr_seq.listing.ProteinListingBuilder;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.proteinfer.MsResultLoader;
import org.yeastrc.www.proteinfer.ProteinAccessionFilter;
import org.yeastrc.www.proteinfer.ProteinCommonNameFilter;
import org.yeastrc.www.proteinfer.ProteinDescriptionFilter;
import org.yeastrc.www.proteinfer.ProteinGoTermsFilter;
import org.yeastrc.www.proteinfer.ProteinProperties;
import org.yeastrc.www.proteinfer.ProteinPropertiesFilter;
import org.yeastrc.www.proteinfer.ProteinPropertiesStore;

/**
 * 
 */
public class ProteinProphetResultsLoader {

    private static final ProteinferDAOFactory pinferDaoFactory = ProteinferDAOFactory.instance();
    private static final org.yeastrc.ms.dao.DAOFactory msDataDaoFactory = org.yeastrc.ms.dao.DAOFactory.instance();
    private static final MsScanDAO scanDao = msDataDaoFactory.getMsScanDAO();
    private static final MsRunSearchDAO rsDao = msDataDaoFactory.getMsRunSearchDAO();
    private static final MsRunSearchAnalysisDAO rsaDao = msDataDaoFactory.getMsRunSearchAnalysisDAO();
    
    private static final MsResultLoader resLoader = MsResultLoader.getInstance();
    
    private static final ProteinferSpectrumMatchDAO psmDao = pinferDaoFactory.getProteinferSpectrumMatchDao();
    private static final ProteinferPeptideDAO peptDao = pinferDaoFactory.getProteinferPeptideDao();
    private static final ProteinProphetProteinDAO ppProtDao = pinferDaoFactory.getProteinProphetProteinDao();
    private static final ProteinProphetProteinGroupDAO ppProtGrpDao = pinferDaoFactory.getProteinProphetProteinGroupDao();
    private static final ProteinProphetPeptideDAO ppPeptDao = pinferDaoFactory.getProteinProphetPeptideDao();
    private static final ProteinferProteinDAO protDao = pinferDaoFactory.getProteinferProteinDao();
    private static final ProteinferInputDAO inputDao = pinferDaoFactory.getProteinferInputDao();
    private static final ProteinferRunDAO ppRunDao = pinferDaoFactory.getProteinferRunDao();
    
    private static final Logger log = Logger.getLogger(ProteinProphetResultsLoader.class);
    
    private ProteinProphetResultsLoader(){}
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given filtering criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getProteinIds(int pinferId, ProteinProphetFilterCriteria filterCriteria) {
    	
        long start = System.currentTimeMillis();
        List<Integer> proteinIds = ppProtDao.getFilteredSortedProteinIds(pinferId, filterCriteria);
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
        
        // apply sorting if needed
        long s = System.currentTimeMillis();
        if(filterCriteria.getSortBy() == SORT_BY.ACCESSION) {
        	proteinIds = ProphetProteinPropertiesSorter.sortIdsByAccession(proteinIds, pinferId,
        			filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
        	long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs by accession: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        }
        
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.PI) {
            proteinIds = ProphetProteinPropertiesSorter.sortIdsByPi(proteinIds, pinferId, 
            		filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
            long e = System.currentTimeMillis();
        	log.info("Time for resorting filtered IDs by pI: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        }
        
        // apply sorting if needed
        if(filterCriteria.getSortBy() == SORT_BY.MOL_WT) {
            proteinIds = ProphetProteinPropertiesSorter.sortIdsByMolecularWt(proteinIds, pinferId, 
            		filterCriteria.isGroupProteins(), filterCriteria.getSortOrder());
        	long e = System.currentTimeMillis();
        	log.info("Time for resorting filtered IDs by Mol. Wt.: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        }
        
        
        long e = System.currentTimeMillis();
        log.info("Time: "+TimeUtils.timeElapsedSeconds(start, e)+" seconds");
        return proteinIds;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get all the proteins in a indistinguishable protein group
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetProtein> getGroupProteins(int pinferId, int groupId, 
            PeptideDefinition peptideDef) {
        
        long s = System.currentTimeMillis();
        
        List<ProteinProphetProtein> groupProteins = ppProtDao.loadProteinProphetIndistinguishableGroupProteins(pinferId, groupId);
        
        List<WProteinProphetProtein> proteins = new ArrayList<WProteinProphetProtein>(groupProteins.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        for(ProteinProphetProtein prot: groupProteins) {
            prot.setPeptideDefinition(peptideDef);
            proteins.add(getWProteinProphetProtein(prot, fastaDatabaseIds));
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get proteins in a group: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get subsuming proteins for a protein
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetProtein> getSubsumingProteins(int piProteinId, int pinferId) {
        
        long s = System.currentTimeMillis();
        
        
        List<Integer> ids = ppProtDao.getSubsumingProteinIds(piProteinId);
        if(ids.size() == 0)
        	return new ArrayList<WProteinProphetProtein>(0);
        
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        List<WProteinProphetProtein> proteins = new ArrayList<WProteinProphetProtein>(ids.size());
        for(int id: ids) {
        	ProteinProphetProtein protein = ppProtDao.loadProtein(id);
        	protein.setPeptideDefinition(new PeptideDefinition());
            proteins.add(getWProteinProphetProtein(protein, fastaDatabaseIds));
        }
        
        
        long e = System.currentTimeMillis();
        log.info("Time to get SUBSUMING proteins for piProtenID: "+piProteinId+" was "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get subsumed proteins for a protein
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetProtein> getSubsumedProteins(int piProteinId, int pinferId) {
        
        long s = System.currentTimeMillis();
        
        
        List<Integer> ids = ppProtDao.getSubsumedProteinIds(piProteinId);
        if(ids.size() == 0)
        	return new ArrayList<WProteinProphetProtein>(0);
        
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        List<WProteinProphetProtein> proteins = new ArrayList<WProteinProphetProtein>(ids.size());
        for(int id: ids) {
        	ProteinProphetProtein protein = ppProtDao.loadProtein(id);
        	protein.setPeptideDefinition(new PeptideDefinition());
            proteins.add(getWProteinProphetProtein(protein, fastaDatabaseIds));
        }
        
        
        long e = System.currentTimeMillis();
        log.info("Time to get SUBSUMED proteins for piProtenID: "+piProteinId+" was "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return proteins;
    }
    
    //---------------------------------------------------------------------------------------------------
    // Get a list of protein groups
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getPageSublist(List<Integer> allProteinIds, int[] pageIndices, 
    		boolean completeProphetGroups, boolean descending) {
    	
    	int firstIndex = descending ? pageIndices[1] : pageIndices[0];
    	int lastIndex = descending ? pageIndices[0] : pageIndices[1];
    	
//    	if(completeProphetGroups) {
//    		firstIndex = getStartIndexToCompleteFirstProphetGroup(allProteinIds, firstIndex);
//    		lastIndex = getEndIndexToCompleteFirstProphetGroup(allProteinIds, lastIndex);
//    	}
//    	else {
    		firstIndex = getStartIndexToCompleteIGroup(allProteinIds, firstIndex);
    		lastIndex = getEndIndexToCompleteIGroup(allProteinIds, lastIndex);
//    	}
    	
        	
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
    
    private static int getStartIndexToCompleteProphetGroup(List<Integer> allProteinIds, int startIndex) {
    	
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
    	ProteinProphetProtein protein = ppProtDao.loadProtein(allProteinIds.get(startIndex));
    	int prophetGroupId = protein.getProteinProphetGroupId();
    	int idx = startIndex - 1;
    	while(idx >= 0) {
    		protein = ppProtDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getProteinProphetGroupId() != prophetGroupId) {
    			idx = idx+1;
    			break;
    		}
    		idx--;
    	}
    	if(idx < 0)
    		idx = 0;
    	return idx;
    }
    
    private static int getEndIndexToCompleteProphetGroup(List<Integer> allProteinIds, int endIndex) {
    	
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
    	ProteinProphetProtein protein = ppProtDao.loadProtein(allProteinIds.get(endIndex));
    	int prophetGroupId = protein.getProteinProphetGroupId();
    	int idx = endIndex + 1;
    	while(idx < allProteinIds.size()) {
    		protein = ppProtDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getProteinProphetGroupId() != prophetGroupId) {
    			idx = idx-1;
    			break;
    		}
    		idx++;
    	}
    	if (idx >= allProteinIds.size())
    		idx--;
    	return idx;
    }
    
    private static int getStartIndexToCompleteIGroup(List<Integer> allProteinIds, int startIndex) {
    	
    	if(startIndex == 0)
    		return startIndex;
    	if(startIndex < 0) {
    		log.error("startIndex < 0 in getStartIndexToCompleteIGroup");
    		throw new IllegalArgumentException("startIndex < 0 in getStartIndexToCompleteIGroup");
    	}
    	if(startIndex >= allProteinIds.size()) {
    		log.error("startIndex >= list size in getStartIndexToCompleteIGroup");
    		throw new IllegalArgumentException("startIndex >= list size in getStartIndexToCompleteIGroup");
    	}
    	ProteinProphetProtein protein = ppProtDao.loadProtein(allProteinIds.get(startIndex));
    	int groupId = protein.getGroupId();
    	int idx = startIndex - 1;
    	while(idx >= 0) {
    		protein = ppProtDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getGroupId() != groupId) {
    			idx = idx+1;
    			break;
    		}
    		idx--;
    	}
    	if(idx < 0)
    		idx = 0;
    	return idx;
    }
    
    private static int getEndIndexToCompleteIGroup(List<Integer> allProteinIds, int endIndex) {
    	
    	if(endIndex == allProteinIds.size() - 1)
    		return endIndex;
    	if(endIndex >= allProteinIds.size()) {
    		log.error("endIndex >= list size in getEndIndexToCompleteIGroup");
    		throw new IllegalArgumentException("endIndex >= list size in getEndIndexToCompleteIGroup");
    	}
    	if(endIndex < 0) {
    		log.error("endInded < 0 in getEndIndexToCompleteIGroup");
    		throw new IllegalArgumentException("endInded < 0 in getEndIndexToCompleteIGroup");
    	}
    	ProteinProphetProtein protein = ppProtDao.loadProtein(allProteinIds.get(endIndex));
    	int groupId = protein.getGroupId();
    	int idx = endIndex + 1;
    	while(idx < allProteinIds.size()) {
    		protein = ppProtDao.loadProtein(allProteinIds.get(idx));
    		if(protein.getGroupId() != groupId) {
    			idx = idx-1;
    			break;
    		}
    		idx++;
    	}
    	if (idx >= allProteinIds.size())
    		idx--;
    	return idx;
    }
    
    public static List<WProteinProphetProteinGroup> getProteinProphetGroups(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        
        long s = System.currentTimeMillis();
        List<WProteinProphetProtein> proteins = getProteins(pinferId, proteinIds, peptideDef);
        
        if(proteins.size() == 0) {
            return new ArrayList<WProteinProphetProteinGroup>(0);
        }
       
        int currGrpId = -1;
        List<WProteinProphetProtein> prophetGrpProteins = null;
        List<WProteinProphetProteinGroup> prophetGrps = new ArrayList<WProteinProphetProteinGroup>();
        
        for(WProteinProphetProtein prot: proteins) {
            if(prot.getProtein().getProteinProphetGroupId() != currGrpId) {
                if(prophetGrpProteins != null && prophetGrpProteins.size() > 0) {
                    ProteinProphetGroup prophetGroup = ppProtGrpDao.load(currGrpId);
                    
                    List<WProteinProphetIndistProteinGroup> indistGrps = makeIndistinguishableGroups(prophetGrpProteins);
                    WProteinProphetProteinGroup grp = new WProteinProphetProteinGroup(prophetGroup, indistGrps);
                    prophetGrps.add(grp);
                }
                currGrpId = prot.getProtein().getProteinProphetGroupId();
                prophetGrpProteins = new ArrayList<WProteinProphetProtein>();
            }
            prophetGrpProteins.add(prot);
        }
        if(prophetGrpProteins != null && prophetGrpProteins.size() > 0) {
            ProteinProphetGroup prophetGroup = ppProtGrpDao.load(currGrpId);
            
            List<WProteinProphetIndistProteinGroup> indistGrps = makeIndistinguishableGroups(prophetGrpProteins);
            WProteinProphetProteinGroup grp = new WProteinProphetProteinGroup(prophetGroup, indistGrps);
            prophetGrps.add(grp);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get WProteinProphetProteinsGroups: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return prophetGrps;
    }
    
    private static List<WProteinProphetIndistProteinGroup> makeIndistinguishableGroups(
            List<WProteinProphetProtein> prophetGrpProteins) {
        
//        // sort by indistinguishable protein group ID
//        Collections.sort(prophetGrpProteins, new Comparator<WProteinProphetProtein>() {
//            @Override
//            public int compare(WProteinProphetProtein o1, WProteinProphetProtein o2) {
//                return Integer.valueOf(o1.getProtein().getGroupId()).compareTo(o2.getProtein().getGroupId());
//            }});
        
        List<WProteinProphetIndistProteinGroup> indistGrps = new ArrayList<WProteinProphetIndistProteinGroup>();
        int currGrpId = -1;
        List<WProteinProphetProtein> iGrpProteins = null;
        for(WProteinProphetProtein prot: prophetGrpProteins) {
            if(prot.getProtein().getGroupId() != currGrpId) {
                if(iGrpProteins != null && iGrpProteins.size() > 0) {
                    
                    WProteinProphetIndistProteinGroup grp = new WProteinProphetIndistProteinGroup(iGrpProteins);
                    indistGrps.add(grp);
                }
                currGrpId = prot.getProtein().getGroupId();
                iGrpProteins = new ArrayList<WProteinProphetProtein>();
            }
            iGrpProteins.add(prot);
        }
        if(iGrpProteins != null && iGrpProteins.size() > 0) {
            
            WProteinProphetIndistProteinGroup grp = new WProteinProphetIndistProteinGroup(iGrpProteins);
            indistGrps.add(grp);
        }
        return indistGrps;
    }

    //---------------------------------------------------------------------------------------------------
    // Get a list of proteins
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetProtein> getProteins(int pinferId, List<Integer> proteinIds, 
            PeptideDefinition peptideDef) {
        long s = System.currentTimeMillis();
        List<WProteinProphetProtein> proteins = new ArrayList<WProteinProphetProtein>(proteinIds.size());
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        for(int id: proteinIds) 
            proteins.add(getWProteinProphetProtein(id, peptideDef, fastaDatabaseIds));
        long e = System.currentTimeMillis();
        log.info("Time to get WProteinProphetProteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return proteins;
    }
    
    public static WProteinProphetProtein getWProteinProphetProtein(int pinferProteinId, 
            PeptideDefinition peptideDef, List<Integer> databaseIds) {
        ProteinProphetProtein protein = ppProtDao.loadProtein(pinferProteinId);
        protein.setPeptideDefinition(peptideDef);
        return getWProteinProphetProtein(protein, databaseIds);
    }
    
    public static WProteinProphetProtein getWProteinProphetProtein(int pinferId, int pinferProteinId, 
            PeptideDefinition peptideDef) {
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
       return getWProteinProphetProtein(pinferProteinId, peptideDef, fastaDatabaseIds);
    }
    
    private static WProteinProphetProtein getWProteinProphetProtein(ProteinProphetProtein protein, List<Integer> databaseIds) {
        WProteinProphetProtein wProt = new WProteinProphetProtein(protein);
        // set the accession and description for the proteins.  
        // This requires querying the NRSEQ database
        assignProteinAccessionDescription(wProt, databaseIds);
        

        // get the molecular weight for the protein
        assignProteinProperties(wProt);
        return wProt;
    }
    
    //---------------------------------------------------------------------------------------------------
    // NR_SEQ lookup 
    //---------------------------------------------------------------------------------------------------
    private static void assignProteinAccessionDescription(WProteinProphetProtein wProt, List<Integer> databaseIds) {
        
    	ProteinListing listing = ProteinListingBuilder.getInstance().build(wProt.getProtein().getNrseqProteinId(), databaseIds);
    	wProt.setProteinListing(listing);
    }
    
    private static void assignProteinProperties(WProteinProphetProtein wProt) {
        
    	 ProteinProperties props = ProteinPropertiesStore.getInstance().getProteinMolecularWtPi(wProt.getProtein().getProteinferId(), wProt.getProtein());
         if(props != null) {
             wProt.setMolecularWeight( (float) (Math.round(props.getMolecularWt()*100) / 100.0));
             wProt.setPi( (float) (Math.round(props.getPi()*100) / 100.0));
         }
    }
    
    //---------------------------------------------------------------------------------------------------
    // Protein Prophet result summary
    //---------------------------------------------------------------------------------------------------
    public static WProteinProphetResultSummary getProteinProphetResultSummary(int pinferId, List<Integer> proteinIds) {
        
        long s = System.currentTimeMillis();
        
        WProteinProphetResultSummary summary = new WProteinProphetResultSummary();
        
        
        // protein counts before filtering
        summary.setAllProteinCount(ppProtDao.getProteinCount(pinferId));
        
        // parsimonious (non-subset) protein IDs before filtering
        List<Integer> parsimProteinIds = ppProtDao.getProteinferProteinIds(pinferId,true);
        summary.setAllParsimoniousProteinCount(parsimProteinIds.size());
        
        // ProteinProphet group count before filtering
        int allProphetGroupCount = ppProtDao.getProteinProphetGroupCount(pinferId, false);
        summary.setAllProteinProphetGroupCount(allProphetGroupCount);
        
        // Parsimonious (non-subset) ProteinProphet group count before filtering
        int allParsmimProphetGroupCount = ppProtDao.getProteinProphetGroupCount(pinferId, true);
        summary.setAllParsimoniousProteinProphetGroupCount(allParsmimProphetGroupCount);
        
        // Indistinguishable group count before filtering
        int allIndGroupCount = ppProtDao.getIndistinguishableGroupCount(pinferId, false);
        summary.setAllProteinGroupCount(allIndGroupCount);
        
        // Parsimonious (non-subset) Indistinguishable group count before filtering
        int allParsimIndGroupCount = ppProtDao.getIndistinguishableGroupCount(pinferId, true);
        summary.setAllParsimoniousProteinGroupCount(allParsimIndGroupCount);
        
        
        // protein counts after filtering
        summary.setFilteredProteinCount(proteinIds.size());
        
        Map<Integer, Integer> protGroupMap = ppProtDao.getProteinGroupIds(pinferId, false);
        Map<Integer, Integer> protProphetGroupMap = ppProtDao.getProteinProphetGroupIds(pinferId, false);
        
        
        Set<Integer> groupIds = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        Set<Integer> prophetGroupIds = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        
        for(int id: proteinIds) {
            groupIds.add(protGroupMap.get(id));
            prophetGroupIds.add(protProphetGroupMap.get(id));
        }
        summary.setFilteredProteinGroupCount(groupIds.size());
        summary.setFilteredProphetGroupCount(prophetGroupIds.size());
        
        groupIds.clear();
        prophetGroupIds.clear();
        
        int parsimCount = 0;
        Set<Integer> myIds = new HashSet<Integer>((int) (proteinIds.size() * 1.5));
        myIds.addAll(proteinIds);
        for(int id: parsimProteinIds) {
            if(myIds.contains(id))  {
                parsimCount++;
                groupIds.add(protGroupMap.get(id));
                prophetGroupIds.add(protProphetGroupMap.get(id));
            }
        }
        summary.setFilteredParsimoniousProteinCount(parsimCount);
        summary.setFilteredParsimoniousProteinGroupCount(groupIds.size());
        summary.setFilteredParsimoniousProphetGroupCount(prophetGroupIds.size());
        
        long e = System.currentTimeMillis();
        log.info("Time to get WProteinProphetResultSummary: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        return summary;
    }
    
    //---------------------------------------------------------------------------------------------------
    // PEPTIDE COUNT
    //---------------------------------------------------------------------------------------------------
    public static int getUniquePeptideCount(int pinferId) {
        return peptDao.getUniquePeptideSequenceCountForRun(pinferId);
    }
    
    public static int getUniqueIonCount(int pinferId) {
        return peptDao.getUniqueIonCountForRun(pinferId);
    }
    
    //---------------------------------------------------------------------------------------------------
    // Peptide ions for a indistinguishable protein group 
    // (sorted by sequence, modification state and charge)
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetIon> getPeptideIonsForProteinGroup(int pinferId, int pinferProteinGroupId) {
        
        long s = System.currentTimeMillis();
        
        List<WProteinProphetIon> ionList = new ArrayList<WProteinProphetIon>();
        
        // get the id of one of the proteins in the indistinguishable group. 
        // All proteins in a group match the same peptides
        int proteinId = ppProtDao.getProteinProphetIndistinguishableGroupProteinIds(pinferId, pinferProteinGroupId).get(0);
        
        ProteinferRun run = ppRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        
        if(pinferProgram == ProteinInferenceProgram.PROTEIN_PROPHET) {
            List<ProteinProphetProteinPeptide> peptides = ppPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(ProteinProphetProteinPeptide peptide: peptides) {
                List<ProteinProphetProteinPeptideIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinProphetProteinPeptideIon ion: ions) {
                    WProteinProphetIon wIon = makeWProteinProphetIon(ion, inputGenerator);
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Invalid program for the action: "+pinferProgram);
        }
        
        long e = System.currentTimeMillis();
        log.info("Time to get peptide ions for pinferID: "+pinferId+
                ", proteinGroupID: "+pinferProteinGroupId+
                " -- "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        return ionList;
    }
    
    private static WProteinProphetIon makeWProteinProphetIon(ProteinProphetProteinPeptideIon ion, Program inputGenerator) {
        
        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
        MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
        if(origResult == null) {
        	log.error("No result found for psm: "+psm.getId());
        }
        MsScan scan = scanDao.loadScanLite(origResult.getScanId());
        return new WProteinProphetIon(ion, origResult, scan);
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
    // Peptide ions for a protein (sorted by sequence, modification state and charge
    //---------------------------------------------------------------------------------------------------
    public static List<WProteinProphetIon> getPeptideIonsForProtein(int pinferId, int proteinId) {
        
        long s = System.currentTimeMillis();
        
        List<WProteinProphetIon> ionList = new ArrayList<WProteinProphetIon>();
        
        ProteinferRun run = ppRunDao.loadProteinferRun(pinferId);
        
        // Get the program used to generate the input for protein inference
        Program inputGenerator = run.getInputGenerator();
        
        // Get the protein inference program used
        ProteinInferenceProgram pinferProgram = run.getProgram();
        
        ProteinferProtein protein = protDao.loadProtein(proteinId);
        String proteinSeq = getProteinSequence(protein);
        
        if (pinferProgram == ProteinInferenceProgram.PROTEIN_PROPHET) {
            List<ProteinProphetProteinPeptide> peptides = ppPeptDao.loadPeptidesForProteinferProtein(proteinId);
            for(ProteinProphetProteinPeptide peptide: peptides) {
                List<Character>[] termResidues = getTerminalresidues(proteinSeq, peptide.getSequence());
                
                List<ProteinProphetProteinPeptideIon> ions = peptide.getIonList();
                sortIonList(ions);
                for(ProteinProphetProteinPeptideIon ion: ions) {
                    WProteinProphetIon wIon = makeWProteinProphetIon(ion, inputGenerator);
                    for(int i = 0; i < termResidues[0].size(); i++) {
                        wIon.addTerminalResidues(termResidues[0].get(i), termResidues[1].get(i));
                    }
                    wIon.setIsUniqueToProteinGroup(peptide.isUniqueToProtein());
                    ionList.add(wIon);
                }
            }
        }
        else {
            log.error("Unknow version of protein inference program: "+pinferProgram);
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
        
        
        int idx = proteinSeq.indexOf(sequence);
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
    

    //---------------------------------------------------------------------------------------------------
    // Get a list of protein IDs with the given sorting criteria
    //---------------------------------------------------------------------------------------------------
    public static List<Integer> getSortedProteinIds(int pinferId, PeptideDefinition peptideDef, 
            List<Integer> proteinIds, SORT_BY sortBy, SORT_ORDER sortOrder, boolean groupProteins) {
        
        long s = System.currentTimeMillis();
        List<Integer> allIds = null;
        if(sortBy == SORT_BY.ACCESSION) {
        	List<Integer> sortedIds = ProphetProteinPropertiesSorter.sortIdsByAccession(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        if(sortBy == SORT_BY.MOL_WT) {
        	List<Integer> sortedIds = ProphetProteinPropertiesSorter.sortIdsByMolecularWt(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        if(sortBy == SORT_BY.PI) {
        	List<Integer> sortedIds = ProphetProteinPropertiesSorter.sortIdsByPi(proteinIds, pinferId, groupProteins, sortOrder);
            long e = System.currentTimeMillis();
            log.info("Time for resorting filtered IDs: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            return sortedIds;
        }
        
        else if (sortBy == SORT_BY.PROTEIN_PROPHET_GROUP) {
            allIds = ppProtDao.sortProteinIdsByProteinProphetGroup(pinferId);
        }
        else if (sortBy == SORT_BY.COVERAGE) {
            allIds = ppProtDao.sortProteinIdsByCoverage(pinferId, groupProteins, sortOrder);
        }
        else if(sortBy == SORT_BY.VALIDATION_STATUS) {
            allIds = ppProtDao.sortProteinIdsByValidationStatus(pinferId);
        }
        else if (sortBy == SORT_BY.NUM_PEPT) {
            allIds = ppProtDao.sortProteinIdsByPeptideCount(pinferId, peptideDef, groupProteins, sortOrder);
        }
        else if (sortBy == SORT_BY.NUM_UNIQ_PEPT) {
            allIds = ppProtDao.sortProteinIdsByUniquePeptideCount(pinferId, peptideDef, groupProteins, sortOrder);
        }
        else if (sortBy == SORT_BY.NUM_SPECTRA) {
            allIds = ppProtDao.sortProteinIdsBySpectrumCount(pinferId, groupProteins, sortOrder);
        }
        else if (sortBy == SORT_BY.PROBABILITY_GRP) {
            allIds = ppProtDao.sortProteinIdsByProbability(pinferId, true);
        }
        else if (sortBy == SORT_BY.PROBABILITY_PROT) {
            allIds = ppProtDao.sortProteinIdsByProbability(pinferId, false);
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
    // Get a list PSMs for a ion
    //---------------------------------------------------------------------------------------------------
    public static List<ProteinProphetSpectrumMatch> getHitsForIon(int pinferIonId, Program inputGenerator) {
        
        List<? extends ProteinferSpectrumMatch> psmList = psmDao.loadSpectrumMatchesForIon(pinferIonId);
        
        List<ProteinProphetSpectrumMatch> wPsmList = new ArrayList<ProteinProphetSpectrumMatch>(psmList.size());
        for(ProteinferSpectrumMatch psm: psmList) {
            MsSearchResult origResult = resLoader.getResult(psm.getResultId(), inputGenerator);
            ProteinProphetSpectrumMatch wPsm = null;
            MsScan scan = scanDao.load(origResult.getScanId());
            wPsm = new ProteinProphetSpectrumMatch(psm, origResult, scan);
            wPsmList.add(wPsm);
        }
        
        return wPsmList;
    }
}
