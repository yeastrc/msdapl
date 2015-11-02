/**
 * IdPickerProteinStatsSaver.java
 * @author Vagisha Sharma
 * Oct 1, 2010
 */
package edu.uwpr.protinfer.idpicker;

import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunSummaryDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRunSummary;

/**
 * 
 */
public class IdPickerStatsSaver {

	private static IdPickerStatsSaver instance = new IdPickerStatsSaver();
	
	private IdPickerStatsSaver() {}
	
	public static IdPickerStatsSaver getInstance() {
		return instance;
	}
	
	public void saveStats(int piRunId) {
		
		// Extract information from the protein inference tables
        IdPickerProteinDAO proteinDao = ProteinferDAOFactory.instance().getIdPickerProteinDao();
        ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
        ProteinferSpectrumMatchDAO specDao = ProteinferDAOFactory.instance().getProteinferSpectrumMatchDao();
        
        int parsimProteinCount = proteinDao.getIdPickerParsimoniousProteinIds(piRunId).size();
        int parsimProteinGroupCount = proteinDao.getIdPickerParsimoniousGroupCount(piRunId);
        int peptSeqCount = peptDao.getUniquePeptideSequenceCountForRun(piRunId);
        int ionCount = peptDao.getUniqueIonCountForRun(piRunId);
        int spectrumCount = specDao.getSpectrumCountForPinferRun(piRunId);
        int minSpecCount = specDao.getMinSpectrumCountForPinferRunProtein(piRunId);
        int maxSpecCount = specDao.getMaxSpectrumCountForPinferRunProtein(piRunId);
        
        
        ProteinferRunSummary summary = new ProteinferRunSummary();
        summary.setPiRunId(piRunId);
        summary.setParsimIndistGroupCount(parsimProteinGroupCount);
        summary.setParsimProteinCount(parsimProteinCount);
        summary.setUniqPeptSeqCount(peptSeqCount);
        summary.setUniqIonCount(ionCount);
        summary.setSpectrumCount(spectrumCount);
        summary.setMinSpectrumCount(minSpecCount);
        summary.setMaxSpectrumCount(maxSpecCount);

		// Save in the summary table
        ProteinferRunSummaryDAO summDao = ProteinferDAOFactory.instance().getProteinferRunSummaryDao();
        summDao.save(summary);
	}
}
