/**
 * 
 */
package org.yeastrc.nrseq;

import org.yeastrc.bio.taxonomy.TaxonomyUtils;

/**
 * CommonNameSupportUtils.java
 * @author Vagisha Sharma
 * Jul 14, 2010
 * 
 */
public class CommonNameSupportUtils {

	private CommonNameSupportUtils() {}


	public static boolean isSpeciesSupported(int speciesId) {
		return (speciesId == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE 
				|| speciesId == TaxonomyUtils.DROSOPHILA_MELANOGASTER
				|| speciesId == TaxonomyUtils.CAENORHABDITIS_ELEGANS
				|| speciesId == TaxonomyUtils.SCHIZOSACCHAROMYCES_POMBE
				|| speciesId == TaxonomyUtils.HOMO_SAPIENS
				);
	}
}
