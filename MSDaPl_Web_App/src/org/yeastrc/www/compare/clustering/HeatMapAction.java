/**
 * HeatMapAction.java
 * @author Vagisha Sharma
 * Apr 23, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
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
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.compare.clustering.ClusteringConstants.GRADIENT;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class HeatMapAction extends Action {

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

		
		ObjectInputStream ois = null;
		
		// Read the form
		String formFile = clustDir+File.separator+ClusteringConstants.FORM_SER;
		ProteinSetComparisonForm myForm = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(formFile));
			myForm = (ProteinSetComparisonForm) ois.readObject();
			//myForm.setNumPerPage(numPerPage);
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
		
		// Are we given an order in which to display the datasets.
		String dsOrder = request.getParameter("dsOrder");
		List<Integer> piRunIds = null;
		if(dsOrder != null) {
			
			String[] tokens = dsOrder.split("_");
			piRunIds = new ArrayList<Integer>(tokens.length);
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
		}
		
		// Are we given a gradient preference in the request
		String gradient = request.getParameter("gradient");
		String[] gradientColors = null;
		if(gradient != null) {
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
		
		// Read the results
		if(myForm.getGroupIndistinguishableProteins()) {
			String grpComparisonFile = clustDir+File.separator+ClusteringConstants.PROT_GRP_SER;
			ProteinGroupComparisonDataset grpComparison = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(grpComparisonFile));
				grpComparison = (ProteinGroupComparisonDataset) ois.readObject();
				if(piRunIds != null)
					grpComparison.setDatasetOrder(piRunIds);
				if(gradientColors != null)
					grpComparison.setSpectrumCountColors(gradientColors);
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
			request.setAttribute("heatmap", new HeatMapData(grpComparison));
			request.setAttribute("hasGroups", true);
		}
		
		else {
			
			String comparisonFile = clustDir+File.separator+ClusteringConstants.PROT_SER;
			ProteinComparisonDataset comparison = null;
			try {
				ois = new ObjectInputStream(new FileInputStream(comparisonFile));
				comparison = (ProteinComparisonDataset) ois.readObject();
				if(piRunIds != null)
					comparison.setDatasetOrder(piRunIds);
				if(gradientColors != null)
					comparison.setSpectrumCountColors(gradientColors);
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
			
			request.setAttribute("heatmap", new HeatMapData(comparison));
			
		}
		
		return mapping.findForward("Success");
	}
}
