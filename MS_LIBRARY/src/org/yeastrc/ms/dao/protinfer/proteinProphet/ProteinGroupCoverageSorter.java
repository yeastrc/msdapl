/**
 * ProteinGroupCoverageSorter.java
 * @author Vagisha Sharma
 * Mar 22, 2010
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;

/**
 * 
 */
public class ProteinGroupCoverageSorter {

	private static ProteinGroupCoverageSorter instance;

	private ProteinGroupCoverageSorter () {}

	public static ProteinGroupCoverageSorter getInstance() {
		if(instance == null)
			instance = new ProteinGroupCoverageSorter();
		return instance;
	}

	/**
	 * Sorting can be either:
	 * 1. ProteinProphetGroupID 
	 * 		-- in this case the input list should already be sorted by proteinProphetGroupId, groupId, coverage (DESC)
	 * 2. IndistinguishableProteinGroupID
	 *		-- list should already be sorted by groupId, coverage (DESC)
	 * @param list
	 */
	void sort(List<ProteinGroupCoverage> list, SORT_ORDER sortOrder, SORT_BY sortBy) {

		if(sortBy == SORT_BY.GROUP_ID)
			sortByIGroupId(list, sortOrder);
		else 
			sortByProphetGroupId(list, sortOrder);
		
	}
	
	// Assume: list is sorted by proteinProphetGroupId, groupId, coverage(DESC)
	private void sortByProphetGroupId(List<ProteinGroupCoverage> list, SORT_ORDER sortOrder) {
		
		int lastProphetGrp = -1;

		// All members of a single ProteinProphetGroup
		List<ProteinGroupCoverage> grpList = new ArrayList<ProteinGroupCoverage>();

		for(ProteinGroupCoverage pgc: list) {
			if(pgc.proteinProphetGroupId != lastProphetGrp) {
				
				setProphetGrpCoverage(grpList, sortOrder);
				lastProphetGrp = pgc.proteinProphetGroupId;
				grpList.clear();
			}
			grpList.add(pgc);
		}

		// last one
		setProphetGrpCoverage(grpList, sortOrder);

		if(sortOrder == SORT_ORDER.ASC) {
			Collections.sort(list, new Comparator<ProteinGroupCoverage>() {
				@Override
				public int compare(ProteinGroupCoverage o1, ProteinGroupCoverage o2) {
					int val = Double.valueOf(o1.prophetGrpCoverage).compareTo(o2.prophetGrpCoverage);
					if(val != 0)    return val;
					val = Integer.valueOf(o1.proteinProphetGroupId).compareTo(o2.proteinProphetGroupId);
					if(val != 0)	return val;
					val = Double.valueOf(o1.grpCoverage).compareTo(o2.grpCoverage);
					if(val != 0)	return val;
					val = Integer.valueOf(o1.proteinGroupId).compareTo(o2.proteinGroupId);
					if(val != 0)    return val;
					return Double.valueOf(o1.coverage).compareTo(o2.coverage);
				}});
		}
		else {
			Collections.sort(list, new Comparator<ProteinGroupCoverage>() {
				@Override
				public int compare(ProteinGroupCoverage o1, ProteinGroupCoverage o2) {
					int val = Double.valueOf(o2.prophetGrpCoverage).compareTo(o1.prophetGrpCoverage);
					if(val != 0)    return val;
					val = Integer.valueOf(o2.proteinProphetGroupId).compareTo(o1.proteinProphetGroupId);
					if(val != 0)    return val;
					val = Double.valueOf(o2.grpCoverage).compareTo(o1.grpCoverage);
					if(val != 0)    return val;
					val = Integer.valueOf(o2.proteinGroupId).compareTo(o1.proteinGroupId);
					if(val != 0)    return val;
					return Double.valueOf(o2.coverage).compareTo(o1.coverage);
				}});
		}
	}
	
