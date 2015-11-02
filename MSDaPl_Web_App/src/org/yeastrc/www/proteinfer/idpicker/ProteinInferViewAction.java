/**
 * ProteinInferViewAction.java
 * @author Vagisha Sharma
 * Mar 19, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.slim.GOSlimLookup;
import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.CommonNameSupportUtils;
import org.yeastrc.project.Project;
import org.yeastrc.project.ProjectDAO;
import org.yeastrc.www.misc.ResultsPager;
import org.yeastrc.www.proteinfer.GOSupportUtils;
import org.yeastrc.www.proteinfer.ProteinInferPhiliusResultChecker;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;
import org.yeastrc.www.proteinfer.ProteinInferToProjectMapper;
import org.yeastrc.www.proteinfer.ProteinInferToSpeciesMapper;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;
import org.yeastrc.www.util.RoundingUtils;

import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;

/**
 * 
 */
public class ProteinInferViewAction extends Action {

	private static final Logger log = Logger.getLogger(ViewProteinInferenceResultAction.class);

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		// User making this request
        User user = UserUtils.getUser(request);

        // form for filtering and display options
        IdPickerFilterForm filterForm = (IdPickerFilterForm)form;
        
        request.setAttribute("proteinInferFilterForm", filterForm);
        
        // look for the protein inference run id in the form first
        int pinferId = filterForm.getPinferId();
        request.setAttribute("pinferId", pinferId);
        
        
        // Get a list of projects for this protein inference run.  If the user making the request to view this
        // protein inference run is not affiliated with the projects, they should not be able to edit any of 
        // the editable fields
        List<Integer> projectIds = ProteinInferToProjectMapper.map(pinferId);
        boolean writeAccess = false;
        ProjectDAO projDao = ProjectDAO.instance();
        for(int projectId: projectIds) {
            Project project = projDao.load(projectId);
            if(project.checkAccess(user.getResearcher())) {
                writeAccess = true;
                break;
            }
        }
        request.setAttribute("writeAccess", writeAccess);
        
        
        long s = System.currentTimeMillis();
        
        
        // Get the peptide definition
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        
        // Get the filtering criteria
        ProteinFilterCriteria filterCriteria = filterForm.getFilterCriteria(peptideDef);
        
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // put the list of filtered and sorted protein IDs in the session, along with the filter criteria
        ProteinInferSessionManager.getInstance().putForIdPicker(request, pinferId, filterCriteria, proteinIds);
        
        
        // page number is now 1
        int pageNum = 1;
        
        // limit to the proteins that will be displayed on this page
        List<Integer> proteinIdsPage = null;
        if(proteinIds.size() > 0) {
        	// get the index range that is to be displayed in this page
        	int[] pageIndices = ResultsPager.instance().getPageIndices(proteinIds, pageNum,false);

        	// sublist to be displayed
        	proteinIdsPage = IdPickerResultsLoader.getPageSublist(proteinIds, pageIndices,
        			filterCriteria.isGroupProteins(), false);
        }
        else {
        	proteinIdsPage = new ArrayList<Integer>(0);
        }
        
        // get the protein groups 
        List<WIdPickerProteinGroup> proteinGroups = null;
        proteinGroups = IdPickerResultsLoader.getProteinGroups(pinferId, proteinIdsPage, peptideDef);
        
        request.setAttribute("proteinGroups", proteinGroups);
        
        DisplayColumns displayColumns = getDisplayColumns(request.getCookies());
        if(ProteinInferToSpeciesMapper.isSpeciesYeast(pinferId)) {
        	request.setAttribute("yeastAbundances", true);
        	displayColumns.setSelected(DisplayColumns.yeastAbundance);
        }
        else
        	displayColumns.setDisabled(DisplayColumns.yeastAbundance);
        
        if(ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(pinferId)) {
        	request.setAttribute("philiusResults", true);
        	displayColumns.setSelected(DisplayColumns.philiusAnnot);
        }
        else
        	displayColumns.setDisabled(DisplayColumns.philiusAnnot);
        
        filterForm.setDisplayColumnList(displayColumns.getColumnList());
        request.setAttribute("displayColumns", filterForm.getDisplayColumns());
        request.getSession().setAttribute("protinferDisplayColumns", filterForm.getDisplayColumns());
        
        
        // get the list of page numbers to display
        int pageCount = ResultsPager.instance().getPageCount(proteinIds.size());
        List<Integer> pages = ResultsPager.instance().getPageList(proteinIds.size(), pageNum);
        
        
        request.setAttribute("currentPage", pageNum);
        request.setAttribute("onFirst", pageNum == 1);
        request.setAttribute("onLast", (pages.size() == 0 || (pageNum == pages.get(pages.size() - 1))));
        request.setAttribute("pages", pages);
        request.setAttribute("pageCount", pageCount);
        
        
        // Run summary
        IdPickerRun idpickerRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        request.setAttribute("idpickerRun", idpickerRun);
        
