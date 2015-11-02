package org.yeastrc.www.proteinfer.idpicker;

import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.domain.protinfer.PeptideDefinition;
import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.SORT_BY;
import org.yeastrc.ms.domain.protinfer.SORT_ORDER;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerParam;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProtein;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerRun;
import org.yeastrc.ms.util.StringUtils;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.nr_seq.listing.ProteinReference;
import org.yeastrc.philius.dao.PhiliusDAOFactory;
import org.yeastrc.philius.dao.PhiliusResultDAO;
import org.yeastrc.philius.domain.PhiliusResult;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.protein.ProteinAbundanceDao;
import org.yeastrc.www.protein.ProteinAbundanceDao.YeastOrfAbundance;
import org.yeastrc.www.proteinfer.ProteinInferPhiliusResultChecker;
import org.yeastrc.www.util.RoundingUtils;

import edu.uwpr.protinfer.idpicker.IDPickerParams;
import edu.uwpr.protinfer.idpicker.IdPickerParamsMaker;

public class ProteinInferDownloadAction extends Action {

    private static final Logger log = Logger.getLogger(ProteinInferDownloadAction.class.getName());
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        IdPickerFilterForm filterForm = (IdPickerFilterForm)form;
        
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
        log.info("DownloadProteinferResultsAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return null;
    }

    private void writeFilteringOptions(PrintWriter writer, IdPickerFilterForm filterForm) {
        
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
        writer.write("Exclude Parsimonious Proteins: "+filterForm.isExcludeParsimoniousProteins()+"\n");
        writer.write("Exclude Non-parsimonious Proteins: "+filterForm.isExcludeNonParsimoniousProteins()+"\n");
        writer.write("Exclude Non-Subset Proteins: "+filterForm.isExcludeNonSubsetProteins()+"\n");
        writer.write("Exclude Subset Proteins: "+filterForm.isExcludeSubsetProteins()+"\n");
        writer.write("Exclude Indistinguishable Groups: "+filterForm.isExcludeIndistinProteinGroups()+"\n");
        writer.write("Validation Status: "+filterForm.getValidationStatusString()+"\n");
        writer.write("Include proteins with peptide charge states: "+filterForm.getChargeStatesString()+"\n");
        writer.write("Peptide sequence: "+filterForm.getPeptide()+"; Exact match: "+filterForm.getExactPeptideMatch()+"\n");
        writer.write("Fasta ID filter: "+filterForm.getAccessionLike()+"\n");
        writer.write("Description filter (Like): "+filterForm.getDescriptionLike()+"\n");
        writer.write("Description filter (Not Like): "+filterForm.getDescriptionNotLike()+"\n");
        writer.write("Search Swiss-Prot and NCBI-NR: "+filterForm.isSearchAllDescriptions()+"\n");
    }

