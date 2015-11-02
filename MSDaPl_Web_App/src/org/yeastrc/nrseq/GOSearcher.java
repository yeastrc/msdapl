/**
 * GOSearcher.java
 * @author Vagisha Sharma
 * Mar 10, 2010
 * @version 1.0
 */
package org.yeastrc.nrseq;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOUtils;
import org.yeastrc.bio.taxonomy.TaxonomyUtils;
import org.yeastrc.db.DBConnectionManager;
import org.yeastrc.nr_seq.listing.ProteinListing;

/**
 * 
 */
public class GOSearcher {

	/**
	 * Get ALL GO nodes for this protein from the Gene Ontology database.
	 * <P>Returned is a Map with 3 keys, "P" (process), "F" (function) and "C" (component)
	 * <p>Each key is maped to a Collection of Go Nodes for that aspect
	 * @param protein
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Set<GONode>> getGONodes (ProteinListing listing) throws Exception {
		Map<String, Set<GONode>> retMap = new HashMap<String, Set<GONode>>();
		boolean foundNodes = false;
		
		retMap.put("P", new HashSet<GONode>());
		retMap.put("C", new HashSet<GONode>());
		retMap.put("F", new HashSet<GONode>());
		
		// Get our connection to the database.
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		try {

			// If this is a S. cerevisiae protein, get the GO annotation from SGD
			if (listing.getSpeciesId() == TaxonomyUtils.SACCHAROMYCES_CEREVISIAE) {
				
				List<String> names = listing.getBestReferenceAccessions();
				if (names.size() == 1) {
					Iterator nIter = names.iterator();
					String name = (String)nIter.next();
					
					String sqlStr = "SELECT GOID FROM tblGOAnnotation WHERE featureName = ?";
					
					conn = DBConnectionManager.getConnection("sgd");	
					stmt = conn.prepareStatement(sqlStr);
					stmt.setString(1, name);
					
					rs = stmt.executeQuery();
					
					while (rs.next()) {
						GOCache gc = GOCache.getInstance();
						
						try {
							String goAcc = String.valueOf( rs.getInt( 1 ) );

							if (!goAcc.startsWith("GO")) {
								while (goAcc.length() < 7) {
									goAcc = "0" + goAcc;
								}
								goAcc = "GO:" + goAcc;
							}
							
							GONode gnode = gc.getGONode( goAcc );
							
							String key = null;
							if (gnode.getAspect() == GOUtils.BIOLOGICAL_PROCESS)
								key = "P";
							else if(gnode.getAspect() == GOUtils.CELLULAR_COMPONENT)
								key = "C";
							else if(gnode.getAspect() == GOUtils.MOLECULAR_FUNCTION)
								key = "F";
							else
								continue;
							
							// Add it to the appropriate Collection in the map
							foundNodes = true;
							retMap.get(key).add(gnode);

						} catch (Exception e) { ; }
					}
					
					rs.close(); rs = null;
					stmt.close(); stmt = null;
					conn.close(); conn = null;
				}
				
			}
			
			// If this is a C. elegans protein, get the GO annotation from Wormbase
			else if ( listing.getSpeciesId() == TaxonomyUtils.CAENORHABDITIS_ELEGANS ) {
				
				List<String> names = listing.getBestReferenceAccessions();
				if (names.size() == 1) {
					Iterator nIter = names.iterator();
					String name = (String)nIter.next();
					
					String sqlStr = "SELECT a.goAcc, a.isNot FROM tblGOAnnotation AS a INNER JOIN tblProteinGenes AS b ON a.WBGeneID = b.WBGeneID WHERE b.systematicName = ?";
					
					conn = DBConnectionManager.getConnection("wormbase");	
					stmt = conn.prepareStatement(sqlStr);
					stmt.setString(1, name);
					
					rs = stmt.executeQuery();
					
					while (rs.next()) {
						GOCache gc = GOCache.getInstance();
						
						try {
							String goAcc = rs.getString( 1 );
							GONode gnode = gc.getGONode( goAcc );
							
							String key = null;
							if (gnode.getAspect() == GOUtils.BIOLOGICAL_PROCESS)
								key = "P";
							else if(gnode.getAspect() == GOUtils.CELLULAR_COMPONENT)
								key = "C";
							else if(gnode.getAspect() == GOUtils.MOLECULAR_FUNCTION)
								key = "F";
							else
								continue;
							
							// Add it to the appropriate Collection in the map
							foundNodes = true;
							retMap.get(key).add(gnode);

						} catch (Exception e) { ; }
					}
					
					rs.close(); rs = null;
					stmt.close(); stmt = null;
					conn.close(); conn = null;
				}
				
			}
			
			
			// If no annotation was found using the preferred method(s) above, search the GO database
			if (!foundNodes) {
				// Get the GO annotation out of the GO database
				
				String sqlStr = "SELECT term.acc FROM seq ";
				sqlStr += " INNER JOIN gene_product_seq AS gps ON seq.id = gps.seq_id ";
				sqlStr += " INNER JOIN gene_product AS gp ON gps.gene_product_id = gp.id ";
				sqlStr += " INNER JOIN species ON gp.species_id = species.id ";
				sqlStr += " INNER JOIN association AS ass ON gp.id = ass.gene_product_id ";
				sqlStr += " INNER JOIN term ON ass.term_id = term.id ";
				sqlStr += " WHERE seq.seq = ? ";
				sqlStr += " AND ass.is_not = 0 ";
				sqlStr += " AND species.ncbi_taxa_id = ?";
				
				conn = DBConnectionManager.getConnection("go");	
				stmt = conn.prepareStatement(sqlStr);
				stmt.setString(1, listing.getSequence());
				stmt.setInt(2, listing.getSpeciesId());
				
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					GOCache gc = GOCache.getInstance();
					GONode gnode = gc.getGONode(rs.getString(1));
					
					String key = null;
					if (gnode.getAspect() == GOUtils.BIOLOGICAL_PROCESS)
						key = "P";
					else if(gnode.getAspect() == GOUtils.CELLULAR_COMPONENT)
						key = "C";
					else if(gnode.getAspect() == GOUtils.MOLECULAR_FUNCTION)
						key = "F";
					else
						continue;
					
					// Add it to the appropriate Collection in the map
					retMap.get(key).add(gnode);
	
				}

				
				rs.close(); rs = null;
				stmt.close(); stmt = null;
				conn.close(); conn = null;
			}

		} finally {

			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rs != null) {
				try { rs.close(); } catch (SQLException e) { ; }
				rs = null;
			}
			if (stmt != null) {
				try { stmt.close(); } catch (SQLException e) { ; }
				stmt = null;
			}
			if (conn != null) {
				try { conn.close(); } catch (SQLException e) { ; }
				conn = null;
			}
		}
		
		return retMap;
	}
}
