/*
 * PhiliusPanel.java
 * Michael Riffle <mriffle@u.washington.edu>
 * May 5, 2008
 */
package org.yeastrc.www.philiusws;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Set;

import javax.swing.JPanel;

import org.yeastrc.philius.domain.PhiliusSegment;


/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @date May 5, 2008
 * Description of class here.
 */
public class PhiliusPanel extends JPanel {

	private static final Color BACKGROUND_COLOR =  new Color( 248, 248, 255 );
	private static final Dimension PREFERRED_SIZE = new Dimension ( 810, 130 );
	public static final int INDENT = 20;
	
	// prevent direct instantiaion of PhiliusPanel
	private PhiliusPanel() {
		this.setPreferredSize( PhiliusPanel.PREFERRED_SIZE);
		this.setBackground( PhiliusPanel.BACKGROUND_COLOR );
	}

	/**
	 * get an instance of PhiliusPanel
	 * @return an instance of PhiliusPanel
	 */
	public static PhiliusPanel getInstance( PhiliusResultPlus anno ) {
		PhiliusPanel panel = new PhiliusPanel();
		panel.setSequenceAnnotation( anno );
		return panel;
	}
	
	
	/**
	 * Paint the graphical representation of this Philius sequence annotation
	 */
	public void paintComponent( Graphics g1 ) {
		Graphics2D g2 = (Graphics2D)g1;
		boolean hasSP = false;
		super.paintComponent( g2 );
		
		// get the size of the image
		
		int length = this.sequenceAnnotation.getSequence().length();
		//System.out.println( "Sequence length: " + length );
		float normalizer = (float)1.0;
		if (length > 760) {
			normalizer = (float)760.0 / (float)length;
			length = 760;
		}
		
		//this.setSize( new Dimension ( length, 100 ) );
		
		

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		        RenderingHints.VALUE_ANTIALIAS_ON);
		
		//g2.setColor( new Color( 0, 0, 0 ) );
		//g2.setStroke(new BasicStroke(4));
		//g2.drawLine( 0, 48, length, 48 );
		
