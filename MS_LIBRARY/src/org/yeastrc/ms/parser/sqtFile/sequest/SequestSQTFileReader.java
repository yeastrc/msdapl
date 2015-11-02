/**
 * SequestSQTFileReader.java
 * @author Vagisha Sharma
 * Aug 21, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.sequest;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.domain.search.sequest.SequestSearchScan;
import org.yeastrc.ms.domain.search.sequest.impl.SequestResult;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SearchScan;

/**
 * 
 */
public class SequestSQTFileReader extends SQTFileReader<SequestSearchScan> {

    private boolean useEvalue = false;

    public SequestSQTFileReader() {
        super();
    }

    public void open(String filePath, boolean useEvalue) throws DataProviderException{
        super.open(filePath);
        this.useEvalue = useEvalue;
    }

    public void open(String fileName, Reader input, boolean useEvalue) throws DataProviderException  {
        super.open(fileName, input);
        this.useEvalue = useEvalue;
    }

    public void init() {
        super.init();
        useEvalue = false;
    }
    /**
     * Returns the next scan in the file. 
     * @return
     * @throws DataProviderException if the scan or any of its associated results were invalid
     */
    @Override
    protected SequestSearchScan nextSearchScan() throws DataProviderException {
        SeqSearchScan scan = new SeqSearchScan(parseScan(currentLine));
        advanceLine();

        while(currentLine != null) {
            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                SequestSearchResultIn result = parsePeptideResult(scan.getScanNumber(), scan.getCharge(), scan.getObservedMass());
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
    private SequestSearchResultIn parsePeptideResult(int scanNumber, int charge, BigDecimal observedMass) throws DataProviderException {

        SequestResult result = parsePeptideResult(currentLine, scanNumber, charge, observedMass);

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
    SequestResult parsePeptideResult(String line, int scanNumber, int charge, BigDecimal observedMass) throws DataProviderException {

        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

        SequestResult result = new SequestResult();
        try {
            result.setxCorrRank(Integer.parseInt(tokens[1]));
            result.setSpRank(Integer.parseInt(tokens[2]));
            result.setCalculatedMass(new BigDecimal(tokens[3]));
            String deltaCn = tokens[4];
            if(deltaCn.equalsIgnoreCase("nan")) deltaCn = "-1.0";
            result.setDeltaCN(new BigDecimal(deltaCn));
            result.setxCorr(new BigDecimal(tokens[5]));
            if (useEvalue)
                result.setEvalue(Double.parseDouble(tokens[6]));
            else
                result.setSp(new BigDecimal(tokens[6]));
            result.setMatchingIons(Integer.parseInt(tokens[7]));
            result.setPredictedIons(Integer.parseInt(tokens[8]));
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
            MsSearchResultPeptide resultPeptide = SequestResultPeptideBuilder.instance().build(
                    result.getOriginalPeptideSequence(), getDynamicResidueMods(), getDynamicTerminalMods());
            result.setResultPeptide(resultPeptide);
        }
        catch(SQTParseException e) {
            throw new DataProviderException(currentLineNum, "Invalid peptide sequence in 'M'. "+e.getMessage(), line);
        }
        return result;
    }

    private static final class SeqSearchScan implements SequestSearchScan {

        private SearchScan scan;
        private List<SequestSearchResultIn> resultList;

        public SeqSearchScan(SearchScan scan) {
            this.scan = scan;
            resultList = new ArrayList<SequestSearchResultIn>();
        }
        public void addSearchResult(SequestSearchResultIn result) {
            resultList.add(result);
        }
        public List<SequestSearchResultIn> getScanResults() {
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

