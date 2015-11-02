package org.yeastrc.www.proteinfer.proteinProphet;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetParam;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.util.TimeUtils;

public class ProteinProphetDownloadAction extends Action {

    private static final Logger log = Logger.getLogger(ProteinProphetDownloadAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        ProteinProphetFilterForm filterForm = (ProteinProphetFilterForm)form;
        
        // get the protein inference id
        int pinferId = filterForm.getPinferId();
        
        long s = System.currentTimeMillis();
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"ProtInfer_"+pinferId+".txt\"");
        response.setHeader("cache-control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.write("Date: "+new Date()+"\n\n");
        writeResults(writer, pinferId, filterForm);
        writer.close();
        long e = System.currentTimeMillis();
        log.info("ProteinProphetDownloadAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return null;
    }

    private void writeFilteringOptions(PrintWriter writer, ProteinProphetFilterForm filterForm) {
        
        writer.write("Min. Peptides: "+filterForm.getMinPeptides()+"\n");
        writer.write("Max. Peptides: "+filterForm.getMaxPeptides()+"\n");
        writer.write("Min. Unique Peptides: "+filterForm.getMinUniquePeptides()+"\n");
        writer.write("Max. Unique Peptides: "+filterForm.getMaxUniquePeptides()+"\n");
        writer.write("Min. Spectrum Matches: "+filterForm.getMinSpectrumMatches()+"\n");
        writer.write("Max. Spectrum Matches: "+filterForm.getMaxSpectrumMatches()+"\n");
        writer.write("Min. Coverage(%): "+filterForm.getMinCoverage()+"\n");
        writer.write("Max. Coverage(%): "+filterForm.getMaxCoverage()+"\n");
        writer.write("Min. Molecular Wt.: "+filterForm.getMinMolecularWt()+"\n");
        writer.write("Max. Molecular Wt.: "+filterForm.getMaxMolecularWt()+"\n");
        writer.write("Min. pI: "+filterForm.getMinPi()+"\n");
        writer.write("Max. pI: "+filterForm.getMaxPi()+"\n");
        writer.write("Min. ProteinProphet Group Probability: "+filterForm.getMinGroupProbability()+"\n");
        writer.write("Max. ProteinProphet Group Probability: "+filterForm.getMaxGroupProbability()+"\n");
        writer.write("Min. ProteinProphet Protein Probability: "+filterForm.getMinProteinProbability()+"\n");
        writer.write("Max. ProteinProphet Protein Probability: "+filterForm.getMaxProteinProbability()+"\n");
        
        
        writer.write("Exclude Subsumed Proteins: "+filterForm.isExcludeSubsumed()+"\n");
        writer.write("Exclude Indistinguishable Groups: "+filterForm.isExcludeIndistinProteinGroups()+"\n");
        
        writer.write("Validation Status: "+filterForm.getValidationStatusString()+"\n");
        writer.write("Include proteins with peptide charge states: "+filterForm.getChargeStatesString()+"\n");
        writer.write("Peptide sequence: "+filterForm.getPeptide()+"; Exact match: "+filterForm.getExactPeptideMatch()+"\n");
        writer.write("Fasta ID filter: "+filterForm.getAccessionLike()+"\n");
        writer.write("Description filter (Like): "+filterForm.getDescriptionLike()+"\n");
        writer.write("Description filter (Not Like): "+filterForm.getDescriptionNotLike()+"\n");
        writer.write("Search Swiss-Prot and NCBI-NR: "+filterForm.isSearchAllDescriptions()+"\n");
    }

    private void writeResults(PrintWriter writer, int pinferId, ProteinProphetFilterForm filterForm) {
        
        
        ProteinProphetRun prophetRun = ProteinferDAOFactory.instance().getProteinProphetRunDao().loadProteinferRun(pinferId);
        // Get the peptide definition; We don't get peptide definition from ProteinProphet params so just
        // use a dummy one.
        PeptideDefinition peptideDef = new PeptideDefinition();
        peptideDef.setUseCharge(true);
        peptideDef.setUseMods(true);
        
        // Get the filtering criteria
        ProteinProphetFilterCriteria filterCriteria = filterForm.getFilterCriteria(peptideDef);
        filterCriteria.setSortBy(SORT_BY.PROTEIN_PROPHET_GROUP);  // This is important for printing groups!!
        filterCriteria.setSortOrder(SORT_ORDER.ASC);
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = ProteinProphetResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // print the parameters used for the protein inference run
        writer.write("Program Version: "+prophetRun.getProgramVersion()+"\n");
        writer.write("Parameters used for ProteinProphet ID: "+prophetRun.getId()+"\n");
        
        for(ProteinProphetParam param: prophetRun.getParams()) {
            writer.write(param.getName()+": "+param.getValue()+"\n");
        }
        writer.write("\n\n");
        
        // print the filtering options being used
        writer.write("Filtering Options: \n");
        writeFilteringOptions(writer, filterForm);
        writer.write("\n\n");
        
        
        // print summary
        WProteinProphetResultSummary summary = ProteinProphetResultsLoader.getProteinProphetResultSummary(pinferId, proteinIds);
        writer.write("Unfiltered ProteinProphet groups:\t"+summary.getAllProteinProphetGroupCount()+"\n");
        writer.write("Unfiltered indistinguishable protein groups:\t"+summary.getAllProteinGroupCount()+"\n");
        writer.write("Unfiltered indistinguishable protein groups (non-subsumed):\t"+summary.getAllParsimoniousProteinGroupCount()+"\n");
        writer.write("Unfiltered proteins:\t"+summary.getAllProteinCount()+"\n");
        writer.write("Unfiltered proteins (non-subsumed):\t"+summary.getAllParsimoniousProteinCount()+"\n");
        
        
        writer.write("Filtered ProteinProphet Groups:\t"+summary.getFilteredProphetGroupCount()+"\n");
        writer.write("Filtered Protein Groups:\t"+summary.getFilteredProteinGroupCount()+"\n");
        writer.write("Filtered Protein Groups (non-subsumed):\t"+summary.getFilteredParsimoniousProteinGroupCount()+"\n");
        writer.write("Filtered Proteins:\t"+summary.getFilteredProteinCount()+"\n");
        writer.write("Filtered Proteins (non-subsumed):\t"+summary.getFilteredParsimoniousProteinCount()+"\n");
        
        writer.write("\n\n");
        
        
        // only one line for each protein group; all members of the group will be displayed comma-separated
        printCollapsedProteinGroups(writer, pinferId, peptideDef, proteinIds);
    }

    
    private void printCollapsedProteinGroups(PrintWriter writer, int pinferId,
            PeptideDefinition peptideDef, List<Integer> proteinIds) {
    	
    	
    	writer.write("ProteinProphetGroupID\t");
    	writer.write("GroupProbability\t");
    	writer.write("IndistinguishableGroupID\t");
    	writer.write("ProteinProbability\t");
    	writer.write("Subsumed\t");
    	writer.write("FastaID\t");
    	writer.write("CommonName\t");
    	
    	writer.write("Mol.Wt\t");
    	writer.write("pI\t");
    	writer.write("Coverage\t");
    	writer.write("NumPeptides\t");
    	writer.write("NumUniquePeptides\t");
    	writer.write("NumSpectra\t");
        writer.write("\n");
        
        
        // proteins are sorted by ProteinProphet group ID first and then by indistinguishable group ID. 
        List<WProteinProphetProteinGroup> proteinGroups = ProteinProphetResultsLoader.getProteinProphetGroups(pinferId, proteinIds, peptideDef);
        
        for(WProteinProphetProteinGroup prophetGroup: proteinGroups) {
        	
        	double prophetGroupProbability = prophetGroup.getGroupProbability();
        	int prophetGroupId = prophetGroup.getProteinProphetGroupNumber();
        	
        	for(WProteinProphetIndistProteinGroup iGroup: prophetGroup.getIndistinguishableProteinGroups()) {
        		
            	String fastaIds = "";
                String commonNames = "";
                String coverageStr = "";
                String molWtStr = "";
                String piStr = "";
                int spectrumCount = 0;
                int numPept = 0;
                int numUniqPept = 0;
            	boolean subsumed = false;
            	
            	for(WProteinProphetProtein wProt: iGroup.getProteins()) {
            		
            		subsumed = wProt.getProtein().getSubsumed();
            		
            		// Fasta IDs
                    try {
                    	fastaIds += ";"+wProt.getAccessionsCommaSeparated();
                    } catch (SQLException e) {
                    	log.error("Error getting accessions", e);
                    	fastaIds += ",ERROR";
                    }
                    
                    // Common Name
                    try {
                    	String cn = wProt.getCommonNamesCommaSeparated();
                    	if(cn.trim().length() > 0)
                    		commonNames += ";"+cn;
                    } catch (SQLException e) {
                    	log.error("Error getting common names", e);
                    	commonNames += ",ERROR";
                    }
        			
                    coverageStr += ","+wProt.getProtein().getCoverage()+"%";
                    molWtStr += ","+wProt.getMolecularWeight();
                    piStr += ","+wProt.getPi();
                    numPept = wProt.getProtein().getPeptideCount();
                    numUniqPept = wProt.getProtein().getUniquePeptideCount();
                    spectrumCount = wProt.getProtein().getSpectrumCount();
            	}
            	
            	writer.write(prophetGroupId+"\t");
            	writer.write(prophetGroupProbability+"\t");
            	writer.write(iGroup.getGroupId()+"\t");
            	writer.write(iGroup.getProbability()+"\t");
            	
            	if(subsumed)
            		writer.write("S\t");
            	else
            		writer.write("\t");
            	
            	// Fasta ID
            	if(fastaIds.length() > 0)
            		fastaIds = fastaIds.substring(1); // remove first comma
            	writer.write(fastaIds+"\t");
            	
            	// Common names
            	if(commonNames.length() > 0)
            		commonNames = commonNames.substring(1);
            	writer.write(commonNames+"\t");
            	
            	 // Molecular weights
            	if(molWtStr.length() > 0)
            		molWtStr = molWtStr.substring(1);
            	writer.write(molWtStr+"\t");
                
                // pIs
            	if(piStr.length() > 0)
            		piStr = piStr.substring(1);
            	writer.write(piStr+"\t");
            	
                // Coverages
            	if(coverageStr.length() > 0)
            		coverageStr = coverageStr.substring(1);
            	writer.write(coverageStr+"\t");
                
            	// # Peptides
            	writer.write(numPept+"\t");
            	// # Unique peptides
            	writer.write(numUniqPept+"\t");
            	// Spectrum Count
            	writer.write(spectrumCount+"\t");
                
                writer.write("\n");
            	
        	}
        }
    }
}
