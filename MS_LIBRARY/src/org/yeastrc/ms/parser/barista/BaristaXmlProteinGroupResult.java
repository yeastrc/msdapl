/**
 * 
 */
package org.yeastrc.ms.parser.barista;

import java.util.HashSet;
import java.util.Set;

/**
 * BaristaXmlProteinGroupResult.java
 * @author Vagisha Sharma
 * Jul 25, 2011
 * 
 */
public class BaristaXmlProteinGroupResult {
	
	private int baristaGroupId = -1;
	private Double score = null;
    private double qvalue = -1.0;
    
    private Set<String> proteinsInGroup;
    private Set<String> peptides;
    
    public BaristaXmlProteinGroupResult() {
    	proteinsInGroup = new HashSet<String>();
    	peptides = new HashSet<String>();
    }
    
    public boolean isComplete() {
    	
    	return (qvalue != -1.0 &&
    			baristaGroupId != -1 &&
				score != null &&
				proteinsInGroup.size() > 0 &&
				peptides.size() > 0);
	}
    
    public String toString() {
    	
		StringBuilder buf = new StringBuilder();
		
		buf.append("BaristaGroupId: "+baristaGroupId);
		buf.append("\n");
		buf.append("qvalue: "+qvalue);
		buf.append("\n");
		buf.append("score: "+score);
		buf.append("\n");
		if(proteinsInGroup.size() == 0)
			buf.append("NO PROTEINS IN GROUP!\n");
		else {
			buf.append("Proteins:\n");
			for(String locus: proteinsInGroup) {
				buf.append(locus+"\n");
			}
		}
		if(peptides.size() == 0)
			buf.append("NO Peptides!\n");
		else {
			buf.append("Peptides:\n");
			for(String peptide: peptides) {
				buf.append(peptide+"\n");
			}
		}
		
		buf.append("\n");
		
		return buf.toString();
	}

	public int getBaristaGroupId() {
		return baristaGroupId;
	}

	public void setBaristaGroupId(int baristaGroupId) {
		this.baristaGroupId = baristaGroupId;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public double getQvalue() {
		return qvalue;
	}

	public void setQvalue(double qvalue) {
		this.qvalue = qvalue;
	}

	public Set<String> getProteinsInGroup() {
		return proteinsInGroup;
	}

	public void addProtein(String protein) {
		this.proteinsInGroup.add(protein);
	}

	public Set<String> getPeptides() {
		return peptides;
	}

	public void addPeptide(String peptide) {
		this.peptides.add(peptide);
	}
}
