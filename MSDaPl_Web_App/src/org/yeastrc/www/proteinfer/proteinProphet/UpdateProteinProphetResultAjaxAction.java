/**
 * UpdateProteinInferenceResultAjaxAction.java
 * @author Vagisha Sharma
 * Jan 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.proteinProphet;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;

/**
 * 
 */
public class UpdateProteinProphetResultAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(UpdateProteinProphetResultAjaxAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        log.info("Got request to update protein inference view");
        
        // form for filtering and display options
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;
        request.setAttribute("proteinProphetFilterForm", filterForm);
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        
        // Get the peptide definition; We don't get peptide definition from ProteinProphet params so just
        // use a dummy one.
        PeptideDefinition peptideDef = new PeptideDefinition();
        peptideDef.setUseCharge(true);
        peptideDef.setUseMods(true);
        
        // filtering criteria from the request
        ProteinProphetFilterCriteria filterCriteria_request = filterForm.getFilterCriteria(peptideDef);
        
        
        // protein Ids
        List<Integer> proteinIds = null;
        
        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        
        long s = System.currentTimeMillis();
        
        // Check if we already have information in the session
        ProteinProphetFilterCriteria filterCriteria_session = sessionManager.getFilterCriteriaForProteinProphet(request, pinferId);
        proteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        
        // If we don't have a filtering criteria in the session return an error
        if(filterCriteria_session == null || proteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
        	// redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale ProteinProphet ID: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("STALE_ID");
            return null;
        }
        else {
        	
        	log.info("Found information in session for: "+pinferId);
        	System.out.println("stored protein ids: "+proteinIds.size());
        	 
        	// we will use the sorting column and sorting order from the filter criteria in the session.
        	filterCriteria_request.setSortBy(filterCriteria_session.getSortBy());
        	filterCriteria_request.setSortOrder(filterCriteria_session.getSortOrder());
        	
        	boolean match = matchFilterCriteria(filterCriteria_session, filterCriteria_request);
        	
        	// check if the protein grouping has changed. If so we may have to resort the proteins. 
            boolean resort = false;
            if(filterCriteria_session.isGroupProteins() != filterCriteria_request.isGroupProteins()) {
            	
            	resort = true; // if the grouping has changed we will resort proteins 
            }
            
            // if the filtering criteria has changed we need to filter the results again
            if(!match)  {
                
            	log.info("Filtering criteria has changed");
            	
                resort = false; // no need to re-sort.  The method below will take that into account.
                // Get a list of filtered and sorted proteins
                proteinIds = ProteinProphetResultsLoader.getProteinIds(pinferId, filterCriteria_request);
            }
            
            if(resort) {
                // resort the filtered protein IDs
                proteinIds = ProteinProphetResultsLoader.getSortedProteinIds(pinferId, 
                        peptideDef, 
                        proteinIds, 
                        filterCriteria_request.getSortBy(), 
                        filterCriteria_request.getSortOrder(),
                        filterCriteria_request.isGroupProteins());
            }
        }
        
        // put the list of filtered and sorted protein IDs in the session, along with the filter criteria
    	sessionManager.putForIdPicker(request, pinferId, filterCriteria_request, proteinIds);
        request.setAttribute("sortBy", filterCriteria_request.getSortBy());
        request.setAttribute("sortOrder", filterCriteria_request.getSortOrder());
        
        
        // page number is now 1
        int pageNum = 1;
        
        boolean group = filterCriteria_request.isGroupProteins();
        
        List<Integer> pageProteinIds = null;
        if(proteinIds.size() > 0) {
        	// determine the list of proteins we will be displaying

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
        	int[] pageIndices = pager.getPageIndices(proteinIds, pageNum, 
        			doReversePage);

        	// sublist to be displayed
        	pageProteinIds = ProteinProphetResultsLoader.getPageSublist(proteinIds, pageIndices, group, doReversePage);
        }
        else {
        	pageProteinIds = new ArrayList<Integer>(0);
        }
        
        
        // get the protein groups 
        List<WProteinProphetProteinGroup> proteinGroups = ProteinProphetResultsLoader.getProteinProphetGroups(pinferId, pageProteinIds, 
                                                        peptideDef);
        request.setAttribute("proteinGroups", proteinGroups);
        
        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(proteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(proteinIds.size(), pageNum);
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", ( pages.size() == 0 || (pageNum == pages.get(pages.size() - 1))));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        
        // Results summary
        WProteinProphetResultSummary summary = ProteinProphetResultsLoader.getProteinProphetResultSummary(pinferId, proteinIds);
        request.setAttribute("resultSummary", summary);
        
        
        long e = System.currentTimeMillis();
        log.info("Total time (UpdateProteinProphetResultAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
    }

    private boolean matchFilterCriteria(ProteinProphetFilterCriteria filterCritSession,  ProteinProphetFilterCriteria filterCriteria) {
        return filterCritSession.equals(filterCriteria);
    }
}
