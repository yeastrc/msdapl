/**
 * PercolatorSQTFileReader.java
 * @author Vagisha Sharma
 * Dec 11, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.percolator;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorSearchScan;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.DbLocus;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.SearchScan;
import org.yeastrc.ms.parser.sqtFile.prolucid.ProlucidResultPeptideBuilder;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestResultPeptideBuilder;

/**
 * 
 */
public class PercolatorSQTFileReader extends SQTFileReader<PercolatorSearchScan> {

    private float percolatorVersion;
    private Program searchProgram;

    public PercolatorSQTFileReader() {
        super();
    }

    public void open(String filePath, Program searchProgram) throws DataProviderException{
        super.open(filePath);
        this.searchProgram = searchProgram;
    }

    public void open(String fileName, Reader input, Program searchProgram) throws DataProviderException  {
        super.open(fileName, input);
        this.searchProgram = searchProgram;
    }

    public void init() {
        super.init();
    }
    
    public SQTHeader getSearchHeader()  throws DataProviderException {

        SQTHeader header = super.getSearchHeader();
        // Grab the percolator version out of the header section.
        try {
            percolatorVersion = Float.parseFloat(header.getSearchEngineVersion());
        }
        catch(NumberFormatException e) {
            throw new DataProviderException("Error parsing Percolator version: "+header.getSearchEngineVersion());
        }
        return header;
    }
    
    public static Map<String, String> getPercolatorParams(SQTHeader header) {
        Map<String, String> params = new HashMap<String, String>();
        for(SQTHeaderItem hi: header.getHeaders()) {
            if(hi.getName().equalsIgnoreCase("Hyperparameters")) {
                // Hyperparameters fdr=0.01, Cpos=0, Cneg=0, maxNiter=10
                String val = hi.getValue();
                val.replaceAll("\\s", ""); // remove spaces
                String[] tokens = val.split(",");
                for(String token: tokens) {
                    String[] pstr = token.split("=");
                    params.put(pstr[0], pstr[1]);
                }
            }
        }
        return params;
    }
    
    /**
     * Returns the next scan in the file. 
     * @return
     * @throws DataProviderException if the scan or any of its associated results were invalid
     */
    @Override
    protected PercolatorSearchScan nextSearchScan() throws DataProviderException {
        PercSearchScan scan = new PercSearchScan(parseScan(currentLine));
        advanceLine();

        while(currentLine != null) {
            // is this one of the results for the scan ('M' line)
            if (isResultLine(currentLine)) {
                PercolatorResultIn result = parsePeptideResult(scan.getScanNumber(), scan.getCharge(), scan.getObservedMass());
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
    private PercolatorResultIn parsePeptideResult(int scanNumber, int charge, BigDecimal observedMass) throws DataProviderException {

        PercolatorAnalysisResult result = parsePeptideResult(currentLine, scanNumber, charge, observedMass);

        advanceLine();

        boolean isPlaceholder = false;
        while (currentLine != null) {
            if (isLocusLine(currentLine)) {
                DbLocus locus = null;
                locus = parseLocus(currentLine);
                
                if (locus != null) {
                    result.addMatchingProteinMatch(locus);
                
                    // NOTE: IGNORE ALL 'M' LINES FOLLOWED BY THE FOLLOWING 'L' LINE
                    // L       Placeholder satisfying DTASelect
                    // This is not a valid result and we will return null
                    if(locus.getAccession().startsWith("Placeholder"))
                        isPlaceholder = true;
                }
                
            }
            else
                break;
            advanceLine();
        }
        if (result.getProteinMatchList().size() == 0)
            throw new DataProviderException(currentLineNum-1, "Invalid 'M' line.  No locus matches found." , null);
        if(isPlaceholder) result = null;
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
    PercolatorAnalysisResult parsePeptideResult(String line, int scanNumber, int charge, BigDecimal observedMass) throws DataProviderException {

        String[] tokens = line.split("\\s+");
        if (tokens.length != 11) {
            throw new DataProviderException(currentLineNum, "Invalid 'M' line. Expected 11 fields", line);
        }

        PercolatorAnalysisResult result = new PercolatorAnalysisResult();
        try {
            result.setxCorrRank(Integer.parseInt(tokens[1]));
            result.setSpRank(Integer.parseInt(tokens[2]));
            result.setMass(new BigDecimal(tokens[3]));
            // TODO Percolator changes the value in DeltaCN column!! It can be "nan" -- causes exception
            // Since we are not storing the DeltaCN values from Percolator SQT's I am ignoring this for now.
           // result.setDeltaCN(new BigDecimal(tokens[4]));

            // TODO Same here. Found "nan" in column 6 -- causes an exception. 
            try {
                double val = Double.parseDouble(tokens[5]);
                if(percolatorVersion >= 1.06) {
                    result.setPosteriorErrorProbability( 1 - val); // column has 1 - PEP
                }
                else {
                    result.setDiscriminantScore(val);
                }
            }
            catch(NumberFormatException ex) {
                if(percolatorVersion >= 1.06) {
                    result.setPosteriorErrorProbability(-1.0); // will set it to null
                }
                else {
                    result.setDiscriminantScore(null); // will set it to null
                }
            }
            
            result.setQvalue(-Double.parseDouble(tokens[6])); // column has (minus)qvalue
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

            MsSearchResultPeptide resultPeptide = null;
            
            if(searchProgram == Program.SEQUEST )//|| searchProgram == Program.EE_NORM_SEQUEST)
                resultPeptide = SequestResultPeptideBuilder.instance().build(
                        result.getOriginalPeptideSequence(), getDynamicResidueMods(), getDynamicTerminalMods());
            
            else if (searchProgram == Program.PROLUCID)
                resultPeptide = ProlucidResultPeptideBuilder.instance().build(
                        result.getOriginalPeptideSequence(), getDynamicResidueMods(), getDynamicTerminalMods());

            else
                throw new SQTParseException("Cannot parse peptide string without know the name of the search program that created it.", SQTParseException.FATAL);
            
            result.setResultPeptide(resultPeptide);
        }
        catch(SQTParseException e) {
            throw new DataProviderException(currentLineNum, "Invalid peptide sequence in 'M'. "+e.getMessage(), line);
        }
        return result;
    }

    private static final class PercSearchScan implements PercolatorSearchScan {

        private SearchScan scan;
        private List<PercolatorResultIn> resultList;

        public PercSearchScan(SearchScan scan) {
            this.scan = scan;
            resultList = new ArrayList<PercolatorResultIn>();
        }
        public void addSearchResult(PercolatorResultIn result) {
            resultList.add(result);
        }
        public List<PercolatorResultIn> getScanResults() {
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
        
        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("S\t"+getScanNumber()+"\t");
            buf.append(getCharge()+"\t");
            buf.append(getObservedMass()+"\n");
            for(PercolatorResultIn result: resultList) {
                buf.append(result.toString()+"\n");
            }
            return buf.toString();
        }
    }

    public static void main(String[] args) throws DataProviderException {
        String file = "/Users/silmaril/WORK/UW/MacCoss_Genn_CE/DIA-NOV08/13NOV08-DIA-700-760-01.sqt";
        
        PercolatorSQTFileReader reader = new PercolatorSQTFileReader();
        reader.open(file, Program.SEQUEST);
        SQTHeader header = reader.getSearchHeader();
        System.out.println(header.toString());
        while(reader.hasNextSearchScan()) {
            PercolatorSearchScan scan = reader.getNextSearchScan();
            System.out.println(scan);
        }
    }
}
