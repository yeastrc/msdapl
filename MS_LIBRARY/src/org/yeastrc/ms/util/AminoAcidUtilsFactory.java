/**
 * AminoAcidUtilsFactory.java
 * @author Vagisha Sharma
 * Apr 15, 2010
 * @version 1.0
 */
package org.yeastrc.ms.util;


/**
 * 
 */
public class AminoAcidUtilsFactory {

	private AminoAcidUtilsFactory() {}
	
	private static SequestAminoAcidUtils seqUtils = new SequestAminoAcidUtils();
	private static MascotAminoAcidUtils mascotUtils = new MascotAminoAcidUtils();
	private static ProteinAminoAcidUtils protUtils;
	private static BaseAminoAcidUtils aaUtils;
	
//	public static BaseAminoAcidUtils getAminoAcidUtils(int searchId) throws Exception {
//		
//		MsSearch search = DAOFactory.instance().getMsSearchDAO().loadSearch(searchId);
//		if(search.getSearchProgram() == Program.SEQUEST)
//			return seqUtils;
//		else if(search.getSearchProgram() == Program.MASCOT)
//			return mascotUtils;
//		else
//			throw new Exception("No rule for getting amino acid utils for program: "+search.getSearchProgram());
//	}
	
	public static SequestAminoAcidUtils getSequestAminoAcidUtils() {
		if(seqUtils == null)
			seqUtils = new SequestAminoAcidUtils();
		return seqUtils;
	}
	
//	public static MascotAminoAcidUtils getMascotAminoAcidUtils() {
//		if(mascotUtils == null)
//			mascotUtils = new MascotAminoAcidUtils();
//		return mascotUtils;
//	}
	
	public static ProteinAminoAcidUtils getProteinAminoAcidUtils() {
		if(protUtils == null)
			protUtils = new ProteinAminoAcidUtils();
		return protUtils;
	}
	
	public static BaseAminoAcidUtils getAminoAcidUtils() {
		if(aaUtils == null)
			aaUtils = new BaseAminoAcidUtils();
		return aaUtils;
	}
}
