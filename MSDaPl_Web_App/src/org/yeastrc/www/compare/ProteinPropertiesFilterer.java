/**
 * ProteinDatasetFilterer.java
 * @author Vagisha Sharma
 * Mar 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.graph.ComparisonProteinGroup;
import org.yeastrc.www.compare.util.FastaDatabaseLookupUtil;
import org.yeastrc.www.proteinfer.ProteinAccessionFilter;
import org.yeastrc.www.proteinfer.ProteinCommonNameFilter;
import org.yeastrc.www.proteinfer.ProteinDescriptionFilter;
import org.yeastrc.www.proteinfer.ProteinGoTermsFilter;
import org.yeastrc.www.proteinfer.ProteinPropertiesFilter;

/**
 * 
 */
public class ProteinPropertiesFilterer {

	private static ProteinPropertiesFilterer instance = null;

	private static final Logger log = Logger.getLogger(ProteinPropertiesFilterer.class.getName());
	
	private ProteinPropertiesFilterer () {}

	public static ProteinPropertiesFilterer getInstance() {
		if(instance == null)
			instance = new ProteinPropertiesFilterer();
		return instance;
	}

	public void applyProteinPropertiesFilters(List<ComparisonProtein> proteins, 
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets) 
		throws Exception {
		
		List<Integer> nrseqIds = new ArrayList<Integer>(proteins.size());
		for(ComparisonProtein protein: proteins) {
			nrseqIds.add(protein.getNrseqId());
		}
		
		nrseqIds = filterNrseqIds(filters, datasets, nrseqIds);
		
		// sort the filtered Ids
		Collections.sort(nrseqIds);
		// keep the ComparisonProteins that are in the filtered IDs
		Iterator<ComparisonProtein> iter = proteins.iterator();
		while(iter.hasNext()) {
			ComparisonProtein prot = iter.next();
			if(Collections.binarySearch(nrseqIds, prot.getNrseqId()) < 0)
				iter.remove();
		}
	}

	
	public void applyProteinPropertiesFiltersToGroup(List<ComparisonProteinGroup> proteinGroups, 
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets) throws Exception {
		
		List<Integer> nrseqIds = new ArrayList<Integer>(proteinGroups.size());
		for(ComparisonProteinGroup proteinGrp: proteinGroups) {
			for(ComparisonProtein protein: proteinGrp.getProteins())
				nrseqIds.add(protein.getNrseqId());
		}
		
		nrseqIds = filterNrseqIds(filters, datasets, nrseqIds);
		
		
		// sort the filtered Ids
		Collections.sort(nrseqIds);
		// keep the ComparisonProteinGroups that are in the filtered IDs
		Iterator<ComparisonProteinGroup> iter = proteinGroups.iterator();
		while(iter.hasNext()) {
			ComparisonProteinGroup protGrp = iter.next();
			
			boolean matches = false;
			// If any one member of the group matches we will keep the entire group
			for(ComparisonProtein prot: protGrp.getProteins()) {
				if(Collections.binarySearch(nrseqIds, prot.getNrseqId()) >= 0) {
					matches = true;
					break; // found a match
				}
			}
			if(!matches)
				iter.remove();
		}
	}
	
	// ----------------------------------------------------------------------
	// ALL FILTERS
	// ----------------------------------------------------------------------
	private List<Integer> filterNrseqIds(ProteinPropertiesFilters filters,
			List<? extends Dataset> datasets, List<Integer> nrseqIds)
			throws Exception {
		log.info("Number of nrseq IDs BEFORE filtering: "+nrseqIds.size());
		
		// apply common name filter
		nrseqIds = applyCommonNameFilter(filters, nrseqIds);
		
		// apply accession filter
		nrseqIds = applyAccessionFilter(filters, datasets, nrseqIds);
		
		// apply description LIKE filter
		nrseqIds = applyDescriptionLikeFilter(filters, datasets, nrseqIds);
		
		// apply GO terms filter
		nrseqIds = applyGOTermsFilter(filters, nrseqIds);
		
		// apply description NOT LIKE filter
		nrseqIds = applyDescriptionNotLikeFilter(filters, datasets, nrseqIds);
		
		
		// apply molecular wt and pI filters
		nrseqIds = applyMolWtAndPiFilters(filters, nrseqIds);
		log.info("Number of nrseq IDs AFTER filtering: "+nrseqIds.size());
		return nrseqIds;
	}
	
