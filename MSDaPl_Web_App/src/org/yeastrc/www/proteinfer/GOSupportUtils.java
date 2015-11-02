/**
 * GOSupportChecker.java
 * @author Vagisha Sharma
 * Mar 25, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.bio.taxonomy.Species;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;

/**
 * 
 */
public class GOSupportUtils {

	private GOSupportUtils() {}


	public static List<Species> getSpeciesList() throws SQLException {
		
		List<Species> speciesList = new ArrayList<Species>();
		// C. elgans
		Species species = new Species();
		species.setId(TaxonomyUtils.CAENORHABDITIS_ELEGANS);
		speciesList.add(species);
		// Drosophila
		species = new Species();
		species.setId(TaxonomyUtils.DROSOPHILA_MELANOGASTER);
		speciesList.add(species);
		// Mouse
//		species = new Species();
//		species.setId(10090);
//		speciesList.add(species);
		// Rat
//		species = new Species();
//		species.setId(10116);
//		speciesList.add(species);
		// Budding Yeast
		species = new Species();
		species.setId(TaxonomyUtils.SACCHAROMYCES_CEREVISIAE);
		speciesList.add(species);
		// Fission Yeast
//		species = new Species();
//		species.setId(TaxonomyUtils.SCHIZOSACCHAROMYCES_POMBE);
//		speciesList.add(species);
		
		return speciesList;
	}
	
	public static boolean isGOSlimSupported(int speciesId) {
		return (speciesId == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE 
				|| speciesId == TaxonomyUtils.DROSOPHILA_MELANOGASTER
				|| speciesId == TaxonomyUtils.CAENORHABDITIS_ELEGANS
				|| speciesId == TaxonomyUtils.HOMO_SAPIENS
				);
	}
	
	public static boolean isGOEnrichmentSupported(int speciesId) {
		return (speciesId == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE 
				|| speciesId == TaxonomyUtils.DROSOPHILA_MELANOGASTER
				|| speciesId == TaxonomyUtils.CAENORHABDITIS_ELEGANS
				);
	}
}
