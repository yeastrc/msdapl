/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

/**
 * MzidConstants.java
 * @author Vagisha Sharma
 * Aug 16, 2011
 * 
 */
public class MzidConstants {

	private MzidConstants() {}
	
	public static final String SEQUEST_PROTOCOL_ID = "sequest_protocol";
	public static final String PERCOLATOR_PROTOCOL_ID = "percolator_protocol";
	public static final String PEPTIDE_PROPHET_PROTOCOL_ID = "peptideprophet_protocol_id";
	
	public static String SEQUEST_SW_ID = "SEQUEST";
	public static String PERCOLATOR_SW_ID = "Percolator";
	public static String PEPTIDE_PROPHET_SW_ID = "PeptideProphet";
	public static String PROTEIN_PROPHET_SW_ID = "ProteinProphet";
	
	
	public static final String SPEC_ID_LIST_ID = "Analysis_Results";
	
	public static final String SPEC_IDENT_ID_PERC = "Percolator_Analysis";
	public static final String SPEC_IDENT_ID_SEQ = "Sequest_Analysis";
	public static final String SPEC_IDENT_ID_PROPHET = "PeptideProphet_Analysis";
	
}
