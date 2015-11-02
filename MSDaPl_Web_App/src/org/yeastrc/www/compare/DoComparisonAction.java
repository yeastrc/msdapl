/**
 * DoComparison.java
 * @author Vagisha Sharma
 * Nov 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import org.yeastrc.jobqueue.MSJob;
import org.yeastrc.jobqueue.MSJobFactory;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.SORT_ORDER;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nrseq.CommonNameSupportUtils;
import org.yeastrc.www.compare.ProteinDatasetComparer.PARSIM;
import org.yeastrc.www.compare.dataset.DatasetBuilder;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.compare.dataset.FilterableDataset;
import org.yeastrc.www.compare.dataset.ProteinProphetDataset;
import org.yeastrc.www.compare.dataset.ProteinferDataset;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.graph.GraphBuilder;
import org.yeastrc.www.compare.util.VennDiagramCreator;

import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;

/**
 * 
 */
public class DoComparisonAction extends Action {

    private static final Logger log = Logger.getLogger(DoComparisonAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
    	log.info("Got request to compare protein inferences");
    	
        // Form we will use
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) form;
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "No comparison form in request."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        // Get the selected protein inference run ids
        // these should be ordered by any user specified order
        List<Integer> allRunIds = myForm.getAllSelectedRunIdsOrdered();
        
        // Need atleast two datasets to compare.
        if(allRunIds.size() < 2) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Please select 2 or more datasets to compare."));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        
        request.setAttribute("comparisonCommands", ComparisonCommand.getCommands());
        
        // Get the species. 
        List<Integer> speciesIds = getMySpeciesIds(allRunIds);
        for(Integer speciesId: speciesIds) {
        	if(CommonNameSupportUtils.isSpeciesSupported(speciesId)) {
        		request.setAttribute("commonNameSupported", true);
        	}
        }
        
        
        // Get the datasets we will be comparing
        List<FilterableDataset> datasets = new ArrayList<FilterableDataset>(allRunIds.size());
        
