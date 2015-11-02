import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferPeptideDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunSummaryDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferSpectrumMatchDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunSummaryDAO;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferRunSummary;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRunSummary;

/**
 * ProtinferSummaryUpdater.java
 * @author Vagisha Sharma
 * Oct 2, 2010
 */


/**
 * 
 */
public class ProtinferSummaryUpdater {

	public static void main(String[] args) throws SQLException {

		ProteinferDAOFactory pinferFactory = ProteinferDAOFactory.instance();
		ProteinferRunDAO runDao = pinferFactory.getProteinferRunDao();

		List<Integer> pinferIds = getProteinInferRunIds();

		for(Integer pinferId: pinferIds) {

			ProteinferRun run = runDao.loadProteinferRun(pinferId);

			System.out.println("Updating summary for "+pinferId);
			ProteinInferenceProgram program = run.getProgram();
			if(ProteinInferenceProgram.isIdPicker(program)) {
				saveIdPickerRunSummary(pinferId);
			}
			else {
				saveProteinProphetSummary(pinferId);
			}
		}
	}

	public static void saveIdPickerRunSummary(int piRunId) {

		ProteinferRunSummaryDAO summDao = ProteinferDAOFactory.instance().getProteinferRunSummaryDao();


		// Extract information from the protein inference tables
		IdPickerProteinDAO proteinDao = ProteinferDAOFactory.instance().getIdPickerProteinDao();
		ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
		ProteinferSpectrumMatchDAO specDao = ProteinferDAOFactory.instance().getProteinferSpectrumMatchDao();

		int parsimProteinCount = proteinDao.getIdPickerParsimoniousProteinIds(piRunId).size();
		int parsimProteinGroupCount = proteinDao.getIdPickerParsimoniousGroupCount(piRunId);
		int peptSeqCount = peptDao.getUniquePeptideSequenceCountForRun(piRunId);
		int ionCount = peptDao.getUniqueIonCountForRun(piRunId);
		int spectrumCount = specDao.getSpectrumCountForPinferRun(piRunId);
		int minSpectrumCount = specDao.getMinSpectrumCountForPinferRunProtein(piRunId);
		int maxSpectrumCount = specDao.getMaxSpectrumCountForPinferRunProtein(piRunId);


		// First look up in the summary table
		ProteinferRunSummary summary = summDao.load(piRunId);
		if(summary != null) {
			// check what we already have in the database with what we are about to save
			boolean mismatch = false;
			if(summary.getParsimIndistGroupCount() != parsimProteinGroupCount)
				mismatch = true;
			if(summary.getParsimProteinCount() != parsimProteinCount)
				mismatch = true;
			if(summary.getUniqPeptSeqCount() != peptSeqCount)
				mismatch = true;
			if(summary.getUniqIonCount() != ionCount)
				mismatch = true;
			
			if(mismatch) {
				System.out.println("Mismatch found for protein inference ID: "+piRunId);
				System.out.println("Calculated: groupCount:"+parsimProteinGroupCount+
						"; proteinCount:"+parsimProteinCount+"; peptCount:"+peptSeqCount+"; ionCount:"+ionCount);
				System.exit(2);
			}
			
			summary.setSpectrumCount(spectrumCount);
			summary.setMinSpectrumCount(minSpectrumCount);
			summary.setMaxSpectrumCount(maxSpectrumCount);
			
			summDao.update(summary);
		}
		else {
			summary = new ProteinferRunSummary();
			summary.setPiRunId(piRunId);
			summary.setParsimIndistGroupCount(parsimProteinGroupCount);
			summary.setParsimProteinCount(parsimProteinCount);
			summary.setUniqPeptSeqCount(peptSeqCount);
			summary.setUniqIonCount(ionCount);
			summary.setSpectrumCount(spectrumCount);
			summary.setMinSpectrumCount(minSpectrumCount);
			summary.setMaxSpectrumCount(maxSpectrumCount);
			// Save in the summary table
			summDao.save(summary);
		}
		
		return;
	}

