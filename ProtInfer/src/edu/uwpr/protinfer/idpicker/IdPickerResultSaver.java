package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferIonDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerInputDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerPeptideBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerSpectrumMatchDAO;
import org.yeastrc.ms.dao.search.MsSearchResultDAO;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerInput;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptideBase;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerSpectrumMatch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideEvidence;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.util.TimeUtils;

public class IdPickerResultSaver {

    private static final Logger log = Logger.getLogger(IdPickerResultSaver.class);
    private static final ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
//    private static final IdPickerRunDAO runDao = factory.getIdPickerRunDao();
    private static final IdPickerInputDAO inputDao = factory.getIdPickerInputDao();
    private static final IdPickerProteinBaseDAO protDao = factory.getIdPickerProteinBaseDao();
    private static final IdPickerPeptideBaseDAO peptDao = factory.getIdPickerPeptideBaseDao();
    private static final ProteinferIonDAO ionDao = factory.getProteinferIonDao();
    private static final ProteinferSpectrumMatchDAO psmDao = factory.getProteinferSpectrumMatchDao();
    private static final IdPickerSpectrumMatchDAO idpPsmDao = factory.getIdPickerSpectrumMatchDao();
    private static final MsSearchResultDAO resDao = DAOFactory.instance().getMsSearchResultDAO();
    
    
    private Map<Integer, Integer> nrseqIdToPinferProteinIdMap = new HashMap<Integer, Integer>();
    
    public <T extends SpectrumMatch> void saveResults(IdPickerRun idpRun, List<InferredProtein<T>> proteins) {
        
        long s = System.currentTimeMillis();
        
        // update the run summary
        // runDao.saveIdPickerRunSummary(idpRun); // this will save entries in the IDPickerRunSummary table only
        
        // save the summary for each input
        inputDao.saveIdPickerInputList(idpRun.getInputList());
        
        // save the inferred proteins, associated peptides and spectrum matches
        saveInferredProteins(idpRun.getId(), proteins);
        
        saveSubsetProteinInformation(idpRun.getId(), proteins);
        
        long e = System.currentTimeMillis();
        log.info("SAVED IDPickerResults in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes.");
    }

    public <T extends SpectrumMatch> void saveAndUpdateResults(int pinferId, 
            List<IdPickerInput> inputList, List<InferredProtein<T>> proteins) {
        
        long s = System.currentTimeMillis();
        // save the summary for each input
        inputDao.saveIdPickerInputList(inputList);
        
        // save and update inferred proteins, associated peptides and spectrum matches
        saveAndUpdateInferredProteins(pinferId, proteins);
        
        long e = System.currentTimeMillis();
        log.info("SAVED AND UPDATED IDPickerResults in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes.");
        
    }
    
