/**
 * GOProteinFilterCriteria.java
 * @author Vagisha Sharma
 * Jul 9, 2010
 */
package org.yeastrc.ms.domain.protinfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class GOProteinFilterCriteria {

	private List<String> goAccessions = null;
    private boolean matchAllGoTerms = false;
    private boolean exactAnnotation = false;
    private List<String> excludeEvidenceCodes;
    
	public List<String> getGoAccessions() {
		return goAccessions;
	}
	public void setGoAccessions(List<String> goAccessions) {
		this.goAccessions = goAccessions;
	}
	
	public boolean isMatchAllGoTerms() {
		return matchAllGoTerms;
	}
	public void setMatchAllGoTerms(boolean matchAllGoTerms) {
		this.matchAllGoTerms = matchAllGoTerms;
	}
	
	public boolean isExactAnnotation() {
		return exactAnnotation;
	}
	public void setExactAnnotation(boolean exactAnnotation) {
		this.exactAnnotation = exactAnnotation;
	}
	
	public List<String> getExcludeEvidenceCodes() {
		return excludeEvidenceCodes;
	}
	public void setExcludeEvidenceCodes(List<String> excludeEvidenceCodes) {
		this.excludeEvidenceCodes = excludeEvidenceCodes;
	}
	
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(!(o instanceof GOProteinFilterCriteria))
			return false;

		GOProteinFilterCriteria that = (GOProteinFilterCriteria) o;

		if(this.matchAllGoTerms != that.matchAllGoTerms)
			return false;
		if(this.exactAnnotation != that.exactAnnotation)
			return false;
     	
		if(this.goAccessions == null) {
			if(that.goAccessions != null)
				return false;
		}
		else {
			if(this.goAccessions.size() != that.goAccessions.size())
				return false;
			
			List<String> thisAcc = new ArrayList<String>(this.goAccessions);
			List<String> thatAcc = new ArrayList<String>(that.goAccessions);
			Collections.sort(thisAcc);
			Collections.sort(thatAcc);
			for(int i = 0; i < thisAcc.size(); i++) {
				if(!(thisAcc.get(i).equalsIgnoreCase(thatAcc.get(i))))
					return false;
			}
		}
		
		if(this.excludeEvidenceCodes == null) {
			if(that.excludeEvidenceCodes != null)
				return false;
		}
		else {
			if(this.excludeEvidenceCodes.size() != that.excludeEvidenceCodes.size())
				return false;
			
			List<String> thisCodes = new ArrayList<String>(this.excludeEvidenceCodes);
			List<String> thatCodes = new ArrayList<String>(that.excludeEvidenceCodes);
			Collections.sort(thisCodes);
			Collections.sort(thatCodes);
			for(int i = 0; i < thisCodes.size(); i++) {
				if(!(thisCodes.get(i).equalsIgnoreCase(thatCodes.get(i))))
					return false;
			}
		}
		return true;
	}
	
	public String toString() {
		
		StringBuilder buf = new StringBuilder();
		buf.append("GO Filters: ");
		
		if(goAccessions == null || goAccessions.size() == 0)
			buf.append("NO ACCESSIONS FOUND! ");
		else {
			buf.append("Accessions: ");
			for(String acc: this.goAccessions) {
				buf.append(acc+",");
			}
			buf.deleteCharAt(buf.length() - 1);
		}
		buf.append("; ");
		
		if(this.isExactAnnotation()) 
			buf.append("EXACT; ");
		if(this.isMatchAllGoTerms())
			buf.append("MATCH ALL; ");
		
		if(this.excludeEvidenceCodes != null && this.excludeEvidenceCodes.size() > 0) {
			buf.append("EXCLUDE: ");
			for(String code: this.excludeEvidenceCodes) {
				buf.append(code+",");
			}
			buf.deleteCharAt(buf.length() - 1);
		}
		
		return buf.toString();
	}
}
