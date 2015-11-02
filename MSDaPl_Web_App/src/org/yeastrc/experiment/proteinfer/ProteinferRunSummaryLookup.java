/**
 * 
 */
package org.yeastrc.experiment.proteinfer;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunSummaryDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunSummaryDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRunSummary;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRunSummary;

/**
 * ProteinferRunSummaryLookup.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public class ProteinferRunSummaryLookup {

	
	private ProteinferRunSummaryLookup () {}
	
	public static ProteinferRunSummary getIdPickerRunSummary(int piRunId) {
		
		ProteinferRunSummaryDAO summDao = ProteinferDAOFactory.instance().getProteinferRunSummaryDao();
		
		// First look up in the summary table
		ProteinferRunSummary summary = summDao.load(piRunId);
		if(summary != null) {
			return summary;
		}
		
		// Extract information from the protein inference tables
        IdPickerProteinDAO proteinDao = ProteinferDAOFactory.instance().getIdPickerProteinDao();
        ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
        
        int parsimProteinCount = proteinDao.getIdPickerParsimoniousProteinIds(piRunId).size();
        int parsimProteinGroupCount = proteinDao.getIdPickerParsimoniousGroupCount(piRunId);
        int peptSeqCount = peptDao.getUniquePeptideSequenceCountForRun(piRunId);
        int ionCount = peptDao.getUniqueIonCountForRun(piRunId);
        
        
        summary = new ProteinferRunSummary();
        summary.setPiRunId(piRunId);
        summary.setParsimIndistGroupCount(parsimProteinGroupCount);
        summary.setParsimProteinCount(parsimProteinCount);
        summary.setUniqPeptSeqCount(peptSeqCount);
        summary.setUniqIonCount(ionCount);

        return summary;
	}
	
	public static ProteinProphetRunSummary getProteinProphetSummary(int piRunId) {
		
		ProteinProphetRunSummaryDAO summDao = ProteinferDAOFactory.instance().getProteinProphetRunSummaryDao();
		
		// First look up in the summary table
		ProteinProphetRunSummary summary = summDao.load(piRunId);
		if(summary != null)
			return summary;
		
		// Extract information from the protein inference tables
		ProteinProphetProteinDAO prophetProtDao = ProteinferDAOFactory.instance().getProteinProphetProteinDao();
        ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
        
        int proteinCount = prophetProtDao.getProteinferProteinIds(piRunId, true).size();
        int iGroupCount = prophetProtDao.getIndistinguishableGroupCount(piRunId, true);
        int prophetGrpCount = prophetProtDao.getProteinProphetGroupCount(piRunId, true);
        int peptSeqCount = peptDao.getUniquePeptideSequenceCountForRun(piRunId);
        int ionCount = peptDao.getUniqueIonCountForRun(piRunId);
        
        summary = new ProteinProphetRunSummary();
        summary.setPiRunId(piRunId);
        summary.setProteinCount(proteinCount);
        summary.setIndistGroupCount(iGroupCount);
        summary.setProphetGroupCount(prophetGrpCount);
        summary.setUniqPeptSeqCount(peptSeqCount);
        summary.setUniqIonCount(ionCount);

        return summary;
	}
}