    public <T extends SpectrumMatch> void updateResults(int pinferId, List<InferredProtein<T>> proteins,
            Program inputGenerator, IDPickerParams params) {
        
        // pinferPeptideIds
        Set<Integer> idpPeptideIds = new HashSet<Integer>();
        // pinferIonIds
        Set<Integer> idpIonIds = new HashSet<Integer>();
        
        for(InferredProtein<T> protein: proteins) {
            
            //load the existing protein
            int pinferProteinId = Integer.parseInt(protein.getAccession());
            IdPickerProteinBase oldProt = protDao.loadProtein(pinferProteinId);
            oldProt.setClusterLabel(protein.getProteinClusterLabel());
            oldProt.setCoverage(protein.getPercentCoverage());
            oldProt.setProteinGroupLabel(protein.getProteinGroupLabel());
            oldProt.setIsParsimonious(protein.getIsAccepted());
            oldProt.setIsSubset(protein.getProtein().isSubset());
            oldProt.setNsaf(protein.getNSAF());
            
            protDao.updateIdPickerProtein(oldProt);
            
            // If the user ran protein inference with a peptide definition being more
            // than just the peptide sequence, we will group all peptide evidence 
            // that reprensent the same peptide sequence (w/o mods). This is because
            // data in the tables should be stored uniformly regardless of peptide
            // definition used for running protein inference
            List<PeptideEvidence<T>> pevList = groupPeptideEvidenceByPeptideSeq(protein.getPeptides());
            
            // save the peptides, ions and the associated spectrum matches
            for(PeptideEvidence<T> pev: pevList) {
                Peptide peptide = pev.getPeptide();
                
                // remember -- peptide.getId() here will return the ionID
                Integer pinferIonId = peptide.getId();
                if(!idpIonIds.contains(pinferIonId)) {
                    
                    idpIonIds.add(pinferIonId);
                    
                    ProteinferIon ion = ionDao.load(pinferIonId);
                    int pinferPeptideId = ion.getProteinferPeptideId();
                    
                    if(!idpPeptideIds.contains(pinferPeptideId)) {
                        
                        idpPeptideIds.add(pinferPeptideId);
                        
                        IdPickerPeptideBase oldPept = peptDao.load(pinferPeptideId);
                        oldPept.setPeptideGroupLabel(pev.getPeptide().getPeptideGroupLabel());
                        oldPept.setUniqueToProtein(pev.getPeptide().isUniqueToProtein());
                        if(peptDao.updateIdPickerPeptide(oldPept) != 1)
                            throw new IllegalArgumentException("Could not update peptide: "+oldPept.getId());
                        rerankSpectra(oldPept, inputGenerator, params);
                        
                        // make the group association if it does not already exist
                        protDao.saveProteinPeptideGroupAssociation(pinferId, protein.getProteinGroupLabel(),
                                peptide.getPeptideGroupLabel());
                    }
                }
            }
        }
        
        // If any of the proteins previously saved did not make it this far delete them 
        // and the associated peptides
        List<Integer> proteinIds = protDao.getProteinferProteinIds(pinferId);
        Set<Integer> madeId = new HashSet<Integer>((int) (proteins.size() * 1.5));
        for(InferredProtein<T> protein: proteins) {
            //load the existing protein
            int pinferProteinId = Integer.parseInt(protein.getAccession());
            madeId.add(pinferProteinId);
        }
        ProteinferProteinDAO baseProtDao = factory.getProteinferProteinDao();
        ProteinferPeptideDAO basePeptDao = factory.getProteinferPeptideDao();
        for(int proteinId: proteinIds) {
            if(!madeId.contains(proteinId)) {
                ProteinferProtein prot = baseProtDao.loadProtein(proteinId);
                // delete all peptides
                List<Integer> peptIds = basePeptDao.getPeptideIdsForProteinferProtein(proteinId);
                for(int peptId: peptIds)
                    basePeptDao.delete(peptId);
                // delete protein
                baseProtDao.delete(proteinId);
            }
        }
    }
    
    
    private void rerankSpectra(IdPickerPeptideBase oldPept, Program inputGenerator, IDPickerParams params) {
        
        if(inputGenerator == Program.PERCOLATOR) {
            
            PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
            List<ProteinferSpectrumMatch> psmList = psmDao.loadSpectrumMatchesForPeptide(oldPept.getId());
            List<PercolatorResult> percResults = new ArrayList<PercolatorResult>(psmList.size());
            
            // get all the percolator results for this peptide
            for(ProteinferSpectrumMatch psm: psmList) {
                PercolatorResult res = presDao.loadForPercolatorResultId(psm.getResultId());
                percResults.add(res);
            }
            
            // rank the results based on which cutoff values we have in the parameters. 
            PercolatorResultsRanker ranker = PercolatorResultsRanker.instance();
            PercolatorParams percParams = new PercolatorParams(params);
            Map<Integer, Integer> resultRankMap = ranker.rankResultsByPeptide(percResults, 
                    percParams.hasPsmQvalueCutoff(), percParams.hasPsmPepCutoff(), percParams.hasPsmDiscriminantScoreCutoff());
            
            
            // set the new rank and update the results
            for(ProteinferSpectrumMatch psm: psmList) {
                psm.setRank(resultRankMap.get(psm.getResultId()));
                if(psmDao.update(psm) != 1)
                    throw new IllegalArgumentException("Could not update psm: "+psm.getId());
            }
        }
        else {
            throw new IllegalArgumentException("Don't know how to rank results for "+inputGenerator);
        }
    }