	public static void saveProteinProphetSummary(int piRunId) {

		ProteinProphetRunSummaryDAO summDao = ProteinferDAOFactory.instance().getProteinProphetRunSummaryDao();


		// Extract information from the protein inference tables
		ProteinProphetProteinDAO prophetProtDao = ProteinferDAOFactory.instance().getProteinProphetProteinDao();
		ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
		ProteinferSpectrumMatchDAO specDao = ProteinferDAOFactory.instance().getProteinferSpectrumMatchDao();

		int proteinCount = prophetProtDao.getProteinferProteinIds(piRunId, true).size();
		int iGroupCount = prophetProtDao.getIndistinguishableGroupCount(piRunId, true);
		int prophetGrpCount = prophetProtDao.getProteinProphetGroupCount(piRunId, true);
		int peptSeqCount = peptDao.getUniquePeptideSequenceCountForRun(piRunId);
		int ionCount = peptDao.getUniqueIonCountForRun(piRunId);
		int spectrumCount = specDao.getSpectrumCountForPinferRun(piRunId);
		int minSpectrumCount = specDao.getMinSpectrumCountForPinferRunProtein(piRunId);
		int maxSpectrumCount = specDao.getMaxSpectrumCountForPinferRunProtein(piRunId);

		// First look up in the summary table
		ProteinProphetRunSummary summary = summDao.load(piRunId);
		if(summary != null) {
			
			// check what we already have in the database with what we are about to save
			boolean mismatch = false;
			if(summary.getProphetGroupCount() != prophetGrpCount)
				mismatch = true;
			if(summary.getIndistGroupCount() != iGroupCount)
				mismatch = true;
			if(summary.getProteinCount() != proteinCount)
				mismatch = true;
			if(summary.getUniqPeptSeqCount() != peptSeqCount)
				mismatch = true;
			if(summary.getUniqIonCount() != ionCount)
				mismatch = true;
			
			if(mismatch) {
				System.out.println("Mismatch found for ProteinProphet inference ID: "+piRunId);
				System.out.println("Calculated: prophetGrpCount:"+prophetGrpCount+
						"; indistGrpCount:"+iGroupCount+
						"; proteinCount:"+proteinCount+"; peptCount:"+peptSeqCount+"; ionCount:"+ionCount);
				System.exit(2);
			}
			
			summary.setSpectrumCount(spectrumCount);
			summary.setMinSpectrumCount(minSpectrumCount);
			summary.setMaxSpectrumCount(maxSpectrumCount);
			
			summDao.update(summary);
		}
		else {
			summary = new ProteinProphetRunSummary();
			summary.setPiRunId(piRunId);
			summary.setProteinCount(proteinCount);
			summary.setIndistGroupCount(iGroupCount);
			summary.setProphetGroupCount(prophetGrpCount);
			summary.setUniqPeptSeqCount(peptSeqCount);
			summary.setUniqIonCount(ionCount);
			summary.setSpectrumCount(spectrumCount);
			summary.setMinSpectrumCount(minSpectrumCount);
			summary.setMaxSpectrumCount(maxSpectrumCount);

			// Save in the summary table
			summDao.save(summary);
		}

		return;
	}

	private static List<Integer> getProteinInferRunIds() throws SQLException {

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DAOFactory.instance().getConnection();
			String sql = "SELECT id FROM msProteinInferRun ORDER BY id DESC";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			List<Integer> pinferIds = new ArrayList<Integer>();
			while(rs.next()) {
				pinferIds.add(rs.getInt("id"));
			}
			return pinferIds;
		}
		finally {
			if(conn != null) try {conn.close();}catch(Exception e){}
			if(stmt != null) try {stmt.close();}catch(Exception e){}
			if(rs != null) try {rs.close();}catch(Exception e){}
		}
	}
}
