/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.writer.mzidentml.jaxb.EnzymeType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamListType;

/**
 * EnzymeTypeMaker.java
 * @author Vagisha Sharma
 * Aug 4, 2011
 * 
 */
public class EnzymeTypeMaker {

	/*
	 * From sequest.params:  https://proteomicsresource.washington.edu/sequest_release/release_201101.php
	 * 
	 [SEQUEST_ENZYME_INFO]
	 0.  No_Enzyme              0      -           -
	 1.  Trypsin                1      KR          P
	 2.  Chymotrypsin           1      FWY         P
	 3.  Clostripain            1      R           -
	 4.  Cyanogen_Bromide       1      M           -
	 5.  IodosoBenzoate         1      W           -
	 6.  Proline_Endopept       1      P           -
	 7.  Staph_Protease         1      E           -
	 8.  Trypsin_K              1      K           P
	 9.  Trypsin_R              1      R           P
	 10. AspN                   0      D           -
	 11. Cymotryp/Modified      1      FWYL        P
	 12. Elastase               1      ALIV        P
	 13. Elastase/Tryp/Chymo    1      ALIVKRWFY   P
	 14. Trypsin                1      KRD         -

	 * 
	 */
	
	/*
	 
	  08/04/11
	  These are the enzymes currently in MSDaPl's database:
	  +----+-------------------+-------+------+-------+-------------+
	  | id | name              | sense | cut  | nocut | description |
      +----+-------------------+-------+------+-------+-------------+
      |  1 | Trypsin           |     1 | KR   | P     | NULL        |
      |  2 | LysC              |     1 | K    | P     | NULL        |
      |  3 | Elastase          |     1 | ALIV | P     | NULL        |
      |  4 | Cymotryp/Modified |     1 | FWYL | P     | NULL        |
      |  5 | LysC              |     1 | K    | -     | NULL        |
      +----+-------------------+-------+------+-------+-------------+

	 
	 */
	
	/**
	 * Only some enzymes are supported.  
	 */
	public static EnzymeType makeEnzymeType(MsEnzymeIn enzyme) {
		
		
		// !----------------------------------------------------------
		// TODO This method should be tested and other enzymes should be supported.
		// !----------------------------------------------------------
		
		if (enzyme == null || enzyme.getName().equalsIgnoreCase("No_Enzyme")) {
			return makeNoEnzyme();
		}
		
		// found entries for these in the controlled vocabulary:
		if(isTrypsin(enzyme)) {
			return makeTrypsin();
		}
		else if(isCynanogenBromide(enzyme)) {
			return makeCyanogenBromide();
		}
		else if(isLysCNoCutP(enzyme)) {
			return makeLysC_OR_TrypsinK_nocutP();
		}
		else if(isLysC(enzyme)) {
			return makeLysC_OR_TrypsinK();
		}
		else if(isArgC(enzyme)) {
			return makeArgC_OR_TrypsinR();
		}
		else if(isModifiedChymotrypsin(enzyme)) {
			return makeModifiedChymotrypsin();
		}
		
		
		// could not find and entry for these:
		else if(enzyme.getName().equalsIgnoreCase("Chymotrypsin")) {
			return null;  // NOTE: can't find a term for this in PSI-MS vocabulary
			              // There is an entry for Chymotrypsin but the given regex does not match
			              // the "cut" residues in sequest.params
		}
		
		
		return null;
	}
	
	private static boolean isTrypsin(MsEnzymeIn enzyme) {
		
		// 1.  Trypsin                1      KR          P
		return (
				("RK".equals(enzyme.getCut()) || "KR".equals(enzyme.getCut()))
				&& 
				"P".equals(enzyme.getNocut())
				&&
				enzyme.getSense() == Sense.CTERM
				);
	}
	
	private static boolean isCynanogenBromide(MsEnzymeIn enzyme) {
		
		// 4.  Cyanogen_Bromide       1      M           -
		return (
				"M".equals(enzyme.getCut())
				&&
				"-".equals(enzyme.getNocut())
				&&
				enzyme.getSense() == Sense.CTERM
				);
		
	}
	