        long s = System.currentTimeMillis();
        for(int piRunId: allRunIds) {
            
            FilterableDataset dataset = DatasetBuilder.instance().buildFilterableDataset(piRunId);
            if(dataset == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", 
                        "No protein inference run found with ID: "+piRunId+"."));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            if(dataset.getSource() == DatasetSource.PROTINFER) {
                ((ProteinferDataset)dataset).setProteinFilterCriteria(myForm.getFilterCriteria());
            }
            else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
                ((ProteinProphetDataset)dataset).setProteinFilterCriteria(myForm.getProteinProphetFilterCriteria(dataset));
            }
            datasets.add(dataset);
        }
        long e = System.currentTimeMillis();
        log.info("Built comparison datasets in "+TimeUtils.timeElapsedMinutes(s, e)+" seconds");
        
        // Do the comparison
        log.info("Starting comparison");
        s = System.currentTimeMillis();
        long start = s;
        ProteinComparisonDataset comparison = ProteinDatasetComparer.instance().compareDatasets(datasets, 
        		PARSIM.getForValue(myForm.getParsimoniousParam()));
        e = System.currentTimeMillis();
        log.info("Time to compare datasets: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        
        // ANY AND, OR, NOT, XOR filters
        DatasetBooleanFilters filters = myForm.getSelectedBooleanFilters();
        // Apply AND, OR, NOT, XOR filters
        s = System.currentTimeMillis();
        ProteinDatasetBooleanFilterer.getInstance().applyBooleanFilters(comparison, filters);
        e = System.currentTimeMillis();
        log.info("Time to apply boolean filters: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        
        
        // If the user requested GO analysis we will not group indistinguishable proteins even if that
        // option was checked in the form. For GO analysis we only care about a list of proteins
        if(myForm.isDoGoAnalysis()) {
        	myForm.setGroupIndistinguishableProteins(false);
        }
        
        // Indistinguishable proteins are NOT being grouped.
        if(!myForm.getGroupIndistinguishableProteins()) {
        	
        	// apply other filters
        	ProteinPropertiesFilterer.getInstance().applyProteinPropertiesFilters(comparison.getProteins(), 
        			myForm.getProteinPropertiesFilters(), datasets);
        	
        	// sorting order
            comparison.setSortBy(myForm.getSortBy());
            comparison.setSortOrder(myForm.getSortOrder());
            
            comparison.setDisplayColumns(myForm.getDisplayColumns());
        	comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
        	
        	// If User requested GO analysis forward to another action class
        	// GO ANALYSIS?
            if(myForm.isDoGoAnalysis()) {
            	
                comparison.initSummary(); // initialize the summary (totalProteinCount, # common proteins)
                request.setAttribute("comparisonForm", myForm);
                request.setAttribute("comparisonDataset", comparison);
                if(myForm.isDoGoSlimAnalysis()) {
                	log.info("DOING GO Slim ANALYSIS...");
                	return mapping.findForward("GOSlim");
                }
                else if(myForm.isDoGoSlimAnalysisTree()) {
                	log.info("MAKING GO Slim Tree...");
                	return mapping.findForward("GOSlim");
                }
                else if(myForm.isDoGoEnrichAnalysis()) {
                	log.info("DOING GENE ONTOLOGY ENRICHMENT ANALYSIS...");
                	return mapping.findForward("GOEnrichment");
                }
                else if(myForm.isDoGoEnrichAnalysisTree()) {
                	log.info("MAKING GO Enriched Terms Tree...");
                	return mapping.findForward("GOEnrichment");
                }
            }
            
            // IS THE USER DOWNLOADING?
            if(myForm.isDownload()) {
                log.info("DOWNLOADING......");
                request.setAttribute("comparisonForm", myForm);
                request.setAttribute("comparisonDataset", comparison);
                return mapping.findForward("Download");
            }
            
            // IS THE USER INTERESTED IN CLUSTERING RESULTS?
            if(myForm.isCluster()) {
            	log.info("CLUSTERING......");
                request.setAttribute("comparisonForm", myForm);
                request.setAttribute("comparisonDataset", comparison);
                return mapping.findForward("Cluster");
            }
            
        	// sort the results
        	s = System.currentTimeMillis();
        	
        	ComparisonProteinSorter sorter = ComparisonProteinSorter.getInstance();
        	SORT_BY sortBy = myForm.getSortBy();
            SORT_ORDER sortOrder = myForm.getSortOrder();
        	if(sortBy == SORT_BY.MOL_WT)
                sorter.sortByMolecularWeight(comparison.getProteins(), sortOrder);
            else if (sortBy == SORT_BY.PI)
                sorter.sortByPi(comparison.getProteins(), sortOrder);
            else if (sortBy == SORT_BY.ACCESSION)
            	sorter.sortByAccession(comparison.getProteins(), sortOrder, comparison.getFastaDatabaseIds());
            else
                sorter.sortByPeptideCount(comparison.getProteins(), sortOrder);
            
            e = System.currentTimeMillis();
            log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
        	
            // Set the page number and the number of results to be displayed per page
            comparison.setNumPerPage(myForm.getNumPerPage()); // this method call should come first
            comparison.setCurrentPage(myForm.getPageNum());
            
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(comparison.getDatasetCount() == 2 || comparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(comparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            // Enable filtering on GO terms
            request.setAttribute("goSupported", true);
            
            // create a list of the dataset ids being compared
            request.setAttribute("datasetIds", StringUtils.makeCommaSeparated(allRunIds));
            
            request.setAttribute("comparison", comparison);
            request.setAttribute("speciesIsYeast", SpeciesChecker.isSpeciesYeast(datasets));
            return mapping.findForward("ProteinList");
        }
        
        else {
        	
        	// If the user IS NOT interested in keeping protein groups intact after filtering 
            // apply filters now
        	if(!myForm.isKeepProteinGroups()) {
        		ProteinPropertiesFilterer.getInstance().applyProteinPropertiesFilters(comparison.getProteins(), 
            			myForm.getProteinPropertiesFilters(), datasets);
        	}
        	
        	// Do graph analysis to get indistinguishable protein groups
            s = System.currentTimeMillis();
            GraphBuilder graphBuilder = new GraphBuilder();
            BipartiteGraph<ComparisonProteinGroup, PeptideVertex> graph = 
                graphBuilder.buildGraph(comparison.getProteins(), allRunIds);
            log.info("BEFORE collapsing graph: "+graph.getLeftVertices().size());
            GraphCollapser<ComparisonProteinGroup, PeptideVertex> collapser = new GraphCollapser<ComparisonProteinGroup, PeptideVertex>();
            collapser.collapseGraph(graph);
            
            List<ComparisonProteinGroup> proteinGroups = graph.getLeftVertices();
            log.info("AFTER collapsing graph: "+proteinGroups.size());
            
            
            // assign group IDs
            int groupId = 1;
            for(ComparisonProteinGroup group: proteinGroups)
                group.setGroupId(groupId++);
            
            
            e = System.currentTimeMillis();
            log.info("Time to do graph analysis: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            // If the user IS interested in keeping protein groups intact after filtering 
            // apply filters now
            if(myForm.isKeepProteinGroups()) {
            	ProteinPropertiesFilterer.getInstance().applyProteinPropertiesFiltersToGroup(proteinGroups, 
            			myForm.getProteinPropertiesFilters(), datasets);
            }
            
            // Sort the results
            s = System.currentTimeMillis();
            
            ComparisonProteinSorter sorter = ComparisonProteinSorter.getInstance();
            SORT_BY sortBy = myForm.getSortBy();
            SORT_ORDER sortOrder = myForm.getSortOrder();
            if(sortBy == SORT_BY.MOL_WT)
                sorter.sortGroupsByMolecularWeight(proteinGroups, sortOrder);
            else if (sortBy == SORT_BY.PI)
                sorter.sortGroupsByPi(proteinGroups, sortOrder);
            else if (sortBy == SORT_BY.ACCESSION)
            	sorter.sortGroupsByAccession(proteinGroups, sortOrder, comparison.getFastaDatabaseIds());
            else
                sorter.sortGroupsByPeptideCount(proteinGroups, sortOrder);
            
            e = System.currentTimeMillis();
            log.info("Time to sort results: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            // Create the group comparison dataset
            ProteinGroupComparisonDataset grpComparison = new ProteinGroupComparisonDataset();
            grpComparison.setDisplayColumns(myForm.getDisplayColumns());
            
            for(ComparisonProteinGroup grp: proteinGroups)
                grpComparison.addProteinGroup(grp);
            
            grpComparison.setDatasets(datasets);
            
            s = System.currentTimeMillis();
            grpComparison.initSummary(); // initialize the summary -- 
                                        // (totalProteinCount, # common proteins)
                                        // spectrum count normalization factors
                                        // calculate min/max normalized spectrum counts for scaling
            e = System.currentTimeMillis();
            log.info("Time to initialize summary: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            
            // IS THE USER DOWNLOADING?
            if(myForm.isDownload()) {
                log.info("DOWNLOADING......");
                request.setAttribute("comparisonForm", myForm);
                request.setAttribute("comparisonGroupDataset", grpComparison);
                return mapping.findForward("Download");
            }
            
            // IS THE USER INTERESTED IN CLUSTERING RESULTS?
            if(myForm.isCluster()) {
            	log.info("CLUSTERING......");
                request.setAttribute("comparisonForm", myForm);
                request.setAttribute("comparisonGroupDataset", grpComparison);
                return mapping.findForward("Cluster");
            }
            
            // Set the page number and the number of results to be displayed per page
            grpComparison.setNumPerPage(myForm.getNumPerPage()); // this method call should come first
            grpComparison.setCurrentPage(myForm.getPageNum());
            
            
            // sorting order
            grpComparison.setSortBy(myForm.getSortBy());
            grpComparison.setSortOrder(myForm.getSortOrder());
            
            
            // Create Venn Diagram only if 2 or 3 datasets are being compared
            if(grpComparison.getDatasetCount() == 2 || grpComparison.getDatasetCount() == 3) {
                String googleChartUrl = VennDiagramCreator.instance().getChartUrl(grpComparison);
                request.setAttribute("chart", googleChartUrl);
            }
            
            long end = System.currentTimeMillis();
            log.info("DoComparisonAction finished in: "+TimeUtils.timeElapsedSeconds(start, end)+" seconds");
            
            
            // Enable filtering on GO terms
            request.setAttribute("goSupported", true);
            
            
            // create a list of the dataset ids being compared
            request.setAttribute("datasetIds", StringUtils.makeCommaSeparated(allRunIds));
            
            request.setAttribute("comparison", grpComparison);
            request.setAttribute("speciesIsYeast", SpeciesChecker.isSpeciesYeast(datasets));
            return mapping.findForward("ProteinGroupList");
        }
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
	
//	private List<Species> getSpeciesList(List<Integer> mySpeciesIds) throws SQLException {
//		List<Species> speciesList = GOSupportUtils.getSpeciesList();
//
//		if(mySpeciesIds.size() == 1) {
//			int sid = mySpeciesIds.get(0);
//			boolean found = false;
//			for(Species sp: speciesList) {
//				if(sp.getId() == sid) {
//					found = true; break;
//				}
//			}
//			if(!found) {
//				Species species = new Species();
//				species.setId(sid);
//				speciesList.add(species);
//			}
//		}
//		return speciesList;
//	}
}
