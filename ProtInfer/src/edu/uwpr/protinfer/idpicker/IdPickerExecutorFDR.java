/**
 * IdPickerExecutorFDR.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.ProgramParam.SCORE;
import edu.uwpr.protinfer.filter.Filter;
import edu.uwpr.protinfer.filter.FilterException;
import edu.uwpr.protinfer.filter.fdr.FdrCalculatorException;
import edu.uwpr.protinfer.filter.fdr.FdrFilterCriteria;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.util.TimeUtils;

/**
 * 
 */
public class IdPickerExecutorFDR {

    private static Logger log = Logger.getLogger(IDPickerExecutor.class);
    
    public void execute(IdPickerRun idpRun, IDPickerParams params) throws Exception {
        
        // get the program used for the generating the input data
        // NOTE: WE ASSUME ALL THE GIVEN inputIds WERE SEARCHED/ANALYSED WITH THE SAME PROGRAM
        Program program = idpRun.getInputGenerator();
        
        // get all the search hits for the given inputIds
        List<PeptideSpectrumMatchIDP> allPsms = getAllSearchHits(idpRun, params);
        
        // filter the search hits
        List<PeptideSpectrumMatchIDP> filteredPsms;
        try {
            filteredPsms = filterSearchHits(allPsms, params, program);
        }
        catch (FdrCalculatorException e) {
            log.error("Error calculating FDR", e);
            throw new Exception(e);
        }
        catch (FilterException e) {
            log.error("Error filtering on fdr", e);
            throw new Exception(e);
        }
        
        allPsms.clear();
        allPsms = null;
        
        if(filteredPsms == null || filteredPsms.size() == 0) {
            log.error("No filtered hits found!");
            throw new Exception("No filtered hits found!");
        }
        // Our search results should already be filtered at this point
        // so remove all spectra with multiple results
        if(params.isRemoveAmbiguousSpectra()) {
            IDPickerExecutor.removeSpectraWithMultipleResults(filteredPsms);
        }
        
        // update the summary statistics
        updateSummaryAfterFiltering(filteredPsms, idpRun);
        
        // assign ids to peptides and proteins(nrseq ids)
        IDPickerExecutor.assignIdsToPeptidesAndProteins(filteredPsms, program);
        
        // infer the proteins;
        List<InferredProtein<SpectrumMatchIDP>> proteins = IDPickerExecutor.inferProteins(filteredPsms, params);
        
        // rank spectrum matches (based on FDR)
        rankPeptideSpectrumMatches(proteins);
        
        // FINALLY save the results
        new IdPickerResultSaver().saveResults(idpRun, proteins);
        
        // Save the stats for quick lookup
        try {
        	log.info("Saving stats for runID: "+idpRun.getId());
        	IdPickerStatsSaver.getInstance().saveStats(idpRun.getId());
        }
        catch (Exception e){
        	log.warn("Error saving status for runID: "+idpRun.getId(),e);
        }
    }
    
    
    private List<PeptideSpectrumMatchIDP> getAllSearchHits(IdPickerRun idpRun, IDPickerParams params) throws Exception {
        
        IdPickerInputGetter resGetter = IdPickerInputGetter.instance();
        List<PeptideSpectrumMatchIDP> allPsms = resGetter.getInput(idpRun, params);
        return allPsms;
    }
    
    private  List<PeptideSpectrumMatchIDP> filterSearchHits(List<PeptideSpectrumMatchIDP> searchHits, 
            IDPickerParams params, Program program) throws FdrCalculatorException, FilterException {
        
        Comparator<PeptideSpectrumMatchIDP> scoreComparator = getScoreComparator(program, params);
        return filterSearchHits(searchHits, params, program, scoreComparator);
    }

    
    private  List<PeptideSpectrumMatchIDP> filterSearchHits(List<PeptideSpectrumMatchIDP> searchHits, 
                                                           IDPickerParams params, Program program,
                                                           Comparator<PeptideSpectrumMatchIDP> scoreComparator) 
    throws FdrCalculatorException, FilterException {

        long start = System.currentTimeMillis();
        
        FdrCalculatorIdPicker<PeptideSpectrumMatchIDP> calculator = new FdrCalculatorIdPicker<PeptideSpectrumMatchIDP>();
        if(!params.useIdPickerFDRFormula()) {
            calculator.setUseIdPickerFdr(false);
        }
        
        // IDPicker separates charge states for calculating FDR using XCorr scores
        if(program == Program.SEQUEST ) { //|| program == Program.EE_NORM_SEQUEST) {
            if(params.getScoreForFDR() == SCORE.XCorr)
                calculator.separateChargeStates(true);
        }
        
        // set the fdr to 1 in the beginning. 
        for (PeptideSpectrumMatchIDP hit: searchHits)
            hit.setFdr(1.0);
        
//        calculator.setDecoyRatio(params.getDecoyRatio());

        // Calculate FDR
        calculator.calculateFdr(searchHits, scoreComparator);
        long e = System.currentTimeMillis();
        log.info("Calculated FDR for score ("+params.getScoreForFDR().name()+
                ") in "+TimeUtils.timeElapsedSeconds(start, e)+" seconds");
        
        
        // Filter based on the given FDR cutoff
        FdrFilterCriteria filterCriteria = new FdrFilterCriteria(params.getMaxFdr());
        List<PeptideSpectrumMatchIDP> filteredHits = Filter.filter(searchHits, filterCriteria);
        log.info("BEFORE filtering: "+searchHits.size()+"; AFTER filtering: "+filteredHits.size());
        e = System.currentTimeMillis();
        
        log.info("Total time for FDR calculation + filtering: "+TimeUtils.timeElapsedSeconds(start, e));
        
        return filteredHits;
    }
    

