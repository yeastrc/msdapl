/**
 * ProteinInferInputGetter.java
 * @author Vagisha Sharma
 * Jan 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.MsSearchDatabase;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;

/**
 * 
 */
public class ProteinInferInputGetter {

    private static final ProteinInferInputGetter instance = new ProteinInferInputGetter();

    private ProteinInferInputGetter() {}

    private static final DAOFactory daoFactory = DAOFactory.instance();
    
    public static ProteinInferInputGetter instance() {
        return instance;
    }

    public ProteinInferInputSummary getInputSearchSummary(MsSearch search) {
        
        ProteinInferInputSummary searchSummary = new ProteinInferInputSummary();
        searchSummary.setInputGroupId(search.getId());
        
        // get the name of the search program
        searchSummary.setProgramName(search.getSearchProgram().displayName());
        searchSummary.setProgramVersion(search.getSearchProgramVersion());

        // get the name(s) of the search databases.
        StringBuilder databases = new StringBuilder();
        for(MsSearchDatabase db: search.getSearchDatabases()) {
            databases.append(", ");
            databases.append(db.getDatabaseFileName());
        }
        if(databases.length() > 0)  databases.deleteCharAt(0);
        searchSummary.setSearchDatabase(databases.toString());

        // get the files
        MsRunSearchDAO runSearchDao = daoFactory.getMsRunSearchDAO();
        List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(search.getId());

        for (int id: runSearchIds) {
            String filename = runSearchDao.loadFilenameForRunSearch(id);
            ProteinInferIputFile rs = new ProteinInferIputFile(id, filename);
            rs.setIsSelected(true);
            searchSummary.addInputFile(rs);
        }
        Collections.sort(searchSummary.getInputFiles(), new Comparator<ProteinInferIputFile>(){
            public int compare(ProteinInferIputFile o1, ProteinInferIputFile o2) {
                return o1.getRunName().compareToIgnoreCase(o2.getRunName());
            }});
        return searchSummary;
    }
    
    public ProteinInferInputSummary getInputSearchSummary(int searchId) {

        // get the search
        MsSearchDAO searchDao = daoFactory.getMsSearchDAO();
        MsSearch search = searchDao.loadSearch(searchId);
        return getInputSearchSummary(search);
    }

    
    public ProteinInferInputSummary getInputAnalysisSummary(MsSearchAnalysis analysis) {
        
        MsSearchAnalysisDAO analysisDao = daoFactory.getMsSearchAnalysisDAO();
        
        ProteinInferInputSummary inputSummary = new ProteinInferInputSummary();
        inputSummary.setInputGroupId(analysis.getId());
        
        // get the name of the analysis program
        inputSummary.setProgramName(analysis.getAnalysisProgram().displayName());
        inputSummary.setProgramVersion(analysis.getAnalysisProgramVersion());
        
        // get the search databases used.
        List<Integer> searchIds = analysisDao.getSearchIdsForAnalysis(analysis.getId());
        MsSearchDAO searchDao = daoFactory.getMsSearchDAO();
        StringBuilder databases = new StringBuilder();
        for(int searchId: searchIds) {
            
            MsSearch search = searchDao.loadSearch(searchId);
            // get the name(s) of the search databases.
            for(MsSearchDatabase db: search.getSearchDatabases()) {
                databases.append(", ");
                databases.append(db.getDatabaseFileName());
            }
        }
        if(databases.length() > 0)  databases.deleteCharAt(0);
        inputSummary.setSearchDatabase(databases.toString());

        // get the list of files
        MsRunSearchAnalysisDAO rsAnalysisDao = daoFactory.getMsRunSearchAnalysisDAO();

        List<Integer> rsAnalysisIds = rsAnalysisDao.getRunSearchAnalysisIdsForAnalysis(analysis.getId());

        for (int id: rsAnalysisIds) {
            String filename = rsAnalysisDao.loadFilenameForRunSearchAnalysis(id);
            ProteinInferIputFile rs = new ProteinInferIputFile(id, filename);
            rs.setIsSelected(true);
            inputSummary.addInputFile(rs);
        }
        Collections.sort(inputSummary.getInputFiles(), new Comparator<ProteinInferIputFile>(){
            public int compare(ProteinInferIputFile o1, ProteinInferIputFile o2) {
                return o1.getRunName().compareToIgnoreCase(o2.getRunName());
            }});
        return inputSummary;
    }
    
    public ProteinInferInputSummary getInputAnalysisSummary(int analysisId) {
        
        MsSearchAnalysisDAO analysisDao = daoFactory.getMsSearchAnalysisDAO();
        MsSearchAnalysis analysis = analysisDao.load(analysisId);
        return getInputAnalysisSummary(analysis);
    }
}