    private <T extends SpectrumMatch> void saveAndUpdateInferredProteins(int pinferId, List<InferredProtein<T>> proteins) {
        
        // map of peptide id and pinferPeptideIDs
        Map<Integer, Integer> idpPeptideIds = new HashMap<Integer, Integer>();
        
        for(InferredProtein<T> protein: proteins) {
            
            // check if there is a protein with the given nrseq id
            ProteinferProtein oldProt = protDao.loadProtein(pinferId, protein.getProteinId());
            int pinferProteinId = 0;
            if(oldProt != null) {
                pinferProteinId = oldProt.getId();
            }
            else {
                pinferProteinId = saveProtein(pinferId, protein);
            }
            
            // If the user ran protein inference with a peptide definition being more
            // than just the peptide sequence, we will group all peptide evidence 
            // that reprensent the same peptide sequence (w/o mods). This is because
            // data in the tables should be stored uniformly regardless of peptide
            // definition used for running protein inference
            List<PeptideEvidence<T>> pevList = groupPeptideEvidenceByPeptideSeq(protein.getPeptides());
            
            // save the peptides, ions and the associated spectrum matches
            for(PeptideEvidence<T> pev: pevList) {
                Peptide peptide = pev.getPeptide();
                Integer pinferPeptideId = idpPeptideIds.get(peptide.getId());
                if(pinferPeptideId == null) {
                    
                    // check first if this peptide is already in the database. 
                    ProteinferPeptide oldPept = peptDao.loadPeptide(pinferId, peptide.getPeptideSequence());
                    if(oldPept == null) {
                        pinferPeptideId = savePeptideEvidence(pev, pinferId);
                    }
                    else {
                        pinferPeptideId = oldPept.getId();
                        updatePeptideEvidence(oldPept, pev);
                    }
                    // add to our map
                    idpPeptideIds.put(peptide.getId(), pinferPeptideId);
                }
                // link the protein and peptide
                protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
                // make the group association if it does not already exist
                // NOT SAVING GROUP ASSOCIATION -- WE SHOULD NOT HAVE THAT INFO YET
                // protDao.saveProteinPeptideGroupAssociation(pinferId, idpProt.getGroupId(), peptide.getPeptideGroupId());
            }
        }
    }

    
    private <T extends SpectrumMatch> void saveInferredProteins(int pinferId, List<InferredProtein<T>> proteins) {
        
        // map of peptide sequence (no mods or charge) and pinferPeptideIDs
        Map<String, Integer> idpPeptideIds = new HashMap<String, Integer>();
        
        for(InferredProtein<T> protein: proteins) {
            
            // save the protein
            int pinferProteinId = saveProtein(pinferId, protein);
            
            // If the user ran protein inference with a peptide definition being more
            // than just the peptide sequence, we will group all peptide evidence 
            // that reprensent the same peptide sequence (w/o mods). This is because
            // data in the tables should be stored uniformly regardless of peptide
            // definition used for running protein inference
            List<PeptideEvidence<T>> pevList = groupPeptideEvidenceByPeptideSeq(protein.getPeptides());
            
            // save the peptides, ions and the associated spectrum matches
            for(PeptideEvidence<T> pev: pevList) {
                Peptide peptide = pev.getPeptide();
                Integer pinferPeptideId = idpPeptideIds.get(peptide.getPeptideSequence());
                if(pinferPeptideId == null) {
                    pinferPeptideId = savePeptideEvidence(pev, pinferId);
                    // add to our map
                    idpPeptideIds.put(peptide.getPeptideSequence(), pinferPeptideId);
                }
                // link the protein and peptide
                protDao.saveProteinferProteinPeptideMatch(pinferProteinId, pinferPeptideId);
                // make the group association if it does not already exist
                protDao.saveProteinPeptideGroupAssociation(pinferId, protein.getProteinGroupLabel(), 
                        peptide.getPeptideGroupLabel());
            }
        }
    }