    private Comparator<PeptideSpectrumMatchIDP> getScoreComparator(Program program, IDPickerParams params) {
        
        if(program == Program.SEQUEST ) { //|| program == Program.EE_NORM_SEQUEST) {
            // we will be comparing XCorr
            if(params.getScoreForFDR() == SCORE.XCorr) {
                return new Comparator<PeptideSpectrumMatchIDP>() {
                    public int compare(PeptideSpectrumMatchIDP o1, PeptideSpectrumMatchIDP o2) {
                        return Double.valueOf(o1.getScore()).compareTo(o2.getScore());
                    }};
            }
            else if(params.getScoreForFDR() == SCORE.DeltaCN) {
                // we will be comparing DeltaCN -- 0.0 is best (deltaCN = (topHit - currentHit)/topHit)
                return new Comparator<PeptideSpectrumMatchIDP>() {
                    public int compare(PeptideSpectrumMatchIDP o1, PeptideSpectrumMatchIDP o2) {
                        return Double.valueOf(o1.getScore()).compareTo(o2.getScore());
                    }};
            }
            else
                throw new IllegalArgumentException("Unknown score type: "+params.getScoreForFDR().name());
                         
        }
        else if(program == Program.PROLUCID) {
            // TODO here we need to know what primary score is used by ProLuCID
            return null;
        }
        else {
            log.error("Unsupported program: "+program.toString());
            return null;
        }
    }
    
    private void updateSummaryAfterFiltering(List<PeptideSpectrumMatchIDP> filteredPsms, IdPickerRun idpRun) {
        
        // sort the filtered hits by source
        Collections.sort(filteredPsms, new Comparator<PeptideSpectrumMatchIDP>() {
            public int compare(PeptideSpectrumMatchIDP o1,PeptideSpectrumMatchIDP o2) {
                return Integer.valueOf(o1.getSpectrumMatch().getSourceId()).compareTo(o2.getSpectrumMatch().getSourceId());
            }});
        
        // count the number of filtered hits for each source
        int filteredDecoyCnt = 0;
        int filteredTargetCnt = 0;
        int lastSourceId = -1;
        for(PeptideSpectrumMatchIDP hit: filteredPsms) {
            if(lastSourceId != hit.getSpectrumMatch().getSourceId()) {
                if(lastSourceId != -1){
                    IdPickerInput input = idpRun.getInputSummaryForRunSearch(lastSourceId);
                    if(input == null) {
                        log.error("Could not find input summary for runSearchID: "+lastSourceId);
                    }
                    else {
                        input.setNumFilteredTargetHits(filteredTargetCnt);
                        input.setNumFilteredDecoyHits(filteredDecoyCnt);
                    }
                }
                filteredTargetCnt = 0;
                filteredDecoyCnt = 0;
                lastSourceId = hit.getSpectrumMatch().getSourceId();
            }
            if(hit.isDecoyMatch())  filteredDecoyCnt++;
            else                    filteredTargetCnt++;
        }
        // update the last one;
        if(lastSourceId != -1) {
            IdPickerInput input = idpRun.getInputSummaryForRunSearch(lastSourceId);
            if(input == null) {
                log.error("Could not find input summary for runSearchID: "+lastSourceId);
            }
            else {
                input.setNumFilteredTargetHits(filteredTargetCnt);
                input.setNumFilteredDecoyHits(filteredDecoyCnt);
            }
        }
        else {
            log.error("Could not update input summary for runSearchIds");
        }
    }

    private void rankPeptideSpectrumMatches(List<InferredProtein<SpectrumMatchIDP>> proteins) {
       
        for(InferredProtein<SpectrumMatchIDP> protein: proteins) {
            
            // look at each peptide for the protein
            for(PeptideEvidence<SpectrumMatchIDP> pev: protein.getPeptides()) {
                // rank all the spectra for this peptide (based on calculated FDR)
                List<SpectrumMatchIDP> psmList = pev.getSpectrumMatchList();
                Collections.sort(psmList, new Comparator<SpectrumMatchIDP>(){
                    @Override
                    public int compare(SpectrumMatchIDP o1, SpectrumMatchIDP o2) {
                        return Double.valueOf(o1.getFdr()).compareTo(o2.getFdr());
                    }});
                int rank = 1;
                for(SpectrumMatchIDP psm: psmList) {
                    psm.setRank(rank); 
                    rank++;
                }
            }
        }
    }
}
