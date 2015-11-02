/**
 * 
 */
package org.yeastrc.www.proteinfer.idpicker;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.EvidenceCode;
import org.yeastrc.bio.go.GOAnnotation;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.GOSearcher;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.GOProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.nr_seq.listing.ProteinListing;
import org.yeastrc.nr_seq.listing.ProteinListingBuilder;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.go.Annotation;
import org.yeastrc.www.go.GOEvidenceCodeConverter;
import org.yeastrc.www.go.ProteinGOAnnotationChecker;

/**
 * GOAnnotationsWriter.java
 * @author Vagisha Sharma
 * Sep 15, 2010
 * 
 */
public class GOAnnotationsWriter {

	private static final Logger log = Logger.getLogger(GOAnnotationsWriter.class);
	
	public void writeIndividualProteins(PrintWriter writer, int pinferId, List<Integer> proteinIds, GOProteinFilterCriteria goProteinFilterCriteria) throws Exception {
		
		writer.write("Accession\tCommonName\tBiologicalProcess\tCellularComponent\tMolecularFunction\n");
		
		ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
		ProteinListingBuilder listingBuilder = ProteinListingBuilder.getInstance();
		GOSearcher searcher = GOSearcher.getInstance();
		
		List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
		
		Set<GONode> bpNodes = new HashSet<GONode>();
		Set<GONode> ccNodes = new HashSet<GONode>();
		Set<GONode> mfNodes = new HashSet<GONode>();
		
		// Are there any evidence codes to be excluded?
		List<EvidenceCode> excludeGoEvidenceCodes = null;
		if(goProteinFilterCriteria != null)
			excludeGoEvidenceCodes = GOEvidenceCodeConverter.convert(goProteinFilterCriteria.getExcludeEvidenceCodes());
		
		for(Integer piProteinId: proteinIds) {
			
			ProteinferProtein protein = protDao.loadProtein(piProteinId);
			int nrseqProteinId = protein.getNrseqProteinId();
			
			ProteinListing listing = listingBuilder.build(nrseqProteinId, fastaDatabaseIds);
			
			Map<String, Set<GOAnnotation>> annotations = searcher.getGOAnnotations(listing);
			
			List<String> bpAnnot = getAnnotations(bpNodes, excludeGoEvidenceCodes, nrseqProteinId, annotations.get("P"));
			
			List<String> ccAnnot = getAnnotations(ccNodes, excludeGoEvidenceCodes, nrseqProteinId, annotations.get("C"));
			
			List<String> mfAnnot = getAnnotations(mfNodes, excludeGoEvidenceCodes, nrseqProteinId, annotations.get("F"));
			
			writer.write(StringUtils.makeCommaSeparated(listing.getFastaAccessions()));
			writer.write("\t"+StringUtils.makeCommaSeparated(listing.getCommonNames()));
			writer.write("\t"+StringUtils.makeCommaSeparated(bpAnnot));
			writer.write("\t"+StringUtils.makeCommaSeparated(ccAnnot));
			writer.write("\t"+StringUtils.makeCommaSeparated(mfAnnot));
			
			writer.write("\n");
		}
		
		// now write the definitions for all the GO nodes we saw
		writeGONodes(writer, bpNodes, ccNodes, mfNodes);
		
		writer.write("\n\n");
	}
	