    private void writeResults(PrintWriter writer, int pinferId, IdPickerFilterForm filterForm) {
        
        
        IdPickerRun idpRun = ProteinferDAOFactory.instance().getIdPickerRunDao().loadProteinferRun(pinferId);
        IDPickerParams idpParams = IdPickerParamsMaker.makeIdPickerParams(idpRun.getParams());
        PeptideDefinition peptideDef = idpParams.getPeptideDefinition();
        
        // Get the filtering criteria
        ProteinFilterCriteria filterCriteria = filterForm.getFilterCriteria(peptideDef);
        filterCriteria.setSortBy(SORT_BY.GROUP_ID); // This is important for printing groups!!
        filterCriteria.setSortOrder(SORT_ORDER.ASC);
        
        // Get the protein Ids that fulfill the criteria.
        List<Integer> proteinIds = IdPickerResultsLoader.getProteinIds(pinferId, filterCriteria);
        
        // print the protein inference ID, short name(if available) and the comments
        writer.write("Protein Inference ID: "+idpRun.getId()+"\n");
        String shortName = idpRun.getName();
        if(shortName != null && shortName.length() > 0)
        	writer.write("Short Name: "+idpRun.getName()+"\n");
        writer.write("Comments: "+idpRun.getComments()+"\n");
        writer.write("\n\n");
        
        // print the parameters used for the protein inference run
        writer.write("Program Version: "+idpRun.getProgramVersion()+"\n");
        writer.write("Parameters used for Protein Inference ID: "+idpRun.getId()+"\n");
        ProteinInferenceProgram program = idpRun.getProgram();
        for(IdPickerParam param: idpRun.getSortedParams()) {
            writer.write(program.getDisplayNameForParam(param.getName())+": "+param.getValue()+"\n");
        }
        writer.write("\n\n");
        
        // print the filtering options being used
        writer.write("Filtering Options: \n");
        writeFilteringOptions(writer, filterForm);
        writer.write("\n\n");
        
        
        // print summary
        WIdPickerResultSummary summary = IdPickerResultsLoader.getIdPickerResultSummary(pinferId, proteinIds);
        writer.write("Filtered Proteins:\t"+summary.getFilteredProteinCount()+"\n");
        writer.write("Filtered Protein Groups:\t"+summary.getFilteredProteinGroupCount()+"\n");
        writer.write("Parsimonious Proteins:\t"+summary.getFilteredParsimoniousProteinCount()+"\n");
        writer.write("Parsimonious Protein Groups:\t"+summary.getFilteredParsimoniousProteinGroupCount()+"\n");
        writer.write("Non-subset Proteins:\t"+summary.getFilteredNonSubsetProteinCount()+"\n");
        writer.write("Non-subset Protein Groups:\t"+summary.getFilteredNonSubsetProteinGroupCount()+"\n");
        writer.write("\n\n");
        
        
        // print input summary
        List<WIdPickerInputSummary> inputSummary = IdPickerResultsLoader.getIDPickerInputSummary(pinferId);
        writer.write("File\tNumHits\tNumFilteredHits\t%Filtered\n");
        int totalTargetHits = 0;
        int filteredTargetHits = 0;
        for(WIdPickerInputSummary input: inputSummary) {
            writer.write(input.getFileName()+"\t");
            writer.write(input.getNumHits()+"\t");
            writer.write(input.getNumFilteredHits()+"\t");
            writer.write(input.getPercentFilteredHits()+"%\n");
            
            totalTargetHits += input.getInput().getNumTargetHits();
            filteredTargetHits += input.getInput().getNumFilteredTargetHits();
        }
        writer.write("TOTAL\t");
        writer.write(totalTargetHits+"\t");
        writer.write(filteredTargetHits+"\t");
        writer.write(RoundingUtils.getInstance().roundTwo((filteredTargetHits*100.0)/(double)totalTargetHits)+"%\n");
        writer.write("\n\n");
        
        if(filterForm.isDownloadGOAnnotations()) {
        	GOAnnotationsWriter annotWriter = new GOAnnotationsWriter();
        	try {
        		if(!filterForm.isCollapseGroups())
        			annotWriter.writeIndividualProteins(writer, pinferId, proteinIds, filterCriteria.getGoFilterCriteria());
        		else
        			annotWriter.writeGroupProteins(writer, pinferId, proteinIds, filterCriteria.getGoFilterCriteria());
			} catch (Exception e) {
				log.error("Error writing GO Annotations", e);
				writer.write("\n\nERROR!  THERE WAS AN ERROR WRITING GO ANNOTATIONS.\nError Message: "+e.getMessage());
			}
        }
        else {
        	if(!filterForm.isCollapseGroups()) {
        		// print each protein
        		printIndividualProteins(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides(), 
        				filterForm.isPrintDescriptions(), filterForm.getDisplayColumns());
        	}
        	else {
        		// user wants to see only one line for each protein group; all members of the group will be displayed comma-separated
        		printCollapsedProteinGroups(writer, pinferId, peptideDef, proteinIds, filterForm.isPrintPeptides(), 
        				filterForm.isPrintDescriptions(), filterForm.getDisplayColumns());
        	}
        }
    }

