/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamType;

/**
 * AnalysisSoftwareMaker.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class AnalysisSoftwareMaker {

	
	public AnalysisSoftwareType makeSequestAnalysisSoftware(String version) {
		
		/*
		  
		 From Example file: 
		 
		 <AnalysisSoftware id="AnaSoft_Sequest" name="ThermoFisher TurboSequest" version="PVM Master v.27 (rev. 12), (c) 1998-2007" uri="http://www.thermo.com/com/cda/product/detail/1,,16483,00.html">
    		<ContactRole contact_ref="THERMOCORP">
      			<Role>
        			<cvParam accession="MS:1001267" name="software vendor" cvRef="PSI-MS"/>
      			</Role>
    		</ContactRole>
    		<SoftwareName>
      			<cvParam accession="MS:1001208" name="Sequest" cvRef="PSI-MS"/>
    		</SoftwareName>
  		</AnalysisSoftware>
 
		 */
		AnalysisSoftwareType software = new AnalysisSoftwareType();
		software.setName(MzidConstants.SEQUEST_SW_ID);
		software.setId(MzidConstants.SEQUEST_SW_ID);
		software.setVersion(version);
		
		CvParamMaker cvparamMaker = CvParamMaker.getInstance();
		/*
		 	[Term]
			id: MS:1001208
			name: Sequest
			is_a: MS:1001456 ! analysis software
		 */
		CVParamType cvParam = cvparamMaker.make("MS:1001208", "Sequest", CvConstants.PSI_CV);
		ParamType paramType = new ParamType();
		paramType.setCvParam(cvParam);
		
		software.setSoftwareName(paramType);
		
		return software;
	}
	
	public AnalysisSoftwareType makePercolatorAnalysisSoftware(String version) {
		
		AnalysisSoftwareType software = new AnalysisSoftwareType();
		software.setName(MzidConstants.PERCOLATOR_SW_ID);
		software.setId(MzidConstants.PERCOLATOR_SW_ID);
		software.setVersion(version);
		
		CvParamMaker cvparamMaker = CvParamMaker.getInstance();
		/*
	 	[Term]
		id: MS:1001490
		name: Percolator
		def: "Percolator." [PSI:PI]
		is_a: MS:1001456 ! analysis software

		 */
		CVParamType cvParam = cvparamMaker.make("MS:1001490", "Percolator", CvConstants.PSI_CV);
		ParamType paramType = new ParamType();
		paramType.setCvParam(cvParam);
		
		software.setSoftwareName(paramType);
		
		return software;
	}
	
	public AnalysisSoftwareType makePeptideProphetAnalysisSoftware(String version) {
		
		AnalysisSoftwareType software = new AnalysisSoftwareType();
		software.setName(MzidConstants.PEPTIDE_PROPHET_SW_ID);
		software.setId(MzidConstants.PEPTIDE_PROPHET_SW_ID);
		software.setVersion(version);
		
		CvParamMaker cvparamMaker = CvParamMaker.getInstance();
		/*
	 	id: MS:1001456
		name: analysis software
		def: "Analysis software." [PSI:MS]
		is_a: MS:1000531 ! software
		 */
		CVParamType cvParam = cvparamMaker.make("MS:1001456", "analysis software", "PeptideProphet", CvConstants.PSI_CV);
		ParamType paramType = new ParamType();
		paramType.setCvParam(cvParam);
		
		software.setSoftwareName(paramType);
		
		return software;
	}
	
	public AnalysisSoftwareType makeProteinProphetAnalysisSoftware(String version) {
		
		AnalysisSoftwareType software = new AnalysisSoftwareType();
		software.setName(MzidConstants.PROTEIN_PROPHET_SW_ID);
		software.setId(MzidConstants.PROTEIN_PROPHET_SW_ID);
		software.setVersion(version);
		
		CvParamMaker cvparamMaker = CvParamMaker.getInstance();
		/*
	 	id: MS:1001456
		name: analysis software
		def: "Analysis software." [PSI:MS]
		is_a: MS:1000531 ! software
		 */
		CVParamType cvParam = cvparamMaker.make("MS:1001456", "analysis software", "ProteinProphet", CvConstants.PSI_CV);
		ParamType paramType = new ParamType();
		paramType.setCvParam(cvParam);
		
		software.setSoftwareName(paramType);
		
		return software;
	}
	
}
