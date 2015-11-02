/**
 * ProlucidSQTFileReader.java
 * @author Vagisha Sharma
 * Aug 30, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.prolucid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchResultIn;
import org.yeastrc.ms.domain.search.prolucid.ProlucidSearchScan;
import org.yeastrc.ms.domain.search.prolucid.impl.ProlucidResult;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SearchScan;

/**
 * 
 */
public class ProlucidSQTFileReader extends SQTFileReader<ProlucidSearchScan> {

    public void init() {
        super.init();
    }
    
    
    /**
     * Returns the next scan in the file. 
     * @return
     * @throws DataProviderException if the scan or any of its associated results were invalid
     */
    @Override
    protected ProlucidSearchScan nextSearchScan() throws DataProviderException {
        PlucidSearchScan scan = new PlucidSearchScan(parseScan(currentLine));
        advanceLine();

        while(currentLine != null) {
            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                ProlucidSearchResultIn result = parsePeptideResult(scan.getScanNumber(), scan.getCharge(), scan.getObservedMass());
                if (result != null) 
                    scan.addSearchResult(result);
            }
            else {
                break;
            }
        }
        return scan;
    }

    /**
     * Parses a 'M' line and any associated 'L' lines
     * @param scanNumber
     * @param charge
     * @return
     * @throws DataProviderException 
     */
    private ProlucidSearchResultIn parsePeptideResult(int scanNumber, int charge, BigDecimal observedMass) throws DataProviderException {

        ProlucidResult result = parsePeptideResult(currentLine, scanNumber, charge, observedMass);

        advanceLine();

        while (currentLine != null) {
            if (isLocusLine(currentLine)) {
                DbLocus locus = null;
                locus = parseLocus(currentLine);
                if (locus != null)
                    result.addMatchingProteinMatch(locus);
            }
            else
                break;
            advanceLine();
        }
//        if (result.getProteinMatchList().size() == 0)
//            throw new DataProviderException(currentLineNum-1, "Invalid 'M' line.  No locus matches found." , null);
        return result;
    }

    /**
     * Parses a 'M' line in the sqt file.
     * @param line
     * @param scanNumber
     * @param charge
     * @return
     * @throws DataProviderException if the line did not contain the expected number of fields OR
     *                         there was an error parsing numbers in the line OR
     *                         there was an error parsing the peptide sequence in this 'M' line.
     */
    ProlucidResult parsePeptideResult(String line, int scanNumber, int charge, BigDecimal observedMass) throws DataProviderException {

        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

        ProlucidResult result = new ProlucidResult();
        try {
            result.setPrimaryScoreRank(Integer.parseInt(tokens[1]));
            result.setSecondaryScoreRank(Integer.parseInt(tokens[2]));
            result.setMass(new BigDecimal(tokens[3]));
            
            // parsing deltaCN column;
            result.setDeltaCN(new BigDecimal(tokens[4]));
            
            // parsing the xcorr column (primary score)
            result.setPrimaryScore(Double.parseDouble(tokens[5]));
            
            // parsing the sp column (secondary score)
            result.setSecondaryScore(Double.parseDouble(tokens[6]));
            
            result.setNumMatchingIons(Integer.parseInt(tokens[7]));
            result.setNumPredictedIons(Integer.parseInt(tokens[8]));
        }
        catch(NumberFormatException e) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Error parsing number(s). "+e.getMessage(), line);
        }

        result.setOriginalPeptideSequence(tokens[9]);
        result.setValidationStatus(tokens[10].charAt(0));
        result.setCharge(charge);
        result.setScanNumber(scanNumber);
        result.setObservedMass(observedMass);

        // parse the peptide sequence
        try {
            MsSearchResultPeptide resultPeptide = ProlucidResultPeptideBuilder.instance().build(
                    result.getOriginalPeptideSequence(), getDynamicResidueMods(), getDynamicTerminalMods());
            result.setResultPeptide(resultPeptide);
        }
        catch(SQTParseException e) {
            throw new DataProviderException(currentLineNum, "Invalid peptide sequence in 'M'. "+e.getMessage(), line);
        }
        return result;
    }

    private static final class PlucidSearchScan implements ProlucidSearchScan {

        private SearchScan scan;
        private List<ProlucidSearchResultIn> resultList;

        public PlucidSearchScan(SearchScan scan) {
            this.scan = scan;
            resultList = new ArrayList<ProlucidSearchResultIn>();
        }
        public void addSearchResult(ProlucidSearchResultIn result) {
            resultList.add(result);
        }
        public List<ProlucidSearchResultIn> getScanResults() {
            return resultList;
        }
        public int getScanNumber() {
            return scan.getScanNumber();
        }
        public int getCharge() {
            return scan.getCharge();
        }
        public BigDecimal getLowestSp() {
            return scan.getLowestSp();
        }
        public BigDecimal getObservedMass() {
            return scan.getObservedMass();
        }
        public int getProcessTime() {
            return scan.getProcessTime();
        }
        public int getSequenceMatches() {
            return scan.getSequenceMatches();
        }
        public String getServerName() {
            return scan.getServerName();
        }
        public Double getTotalIntensity() {
            return scan.getTotalIntensity();
        }
    }
}
