package org.yeastrc.ms.dao.protinfer.ibatis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.ibatis.BaseSqlMapDAO;
import org.yeastrc.ms.dao.protinfer.GenericProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.protinfer.GenericProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferInput;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.ProteinferStatus;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.extensions.ParameterSetter;
import com.ibatis.sqlmap.client.extensions.ResultGetter;
import com.ibatis.sqlmap.client.extensions.TypeHandlerCallback;

public class ProteinferRunDAO extends BaseSqlMapDAO implements GenericProteinferRunDAO<ProteinferInput, ProteinferRun> {

    private static final String sqlMapNameSpace = "ProteinferRun";
    
    public ProteinferRunDAO(SqlMapClient sqlMap) {
        super(sqlMap);
    }

    @Override
    public int save(GenericProteinferRun<?> run) {
        return super.saveAndReturnId(sqlMapNameSpace+".insert", run);
    }
    
    public void update(GenericProteinferRun<?> run) {
        super.update(sqlMapNameSpace+".update", run);
    }
    
    public ProteinferRun loadProteinferRun(int proteinferId) {
        return (ProteinferRun) super.queryForObject(sqlMapNameSpace+".select", proteinferId);
    }
    
    @Override
    public int getMaxProteinHitCount(int proteinferId) {
        Integer count = (Integer)queryForObject(sqlMapNameSpace+".getMaxProteinHitCount", proteinferId);
        if(count != null)   return count;
        return 0;
    }
    
