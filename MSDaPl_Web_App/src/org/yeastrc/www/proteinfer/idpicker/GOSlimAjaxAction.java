/**
 * 
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOAnalysisProtein;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProtein;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.proteinfer.ProteinInferFilterForm;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;

import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;

/**
 * GOSlimAjaxAction.java
 * @author Vagisha Sharma
 * May 21, 2010
 * 
 */
public class GOSlimAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(GOSlimAjaxAction.class.getName());

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		log.info("Got request for GO Slim analysis for protein inference");
		
		// form for filtering and display options
		ProteinInferFilterForm filterForm = (ProteinInferFilterForm)form;

		// get the protein inference id
		int pinferId = filterForm.getPinferId();

		long s = System.currentTimeMillis();

		// Get the peptide definition; 
		IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        // filtering criteria from the request
        ProteinFilterCriteria filterCriteria_request = filterForm.getFilterCriteria(peptideDef);
        
        // protein Ids
        List<Integer> proteinIds = null;
        
        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        
        // Check if we already have information in the session
        ProteinFilterCriteria filterCriteria_session = sessionManager.getFilterCriteriaForIdPicker(request, pinferId);
        proteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        
        // If we don't have a filtering criteria in the session return an error
        if(filterCriteria_session == null || proteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
        	// redirect to the /viewProteinInferenceResult action if this different from the
            // protein inference ID stored in the session
            log.error("Stale protein inference ID: "+pinferId);
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
        	
            
            // if the filtering criteria has changed we need to filter the results again
            if(!match)  {
                
            	log.info("Filtering criteria has changed");
            	
            	proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria_request);
            }
        }
        
        long e = System.currentTimeMillis();
        log.info("Got filtered nrseq protein ids for GO Slim analysis in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
		
        request.setAttribute("pinferId", pinferId);
        request.setAttribute("goAspect", filterForm.getGoAspect());
        request.setAttribute("goSlimTermId", filterForm.getGoSlimTermId());
        
        
        if(filterForm.isGetGoSlimTree()) {
        	
	        // We have the protein inference protein IDs; Get the corresponding nrseq protein IDs
	        List<Integer> nrseqIds = new ArrayList<Integer>(proteinIds.size());
	        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
	        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
	        for(int proteinId: proteinIds) {
	            ProteinferProtein protein = protDao.loadProtein(proteinId);
	            nrseqIds.add(protein.getNrseqProteinId());
	        }
	        request.setAttribute("nrseqProteinIds", nrseqIds);
        
        	return mapping.findForward("GoTree");
        }
        
        else {
        	
        	// We have the protein inference protein IDs; Get the corresponding nrseq protein IDs and protein group IDs
	        List<GOAnalysisProtein> goAnalysisProteins = new ArrayList<GOAnalysisProtein>(proteinIds.size());
	        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
	        IdPickerProteinDAO protDao = factory.getIdPickerProteinDao();
	        for(int proteinId: proteinIds) {
	            IdPickerProtein protein = protDao.loadProtein(proteinId);
	            goAnalysisProteins.add(new GOAnalysisProtein(protein.getNrseqProteinId(), protein.getProteinGroupLabel()));
	        }
	        
        	request.setAttribute("goSlimProteins", goAnalysisProteins);
        	request.setAttribute("doGroupAnalysis", true); // default is to get GO information at the group level
        	return mapping.findForward("Success");
        }
		
	}
	
	private boolean matchFilterCriteria(ProteinFilterCriteria filterCritSession,  ProteinFilterCriteria filterCriteria) {
        return filterCritSession.equals(filterCriteria);
    }

}
