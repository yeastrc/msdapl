/**
 * ProteinGroupNSAFSoter.java
 * @author Vagisha Sharma
 * Mar 22, 2010
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer.idpicker.ibatis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.ProteinGroupCoverageSorter.ProteinGroupCoverage;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;

/**
 * 
 */
public class ProteinGroupNSAFSorter {

	private static ProteinGroupNSAFSorter instance;

	private ProteinGroupNSAFSorter () {}

	public static ProteinGroupNSAFSorter getInstance() {
		if(instance == null)
			instance = new ProteinGroupNSAFSorter();
		return instance;
	}

	/**
	 * list should already be sorted in the order -- groupId, nsaf (DESC)
	 * @param list
	 */
	void sort(List<ProteinGroupNsaf> list, SORT_ORDER sortOrder) {

		int lastGrp = -1;

		List<ProteinGroupNsaf> grpList = new ArrayList<ProteinGroupNsaf>();

		for(ProteinGroupNsaf pgc: list) {
			if(pgc.proteinGroupLabel != lastGrp) {

				setGrpNsaf(grpList, sortOrder);
				lastGrp = pgc.proteinGroupLabel;
				grpList.clear();
			}
			grpList.add(pgc);
		}

		// last one
		setGrpNsaf(grpList, sortOrder);

		if(sortOrder == SORT_ORDER.ASC) {
			Collections.sort(list, new Comparator<ProteinGroupNsaf>() {
				@Override
				public int compare(ProteinGroupNsaf o1, ProteinGroupNsaf o2) {
					int val = Double.valueOf(o1.grpNsaf).compareTo(o2.grpNsaf);
					if(val != 0)    return val;
					val = Integer.valueOf(o1.proteinGroupLabel).compareTo(o2.proteinGroupLabel);
					if(val != 0)    return val;
					return Double.valueOf(o1.nsaf).compareTo(o2.nsaf);
				}});
		}
		else {
			Collections.sort(list, new Comparator<ProteinGroupNsaf>() {
				@Override
				public int compare(ProteinGroupNsaf o1, ProteinGroupNsaf o2) {
					int val = Double.valueOf(o2.grpNsaf).compareTo(o1.grpNsaf);
					if(val != 0)    return val;
					val = Integer.valueOf(o1.proteinGroupLabel).compareTo(o2.proteinGroupLabel);
					if(val != 0)    return val;
					return Double.valueOf(o2.nsaf).compareTo(o1.nsaf);
				}});
		}
	}

	private void setGrpNsaf(List<ProteinGroupNsaf> grpList, SORT_ORDER sortOrder) {

		if(grpList.size() == 0)
			return;

		double grpNsaf = sortOrder == SORT_ORDER.DESC ? grpList.get(0).nsaf : 
			grpList.get(grpList.size() - 1).nsaf;

		for(ProteinGroupNsaf pgn: grpList)
			pgn.grpNsaf = grpNsaf;
	}
	
	public static class ProteinGroupNsaf {
        private int proteinId;
        private int proteinGroupLabel;
        private double nsaf;
        private double grpNsaf;
        public void setProteinId(int proteinId) {
            this.proteinId = proteinId;
        }
        public void setProteinGroupLabel(int proteinGroupLabel) {
            this.proteinGroupLabel = proteinGroupLabel;
        }
        public void setNsaf(double nsaf) {
            this.nsaf = nsaf;
        }
        public int getProteinId() {
        	return proteinId;
        }
    }
}