	private static boolean isLysCNoCutP(MsEnzymeIn enzyme) {
		
		// 8.  Trypsin_K              1      K           P
		return (
				"K".equals(enzyme.getCut())
				&&
				"P".equals(enzyme.getNocut())
				&&
				enzyme.getSense() == Sense.CTERM
				);
		
	}
	
	private static boolean isLysC(MsEnzymeIn enzyme) {
		
		return (
				"K".equals(enzyme.getCut())
				&&
				"-".equals(enzyme.getNocut())
				&&
				enzyme.getSense() == Sense.CTERM
				);
		
	}
	
	private static boolean isArgC(MsEnzymeIn enzyme) {
		
		// 9.  Trypsin_R              1      R           P
		return (
				"R".equals(enzyme.getCut())
				&&
				"P".equals(enzyme.getNocut())
				&&
				enzyme.getSense() == Sense.CTERM
				);
		
	}
	
	private static boolean isModifiedChymotrypsin(MsEnzymeIn enzyme) {
		
		// 11. Cymotryp/Modified      1      FWYL        P
		return (
				enzyme.getCut() != null
				&&
				enzyme.getCut().length() == 4
				&&
				enzyme.getCut().contains("F")
				&& 
				enzyme.getCut().contains("W")
				&&
				enzyme.getCut().contains("Y")
				&&
				enzyme.getCut().contains("L")
				&&
				"P".equals(enzyme.getNocut())
				&&
				enzyme.getSense() == Sense.CTERM
				);
		
	}
	
	public static EnzymeType makeNoEnzyme() {
		
		EnzymeType enzymeType = new EnzymeType();
		enzymeType.setId("Enz1");
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001091", "NoEnzyme", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeTrypsin() {
		
		// 1.  Trypsin                1      KR          P
		
		/*
		 	[Term]
			id: MS:1001251
			name: Trypsin
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001176 ! (?<=[KR])(?!P)
		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001251", "Trypsin", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeModifiedChymotrypsin() {
		
		/*
		 	[Term]
			id: MS:1001306
			name: Chymotrypsin
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001332 ! (?<=[FYWL])(?!P)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001306", "Chymotrypsin", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeCyanogenBromide() {
		
		// 4.  Cyanogen_Bromide       1      M           -
		/*
		 	[Term]
			id: MS:1001307
			name: CNBr
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001333 ! (?<=M)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001307", "CNBr", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeLysC_OR_TrypsinK_nocutP() {
		
		// 8.  Trypsin_K              1      K           P
		/*
		 	[Term]
			id: MS:1001309
			name: Lys-C
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001335 ! (?<=K)(?!P)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001309", "Lys-C", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeLysC_OR_TrypsinK() {
		
		/*
		 	[Term]
			id: MS:1001310
			name: Lys-C/P
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001336 ! (?<=K)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001310", "Lys-C/P", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	public static EnzymeType makeArgC_OR_TrypsinR() {
		
		// 9.  Trypsin_R              1      R           P
		/*
		 	[Term]
			id: MS:1001303
			name: Arg-C
			is_a: MS:1001045 ! cleavage agent name
			relationship: has_regexp MS:1001272 ! (?<=R)(?!P)

		 */
		EnzymeType enzymeType = makeEnzymeType();
		ParamListType nameParam = new ParamListType();
		nameParam.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001303", "Arg-C", CvConstants.PSI_CV));
		enzymeType.setEnzymeName(nameParam);
		return enzymeType;
	}
	
	private static EnzymeType makeEnzymeType() {
		
		EnzymeType enzymeType = new EnzymeType();
		enzymeType.setId("Enz1");
		enzymeType.setCTermGain("OH");
		enzymeType.setNTermGain("H");
		enzymeType.setMinDistance(1);
		return enzymeType;
	}
}