        // Input summary
        List<WIdPickerInputSummary> inputSummary = IdPickerResultsLoader.getIDPickerInputSummary(pinferId);
        request.setAttribute("inputSummary", inputSummary);
        int totalDecoyHits = 0;
        int totalTargetHits = 0;
        int filteredTargetHits = 0;
        for(WIdPickerInputSummary input: inputSummary) {
            totalDecoyHits += input.getInput().getNumDecoyHits();
            totalTargetHits += input.getInput().getNumTargetHits();
            filteredTargetHits += input.getInput().getNumFilteredTargetHits();
        }
        request.setAttribute("totalDecoyHits", totalDecoyHits);
        request.setAttribute("totalTargetHits", totalTargetHits);
        request.setAttribute("filteredTargetHits", filteredTargetHits);
        request.setAttribute("filteredPercent", 
        		RoundingUtils.getInstance().roundTwo(filteredTargetHits*100.0/(double)totalTargetHits));
        request.setAttribute("filteredUniquePeptideCount", IdPickerResultsLoader.getUniquePeptideCount(pinferId));
        request.setAttribute("filteredUniqueIonCount", IdPickerResultsLoader.getUniqueIonCount(pinferId));
        
        // Results summary
        WIdPickerResultSummary summary = IdPickerResultsLoader.getIdPickerResultSummary(pinferId, proteinIds);
        request.setAttribute("resultSummary", summary);
        
        
        request.setAttribute("sortBy", filterCriteria.getSortBy());
        request.setAttribute("sortOrder", filterCriteria.getSortOrder());
        
        // Determine if we support GO analysis for this species
        // Species for GO analyses
        List<Integer> speciesIds = ProteinInferToSpeciesMapper.map(pinferId);
        boolean supported = false; 
        boolean goslimSupported = false; 
        boolean goEnrichmentSupported = false;
        for(Integer speciesId: speciesIds) {
        	if(GOSupportUtils.isGOSlimSupported(speciesId)) {
        		supported = true;
        		goslimSupported = true;
        	}
        	if(GOSupportUtils.isGOEnrichmentSupported(speciesId)) {
        		supported = true;
        		goEnrichmentSupported = true;
        	}
        }
        if(supported) {
        	request.setAttribute("goSupported", true);
        	if(speciesIds.size() == 1) 
        		filterForm.setSpeciesId(speciesIds.get(0));
        	List<Species> speciesList = getSpeciesList(speciesIds);
        	request.setAttribute("speciesList", speciesList);

        	if(goslimSupported) {
        		// GO Slim terms
        		List<GONode> goslims = GOSlimLookup.getGOSlims();
        		request.setAttribute("goslims", goslims);
        		if(goslims.size() > 0) {
        			for(GONode slim: goslims) {
        				if(slim.getName().contains("Generic")) {
        					filterForm.setGoSlimTermId(slim.getId());
        					break;
        				}
        			}
        		}
        		request.setAttribute("goSlimSupported", true);
        	}
        	
        	if(goEnrichmentSupported)
        		request.setAttribute("goEnrichmentSupported", true);
        }
        
        for(Integer speciesId: speciesIds) {
        	if(CommonNameSupportUtils.isSpeciesSupported(speciesId)) {
        		request.setAttribute("commonNameSupported", true);
        	}
        }
        
        long e = System.currentTimeMillis();
        log.info("Total time (ProteinInferViewAction): "+TimeUtils.timeElapsedSeconds(s, e));
        
        // Go!
        return mapping.findForward("Success");
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
	
	private DisplayColumns getDisplayColumns(Cookie[] cookies) {
		
		DisplayColumns displayColumns = new DisplayColumns();
		displayColumns.setAllSelected();
		for(Cookie cookie: cookies) {
			if(cookie.getName().equals("noDispCols_protinfer")) {
				String val = cookie.getValue();
				if(val != null) {
					String[] tokens = val.split("_");
					for(String tok: tokens) {
						displayColumns.setNotSelected(tok.charAt(0));
					}
				}
			}
		}
		
		return displayColumns;
	}
}
