/**
 *
 */
package org.yeastrc.www.project.experiment;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.experiment.PercolatorResultPlus;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorPeptideResultDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanDAO;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.ms.dao.search.MsSearchDAO;
import org.yeastrc.ms.dao.search.prolucid.ProlucidSearchResultDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultFilterCriteria;
import org.yeastrc.ms.domain.run.MsScan;
import org.yeastrc.ms.domain.run.ms2file.MS2Scan;
import org.yeastrc.ms.domain.search.MsSearchResultProtein;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.util.RoundingUtils;

/**
 * DownloadPercolatorResults.java
 * @author Vagisha Sharma
 * Jun 10, 2010
 *
 */
public class DownloadPercolatorResults extends Action {

	private static final Logger log = Logger.getLogger(DownloadPercolatorResults.class.getName());

    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {


    	Integer analysisId = (Integer) request.getAttribute("analysisId");
    	List<Integer> resultIds = (List<Integer>) request.getAttribute("analysisResultIds");
    	Program program = (Program) request.getAttribute("analysisProgram");
    	PercolatorFilterResultsForm myForm = (PercolatorFilterResultsForm) request.getAttribute("filterForm");

    	long s = System.currentTimeMillis();
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition","attachment; filename=\"Analysis_"+analysisId+".txt\"");
        response.setHeader("cache-control", "no-cache");
        PrintWriter writer = response.getWriter();
        writer.write("Date: "+new Date()+"\n\n");
        if(myForm.isPeptideResults())
        	writePeptideResults(writer, analysisId, resultIds, program, myForm);
        else
        	writePsmResults(writer, analysisId, resultIds, program, myForm);
        writer.close();
        long e = System.currentTimeMillis();
        log.info("DownloadAnalysisResults results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
        return null;
    }

    private void writePsmResults(PrintWriter writer, int analysisId, List<Integer> resultIds, Program analysisProgram, AnalysisFilterResultsForm myForm) {

    	if(analysisProgram == Program.PERCOLATOR) {
            writePercolatorPsmResults(writer, analysisId, analysisProgram, resultIds, ((PercolatorFilterResultsForm)myForm));
        }
    	else {
    		log.error("Unrecognized analysis program: "+analysisProgram);
    		writer.write("Unrecognized analysis program: "+analysisProgram);
    	}
    }

    private void writePeptideResults(PrintWriter writer, int analysisId, List<Integer> resultIds, Program analysisProgram, AnalysisFilterResultsForm myForm) {

    	if(analysisProgram == Program.PERCOLATOR) {
            writePercolatorPeptideResults(writer, analysisId, analysisProgram, resultIds, ((PercolatorFilterResultsForm)myForm));
        }
    	else {
    		log.error("Unrecognized analysis program: "+analysisProgram);
    		writer.write("Unrecognized analysis program: "+analysisProgram);
    	}
    }

