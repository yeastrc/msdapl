package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOAnnotation;
import org.yeastrc.bio.go.GOSearcher;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.philius.dao.PhiliusDAOFactory;
import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.www.protein.ProteinAbundanceDao;
import org.yeastrc.www.protein.ProteinAbundanceDao.YeastOrfAbundance;
import org.yeastrc.www.proteinfer.idpicker.IdPickerResultsLoader;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerIonForProtein;
import org.yeastrc.www.proteinfer.idpicker.WIdPickerProtein;
import org.yeastrc.www.proteinfer.proteinProphet.ProteinProphetResultsLoader;
import org.yeastrc.www.proteinfer.proteinProphet.WProteinProphetIon;
import org.yeastrc.www.proteinfer.proteinProphet.WProteinProphetProtein;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

public class ProteinDetailsAjaxAction extends Action {

    private static final Logger log = Logger.getLogger(ProteinDetailsAjaxAction.class);
    
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

        int pinferId = 0;
        try {pinferId = Integer.parseInt(request.getParameter("pinferId"));}
        catch(NumberFormatException e) {}

        if(pinferId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid Protein Inference ID: "+pinferId+"</b>");
            return null;
        }

        int pinferProtId = 0;
        try {pinferProtId = Integer.parseInt(request.getParameter("pinferProtId"));}
        catch(NumberFormatException e) {}

        if(pinferProtId == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid protein inference protein ID: "+pinferProtId+"</b>");
            return null;
        }

        // Get the peptide definition from the session, if present
        PeptideDefinition peptideDef = null;
        Integer pinferId_session = (Integer)request.getSession().getAttribute("pinferId");
        if(pinferId_session != null && pinferId_session == pinferId) {
            ProteinFilterCriteria filterCriteria = (ProteinFilterCriteria) request.getAttribute("pinferFilterCriteria");
            if(filterCriteria != null) {
                peptideDef = filterCriteria.getPeptideDefinition();
            }
        }
        if(peptideDef == null) peptideDef = new PeptideDefinition();
        
        log.info("Got request for protien inference protein ID: "+pinferProtId+" of protein inference run: "+pinferId);

        long s = System.currentTimeMillis();
        
        
        ProteinferRun run = ProteinferDAOFactory.instance().getProteinferRunDao().loadProteinferRun(pinferId);
        request.setAttribute("protInferProgram", run.getProgram().name());
        request.setAttribute("inputGenerator", run.getInputGenerator().name());
        request.setAttribute("isIdPicker", ProteinInferenceProgram.isIdPicker(run.getProgram()));
        
        request.setAttribute("pinferProtId", pinferProtId);
        request.setAttribute("pinferId", pinferId);
        
