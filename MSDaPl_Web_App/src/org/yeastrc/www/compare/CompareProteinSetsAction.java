/**
 * CompareProtInferResultsAction.java
 * @author Vagisha Sharma
 * Apr 9, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.slim.GOSlimLookup;
import org.yeastrc.www.compare.dataset.DatasetBuilder;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.compare.dataset.SelectableDataset;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class CompareProteinSetsAction extends Action {

    private static final Logger log = Logger.getLogger(CompareProteinSetsAction.class);

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
        
        
        // get the protein inference ids to compare
        // first, look for ids in the request parameters
        List<Integer> allRunIds = null;
        boolean groupProteins = true;
        String idStr = request.getParameter("piRunIds");
        if(idStr != null && idStr.trim().length() > 0) {
            allRunIds = new ArrayList<Integer>();
            String[] tokens = idStr.split(",");
            for(String tok: tokens) {
                int piRunId = Integer.parseInt(tok.trim());
                allRunIds.add(piRunId);
            }
            groupProteins = Boolean.parseBoolean(request.getParameter("groupProteins"));
        }
        
        if(allRunIds == null || allRunIds.size() < 2) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more datasets to compare."));
            saveErrors(request, errors);
            return mapping.findForward("Failure");
        }
        
        
        List<SelectableDataset> datasets = new ArrayList<SelectableDataset>(allRunIds.size());
        
        int datasetIndex = 0;
        for(int piRunId: allRunIds) {
            
            SelectableDataset dataset = DatasetBuilder.instance().buildSelectableDataset(piRunId);
            if(dataset == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                        "No protein inference run found with ID: "+piRunId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            dataset.setSelected(false);
            dataset.setDatasetIndex(datasetIndex++);
            datasets.add(dataset);
        }
        
        // Form we will use
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm)form;
        myForm.setGroupIndistinguishableProteins(groupProteins);
        myForm.setKeepProteinGroups(true);
        myForm.setScaleRows(true);
       
        
        // ANY AND, OR, NOT, XOR filters
        myForm.setAndList(datasets);
        myForm.setOrList(datasets);
        myForm.setNotList(datasets);
        myForm.setXorList(datasets);
        
        // Do we have ProteinProphet datasets
        for(SelectableDataset dataset: datasets) {
            if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
                myForm.setHasProteinProphetDatasets(true);
                myForm.setUseProteinGroupProbability(true);
                break;
            }
        }
        
        // GO Slim terms
        List<GONode> goslims = GOSlimLookup.getGOSlims();
        request.getSession().setAttribute("goslims", goslims); // set this in the session
        if(goslims.size() > 0) {
        	for(GONode slim: goslims) {
        		if(slim.getName().contains("Generic")) {
        			myForm.setGoSlimTermId(slim.getId());
        			break;
        		}
        	}
        }
        
        myForm.setComparisonActionId(ComparisonCommand.FILTER.getId());
        
//        // Do we have ProteinProphet datasets
//        for(SelectableDataset dataset: datasets) {
//            if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
//                myForm.setHasProteinProphetDatasets(true);
//                myForm.setUseProteinGroupProbability(true);
//                break;
//            }
//        }
        
        // Check if there is a cookie in the request for column display
        Cookie[] cookies = request.getCookies();
        DisplayColumns displayColumns = getDisplayColumns(cookies);
        myForm.setDisplayColumns(displayColumns);
        
        return mapping.findForward("DoComparison");
    }

	private DisplayColumns getDisplayColumns(Cookie[] cookies) {
		
		DisplayColumns displayColumns = new DisplayColumns();
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals("noDispCols_compare")) {
				String val = cookie.getValue();
				if(val != null) {
					String[] tokens = val.split("_");
					for(String tok: tokens) {
						displayColumns.setNoDisplay(tok.charAt(0));
					}
				}
			}
		}
		return displayColumns;
	}
    
}