    private void printIndividualProteins(PrintWriter writer, int pinferId,
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides, boolean printDescriptions,
            DisplayColumns displayColumns) {
    	
    	if(displayColumns.getShowGroupId())
    		writer.write("ProteinGroupID\t");
    	if(displayColumns.getShowFastaId() || displayColumns.getShowCommonName()) {
    		writer.write("Parsimonious\t");
    		writer.write("Subset\t");
    	}
    	if(displayColumns.getShowFastaId())
    		writer.write("FastaID\t");
    	if(displayColumns.getShowCommonName())
    		writer.write("CommonName\t");
    	if(displayColumns.getShowMolWt())
        	writer.write("Mol.Wt\t");
        if(displayColumns.getShowPi())
        	writer.write("pI\t");
        if(displayColumns.getShowPhiliusAnnotations()) {
        	writer.write("TM\tSP\t");
        }
    	if(displayColumns.getShowCoverage())
    		writer.write("Coverage\t");
    	if(displayColumns.getShowNsaf())
    		writer.write("NSAF\t");
        if(displayColumns.getShowYeastCopiesPerCell())
        	writer.write("#Copies/Cell\t");
        if(displayColumns.getShowNumPept())
        	writer.write("NumPeptides\t");
        if(displayColumns.getShowNumUniqPept())
        	writer.write("NumUniquePeptides\t");
        if(displayColumns.getShowNumSpectra())
    		writer.write("NumSpectra\t");
        
        
        if(printPeptides)
            writer.write("Peptides\t");
        if(printDescriptions)
            writer.write("Description\t");
        writer.write("\n");

        // Only for yeast
        ProteinAbundanceDao adundanceDao = ProteinAbundanceDao.getInstance();
        
        // For Philius results
        PhiliusResultDAO philiusDao = PhiliusDAOFactory.getInstance().getPhiliusResultDAO();
        
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        boolean getPhiliusResults = ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(pinferId);
        
        for(int i = 0; i < proteinIds.size(); i++) {
            int proteinId = proteinIds.get(i);
            WIdPickerProtein wProt = IdPickerResultsLoader.getIdPickerProtein(proteinId, peptideDef, fastaDatabaseIds, getPhiliusResults);
            
            if(displayColumns.getShowGroupId())
            	writer.write(wProt.getProtein().getProteinGroupLabel()+"\t");
            
            if(displayColumns.getShowFastaId() || displayColumns.getShowCommonName()) {
            	if(wProt.getProtein().getIsParsimonious())
            		writer.write("P\t");
            	else
            		writer.write("\t");
            	
            	if(wProt.getProtein().getIsSubset())
            		writer.write("S\t");
            	else
            		writer.write("\t");
            }
            
            if(displayColumns.getShowFastaId()) {
            	try {
            		writer.write(wProt.getAccessionsCommaSeparated()+"\t");
            	} catch (SQLException e) {
            		log.error("Error getting accessions", e);
            		writer.write("ERROR\t");
            	}
            }
            
            if(displayColumns.getShowCommonName()) {
            	try {
            		writer.write(wProt.getCommonNamesCommaSeparated()+"\t");
            	} catch (SQLException e) {
            		log.error("Error getting common names", e);
            		writer.write("ERROR\t");
            	}
            }
            
            if(displayColumns.getShowMolWt())
            	writer.write(wProt.getMolecularWeight()+"\t");
            if(displayColumns.getShowPi())
            	writer.write(wProt.getPi()+"\t");
            
            
            if(displayColumns.getShowPhiliusAnnotations()) {
            	int sequenceId = wProt.getProteinListing().getSequenceId();
            	PhiliusResult philiusresult = philiusDao.loadForSequence(sequenceId);
            	if(philiusresult != null) {
            		if(philiusresult.isTransMembrane())
            			writer.write("TM\t");
            		else
            			writer.write("-\t");
            		
            		if(philiusresult.isSignalPeptide())
            			writer.write("SP\t");
            		else
            			writer.write("-\t");
            	}
            }


            if(displayColumns.getShowCoverage())
            	writer.write(wProt.getProtein().getCoverage()+"\t");
            
            if(displayColumns.getShowNsaf())
            	writer.write(wProt.getProtein().getNsafFormatted()+"\t");
            
            if(displayColumns.getShowYeastCopiesPerCell()) {
            	try {
					List<YeastOrfAbundance> abundances = adundanceDao.getAbundance(wProt.getProtein().getNrseqProteinId());
					if(abundances == null || abundances.size() == 0) {
						writer.write("NOT_AVAILABLE\t");
					}
					else if(abundances.size() == 1) {
						writer.write(abundances.get(0).getAbundanceToPrint()+"\t");
					}
					else {
						
						boolean allUnknown = true;
				    	for(YeastOrfAbundance oa: abundances) {
				    		if(!oa.isAbundanceNull()) {
				    			allUnknown = false;
				    			break;
				    		}
				    	}
				    	if(allUnknown) {
				    		writer.write("NOT_DETECTED\t");
				    	}
				    	else {
				    		List<String> toPrint = new ArrayList<String>(abundances.size());
				    		for(YeastOrfAbundance abundance: abundances) {
				    			toPrint.add(abundance.getOrfName()+":"+abundance.getAbundanceToPrint());
				    		}
				    		writer.write(StringUtils.makeCommaSeparated(toPrint)+"\t");
				    	}
					}
				} catch (SQLException e) {
					log.error("Exception getting protein copies / cell for protein: "+wProt.getProtein().getNrseqProteinId(), e);
					writer.write("ERROR\t");
				}
            }
            
            if(displayColumns.getShowNumPept())
            	writer.write(wProt.getProtein().getPeptideCount()+"\t");
            if(displayColumns.getShowNumUniqPept())
            	writer.write(wProt.getProtein().getUniquePeptideCount()+"\t");
            if(displayColumns.getShowNumSpectra())
            	writer.write(wProt.getProtein().getSpectrumCount()+"\t");
            
            
            
            if(printPeptides) {
                writer.write("\t"+getPeptides(proteinId));
            }
            if(printDescriptions) {
            	try {
					ProteinReference ref = wProt.getOneDescriptionReference();
					if(ref != null)
						writer.write("\t"+wProt.getOneDescriptionReference().getDescription());
				} catch (SQLException e) {
					log.error("Error getting description", e);
					writer.write("\tERROR");
				}
            }
            writer.write("\n");
        }
    }