	// ----------------------------------------------------------------------
	// MOLECULAR WT. AND PI
	// ----------------------------------------------------------------------
	private List<Integer> applyMolWtAndPiFilters(
			ProteinPropertiesFilters filters, List<Integer> nrseqIds) {
		long s;
		long e;
		if(filters.hasMolecularWtFilter() || filters.hasPiFilter()) {
			s = System.currentTimeMillis();
			ProteinFilterCriteria filterCriteria = new ProteinFilterCriteria();
			filterCriteria.setMinMolecularWt(filters.getMinMolecularWt());
			filterCriteria.setMaxMolecularWt(filters.getMaxMolecularWt());
			filterCriteria.setMinPi(filters.getMinPi());
			filterCriteria.setMaxPi(filters.getMaxPi());
			nrseqIds = ProteinPropertiesFilter.getInstance().filterNrseqIdsyMolecularWtAndPi(nrseqIds, filterCriteria);
			e = System.currentTimeMillis();
			log.info("Time to filter on Mol. Wt. and pI: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}
		return nrseqIds;
	}
	
	// ----------------------------------------------------------------------
	// DESCRIPTION NOT LIKE
	// ----------------------------------------------------------------------
	private List<Integer> applyDescriptionNotLikeFilter(
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets,
			List<Integer> nrseqIds) {
		long s;
		long e;
		if(filters.hasDescriptionNotLikeFilter()) {
			s = System.currentTimeMillis();
			
			List<Integer> pinferIds = new ArrayList<Integer>(datasets.size());
			for(Dataset ds: datasets)
				pinferIds.add(ds.getDatasetId());
			
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsByDescriptionNotLike(pinferIds, nrseqIds, 
					filters.getDescriptionNotLike(), filters.isSearchAllDescriptions());
			e = System.currentTimeMillis();
			log.info("Time to filter on description (NOT LIKE): "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}
		return nrseqIds;
	}
	
	// ----------------------------------------------------------------------
	// DESCRIPTION LIKE
	// ----------------------------------------------------------------------
	private List<Integer> applyDescriptionLikeFilter(
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets,
			List<Integer> nrseqIds) {
		if(filters.hasDescriptionLikeFilter()) {
			long s = System.currentTimeMillis();
			
			List<Integer> pinferIds = new ArrayList<Integer>(datasets.size());
			for(Dataset ds: datasets)
				pinferIds.add(ds.getDatasetId());
			
			nrseqIds = ProteinDescriptionFilter.getInstance().filterNrseqIdsByDescriptionLike(pinferIds, nrseqIds, 
					filters.getDescriptionLike(), filters.isSearchAllDescriptions());
			long e = System.currentTimeMillis();
			log.info("Time to filter on description (LIKE): "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}
		return nrseqIds;
	}

	// ----------------------------------------------------------------------
	// ACCESSION
	// ----------------------------------------------------------------------
	private List<Integer> applyAccessionFilter(
			ProteinPropertiesFilters filters, List<? extends Dataset> datasets,
			List<Integer> nrseqIds) throws SQLException {
		
		if(filters.hasAccessionFilter()) {
			long s = System.currentTimeMillis();
			
			List<Integer> fastaDbIds = FastaDatabaseLookupUtil.getFastaDatabaseIds(datasets);
			
			nrseqIds = ProteinAccessionFilter.getInstance().filterNrseqIdsByAccession(nrseqIds, 
					filters.getAccessionLike(), fastaDbIds);
			long e = System.currentTimeMillis();
			
			log.info("Time to filter on accession: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}
		return nrseqIds;
	}
	
	// ----------------------------------------------------------------------
	// COMMON NAME
	// ----------------------------------------------------------------------
	private List<Integer> applyCommonNameFilter(
			ProteinPropertiesFilters filters, List<Integer> nrseqIds) throws SQLException {
		
		if(filters.hasCommonNameFilter()) {
			long s = System.currentTimeMillis();
			
			nrseqIds = ProteinCommonNameFilter.getInstance().filterNrseqIdsByCommonName(nrseqIds, 
					filters.getCommonNameLike());
			long e = System.currentTimeMillis();
			
			log.info("Time to filter on common name: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}
		return nrseqIds;
	}
	
	// ----------------------------------------------------------------------
	// GO FILTERS
	// ----------------------------------------------------------------------
	private List<Integer> applyGOTermsFilter(
			ProteinPropertiesFilters filters, List<Integer> nrseqIds) throws Exception {
		
		GOProteinFilterCriteria goFilters = filters.getGoFilter();
		
		if(goFilters != null) {
			long s = System.currentTimeMillis();
			
			nrseqIds = ProteinGoTermsFilter.getInstance().filterNrseqProteins(nrseqIds, filters.getGoFilter());
			long e = System.currentTimeMillis();
			
			log.info("Time to filter on GO terms: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}
		return nrseqIds;
	}
}
