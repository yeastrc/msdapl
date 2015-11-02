/**
 * IdPickerExecutorNoFDR.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.search.Program;

import edu.uwpr.protinfer.PeptideKeyCalculator;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinInferrerMaximal;
import edu.uwpr.protinfer.infer.SpectrumMatch;

/**
 * 
 */
public class IdPickerExecutorNoFDR {

    private static Logger log = Logger.getLogger(IDPickerExecutor.class);
    
    
    public void execute(IdPickerRun idpRun, IDPickerParams params, boolean split) throws Exception {
        if(!split)
            execute(idpRun, params);
        else {
            System.out.println("Splitting Input!!");
            // get the program used for the generating the input data
            // NOTE: WE ASSUME ALL THE GIVEN inputIds WERE SEARCHED/ANALYSED WITH THE SAME PROGRAM
            Program program = idpRun.getInputGenerator();
            
            List<IdPickerInput> inputList = idpRun.getInputList();
            for(int i = 0; i < inputList.size(); i+=10) {
                int e = Math.min(inputList.size(), i+10);
                List<IdPickerInput> list = inputList.subList(i, e);
                
                // get all the search hits for the given inputIds
                List<PeptideSpectrumMatchNoFDR> allPsms = IdPickerInputGetter.instance().getInputNoFdr(list, program, params);

                // assign ids to peptides and proteins(nrseq ids)
                IDPickerExecutor.assignIdsToPeptidesAndProteins(allPsms, program);
                
                // infer the proteins;
//                List<InferredProtein<SpectrumMatch>> proteins = IDPickerExecutor.inferProteins(allPsms, params);
                // Infer ALL proteins. We will do parsimonous analysis AFTER we have ALL proteins and peptides. 
                ProteinInferrerMaximal maxInferrer = new ProteinInferrerMaximal();
                List<InferredProtein<SpectrumMatch>> proteins = maxInferrer.inferProteins(allPsms);
                log.info("Inferred ALL proteins for sublist");
                
                // save the results
                new IdPickerResultSaver().saveAndUpdateResults(idpRun.getId(), list, proteins);
                
                allPsms.clear();
                allPsms = null;
            }
            
            log.info("Inferring proteins on ALL input");
            // FINALLY infer the proteins from the entire input.
            ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
            List<Integer> proteinIds = protDao.getProteinferProteinIds(idpRun.getId());
            List<InferredProtein<SpectrumMatch>> iProteins = new ArrayList<InferredProtein<SpectrumMatch>>(proteinIds.size());
            
            for(Integer piProtId: proteinIds) {
                ProteinferProtein prot = protDao.loadProtein(piProtId);
                
                Protein baseProt = new Protein(""+prot.getId(), prot.getNrseqProteinId());
                InferredProtein<SpectrumMatch> iProt = new InferredProtein<SpectrumMatch>(baseProt);
                iProt.setSpectrumMatchCount(prot.getSpectrumCount());
                iProteins.add(iProt);
               
                // split the peptides into PeptideEvidence objects based on the PeptideDefinition
                for(ProteinferPeptide pept: prot.getPeptides()) {
                    
                    List<ProteinferIon> ionList = pept.getIonList();
                    Set<String> keysFound = new HashSet<String>();
                    for(ProteinferIon ion: ionList) {
                        ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
                        String key = PeptideKeyCalculator.getKey(ion, params.getPeptideDefinition());
                        if(!keysFound.contains(key)) {
                            Peptide peptBase = new Peptide(pept.getSequence(), key, ion.getId());
                            PeptideEvidence<SpectrumMatch> pev = new PeptideEvidence<SpectrumMatch>(peptBase);
                            iProt.addPeptideEvidence(pev);
                            
                            keysFound.add(key);
                        }
                    }
                }
            }
            // calculate protein coverage
            IDPickerExecutor.calculateProteinSequenceCoverage(iProteins); // throws exception
            
            // filter and do parsimony analysis
            ProteinInferrerIdPicker inferrer = new ProteinInferrerIdPicker();
            iProteins =  inferrer.inferProteins(iProteins, params);
            
            // calculate normalized spectrum abundance factor
            NSAFCalculator.instance().calculateNSAF(iProteins, params.isCalculateAllNsaf());
            
            
            new IdPickerResultSaver().updateResults(idpRun.getId(), iProteins, idpRun.getInputGenerator(), params);
        }
    }
    
    public void execute(IdPickerRun idpRun, IDPickerParams params) throws Exception {
        
        // get the program used for the generating the input data
        // NOTE: WE ASSUME ALL THE GIVEN inputIds WERE SEARCHED/ANALYSED WITH THE SAME PROGRAM
        Program program = idpRun.getInputGenerator();
        
        // get all the search hits for the given inputIds
        List<PeptideSpectrumMatchNoFDR> allPsms = IdPickerInputGetter.instance().getInputNoFdr(idpRun, params);

        // assign ids to peptides and proteins(nrseq ids)
        IDPickerExecutor.assignIdsToPeptidesAndProteins(allPsms, program);
        
        // infer the proteins;
        List<InferredProtein<SpectrumMatch>> proteins = IDPickerExecutor.inferProteins(allPsms, params);
        
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
   
}
