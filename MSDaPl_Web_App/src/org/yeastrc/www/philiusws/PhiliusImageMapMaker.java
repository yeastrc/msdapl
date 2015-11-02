/*
 * PhiliusImageMapMaker.java
 * Michael Riffle <mriffle@u.washington.edu>
 * May 9, 2008
 */
package org.yeastrc.www.philiusws;

import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.philius.domain.PhiliusSegment;



/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @date May 9, 2008
 * Description of class here.
 */
public class PhiliusImageMapMaker {

	// prevent direct instantiaion of PhiliusImageMapMaker
	private PhiliusImageMapMaker() {
	}

	/**
	 * get an instance of PhiliusImageMapMaker
	 * @return an instance of PhiliusImageMapMaker
	 */
	public static PhiliusImageMapMaker getInstance() {
		return new PhiliusImageMapMaker();
	}
	
	/**
	 * Create the HTML for a client side image map that maps exactly onto the images created in the PhiliusPanel
	 * @param anno
	 * @return
	 * @throws Exception
	 */
	public String getImageMap( PhiliusResult anno, String sequence ) throws Exception {
		StringBuffer mapstr = new StringBuffer( "<map name=\"philiusMap\">\n" );
		
		// loop through the segments
		for ( PhiliusSegment segment : anno.getSegments() ) {
			int x1, y1, x2, y2 = 0;
			
			mapstr.append( "<area shape=\"rect\" nohref ");
			
			// get normalizer
			int length = sequence.length();
			float normalizer = (float)1.0;
			if (length > 760) {
				normalizer = (float)760.0 / (float)length;
				length = 760;
			}
			
			x1 = PhiliusPanel.INDENT + Math.round( (float)segment.getStart() * normalizer );
			x2 = PhiliusPanel.INDENT + Math.round( (float)segment.getEnd() * normalizer );
			// set rectangle coordinates
			if (segment.isSp() || segment.isTransMembraneHelix()) {
				y1 = 28;
				y2 = 68;
			} else if( segment.isCytoplasmic()) {
				y1 = 48;
				y2 = 68;
			} else {
				y1 = 28;
				y2 = 48;
			}
			
			// to coords to html
			mapstr.append( "coords=\"" + x1 + "," + y1 + "," + x2 + "," + y2 + "\" ");
			
			// add in call to tool tip javascript here
			StringBuffer tooltip = new StringBuffer( "Segment Type: " + segment.getType().getLongName() + "<br>" );
			tooltip.append( "Type Confidence: " + segment.getConfidence() + "<br>" );
			tooltip.append( "Residues: " + segment.getStart() + " - " + segment.getEnd() );
			mapstr.append( "onMouseOver=\"ddrivetip('" + tooltip.toString() + "')\" onMouseOut=\"hideddrivetip()\"" );
			
			//end the area tag
			mapstr.append( ">\n" );
		}
		
		
		mapstr.append( "</map>\n" );
		return mapstr.toString();
	}
	
}
