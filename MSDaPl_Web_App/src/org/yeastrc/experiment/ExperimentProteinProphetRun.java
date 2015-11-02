/**
 * ExperimentProteinInferRun.java
 * @author Vagisha Sharma
 * Aug 5, 2009
 * @version 1.0
 */
package org.yeastrc.experiment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;

/**
 * 
 */
public class ExperimentProteinProphetRun {

    private final ProteinProphetRun run;
    private int uniqPeptideSequenceCount;
    private int uniqIonCount;
    private int numParsimoniousProteins;
    private int numParsimoniousProteinGroups;
    private int numParsimoniousProteinProphetGroups;
    private boolean isBookmarked = false;
    
    private static final Pattern tppVersionPattern = Pattern.compile("TPP\\s+(v\\d+\\.\\d+)");
    
    public boolean getIsBookmarked() {
		return isBookmarked;
	}

	public void setBookmarked(boolean isBookmarked) {
		this.isBookmarked = isBookmarked;
	}
	
    public int getUniqIonCount() {
		return uniqIonCount;
	}

	public void setUniqIonCount(int uniqueIonCount) {
		this.uniqIonCount = uniqueIonCount;
	}

	public int getNumParsimoniousProteinProphetGroups() {
		return numParsimoniousProteinProphetGroups;
	}

	public void setNumParsimoniousProteinProphetGroups(int numProteinProphetGroups) {
		this.numParsimoniousProteinProphetGroups = numProteinProphetGroups;
	}

    public ExperimentProteinProphetRun(ProteinProphetRun run) {
        this.run = run;
    }
    
    public ProteinProphetRun getProteinProphetRun() {
        return run;
    }
    
    public int getUniqPeptideSequenceCount() {
        return uniqPeptideSequenceCount;
    }

    public void setUniqPeptideSequenceCount(int uniqPeptideSequenceCount) {
        this.uniqPeptideSequenceCount = uniqPeptideSequenceCount;
    }

    public int getNumParsimoniousProteins() {
        return numParsimoniousProteins;
    }

    public void setNumParsimoniousProteins(int numParsimoniousProteins) {
        this.numParsimoniousProteins = numParsimoniousProteins;
    }

    public int getNumParsimoniousProteinGroups() {
        return numParsimoniousProteinGroups;
    }

    public void setNumParsimoniousProteinGroups(int numParsimoniousProteinGroups) {
        this.numParsimoniousProteinGroups = numParsimoniousProteinGroups;
    }
    
    public String getProgramVersionShort() {
    	String version = run.getProgramVersion();
    	if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {
    		Matcher m = tppVersionPattern.matcher(version);
    		if(m.find()) {
    			version = m.group(1);
    		}
    	}
    	return version;
    }
    
}