        List<MsSearchResultPeptide> peptides = null;
        int nrseqProteinId = 0;
        
        
        if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {
            // get the protein 
            WIdPickerProtein iProt = IdPickerResultsLoader.getIdPickerProtein(pinferId, pinferProtId, peptideDef);
            request.setAttribute("protein", iProt);
            
            
            // Abundance information. Only for yeast
            // Ghaemmaghami, et al., Nature 425, 737-741 (2003)
            if(iProt.getProteinListing().getSpeciesId() == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE) {
            	List<YeastOrfAbundance> abundances = ProteinAbundanceDao.getInstance().getAbundance(iProt.getProteinListing().getNrseqProteinId());
            	if(abundances == null || abundances.size() == 0)
            		request.setAttribute("proteinAbundance", "NOT AVAILABLE");
            	else {
            		if(abundances.size() == 1) {
            			String abundance = abundances.get(0).getAbundanceString();
            			request.setAttribute("proteinAbundance", abundance);
            		}
            		else {
            			String aString = "";
            			for(YeastOrfAbundance a: abundances) {
            				String abundance = a.getAbundanceAndOrfNameString();
            				aString += ", "+abundance;
            			}
            			aString = aString.substring(1);
            			request.setAttribute("proteinAbundance", aString);
            		}
            	}
            }
            
            // Philius information
            boolean hasPhiliusResult = ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(run.getId());
            if(hasPhiliusResult) {
            	PhiliusResult result = PhiliusDAOFactory.getInstance().getPhiliusResultDAO().loadForSequence(iProt.getProteinListing().getSequenceId());
            	if(result != null) {
            		request.setAttribute("philiusAnnotation", result.getAnnotation());
            	}
            }
            
            // Gene Ontology information
            Set<GOAnnotation> bpAnnots = GOSearcher.getInstance().getGOAnnotations(GOUtils.BIOLOGICAL_PROCESS, iProt.getProteinListing());
    		if ( bpAnnots != null && bpAnnots.size() > 0)
    			request.setAttribute("processes", bpAnnots);

    		Set<GOAnnotation> ccAnnots = GOSearcher.getInstance().getGOAnnotations(GOUtils.CELLULAR_COMPONENT, iProt.getProteinListing());
    		if ( ccAnnots != null && ccAnnots.size() > 0)
    			request.setAttribute("components", ccAnnots);
    		
    		Set<GOAnnotation> mfAnnots = GOSearcher.getInstance().getGOAnnotations(GOUtils.MOLECULAR_FUNCTION, iProt.getProteinListing());
    		if ( mfAnnots != null && mfAnnots.size() > 0)
    			request.setAttribute("functions", mfAnnots);

    		
            // get other proteins in this group
            List<WIdPickerProtein> groupProteins = IdPickerResultsLoader.getGroupProteins(pinferId, 
                    iProt.getProtein().getProteinGroupLabel(), 
                    peptideDef);
            if(groupProteins.size() == 1)
                groupProteins.clear();
            else {
                Iterator<WIdPickerProtein> protIter = groupProteins.iterator();
                while(protIter.hasNext()) {
                    WIdPickerProtein prot = protIter.next();
                    if(prot.getProtein().getId() == iProt.getProtein().getId()) {
                        protIter.remove();
                        break;
                    }
                }
            }
            request.setAttribute("groupProteins", groupProteins);
            
            // is this protein a subset protein
            if(iProt.getProtein().getIsSubset()) {
            	List<WIdPickerProtein> superProteins = IdPickerResultsLoader.getSuperProteins(iProt.getProtein(), pinferId);
            	request.setAttribute("superProteins", superProteins);
            }
            
            // If this protein has any subset proteins get them
            List<WIdPickerProtein> subsetProteins = IdPickerResultsLoader.getSubsetProteins(iProt.getProtein(), pinferId);
            if(subsetProteins.size() > 0) {
            	request.setAttribute("subsetProteins", subsetProteins);
            }

            // We will return the best filtered search hit for each peptide ion (along with terminal residues in the protein).
            List<WIdPickerIonForProtein> ionsWAllSpectra = IdPickerResultsLoader.getPeptideIonsWithTermResiduesForProtein(pinferId, pinferProtId);
            request.setAttribute("ionList", ionsWAllSpectra);
            
            // Get the peptides for this protein (for building the protein sequence HTML)
            peptides = new ArrayList<MsSearchResultPeptide>(ionsWAllSpectra.size());
            for(WIdPickerIonForProtein ion: ionsWAllSpectra) {
                peptides.add(ion.getBestSpectrumMatch().getResultPeptide());
            }
            
            nrseqProteinId = iProt.getProtein().getNrseqProteinId();
            
        }// end if(ProteinInferenceProgram.isIdPicker(run.getProgram()))
        
        else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
            
            // get the protein 
            WProteinProphetProtein pProt = ProteinProphetResultsLoader.getWProteinProphetProtein(pinferId, pinferProtId, peptideDef);
            request.setAttribute("protein", pProt);
            
