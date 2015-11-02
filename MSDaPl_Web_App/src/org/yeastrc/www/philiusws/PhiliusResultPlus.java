/**
 * PhiliusResult.java
 * @author Vagisha Sharma
 * Feb 8, 2010
 * @version 1.0
 */
package org.yeastrc.www.philiusws;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.yeastrc.philius.domain.PhiliusResult;

/**
 * 
 */
public class PhiliusResultPlus {

	private PhiliusResult result;
	private String sequence;
	private Set<String> coveredSequences;
	
	
	public PhiliusResult getResult() {
		return result;
	}

	public void setResult(PhiliusResult result) {
		this.result = result;
	}
	
	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Set<String> getCoveredSequences() {
		return coveredSequences;
	}

	public void setCoveredSequences(Set<String> coveredSequences) {
		this.coveredSequences = coveredSequences;
	}

}
