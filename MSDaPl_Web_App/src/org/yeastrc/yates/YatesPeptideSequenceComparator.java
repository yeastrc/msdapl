/*
 * YatesPeptideSequenceComparator.java
 * Created on Oct 19, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.yates;

import java.util.Comparator;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Oct 19, 2004
 */

public class YatesPeptideSequenceComparator implements Comparator {

	private YatesResult result;
	
	/**
	 * Set the Result object we're using as the context of the comparison, as
	 * comparing two ms peptide sequences in this object are done within the
	 * context of a single ms result (protein)
	 * @param res
	 */
	public void setResult(YatesResult res) {
		this.result = res;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		if (this.result == null)
			return 0;
		
		YatesPeptide yp1 = (YatesPeptide)arg0;
		YatesPeptide yp2 = (YatesPeptide)arg1;
		
		// Get the sequences of the two peptides
		String seq1 = null;
		String seq2 = null;		
		try {
			seq1 = yp1.getPeptide().getSequenceString();
			seq2 = yp2.getPeptide().getSequenceString();
		} catch (Exception e) {

			// Problem getting sequence, say they're equal
			return 0;
		}
		
		// clean
		yp1 = null;
		yp2 = null;
		
		// When comparing anything to null, say they're the same
		if (seq1 == null || seq2 == null)
			return 0;

		// If both sequences are the same, say they are equal
		if (seq1.equals(seq2))
			return 0;
		
		// Get our result protein sequence, if we can't say they're equal
		String resSeq = null;
		try {
			resSeq = this.result.getHitProtein().getPeptide().getSequenceString();
		} catch (Exception e) {
			return 0;
		}
		if (resSeq == null)
			return 0;
		
		// Get the index of the two sequences in the protein sequence
		int idx1 = resSeq.indexOf(seq1);
		int idx2 = resSeq.indexOf(seq2);
		
		int length1 = seq1.length();
		int length2 = seq2.length();

		// clean
		resSeq = null;
		seq1 = null;
		seq2 = null;
		
		// Say peptide sequences that appear first in the protein sequence are less than ones that appear later
		if (idx1 < idx2)
			return -1;
		
		if (idx1 > idx2)
			return 1;
		
		// If we're here, the indexes were the same.  Say the shorter one is less than the longer one
		if (length1 < length2)
			return -1;
		
		if (length1 > length2)
			return 1;
		
		// Should never get to here, but if we do, say they're equal
		return 0;
	}
}
