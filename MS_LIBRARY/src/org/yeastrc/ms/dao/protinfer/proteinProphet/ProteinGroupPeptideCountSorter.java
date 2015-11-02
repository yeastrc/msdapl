/**
 * ProteinGroupPeptideCountSorder.java
 * @author Vagisha Sharma
 * Mar 30, 2010
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.proteinProphet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.domain.protinfer.SORT_ORDER;

/**
 * 
 */
public class ProteinGroupPeptideCountSorter {

	private static ProteinGroupPeptideCountSorter instance;

	private ProteinGroupPeptideCountSorter () {}

	public static ProteinGroupPeptideCountSorter getInstance() {
		if(instance == null)
			instance = new ProteinGroupPeptideCountSorter();
		return instance;
	}

	/**
	 * Input list should already be sorted by proteinProphetGroupId, groupId, peptideCount (DESC)
	 * @param list
	 */
	void sort(List<ProteinGroupPeptideCount> list, SORT_ORDER sortOrder) {

		sortByProphetGroupId(list, sortOrder);
	}
	
	// Assume: list is sorted by proteinProphetGroupId, groupId, peptideCount(DESC)
	private void sortByProphetGroupId(List<ProteinGroupPeptideCount> list, SORT_ORDER sortOrder) {
		
		int lastProphetGrp = -1;

		// All members of a single ProteinProphetGroup
		List<ProteinGroupPeptideCount> grpList = new ArrayList<ProteinGroupPeptideCount>();

		for(ProteinGroupPeptideCount pgc: list) {
			if(pgc.proteinProphetGroupId != lastProphetGrp) {
				
				setProphetGrpPeptideCount(grpList, sortOrder);
				lastProphetGrp = pgc.proteinProphetGroupId;
				grpList.clear();
			}
			grpList.add(pgc);
		}

		// last one
		setProphetGrpPeptideCount(grpList, sortOrder);

		if(sortOrder == SORT_ORDER.ASC) {
			Collections.sort(list, new Comparator<ProteinGroupPeptideCount>() {
				@Override
				public int compare(ProteinGroupPeptideCount o1, ProteinGroupPeptideCount o2) {
					int val = Integer.valueOf(o1.prophetGrpPeptideCount).compareTo(o2.prophetGrpPeptideCount);
					if(val != 0)    return val;
					val = Integer.valueOf(o1.proteinProphetGroupId).compareTo(o2.proteinProphetGroupId);
					if(val != 0)	return val;
					val = Integer.valueOf(o1.peptideCount).compareTo(o2.peptideCount);
					if(val != 0)	return val;
					return Integer.valueOf(o1.proteinGroupId).compareTo(o2.proteinGroupId);
				}});
		}
		else {
			Collections.sort(list, new Comparator<ProteinGroupPeptideCount>() {
				@Override
				public int compare(ProteinGroupPeptideCount o1, ProteinGroupPeptideCount o2) {
					int val = Integer.valueOf(o2.prophetGrpPeptideCount).compareTo(o1.prophetGrpPeptideCount);
					if(val != 0)    return val;
					val = Integer.valueOf(o2.proteinProphetGroupId).compareTo(o1.proteinProphetGroupId);
					if(val != 0)    return val;
					val = Integer.valueOf(o2.peptideCount).compareTo(o1.peptideCount);
					if(val != 0)    return val;
					return Integer.valueOf(o2.proteinGroupId).compareTo(o1.proteinGroupId);
				}});
		}
	}
	
	private void setProphetGrpPeptideCount(List<ProteinGroupPeptideCount> grpList, SORT_ORDER sortOrder) {

		if(grpList.size() == 0)
			return;

		int minCount = Integer.MAX_VALUE;
		int maxCount = 0;

		for(ProteinGroupPeptideCount pgc: grpList) {
			minCount = pgc.peptideCount < minCount ? pgc.peptideCount : minCount;
			maxCount = pgc.peptideCount > maxCount ? pgc.peptideCount : maxCount;
		}

		// set the peptide count for the Prophet group
		int grpCount = sortOrder == SORT_ORDER.DESC ? maxCount : minCount;

		for(ProteinGroupPeptideCount pgc: grpList)
			pgc.prophetGrpPeptideCount = grpCount;
	}
	
	public static class ProteinGroupPeptideCount {
		private int proteinId;
		private int proteinGroupId;
		private int proteinProphetGroupId;
		private int peptideCount;
		private int prophetGrpPeptideCount;
		
		public void setProteinId(int proteinId) {
			this.proteinId = proteinId;
		}
		public void setProteinGroupId(int proteinGroupId) {
			this.proteinGroupId = proteinGroupId;
		}
		public void setProteinProphetGroupId(int proteinProphetGroupId) {
			this.proteinProphetGroupId = proteinProphetGroupId;
		}
		public void setPeptideCount(int peptideCount) {
			this.peptideCount = peptideCount;
		}
		public int getProteinId() {
			return proteinId;
		}
	}
}
