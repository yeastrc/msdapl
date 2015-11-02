/**
 * ListSearches.java
 * @author Vagisha Sharma
 * Jan 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.project.Project;
import org.yeastrc.www.proteinfer.ExperimentSearcher;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary.ProteinInferIputFile;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ListProteinInferInputGroupAction extends Action {


    private static final Logger log = Logger.getLogger(ListProteinInferInputGroupAction.class);
    
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        // look for the search / analysis ids the user already has
        String excludeInputGrps = request.getParameter("excludeInputGroups");
        List<Integer> excludeInputGrpList = new ArrayList<Integer>();
        if(excludeInputGrps != null) {
            excludeInputGrps = excludeInputGrps.replaceAll("\\s", "");
            String[] tokens = excludeInputGrps.split(",");
            for(String tok: tokens) {
                excludeInputGrpList.add(Integer.parseInt(tok));
            }
        }
        
        String inputGenerator = request.getParameter("inputGenerator");
        Program prog = Program.instance(inputGenerator);
        if(prog == Program.UNKNOWN) {
            ActionErrors errors = new ActionErrors();
            errors.add("proteinfer", new ActionMessage("error.proteinfer.invalid.program", inputGenerator));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // get the projects for this user
        @SuppressWarnings("unchecked")
        List<Project> userProjects = user.getProjects();
        
        
        if(Program.isSearchProgram(prog)) {
            List<ProjectInputGroup> inputGroups = getProjectSearches(excludeInputGrpList, prog, userProjects);
            request.setAttribute("projectInputGroups", inputGroups);
            request.setAttribute("inputType", InputType.SEARCH.name());
        }
        else if(Program.isAnalysisProgram(prog)) {
            List<ProjectInputGroup> inputGroups = getProjectSearchAnalyses(excludeInputGrpList, prog, userProjects);
            request.setAttribute("projectInputGroups", inputGroups);
            request.setAttribute("inputType", InputType.ANALYSIS.name());
        }
        request.setAttribute("program", prog.displayName());
        
        // Go!
        return mapping.findForward("Success");
    }

    private List<ProjectInputGroup> getProjectSearches(List<Integer> excludeInputGrpList, 
            Program prog, List<Project> userProjects)
            throws SQLException {
        
        ExperimentSearcher searcher = ExperimentSearcher.instance();
        List<ProjectInputGroup> projSearches = new ArrayList<ProjectInputGroup>(userProjects.size());
        
        
        MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();
        MsRunSearchDAO runSearchDao = DAOFactory.instance().getMsRunSearchDAO();
        
        for(Project proj: userProjects) {
            System.out.println("Getting search ids for project: "+proj.getID());
            List<Integer> searchIds = searcher.getSearchIdsForProject(proj);
            for(int searchId: searchIds) {
                // if the user already has this search don't add it
                if(excludeInputGrpList.contains(searchId))
                    continue;
                
                // make sure this search was done using the same search program
                // as the other searches the user has
                MsSearch search = searchDao.loadSearch(searchId);
                if(search.getSearchProgram() == prog) {
                    List<Integer> runSearchIds = runSearchDao.loadRunSearchIdsForSearch(search.getId());
                    List<ProteinInferIputFile> runSearchFiles = new ArrayList<ProteinInferIputFile>(runSearchIds.size());
                    for(int rsId: runSearchIds) {
                        String filename = runSearchDao.loadFilenameForRunSearch(rsId);
                        runSearchFiles.add(new ProteinInferIputFile(rsId, filename));
                    }
                    Collections.sort(runSearchFiles, new Comparator<ProteinInferIputFile>() {
                        @Override
                        public int compare(ProteinInferIputFile o1,
                                ProteinInferIputFile o2) {
                            return o1.getRunName().compareTo(o2.getRunName());
                        }});
                    projSearches.add(new ProjectInputGroup(proj.getID(), search, runSearchFiles));
                }
            }
        }
        return projSearches;
    }
    
    
    private List<ProjectInputGroup> getProjectSearchAnalyses(List<Integer> excludeInputGrpList, 
            Program prog, List<Project> userProjects)
            throws SQLException {
        
        ExperimentSearcher searcher = ExperimentSearcher.instance();
        List<ProjectInputGroup> projAnalyses = new ArrayList<ProjectInputGroup>(userProjects.size());
        
        MsSearchAnalysisDAO saDao = DAOFactory.instance().getMsSearchAnalysisDAO();
        MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();

        // If we are loading Percolator results make sure the versions are compatible
        String percolatorVersion = null;
        if(prog == Program.PERCOLATOR) {
            MsSearchAnalysis percAnalysis = saDao.load(excludeInputGrpList.get(0)); // the size of this list should always be > 0
            percolatorVersion = percAnalysis.getAnalysisProgramVersion();
        }
        
        for(Project proj: userProjects) {
            System.out.println("Getting analysis ids for project: "+proj.getID());
            List<Integer> analysisIds = searcher.getSearchAnalysisIdsForProject(proj);
            for(int analysisId: analysisIds) {
                // if the user already has this analysis don't add it
                if(excludeInputGrpList.contains(analysisId))
                    continue;

                // make sure this analysis was done using the same analysis program
                // as the other analyses the user has
                MsSearchAnalysis analysis = saDao.load(analysisId);
                if(analysis.getAnalysisProgram() == prog) {
                    
                    // check for Percolator version
                    if(prog == Program.PERCOLATOR) {
                        if(!compatiblePercolatorVersions(percolatorVersion, analysis.getAnalysisProgramVersion()))
                            continue;
                                
                    }
                    List<Integer> rsAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(analysisId);
                    List<ProteinInferIputFile> runSearchAnalysisFiles = new ArrayList<ProteinInferIputFile>(rsAnalysisIds.size());
                    for(int rsaId: rsAnalysisIds) {
                        String filename = rsaDao.loadFilenameForRunSearchAnalysis(rsaId);
                        runSearchAnalysisFiles.add(new ProteinInferIputFile(rsaId, filename));
                    }
                    Collections.sort(runSearchAnalysisFiles, new Comparator<ProteinInferIputFile>() {
                        @Override
                        public int compare(ProteinInferIputFile o1,
                                ProteinInferIputFile o2) {
                            return o1.getRunName().compareTo(o2.getRunName());
                        }});
                    projAnalyses.add(new ProjectInputGroup(proj.getID(), analysis, runSearchAnalysisFiles));
                }
            }
        }
        return projAnalyses;
    }

    private boolean compatiblePercolatorVersions(String percolatorVersion, String analysisProgramVersion) {
        
        if(percolatorVersion == null || analysisProgramVersion == null)
            return false;
        float reqVer = Float.parseFloat(percolatorVersion);
        float myVer = 0;
        try { myVer = Float.parseFloat(analysisProgramVersion); }
        catch(NumberFormatException e) {return false;}
        
        if(reqVer < 1.06f && myVer >= 1.06f)
            return false;
        if(reqVer >= 1.06f && myVer < 1.06f)
            return false;
        
        // Percolator pre v 1.15 did not calculate peptide-level q-values.
        // TODO: There should a warning mechanism in the interface prompting users to use only PSM-level cutoffs 
        // when mixing results from the old and new versions
        // For now, older version experiments will not be listed as the options.
        if(reqVer >= 1.15f && myVer < 1.15f) {
        	return false;
        }
        return true;
    }
}
