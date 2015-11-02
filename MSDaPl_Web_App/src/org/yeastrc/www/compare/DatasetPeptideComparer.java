/**
 * DatasetPeptideComparer.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinferIon;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferSpectrumMatch;
import org.yeastrc.ms.domain.search.MsSearchResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetPeptideInformation;
import org.yeastrc.www.compare.dataset.DatasetProteinInformation;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.proteinfer.MsResultLoader;

import edu.uwpr.protinfer.PeptideKeyCalculator;

/**
 * 
 */
public class DatasetPeptideComparer {

    private static DatasetPeptideComparer instance;
    private static final ProteinferDAOFactory daoFactory = ProteinferDAOFactory.instance();
    private static final ProteinferPeptideDAO peptDao = daoFactory.getProteinferPeptideDao();
    private static final ProteinferProteinDAO protDao = daoFactory.getProteinferProteinDao();
    
    private static final Logger log = Logger.getLogger(DatasetPeptideComparer.class.getName());
    
    private DatasetPeptideComparer() {}
    
    public static DatasetPeptideComparer instance() {
        if(instance == null)
            instance = new DatasetPeptideComparer();
        return instance;
    }
    
    public int getTotalPeptSeqForProtein(ComparisonProtein protein) {
        
        List<Integer> datasetIds = new ArrayList<Integer>(protein.getDatasetInformation().size());
        for(DatasetProteinInformation dpi: protein.getDatasetInformation()) {
            // If this dataset does not contain this protein move on.
            if(!dpi.isPresent()) continue;
            datasetIds.add(dpi.getDatasetId());
        }
        return protDao.getPeptideCountForProtein(protein.getNrseqId(), datasetIds);
        
//        Set<String> peptides = new HashSet<String>();
//        for(DatasetProteinInformation dpi: protein.getDatasetInfo()) {
//            
//            // If this dataset does not contain this protein move on.
//            if(!dpi.isPresent()) continue;
//            
//            List<String> protPeptides = getPeptideSequences(protein.getNrseqId(), dpi.getDataset());
//            peptides.addAll(protPeptides);
////            max = Math.max(max, getPeptideCount(protein.getNrseqId(), dpi.getDataset()));
//        }
//        return peptides.size();
    }
    
    private int getPeptideCount(int nrseqProteinId, Dataset dataset) {
        
        int count = 0;
        if(dataset.getSource() == DatasetSource.DTA_SELECT) {
            
            
        }
        else if(dataset.getSource() == DatasetSource.PROTINFER || dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
            
            ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
            nrseqIds.add(nrseqProteinId);
            List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
            for(int piProteinId: piProteinIds) {
                count += peptDao.getPeptideIdsForProteinferProtein(piProteinId).size();
            }
            return count;
        }
        return 0;
    }
    
    private List<String> getPeptideSequences(int nrseqProteinId, Dataset dataset) {
        
        List<String> peptides = new ArrayList<String>();
        if(dataset.getSource() == DatasetSource.DTA_SELECT) {
            
            // TODO
        }
        else if(dataset.getSource() == DatasetSource.PROTINFER || dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
            
            ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
            nrseqIds.add(nrseqProteinId);
            List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
            for(int piProteinId: piProteinIds) {
                List<ProteinferPeptide> piPeptides = peptDao.loadPeptidesForProteinferProtein(piProteinId);
                for(ProteinferPeptide pept: piPeptides) {
                    peptides.add(pept.getSequence());
                }
            }
        }
        return peptides;
    }
    
    public PeptideComparisonDataset getComparisonPeptides(int nrseqProteinId, List<Dataset> datasets) {
        
        Map<String, ComparisonPeptide> peptSeqMap = new HashMap<String, ComparisonPeptide>();
        
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(nrseqProteinId);
        
        MsResultLoader resLoader = MsResultLoader.getInstance();
        PeptideDefinition peptDef = new PeptideDefinition();
        peptDef.setUseCharge(true);
        peptDef.setUseMods(true);
        
        for(Dataset dataset: datasets) {
            
        	ProteinferRun run = daoFactory.getProteinferRunDao().loadProteinferRun(dataset.getDatasetId());
        	Program inputGenerator = run.getInputGenerator();
        	
            if(dataset.getSource() != DatasetSource.DTA_SELECT) {
                List<Integer> piProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
                
                for(int piProteinId: piProteinIds) {
                    List<ProteinferPeptide> peptides = peptDao.loadPeptidesForProteinferProtein(piProteinId);
                    
                    for(ProteinferPeptide pept: peptides) {
                    	
                    	for(ProteinferIon ion: pept.getIonList()) {
                    		ProteinferSpectrumMatch psm = ion.getBestSpectrumMatch();
                    		int resultId = psm.getResultId();
                    		MsSearchResult result = resLoader.getResult(resultId, inputGenerator);
                    		
                    		String modifiedSeq = null;
                    		String peptideKey = null;
							try {
								modifiedSeq = result.getResultPeptide().getModifiedPeptide();
								peptideKey = modifiedSeq+"_"+ion.getCharge();
							} catch (ModifiedSequenceBuilderException e) {
								log.error("Error building modified sequence for ion: "+ion.getId());
								peptideKey = "ERROR";
							}
                    		ComparisonPeptide compPept = peptSeqMap.get(peptideKey);
                    		
                    		if(compPept == null) {
                                compPept = new ComparisonPeptide(nrseqProteinId);
                                compPept.setSequence(modifiedSeq);
                                compPept.setCharge(ion.getCharge());
                                peptSeqMap.put(peptideKey, compPept);
                             }
                    		
                    		DatasetPeptideInformation dpi = new DatasetPeptideInformation(dataset);
                            dpi.setPresent(true);
                            dpi.setSpectrumCount(ion.getSpectrumCount());
                            dpi.setUnique(pept.isUniqueToProtein());
                            compPept.addDatasetInformation(dpi);
                    		
                    	}
                    }
                }
            }
        }
        
        PeptideComparisonDataset pd = new PeptideComparisonDataset(nrseqProteinId);
        pd.setDatasets(datasets);
        pd.setPeptides(new ArrayList<ComparisonPeptide>(peptSeqMap.values()));
        return pd;
    }
}
