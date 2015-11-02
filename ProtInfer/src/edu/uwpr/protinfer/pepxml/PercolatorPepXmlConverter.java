/**
 * PercolatorPepXmlConverter.java
 * @author Vagisha Sharma
 * Feb 3, 2009
 * @version 1.0
 */
package edu.uwpr.protinfer.pepxml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.analysis.MsRunSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.MsSearchAnalysisDAO;
import org.yeastrc.ms.dao.analysis.percolator.PercolatorResultDAO;
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.domain.analysis.MsRunSearchAnalysis;
import org.yeastrc.ms.domain.analysis.MsSearchAnalysis;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;

/**
 * 
 */
public class PercolatorPepXmlConverter extends PepXmlConverter<PercolatorResult> {

    
    private static final DAOFactory daofactory = DAOFactory.instance();
    private static final MsSearchAnalysisDAO analysisDao = DAOFactory.instance().getMsSearchAnalysisDAO();
    private static final MsRunSearchAnalysisDAO rsaDao = DAOFactory.instance().getMsRunSearchAnalysisDAO();
    private static final SQTRunSearchDAO runSearchDao = daofactory.getSqtRunSearchDAO();
    private static final SequestSearchDAO seqSearchDao = daofactory.getSequestSearchDAO();
    private static final SequestSearchResultDAO seqResDao = daofactory.getSequestResultDAO();
    private static final PercolatorResultDAO resultDao = daofactory.getPercolatorResultDAO();
    