		// loop through the segments and draw them
		try {
			
			// draw lines first
			for ( PhiliusSegment segment : this.sequenceAnnotation.getResult().getSegments() ) {
				
				int intensity = Math.round( (float)255.0 * (float)Math.pow( segment.getConfidence().floatValue() , 3.0 ) );
				
				if (segment.isTransMembraneHelix() || segment.isSp()) {
					continue;
				} else {
					
					int y_loc = 28;
					if ( segment.isNonCytoplasmic())
						g2.setColor( new Color ( 0, intensity, 0) );
					else {
						g2.setColor( new Color ( 0, 0, intensity) );
						y_loc = 48;
					}
					
					g2.setStroke(new BasicStroke(1));
					g2.fillRect( INDENT + Math.round( segment.getStart() * normalizer ) , y_loc,
							Math.round( segment.getEnd() * normalizer ) - Math.round( segment.getStart() * normalizer ), 20 );
					g2.drawRect( INDENT + Math.round( segment.getStart() * normalizer ) , y_loc,
							Math.round( segment.getEnd() * normalizer ) - Math.round( segment.getStart() * normalizer ), 20 );
				}
			
				
			}
			
			for ( PhiliusSegment segment : this.sequenceAnnotation.getResult().getSegments() ) {
				if (!segment.isTransMembraneHelix()  && !segment.isSp())
					continue;
				
				//System.out.println( Math.round( segment.getStart() * normalizer ) + "\t" + 28 + "\t" + Math.round( (segment.getEnd() - segment.getStart()) * normalizer ) + "\t" + 40 + "\ttrue" );
				g2.setColor( Color.BLACK );
				Rectangle r = new Rectangle( INDENT + Math.round( segment.getStart() * normalizer ), 28, Math.round( (segment.getEnd() - segment.getStart()) * normalizer ), 40 );
			    GradientPaint gp = null;
			    
			    int intensity = Math.round( (float)255.0 * (float)Math.pow( segment.getConfidence().floatValue() , 3.0) );
			    
			    if (segment.isTransMembraneHelix()) {
			    	gp = new GradientPaint( INDENT + Math.round( segment.getStart() * normalizer ), 28, new Color ( intensity, intensity, 0),
			    						    INDENT + Math.round( segment.getEnd() * normalizer ) - Math.round( 8.0 * normalizer), 28, new Color( 255, 255, 255),
			    						   true);
			    } else {
			    	hasSP = true;
			    	gp = new GradientPaint( INDENT + Math.round( segment.getStart() * normalizer ), 28, new Color ( intensity, 0, 0),
 						   				    INDENT + Math.round( segment.getEnd() * normalizer ) - Math.round( 8.0 * normalizer), 28, new Color( 255, 255, 255),
 						   				   true);			    	
			    }
			    // Fill with a gradient.
			    g2.setPaint(gp);
			    g2.fill(r);
			    g2.draw( r );
			    g2.setPaint( null );
				
			}
			
			// draw position markers
			boolean drawUp = false;
			for ( PhiliusSegment segment : this.sequenceAnnotation.getResult().getSegments() ) {

				Font f = new Font("Times", Font.PLAIN, 9);
				g2.setFont( f );
				
				if ( segment.getStart() == 1 ) {
					g2.setStroke(new BasicStroke(1));
					g2.setColor( Color.black );
					
					g2.drawLine( INDENT + Math.round( segment.getStart() * normalizer ), 58, INDENT + Math.round( segment.getStart() * normalizer ), 18 );
					g2.drawString( "1", INDENT + Math.round( segment.getStart() * normalizer ) - 3, 16 );
					
				}
				
				
				if ( drawUp ) {
					g2.drawLine( INDENT + Math.round( segment.getEnd() * normalizer ), 68, INDENT + Math.round( segment.getEnd() * normalizer ), 18 );
					g2.drawString( String.valueOf( segment.getEnd() ), INDENT + Math.round( segment.getEnd() * normalizer ) - 3, 16 );
					drawUp = false;
				} else {
					g2.drawLine( INDENT + Math.round( segment.getEnd() * normalizer ), 28, INDENT + Math.round( segment.getEnd() * normalizer ), 78 );
					g2.drawString( String.valueOf( segment.getEnd() ), INDENT + Math.round( segment.getEnd() * normalizer ) - 3, 90 );
					drawUp = true;
				}
				
				
			}
			
			// Draw the sequence coverage if we have the information
			if(this.sequenceAnnotation.getCoveredSequences() != null)
				drawSequenceCoverage(g2, normalizer);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}

	}

	private void drawSequenceCoverage(Graphics2D g2, float normalizer) {
		
		// draw the main sequence first
		for ( PhiliusSegment segment : this.sequenceAnnotation.getResult().getSegments() ) {

			Font f = new Font("Times", Font.PLAIN, 9);
			g2.setFont( f );
			
			g2.setStroke(new BasicStroke(1));
			if (segment.isSp())
				g2.setColor( Color.red );
			else if(segment.isTransMembraneHelix())
				g2.setColor(Color.YELLOW);
			else if(segment.isCytoplasmic())
				g2.setColor(Color.blue);
			else if(segment.isNonCytoplasmic())
				g2.setColor(Color.green);
			
			int start = INDENT + Math.round( segment.getStart() * normalizer );
			int end = INDENT + Math.round( segment.getEnd() * normalizer );
			int y = 98;
			int height = 5;
			int width = end - start + 1;
			g2.fillRect(start, y, width, height);
		}
		
		// draw the peptide coverage
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(3));
		Set<String> coveredSequences = this.sequenceAnnotation.getCoveredSequences();
		for(String peptide: coveredSequences) {
			int start = this.sequenceAnnotation.getSequence().indexOf(peptide);
			if(start > -1) {
				int end = start + peptide.length();
				System.out.println("start "+start+"; end "+end);
				g2.drawLine( INDENT + Math.round(start * normalizer ), 108, INDENT +  Math.round(end * normalizer ), 108 );
			}
		}
	}
	
	private PhiliusResultPlus sequenceAnnotation;
	
	
	/**
	 * @return the sequenceAnnotation
	 */
	public PhiliusResultPlus getSequenceAnnotation() {
		return sequenceAnnotation;
	}

	/**
	 * @param sequenceAnnotation the sequenceAnnotation to set
	 */
	public void setSequenceAnnotation(PhiliusResultPlus sequenceAnnotation) {
		this.sequenceAnnotation = sequenceAnnotation;
	}
	
}
