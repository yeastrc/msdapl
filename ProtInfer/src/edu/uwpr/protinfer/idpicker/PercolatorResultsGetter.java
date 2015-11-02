package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatch;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatchingService;
import org.yeastrc.ms.service.database.fasta.PeptideProteinMatchingServiceException;

import edu.uwpr.protinfer.PeptideKeyCalculator;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.util.TimeUtils;

public class PercolatorResultsGetter implements ResultsGetter {

private static final Logger log = Logger.getLogger(IdPickerInputGetter.class);
    
    private static PercolatorResultsGetter instance = new PercolatorResultsGetter();
    
    private PercolatorResultsGetter() {}
    
    public static final PercolatorResultsGetter instance() {
        return instance;
    }
    
    @Override
    public List<PeptideSpectrumMatchIDP> getResults(IdPickerRun run, IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public List<PeptideSpectrumMatchIDP> getResults(List<IdPickerInput> inputList, Program inputGenerator, IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Returns a list of peptide spectrum matches which are filtered by relevant score(s)
     * and for min peptide length and 
     * ranked by relevant score(s) for each peptide (as defined in the PeptideDefinition). 
     * Ambiguous spectra are filtered.
     * @throws ResultGetterException 
     */
    @Override
    public  List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun run, IDPickerParams params) 
        throws ResultGetterException {
        
        return getResultsNoFdr(run.getInputList(), run.getInputGenerator(), params);
    }
    
    
    @Override
    public List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(List<IdPickerInput> inputList, Program inputGenerator,
            IDPickerParams params) throws ResultGetterException {
        
        long start = System.currentTimeMillis();

        PercolatorParams percParams = new PercolatorParams(params);
        
        // 1. Get ALL the PercolatorResults (filtered by score(s) and peptide length
        //    Ambiguous spectra are also removed
        List<PercolatorResult> allResults;
		try {
			allResults = getAllPercolatorResults(inputList, inputGenerator, percParams);
		} catch (ModifiedSequenceBuilderException e) {
			throw new ResultGetterException("Error while getting Percolator results", e);
		}

        // 2. Convert list of PercolatorResult to PeptideSpectrumMatchNoFDR
        //    Rank the results for each peptide.
        List<PeptideSpectrumMatchNoFDR> psmList;
		try {
			psmList = rankAndConvertResults(params, percParams, allResults);
		} catch (ModifiedSequenceBuilderException e) {
			throw new ResultGetterException("Error while converting and ranking Percolator results", e);
		}
        
        // 3. Get all the matching proteins
        if(params.isRefreshPeptideProteinMatches()) {
        	log.info("Refreshing peptide protein matches");
        	assignMatchingProteinsFromFasta(psmList, inputList.get(0).getProteinferId(), percParams);
        }
        else {
        	assignMatchingProteins(psmList);
        }
        
        
        long end = System.currentTimeMillis();
        log.info("Total time to get results: "+TimeUtils.timeElapsedMinutes(start, end)+" minutes \n");
        
        return psmList;
    }
    
    
    // Assign matching proteins to each peptide
    private void assignMatchingProteins(List<PeptideSpectrumMatchNoFDR> psmList) {
        
        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();
        
        long s = System.currentTimeMillis();
        // map of protein accession and protein
        Map<String, Protein> proteinMap = new HashMap<String, Protein>();
        
        for(PeptideSpectrumMatchNoFDR psm: psmList) {
            
            // read the matching proteins from the database now
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(psm.getSearchResultId());

            for (MsSearchResultProtein protein: msProteinList) {
                // we could have multiple accessions, keep the first one only
                String[] accessionStrings = protein.getAccession().split("\\cA");
            
                Protein prot = proteinMap.get(accessionStrings[0]);
                // If we have not already seen this protein create a new entry
                if(prot == null) {
                    prot = new Protein(accessionStrings[0], -1);
                    proteinMap.put(accessionStrings[0], prot);
                }
                psm.getPeptideHit().addProtein(prot);
            }
        }
        
        long e = System.currentTimeMillis();
        log.info("\tTime to get matching proteins: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
    }
    
    // Assign matching proteins to each peptide
    private void assignMatchingProteinsFromFasta(List<PeptideSpectrumMatchNoFDR> psmList, int pinferId, 
    		PercolatorParams percParams) throws ResultGetterException{
        
        
        long s = System.currentTimeMillis();
        
        PeptideProteinMatchingService matchService = initializePeptideProteinMatchingService(pinferId, percParams);
        
        // map of protein accession and protein
        Map<String, Protein> proteinMap = new HashMap<String, Protein>();
        
        // map of peptide sequence and protein matches
        Map<String, List<Protein>> peptideProteinMap = new HashMap<String, List<Protein>>();
        
        int cnt = 0;
        for(PeptideSpectrumMatchNoFDR psm: psmList) {
            
        	String peptide = psm.getPeptideSequence();
        	
        	List<Protein> protMatches = peptideProteinMap.get(peptide);
        	
        	if(protMatches == null) {
        		
        		List<PeptideProteinMatch> matches = null;
				try {
					matches = matchService.getMatchingProteins(peptide);
				} catch (PeptideProteinMatchingServiceException e1) {
					log.error("Error finding protein matches found for peptide: "+peptide);
            		throw new ResultGetterException("Error finding protein matches found for peptide: "+peptide, e1);
				}
            	
        		if(matches == null || matches.size() == 0) {
            		log.error("No protein matches found for peptide: "+peptide);
            		throw new ResultGetterException("No protein matches found for peptide: "+peptide+" "+matchService.getCriteria());
            	}
        		
        		protMatches = new ArrayList<Protein>(matches.size());
        		peptideProteinMap.put(peptide, protMatches);
        		
                for (PeptideProteinMatch protein: matches) {
                
                	String accession = protein.getProtein().getAccessionString();
                    Protein prot = proteinMap.get(accession);
                    // If we have not already seen this protein create a new entry
                    if(prot == null) {
                        prot = new Protein(accession, -1);
                        proteinMap.put(accession, prot);
                    }
                    protMatches.add(prot);
                    //psm.getPeptideHit().addProtein(prot);
                }
        	}
        	
        	for(Protein prot: protMatches) {
        		psm.getPeptideHit().addProtein(prot);
        	}
        	
        	cnt++;
        	if(cnt % 1000 == 0) {
        		log.info("Peptide to protein matches found for "+cnt+" PSMs");
        	}
        }
        
        matchService.clearMaps();
        
        long e = System.currentTimeMillis();
        log.info("\tTime to get matching proteins: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
    }
    
    private PeptideProteinMatchingService initializePeptideProteinMatchingService(int pinferId, 
    		PercolatorParams percParams) throws ResultGetterException{

    	// Get the database ID of the fasta file used for the search
        List<Integer> fastaDatabaseIds = getDatabaseIdsForProteinInference(pinferId);
        if(fastaDatabaseIds.size() != 1) {
        	log.error("Expected 1 fasta databaseID found: "+fastaDatabaseIds.size());
        	if(fastaDatabaseIds.size() == 0)
        		throw new ResultGetterException("No fasta database found for protein inference input. Protein inference ID: "+pinferId);
        	else
        		throw new ResultGetterException("Multiple fasta databases found for protein inference input. Protein inference ID: "+pinferId);
        }

    	ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
    	MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();

    	List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(pinferId);
    	if(searchIds.size() == 0) {
    		log.error("No search Ids found for protein inference ID: "+pinferId);
    		throw new ResultGetterException("No search Ids found for protein inference ID: "+pinferId);
    	}
    	else if(searchIds.size() > 1) {
    		log.warn("Multiple search Ids found for protein inference ID: "+pinferId);
    	}
         
    	// If we have multiple searches for this protein inference make sure they all have the 
    	// same enzyme and numEnzymaticTermini
        // get the search
        List<MsEnzyme> enzymes = getSearchEnzymes(searchIds, pinferId);
        
        int numEnzymaticTermini = getNumEnzymaticTermini(searchIds, pinferId);
        
        boolean clipNtermMet = getClipNtermMetionine(searchIds, pinferId);

        
        // Initialize the peptide protein matching service
        PeptideProteinMatchingService matchService = null;
        try {
			matchService = new PeptideProteinMatchingService(fastaDatabaseIds.get(0));
			
		} catch (PeptideProteinMatchingServiceException e1) {
			throw new ResultGetterException("Error initializing PeptideProteinMatchingService", e1);
		}

        matchService.setNumEnzymaticTermini(numEnzymaticTermini);
        matchService.setEnzymes(enzymes);
        
        matchService.setDoItoLSubstitution(percParams.getIdPickerParams().isDoItoLSubstitution());
        matchService.setRemoveAsterisks(percParams.getIdPickerParams().isRemoveAsterisks());
        matchService.setClipNtermMet(clipNtermMet);
        
        log.info("Initialized peptide protein matching service");
        return matchService;
    }
    
    private boolean getClipNtermMetionine(List<Integer> searchIds, int pinferId) throws ResultGetterException {
    	
    	MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
    	
    	
    	boolean clipNtermMet = false;
    	boolean first = true;
    	
    	for(int searchId: searchIds) {
    		
    		MsSearch msSearch = searchDao.loadSearch(searchId);
    		
    		boolean clip;
    		
    		if(msSearch.getSearchProgram() == Program.SEQUEST || msSearch.getSearchProgram() == Program.COMET) {
    			clip = DAOFactory.instance().getSequestSearchDAO().getClipNterMethionine(searchId);
            }
    		else
    			clip = false;
    		
    		if(!first) {
    			
    			if(clipNtermMet != clip)
    				throw new ResultGetterException("Value of clip_nterm_methionine was not the same for all searches. Protein inference ID: "+pinferId);
    		}
    		else {
    			first = false;
    			clipNtermMet = clip;
    		}
    	}
        
    	return clipNtermMet;
    }

    private int getNumEnzymaticTermini(List<Integer> searchIds, int pinferId) throws ResultGetterException {
    	
    	MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
    	
    	int numEnzymaticTermini = -1; 
    	
    	for(int searchId: searchIds) {
    		
    		MsSearch msSearch = searchDao.loadSearch(searchId);
    		
    		int net = -1;
    		
    		if(msSearch.getSearchProgram() == Program.SEQUEST || msSearch.getSearchProgram() == Program.COMET) {
    			net = DAOFactory.instance().getSequestSearchDAO().getNumEnzymaticTermini(searchId);
            }
            else if(msSearch.getSearchProgram() == Program.MASCOT)
            	net = DAOFactory.instance().getMascotSearchDAO().getNumEnzymaticTermini(searchId);
    		
            else if(msSearch.getSearchProgram() == Program.PROLUCID)
            	net = DAOFactory.instance().getProlucidSearchDAO().getSpecificity(searchId);
    		
            else {
            	throw new ResultGetterException("Unknown search program: "+msSearch.getSearchProgram()+". Protein inference ID: "+pinferId);
            }
    		
    		if(numEnzymaticTermini != -1) {
    			
    			if(numEnzymaticTermini != net)
    				throw new ResultGetterException("Number of enzymatic termini used for searches is not the same. Protein inference ID: "+pinferId);
    		}
    		else {
    			numEnzymaticTermini = net;
    		}
    		
    	}
        
    	return numEnzymaticTermini;
    }
    
    private List<MsEnzyme> getSearchEnzymes(List<Integer> searchIds, int pinferId) throws ResultGetterException {
    	
    	MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
    	
    	List<MsEnzyme> enzymes = null;
    	
    	
    	for(int searchId: searchIds) {
    		
    		MsSearch msSearch = searchDao.loadSearch(searchId);
            List<MsEnzyme> searchEnzList = msSearch.getEnzymeList();
            
            if(enzymes != null) {
            	
            	if(enzymes.size() != searchEnzList.size()) {
            		throw new ResultGetterException("Enzymes used for "+searchIds.size()+" searches do not match. Protein inference ID: "+pinferId);
            	}
            	
            	for(MsEnzyme enzyme: searchEnzList) {
            		
            		if(!enzymes.contains(enzyme)) {
            			throw new ResultGetterException("Enzyme "+enzyme.getName()+" used for searchID: "+searchId+" is not common to all searches. Protein inference ID: "+pinferId);
            		}
            	}
            	
            }
            else
            	enzymes = searchEnzList;
    	}
    	
    	return enzymes;
    }
    
    private List<Integer> getDatabaseIdsForProteinInference(int pinferId) {
        
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        
        List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(pinferId);
        if(searchIds.size() == 0) {
            log.error("No search Ids found for protein inference ID: "+pinferId);
        }
        
        Set<Integer> databaseIds = new HashSet<Integer>();
        for(int searchId: searchIds) {
            MsSearch search = searchDao.loadSearch(searchId);
            for(MsSearchDatabase db: search.getSearchDatabases()) {
                databaseIds.add(db.getSequenceDatabaseId());
            }
        }
        return new ArrayList<Integer>(databaseIds);
    }
    

    // Convert the list of PercolatorResult to a list of PeptideSpectrumMatchNoFDR 
    // Results are ranked for each peptide. 
    private List<PeptideSpectrumMatchNoFDR> rankAndConvertResults(IDPickerParams params, PercolatorParams percParams,
            List<PercolatorResult> allResults) throws ModifiedSequenceBuilderException {
        
        long s = System.currentTimeMillis();
        // Rank the Percolator results
        Map<Integer, Integer> resultRanks = rankResults(allResults, percParams);
        
        // make a list of peptide spectrum matches and read the matching proteins from the database
        PeptideDefinition peptideDef = params.getPeptideDefinition();
        
        // map of peptide_key and peptideHit
        Map<String, PeptideHit> peptideHitMap = new HashMap<String, PeptideHit>();
        
        // convert the list of PercolatorResult into a list of PeptideSpectrumMatchNoFDR objects
        List<PeptideSpectrumMatchNoFDR> psmList = new ArrayList<PeptideSpectrumMatchNoFDR>(allResults.size());
        Iterator<PercolatorResult> iter = allResults.iterator();
        while(iter.hasNext()) {

            PercolatorResult result = iter.next();
            
            // get the peptide key
            String peptideKey = PeptideKeyCalculator.getKey(result, peptideDef);
            
            PeptideHit peptHit = peptideHitMap.get(peptideKey);
            // If we haven't already seen this peptide, create a new entry
            if(peptHit == null) {
                Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), peptideKey, -1);
                peptHit = new PeptideHit(peptide);
                peptideHitMap.put(peptideKey, peptHit);
            }
            
            SpectrumMatchNoFDRImpl specMatch = new SpectrumMatchNoFDRImpl();
            specMatch.setResultId(result.getPercolatorResultId());
            specMatch.setSearchResultId(result.getId());
            specMatch.setScanId(result.getScanId());
            specMatch.setCharge(result.getCharge());
            specMatch.setSourceId(result.getRunSearchAnalysisId());
            specMatch.setModifiedSequence(result.getResultPeptide().getModifiedPeptide());
            specMatch.setRank(resultRanks.get(result.getId()));

            PeptideSpectrumMatchNoFDRImpl psm = new PeptideSpectrumMatchNoFDRImpl();
            psm.setPeptide(peptHit);
            psm.setSpectrumMatch(specMatch);

            psmList.add(psm);
            
            // remove the original result to free up space.
            result = null;
            iter.remove();
        }
        log.info("Number of ions seen: "+peptideHitMap.size());
        
        // free up unused maps
        allResults.clear();  allResults = null;
        resultRanks.clear(); resultRanks = null;
        peptideHitMap.clear(); peptideHitMap = null;
        
        long e = System.currentTimeMillis();
        log.info("\tTime to rank peptide spectra and create list of spectrum matches: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        return psmList;
    }

    // Returns a list of PercolatorResults for the given inputIds, filtered by relevant scores and 
    // min. peptide length.
    private List<PercolatorResult> getAllPercolatorResults(List<IdPickerInput> inputList, 
            Program inputGenerator,
            PercolatorParams percParams) throws ModifiedSequenceBuilderException {
        
        PercolatorResultDAO resultDao = DAOFactory.instance().getPercolatorResultDAO();
        
        List<PercolatorResult> allResults = new ArrayList<PercolatorResult>();
        
        Double psmQvalCutoff = percParams.hasPsmQvalueCutoff() ? percParams.getPsmQvalueCutoff() : null;
        Double peptideQvalCutoff = percParams.hasPeptideQvalueCutoff() ? percParams.getPeptideQvalueCutoff() : null;
        Double psmPepCutoff = percParams.hasPsmPepCutoff() ? percParams.getPsmPEPCutoff() : null;
        Double peptidePepCutoff = percParams.hasPeptidePepCutoff() ? percParams.getPeptidePEPCutoff() : null;
        Double psmDsCutoff = percParams.hasPsmDiscriminantScoreCutoff() ? percParams.getPsmDiscriminantScoreCutoff() : null;
        Double peptideDsCutoff = percParams.hasPeptideDiscriminantScoreCutoff() ? percParams.getPeptideDiscriminantScoreCutoff() : null;
        
        if(percParams.isUsePeptideLevelScores()) {
            log.info("Thresholds -- PEPTIDE qvalue: "+peptideQvalCutoff+"; pep: "+peptidePepCutoff+" ds: "+peptideDsCutoff);
        }
        log.info("Thresholds -- PSM qvalue: "+psmQvalCutoff+"; pep: "+psmPepCutoff+" ds: "+psmDsCutoff);
        
        // first get all the results; remove all hits to small peptides; results will be filtered by relevant scores.
        for(IdPickerInput input: inputList) {
            
            int inputId = input.getInputId();
            
            log.info("Loading Percolator results for runSearchAnalysisID: "+inputId);

            long s = System.currentTimeMillis();
            
            List<PercolatorResult> resultList = null;
            if(!percParams.isUsePeptideLevelScores()) {
            	log.info("Filtering on PSM scores");
            	resultList = resultDao.loadTopPercolatorResultsN(inputId, 
                                            psmQvalCutoff, 
                                            psmPepCutoff, 
                                            psmDsCutoff,
                                            true); // get the dynamic residue mods
            }
            else {
            	log.info("Filtering on peptide scores");
            	PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
            	if(percParams.isUsePsmLevelScores())
            		resultList = peptResDao.loadPercolatorPsms(inputId, 
            				peptideQvalCutoff, peptidePepCutoff, peptideDsCutoff, 
            				psmQvalCutoff, psmPepCutoff, psmDsCutoff);
            	else
            		resultList = peptResDao.loadPercolatorPsms(inputId, peptideQvalCutoff, peptidePepCutoff, peptideDsCutoff);
            }
            
            log.info("\tTotal hits that pass score thresholds for runSearchAnalysisID "+inputId+": "+resultList.size());
            long e = System.currentTimeMillis();

            log.info("\tTime: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        
            // Remove search hits to small peptides
            removeSmallPeptides(resultList, percParams.getIdPickerParams().getMinPeptideLength());
            
            
            // We are not going to calculate FDR so we remove all spectra with multiple results at this point
            // Our search results should already be filtered at this point
            if(percParams.getIdPickerParams().isRemoveAmbiguousSpectra()) {
                removeSpectraWithMultipleResults(resultList);
            }
            
            allResults.addAll(resultList);
            
            input.setNumFilteredTargetHits(resultList.size());
            input.setNumTargetHits(IdPickerInputGetter.instance().getUnfilteredInputCount(inputId, inputGenerator));
        }
        return allResults;
    }
    
    
    protected void removeSpectraWithMultipleResults(List<PercolatorResult> psmList) throws ModifiedSequenceBuilderException {
        AmbiguousSpectraFilter specFilter = AmbiguousSpectraFilter.instance();
        specFilter.filterSpectraWithMultipleResults(psmList);
    }


    private void removeSmallPeptides(List<PercolatorResult> resultList, int minPeptideLength) {

        log.info("Removing search hits with peptide length < "+minPeptideLength);
        Iterator<PercolatorResult> iter = resultList.iterator();
        int removed = 0;
        while(iter.hasNext()) {
            PercolatorResult res = iter.next();
            // if the length of the peptide is less than the required threshold do not add it to the final list
            if(res.getResultPeptide().getPeptideSequence().length() < minPeptideLength) {
                iter.remove();
                removed++;
            }
        }
        log.info("\tRemoved "+removed+" spectra. Remaining spectra: "+resultList.size());
    }
    
    /**
     * PSM's are ranked for a peptide sequence (regardless of peptide definition).
     * @param resultList
     * @param percParams
     * @return
     */
	private Map<Integer, Integer> rankResults(List<PercolatorResult> resultList, PercolatorParams percParams) {
        
        PercolatorResultsRanker resultRanker = PercolatorResultsRanker.instance();
        return resultRanker.rankResultsByPeptide(resultList, percParams.hasPsmQvalueCutoff(),
                percParams.hasPsmPepCutoff(), percParams.hasPsmDiscriminantScoreCutoff());
    }
    
}