	private void writePercolatorPsmResults(PrintWriter writer,int analysisId, Program analysisProgram, List<Integer> resultIds,
			PercolatorFilterResultsForm myForm) {

		PercolatorResultFilterCriteria filterCriteria = myForm.getFilterCriteria();
		writeFilters(writer, filterCriteria, myForm.isPeptideResults());
		writer.write("\n\n");

		// Get the names of the files
		Map<Integer, String> filenameMap = getFileNames(analysisId);

		// Do we have Bullseye results for the searched files
        boolean hasBullsEyeArea = false;
        List<Integer> searchIds = DAOFactory.instance().getMsSearchAnalysisDAO().getSearchIdsForAnalysis(analysisId);
        MsRunSearchDAO rsDao = DAOFactory.instance().getMsRunSearchDAO();
        MS2RunDAO runDao = DAOFactory.instance().getMS2FileRunDAO();
        List<Integer> runSearchIds = rsDao.loadRunSearchIdsForSearch(searchIds.get(0));
        for(int runSearchId: runSearchIds) {
            int runId = rsDao.loadRunSearch(runSearchId).getRunId();
            if(runDao.isGeneratedByBullseye(runId)) {
                hasBullsEyeArea = true;
                break;
            }
        }

        MsSearchAnalysis analysis = DAOFactory.instance().getMsSearchAnalysisDAO().load(analysisId);
        // Which version of Percolator are we using
        String version = analysis.getAnalysisProgramVersion();
        boolean hasPEP = true;
        try {
            float vf = Float.parseFloat(version.trim());
            if(vf < 1.06)   hasPEP = false;
        }
        catch(NumberFormatException e){
            log.error("Cannot determine if this version of Percolator prints PEP. Version: "+version);
        }

        // Summary
        writer.write("SUMMARY:  ");
        writer.write("# Unfiltered Results: "+getUnfilteredResultCount(analysisProgram, analysisId));
        writer.write("# Filtered Results: "+resultIds.size());
        writer.write("\n\n\n");

        // Results
        PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
        SequestSearchResultDAO seqResDao = DAOFactory.instance().getSequestResultDAO();
        ProlucidSearchResultDAO prolucidResDao = DAOFactory.instance().getProlucidResultDAO();
        MsScanDAO scanDao = DAOFactory.instance().getMsScanDAO();
        MS2ScanDAO ms2ScanDao = DAOFactory.instance().getMS2FileScanDAO();

        PercolatorPeptideResultDAO peptResDao = DAOFactory.instance().getPercolatorPeptideResultDAO();
        boolean hasPeptideResults = false;
        if(peptResDao.peptideCountForAnalysis(analysisId) > 0) {
      	  hasPeptideResults = true;
        }

        
        
        
		boolean hasSequestData = false;
		boolean hasProlucidData = false;

        MsSearchDAO msSearchDAO = DAOFactory.instance().getMsSearchDAO();
        List<String> msSearchAnalysisProgramNamesForSearchAnalysisID =  msSearchDAO.getAnalysisProgramNamesForSearchAnalysisID( analysisId );
        
        for ( String msSearchAnalysisProgramName : msSearchAnalysisProgramNamesForSearchAnalysisID ) {
        	
        	Program msSearchAnalysisProgram = Program.instance( msSearchAnalysisProgramName );
        	
        	if ( Program.isSequest( msSearchAnalysisProgram ) ) {
        		
        		hasSequestData = true;
        	}
        	
        	if ( msSearchAnalysisProgram.equals( Program.PROLUCID ) ) {
        		
        		 hasProlucidData = true;
        	}
        }
        
        if ( ! hasProlucidData ) {
        	
        	hasSequestData = true; // Default to Sequest if not Prolucid.  Handles COMET and others
        }
        


        writePsmFileHeader(writer, hasBullsEyeArea, hasPEP, hasPeptideResults, hasSequestData, hasProlucidData);


		for(int percResultId: resultIds) {
			PercolatorResult result = presDao.loadForPercolatorResultId(percResultId);
            PercolatorResultPlus resPlus = null;

            if(hasBullsEyeArea) {
                MS2Scan scan = ms2ScanDao.loadScanLite(result.getScanId());
                resPlus = new PercolatorResultPlus(result, scan);
            }
            else {
                MsScan scan = scanDao.loadScanLite(result.getScanId());
                resPlus = new PercolatorResultPlus(result, scan);
            }

            if(hasPeptideResults) {
            	resPlus.setPeptideResult(peptResDao.load(result.getPeptideResultId()));
            }

            resPlus.setFilename(filenameMap.get(result.getRunSearchAnalysisId()));
            
            
            SequestSearchResult sequestSearchResult = seqResDao.load(result.getId());
            
            if ( sequestSearchResult != null ) {
            	
           		resPlus.setSequestData(sequestSearchResult.getSequestResultData());
           		
            } else {
           		
            	ProlucidSearchResult prolucidSearchResult = prolucidResDao.load(result.getId());
            	
            	if ( prolucidSearchResult != null ) {
            	
            		resPlus.setProlucidData(prolucidSearchResult.getProlucidResultData());
            	}
            }

            writePsmResult(resPlus, hasPEP, hasBullsEyeArea, hasPeptideResults, hasSequestData, hasProlucidData,writer);
		}

        
        
//        List<PercolatorResultPlus> percResultPlusList = new ArrayList<PercolatorResultPlus>( resultIds.size() );
//
//		for(int percResultId: resultIds) {
//			PercolatorResult result = presDao.loadForPercolatorResultId(percResultId);
//            PercolatorResultPlus resPlus = null;
//
//            if(hasBullsEyeArea) {
//                MS2Scan scan = ms2ScanDao.loadScanLite(result.getScanId());
//                resPlus = new PercolatorResultPlus(result, scan);
//            }
//            else {
//                MsScan scan = scanDao.loadScanLite(result.getScanId());
//                resPlus = new PercolatorResultPlus(result, scan);
//            }
//
//            if(hasPeptideResults) {
//            	resPlus.setPeptideResult(peptResDao.load(result.getPeptideResultId()));
//            }
//
//            resPlus.setFilename(filenameMap.get(result.getRunSearchAnalysisId()));
//            
//            
//            SequestSearchResult sequestSearchResult = seqResDao.load(result.getId());
//            
//            if ( sequestSearchResult != null ) {
//            	
//           		resPlus.setSequestData(sequestSearchResult.getSequestResultData());
//           		
//            } else {
//           		
//            	ProlucidSearchResult prolucidSearchResult = prolucidResDao.load(result.getId());
//            	
//            	if ( prolucidSearchResult != null ) {
//            	
//            		resPlus.setProlucidData(prolucidSearchResult.getProlucidResultData());
//            	}
//            }
//            
//            percResultPlusList.add( resPlus );
//		}
//		
//		boolean hasSequestData = true;
//		boolean hasProlucidData = false;
//    	
//    	boolean foundSequestData = false;
//    	boolean foundProlucidData = false;
//
//    	if ( ! percResultPlusList.isEmpty() ) {
//    		
//    		for ( PercolatorResultPlus result : percResultPlusList ) {
//
//    			if ( result.getSequestData() != null ) {
//    				foundSequestData = true;
//    			}
//    			if ( result.getProlucidData() != null ) {
//    				foundProlucidData = true;
//    			}
//    		}
//        	
//        	if ( foundSequestData ) {
//        		hasSequestData = true;
//        	} else {
//        		hasSequestData = false;
//        	}
//        	if ( foundProlucidData ) {
//        		hasProlucidData = true;
//        	} else {
//        		hasProlucidData = false;
//        	}
//    	}
//
//        writePsmFileHeader(writer, hasBullsEyeArea, hasPEP, hasPeptideResults, hasSequestData, hasProlucidData);
//
//		for( PercolatorResultPlus resPlus : percResultPlusList ) {
//
//            writePsmResult(resPlus, hasPEP, hasBullsEyeArea, hasPeptideResults, hasSequestData, hasProlucidData,writer);
//		}

	}