	public void writeGroupProteins(PrintWriter writer, int pinferId, List<Integer> proteinIds, GOProteinFilterCriteria goProteinFilterCriteria) throws Exception {
		
		writer.write("Accession(s)\tCommonName(s)\tBiologicalProcess\tCellularComponent\tMolecularFunction\n");
		
		GOSearcher searcher = GOSearcher.getInstance();
		
		List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        
        int currentGroupLabel = -1;
        String fastaIds = "";
        String commonNames = "";
        Set<String> bpNodeAccessions = new HashSet<String>();
        Set<String> ccNodeAccessions = new HashSet<String>();
        Set<String> mfNodeAccessions = new HashSet<String>();
        
        Set<GONode> bpNodes = new HashSet<GONode>();
		Set<GONode> ccNodes = new HashSet<GONode>();
		Set<GONode> mfNodes = new HashSet<GONode>();
		
		// Are there any evidence codes to be excluded?
		List<EvidenceCode> excludeGoEvidenceCodes = null;
		if(goProteinFilterCriteria != null)
			excludeGoEvidenceCodes = GOEvidenceCodeConverter.convert(goProteinFilterCriteria.getExcludeEvidenceCodes());
		
        for(int i = 0; i < proteinIds.size();  i++) {
            int proteinId = proteinIds.get(i);
            WIdPickerProtein wProt = IdPickerResultsLoader.getIdPickerProtein(proteinId, 
            		new PeptideDefinition(), // add a dummy PeptideDefinition. We are only interested in protein groups and GO annotations
            		fastaDatabaseIds,
            		false); // we don't want Philius annotations
            
            if(wProt.getProtein().getProteinGroupLabel() != currentGroupLabel) {
            	
                if(currentGroupLabel != -1) {
                    
                    // Accessions(s)
                    writer.write(trimComma(fastaIds)+"\t");
                    
                    // Common name(s)
                    writer.write(trimComma(commonNames)+"\t");
                    
                    // Biological Process
                    writer.write(StringUtils.makeCommaSeparated(bpNodeAccessions)+"\t");
                    // Cellular Component
                    writer.write(StringUtils.makeCommaSeparated(ccNodeAccessions)+"\t");
                    // Molecular Function
                    writer.write(StringUtils.makeCommaSeparated(mfNodeAccessions));
                    
                    writer.write("\n");
                }
                fastaIds = "";
                commonNames = "";
                bpNodeAccessions.clear();
                bpNodeAccessions = new HashSet<String>();
                ccNodeAccessions.clear();
                ccNodeAccessions = new HashSet<String>();
                mfNodeAccessions.clear();
                mfNodeAccessions = new HashSet<String>();
                
                currentGroupLabel = wProt.getProtein().getProteinGroupLabel();
            }
            try {
				fastaIds += ";"+wProt.getAccessionsCommaSeparated();
			} catch (SQLException e) {
				log.error("Error getting accessions", e);
				fastaIds += ",ERROR";
			}
			try {
				String cn = wProt.getCommonNamesCommaSeparated();
				if(cn.trim().length() > 0)
					commonNames += ";"+cn;
			} catch (SQLException e) {
				log.error("Error getting common names", e);
				fastaIds += ",ERROR";
			}
			
			// GET GO annotations for this protein
			ProteinListing listing = wProt.getProteinListing();
			Map<String, Set<GOAnnotation>> annotations = searcher.getGOAnnotations(listing);
			int nrseqProteinId = listing.getNrseqProteinId();
			
			List<String> bpAnnot = getAnnotations(bpNodes, excludeGoEvidenceCodes, nrseqProteinId, annotations.get("P"));
			
			List<String> ccAnnot = getAnnotations(ccNodes, excludeGoEvidenceCodes, nrseqProteinId, annotations.get("C"));
			
			List<String> mfAnnot = getAnnotations(mfNodes, excludeGoEvidenceCodes, nrseqProteinId, annotations.get("F"));
			
			bpNodeAccessions.addAll(bpAnnot);
			ccNodeAccessions.addAll(ccAnnot);
			mfNodeAccessions.addAll(mfAnnot);
			
        }
        // write the last one
        writer.write(trimComma(fastaIds)+"\t");
        writer.write(trimComma(commonNames)+"\t");
        writer.write(StringUtils.makeCommaSeparated(bpNodeAccessions)+"\t");
        writer.write(StringUtils.makeCommaSeparated(ccNodeAccessions)+"\t");
        writer.write(StringUtils.makeCommaSeparated(mfNodeAccessions));
        
        writer.write("\n");
        
        
		// now write the definitions for all the GO nodes we saw
		writeGONodes(writer, bpNodes, ccNodes, mfNodes);
		
		writer.write("\n\n");
	}

	private List<String> getAnnotations(Set<GONode> goNodes,
			List<EvidenceCode> excludeGoEvidenceCodes, int nrseqProteinId,
			Set<GOAnnotation> annotations) throws SQLException {
		
		List<String> anotAccessions = new ArrayList<String>();
		for(GOAnnotation annot: annotations) {
			if(excludeGoEvidenceCodes != null) {
				Annotation a = ProteinGOAnnotationChecker.isProteinAnnotated(nrseqProteinId, annot.getNode().getId(), excludeGoEvidenceCodes);
				if(a == Annotation.NONE)
					continue;
			}
			anotAccessions.add(annot.getNode().getAccession());
			goNodes.add(annot.getNode());
		}
		return anotAccessions;
	}
	
	
	private String trimComma(String toTrim) {
		if(toTrim.length() > 0)
			return toTrim.substring(1);
		else
			return toTrim;
	}

	private void writeGONodes(PrintWriter writer, Set<GONode> bpNodes,
			Set<GONode> ccNodes, Set<GONode> mfNodes) {
		
		writer.write("\n\n");
		writer.write("GOAccession\tAspect\tName\n");
		
		for(GONode node: bpNodes) {
			writer.write(node.getAccession()+"\tP\t"+node.getName()+"\n");
		}
		for(GONode node: ccNodes) {
			writer.write(node.getAccession()+"\tC\t"+node.getName()+"\n");
		}
		for(GONode node: mfNodes) {
			writer.write(node.getAccession()+"\tF\t"+node.getName()+"\n");
		}
	}
}
