/**
 * ClusteringGatewayAction.java
 * @author Vagisha Sharma
 * Apr 21, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
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
import org.yeastrc.www.compare.DatasetBooleanFilters;
import org.yeastrc.www.compare.ProteinPropertiesFilters;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.compare.clustering.ClusteringConstants.GRADIENT;

/**
 * 
 */
public class ClusteringGatewayAction extends Action {

	private static final Logger log = Logger.getLogger(ClusteringGatewayAction.class.getName());

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
    	// If we are here it is from the UpdateProteinComparisonAction.  
    	// We have already made sure that the user has logged in.
       
    	// Form we will use
        if(form == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No comparison form in request."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // Look for a clustering token in the form
        String token = ((ProteinSetComparisonForm) form).getClusteringToken();
        
        // Look for it in the session
        if(token == null || token.trim().length() == 0) {
        	token = (String)request.getSession().getAttribute("clustering_token");
        }
        
        // No token found
        if(token == null || token.trim().length() == 0) {
        	log.info("No token found in request or session; Starting new clustering job");
        	return startNewClustering(mapping, (ProteinSetComparisonForm) form); // NEW CLUSTERING ACTION
        }
        
        // Token found
        else {
        	
        	// If this is a fresh token
        	if(((ProteinSetComparisonForm) form).isNewToken()) {
        		log.info("Token found. New token. Forwarding to DoComparison");
        		return mapping.findForward("DoComparison");
        	}
        	
        	// this is not a fresh token
        	else {
        		
        		// Check if we already have results for this token
            	String resultDir = request.getSession().getServletContext().getRealPath(ClusteringConstants.BASE_DIR);
            	resultDir = resultDir+File.separator+token;
            	
            	// If results directory exists
            	if(new File(resultDir).exists()) {
            		
            		// read serialized from from results directory
            		String formFile = resultDir+File.separator+ClusteringConstants.FORM_SER;
            		ProteinSetComparisonForm savedForm = null;
            		ObjectInputStream ois = null;
            		try {
            			ois = new ObjectInputStream(new FileInputStream(formFile));
            			savedForm = (ProteinSetComparisonForm) ois.readObject();
            		}
            		catch (Exception e) {  // Catch ANY kind of exception
            			log.error("Error reading serialized form in directory", e);
            			
            		}
            		
            		// If we were not able to read the form start a new clustering process
            		if(savedForm == null) {
            			log.info("Token found.  Old token. BUT could not read serialized form. Starting new clustering job");
            			return startNewClustering(mapping, (ProteinSetComparisonForm) form); // NEW CLUSTERING ACTION
            		}
            		
            		else {
            			// if form in request matches serialized form
            			if(formsMatch((ProteinSetComparisonForm) form, savedForm)) {
            				log.info("Toke found.  Old token. Forms match. Reading saved results");
            				
            				// Columns we don't want to display
            				String noDispCols = ((ProteinSetComparisonForm) form).getDisplayColumns().getNoDisplayColCommaSeparated();
            				if(noDispCols.length() > 0)
            					noDispCols = "&noDispCol="+noDispCols;
            				
            				// Dataset order
            				List<Integer> datasetOrder = ((ProteinSetComparisonForm) form).getAllSelectedRunIdsOrdered();
            				String order = "";
            				for(Integer id: datasetOrder)
            					order += "_"+id;
            				if(order.length() > 0)
            					order = order.substring(1);
            				order = "&dsOrder="+order;
            				
            				// gradient
            				String gradient = "";
            				if(((ProteinSetComparisonForm) form).getHeatMapGradient() == GRADIENT.BY)
            					gradient="&gradient="+GRADIENT.BY.name();
            				else
            					gradient="&gradient="+GRADIENT.GR.name();
            				
            				// Anchor
            				String anchor = "";
            		        if(((ProteinSetComparisonForm) form).getRowIndex() != -1) {
            		        	anchor = "#"+((ProteinSetComparisonForm) form).getRowIndex();
            		        }
            		        
            		        // Downloading options
            		        String downloadOpts = "";
            		        if(((ProteinSetComparisonForm) form).isDownload()) {
            		        	downloadOpts = "&download=T";
            		        	if(((ProteinSetComparisonForm) form).isCollapseProteinGroups())
            		        		downloadOpts += "&dgrp=T";
            		        	if(((ProteinSetComparisonForm) form).isIncludeDescriptions())
            		        		downloadOpts += "&descr=T";
            		        }
            		        ActionForward fwd = mapping.findForward("ReadOld");
            				ActionForward newFwd = new ActionForward(fwd.getPath()+
            						"?token="+token+"&page="+((ProteinSetComparisonForm)form).getPageNum()+
            						"&count="+((ProteinSetComparisonForm)form).getNumPerPage()+
            						noDispCols+
            						order+
            						gradient+
            						downloadOpts+
            						anchor, 
            						fwd.getRedirect());
            				return newFwd;
            			}

            			// form in request does not match serialized form
            			else {
            				log.info("Toke found.  Old token. Forms DON'T match. Starting new clustering job");
            				return startNewClustering(mapping, (ProteinSetComparisonForm) form); // NEW CLUSTERING ACTION
            			}
            		}
            	}
            	
            	// Results directory does not exist anymore. We have to do clustering all over again.
            	else {
            		log.info("Toke found.  Old token. BUT results directory not found. Starting new clustering job");
            		return startNewClustering(mapping, (ProteinSetComparisonForm) form); // NEW CLUSTERING ACTION
            	}
        	}
        }
    }