	private void writePercolatorPeptideResults(PrintWriter writer,int analysisId, Program analysisProgram, List<Integer> resultIds,
			PercolatorFilterResultsForm myForm) {

		PercolatorResultFilterCriteria filterCriteria = myForm.getFilterCriteria();
		writeFilters(writer, filterCriteria, myForm.isPeptideResults());
		writer.write("\n\n");

		// Get the names of the files
		Map<Integer, String> filenameMap = getFileNames(analysisId);

        // Summary
        writer.write("SUMMARY:  ");
        writer.write("# Unfiltered Results: "+getUnfilteredResultCount(analysisProgram, analysisId));
        writer.write("# Filtered Results: "+resultIds.size());
        writer.write("\n\n\n");

        // Results
        PercolatorPeptideResultDAO presDao = DAOFactory.instance().getPercolatorPeptideResultDAO();

        writer.write("Petide\t");
		writer.write("q-value\t");
    	writer.write("PEP\t");
    	writer.write("DiscriminantScore\t");
    	writer.write("pvalue\t");
    	writer.write("#PSMs\t");
    	writer.write("Protein(s)\n");

		for(int percPeptideResultId: resultIds) {
			PercolatorPeptideResult result = presDao.load(percPeptideResultId);

	        String modifiedSequence = null;
	        try {
	        	// get modified peptide of the form: K.PEP[+80]TIDE.L
	        	modifiedSequence = result.getResultPeptide().getFullModifiedPeptide(true);
	        }
	        catch (ModifiedSequenceBuilderException e) {
	            modifiedSequence = "Error building peptide sequence";
	        }

            writer.write(modifiedSequence+"\t");
            writer.write(result.getQvalueRounded3SignificantDigits() + "\t");
            writer.write(result.getPosteriorErrorProbabilityRounded3SignificantDigits()+"\t");
            writer.write(result.getDiscriminantScoreRounded3SignificantDigits()+"\t");
            writer.write(result.getPvalueRounded3SignificantDigits()+"\t");
            writer.write(result.getPsmIdList().size()+"\t");

            String proteins = "";
            for(MsSearchResultProtein protein: result.getProteinMatchList())
            	proteins += ","+protein.getAccession();

            if(proteins.length() > 0)
            	proteins = proteins.substring(1); // remove first comma
            writer.write(proteins+"\n");
		}
	}

