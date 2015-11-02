package org.yeastrc.grant;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class GrantSorter {

	private static GrantSorter instance = new GrantSorter();
	
	private GrantSorter() {}
	
	public static GrantSorter getInstance() {
		return instance;
	}
	
	public void sortByTitle (List<Grant> grants) {
		Collections.sort(grants, new GrantSorter.GrantTitleComparator());
	}
	
	public void sortByPI (List<Grant> grants) {
		Collections.sort(grants, new GrantSorter.GrantPIComparator());
	}
	
	public void sortBySourceType(List<Grant> grants) {
		Collections.sort(grants, new GrantSorter.GrantSourceTypeComparator());
	}
	
	public void sortBySourceName(List<Grant> grants) {
		Collections.sort(grants, new GrantSorter.GrantSourceNameComparator());
	}
	
	public void sortByGrantNumber(List<Grant> grants) {
		Collections.sort(grants, new GrantSorter.GrantNumberComparator());
	}
	
	public void sortByGrantAmount(List<Grant> grants) {
		Collections.sort(grants, new GrantSorter.GrantAmountComparator());
	}
	
	private static class GrantTitleComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getTitle().length() == 0)
				return 1;
			if (g2 == null || g2.getTitle().length() == 0)
				return -1;
			return g1.getTitle().compareTo(g2.getTitle());
		}
	}
	
	private static class GrantPIComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getGrantPI().getLastName().length() == 0)
				return 1;
			if (g2 == null || g2.getGrantPI().getLastName().length() == 0)
				return -1;
			return g1.getGrantPI().getLastName().compareTo(g2.getGrantPI().getLastName());
		}
	}
	
	private static class GrantSourceTypeComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null)
				return 1;
			if (g2 == null)
				return -1;
			FundingSourceType type1 = g1.getFundingSource().getSourceType();
			FundingSourceType type2 = g2.getFundingSource().getSourceType();
			return type1.getDisplayName().compareTo(type2.getDisplayName());
		}
	}
	
	private static class GrantSourceNameComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null)
				return 1;
			if (g2 == null)
				return -1;
			FundingSourceName source1 = g1.getFundingSource().getSourceName();
			FundingSourceName source2 = g2.getFundingSource().getSourceName();
			if (source1.getDisplayName().length() == 0)
				return 1;
			if (source2.getDisplayName().length() == 0)
				return -1;
			return source1.getDisplayName().compareTo(source2.getDisplayName());
		}
	}
	
	private static class GrantNumberComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getGrantNumber().length() == 0)
				return 1;
			if (g2 == null || g2.getGrantNumber().length() == 0)
				return -1;
			return g1.getGrantNumber().compareTo(g2.getGrantNumber());
		}
	}
	
	private static class GrantAmountComparator implements Comparator<Grant> {

		@Override
		public int compare(Grant g1, Grant g2) {
			if (g1 == null && g2 == null)
				return 0;
			if (g1 == null || g1.getGrantAmount().length() == 0)
				return 1;
			if (g2 == null || g2.getGrantAmount().length() == 0)
				return -1;
			return g1.getGrantAmount().compareTo(g2.getGrantAmount());
		}
	}
}
