package org.yeastrc.ms.parser.sqtFile;

import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SQTSearchDataProvider;


public abstract class SQTFileReader <T extends SQTSearchScanIn<?>> extends AbstractReader 
    implements SQTSearchDataProvider<T>  {

    private List<? extends MsResidueModificationIn> searchDynamicResidueMods;
    private List<? extends MsTerminalModificationIn> searchDynamicTerminalMods;
    

    private static final Logger log = Logger.getLogger(SQTFileReader.class);

    private static final Pattern headerPattern = Pattern.compile("^H\\s+([\\S]+)\\s*(.*)");
    private static final Pattern locusPattern = Pattern.compile("^L\\s+([\\S]+)\\s*(.*)");
    
    public void init() {
        searchDynamicResidueMods.clear();
        searchDynamicTerminalMods.clear();
    }
    
    @SuppressWarnings("unchecked")
	public static SearchFileFormat getSearchFileType(String filePath) {
        
        SQTFileReader reader = new SQTFileReader(){
            @Override
            public SQTSearchScanIn nextSearchScan()
            throws DataProviderException {
                throw new UnsupportedOperationException("");
            }};
            
            try {
                reader.open(filePath);
                SQTHeader header = reader.getSearchHeader();
                return header.getSearchFileFormat();
            }
            catch (DataProviderException e) {
                log.error("Error getting SQT file format", e);
            }
            finally {
                reader.close();
            }
            return SearchFileFormat.UNKNOWN;
    }
    
    @SuppressWarnings("unchecked")
	public static SearchFileFormat getSearchFileType(String fileName, Reader input) {
        
        SQTFileReader reader = new SQTFileReader(){
            @Override
            public SQTSearchScanIn nextSearchScan()
                    throws DataProviderException {
                throw new UnsupportedOperationException("");
            }};
            
            try {
                reader.open(fileName, input);
                SQTHeader header = reader.getSearchHeader();
                return header.getSearchFileFormat();
            }
            catch (DataProviderException e) {
            	log.error("Error getting SQT file format", e);
            }
            finally {
                reader.close();
            }
            return SearchFileFormat.UNKNOWN;
    }
    
    public SQTFileReader() {
        searchDynamicResidueMods = new ArrayList<MsResidueModificationIn>();
        searchDynamicTerminalMods = new ArrayList<MsTerminalModificationIn>();
    }
    
    public void setDynamicResidueMods(List<? extends MsResidueModificationIn> dynaResidueMods) {
        if (dynaResidueMods != null)
            this.searchDynamicResidueMods = dynaResidueMods;
    }
    
    protected List<MsResidueModificationIn> getDynamicResidueMods() {
        return (List<MsResidueModificationIn>) this.searchDynamicResidueMods;
    }
    
    public void setDynamicTerminalMods(List<? extends MsTerminalModificationIn> dynaTerminalMods) {
        if (dynaTerminalMods != null)
            this.searchDynamicTerminalMods = dynaTerminalMods;
    }
    
    protected List<MsTerminalModificationIn> getDynamicTerminalMods() {
        return (List<MsTerminalModificationIn>) this.searchDynamicTerminalMods;
    }
    
    public SQTHeader getSearchHeader()  throws DataProviderException {

        SQTHeader header = new SQTHeader();
        
        while (isHeaderLine(currentLine)) {
            String[] nameAndVal = parseHeader(currentLine);
            addHeaderItem(header, nameAndVal);
            advanceLine();
        }
        
        if (!header.isValid())
            throw new DataProviderException("Invalid SQT Header. One or more required headers is missing. "+
                    "Required headers:\n\t"+SQTHeader.requiredHeaders());
        
        return header;
    }

    private void addHeaderItem(SQTHeader header, String[] nameAndVal) throws DataProviderException {
        
        if (nameAndVal.length == 0)
            return;
        
        String name = nameAndVal[0];
        String val = nameAndVal.length > 1 ? nameAndVal[1] : null;
        
        try { 
            header.addHeaderItem(name, val);
        }
        catch(SQTParseException e) {
            DataProviderException ex = new DataProviderException(currentLineNum, e.getMessage(), currentLine, e);
            if (e.isFatal())
                throw ex;
            else
                log.warn(ex.getMessage());
        }
    }
    
    String[] parseHeader(String line) {
        return parseNameValueLine(line, headerPattern);
    }
    
    public boolean hasNextSearchScan()  {
        return currentLine != null;
    }

    @Override
    public T getNextSearchScan() throws DataProviderException {
         T scan = nextSearchScan();
         if(scan.getScanResults().size() == 0) {
             // This will not cause an UploadException
             DataProviderException ex = new DataProviderException(currentLineNum-1, "Invalid 'S' line.  No results found." , null);
             log.debug(ex.getMessage());
         }
         
         for(int i = 0; i < scan.getScanResults().size(); i++) {
             if(scan.getScanResults().get(i).getProteinMatchList().size() == 0) {
                 throw new DataProviderException(currentLineNum-1,
                         "Invalid 'M' line for scan "+
                         scan.getScanNumber()+"; charge: "+scan.getCharge()+
                         ".  No locus matches found.", null);
             }
         }
         return scan;
    }
    
    protected abstract T nextSearchScan() throws DataProviderException;
    
    protected SearchScan parseScan(String line) throws DataProviderException {

        // make sure we have a scan line
        if (!isScanLine(line)) {
            throw new DataProviderException(currentLineNum, "Error parsing scan. Expected line starting with 'S'.", line);
        }

        String[] tokens = line.split("\\s+");
        if (tokens.length != 10) {
            throw new DataProviderException(currentLineNum, "Invalid 'S' line. Expected 10 fields", line);
        }

        SearchScan scan = new SearchScan();
        try {
            scan.setStartScan(Integer.parseInt(tokens[1]));
            scan.setEndScan(Integer.parseInt(tokens[2]));
            scan.setCharge(Integer.parseInt(tokens[3]));
            int procTime = Integer.parseInt(tokens[4]);
            if(procTime < 0) procTime = Integer.MAX_VALUE; // flag this.
            scan.setProcessingTime(procTime);
            scan.setObservedMass(new BigDecimal(tokens[6]));
            scan.setTotalIntensity(Double.parseDouble(tokens[7]));
            scan.setLowestSp(new BigDecimal(tokens[8]));
            scan.setSequenceMatches(Integer.parseInt(tokens[9]));
        }
        catch(NumberFormatException e) {
            throw new DataProviderException(currentLineNum, "Invalid 'S' line. Error parsing number(s). "+e.getMessage(), line);
        }
        scan.setServer(tokens[5]);

        return scan;
    }


    /**
     * Parses a 'L' line in the sqt file
     * @param line
     * @return
     * @throws DataProviderException
     */
    protected DbLocus parseLocus(String line) throws DataProviderException {
        
        String[] nameAndVal = super.parseNameValueLine(line, locusPattern);
        if (nameAndVal.length == 2) {
            return new DbLocus(nameAndVal[0], nameAndVal[1]);
        }
        else if (nameAndVal.length == 1) {
            return new DbLocus(nameAndVal[0], null);
        }
        
        else {
            throw new DataProviderException(currentLineNum, "Invalid 'L' line. Expected 2 fields", line);
        }
    }


    private static boolean isScanLine(String line) {
        if (line == null)   return false;
        return line.startsWith("S");
    }

    private static boolean isHeaderLine(String line) {
        if (line == null)   return false;
        return line.startsWith("H");
    }

    protected static boolean isResultLine(String line) {
        if (line == null)   return false;
        return line.startsWith("M");
    }

    protected static boolean isLocusLine(String line) {
        if (line == null)   return false;
        return line.startsWith("L");
    }

    protected boolean isValidLine(String line) {
        if (line.trim().length() == 0)  return false;
        return( line.charAt(0) == 'L'   || 
                line.charAt(0) == 'M'   || 
                line.charAt(0) == 'S'   ||
                line.charAt(0) == 'H');
    }
}
