/**
 * ProteinGroupSpectrumCountSorter.java
 * @author Vagisha Sharma
 * Mar 31, 2010
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
public class ProteinGroupSpectrumCountSorter {

	private static ProteinGroupSpectrumCountSorter instance;

	private ProteinGroupSpectrumCountSorter () {}

	public static ProteinGroupSpectrumCountSorter getInstance() {
		if(instance == null)
			instance = new ProteinGroupSpectrumCountSorter();
		return instance;
	}

	/**
	 * Input list should already be sorted by proteinProphetGroupId, groupId, spectrumCount (DESC)
	 * @param list
	 */
	void sort(List<ProteinGroupSpectrumCount> list, SORT_ORDER sortOrder) {

		sortByProphetGroupId(list, sortOrder);
		
	}
	
	// Assume: list is sorted by proteinProphetGroupId, groupId, spectrumCount(DESC)
	private void sortByProphetGroupId(List<ProteinGroupSpectrumCount> list, SORT_ORDER sortOrder) {
		
		int lastProphetGrp = -1;

		// All members of a single ProteinProphetGroup
		List<ProteinGroupSpectrumCount> grpList = new ArrayList<ProteinGroupSpectrumCount>();

		for(ProteinGroupSpectrumCount pgc: list) {
			if(pgc.proteinProphetGroupId != lastProphetGrp) {
				
				setProphetGrpSpectrumCount(grpList, sortOrder);
				lastProphetGrp = pgc.proteinProphetGroupId;
				grpList.clear();
			}
			grpList.add(pgc);
		}

		// last one
		setProphetGrpSpectrumCount(grpList, sortOrder);

		if(sortOrder == SORT_ORDER.ASC) {
			Collections.sort(list, new Comparator<ProteinGroupSpectrumCount>() {
				@Override
				public int compare(ProteinGroupSpectrumCount o1, ProteinGroupSpectrumCount o2) {
					int val = Integer.valueOf(o1.prophetGrpSpectrumCount).compareTo(o2.prophetGrpSpectrumCount);
					if(val != 0)    return val;
					val = Integer.valueOf(o1.proteinProphetGroupId).compareTo(o2.proteinProphetGroupId);
					if(val != 0)	return val;
					val = Integer.valueOf(o1.spectrumCount).compareTo(o2.spectrumCount);
					if(val != 0)	return val;
					return Integer.valueOf(o1.proteinGroupId).compareTo(o2.proteinGroupId);
				}});
		}
		else {
			Collections.sort(list, new Comparator<ProteinGroupSpectrumCount>() {
				@Override
				public int compare(ProteinGroupSpectrumCount o1, ProteinGroupSpectrumCount o2) {
					int val = Integer.valueOf(o2.prophetGrpSpectrumCount).compareTo(o1.prophetGrpSpectrumCount);
					if(val != 0)    return val;
					val = Integer.valueOf(o2.proteinProphetGroupId).compareTo(o1.proteinProphetGroupId);
					if(val != 0)    return val;
					val = Integer.valueOf(o2.spectrumCount).compareTo(o1.spectrumCount);
					if(val != 0)    return val;
					return Integer.valueOf(o2.proteinGroupId).compareTo(o1.proteinGroupId);
				}});
		}
	}
	
	private void setProphetGrpSpectrumCount(List<ProteinGroupSpectrumCount> grpList, SORT_ORDER sortOrder) {

		if(grpList.size() == 0)
			return;

		int minCount = Integer.MAX_VALUE;
		int maxCount = 0;

		for(ProteinGroupSpectrumCount pgc: grpList) {
			minCount = pgc.spectrumCount < minCount ? pgc.spectrumCount : minCount;
			maxCount = pgc.spectrumCount > maxCount ? pgc.spectrumCount : maxCount;
		}

		// set the peptide count for the Prophet group
		int grpCount = sortOrder == SORT_ORDER.DESC ? maxCount : minCount;

		for(ProteinGroupSpectrumCount pgc: grpList)
			pgc.prophetGrpSpectrumCount = grpCount;
	}
	
	public static class ProteinGroupSpectrumCount {
		private int proteinId;
		private int proteinGroupId;
		private int proteinProphetGroupId;
		private int spectrumCount;
		private int prophetGrpSpectrumCount;
		
		public void setProteinId(int proteinId) {
			this.proteinId = proteinId;
		}
		public void setProteinGroupId(int proteinGroupId) {
			this.proteinGroupId = proteinGroupId;
		}
		public void setProteinProphetGroupId(int proteinProphetGroupId) {
			this.proteinProphetGroupId = proteinProphetGroupId;
		}
		public void setSpectrumCount(int spectrumCount) {
			this.spectrumCount = spectrumCount;
		}
		public int getProteinId() {
			return proteinId;
		}
	}
}