	private boolean formsMatch(ProteinSetComparisonForm myForm,
			ProteinSetComparisonForm savedForm) {
		
		List<Integer> myRunIds = myForm.getAllSelectedRunIdsOrdered();
		List<Integer> savedRunIds = savedForm.getAllSelectedRunIdsOrdered();
		if(myRunIds.size() != savedRunIds.size()) {
			log.info("selected runIds do not match");
			return false;
		}
		Collections.sort(myRunIds);
		Collections.sort(savedRunIds);
		for(int i = 0; i < myRunIds.size(); i++) {
			if(myRunIds.get(i).intValue() != savedRunIds.get(i).intValue()) {
				log.info("selected runIds do not match");
				return false;
			}
		}
		
		if(myForm.getParsimoniousParam() != savedForm.getParsimoniousParam()) 
			return false;
		
		if(myForm.getGroupIndistinguishableProteins() != savedForm.getGroupIndistinguishableProteins())
			return false;
		
		if(myForm.isKeepProteinGroups() != savedForm.isKeepProteinGroups())
			return false;
		
		// compare the filtering criteria in the two forms
		ProteinPropertiesFilters myFilters = myForm.getProteinPropertiesFilters();
		ProteinPropertiesFilters savedFilters = savedForm.getProteinPropertiesFilters();
		if(!(myFilters.equals(savedFilters))) {
			log.info("ProteinPropertiesFilters don't match");
			return false;
		}
		
		// Compare the boolean filters
		DatasetBooleanFilters myBoolFilters = myForm.getSelectedBooleanFilters();
		DatasetBooleanFilters theirBoolFilters = savedForm.getSelectedBooleanFilters();
		if(!(myBoolFilters.equals(theirBoolFilters))) {
			log.info("DatasetBooleanFilters don't match");
			return false;
		}
		
		// Compare clustering options
		if(myForm.isUseLogScale() != savedForm.isUseLogScale())
			return false;
		if(myForm.getLogBase() != savedForm.getLogBase())
			return false;
		if(myForm.getReplaceMissingWithValueDouble() != savedForm.getReplaceMissingWithValueDouble())
			return false;
		if(myForm.isClusterColumns() != savedForm.isClusterColumns())
			return false;
		return true;
	}

	private ActionForward startNewClustering(ActionMapping mapping,
			ProteinSetComparisonForm myForm) {
//		String token;
//		// Create a new token and put it in the form
//		token = createToken();
//		myForm.setClusteringToken(token);
//		myForm.setNewToken(true); // this is new token
		// Forward to the wait action
		return mapping.findForward("WaitForClustering");
	}

//	private String createToken() {
//		return String.valueOf(System.currentTimeMillis());
//	}
}