	// Assume: list is sorted by groupId, coverage(DESC)
	private void sortByIGroupId(List<ProteinGroupCoverage> list, SORT_ORDER sortOrder) {
		
		int lastGrp = -1;

		// All members of a single indistinguishable protein group
		List<ProteinGroupCoverage> grpList = new ArrayList<ProteinGroupCoverage>();

		for(ProteinGroupCoverage pgc: list) {
			if(pgc.proteinGroupId != lastGrp) {

				setGrpCoverage(grpList, sortOrder);
				lastGrp = pgc.proteinGroupId;
				grpList.clear();
			}
			grpList.add(pgc);
		}

		// last one
		setGrpCoverage(grpList, sortOrder);

		if(sortOrder == SORT_ORDER.ASC) {
			Collections.sort(list, new Comparator<ProteinGroupCoverage>() {
				@Override
				public int compare(ProteinGroupCoverage o1, ProteinGroupCoverage o2) {
					int val = Double.valueOf(o1.grpCoverage).compareTo(o2.grpCoverage);
					if(val != 0)    return val;
					val = Integer.valueOf(o1.proteinGroupId).compareTo(o2.proteinGroupId);
					if(val != 0)    return val;
					return Double.valueOf(o1.coverage).compareTo(o2.coverage);
				}});
		}
		else {
			Collections.sort(list, new Comparator<ProteinGroupCoverage>() {
				@Override
				public int compare(ProteinGroupCoverage o1, ProteinGroupCoverage o2) {
					int val = Double.valueOf(o2.grpCoverage).compareTo(o1.grpCoverage);
					if(val != 0)    return val;
					val = Integer.valueOf(o1.proteinGroupId).compareTo(o2.proteinGroupId);
					if(val != 0)    return val;
					return Double.valueOf(o2.coverage).compareTo(o1.coverage);
				}});
		}
	}

	private void setProphetGrpCoverage(List<ProteinGroupCoverage> grpList, SORT_ORDER sortOrder) {

		if(grpList.size() == 0)
			return;

		// set the coverage for indistinguishable protein groups
		int lastGroupId = -1;

		double minCoverage = Double.MAX_VALUE;
		double maxCoverage = 0;
		// All members of a single indistinguishable protein group
		List<ProteinGroupCoverage> iGrpList = new ArrayList<ProteinGroupCoverage>();

		for(ProteinGroupCoverage pgc: grpList) {
			if(pgc.proteinGroupId != lastGroupId) {

				setGrpCoverage(iGrpList, sortOrder);
				lastGroupId = pgc.proteinGroupId;
				iGrpList.clear();
			}
			iGrpList.add(pgc);
			minCoverage = pgc.coverage < minCoverage ? pgc.coverage : minCoverage;
			maxCoverage = pgc.coverage > maxCoverage ? pgc.coverage : maxCoverage;
		}

		// last one
		setGrpCoverage(iGrpList, sortOrder);
		
		// set the coverage for the Prophet group
		double grpCoverage = sortOrder == SORT_ORDER.DESC ? maxCoverage : minCoverage;

		for(ProteinGroupCoverage pgc: grpList)
			pgc.prophetGrpCoverage = grpCoverage;
	}
	
	private void setGrpCoverage(List<ProteinGroupCoverage> grpList, SORT_ORDER sortOrder) {

		if(grpList.size() == 0)
			return;

		double minCoverage = Double.MAX_VALUE;
		double maxCoverage = 0;
		
		for(ProteinGroupCoverage pgc: grpList) {
			minCoverage = pgc.coverage < minCoverage ? pgc.coverage : minCoverage;
			maxCoverage = pgc.coverage > maxCoverage ? pgc.coverage : maxCoverage;
		}
//		double grpCoverage = sortOrder == SORT_ORDER.DESC ? grpList.get(0).coverage : 
//			grpList.get(grpList.size() - 1).coverage;

		double grpCoverage = sortOrder == SORT_ORDER.DESC ? maxCoverage : minCoverage;
		
		for(ProteinGroupCoverage pgc: grpList)
			pgc.grpCoverage = grpCoverage;
	}

	public static class ProteinGroupCoverage {
		private int proteinId;
		private int proteinGroupId;
		private int proteinProphetGroupId;
		private double coverage;
		private double grpCoverage;
		private double prophetGrpCoverage;
		
		public void setProteinId(int proteinId) {
			this.proteinId = proteinId;
		}
		public void setProteinGroupId(int proteinGroupId) {
			this.proteinGroupId = proteinGroupId;
		}
		public void setProteinProphetGroupId(int proteinProphetGroupId) {
			this.proteinProphetGroupId = proteinProphetGroupId;
		}
		public void setCoverage(double coverage) {
			this.coverage = coverage;
		}
		public int getProteinId() {
			return proteinId;
		}
	}
}