    private <T extends SpectrumMatch> int savePeptideEvidence(PeptideEvidence<T> pev, int pinferId) {
        
        int pinferPeptideId = savePeptide(pev, pinferId);
        
        // sort the psm's by sequence + mod_state + charge
        Map<String, List<T>> map = new HashMap<String, List<T>>((int) (pev.getSpectrumMatchList().size()*1.5));
        Map<String, Integer> modIds = new HashMap<String, Integer>();
        int modId = 1;
        for(T psm: pev.getSpectrumMatchList()) {
            String modseq = psm.getModifiedSequence(); // this is the modified sequence
            
            
            String key = modseq+"_"+psm.getCharge();
            List<T> list = map.get(key);
            if(list == null) {
                list = new ArrayList<T>();
                map.put(key, list);
                
                // get the modification id
                Integer mid = modIds.get(modseq);
                if(mid == null) {
                    mid = modId;
                    modId++;
                    modIds.put(modseq, mid);
                }
            }
            list.add(psm);
        }
        
        // save all the ions (sequence + mod_state + charge) for the peptide
        for(String key: map.keySet()) {
            ProteinferIon ion = new ProteinferIon();
            List<T> psmList = map.get(key);
            ion.setProteinferPeptideId(pinferPeptideId);
            ion.setCharge(psmList.get(0).getCharge());
            String modseq = key.substring(0, key.lastIndexOf("_"));
//            ion.setSequence(modseq);
            ion.setModificationStateId(modIds.get(modseq));
            ion.setModifiedSequence(modseq);
            int ionId = ionDao.save(ion);
            
            // save all the spectra for this ion
            for(T psm: psmList) {
                if(psm instanceof SpectrumMatchIDP) {
                    saveIdPickerSpectrumMatch(ionId, psm);
                }
                else {
                    saveSpectrumMatch(ionId, psm);
                }
            }
        }
        return pinferPeptideId;
    }

    
    private <T extends SpectrumMatch> void updatePeptideEvidence(ProteinferPeptide oldPeptide, PeptideEvidence<T> pev) {
    
        
        // check if we already have some ions for this peptide. If so, we will add
        // spectrum matches to existing ions. 
        List<ProteinferIon> ionList = oldPeptide.getIonList();
        Map<String, Integer> modStateIdMap = new HashMap<String, Integer>();
        Map<String, Integer> ionIdMap = new HashMap<String, Integer>();
        for(ProteinferIon ion: ionList) {
            String modPeptide = ion.getModifiedSequence();
//            ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
//            String modPeptide = null;
//            try {
//                modPeptide = getModifiedPeptide(psm.getSearchResultId());
//            }
//            catch (ModifiedSequenceBuilderException e) {
//                throw new RuntimeException("Error getting modified sequence", e);
//            }
            // modification state id
            Integer modStateId = modStateIdMap.get(modPeptide);
            if(modStateId == null) 
                modStateIdMap.put(modPeptide, ion.getModificationStateId());
            else
                // make sure the mod state Id for this ion is same as the one 
                // that has the same modified sequence
                if(modStateId != ion.getModificationStateId())
                    throw new RuntimeException("Found ions with same modified sequence have different modificationStateID"+
                            ": ionID: "+ion.getId());
            // ion id
            String modPeptAndCharge = modPeptide+"_"+ion.getCharge();
            Integer ionId = ionIdMap.get(modPeptAndCharge);
            if(ionId == null)
                ionIdMap.put(modPeptAndCharge, ionId);
            else
                // should never happen!!!
                throw new RuntimeException("Found ions with same modified sequence and charge!!"+
                        ": ionID: "+ion.getId());
        }
        
        int maxModId = 0;
        for(Integer modId: modStateIdMap.values())
            maxModId = Math.max(maxModId, modId);
        
        // sort the psm's by sequence + mod_state + charge
        // add Ids for any new modifications states we see in the current results.
        Map<String, List<T>> map = new HashMap<String, List<T>>((int) (pev.getSpectrumMatchList().size()*1.5));
//        Map<String, Integer> modIds = new HashMap<String, Integer>();
        int modId = maxModId + 1;
        for(T psm: pev.getSpectrumMatchList()) {
            String modseq = psm.getModifiedSequence(); // this is the modified sequence
            
            String key = modseq+"_"+psm.getCharge();
            List<T> list = map.get(key);
            if(list == null) {
                list = new ArrayList<T>();
                map.put(key, list);
                
                // get the modification id
                Integer mid = modStateIdMap.get(modseq);
                if(mid == null) {
                    mid = modId;
                    modId++;
                    modStateIdMap.put(modseq, mid);
                }
            }
            list.add(psm);
        }
        
        
        // save all the ions (sequence + mod_state + charge) for the peptide
        for(String modSeqAndCharge: map.keySet()) {
            
            List<T> psmList = map.get(modSeqAndCharge); // spectra for this ion
            
            Integer ionId = ionIdMap.get(modSeqAndCharge);
            
            if(ionId == null) {
                // have we see this modification state before?
                String modseq = modSeqAndCharge.substring(0, modSeqAndCharge.lastIndexOf("_"));
                // save the ion
                ProteinferIon ion = new ProteinferIon();
                ion.setProteinferPeptideId(oldPeptide.getId());
                ion.setCharge(psmList.get(0).getCharge());
                ion.setModificationStateId(modStateIdMap.get(modseq));
                ion.setModifiedSequence(modseq);
                ionId = ionDao.save(ion);
            }
            // save all the spectra for this ion
            for(T psm: psmList) {
                if(psm instanceof SpectrumMatchIDP) {
                    saveIdPickerSpectrumMatch(ionId, psm);
                }
                else {
                    saveSpectrumMatch(ionId, psm);
                }
            }
        }
    }
    
