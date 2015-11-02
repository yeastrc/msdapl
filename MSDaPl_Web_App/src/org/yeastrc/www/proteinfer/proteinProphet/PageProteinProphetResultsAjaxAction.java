/**

 * PageProteinferResultsAction.java
 * @author Vagisha Sharma
 * Jan 8, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

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
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class PageProteinProphetResultsAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(PageProteinProphetResultsAjaxAction.class);
    
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
            response.getWriter().write("ERROR: Invalid ProteinProphet ID: "+pinferId);
            return null;
        }
        
        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        // Check if we already have information in the session
        ProteinFilterCriteria filterCriteria_session = sessionManager.getFilterCriteriaForProteinProphet(request, pinferId);
        List<Integer> storedProteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        // If we don't have a filtering criteria in the session return an error
        if(filterCriteria_session == null || storedProteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
        	// redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale ProteinProphet ID: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("STALE_ID");
            return null;
        }
        
        
        // Peptide definition from the session
        PeptideDefinition peptideDef = filterCriteria_session.getPeptideDefinition();

        // How are we displaying the results (grouped by protein group or individually)
        boolean group = filterCriteria_session.isGroupProteins();
        request.setAttribute("groupProteins", group);
        
        
        // get the page number from the request
        int pageNum = 1;
        try {pageNum = Integer.parseInt(request.getParameter("pageNum"));}
        catch(NumberFormatException e){ pageNum = 1;}
        request.setAttribute("pageNum", pageNum);
        
        long s = System.currentTimeMillis();
        
        log.info("Paging results for ProteinProphetID: "+pinferId+"; page num: "+pageNum+"; sort order: "
        		+filterCriteria_session.getSortOrder() );
        
        
        // We can use the pager to page the results in the reverse order (SORT_ORDER == DESC)
        // However, if we are grouping ProteinProphet groups 
        // AND the sorting column is NOT ProteinProphetGroup specific
        // we must have already sorted the results in descending order
        boolean doReversePage = filterCriteria_session.getSortOrder() == SORT_ORDER.DESC;
        if(group && !SORT_BY.isProteinProphetGroupSpecific(filterCriteria_session.getSortBy()))
        	doReversePage = false;
        
        if(doReversePage)
    		log.info("REVERSE PAGING...");
        
        // get the index range that is to be displayed in this page
        ResultsPager pager = ResultsPager.instance();
        int[] pageIndices = pager.getPageIndices(storedProteinIds, pageNum, 
                doReversePage);
        // sublist to be displayed
        List<Integer> proteinIds = ProteinProphetResultsLoader.getPageSublist(storedProteinIds, pageIndices, group, doReversePage);
        
        
        
        // get the protein groups
        List<WProteinProphetProteinGroup> proteinGroups = ProteinProphetResultsLoader.getProteinProphetGroups(pinferId, proteinIds, 
                peptideDef);
        request.setAttribute("proteinGroups", proteinGroups);
        
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
        
        long e = System.currentTimeMillis();
        log.info("Total time (PageProteinProphetResultAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        return mapping.findForward("Success");
        
    }
}
