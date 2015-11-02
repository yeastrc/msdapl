/**
 * ProteinDetailsAction.java
 * @author Vagisha Sharma
 * May 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.protein;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.go.GOAnnotation;
import org.yeastrc.bio.go.GOSearcher;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.nr_seq.listing.ProteinListingBuilder;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrProtein;
import org.yeastrc.philius.dao.PhiliusDAOFactory;
import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.protein.ProteinAbundanceDao.YeastOrfAbundance;
import org.yeastrc.www.proteinfer.ProteinDetailsAjaxAction;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ProteinDetailsAction extends Action {

private static final Logger log = Logger.getLogger(ProteinDetailsAjaxAction.class);
    
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

		int proteinId = 0;
		String strId = request.getParameter("id");
		if (strId == null || strId.equals("")) {
			ActionErrors errors = new ActionErrors();
			errors.add("protein", new ActionMessage("error.protein.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		try {
			proteinId = Integer.parseInt(strId);
		
		} catch (NumberFormatException nfe) {
			ActionErrors errors = new ActionErrors();
			errors.add("protein", new ActionMessage("error.project.invalidid"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}

		List<Integer> pinferIds = null;
		strId = request.getParameter("pinferIds");
		if (strId == null || strId.equals("")) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No protein infrence IDs found in request"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		try {
			String[] tokens = strId.split(",");
			pinferIds = new ArrayList<Integer>(tokens.length);
			for(String tok: tokens) {
				pinferIds.add(Integer.parseInt(tok));
			}
		}
		catch(NumberFormatException e) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
					"Error parsing protein inference ID", e));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		if(pinferIds == null || pinferIds.size() == 0) {
			ActionErrors errors = new ActionErrors();
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No protein infrence IDs found in request"));
			saveErrors( request, errors );
			return mapping.findForward("Failure");
		}
		
		
        long s = System.currentTimeMillis();
        
        // Get the fasta databases 
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInferences(pinferIds, true);
        
        // Load our protein
        Protein protein = new Protein();
        NrProtein nrProtein = NrSeqLookupUtil.getNrProtein(proteinId);
        protein.setProtein(nrProtein);
        
        ProteinListing listing = ProteinListingBuilder.getInstance().build(nrProtein.getId(), fastaDatabaseIds);
        protein.setProteinListing(listing);
        
        // Get the sequence for this protein
        String sequence = NrSeqLookupUtil.getProteinSequence(nrProtein.getId());
        protein.setSequence(sequence);
        
        // get the peptides for this protein from the given protein inferences
        // Get the unique peptide sequences for this protein (for building the protein sequence HTML)
        Set<String> peptideSequences = new HashSet<String>();
        ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
        
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(proteinId);
        for(int pinferId: pinferIds) {
        	List<Integer> pinferProteinIds = protDao.getProteinIdsForNrseqIds(pinferId, nrseqIds);
        	for(Integer piProteinId: pinferProteinIds) {
        		ProteinferProtein piProtein = protDao.loadProtein(piProteinId);
        		if(piProtein != null) {
        			for(ProteinferPeptide peptide: piProtein.getPeptides()) {
        				peptideSequences.add(peptide.getSequence());
        			}
        		}
        	}
        }
        protein.setPeptides(peptideSequences);
        
        request.setAttribute("protein", protein);
        
        // Abundance information. Only for yeast
        // Ghaemmaghami, et al., Nature 425, 737-741 (2003)
        if(nrProtein.getSpeciesId() == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE) {
        	List<YeastOrfAbundance> abundances = ProteinAbundanceDao.getInstance().getAbundance(nrProtein.getId());
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
        
        // Philius information, if it is already available
        PhiliusResult result = PhiliusDAOFactory.getInstance().getPhiliusResultDAO().loadForSequence(protein.getProteinListing().getSequenceId());
        if(result != null) {
        	request.setAttribute("philiusAnnotation", result.getAnnotation());
        }
        
        
        // Gene Ontology information
        Map<String, Set<GOAnnotation>> goterms = GOSearcher.getInstance().getGOAnnotations(protein.getProteinListing());
        if ( (goterms.get("P")).size() > 0)
			request.setAttribute("processes", goterms.get("P"));

		if ( (goterms.get("C")).size() > 0)
			request.setAttribute("components", goterms.get("C"));
		
		if ( (goterms.get("F")).size() > 0)
			request.setAttribute("functions", goterms.get("F"));
		
        long e = System.currentTimeMillis();
        
        log.info("Total time (ProteinDetailsAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        return mapping.findForward("Success");
    }
}