            // Abundance information. Only for yeast
            // Ghaemmaghami, et al., Nature 425, 737-741 (2003)
            if(pProt.getProteinListing().getSpeciesId() == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE) {
            	List<YeastOrfAbundance> abundances = ProteinAbundanceDao.getInstance().getAbundance(pProt.getProteinListing().getNrseqProteinId());
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
            
            // Philius information
            boolean hasPhiliusResult = ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(run.getId());
            if(hasPhiliusResult) {
            	PhiliusResult result = PhiliusDAOFactory.getInstance().getPhiliusResultDAO().loadForSequence(pProt.getProteinListing().getSequenceId());
            	if(result != null) {
            		request.setAttribute("philiusAnnotation", result.getAnnotation());
            	}
            }
            
            // Gene Ontology information
            Set<GOAnnotation> bpAnnots = GOSearcher.getInstance().getGOAnnotations(GOUtils.BIOLOGICAL_PROCESS, pProt.getProteinListing(), false);
    		if ( bpAnnots != null && bpAnnots.size() > 0)
    			request.setAttribute("processes", bpAnnots);

    		Set<GOAnnotation> ccAnnots = GOSearcher.getInstance().getGOAnnotations(GOUtils.CELLULAR_COMPONENT, pProt.getProteinListing(), false);
    		if ( ccAnnots != null && ccAnnots.size() > 0)
    			request.setAttribute("components", ccAnnots);
    		
    		Set<GOAnnotation> mfAnnots = GOSearcher.getInstance().getGOAnnotations(GOUtils.MOLECULAR_FUNCTION, pProt.getProteinListing(), false);
    		if ( mfAnnots != null && mfAnnots.size() > 0)
    			request.setAttribute("functions", mfAnnots);

    		
    		
            // get other proteins in this group
            List<WProteinProphetProtein> groupProteins = ProteinProphetResultsLoader.getGroupProteins(pinferId, 
                    pProt.getProtein().getGroupId(), 
                    peptideDef);
            if(groupProteins.size() == 1)
                groupProteins.clear();
            else {
                Iterator<WProteinProphetProtein> protIter = groupProteins.iterator();
                while(protIter.hasNext()) {
                    WProteinProphetProtein prot = protIter.next();
                    if(prot.getProtein().getId() == pProt.getProtein().getId()) {
                        protIter.remove();
                        break;
                    }
                }
            }
            request.setAttribute("groupProteins", groupProteins);
            
            // is this protein subsumed
            if(pProt.getProtein().getSubsumed()) {
            	List<WProteinProphetProtein> subsumingProteins = ProteinProphetResultsLoader.getSubsumingProteins(pProt.getProtein().getId(), pinferId);
            	request.setAttribute("subsumingProteins", subsumingProteins);
            }
            
            // Any proteins this protein is subsuming
            List<WProteinProphetProtein> subsumedProteins = ProteinProphetResultsLoader.getSubsumedProteins(pProt.getProtein().getId(), pinferId);
            if(subsumedProteins.size() > 0) {
            	request.setAttribute("subsumedProteins", subsumedProteins);
            }

            // We will return the best filtered search hit for each peptide ion (along with terminal residues in the protein).
            List<WProteinProphetIon> ionsWAllSpectra = ProteinProphetResultsLoader.getPeptideIonsForProtein(pinferId, pinferProtId);
            request.setAttribute("ionList", ionsWAllSpectra);
            
            // Get the peptides for this protein (for building the protein sequence HTML)
            peptides = new ArrayList<MsSearchResultPeptide>(ionsWAllSpectra.size());
            for(WProteinProphetIon ion: ionsWAllSpectra) {
                peptides.add(ion.getBestSpectrumMatch().getResultPeptide());
            }
            
            nrseqProteinId = pProt.getProtein().getNrseqProteinId();
        }
        
        // Get the sequence for this protein
        String sequence = NrSeqLookupUtil.getProteinSequence(nrseqProteinId);
        try {
        String proteinSequenceHtml = ProteinSequenceHtmlBuilder.getInstance().build(sequence, peptides);
        request.setAttribute("proteinSequenceHtml", proteinSequenceHtml);
        request.setAttribute("proteinSequence", sequence);
        }
        catch(ProteinSequenceHtmlBuilderException e) {
        	response.setContentType("text/html");
            response.getWriter().write("<b>Error getting sequence coverage</b><br>");
            response.getWriter().write("<b>Sequence:</b><br>");
            response.getWriter().write(sequence+"<br>");
            response.getWriter().write("<b>Error msessage:</b><br>");
            response.getWriter().write(e.getMessage());
            return null;
        }
        
        
        
        long e = System.currentTimeMillis();
        log.info("Total time (ProteinDetailsAjaxAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        if(ProteinInferenceProgram.isIdPicker(run.getProgram()))
        	return mapping.findForward("SuccessIdPicker");
        else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET)
        	return mapping.findForward("SuccessProphet");
        else {
        	response.setContentType("text/html");
            response.getWriter().write("<b>Unrecognized Protein Inference program: "+run.getProgramString()+"</b>");
            return null;
        }
    }
}
