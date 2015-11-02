package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsSearchResultProteinDAO;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

import edu.uwpr.protinfer.PeptideKeyCalculator;
import edu.uwpr.protinfer.ProgramParam.SCORE;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideHit;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.util.TimeUtils;

public abstract class SearchResultsGetter <T extends MsSearchResult> implements ResultsGetter {

    private static final Logger log = Logger.getLogger(SearchResultsGetter.class);

    @Override
    public List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(IdPickerRun idpRun,
            IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PeptideSpectrumMatchNoFDR> getResultsNoFdr(
            List<IdPickerInput> inputList, Program inputGenerator,
            IDPickerParams params) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PeptideSpectrumMatchIDP> getResults(IdPickerRun run, IDPickerParams params) 
        throws ModifiedSequenceBuilderException {
        return getResults(run.getInputList(), run.getInputGenerator(), params);
    }

    @Override
    public List<PeptideSpectrumMatchIDP> getResults(
            List<IdPickerInput> inputList, Program inputGenerator,
            IDPickerParams params) throws ModifiedSequenceBuilderException {

        long start = System.currentTimeMillis();

        // 1. Get ALL the SequestResults
        List<T> allResults = getAllSearchResults(inputList, inputGenerator, params);

        // 2. Convert list of SequestResult to PeptideSpectrumMatchIDP
        List<PeptideSpectrumMatchIDP> psmList = convertResults(allResults, params);
        
        // 3. Get all the matching proteins
        assignMatchingProteins(psmList, params);
        
        // 4. Update target and decoy count
        updateTargetAndDecoyCount(inputList, psmList);
        
        // 5. Filter hits to small peptides
        removeSmallPeptides(psmList, params);

        long end = System.currentTimeMillis();
        log.info("Total time to get results: "+TimeUtils.timeElapsedMinutes(start, end)+" minutes \n");

        return psmList;
    }


    private void updateTargetAndDecoyCount(List<IdPickerInput> inputList, List<PeptideSpectrumMatchIDP> psmList) {

        log.info("Updating target and decoy hit count");
        Map<Integer, Integer> targetCountMap = new HashMap<Integer, Integer>(inputList.size() * 2);
        Map<Integer, Integer> decoyCountMap = new HashMap<Integer, Integer>(inputList.size() * 2);

        for(IdPickerInput input: inputList) {
            targetCountMap.put(input.getInputId(), 0);
            decoyCountMap.put(input.getInputId(), 0);
        }

        for(PeptideSpectrumMatchIDP psm: psmList) {
            int runSearchId = psm.getSpectrumMatch().getSourceId();

            int tc = targetCountMap.get(runSearchId);
            int dc = decoyCountMap.get(runSearchId);

            if(psm.isDecoyMatch())  dc++;
            else                    tc++;

            targetCountMap.put(runSearchId, tc);
            decoyCountMap.put(runSearchId, dc);
        }

        for(IdPickerInput input: inputList) {
            input.setNumDecoyHits(decoyCountMap.get(input.getInputId()));
            input.setNumTargetHits(targetCountMap.get(input.getInputId()));
        }
    }

    private List<PeptideSpectrumMatchIDP> convertResults(List<T> resultList, IDPickerParams params) 
        throws ModifiedSequenceBuilderException {

        // make a list of peptide spectrum matches
        long s = System.currentTimeMillis();

        PeptideDefinition peptideDef = params.getPeptideDefinition();

        List<PeptideSpectrumMatchIDP> psmList = new ArrayList<PeptideSpectrumMatchIDP>(resultList.size());

        // map of peptide_key and peptideHit
        Map<String, PeptideHit> peptideHitMap = new HashMap<String, PeptideHit>();

        int newPept = 0;
        Iterator<T> iter = resultList.iterator();
        while(iter.hasNext()) {

            T result = iter.next();
            
            // get the peptide
            String peptideKey = PeptideKeyCalculator.getKey(result, peptideDef);

            PeptideHit peptHit = peptideHitMap.get(peptideKey);
            // If we haven't already seen this peptide, create a new entry
            if(peptHit == null) {
                newPept++;
                Peptide peptide = new Peptide(result.getResultPeptide().getPeptideSequence(), peptideKey, -1);
                peptHit = new PeptideHit(peptide);
                peptideHitMap.put(peptideKey, peptHit);
            }

            PeptideSpectrumMatchIDP psm = createPeptideSpectrumMatch(result, peptHit, params.getScoreForFDR());

            psmList.add(psm);
            
            result = null;
            iter.remove();
        }
        System.out.println("Number of peptides created: "+newPept);

        long e = System.currentTimeMillis();
        log.info("\tTime to create list of spectrum matches: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
        e = System.currentTimeMillis();
        return psmList;
    }

    
    private void removeSmallPeptides(List<PeptideSpectrumMatchIDP> psmList, IDPickerParams params) {

        log.info("Removing search hits with peptide length < "+params.getMinPeptideLength());
        Iterator<PeptideSpectrumMatchIDP> iter = psmList.iterator();
        int removed = 0;
        while(iter.hasNext()) {
            PeptideSpectrumMatchIDP psm = iter.next();
            // if the length of the peptide is less than the required threshold do not add it to the final list
            if(psm.getPeptideHit().getPeptide().getPeptideSequence().length() < params.getMinPeptideLength()) {
                iter.remove();
                removed++;
            }
        }
        log.info("\tRemoved "+removed+" spectra. Remaining spectra: "+psmList.size());
    }


    private void assignMatchingProteins(List<PeptideSpectrumMatchIDP> psmList, IDPickerParams params) {

        MsSearchResultProteinDAO protDao = DAOFactory.instance().getMsProteinMatchDAO();

        long s = System.currentTimeMillis();
        // map of protein accession and protein
        Map<String, Protein> proteinMap = new HashMap<String, Protein>();

        String decoyPrefix = params.getDecoyPrefix();

        int newProt = 0;
        for (PeptideSpectrumMatchIDP psm: psmList) {

            // read the matching proteins from the database now
            List<MsSearchResultProtein> msProteinList = protDao.loadResultProteins(psm.getSearchResultId());

            for (MsSearchResultProtein protein: msProteinList) {

                // we could have multiple accessions, keep the first one only
                String[] accessionStrings = protein.getAccession().split("\\cA");

                Protein prot = proteinMap.get(accessionStrings[0]);
                // If we have not already seen this protein create a new entry
                if(prot == null) {
                    newProt++;
                    prot = new Protein(accessionStrings[0], -1);
                    if(decoyPrefix != null) {
                        if (prot.getAccession().startsWith(decoyPrefix))
                            prot.setDecoy();
                    }
                    proteinMap.put(accessionStrings[0], prot);
                }
                psm.getPeptideHit().addProtein(prot);
            }
        }
        System.out.println("Number of peptides created: "+newProt);

        long e = System.currentTimeMillis();
        log.info("\tTime to get matching proteins: "+TimeUtils.timeElapsedSeconds(s,e)+" seconds.");
    }

    // --------------------------------------------------------------------------------------------------------------
    // To be implemented by subclasses
    // --------------------------------------------------------------------------------------------------------------
    abstract List<T> getAllSearchResults(List<IdPickerInput> inputList, Program inputGenerator,  IDPickerParams params);
    
    abstract PeptideSpectrumMatchIDP createPeptideSpectrumMatch(T result, PeptideHit peptHit, SCORE score) 
        throws ModifiedSequenceBuilderException;
}
