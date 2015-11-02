/**
 * ProteinGroupCoverageSorter.java
 * @author Vagisha Sharma
 * Mar 22, 2010
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
	 * list should already be sorted in the order -- groupId, coverage (DESC)
	 * @param list
	 */
	void sort(List<ProteinGroupCoverage> list, SORT_ORDER sortOrder) {

		int lastGrp = -1;

		List<ProteinGroupCoverage> grpList = new ArrayList<ProteinGroupCoverage>();

		for(ProteinGroupCoverage pgc: list) {
			if(pgc.proteinGroupLabel != lastGrp) {

				setGrpCoverage(grpList, sortOrder);
				lastGrp = pgc.proteinGroupLabel;
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
					val = Integer.valueOf(o1.proteinGroupLabel).compareTo(o2.proteinGroupLabel);
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
					val = Integer.valueOf(o1.proteinGroupLabel).compareTo(o2.proteinGroupLabel);
					if(val != 0)    return val;
					return Double.valueOf(o2.coverage).compareTo(o1.coverage);
				}});
		}
	}

	private void setGrpCoverage(List<ProteinGroupCoverage> grpList, SORT_ORDER sortOrder) {

		if(grpList.size() == 0)
			return;

		double grpCoverage = sortOrder == SORT_ORDER.DESC ? grpList.get(0).coverage : 
			grpList.get(grpList.size() - 1).coverage;

		for(ProteinGroupCoverage pgc: grpList)
			pgc.grpCoverage = grpCoverage;
	}

	public static class ProteinGroupCoverage {
		private int proteinId;
		private int proteinGroupLabel;
		private double coverage;
		private double grpCoverage;

		public void setProteinId(int proteinId) {
			this.proteinId = proteinId;
		}
		public void setProteinGroupLabel(int proteinGroupLabel) {
			this.proteinGroupLabel = proteinGroupLabel;
		}
		public void setCoverage(double coverage) {
			this.coverage = coverage;
		}
		public int getProteinId() {
			return proteinId;
		}
	}
}
