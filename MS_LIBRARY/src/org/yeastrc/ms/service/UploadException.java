/**
 * UploadException.java
 * @author Vagisha Sharma
 * Jul 31, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service;

/**
 * 
 */
public class UploadException extends Exception {

    private static final long serialVersionUID = 1L;

    public static int WARN = 0;
    private static int ERROR = 1;
    
    public static enum ERROR_CODE {
        
        
        PREUPLOAD_CHECK_FALIED  ("Pre-upload check failed", ERROR),
        GENERAL                 ("", WARN),
        
        SQT_BACKUP_ERROR            ("SQT backup error", WARN),
        
        EXPT_NOT_FOUND          ("Experiment not found in the database", ERROR),
        DIRECTORY_NOT_FOUND     ("Directory not found.", ERROR),
        EMPTY_DIRECTORY         ("No files found to upload.", ERROR),
        MISSING_SCAN_DATA_FILE  ("Missing required scan data files.", ERROR),
        MULTI_SCAN_DATA_FORMATS ("More than one scan data file types found", ERROR),
        NO_SCAN_DATA_FORMATS    ("No supported scan data file types found", ERROR),
        UNSUPPORTED_SCAN_DATA_FORMAT ("Unsupported scan data format", ERROR),
        
        
        CREATE_EXPT_ERROR       ("Error creating experiment.", ERROR),
        
        READ_ERROR_MS2          ("Error reading MS2 file", ERROR),
        INVALID_MS2_SCAN        ("Invalid MS2 scan", ERROR),
        SHA1SUM_CALC_ERROR      ("Error calculating sha1sum", ERROR),
        
        RUNTIME_MS2_ERROR       ("", ERROR),
        
        READ_ERROR_MZXML          ("Error reading mzXML file", ERROR),
        INVALID_MZXML_SCAN        ("Invalid mzXML scan", ERROR),
        RUNTIME_MZXML_ERROR       ("", ERROR),
        
        READ_ERROR_MZML          ("Error reading mzML file", ERROR),
        INVALID_MZML_SCAN        ("Invalid mzML scan", ERROR),
        RUNTIME_MZML_ERROR       ("", ERROR),
        
        NO_SQT_TYPE             ("No sqt type found", ERROR),
        UNSUPPORTED_SQT         ("Unsupported sqt file found", ERROR),
        MULTIPLE_SQT_TYPES      ("More than one sqt file types found", ERROR),
        NO_RUNID_FOR_SQT        ("No runID found for sqt file", WARN),
        NO_RUNID_FOR_SEARCH_FILE("No runID found for input search file", WARN),
        INVALID_SQT_HEADER      ("Invalid SQT header", WARN),
        INVALID_SQT_SCAN        ("Invalid SQT scan", WARN),
        NO_SCANID_FOR_SCAN      ("No database scanID found for scan", WARN),
        DUPLICATE_SCAN_CHARGE   ("Duplicate result found for scan + charge combination", ERROR),
        READ_ERROR_SQT          ("Error reading SQT file", ERROR),
        
        MOD_LOOKUP_FAILED       ("Modification lookup failed", WARN),
        
        MISSING_SEQUEST_PARAMS  ("Missing sequest.params file.", ERROR),
        MISSING_COMET_PARAMS    ("Missing comet.params file.", ERROR),
        MISSING_PROLUCID_PARAMS ("Missing search.xml files.", ERROR),
        UNKNOWN_PARAMS          ("Unknown parameters file for search.", ERROR),
        PARAM_PARSING_ERROR     ("Error parsing parameters file", ERROR),
        NO_RUN_SEARCHES_UPLOADED("No run searches were uploaded", ERROR),
        AMBIG_PROG_VERSION      ("Ambiguous analysis program version in sqt files", ERROR),
        
        SEARCHDB_NOT_FOUND      ("Fasta file not found", ERROR),
        PROTEIN_NOT_FOUND       ("Protein not found in database", ERROR),
        
        SCAN_CHARGE_NOT_FOUND   ("MS2 scan charge information not found", ERROR),
        
        // For Percolator uploads
        NO_RUNSEARCHID_FOR_ANALYSIS_FILE  ("No runSearchID found for analysis file", WARN),
        NO_RSANALYSISID_FOR_ANALYSIS_FILE  ("No runSearchAnalysisID found for analysis file", WARN),
        NO_PERC_ANALYSIS_UPLOADED("No Percolator analysis files were uploaded", ERROR),
        NO_MATCHING_SEARCH_RESULT("No matching search result was found for the analysis result", WARN),
        MULTI_MATCHING_SEARCH_RESULT("Multiple matching search results were found for the analysis result", WARN),
        NO_MATCHING_SEARCH_SCAN("No matching search scan was found for the Percolator search scan", WARN),
        
        // General
        RUNTIME_SQT_ERROR       ("Runtime exception.", WARN),
        RUNTIME_ERROR           ("Runtime exception", WARN),
        
        // PepXml
        PEPXML_ERROR            ("Error reading pepxml", WARN),
        
        // ProtXml
        PROTXML_ERROR           ("Error reading protxml", WARN),
        
        // PeptideProphet
        NO_PEPTPROPH_ANALYSIS_UPLOADED("No PeptideProphet analyses were uploaded", ERROR),
        
        // Percolator XML
        PERC_XML_ERROR 			("Error reading Percolator XML", WARN),
        
        // Error disabling/enabling keys
        ERROR_SQL_DISABLE_KEYS  ("Error disabling keys", ERROR),
        ERROR_SQL_ENABLE_KEYS  ("Error enabling keys", ERROR)
        ;
      
        private String message = "";
        private int errType;
        
        private ERROR_CODE(String message, int errType) {
            this.message = message;
            this.errType = errType;
        }
        public String getMessage() {
            return message;
        }
        public boolean isError() {
            return errType == ERROR;
        }
    }
    
    private final ERROR_CODE errCode;
    private String directory; 
    private String file;
    private String errorMessage;
    
    public UploadException(ERROR_CODE error) {
        this.errCode = error;
    }
    
    public UploadException(ERROR_CODE error, Exception e) {
        super(e);
        this.errCode = error;
    }
    
    public ERROR_CODE getErrorCode() {
        return errCode;
    }
    
    public String getMessage() {
        StringBuilder buf = new StringBuilder();
        if (errCode.isError()) 
            buf.append("ERROR: ");
        else
            buf.append("WARNING: ");
        buf.append(errCode.getMessage());
        if (file != null)
            buf.append("\n\tFile: "+file);
        if (directory != null)
            buf.append("\n\tDirectory: "+directory);
        if (errorMessage != null)
            buf.append("\n\t"+errorMessage);
        buf.append("\n");
        
        return buf.toString();
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
    
    public void setFile(String file) {
        this.file = file;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public void appendErrorMessage(String toAppend) {
        if (errorMessage == null || errorMessage.length() == 0) {
            errorMessage = toAppend;
        }
        else {
            errorMessage = errorMessage+"\n\t"+toAppend;
        }
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
