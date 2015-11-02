/**
 * ReadClusteredSpectrumCountsAction.java
 * @author Vagisha Sharma
 * Apr 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.www.compare.ComparisonCommand;
import org.yeastrc.www.compare.DisplayColumns;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.compare.clustering.ClusteringConstants.GRADIENT;
import org.yeastrc.www.compare.util.VennDiagramCreator;
import org.yeastrc.www.proteinfer.GOSupportUtils;
import org.yeastrc.www.taglib.HistoryTag;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ReadClusteredSpectrumCountsAction extends Action {

	private static final Logger log = Logger.getLogger(ReadClusteredSpectrumCountsAction.class.getName());

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

		long jobToken = 0;
		String strId = (String)request.getParameter("token");
		try {
			if(strId != null) {
				jobToken = Long.parseLong(strId);
			}
		}
		catch (NumberFormatException e) {}
		if(jobToken <= 0) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Invalid token in request: "+strId));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		// Check if we have a results directory matching this token
		String clustDir = request.getSession().getServletContext().getRealPath(ClusteringConstants.BASE_DIR);
		clustDir += File.separator+jobToken;

		if(!(new File(clustDir).exists())) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"No results found for token: "+strId));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		// Get the requested page number
		int page = 0;
		strId = (String)request.getParameter("page");
		try {
			if(strId != null) {
				page = Integer.parseInt(strId);
			}
		}
		catch (NumberFormatException e) {}
		
		if(page <= 0) {
			page = 1;
		}
		
		// Get the number of results to display per page
		int numPerPage = 0;
		strId = (String)request.getParameter("count");
		try {
			if(strId != null) {
				numPerPage = Integer.parseInt(strId);
			}
		}
		catch (NumberFormatException e) {}
		
		if(numPerPage <= 1) {
			numPerPage = 50;
		}
		
		ObjectInputStream ois = null;
		
		// Read the form
		String formFile = clustDir+File.separator+ClusteringConstants.FORM_SER;
		ProteinSetComparisonForm myForm = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(formFile));
			myForm = (ProteinSetComparisonForm) ois.readObject();
			myForm.setNumPerPage(numPerPage);
		}
		catch (IOException e) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
			"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		catch(ClassCastException e) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
			"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		finally {
			if(ois != null) try {ois.close();} catch(IOException e){}
		}
		
		// Are we given an order in which to display the datasets
		String dsOrder = request.getParameter("dsOrder");
		if(dsOrder != null) {
			
			// Set this in the request so that the link to the HTML can use this order
			request.setAttribute("dsOrder", dsOrder);
			
			String[] tokens = dsOrder.split("_");
			List<Integer> piRunIds = new ArrayList<Integer>(tokens.length);
			for(String tok: tokens) {
				try {piRunIds.add(Integer.parseInt(tok));}
				catch(NumberFormatException e) {
					ActionErrors errors = new ActionErrors();
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Error parsing dataset order: "+dsOrder));
					saveErrors( request, errors );
					return mapping.findForward("Failure");
				}
			}
			// set the order in the form
			if(!myForm.setDatasetOrder(piRunIds)) {
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
				"Error setting dataset order: "+dsOrder));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
		}
		
		// We are going to display all columns unless we have the noDispCol parameter in the request
		myForm.resetDisplayColumns();
		
		// If we have display column preferences in the request get them now
		String noDispCol = request.getParameter("noDispCol");
		if(noDispCol != null) {
			DisplayColumns displayColumns = parseNoDispCol(noDispCol);
			if(displayColumns != null)
				myForm.setDisplayColumns(displayColumns);
		}
		// If we did not find a parameter in the request look for a cookie
		else { 
			 Cookie[] cookies = request.getCookies();
			 DisplayColumns displayColumns = getDisplayColumns(cookies);
			 myForm.setDisplayColumns(displayColumns);
		}
		request.setAttribute("proteinSetComparisonForm", myForm);
		
		// Are we given a gradient preference in the request
		String gradient = request.getParameter("gradient");
		String[] gradientColors = null;
		if(gradient != null) {
			
			// Set this in the request so that the link to the HTML can use this order
			request.setAttribute("gradient", gradient);
			
			List<String> colors = null;
			
			GRADIENT grad = GRADIENT.valueOf(GRADIENT.class, gradient);
			
			if(grad == GRADIENT.BY) {
				String colorFile = clustDir+File.separator+ClusteringConstants.COLORS_BY;
				try {colors = SpectrumCountClusterer.readColors(new File(colorFile));}
				catch(IOException e) {log.error("Error reading colors file: "+colorFile, e);}
				myForm.setHeatMapGradient(GRADIENT.BY);
			}
			if(grad == GRADIENT.GR) {
				String colorFile = clustDir+File.separator+ClusteringConstants.COLORS_RG;
				try {colors = SpectrumCountClusterer.readColors(new File(colorFile));}
				catch(IOException e) {log.error("Error reading colors file: "+colorFile, e);}
				myForm.setHeatMapGradient(GRADIENT.GR);
			}
			
			if(colors != null && colors.size() > 0) {
				gradientColors = new String[colors.size()];
				gradientColors = colors.toArray(gradientColors);
			}
		}
		
		// R image output
		String imgUrl = request.getSession().getServletContext().getContextPath()+"/"+ClusteringConstants.BASE_DIR+"/"+jobToken+"/"+ClusteringConstants.IMG_FILE;
        request.setAttribute("clusteredImgUrl", imgUrl);
        
        
		// create a list of the dataset ids being compared
		// Get the selected protein inference run ids
		List<Integer> allRunIds = myForm.getAllSelectedRunIdsOrdered();
		request.setAttribute("datasetIds", StringUtils.makeCommaSeparated(allRunIds));

		
		request.setAttribute("comparisonCommands", ComparisonCommand.getCommands());
        
        
        
		// Read the results
		if(myForm.getGroupIndistinguishableProteins()) {
			String grpComparisonFile = clustDir+File.separator+ClusteringConstants.PROT_GRP_SER;
			ProteinGroupComparisonDataset grpComparison = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(grpComparisonFile));
				grpComparison = (ProteinGroupComparisonDataset) ois.readObject();
			}
			catch (IOException e) {
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
						"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			catch(ClassCastException e) {
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
						"Error reading result for ProteinGroupComparisonDataset. "+e.getMessage()));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			finally {
				if(ois != null) try {ois.close();} catch(IOException e){}
			}
			
			grpComparison.setDisplayColumns(myForm.getDisplayColumns());
			// If we are clustering columns AND we are not given a dataset order in the request parameters we will display the clustered order
			// Otherwise we will display the order given to us in the request parameters.
			if(!myForm.isClusterColumns() || dsOrder != null)
				grpComparison.setDatasetOrder(myForm.getAllSelectedRunIdsOrdered());
			else {
				// Change the order in the form if we are using the clustered order
				myForm.setDatasetOrder(grpComparison.getDatasetOrder());
			}
			grpComparison.initSummary(); // this needs to be done in case the dataset order has changed
			request.setAttribute("comparison", grpComparison);
			
			// If the user is downloading redirect to the download page
			if(request.getParameter("download") != null) {
				if(request.getParameter("dgrp") != null) // collpase protein groups
					myForm.setCollapseProteinGroups(true);
				if(request.getParameter("descr") != null) // get descriptions
					myForm.setIncludeDescriptions(true);
				request.setAttribute("comparisonForm", myForm);
				request.setAttribute("comparisonGroupDataset", grpComparison);
				return mapping.findForward("Download");
			}
			
			if(gradientColors != null) {
				grpComparison.setSpectrumCountColors(gradientColors);
			}
			grpComparison.setRowCount(numPerPage);
			grpComparison.setCurrentPage(page);
			
			// Create Venn Diagram only if 2 or 3 datasets are being compared
	        if(grpComparison.getDatasetCount() == 2 || grpComparison.getDatasetCount() == 3) {
	            String googleChartUrl = VennDiagramCreator.instance().getChartUrl(grpComparison);
	            request.setAttribute("chart", googleChartUrl);
	        }
	        
	        request.setAttribute(HistoryTag.NO_HISTORY_ATTRIB, true); // Don't want this to be saved to history.
	        return mapping.findForward("ProteinGroupList");
		}
		
		else {
			
			String comparisonFile = clustDir+File.separator+ClusteringConstants.PROT_SER;
			ProteinComparisonDataset comparison = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(comparisonFile));
				comparison = (ProteinComparisonDataset) ois.readObject();
			}
			catch (IOException e) {
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
						"Error reading result for ProteinComparisonDataset. "+e.getMessage()));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			catch(ClassCastException e) {
				ActionErrors errors = new ActionErrors();
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
						"Error reading result for ProteinComparisonDataset. "+e.getMessage()));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}
			finally {
				if(ois != null) try {ois.close();} catch(IOException e){}
			}
			
			comparison.setDisplayColumns(myForm.getDisplayColumns());
			// If we are clustering columns AND we are not given a dataset order in the request parameters we will display the clustered order
			// Otherwise we will display the order given to us in the request parameters.
			if(!myForm.isClusterColumns() || dsOrder != null)
				comparison.setDatasetOrder(myForm.getAllSelectedRunIdsOrdered());
			else {
				// Change the order in the form if we are using the clustered order
				myForm.setDatasetOrder(comparison.getDatasetOrder()); 
			}
			comparison.initSummary(); // this needs to be done in case the dataset order has changed
			request.setAttribute("comparison", comparison);
			
			// If the user is downloading redirect to the download page
			if(request.getParameter("download") != null) {
				request.setAttribute("comparisonForm", myForm);
				if(request.getParameter("dgrp") != null) // collpase protein groups
					myForm.setCollapseProteinGroups(true);
				if(request.getParameter("descr") != null) // get descriptions
					myForm.setIncludeDescriptions(true);
				request.setAttribute("comparisonDataset", comparison);
				return mapping.findForward("Download");
			}
			
			if(gradientColors != null) {
				comparison.setSpectrumCountColors(gradientColors);
			}
			comparison.setRowCount(numPerPage);
			comparison.setCurrentPage(page);
			
	        
			// Create Venn Diagram only if 2 or 3 datasets are being compared
	        if(comparison.getDatasetCount() == 2 || comparison.getDatasetCount() == 3) {
	            String googleChartUrl = VennDiagramCreator.instance().getChartUrl(comparison);
	            request.setAttribute("chart", googleChartUrl);
	        }
	        
	        request.setAttribute(HistoryTag.NO_HISTORY_ATTRIB, true); // Don't want this to be saved to history.
	        return mapping.findForward("ProteinList");
		}
		
	}

	private DisplayColumns parseNoDispCol(String noDispCol) {
		
		DisplayColumns displayColumns = new DisplayColumns();
		String[] tokens = noDispCol.split(",");
		for(String tok: tokens) {
			displayColumns.setNoDisplay(tok.charAt(0));
		}
		return displayColumns;
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
	
	private List<Integer> getMySpeciesIds(List<Integer> piRunIds) throws Exception {

		Set<Integer> species = new HashSet<Integer>();

		ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
		MsSearchDAO searchDao = DAOFactory.instance().getMsSearchDAO();

		for(Integer piRunId: piRunIds) {
			List<Integer> searchIds = runDao.loadSearchIdsForProteinferRun(piRunId);
			if(searchIds != null) {
				for(int searchId: searchIds) {

					MsSearch search = searchDao.loadSearch(searchId);
					MSJob job = MSJobFactory.getInstance().getMsJobForExperiment(search.getExperimentId());
					species.add(job.getTargetSpecies());
				}
			}
		}
		return new ArrayList<Integer>(species);
	}
	
	private List<Species> getSpeciesList(List<Integer> mySpeciesIds) throws SQLException {
		List<Species> speciesList = GOSupportUtils.getSpeciesList();

		if(mySpeciesIds.size() == 1) {
			int sid = mySpeciesIds.get(0);
			boolean found = false;
			for(Species sp: speciesList) {
				if(sp.getId() == sid) {
					found = true; break;
				}
			}
			if(!found) {
				Species species = new Species();
				species.setId(sid);
				speciesList.add(species);
			}
		}
		return speciesList;
	}

}