	private void writeFilters(PrintWriter writer,
			PercolatorResultFilterCriteria filterCriteria, boolean peptideLevelFilter) {
		writer.write("FILTERS:\n");

		if(!peptideLevelFilter) {
			if(filterCriteria.getMinScan() != null)
				writer.write("Min. Scan: "+filterCriteria.getMinScan()+"\n");
			if(filterCriteria.getMaxScan() != null)
				writer.write("Max. Scan: "+filterCriteria.getMaxScan()+"\n");
			if(filterCriteria.getMinCharge() != null)
				writer.write("Min. Charge: "+filterCriteria.getMinCharge()+"\n");
			if(filterCriteria.getMaxCharge() != null)
				writer.write("Max. Charge: "+filterCriteria.getMaxCharge()+"\n");
			if (filterCriteria.getMinRetentionTime() != null)
				writer.write("Min. RT: "+filterCriteria.getMinRetentionTime()+"\n");
			if(filterCriteria.getMaxRetentionTime() != null)
				writer.write("Min. RT: "+filterCriteria.getMaxRetentionTime()+"\n");
			if (filterCriteria.getMinObservedMass() != null)
				writer.write("Min. Obs. Mass: "+filterCriteria.getMinObservedMass()+"\n");
			if(filterCriteria.getMaxObservedMass() != null)
				writer.write("Max. Obs. Mass: "+filterCriteria.getMaxObservedMass()+"\n");
		}

		if(peptideLevelFilter) {
			if (filterCriteria.getMinQValue() != null)
				writer.write("Min. q-value (Peptide): "+filterCriteria.getMinQValue()+"\n");
			if (filterCriteria.getMaxQValue() != null)
				writer.write("Max. q-value (Peptide): "+filterCriteria.getMaxQValue()+"\n");
			if(filterCriteria.getMinPep() != null)
				writer.write("Min. PEP (Peptide): "+filterCriteria.getMinPep()+"\n");
			if (filterCriteria.getMaxPep() != null)
				writer.write("Max. PEP (Peptide): "+filterCriteria.getMaxPep()+"\n");
		}
		else {
			if (filterCriteria.getMinQValue() != null)
				writer.write("Min. q-value (PSM): "+filterCriteria.getMinQValue()+"\n");
			if (filterCriteria.getMaxQValue() != null)
				writer.write("Max. q-value (PSM): "+filterCriteria.getMaxQValue()+"\n");
			if(filterCriteria.getMinPep() != null)
				writer.write("Min. PEP (PSM): "+filterCriteria.getMinPep()+"\n");
			if (filterCriteria.getMaxPep() != null)
				writer.write("Max. PEP (PSM): "+filterCriteria.getMaxPep()+"\n");
		}


		if (filterCriteria.getPeptide() != null) {
			writer.write("Filter peptide: "+filterCriteria.getPeptide());
			if(filterCriteria.getPeptide() != null && filterCriteria.getPeptide().trim().length() > 0) {
				writer.write("; Get exact match: "+filterCriteria.isShowOnlyModified());
			}
			writer.write("\n");
		}

		writer.write("Modified peptides: "+(!filterCriteria.isShowOnlyUnmodified())+"\n");
		writer.write("Un-modified peptides: "+(!filterCriteria.isShowOnlyModified())+"\n");
	}

	private void writePsmFileHeader(PrintWriter writer, boolean hasBullsEyeArea,
			boolean hasPEP, boolean hasPeptideLevelScore,
			boolean hasSequestData,
			boolean hasProlucidData) {
		
		writer.write("File\t");
        writer.write("Scan#\t");
        writer.write("Charge\t");
        writer.write("ObsMass\t");
        writer.write("RT\t");
        if(hasBullsEyeArea)
        	writer.write("Area\t");
        // qvalue at the PSM level
        writer.write("q-value(PSM)\t");

        // PEP at the PSM level
        if(hasPEP)
        	writer.write("PEP(PSM)\t");
        else
        	writer.write("DiscriminantScore\t");

        if(hasPeptideLevelScore) {
        	writer.write("q-value(Peptide)\t");
        	writer.write("PEP(Peptide)\t");
        }

        if ( hasSequestData ) {
        	writer.write("XCorrRank\t");
        	writer.write("XCorr\t");
        }
        if ( hasProlucidData ) {
        	writer.write("PrimaryScoreRank\t");
        	writer.write("PrimaryScore\t");
        }
        
        writer.write("Peptide\t");
        writer.write("Protein(s)\n");
	}

