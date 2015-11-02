package edu.uwpr.protinfer.idpicker;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.Program;

public class IdPickerInputGetter {

    private static final Logger log = Logger.getLogger(IdPickerInputGetter.class);
    
    private static IdPickerInputGetter instance = new IdPickerInputGetter();
    
    private IdPickerInputGetter() {}
    
    public static final IdPickerInputGetter instance() {
        return instance;
    }
    
    public int getUnfilteredInputCount(int inputId, Program program) {
        if(program == Program.PERCOLATOR) {
            List<Integer> percResultIds = DAOFactory.instance().getPercolatorResultDAO().loadIdsForRunSearchAnalysis(inputId);
            return percResultIds.size();
        }
        else if( Program.isSearchProgram(program)) {
            List<Integer> resultIds = DAOFactory.instance().getMsSearchResultDAO().loadResultIdsForRunSearch(inputId);
            return resultIds.size();
        }
        else {
            log.warn("Don't know how to get unfiltered result count for program : "+program);
            return 0;
        }
    }
    
    /**
     * Returns a list of peptide spectrum matches which are filtered by relevant score(s)
     * and for min peptide length and 
     * ranked by relevant score(s) for each peptide (as defined in the PeptideDefinition). 
     * @throws Exception 
     */
    public List<PeptideSpectrumMatchNoFDR> getInputNoFdr(IdPickerRun run , IDPickerParams params) throws Exception {
        
        Program inputGenerator = run.getInputGenerator();
        log.info("Reading search/analysis results for Protein Inference run: "+run.getId()+"; Input Generator Program: "+inputGenerator.displayName());
        if(inputGenerator == Program.PERCOLATOR) {
           PercolatorResultsGetter percResGetter = PercolatorResultsGetter.instance();
           return percResGetter.getResultsNoFdr(run, params);
        }
        else {
            log.error("Don't know how to get Protein Inference input for: "+inputGenerator);
            throw new Exception("Don't know how to get Protein Inference input for: "+inputGenerator);
        }
    }
    
    public List<PeptideSpectrumMatchNoFDR> getInputNoFdr(List<IdPickerInput> inputList, Program inputGenerator, IDPickerParams params) 
            throws Exception {
        
        log.info("Reading search/analysis results. Input Generator Program: "+inputGenerator.displayName());
        if(inputGenerator == Program.PERCOLATOR) {
           PercolatorResultsGetter percResGetter = PercolatorResultsGetter.instance();
           return percResGetter.getResultsNoFdr(inputList, inputGenerator, params);
        }
        else {
            log.error("Don't know how to get Protein Inference input for: "+inputGenerator);
            throw new Exception("Don't know how to get Protein Inference input for: "+inputGenerator);
        }
    }
    
    
    public List<PeptideSpectrumMatchIDP> getInput(IdPickerRun run, IDPickerParams params) throws Exception {
        
        Program inputGenerator = run.getInputGenerator();
        log.info("Reading search/analysis results for Protein Inference run: "+run.getId()+"; Input Generator Program: "+inputGenerator.displayName());
        
        if (inputGenerator == Program.SEQUEST || inputGenerator == Program.COMET) { // || inputGenerator == Program.EE_NORM_SEQUEST) {
            SequestResultsGetter seqResGetter = SequestResultsGetter.instance();
            return seqResGetter.getResults(run, params);
        }
        else if (inputGenerator == Program.PROLUCID) {
            ProlucidResultsGetter plcidResGetter = ProlucidResultsGetter.instance();
            return plcidResGetter.getResults(run, params);
        }
        else {
            log.error("Don't know how to get Protein Inference input for: "+inputGenerator);
            throw new Exception("Don't know how to get Protein Inference input for: "+inputGenerator);
        }
    }
    
}
