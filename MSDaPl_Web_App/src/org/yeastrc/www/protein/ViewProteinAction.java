/*
 * ViewProteinAction.java
 * Created on Oct 4, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.protein;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.nr_seq.NRProtein;
import org.yeastrc.nr_seq.NRProteinFactory;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectsSearcher;
import org.yeastrc.www.protein.ProteinAbundanceDao.YeastOrfAbundance;
import org.yeastrc.www.proteinfer.job.ProteinInferJobSearcher;
import org.yeastrc.www.proteinfer.job.ProteinferJob;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.yates.YatesRun;
import org.yeastrc.yates.YatesRunSearcher;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 4, 2004
 */

public class ViewProteinAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		// The protein we're viewing
		int proteinID;
		
		// User making this request
		User user = UserUtils.getUser(request);
		if (user == null) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.login.notloggedin"));
			saveErrors( request, errors );
			return mapping.findForward("authenticate");
		}


		NRProtein protein = null;
		try {
			String strID = request.getParameter("id");

			if (strID == null || strID.equals("")) {
				ActionErrors errors = new ActionErrors();
				errors.add("protein", new ActionMessage("error.protein.invalidid"));
				saveErrors( request, errors );
				return mapping.findForward("Failure");
			}

			proteinID = Integer.parseInt(strID);

			// Load our protein
			NRProteinFactory nrpf = NRProteinFactory.getInstance();
			protein = (NRProtein)(nrpf.getProtein(proteinID));
			
			// Abundance information. Only for yeast
	        // Ghaemmaghami, et al., Nature 425, 737-741 (2003)
	        if(protein.getSpecies().getId() == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE) {
	        	List<YeastOrfAbundance> abundances = ProteinAbundanceDao.getInstance().getAbundance(protein.getId());
	        	if(abundances == null || abundances.size() == 0)
	        		request.setAttribute("proteinAbundance", "NOT AVAILABLE");
	        	else {
	        		if(abundances.size() == 1) {
	        			request.setAttribute("proteinAbundance", abundances.get(0).getAbundanceString());
	        		}
	        		else {
	        			String aString = "";
	        			for(YeastOrfAbundance a: abundances) {
	        				aString +=  ", "+a.getAbundanceAndOrfNameString();
	        			}
	        			aString = aString.substring(1);
	        			request.setAttribute("proteinAbundance", aString);
	        		}
	        	}
	        }
		
		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("protein", new ActionMessage("error.project.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		} catch (Exception e) {
			ActionErrors errors = new ActionErrors();
			errors.add("username", new ActionMessage("error.project.projectnotfound"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");	
		}

		Map goterms = protein.getGOAll();
		
		if ( ((Collection)goterms.get("P")).size() > 0)
			request.setAttribute("processes", goterms.get("P"));

		if ( ((Collection)goterms.get("C")).size() > 0)
			request.setAttribute("components", goterms.get("C"));
		
		if ( ((Collection)goterms.get("F")).size() > 0)
			request.setAttribute("functions", goterms.get("F"));
		
		// clean up
		goterms = null;
		
		YatesRunSearcher yrs = new YatesRunSearcher();
		yrs.setProtein(protein);
		Collection runs = yrs.search();
		
		// Make sure only runs belonging to projects this user has access to are listed.
		if (runs != null && runs.size() > 0) {
			Iterator iter = runs.iterator();
			while (iter.hasNext()) {
				YatesRun yr = (YatesRun)(iter.next());
				if (!yr.getProject().checkReadAccess(user.getResearcher()))
					iter.remove();
			}
			
			request.setAttribute("yatesdata", runs);
		}
		
		
		// Get the protein inference runs where this protein was listed
		// This is a bit convoluted
		// First get all the users' projects (projects the user has read access to)
		ProjectsSearcher projSearcher = new ProjectsSearcher();
        projSearcher.setResearcher(user.getResearcher());
        List<Project> projects = projSearcher.search();
//		List<Project> projects = user.getProjects();
        
		List<Integer> pinferIds = new ArrayList<Integer>();
		for(Project project: projects) {
		    List<Integer> experimentIds = ProjectExperimentDAO.instance().getExperimentIdsForProject(project.getID());
		    
	        for(int experimentId: experimentIds) {
	            List<ProteinferJob> piJobs = ProteinInferJobSearcher.getInstance().getProteinferJobsForMsExperiment(experimentId);
	            for(ProteinferJob job: piJobs) {
	                pinferIds.add(job.getPinferId());
	            }
	        }
		}
		// We now have the protein inference runs for this user.
		// Find out which one of then has the protein of interest.
		List<ProteinferRun> piRuns = new ArrayList<ProteinferRun>(pinferIds.size());
		ProteinferRunDAO piRunDao = ProteinferDAOFactory.instance().getProteinferRunDao();
		ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
		ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
		nrseqIds.add(proteinID);
		for(int pinferId: pinferIds) {
		    if(protDao.getProteinIdsForNrseqIds(pinferId, nrseqIds).size() > 0) {
		        ProteinferRun piRun = piRunDao.loadProteinferRun(pinferId);
		        piRuns.add(piRun);
		    }
		}
		request.setAttribute("piRuns", piRuns);
		
		
		// Set this project in the request, as a bean to be displayed on the view
		request.setAttribute("protein", protein);
		return mapping.findForward("Success");
	}

}