    private <T extends SpectrumMatch> void saveSubsetProteinInformation(int pinferId, List<InferredProtein<T>> proteins) {
        
        
        for(InferredProtein<T> protein: proteins) {
            
            if(protein.getIsSubset()) {
            	
            	int pinferProteinId = nrseqIdToPinferProteinIdMap.get(protein.getProteinId());
            	
            	for(Integer proteinGroupLabel: protein.getProtein().getSuperProteinGroupLabels()) {
            		
            		// get a list of proteinIds for this proteinGroupLabel
            		List<Integer> superProteinIds = protDao.getIdPickerGroupProteinIds(pinferId, proteinGroupLabel);
            		// save an entry in the IDPickerSubsetProtein table
            		for(Integer superProteinId: superProteinIds) {
            			// The super-protein might be a subset protein itself. Don't save in that case
            			IdPickerProteinBase protBase = protDao.loadProtein(superProteinId);
            			if(protBase.getIsSubset())
            				continue;
            			protDao.saveSubsetProtein(pinferProteinId, superProteinId);
            		}
            	}
            }
        }
    }

    // ----------------------------------------------------------------------------------------------
    // Group peptide evidence by sequence, ignore any peptide definition used to do protein inference.
    // ----------------------------------------------------------------------------------------------
    private<T extends SpectrumMatch> List<PeptideEvidence<T>> groupPeptideEvidenceByPeptideSeq(List<PeptideEvidence<T>> pevList) {
        
        Map<String, PeptideEvidence<T>> peptListMap = new HashMap<String, PeptideEvidence<T>>(pevList.size()*2);
        for(PeptideEvidence<T> pev: pevList) {
            PeptideEvidence<T> pevO = peptListMap.get(pev.getPeptide().getPeptideSequence());
            if(pevO == null) {
                peptListMap.put(pev.getPeptide().getPeptideSequence(), pev);
            }
            else {
                PeptideEvidence<T> pevC = combinePeptideEvidence(pev, pevO);
                peptListMap.put(pev.getPeptide().getPeptideSequence(), pevC);
            }
        }
        
        return new ArrayList<PeptideEvidence<T>>(peptListMap.values());
    }
    
