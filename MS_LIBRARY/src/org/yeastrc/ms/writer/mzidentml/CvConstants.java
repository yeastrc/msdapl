/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;

/**
 * CvConstants.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class CvConstants {

	public static final CvType PSI_CV = new Psi_Cv();
	public static final CvType UNIMOD_CV = new Unimod_Cv();
	public static final CvType UNIT_ONTOLOGY_CV = new UnitOntology_Cv();
	
	private static final class Psi_Cv extends CvType {
		
		// <cv id="PSI-MS" fullName="Proteomics Standards Initiative Mass Spectrometry Vocabularies" 
		//     uri="http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo" version="2.32.0"/>
		public Psi_Cv() {
			super.setUri("http://psidev.cvs.sourceforge.net/viewvc/*checkout*/psidev/psi/psi-ms/mzML/controlledVocabulary/psi-ms.obo");
			super.setId("PSI-MS");
			super.setVersion("2.32.0");
			super.setFullName("PSI-MS");
		}
	}
	
	
	private static final class Unimod_Cv extends CvType {
		
		// <cv id="UNIMOD" fullName="unimod modifications ontology" uri="http://www.unimod.org/obo/unimod.obo"/>
		public Unimod_Cv() {
			super.setUri("http://www.unimod.org/obo/unimod.obo");
			super.setId("UNIMOD");
			super.setFullName("UNIMOD");
		}
	}
	
	private static final class UnitOntology_Cv extends CvType {
		
		// <cv id="UO" fullName="Unit Ontology" uri="http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/unit.obo"/>
		public UnitOntology_Cv() {

			super.setUri("http://obo.cvs.sourceforge.net/*checkout*/obo/obo/ontology/phenotype/unit.obo");
			super.setId("UO");
			super.setFullName("Unit Ontology");
		}
	}
}
