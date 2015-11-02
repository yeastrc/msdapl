/**
 * SequestPepXmlConverter.java
 * @author Vagisha Sharma
 * Feb 2, 2009
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
import org.yeastrc.ms.dao.search.GenericSearchDAO.MassType;
import org.yeastrc.ms.dao.search.sequest.SequestSearchDAO;
import org.yeastrc.ms.dao.search.sequest.SequestSearchResultDAO;
import org.yeastrc.ms.dao.search.sqtfile.SQTRunSearchDAO;
import org.yeastrc.ms.domain.run.MsRun;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.sequest.SequestResultData;
import org.yeastrc.ms.domain.search.sequest.SequestSearch;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearch;

/**
 * 
 */
public class SequestPepXmlConverter extends PepXmlConverter<SequestSearchResult> {

    private static final DAOFactory daofactory = DAOFactory.instance();
    private static final SequestSearchDAO seqSearchDao = daofactory.getSequestSearchDAO();
    private static final SQTRunSearchDAO runSearchDao = daofactory.getSqtRunSearchDAO();
    private static final SequestSearchResultDAO resultDao = daofactory.getSequestResultDAO();
    
    private static final Logger log = Logger.getLogger(SequestPepXmlConverter.class);
    
    
    public void convertSearch(int searchId, String outfile) {
        
        
        XMLStreamWriter writer = null;
        try {
            writer = initDocument(outfile);
            startMsmsPipelineAnalysis(writer, outfile);
            
            List<Integer> runSearchIdList = runSearchDao.loadRunSearchIdsForSearch(searchId);
            for(int runSearchId: runSearchIdList) {
                
                writeRunSearch(runSearchId, writer);
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
    
    public void convertRunSearches(List<Integer> runSearchIds, String outfile) {
        
        XMLStreamWriter writer = null;
        try {
            writer = initDocument(outfile);
            startMsmsPipelineAnalysis(writer, outfile);
            
            for(int runSearchId: runSearchIds) {
                writeRunSearch(runSearchId, writer);
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
    
    public void convertRunSearch(int runSearchId, String outdir) {
        
        String outfile = runSearchDao.loadFilenameForRunSearch(runSearchId)+".pep.xml";
        outfile = outdir+File.separator+outfile;
        
        XMLStreamWriter writer = null;
        try {
            writer = initDocument(outfile);
            startMsmsPipelineAnalysis(writer, outfile);
            
            writeRunSearch(runSearchId, writer);
            
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


    private void writeRunSearch(int runSearchId, XMLStreamWriter writer)
            throws XMLStreamException {
        SQTRunSearch runSearch = runSearchDao.loadRunSearch(runSearchId);
        if(runSearch == null) {
            log.error("RunSearch with ID "+runSearchId+" not found!");
            throw new IllegalArgumentException("RunSearch with ID "+runSearchId+" not found!");
        }
        
        SequestSearch search = seqSearchDao.loadSearch(runSearch.getSearchId());
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
        writeSearchResults(runSearchId, staticMods, writer, basefile);
        
        endMsmsRunSummary(writer);
    }
    
    private void writeSearchResults(int runSearchId, List<MsResidueModification> staticMods, XMLStreamWriter writer, String basefile) throws XMLStreamException {
        
        // get all the scanIds for the run
        List<Integer> scanIds = scanDao.loadScanIdsForRun(runSearchId);
        // sort the scanIds
        Collections.sort(scanIds);
        
        // get a list of all the resultIds for this run search.
        List<Integer> resultIds = resultDao.loadResultIdsForRunSearch(runSearchId);
        // sort the resultIds
        Collections.sort(resultIds);
        
        // get all the results one scanId at a time; group them by charge and write out to xml
        for(int scanId: scanIds) {
            List<SequestSearchResult> results = getResultsFor(runSearchId, scanId);
            // remove the result ids returned by the function above from our sorted list of 
            // all result ids for this runSearch.  
            for(SequestSearchResult res: results) {
                int idx = Collections.binarySearch(resultIds, res.getId());
                if(idx >= 0)    resultIds.remove(idx);
            }
            writeResultsForScan(results, staticMods, basefile, writer);
        }
        
        // at the end the resultIds list should be empty.  If not print an error
        if(resultIds.size() > 0) {
            log.error("Did not print all resultIds");
        }
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
    double getCalculatedPeptideMassPlusH(SequestSearchResult result) {
        return result.getSequestResultData().getCalculatedMass().doubleValue();
    }
    
    @Override
    int getResultRankInList(List<SequestSearchResult> resultList,
            SequestSearchResult result) {
        return result.getSequestResultData().getxCorrRank();
    }

    private List<SequestSearchResult> getResultsFor(int runSearchId, int scanId) {
        List<Integer> resultIds = resultDao.loadResultIdsForSearchScan(runSearchId, scanId);
        List<SequestSearchResult> results = new ArrayList<SequestSearchResult>(resultIds.size());
        for(int resultId: resultIds) {
            results.add(resultDao.load(resultId));
        }
        return results;
    }

    @Override
    void sortResultsByRank(List<SequestSearchResult> results) {
        Collections.sort(results, new Comparator<SequestSearchResult>(){
            public int compare(SequestSearchResult o1, SequestSearchResult o2) {
                return Integer.valueOf(o1.getSequestResultData().getxCorrRank()).compareTo(o2.getSequestResultData().getxCorrRank());
            }});
    }

    @Override
    void writeScores(SequestSearchResult result, XMLStreamWriter writer)
            throws XMLStreamException {
        
        SequestResultData resData = result.getSequestResultData();
        // write all the scores for this result
        writer.writeStartElement("search_score");
        writer.writeAttribute("name", "xcorr");
        writer.writeAttribute("value", resData.getxCorr().toString());
        writer.writeEndElement();
        newLine(writer);
        writer.writeCharacters("\t\t");
        writer.writeStartElement("search_score");
        writer.writeAttribute("name", "deltacn");
        writer.writeAttribute("value", resData.getDeltaCN().toString());
        writer.writeEndElement();
        newLine(writer);
        writer.writeCharacters("\t\t");
        writer.writeStartElement("search_score");
        writer.writeAttribute("name", "spscore");
        writer.writeAttribute("value", resData.getSp().toString());
        writer.writeEndElement();
        newLine(writer);
        writer.writeCharacters("\t\t");
        writer.writeStartElement("search_score");
        writer.writeAttribute("name", "sprank");
        writer.writeAttribute("value", String.valueOf(resData.getSpRank()));
        writer.writeEndElement();
        newLine(writer);
    }
    
    public static void main(String[] args) {
        int runSearchId = 2; // 011805-ammsul2-15-totalN2-02
        int searchId = 1;
        String outDir = "/Users/silmaril/WORK/UW/MacCoss_Genn_CE/";
        String outFile = outDir+File.separator+"011805-ammsul2-15-totalN2-02.pepxml";
        SequestPepXmlConverter converter = new SequestPepXmlConverter();
        converter.convertRunSearch(runSearchId, outDir);
         //converter.convertSearch(searchId, outFile);
    }

}
