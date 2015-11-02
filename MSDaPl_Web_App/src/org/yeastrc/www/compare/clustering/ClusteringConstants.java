/**
 * ClusteringConstants.java
 * @author Vagisha Sharma
 * Apr 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

/**
 * 
 */
public class ClusteringConstants {

	private ClusteringConstants() {}
	
	public static final String BASE_DIR = "clustering";
	public static final String IMG_FILE = "clustered.pdf";
	public static final String INPUT_FILE = "input.txt";
	public static final String INPUT_FILE_MOD = "input_mod.txt"; // modified spectrum counts; scaled, normalized etc.
																 // This is the input that will be give to to heatmap.2
	public static final String OUTPUT_FILE = "output.txt";
	public static final String R_SCRIPT = "r.script.txt";
	public static final String SH_SCRIPT = "run.sh";
	public static final String COLORS_RG = "colors_rg.txt";
	public static final String COLORS_BY = "colors_by.txt";
	public static final String PROT_GRP_SER = "ProteinGroupComparisonDataset.ser";
	public static final String PROT_SER = "ProteinComparisonDataset.ser";
	public static final String FORM_SER = "ProteinSetComparisonForm.ser";
	
	public static enum GRADIENT {
		BY("Blue-Yellow"),
		GR("Green-Red");
		
		private String displayName;
		
		private GRADIENT(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public static GRADIENT getGradient(String displayName) {
			for(GRADIENT grad: GRADIENT.values()) {
				if(grad.getDisplayName().equalsIgnoreCase(displayName))
					return grad;
			}
			return null;
		}
	}
}