    private String getPeptides(int proteinId) {
    	
    	IdPickerProtein protein = ProteinferDAOFactory.instance().getIdPickerProteinDao().loadProtein(proteinId);
    	
    	List<WIdPickerIon> ionList = IdPickerResultsLoader.getPeptideIonsForProtein(protein.getProteinferId(), 
    			proteinId);
    	
    	Set<String> uniqModifiedPeptides = new HashSet<String>();
    	for(WIdPickerIon ion: ionList) {
    		uniqModifiedPeptides.add(ion.getIonSequence());
    	}
    	
        // ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
        // List<ProteinferPeptide> peptides = peptDao.loadPeptidesForProteinferProtein(proteinId);
    	
        StringBuilder buf = new StringBuilder();
//        for(ProteinferPeptide pept: peptides) {
//            buf.append(","+pept.getSequence());
//        }
        
        for(String seq: uniqModifiedPeptides) {
        	buf.append(","+seq);
        }
        if(buf.length() > 0)
            buf.deleteCharAt(0); // remove first comma
        return buf.toString();
    }

    private void printCollapsedProteinGroups(PrintWriter writer, int pinferId,
            PeptideDefinition peptideDef, List<Integer> proteinIds, boolean printPeptides, boolean printDescriptions, 
            DisplayColumns displayColumns) {
    	
    	
    	
    	if(displayColumns.getShowGroupId())
    		writer.write("ProteinGroupID\t");
    	if(displayColumns.getShowFastaId() || displayColumns.getShowCommonName()) {
    		writer.write("Parsimonious\t");
    		writer.write("Subset\t");
    	}
    	if(displayColumns.getShowFastaId())
    		writer.write("FastaID\t");
    	if(displayColumns.getShowCommonName())
    		writer.write("CommonName\t");
    	if(displayColumns.getShowMolWt())
        	writer.write("Mol.Wt\t");
        if(displayColumns.getShowPi())
        	writer.write("pI\t");
        if(displayColumns.getShowPhiliusAnnotations()) {
        	writer.write("TM\tSP\t");
        }
    	if(displayColumns.getShowCoverage())
    		writer.write("Coverage\t");
    	if(displayColumns.getShowNsaf())
    		writer.write("NSAF\t");
        if(displayColumns.getShowYeastCopiesPerCell())
        	writer.write("#Copies/Cell\t");
        if(displayColumns.getShowNumPept())
        	writer.write("NumPeptides\t");
        if(displayColumns.getShowNumUniqPept())
        	writer.write("NumUniquePeptides\t");
        if(displayColumns.getShowNumSpectra())
    		writer.write("NumSpectra\t");
        
        if(printPeptides)
            writer.write("Peptides\t");
        if(printDescriptions)
            writer.write("Description\t");
        writer.write("\n");
        
        int currentGroupLabel = -1;
        boolean parsimonious = false;
        boolean subset = false;
        String fastaIds = "";
        String commonNames = "";
        String descStr = "";
        String coverageStr = "";
        String NsafStr = "";
        String molWtStr = "";
        String piStr = "";
        String tmStr = "";
        String spStr = "";
        String yeastAbundanceStr = "";
        String peptides = "";
        int spectrumCount = 0;
        int numPept = 0;
        int numUniqPept = 0;
        
        List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
        boolean getPhiliusResults = ProteinInferPhiliusResultChecker.getInstance().hasPhiliusResults(pinferId);
        PhiliusResultDAO philiusDao = PhiliusDAOFactory.getInstance().getPhiliusResultDAO();
        
        // Only for yeast
        ProteinAbundanceDao adundanceDao = ProteinAbundanceDao.getInstance();
        
        for(int i = 0; i < proteinIds.size();  i++) {
        	
            int proteinId = proteinIds.get(i);
            WIdPickerProtein wProt = IdPickerResultsLoader.getIdPickerProtein(proteinId, peptideDef, fastaDatabaseIds, getPhiliusResults);
            
            if(wProt.getProtein().getProteinGroupLabel() != currentGroupLabel) {
                if(currentGroupLabel != -1) {
                	
                	if(displayColumns.getShowGroupId())
                		writer.write(currentGroupLabel+"\t");
                	
                	if(displayColumns.getShowFastaId() || displayColumns.getShowCommonName()) {
                		if(parsimonious)
                			writer.write("P\t");
                		else
                			writer.write("\t");
                		
                		if(subset)
                			writer.write("S\t");
                		else
                			writer.write("\t");
                		
                	}
                	
                	// Fasta ID
                	if(displayColumns.getShowFastaId()) {
                		if(fastaIds.length() > 0)
                			fastaIds = fastaIds.substring(1); // remove first comma
                		writer.write(fastaIds+"\t");
                	}
                	
                	// Common names
                	if(displayColumns.getShowCommonName()) {
                		if(commonNames.length() > 0)
                			commonNames = commonNames.substring(1);
                		writer.write(commonNames+"\t");
                	}
                	
                	 // Molecular weights
                	if(displayColumns.getShowMolWt()) {
                		if(molWtStr.length() > 0)
                			molWtStr = molWtStr.substring(1);
                		writer.write(molWtStr+"\t");
                	}
                    
                    // pIs
                	if(displayColumns.getShowPi()) {
                		if(piStr.length() > 0)
                			piStr = piStr.substring(1);
                		writer.write(piStr+"\t");
                	}
                	
                	// Philius Annotations
                	if(displayColumns.getShowPhiliusAnnotations()) {
                		
                		if(tmStr.length() > 0)
                			tmStr = tmStr.substring(1);
                		writer.write(tmStr+"\t");
                		
                		if(spStr.length() > 0)
                			spStr = spStr.substring(1);
                		writer.write(spStr+"\t");
                	}
                	
                    // Coverages
                	if(displayColumns.getShowCoverage()) {
                		if(coverageStr.length() > 0)
                			coverageStr = coverageStr.substring(1);
                		writer.write(coverageStr+"\t");
                	}
                    
                	// NSAFs
                	if(displayColumns.getShowNsaf()) {
                		if(NsafStr.length() > 0)
                			NsafStr = NsafStr.substring(1);
                		writer.write(NsafStr+"\t");
                	}
                	
                	// Yeast Abundance -- copies / cell
                	if(displayColumns.getShowYeastCopiesPerCell()) {
                		if(yeastAbundanceStr.length() > 0)
                			yeastAbundanceStr = yeastAbundanceStr.substring(1);
                		writer.write(yeastAbundanceStr+"\t");
                	}
                	
                	// # Peptides
                	if(displayColumns.getShowNumPept())
                		writer.write(numPept+"\t");
                	// # Unique peptides
                	if(displayColumns.getShowNumUniqPept())
                		writer.write(numUniqPept+"\t");
                	// Spectrum Count
                	if(displayColumns.getShowNumSpectra())
                		writer.write(spectrumCount+"\t");
                    
                   
                    
                    if(printPeptides)
                        writer.write("\t"+peptides);
                    
                    if(printDescriptions) {
                        if(descStr.length() > 0)
                            descStr = descStr.substring(1);
                        writer.write("\t"+descStr);
                    }
                    
                    writer.write("\n");
                }
                fastaIds = "";
                peptides = "";
                commonNames = "";
                descStr = "";
                coverageStr = "";
                NsafStr = "";
                molWtStr = "";
                piStr = "";
                tmStr = "";
                spStr = "";
                yeastAbundanceStr = "";
                currentGroupLabel = wProt.getProtein().getProteinGroupLabel();
                parsimonious = wProt.getProtein().getIsParsimonious();
                subset = wProt.getProtein().getIsSubset();
                spectrumCount = wProt.getProtein().getSpectrumCount();
                numPept = wProt.getProtein().getPeptideCount();
                numUniqPept = wProt.getProtein().getUniquePeptideCount();
                if(printPeptides) {
                    peptides = getPeptides(proteinId);
                }
            }
            
            if(displayColumns.getShowFastaId()) {
            	try {
            		fastaIds += ";"+wProt.getAccessionsCommaSeparated();
            	} catch (SQLException e) {
            		log.error("Error getting accessions", e);
            		fastaIds += ",ERROR";
            	}
            }
            
            if(displayColumns.getShowCommonName()) {
            	try {
            		String cn = wProt.getCommonNamesCommaSeparated();
            		if(cn.trim().length() > 0)
            			commonNames += ";"+cn;
            	} catch (SQLException e) {
            		log.error("Error getting common names", e);
            		commonNames += ",ERROR";
            	}
            }
			
            if(printDescriptions) {
            	try {
            		ProteinReference ref = wProt.getOneDescriptionReference();
            		if(ref != null)
            			descStr += ", "+wProt.getOneDescriptionReference().getDescription();
            	} catch (SQLException e) {
            		log.error("Error getting description", e);
            		descStr += ", ERROR";
            	}
            }
            
            if(displayColumns.getShowCoverage())
            	coverageStr += ","+wProt.getProtein().getCoverage()+"%";
            if(displayColumns.getShowNsaf())
            	NsafStr += ","+wProt.getProtein().getNsafFormatted();
            if(displayColumns.getShowMolWt())
            	molWtStr += ","+wProt.getMolecularWeight();
            if(displayColumns.getShowPi())
            	piStr += ","+wProt.getPi();
            
            if(displayColumns.getShowYeastCopiesPerCell()) {
            	
            	try {
					List<YeastOrfAbundance> abundances = adundanceDao.getAbundance(wProt.getProtein().getNrseqProteinId());
					if(abundances == null || abundances.size() == 0) {
						yeastAbundanceStr += ";NOT_AVAILABLE";
					}
					else if(abundances.size() == 1) {
						yeastAbundanceStr += ";"+abundances.get(0).getAbundanceToPrint();
					}
					else {
						
						boolean allUnknown = true;
				    	for(YeastOrfAbundance oa: abundances) {
				    		if(!oa.isAbundanceNull()) {
				    			allUnknown = false;
				    			break;
				    		}
				    	}
				    	if(allUnknown) {
				    		yeastAbundanceStr += ";NOT_DETECTED";
				    	}
				    	else {
				    		List<String> toPrint = new ArrayList<String>(abundances.size());
				    		for(YeastOrfAbundance abundance: abundances) {
				    			toPrint.add(abundance.getOrfName()+":"+abundance.getAbundanceToPrint());
				    		}
				    		yeastAbundanceStr += ";"+StringUtils.makeCommaSeparated(toPrint);
				    	}
					}
				} catch (SQLException e) {
					log.error("Exception getting protein copies / cell for protein: "+wProt.getProtein().getNrseqProteinId(), e);
					yeastAbundanceStr += ";ERROR";
				}
            }
            
            if(displayColumns.getShowPhiliusAnnotations()) {
            	
            	int sequenceId = wProt.getProteinListing().getSequenceId();
            	PhiliusResult philiusresult = philiusDao.loadForSequence(sequenceId);
            	if(philiusresult != null) {
            		if(philiusresult.isTransMembrane())
            			tmStr += ",TM";
            		else
            			tmStr += ",-";
            		
            		if(philiusresult.isSignalPeptide())
            			spStr += ",SP";
            		else
            			spStr += ",-";
            	}
            }
        }
        
        // write the last one
        if(displayColumns.getShowGroupId())
    		writer.write(currentGroupLabel+"\t");
    	
    	if(displayColumns.getShowFastaId() || displayColumns.getShowCommonName()) {
    		if(parsimonious)
    			writer.write("P\t");
    		else
    			writer.write("\t");
    		
    		if(subset)
    			writer.write("S\t");
    		else
    			writer.write("\t");
    	}
    	
    	// Fasta ID
    	if(displayColumns.getShowFastaId()) {
    		if(fastaIds.length() > 0)
    			fastaIds = fastaIds.substring(1); // remove first comma
    		writer.write(fastaIds+"\t");
    	}
    	
    	// Common names
    	if(displayColumns.getShowCommonName()) {
    		if(commonNames.length() > 0)
    			commonNames = commonNames.substring(1);
    		writer.write(commonNames+"\t");
    	}
    	
    	 // Molecular weights
    	if(displayColumns.getShowMolWt()) {
    		if(molWtStr.length() > 0)
    			molWtStr = molWtStr.substring(1);
    		writer.write(molWtStr+"\t");
    	}
        
        // pIs
    	if(displayColumns.getShowPi()) {
    		if(piStr.length() > 0)
    			piStr = piStr.substring(1);
    		writer.write(piStr+"\t");
    	}
    	
    	// Philius Annotations
    	if(displayColumns.getShowPhiliusAnnotations()) {
    		
    		if(tmStr.length() > 0)
    			tmStr = tmStr.substring(1);
    		writer.write(tmStr+"\t");
    		
    		if(spStr.length() > 0)
    			spStr = spStr.substring(1);
    		writer.write(spStr+"\t");
    	}
    	
        // Coverages
    	if(displayColumns.getShowCoverage()) {
    		if(coverageStr.length() > 0)
    			coverageStr = coverageStr.substring(1);
    		writer.write(coverageStr+"\t");
    	}
        
    	// NSAFs
    	if(displayColumns.getShowNsaf()) {
    		if(NsafStr.length() > 0)
    			NsafStr = NsafStr.substring(1);
    		writer.write(NsafStr+"\t");
    	}
    	
    	// Yeast Abundance -- copies / cell
    	if(displayColumns.getShowYeastCopiesPerCell()) {
    		if(yeastAbundanceStr.length() > 0)
    			yeastAbundanceStr = yeastAbundanceStr.substring(1);
    		writer.write(yeastAbundanceStr+"\t");
    	}
    	
    	// # Peptides
    	if(displayColumns.getShowNumPept())
    		writer.write(numPept+"\t");
    	// # Unique peptides
    	if(displayColumns.getShowNumUniqPept())
    		writer.write(numUniqPept+"\t");
    	// Sectrum Count
    	if(displayColumns.getShowNumSpectra())
    		writer.write(spectrumCount+"\t");
        
       
        
        if(printPeptides)
            writer.write("\t"+peptides);
        
        if(printDescriptions) {
            if(descStr.length() > 0)
                descStr = descStr.substring(1);
            writer.write("\t"+descStr);
        }
        
        writer.write("\n");
    }
}