    @Override
    public List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds) {
        if(inputIds.size() == 0) 
            return new ArrayList<Integer>(0);
        
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for(Integer id: inputIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        return super.queryForList(sqlMapNameSpace+".selectPinferIdsForInputIds", buf.toString());
    }
    
    public List<Integer> loadProteinferIdsForInputIds(List<Integer> inputIds, Program inputGenerator) {
        if(inputIds.size() == 0) 
            return new ArrayList<Integer>(0);
        
        StringBuilder buf = new StringBuilder();
        buf.append("(");
        for(Integer id: inputIds) {
            buf.append(id+",");
        }
        buf.deleteCharAt(buf.length() - 1);
        buf.append(")");
        
        Map<String, String> map = new HashMap<String, String>(4);
        map.put("inputGenerator", inputGenerator.name());
        map.put("inputIds", buf.toString());
        
        return super.queryForList(sqlMapNameSpace+".selectPinferIdsForInputIdsProgram", map);
    }
    
    @Override
    public List<Integer> loadSearchIdsForProteinferRun(int pinferId) {
        ProteinferRun run = loadProteinferRun(pinferId);
        if(run == null) {
            throw new IllegalArgumentException("No protein inference run for ID: "+pinferId);
        }
        Program program = run.getInputGenerator();
        if(Program.isSearchProgram(program)) {
            return queryForList(sqlMapNameSpace+".selectSearchIdsForRunSearchIds", pinferId);
        }
        else if(Program.isAnalysisProgram(program)) {
            return queryForList(sqlMapNameSpace+".selectSearchIdsForRunSearchAnalysisIds", pinferId);
        }
        else {
            throw new IllegalArgumentException("Unknown input genrator for protein inference: "+program.name());
        }
    }
    
    @Override
	public List<Integer> loadProteinferIdsForExperiment(int experimentId) {
		
		Set<Integer> pinferRunIds = new HashSet<Integer>();
		
		MsSearchAnalysisDAO analysisDao = DAOFactory.instance().getMsSearchAnalysisDAO();
		
        // Get the searchIds for this experiment
        List<Integer> searchIds = DAOFactory.instance().getMsSearchDAO().getSearchIdsForExperiment(experimentId);
        
        for(int searchId: searchIds) {
        	
            List<Integer> piRunIds = getPinferRunIdsForSearch(searchId);
            pinferRunIds.addAll(piRunIds);
            
            // for each search get the analysis (e.g. Percolator or PeptideProphet results)
            List<Integer> analysisIds = analysisDao.getAnalysisIdsForSearch(searchId);

            
            // get the protein inference IDs associated with each analysis
            for(int analysisId: analysisIds) {

            	pinferRunIds.addAll(getProteinferIdsForMsSearchAnalysis(analysisId));
            }

        }
        
        if(pinferRunIds == null || pinferRunIds.size() == 0)
            return new ArrayList<Integer>(0);
        
        return new ArrayList<Integer>(pinferRunIds);
		
	}
	

    private List<Integer> getPinferRunIdsForSearch(int msSearchId) {
        
    	MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
    	MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        
        // load the search
        MsSearch search = searchDao.loadSearch(msSearchId);
        
        Set<Integer> pinferIdsSet = new HashSet<Integer>();
        
        // first get all the runSearchIds for this search
        List<Integer> msRunSearchIds = runSearchDao.loadRunSearchIdsForSearch(msSearchId);
        
        // load protein inference results for the runSearchIDs where the input generator was 
        // the search program
        List<Integer> searchPinferIds = loadProteinferIdsForInputIds(msRunSearchIds, search.getSearchProgram());
        pinferIdsSet.addAll(searchPinferIds);
        
        return new ArrayList<Integer>(pinferIdsSet);
    }
    
    public List<Integer> getProteinferIdsForMsSearchAnalysis(int msSearchAnalysisId) {

    	MsSearchAnalysisDAO analysisDao = DAOFactory.instance().getMsSearchAnalysisDAO();
    	MsRunSearchAnalysisDAO rsanalysisDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
    	
    	 // load the analysis
        MsSearchAnalysis analysis = analysisDao.load(msSearchAnalysisId);
        // get all the runSearchAnalysisIds for each analysis done on the search
        List<Integer> runSearchAnalysisIds = rsanalysisDao.getRunSearchAnalysisIdsForAnalysis(msSearchAnalysisId);
        
        // load protein inference results for the runSearchAnalysisIDs where the input generator was 
        // the analysis program
        List<Integer> piRunIds = loadProteinferIdsForInputIds(runSearchAnalysisIds, analysis.getAnalysisProgram());

    	Set<Integer> pinferRunIds = new HashSet<Integer>();
    	pinferRunIds.addAll(piRunIds);

    	return new ArrayList<Integer>(pinferRunIds);
    }
    
    public void delete(int pinferId) {
        super.delete(sqlMapNameSpace+".delete", pinferId);
    }
    
    /**
     * Type handler for converting between ProteinferStatus and SQL's CHAR type.
     */
    public static final class ProteinferStatusTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToProteinferStatus(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ProteinferStatus status = (ProteinferStatus) parameter;
            if (status == null)
                setter.setNull(java.sql.Types.CHAR);
            else
                setter.setString(String.valueOf(status.getStatusChar()));
        }

        public Object valueOf(String s) {
            return stringToProteinferStatus(s);
        }
        
        private ProteinferStatus stringToProteinferStatus(String statusStr) {
            if (statusStr == null)
                throw new IllegalArgumentException("String representing ProteinferStatus cannot be null");
            if (statusStr.length() != 1)
                throw new IllegalArgumentException("Cannot convert "+statusStr+" to ProteinferStatus");
            ProteinferStatus status = ProteinferStatus.getStatusForChar(Character.valueOf(statusStr.charAt(0)));
            if (status == null)
                throw new IllegalArgumentException("Invalid ProteinferStatus value: "+statusStr);
            return status;
        }
    }
    
    /**
     * Type handler for converting between ProteinferStatus and SQL's CHAR type.
     */
    public static final class ProteinferProgramTypeHandler implements TypeHandlerCallback {

        public Object getResult(ResultGetter getter) throws SQLException {
            return stringToProteinferProgram(getter.getString());
        }

        public void setParameter(ParameterSetter setter, Object parameter)
                throws SQLException {
            ProteinInferenceProgram program = (ProteinInferenceProgram) parameter;
            if (program == null)
                setter.setNull(java.sql.Types.VARCHAR);
            else
                setter.setString(program.name());
        }

        public Object valueOf(String s) {
            return stringToProteinferProgram(s);
        }
        
        private ProteinInferenceProgram stringToProteinferProgram(String programStr) {
            if (programStr == null)
                throw new IllegalArgumentException("String representing ProteinInferenceProgram cannot be null");
            ProteinInferenceProgram program = ProteinInferenceProgram.getProgramForName(programStr);
            if (program == null)
                throw new IllegalArgumentException("Invalid ProteinInferenceProgram value: "+programStr);
            return program;
        }
    }

}
