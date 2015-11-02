package org.yeastrc.ms.parser.sqtFile;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.domain.search.sqtfile.impl.HeaderItemBean;


public class SQTHeader implements SQTRunSearchIn {

    // required headers 
    private static final String SQTGENERATOR_VERSION = "SQTGeneratorVersion";
    private static final String SQTGENERATOR = "SQTGenerator";
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy, hh:mm a"); // Example: 01/29/2008, 03:34 AM
    
    private String sqtGenerator;
    private String sqtGeneratorVersion;
    
    private String startTimeString;
    private String endTimeString;
    private Date startDate;
    private Date endDate;
    private int searchDuration = -1;
    
    
    private List<SQTHeaderItem> headerItems;
    
    private SearchFileFormat sqtType = null;
    private Program program = null;
    
    public SQTHeader() {
        headerItems = new ArrayList<SQTHeaderItem>();
    }
   
    public boolean isValid() {
        if (sqtGenerator == null)           return false;
//        if (sqtGeneratorVersion == null)    return false;
        
        return true;
    }
    
    public static final String requiredHeaders() {
       StringBuilder buf = new StringBuilder();
       buf.append(SQTGENERATOR);
//       buf.append(", ");
//       buf.append(SQTGENERATOR_VERSION);
       return buf.toString();
    }
    
    /**
     * @param name
     * @param value
     * @throws SQTParseException if header name is null or if the header value is invalid
     */
    public void addHeaderItem(String name, String value) throws SQTParseException {
        
        if (name == null)
            throw new SQTParseException("name for Header cannot be null.");
        
        headerItems.add(new HeaderItemBean(name, value));
        
        // if there is no value for this header ignore it; It will still get added to the 
        // headerItems list. 
        if (value == null || value.trim().length() == 0)
            return;
        
        if (isSqtGenerator(name))
            sqtGenerator = value;
        else if (isSqtGeneratorVersion(name))
            sqtGeneratorVersion = value;
        else if (isStartTime(name))
            setStartTime(value);
        else if (isEndTime(name))
            setEndTime(value);
    }

    //-------------------------------------------------------------------------------------------------------
    // Start and end times of the search
    //-------------------------------------------------------------------------------------------------------
    private void setEndTime(String value) throws SQTParseException {
        endTimeString = value;
        if (endTimeString != null) {
            try {
                endDate = new Date(getTime(endTimeString));
            }
            catch (ParseException e) {
                throw new SQTParseException("Error parsing end time: "+value+"\n"+e.getMessage(),
                        SQTParseException.NON_FATAL,
                        e);
            }
        }
    }

    private void setStartTime(String value) throws SQTParseException {
        startTimeString = value;
        if (startTimeString != null) {
            try {
                startDate = new Date(getTime(startTimeString));
            }
            catch (ParseException e) {
                throw new SQTParseException("Error parsing start time: "+value+"\n"+e.getMessage(),
                        SQTParseException.NON_FATAL,
                        e);
            }
        }
    }
    
    //-------------------------------------------------------------------------------------------------------
    // These are the header names we know
    //-------------------------------------------------------------------------------------------------------
    private boolean isSqtGenerator(String name) {
        return name.equalsIgnoreCase(SQTGENERATOR);
    }
    
    private boolean isSqtGeneratorVersion(String name) {
        return name.equalsIgnoreCase(SQTGENERATOR_VERSION);
    }
    
    private boolean isStartTime(String name) {
        return name.equalsIgnoreCase("StartTime");
    }
    
    private boolean isEndTime(String name) {
        return name.equalsIgnoreCase("EndTime");
    }
    
    
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (SQTHeaderItem h: headerItems) {
            buf.append(h.toString());
            buf.append("\n");
        }
        if (buf.length() > 0)
            buf.deleteCharAt(buf.length() -1);
        return buf.toString();
    }

    /**
     * @return the sqtGeneratorVersion
     */
    public String getSearchEngineVersion() {
        getSearchFileFormat(); // this will determine the version if this is a PercolatorSQT;
        return sqtGeneratorVersion;
    }
    
    public List<SQTHeaderItem> getHeaders() {
       return headerItems;
    }

    public SearchFileFormat getSearchFileFormat() {
        if (sqtType != null)
            return sqtType;
        
        // make a check for Percolator first
        // Percolator files do not add Percolator to the sqtGenerator header.
        // Look for it in the other headers
        for(SQTHeaderItem f: headerItems) {
            if (f.getName().equalsIgnoreCase(Program.PERCOLATOR.displayName())) {
                sqtType = SearchFileFormat.SQT_PERC;
                this.sqtGeneratorVersion = parsePercolatorVersion(f.getValue());
                return sqtType;
            }
        }
        if (sqtGenerator.equalsIgnoreCase(Program.SEQUEST.displayName()))
            sqtType = SearchFileFormat.SQT_SEQ;
        else if (sqtGenerator.equalsIgnoreCase(Program.EE_NORM_SEQUEST.displayName()))
            sqtType = SearchFileFormat.SQT_EENSEQ;
        else if (sqtGenerator.equalsIgnoreCase(Program.NORM_SEQUEST.displayName()))
            sqtType = SearchFileFormat.SQT_NSEQ;
        else if (sqtGenerator.equalsIgnoreCase(Program.PERCOLATOR.displayName()))
            sqtType = SearchFileFormat.SQT_PERC;
        else if (sqtGenerator.equalsIgnoreCase(Program.PROLUCID.displayName()))
            sqtType = SearchFileFormat.SQT_PLUCID;
        else if (sqtGenerator.equalsIgnoreCase(Program.PEPPROBE.displayName()))
            sqtType = SearchFileFormat.SQT_PPROBE;
        else if (sqtGenerator.equalsIgnoreCase(Program.TIDE.displayName()))
            sqtType = SearchFileFormat.SQT_TIDE;
        else if (sqtGenerator.equalsIgnoreCase(Program.COMET.displayName()))
            sqtType = SearchFileFormat.SQT_COMET;
        else {
            sqtType = SearchFileFormat.UNKNOWN;
        }
        return sqtType;
    }

    String parsePercolatorVersion(String value) {
        
        // Example: Percolator v 1.07, Build Date Aug 27 2008 10:06:10
        Pattern pattern1 = Pattern.compile("v\\s+(\\d+\\.\\d+)\\s*,.*"); 
        // Percolator version 1.08, Build Date Jan 12 2009 13:47:51
        Pattern pattern2 = Pattern.compile("version\\s+(\\d+\\.\\d+)\\s*,.*"); 
        Matcher matcher = pattern1.matcher(value);
        if(matcher.matches()) {
            return matcher.group(1);
        }
        else {
            matcher = pattern2.matcher(value);
            if(matcher.matches()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    @Override
    public Program getSearchProgram() {
        if (program != null)
            return program;
        program = Program.programForFileFormat(getSearchFileFormat());
        return program;
    }
    
    public Date getSearchDate() {
        return this.startDate;
    }
    
    public int getSearchDuration() {
        
        // if we don't have start or end time return 0
        if (endDate == null || startDate == null) {
            searchDuration = 0;
        }
        // calculating for the first time
        else if (searchDuration == -1) {
            long start = startDate.getTime();
            long end = endDate.getTime();
            searchDuration = (int)((end - start)/(1000*60));
        }
        return searchDuration;
    }

    /**
     * Example of a valid time string: 01/29/2008, 03:34 AM
     * @param timeStr
     * @return
     * @throws ParseException 
     */
    long getTime(String timeStr) throws ParseException {
        return dateFormat.parse(timeStr).getTime();
    }
}
