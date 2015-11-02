/**
 * ViewXtandemResults.java
 * @author Vagisha Sharma
 * Oct 27, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.experiment.TabularXtandemResults;
import org.yeastrc.experiment.XtandemResultPlus;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.xtandem.XtandemSearchResultDAO;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.SORT_BY;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.domain.search.xtandem.XtandemSearchResult;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ViewXtandemResults extends Action {

    private static final Logger log = Logger.getLogger(ViewXtandemResults.class.getName());

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

        // Get the form
        XtandemFilterResultsForm myForm = (XtandemFilterResultsForm)form;

        int searchId = myForm.getSearchId();
        if(searchId == 0) {
            try {
                String strID = request.getParameter("ID");
                if(strID != null)
                    searchId = Integer.parseInt(strID);


            } catch (NumberFormatException nfe) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Mascot search"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
        }
        // If we still don't have a valid id, return an error
        if(searchId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Mascot search"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }

        // If this is a brand new form
        if(myForm.getSearchId() == 0) {
            myForm.setSearchId(searchId);
            myForm.setShowModified(true);
            myForm.setShowUnmodified(true);
            myForm.setExactPeptideMatch(true);
            myForm.setSortBy(SORT_BY.FILE_SEARCH);
            myForm.setSortOrder(SORT_ORDER.ASC);
            myForm.setMaxRank("1");
        }


        // TODO Does the user have access to look at these results? 

        // GET THE SUMMARY 
        int projectId = 0;
        int experimentId = 0;
        String program = null;
        int numResults = 0;
        int numResultsFiltered = 0;


        MsSearch search = DAOFactory.instance().getMsSearchDAO().loadSearch(searchId);
        program = search.getSearchProgram().toString();
        if(search.getSearchProgramVersion() != null) program = program+" "+search.getSearchProgramVersion();
        experimentId = search.getExperimentId();
        if(experimentId != 0) {
            // Get the project for this experiment
            projectId = ProjectExperimentDAO.instance().getProjectIdForExperiment(experimentId);
        }



        // Get ALL the filtered and sorted resultIds
        XtandemSearchResultDAO mresDao = DAOFactory.instance().getXtandemResultDAO();
        numResults = mresDao.numSearchResults(searchId);
        List<Integer> resultIds = mresDao.loadResultIdsForSearch(searchId,
                myForm.getFilterCriteria(), myForm.getSortCriteria());
        numResultsFiltered = resultIds.size();




        // Extract the ones we will display
        int numResultsPerPage = myForm.getNumPerPage();
        int pageNum = myForm.getPageNum();
        if(pageNum <= 0) {
            pageNum = 1;
            myForm.setPageNum(pageNum);
        }
        ResultsPager pager = ResultsPager.instance();
        boolean desc = false;
        if(myForm.getSortOrder() != null)
            desc = myForm.getSortOrder() == SORT_ORDER.DESC ? true : false;
        // TODO if the pageNum is out of range .....
        List<Integer> forPage = pager.page(resultIds, pageNum, numResultsPerPage, desc);



        // Get details for the result we will display
        Map<Integer, String> filenameMap = getFileNames(searchId);
        List<XtandemResultPlus> results = new ArrayList<XtandemResultPlus>(numResultsPerPage);

        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        for(Integer resultId: forPage) {
            XtandemSearchResult result = mresDao.load(resultId);
            MsScan scan = scanDao.loadScanLite(result.getScanId());
            XtandemResultPlus resPlus = new XtandemResultPlus(result, scan);
            resPlus.setFilename(filenameMap.get(result.getRunSearchId()));
            results.add(resPlus);
        }


        // Set up for tabular display
        boolean useEvalue = DAOFactory.instance().getSequestSearchDAO().hasEvalue(searchId);
        TabularXtandemResults tabResults = new TabularXtandemResults(results, useEvalue);
        tabResults.setCurrentPage(pageNum);
        tabResults.setNumPerPage(numResultsPerPage);
        int pageCount = pager.getPageCount(resultIds.size(), numResultsPerPage);
        tabResults.setLastPage(pageCount);
        List<Integer> pageList = pager.getPageList(resultIds.size(), pageNum, numResultsPerPage);
        tabResults.setDisplayPageNumbers(pageList);
        tabResults.setSortedColumn(myForm.getSortBy());
        tabResults.setSortOrder(myForm.getSortOrder());


        // required attributes in the request
        request.setAttribute("projectId", projectId);
        request.setAttribute("experimentId", experimentId);
        request.setAttribute("program", program);
        request.setAttribute("numResults", numResults);
        request.setAttribute("numResultsFiltered", numResultsFiltered);

        request.setAttribute("filterForm", myForm);
        request.setAttribute("results", tabResults);
        request.setAttribute("searchId", searchId);


        // Forward them on to the happy success view page!
        return mapping.findForward("Success");


    }

    private Map<Integer, String> getFileNames(int searchId) {

        MsRunSearchDAO saDao = DAOFactory.instance().getMsRunSearchDAO();
        List<Integer> runSearchIds = saDao.loadRunSearchIdsForSearch(searchId);

        Map<Integer, String> filenameMap = new HashMap<Integer, String>(runSearchIds.size()*2);
        for(int runSearchId: runSearchIds) {
            String filename = saDao.loadFilenameForRunSearch(runSearchId);
            filenameMap.put(runSearchId, filename);
        }
        return filenameMap;
    }
}