	private void writePsmResult(PercolatorResultPlus result, boolean hasPEP, boolean hasBullseyeArea, boolean hasPeptideLevelScores,
			boolean hasSequestData,
			boolean hasProlucidData,
			PrintWriter writer) {

		writer.write(result.getFilename()+"\t");
		writer.write(result.getScanNumber()+"\t");
        writer.write(result.getCharge()+"\t");
        writer.write(String.valueOf(RoundingUtils.getInstance().roundFour(result.getObservedMass()))+"\t");

        // Retention time
        BigDecimal temp = result.getRetentionTime();
        if(temp == null) {
            writer.write("\t");
        }
        else
            writer.write(RoundingUtils.getInstance().roundFour(temp)+"\t");

        // Area of the precursor ion
        if(hasBullseyeArea) {
            writer.write(String.valueOf(RoundingUtils.getInstance().roundTwo(result.getArea()))+"\t");
        }

        writer.write(result.getQvalueRounded3SignificantDigits()+"\t");
        if(hasPEP)
            writer.write(result.getPosteriorErrorProbabilityRounded3SignificantDigits()+"\t");
        else
            writer.write(result.getDiscriminantScoreRounded3SignificantDigits()+"\t");

        // Peptide-level scores
        if(hasPeptideLevelScores) {
        	writer.write(result.getPeptideQvalue()+"\t");
        	writer.write(result.getPeptidePosteriorErrorProbability()+"\t");
        }

        if ( hasSequestData ) {
    		if ( result.getSequestData() != null ) {

				// Sequest data
    			writer.write(result.getSequestData().getxCorrRank()+"\t");
    	        writer.write(RoundingUtils.getInstance().roundTwo(result.getSequestData().getxCorr())+"\t");
				
    		} else {
				// No Sequest data this row
    	        writer.write("" + "\t");
    	        writer.write("" + "\t");
    		}
        }        
        if ( hasProlucidData ) {

        	if ( result.getProlucidData() != null ) {

        		// Prolucid data
    			writer.write(result.getProlucidData().getPrimaryScoreRank()+"\t");
    	        writer.write(RoundingUtils.getInstance().roundTwo(result.getProlucidData().getPrimaryScore())+"\t");
    		} else {
				// No Prolucid data this row
    	        writer.write("" + "\t");
    	        writer.write("" + "\t");
        	}
        }
        
        

        try {
            writer.write(result.getResultPeptide().getFullModifiedPeptide()+"\t");
        }
        catch (ModifiedSequenceBuilderException e) {
        	writer.write("ERROR\t");
        }

        String proteins = "";
        for(MsSearchResultProtein protein: result.getProteinMatchList())
        	proteins += ","+protein.getAccession();

        if(proteins.length() > 0)
        	proteins = proteins.substring(1); // remove first comma
        writer.write(proteins+"\n");

	}

	// ----------------------------------------------------------------------------------------
    // FILENAMES FOR THE ANALYSIS ID
    // ----------------------------------------------------------------------------------------
    private Map<Integer, String> getFileNames(int searchAnalysisId) {

        MsRunSearchAnalysisDAO saDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
        List<Integer> runSearchAnalysisIds = saDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);

        Map<Integer, String> filenameMap = new HashMap<Integer, String>(runSearchAnalysisIds.size()*2);
        for(int runSearchAnalysisId: runSearchAnalysisIds) {
            String filename = saDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId);
            filenameMap.put(runSearchAnalysisId, filename);
        }
        return filenameMap;

    }

    // ----------------------------------------------------------------------------------------
    // UN-FILTERED RESULT COUNT
    // ----------------------------------------------------------------------------------------
    private int getUnfilteredResultCount(Program program, int searchAnalysisId) {
        // Get ALL the filtered and resultIds
    	if(program == Program.PERCOLATOR) {
            PercolatorResultDAO presDao = DAOFactory.instance().getPercolatorResultDAO();
            return presDao.numAnalysisResults(searchAnalysisId);
        }
        else {
            log.error("Unrecognized analysis program: "+program.displayName());
            return 0;
        }
    }
}
