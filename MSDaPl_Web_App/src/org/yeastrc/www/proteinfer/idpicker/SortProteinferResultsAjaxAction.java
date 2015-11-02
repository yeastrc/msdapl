/**
 * SortProteinferResultsAction.java
 * @author Vagisha Sharma
 * Jan 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.ProteinInferPhiliusResultChecker;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;
import org.yeastrc.www.proteinfer.ProteinInferToSpeciesMapper;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SortProteinferResultsAjaxAction extends Action{

    private static final Logger log = Logger.getLogger(SortProteinferResultsAjaxAction.class);

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {

        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        // get the protein inference ID from the request
        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("inferId"));}
        catch(NumberFormatException e){};
        if(pinferId <= 0) {
            log.error("Invalid protein inference run id: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("ERROR: Invalid protein inference ID: "+pinferId);
            return null;
        }

        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        // Check if we already have information in the session
        ProteinFilterCriteria filterCriteria_session = sessionManager.getFilterCriteriaForIdPicker(request, pinferId);
        List<Integer> storedProteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        // If we don't have a filtering criteria in the session return an error
        if(filterCriteria_session == null || storedProteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
        	// redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale protein inference ID: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("STALE_ID");
            return null;
        }

        // Protein filter criteria from the session
        PeptideDefinition peptideDef = filterCriteria_session.getPeptideDefinition();


        // How are we displaying the results (grouped by protein group or individually)
        boolean group = filterCriteria_session.isGroupProteins();
        request.setAttribute("groupProteins", group);


        SORT_BY sortBy_request = null;
        SORT_ORDER sortOrder_request = null;
        
        if((String) request.getParameter("sortBy") != null) {
            sortBy_request = SORT_BY.getSortByForString((String) request.getParameter("sortBy"));
        }
        if((String) request.getParameter("sortOrder") != null) {
        	sortOrder_request = SORT_ORDER.getSortByForString((String) request.getParameter("sortOrder"));
        }


        long s = System.currentTimeMillis();
        log.info("Got request to sort results for protein inference: "+pinferId+"; sort by: "+sortBy_request+"; sort order: "+sortOrder_request);

        if(sortBy_request == null)
        	sortBy_request = filterCriteria_session.getSortBy();
        if(sortOrder_request == null)
        	sortOrder_request = filterCriteria_session.getSortOrder();
        
        boolean doResort = false;
        
        // figure out if we will be resorting the results
        // We will resort the results if:
        // 1. the column select for sorting (SORT_BY) has changed
        // 2. the sorting order (SORT_ORDER) has changed 
        //    AND we are grouping proteins
        //    AND we are sorting on a protein specific column (e.g. coverage, NSAF, mol. Wt. etc)
        // If proteins are not being grouped AND SORT_BY has not changed we don't need to 
        // resort as we can just use the existing list of proteins displayed in reverse order
        if(sortBy_request != filterCriteria_session.getSortBy()) {
        	log.info("SORT_BY has changed; SORT_BY in session: "+filterCriteria_session.getSortBy()+
        			", SORT_BY in request: "+sortBy_request);
        	doResort = true;
        	log.info("RE-SORTING....");
        	filterCriteria_session.setSortBy(sortBy_request);
        }
        if(sortOrder_request != filterCriteria_session.getSortOrder()) {
        	log.info("SORT_ORDER has changed; SORT_ORDER in session: "+filterCriteria_session.getSortOrder()+
        			", SORT_ORDER in request: "+sortOrder_request);
        	filterCriteria_session.setSortOrder(sortOrder_request);
        	
        	if(group && SORT_BY.isProteinSpecific(sortBy_request)) {
        		doResort = true;
            	log.info("RE-SORTING grouped proteins....");
        	}
        }
        
        if(doResort){
        	List<Integer> newSortedIds = null;

        	// resort  the results based on the given criteria
        	newSortedIds = IdPickerResultsLoader.getSortedProteinIds(pinferId, 
        			peptideDef, 
        			storedProteinIds, 
        			filterCriteria_session.getSortBy(),
        			filterCriteria_session.getSortOrder(),
        			group);

        	sessionManager.putForIdPicker(request, pinferId, filterCriteria_session, newSortedIds);
        	storedProteinIds = newSortedIds;
        }

        // page number is now 1
        int pageNum = 1;


        // determine the list of proteins we will be displaying
        
        // We can use the pager to page the results in the reverse order (SORT_ORDER == DESC)
        // However, if we are grouping indistinguishable proteins 
        // AND the sorting column is protein specific
        // we must have already sorted the results in descending order
        boolean doReversePage = filterCriteria_session.getSortOrder() == SORT_ORDER.DESC;
        if(group && SORT_BY.isProteinSpecific(filterCriteria_session.getSortBy()))
        	doReversePage = false;
        
        if(doReversePage)
        	log.info("REVERSE PAGING...");
        
        // get the index range that is to be displayed in this page
        ResultsPager pager = ResultsPager.instance();
        int[] pageIndices = pager.getPageIndices(storedProteinIds, pageNum, 
                doReversePage);
        // sublist to be displayed
        List<Integer> proteinIds = IdPickerResultsLoader.getPageSublist(storedProteinIds, pageIndices, group, doReversePage);
       

        // get the protein groups
        List<WIdPickerProteinGroup> proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIds, peptideDef);
        request.setAttribute("proteinGroups", proteinGroups);

        if(ProteinInferToSpeciesMapper.isSpeciesYeast(pinferId)) {
        	request.setAttribute("yeastAbundances", true);
        }
        
        if(ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(pinferId)) {
        	request.setAttribute("philiusResults", true);
        }

        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(storedProteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(storedProteinIds.size(), pageNum);

        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", (pageNum == pages.get(pages.size() - 1)));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);

        request.setAttribute("sortBy", filterCriteria_session.getSortBy());
        request.setAttribute("sortOrder", filterCriteria_session.getSortOrder());

        DisplayColumns displayColumnPrefs = (DisplayColumns) request.getSession().getAttribute("protinferDisplayColumns");
        request.setAttribute("displayColumns", displayColumnPrefs);
        
        long e = System.currentTimeMillis();
        log.info("Total time (SortProteinInferenceResultAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));

        return mapping.findForward("Success");

    }

}
