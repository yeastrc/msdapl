/**
 * 
 */
package org.yeastrc.ms.parser.barista;

import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.DataProviderException;

/**
 * BaristaXmlFileReaderApp.java
 * @author Vagisha Sharma
 * Jul 25, 2011
 * 
 */
public class BaristaXmlFileReaderApp {

	private boolean readPsms = false;
	private double psmQvalCutoff = 1.0;
	
	private boolean readPeptides = false;
	private double peptideQvalCutoff = 1.0;
	
	private boolean readProteinGroups = false;
	private double proteinGrpQvalCutoff = 1.0;
	
	public String filePath;
	
	
	public void setReadPsms(boolean readPsms) {
		this.readPsms = readPsms;
	}
	public void setPsmQvalCutoff(double psmQvalCutoff) {
		this.psmQvalCutoff = psmQvalCutoff;
	}
	public void setReadPeptides(boolean readPeptides) {
		this.readPeptides = readPeptides;
	}
	public void setPeptideQvalCutoff(double peptideQvalCutoff) {
		this.peptideQvalCutoff = peptideQvalCutoff;
	}
	public void setReadProteinGroups(boolean readProteinGroups) {
		this.readProteinGroups = readProteinGroups;
	}
	public void setProteinGrpQvalCutoff(double proteinGrpQvalCutoff) {
		this.proteinGrpQvalCutoff = proteinGrpQvalCutoff;
	}
	
	public void read(String filePath) {
		
		this.filePath = filePath;
		
		BaristaXmlFileReader reader = new BaristaXmlFileReader();
		reader.setSearchProgram(Program.SEQUEST);
		
		int proteinGrpsAboveThreshold = 0;
		int peptidesAboveThreshold = 0;
		int psmsAboveThreshold = 0;
		
		
		try {
			reader.open(filePath);
			
			if(this.readProteinGroups) {
				
				while(reader.hasNextProteinGroup()) {
					
					BaristaXmlProteinGroupResult proteinGroup = reader.getNextProteinGroup();
					
					if(proteinGroup.getQvalue() <= this.proteinGrpQvalCutoff) {
						proteinGrpsAboveThreshold++;
					}
				}
			}
			
			if(this.readPeptides) {
				
				while(reader.hasNextPeptide()) {
					
					BaristaXmlPeptideResult peptide = reader.getNextPeptide();
					
					if(peptide.getQvalue() <= this.peptideQvalCutoff) {
						peptidesAboveThreshold++;
					}
				}
			}
			
			if(this.readPsms) {
				
				while(reader.hasNextPsm()) {
					
					BaristaXmlPsmResult psm = reader.getNextPsm();
					
					if(psm.getQvalue() <= this.psmQvalCutoff) {
						psmsAboveThreshold++;
					}
				}
			}
		}
		catch(DataProviderException e) {
			
			System.out.println("There was an error reading the file "+filePath);
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			reader.close();
		}
		
		System.out.println("Number of PSMs with qvalue <= "+this.psmQvalCutoff+" "+psmsAboveThreshold);
		System.out.println("Number of peptides with qvalue <= "+this.peptideQvalCutoff+" "+peptidesAboveThreshold);
		System.out.println("Number of protein groups with qvalue <= "+this.proteinGrpQvalCutoff+" "+proteinGrpsAboveThreshold);
		
	}
	
	public static void main(String[] args) {
		
		// sed 's/p:group_id=\([0-9]*\)\>/group_id="\1"\>/g' barista_output.xml > barista_output.mine.xml
		// sed 's/p:peptide_id=/peptide_id=/g' barista_output.mine.2.xml > barista_output.mine.3.xml
		// sed 's/p:psm_id=\([0-9]*\)\>/psm_id="\1"\>/g' barista_output.mine.3.xml > barista_output.mine.4.xml
		
		String filePath = "/Users/vagisha/WORK/MSDaPl_data/barista_test/barista_output.mine.4.xml";
		
		boolean readPsms = true;
		double psmQvalCutoff = 0.01;
		
		boolean readPeptides = true;
		double peptideQvalCutoff = 0.01;
		
		boolean readProteinGroups = true;
		double proteinGrpQvalCutoff = 0.01;
		
		BaristaXmlFileReaderApp app = new BaristaXmlFileReaderApp();
		app.setReadPsms(readPsms);
		app.setPsmQvalCutoff(psmQvalCutoff);
		app.setReadPeptides(readPeptides);
		app.setPeptideQvalCutoff(peptideQvalCutoff);
		app.setReadProteinGroups(readProteinGroups);
		app.setProteinGrpQvalCutoff(proteinGrpQvalCutoff);
		
		app.read(filePath);
	}
	
}