    private static final Logger log = Logger.getLogger(PercolatorPepXmlConverter.class);
    
    
    public void convertSearchAnalysis(int searchAnalysisId, String outfile) {
        
        XMLStreamWriter writer = null;
        try {
            writer = initDocument(outfile);
            startMsmsPipelineAnalysis(writer, outfile);
            
            // Write analysis_summary element
            writeAnalysisSummary(searchAnalysisId, writer);
            
            List<Integer> runSearchAnalysisIds = rsaDao.getRunSearchAnalysisIdsForAnalysis(searchAnalysisId);
            for(int runSearchAnalysisId: runSearchAnalysisIds) {
                
                MsRunSearchAnalysis rsa = rsaDao.load(runSearchAnalysisId);
                writeRunSearchAnalysis(rsa, writer);
            }
            endMsmsPipelineAnalysis(writer);
        }
        catch (FileNotFoundException e) {
            log.error("", e);
        }
        catch (XMLStreamException e) {
            log.error("",e);
        }
        finally {
            if(writer != null) try {
                writer.close();
            }
            catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void convertRunSearchAnalyses(List<Integer> runSearchAnalysisIds, String outfile) {
        
        XMLStreamWriter writer = null;
        try {
            writer = initDocument(outfile);
            startMsmsPipelineAnalysis(writer, outfile);
            
            // Write analysis_summary element
            // TODO make sure all IDs come from the same percolator analysis
            MsRunSearchAnalysis rsa = rsaDao.load(runSearchAnalysisIds.get(0));
            writeAnalysisSummary(rsa.getAnalysisId(), writer);
            
            for(int runSearchAnalysisId: runSearchAnalysisIds) {
                
                rsa = rsaDao.load(runSearchAnalysisId);
                writeRunSearchAnalysis(rsa, writer);
            }
            endMsmsPipelineAnalysis(writer);
        }
        catch (FileNotFoundException e) {
            log.error("", e);
        }
        catch (XMLStreamException e) {
            log.error("",e);
        }
        finally {
            if(writer != null) try {
                writer.close();
            }
            catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void convertRunSearcAnaysis(int runSearchAnalysisId, String outdir) {
        
        String outfile = rsaDao.loadFilenameForRunSearchAnalysis(runSearchAnalysisId)+".pep.xml";
        outfile = outdir+File.separator+outfile;
        
        XMLStreamWriter writer = null;
        try {
            writer = initDocument(outfile);
            startMsmsPipelineAnalysis(writer, outfile);
            
            MsRunSearchAnalysis rsa = rsaDao.load(runSearchAnalysisId);
            
            // Write analysis_summary element
            writeAnalysisSummary(rsa.getAnalysisId(), writer);
            
            writeRunSearchAnalysis(rsa, writer);
            
            endMsmsPipelineAnalysis(writer);
        }
        catch (FileNotFoundException e) {
            log.error("", e);
        }
        catch (XMLStreamException e) {
            log.error("",e);
        }
        finally {
            if(writer != null) try {
                writer.close();
            }
            catch (XMLStreamException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeRunSearchAnalysis(MsRunSearchAnalysis rsa,
            XMLStreamWriter writer) throws XMLStreamException {
        
        // NOTE: We assume that Percolator is run on Sequest results 
        int runSearchId = rsa.getRunSearchId();
        
        SQTRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        if(runSearch == null) {
            log.error("RunSearch with ID "+rsa.getRunSearchId()+" not found!");
            throw new IllegalArgumentException("RunSearch with ID "+runSearchId+" not found!");
        }
        
        SequestSearch search = seqSearchDao.loadSearch(runSearch.getSearchId());
        if(search == null) {
            log.error("Search with ID "+runSearch.getSearchId()+" not found!");
            throw new IllegalArgumentException("Search with ID "+runSearch.getSearchId()+" not foune!");
        }
        List<MsResidueModification> staticMods = search.getStaticResidueMods();
        
        MsRun run = runDao.loadRun(runSearchId);
        if(run == null) {
            log.error("No run found with ID: "+runSearchId);
            throw new IllegalArgumentException("No run found with ID: "+runSearchId);
        }
        startMsmsRunSummary(run, writer);
        writeEnzymes(search, writer);
        String basefile = run.getFileName();
        
        // write search summary; "search_summary" element
        writeSearchSummary(search, writer, basefile);
        
        // write the search results; "spectrum_query" elements.
        writeSearchResults(rsa, runSearch.getRunId(), staticMods, writer, basefile);
        
        endMsmsRunSummary(writer);
    }
    
    
    private void writeAnalysisSummary(int analysisId, XMLStreamWriter writer) throws XMLStreamException {
        MsSearchAnalysis analysis = analysisDao.load(analysisId);
        if(analysis == null) {
            log.error("Percolator analysis with ID: "+analysisId+" not found");
            throw new IllegalArgumentException("Percolator analysis with ID: "+analysisId+" not found");
        }
        writer.writeStartElement("analysis_summary");
        writer.writeAttribute("analysis", "Percolator");
        writer.writeAttribute("time", getXMLDate(analysis.getUploadDate()));
        
        // write the percolator parameters
        List<PercolatorParam> params = DAOFactory.instance().getPercoltorParamsDAO().loadParams(analysisId);
        for(PercolatorParam param: params) {
            writer.writeStartElement("percolator_parameter");
            writer.writeAttribute("name", param.getParamName());
            writer.writeAttribute("value", param.getParamValue());
            writer.writeEndElement();
            newLine(writer);
        }
        
        writer.writeEndElement();
        newLine(writer);
    }

    
    private void writeSearchResults(MsRunSearchAnalysis rsa, int runId, List<MsResidueModification> staticMods, XMLStreamWriter writer, String basefile) 
            throws XMLStreamException {
        
        // get all the scanIds for the run
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runId);
        // sort the scanIds
        Collections.sort(scanIds);
        
        // get a list of all the resultIds for this run search.
        List<Integer> percResultIds = resultDao.loadIdsForRunSearchAnalysis(rsa.getId());
        // sort the resultIds
        Collections.sort(percResultIds);
        
        // get all the results one scanId at a time; group them by charge and write out to xml
        for(int scanId: scanIds) {
            List<PercolatorResult> results = getResultsFor(rsa.getId(), scanId);
            // remove the result ids returned by the function above from our sorted list of 
            // all result ids for this runSearch.  
            for(PercolatorResult res: results) {
                int idx = Collections.binarySearch(percResultIds, res.getId());
                if(idx >= 0)    percResultIds.remove(idx);
            }
            writeResultsForScan(results, staticMods, basefile, writer);
        }
        
        // at the end the resultIds list should be empty.  If not print an error
        if(percResultIds.size() > 0) {
            log.error("Did not print all resultIds");
        }
    }
    
    private List<PercolatorResult> getResultsFor(int runSearchAnalysisId, int scanId) {
        List<Integer> resultIds = resultDao.loadIdsForRunSearchAnalysisScan(runSearchAnalysisId, scanId);
        List<PercolatorResult> results = new ArrayList<PercolatorResult>(resultIds.size());
        for(int percResultId: resultIds) {
            results.add(resultDao.loadForPercolatorResultId(percResultId));
        }
        return results;
    }
    
    MassType getPrecursorMassType(int searchId) {
        return seqSearchDao.getParentMassType(searchId);
    }
    
    MassType getFragmentMassType(int searchId){
        return seqSearchDao.getFragmentMassType(searchId);
    }

    @Override
    String getMaxNumInternalClevages(int searchId) {
        return seqSearchDao.getSearchParamValue(searchId, "max_num_internal_cleavage_sites");
    }

    @Override
    String getMinNumTermini(int searchId) {
        return seqSearchDao.getSearchParamValue(searchId, "num_enzyme_termini");
    }

    @Override
    void writeProgramSpecificParams(int searchId, XMLStreamWriter writer) throws XMLStreamException {
        SequestSearch search = seqSearchDao.loadSearch(searchId);
        List<Param> params = search.getSequestParams();
        // TODO do we really need to write this out
        for(Param param: params) {
            writer.writeStartElement("parameter");
            writer.writeAttribute("name", param.getParamName());
            writer.writeAttribute("value", param.getParamValue());
            newLine(writer);
            writer.writeEndElement();
            newLine(writer);
        }
    }
    
    @Override
    void sortResultsByRank(List<PercolatorResult> results) {
       Collections.sort(results, new Comparator<PercolatorResult>() {
        @Override
        public int compare(PercolatorResult o1, PercolatorResult o2) {
            return Double.valueOf(o1.getQvalue()).compareTo(o2.getQvalue());
        }});
    }

    @Override
    int getResultRankInList(List<PercolatorResult> resultList,
            PercolatorResult result) {
        sortResultsByRank(resultList);
        for(int i = 0; i < resultList.size(); i++) {
            if(result.getId() == resultList.get(i).getId())
                return i+1;
        }
        return -1;
    }

    @Override
    void writeScores(PercolatorResult result, XMLStreamWriter writer)
            throws XMLStreamException {
        
        writer.writeStartElement("analysis_result");
        writer.writeAttribute("analysis", "Percolator");
        
        // write all the scores for this result
        writer.writeStartElement("percolator_score");
        writer.writeAttribute("name", "qvalue");
        writer.writeAttribute("value", String.valueOf(result.getQvalue()));
        writer.writeEndElement();
        newLine(writer);
        
        if(result.getPosteriorErrorProbability() != -1.0) {
            writer.writeCharacters("\t\t");
            writer.writeStartElement("percolator_score");
            writer.writeAttribute("name", "deltacn");
            writer.writeAttribute("value", String.valueOf(result.getPosteriorErrorProbability()));
            writer.writeEndElement();
            newLine(writer);
        }
        
        if(result.getDiscriminantScore() != null) {
            writer.writeCharacters("\t\t");
            writer.writeStartElement("percolator_score");
            writer.writeAttribute("name", "spscore");
            writer.writeAttribute("value", result.getDiscriminantScore().toString());
            writer.writeEndElement();
            newLine(writer);
        }
        
        writer.writeEndElement();
        newLine(writer);
    }

    @Override
    double getCalculatedPeptideMassPlusH(PercolatorResult result) {
        // NOTE: Assuming Percolator was run on Sequest data.
        SequestSearchResult seqRes = seqResDao.load(result.getId());
        return seqRes.getSequestResultData().getCalculatedMass().doubleValue();
    }
}
