/**
 * PhiliusSequenceHtmlFormatter.java
 * @author Vagisha Sharma
 * Feb 3, 2010
 * @version 1.0
 */
package org.yeastrc.www.philiusws;

import java.util.List;
import java.util.Set;

import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.philius.domain.PhiliusSegment;
import org.yeastrc.philius.domain.PhiliusSegmentType;

/**
 * 
 */
public class PhiliusSequenceHtmlFormatter {

	private static PhiliusSequenceHtmlFormatter instance = null;
	
	private PhiliusSequenceHtmlFormatter() {}
	
	public static PhiliusSequenceHtmlFormatter getInstance() {
		if(instance == null) {
			instance = new PhiliusSequenceHtmlFormatter();
		}
		return instance;
	}
	
	public String format(PhiliusResult psa, String sequence, Set<String> peptideSequences) throws Exception {

		if ( psa.getSegments() == null || psa.getSegments().size() < 1 )
			return null;
		
		int[] coveredResidues = new int[sequence.length()]; 
		
		for ( String pseq : peptideSequences ) {
            if (pseq == null) continue;

            int index = sequence.indexOf(pseq);
            if (index == -1) continue;                  //shouldn't happen
            if (index > coveredResidues.length - 1) continue;  //shouldn't happen

            int end = Math.min(index + pseq.length(), coveredResidues.length);
            for (int i = index; i < end; i++) {
            	coveredResidues[i] = 1;
            }
        }
		
		String retStr =	"      1          11         21         31         41         51         \n";
		retStr +=		"      |          |          |          |          |          |          \n";
		retStr +=		"    1 ";
		
		
		char[] residues = sequence.toCharArray();
		int counter = 0;
		
		String lastSpanTag = null;						//format of the last span tag
		List<org.yeastrc.philius.domain.PhiliusSegment> segments = psa.getSegments();
		
		for (int i = 0; i < residues.length; i++ ) {
			boolean isStart = false;
			boolean isEnd = false;
			
			counter++;
			
			// if this is the start or end of a segment, make sure to mark spans here
			for ( PhiliusSegment segment :segments ) {
				if ( segment.getStart() == counter ) {
					isStart = true;
					StringBuilder tooltip = new StringBuilder( "Segment Type: " + segment.getType().getLongName() + "<br>" );
					tooltip.append( "Type Confidence: " + segment.getConfidence() + "<br>" );
					tooltip.append( "Residues: " + segment.getStart() + " - " + segment.getEnd() );
					
					lastSpanTag = "<span " + getStyleForSegmentType(segment.getType()) + "\" onMouseOver=\"ddrivetip('" + tooltip.toString() + "')\" onMouseOut=\"hideddrivetip()\">";
				}
				if ( segment.getEnd() == counter ) isEnd = true;
			}
			
			if (isStart || i % 60 == 0)
				retStr += lastSpanTag;
			if(coveredResidues[i] == 1)
				retStr += "<span style=\"border-bottom:3px solid black;\"><B>";
			retStr += residues[i];
			if(coveredResidues[i] == 1)
				retStr += "</B></span>";
			
			if (isEnd)
				retStr += "</span>";
			
			if (counter % 60 == 0) {
				
				if (!isEnd)
					retStr += "</span>";
				
				if (counter < 1000) retStr += " ";
				if (counter< 100) retStr += " ";

				retStr += String.valueOf(counter);
				retStr += "\n\n ";					//add a </span> at the end of each line
				
				if (counter < 100) retStr += " ";
				if (counter < 1000) retStr += " ";
				retStr += String.valueOf(counter + 1) + " ";

			} else if (counter % 10 == 0) {
				if (!isEnd)
					retStr += "</span> " + lastSpanTag;
				else
					retStr += " ";
			}
		
		}
		
		return retStr;
	}
	
	public String getPhiliusLegend() {
		StringBuilder buf = new StringBuilder();
		buf.append("<b>Legend: </b><span "+getStyleForSegmentType(PhiliusSegmentType.TRANS_MEMBRANE_HELIX)+">Transmembrane Helix</span>, ");
		buf.append("<span "+getStyleForSegmentType(PhiliusSegmentType.NON_CYTOPLASMIC)+">Non-Cytoplasmic</span>, ");
		buf.append("<span "+getStyleForSegmentType(PhiliusSegmentType.CYTOPLASMIC)+">Cytoplasmic</span>, ");
		buf.append("<span "+getStyleForSegmentType(PhiliusSegmentType.SIGNAL_PEPTIDE)+">Signal Peptide</span>");
		return buf.toString();
	}
	
	private String getStyleForSegmentType(PhiliusSegmentType type) {
		switch (type) {
		case SIGNAL_PEPTIDE:
			return "class=\"philius_sp\"";
		case NON_CYTOPLASMIC:
			return "class=\"philius_nc\"";
		case CYTOPLASMIC:
			return "class=\"philius_c\"";
		case TRANS_MEMBRANE_HELIX:
			return "class=\"philius_tm\"";
		default:
			return "";
		}
	}
	
//	private String noSegmentsFormatted(PhiliusSequenceAnnotationWS psa) {
//		String retStr =	"      1          11         21         31         41         51         \n";
//		retStr +=		"      |          |          |          |          |          |          \n";
//		retStr +=		"    1 ";
//		
//		char[] residues = psa.getSequence().toCharArray();
//		int counter = 0;
//		
//		for (int i = 0; i < residues.length; i++ ) {
//			retStr += residues[i];
//			
//			counter++;
//			if (counter % 60 == 0) {
//				if (counter < 1000) retStr += " ";
//				if (counter< 100) retStr += " ";
//
//				retStr += String.valueOf(counter);
//				retStr += "\n ";
//
//				if (counter < 100) retStr += " ";
//				if (counter < 1000) retStr += " ";
//				retStr += String.valueOf(counter + 1) + " ";
//
//			} else if (counter % 10 == 0) {
//				retStr += " ";
//			}
//		}
//		return retStr;
//	}
}
