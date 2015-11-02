/**
 * DatasetBuilder.java
 * @author Vagisha Sharma
 * Apr 17, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare.dataset;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunSummaryDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunSummaryDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferRunSummary;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRunSummary;


/**
 * 
 */
public class DatasetBuilder {

    private static DatasetBuilder instance;
    
    private ProteinferDAOFactory fact;
    private ProteinferRunDAO runDao;
    private ProteinferSpectrumMatchDAO specDao;
    private ProteinferRunSummaryDAO piRunSummaryDao;
    private ProteinProphetRunSummaryDAO prophetRunSummaryDao;
    
    
    private DatasetBuilder() {
        fact = ProteinferDAOFactory.instance();
        runDao = fact.getProteinferRunDao();
        specDao = fact.getProteinferSpectrumMatchDao();
        piRunSummaryDao = fact.getProteinferRunSummaryDao();
        prophetRunSummaryDao = fact.getProteinProphetRunSummaryDao();
    }
    
    public static DatasetBuilder instance() {
        if(instance == null)
            instance = new DatasetBuilder();
        return instance;
    }
    
    public Dataset buildDataset(int datasetId) {
        
        ProteinferRun run = runDao.loadProteinferRun(datasetId);
        if(run == null)
            return null;
        DatasetSource source = DatasetSource.getSourceForProtinferProgram(run.getProgram());
        Dataset dataset = new Dataset(datasetId, source);
        dataset.setDatasetComments(run.getComments());
        dataset.setDatasetName(run.getName());
        initDataset(dataset);
        return dataset;
    }
    
    private void initDataset(Dataset dataset) {
    	
    	if(dataset.getSource() == DatasetSource.PROTINFER) {
    		ProteinferRunSummary summary = piRunSummaryDao.load(dataset.getDatasetId());
    		if(summary != null) {
    			dataset.setSpectrumCount(summary.getSpectrumCount());
    			dataset.setMaxProteinSpectrumCount(summary.getMaxSpectrumCount());
    			dataset.setMinProteinSpectrumCount(summary.getMinSpectrumCount());
    			return;
    		}
    	}
    	else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
    		ProteinProphetRunSummary summary = prophetRunSummaryDao.load(dataset.getDatasetId());
    		if(summary != null) {
    			dataset.setSpectrumCount(summary.getSpectrumCount());
    			dataset.setMaxProteinSpectrumCount(summary.getMaxSpectrumCount());
    			dataset.setMinProteinSpectrumCount(summary.getMinSpectrumCount());
    			return;
    		}
    	}
    	// Either we did not find a summary entry for this protein inference or we don't save summary 
    	// information for this protein inference program.
        dataset.setSpectrumCount(specDao.getSpectrumCountForPinferRun(dataset.getDatasetId()));
        dataset.setMaxProteinSpectrumCount(specDao.getMaxSpectrumCountForPinferRunProtein(dataset.getDatasetId()));
        dataset.setMinProteinSpectrumCount(specDao.getMinSpectrumCountForPinferRunProtein(dataset.getDatasetId()));
    }
    
    public SelectableDataset buildSelectableDataset(int datasetId) {
        ProteinferRun run = runDao.loadProteinferRun(datasetId);
        if(run == null)
            return null;
        DatasetSource source = DatasetSource.getSourceForProtinferProgram(run.getProgram());
        SelectableDataset dataset = new SelectableDataset(datasetId, source);
        dataset.setSelected(false);
        dataset.setDatasetComments(run.getComments());
        dataset.setDatasetName(run.getName());
        return dataset;
    }
    
    public FilterableDataset buildFilterableDataset(int datasetId) {
        Dataset dataset = buildDataset(datasetId);
        if(dataset == null)
            return null;
        
        if(dataset.getSource() == DatasetSource.PROTINFER)
            return buildProtInferFilterableDataset(dataset);
        else if (dataset.getSource() == DatasetSource.PROTEIN_PROPHET)
            return buildProteinProphetFilterableDataset(dataset);
        return null;
    }

    private ProteinferDataset buildProtInferFilterableDataset(Dataset dataset) {
        ProteinferDataset prDataset = new ProteinferDataset(dataset);
        return prDataset;
    }
    
    private ProteinProphetDataset buildProteinProphetFilterableDataset(Dataset dataset) {
        ProteinProphetDataset prDataset = new ProteinProphetDataset(dataset);
        ProteinferDAOFactory fact = ProteinferDAOFactory.instance();
        prDataset.setRoc(fact.getProteinProphetRocDao().loadRoc(dataset.getDatasetId()));
        prDataset.setProbabilityForDefaultError();
        return prDataset;
    }
}