    private <T extends SpectrumMatch> PeptideEvidence<T> combinePeptideEvidence(PeptideEvidence<T> pev1, PeptideEvidence<T> pev2) {
       
        for(T psm: pev1.getSpectrumMatchList()) {
            pev2.addSpectrumMatch(psm);
        }
        return pev2;
    }
    
    // ----------------------------------------------------------------------------------------------
    // SAVE a spectrum match
    // ----------------------------------------------------------------------------------------------
    private <T extends SpectrumMatch> void saveSpectrumMatch(int ionId, T psm) {
        ProteinferSpectrumMatch idpPsm = new ProteinferSpectrumMatch();
        idpPsm.setProteinferIonId(ionId);
        idpPsm.setResultId(psm.getResultId());
        idpPsm.setScanId(psm.getScanId());
        idpPsm.setRank(psm.getRank());
        psmDao.saveSpectrumMatch(idpPsm);
    }

    // ----------------------------------------------------------------------------------------------
    // SAVE a IDPicker spectrum match (this has FDR)
    // ----------------------------------------------------------------------------------------------
    private <T extends SpectrumMatch> void saveIdPickerSpectrumMatch(int ionId, T psm) {
        
        IdPickerSpectrumMatch idpPsm = new IdPickerSpectrumMatch();
        idpPsm.setProteinferIonId(ionId);
        idpPsm.setResultId(psm.getResultId());
        idpPsm.setScanId(psm.getScanId());
        idpPsm.setFdr(((SpectrumMatchIDP)psm).getFdr());
        idpPsm.setRank(psm.getRank());
        idpPsmDao.saveSpectrumMatch(idpPsm);
    }

    // ----------------------------------------------------------------------------------------------
    // SAVE a protein
    // ----------------------------------------------------------------------------------------------
    private <T extends SpectrumMatch> int saveProtein(int pinferId, InferredProtein<T> protein) {
        IdPickerProteinBase idpProt = new IdPickerProteinBase();
        idpProt.setProteinferId(pinferId);
        idpProt.setNrseqProteinId(protein.getProteinId());
        idpProt.setClusterLabel(protein.getProteinClusterLabel());
        idpProt.setProteinGroupLabel(protein.getProteinGroupLabel());
        idpProt.setIsParsimonious(protein.getIsAccepted());
        idpProt.setIsSubset(protein.getProtein().isSubset());
        idpProt.setCoverage(protein.getPercentCoverage());
        idpProt.setNsaf(protein.getNSAF());
        int pinferProteinId = protDao.saveIdPickerProtein(idpProt);
        nrseqIdToPinferProteinIdMap.put(protein.getProteinId(), pinferProteinId);
        return pinferProteinId;
    }
    
    // ----------------------------------------------------------------------------------------------
    // SAVE a peptide
    // ----------------------------------------------------------------------------------------------
    private <T extends SpectrumMatch> int savePeptide(PeptideEvidence<T> pev, int pinferId) {
        Peptide peptide = pev.getPeptide();
        IdPickerPeptideBase idpPept = new IdPickerPeptideBase();
        idpPept.setPeptideGroupLabel(peptide.getPeptideGroupLabel());
        idpPept.setSequence(peptide.getPeptideSequence());
        idpPept.setUniqueToProtein(peptide.isUniqueToProtein());
        idpPept.setProteinferId(pinferId);
        
        int pinferPeptideId = peptDao.saveIdPickerPeptide(idpPept);
        return pinferPeptideId;
    }
    
    // ----------------------------------------------------------------------------------------------
    // Get modified peptide for a result (this should be the id from the msRunSearchResult table)
    // ----------------------------------------------------------------------------------------------
    public String getModifiedPeptide (int resultId) throws ModifiedSequenceBuilderException {
        MsSearchResult res = resDao.load(resultId);
        return res.getResultPeptide().getModifiedPeptide();
    }
   
}
