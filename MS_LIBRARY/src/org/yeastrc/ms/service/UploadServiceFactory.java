/**
 * UploadServiceFactory.java
 * @author Vagisha Sharma
 * Mar 25, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.search.SearchFileFormat;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.pepxml.PepXmlUtils;
import org.yeastrc.ms.parser.percolator.PercolatorXmlFileReader;
import org.yeastrc.ms.parser.sqtFile.SQTFileReader;
import org.yeastrc.ms.service.ms2file.MS2DataUploadService;
import org.yeastrc.ms.service.mzml.MzMlDataUploadService;
import org.yeastrc.ms.service.mzxml.MzXmlDataUploadService;
import org.yeastrc.ms.service.pepxml.PepXmlCometDataUploadService;
import org.yeastrc.ms.service.pepxml.PepXmlMascotDataUploadService;
import org.yeastrc.ms.service.pepxml.PepXmlSequestDataUploadService;
import org.yeastrc.ms.service.pepxml.PepXmlXtandemDataUploadService;
import org.yeastrc.ms.service.pepxml.PepxmlAnalysisDataUploadService;
import org.yeastrc.ms.service.percolator.PercolatorXmlDataUploadService;
import org.yeastrc.ms.service.protxml.ProtxmlDataUploadService;
import org.yeastrc.ms.service.sqtfile.CometSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.PercolatorSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.ProlucidSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.SequestSQTDataUploadService;
import org.yeastrc.ms.service.sqtfile.TideSQTDataUploadService;
import org.yeastrc.ms.util.FileUtils;

/**
 * This class determines the upload service class for a particular type of data 
 * (spectrum, search results, analysis results or protein inference results) based on the 
 * files in the input directory.
 */
public class UploadServiceFactory {

    private static final UploadServiceFactory instance = new UploadServiceFactory();
    
    private static final Logger log = Logger.getLogger(UploadServiceFactory.class.getName());
    
    private UploadServiceFactory() {}
    
    public static UploadServiceFactory instance() {
        return instance;
    }
    
    
    // ------------------------------------------------------------------------------------------------------
    // Spectrum Data Upload service
    // ------------------------------------------------------------------------------------------------------
    /**
     * Looks for supported file formats for spectrum data in the given input directory and returns the appropriate
     * upload service class
     * @param dataDirectory
     * @return 
     * @throws UploadServiceFactoryException if the directory does not contain any, or contains multiple supported
     *                                       file formats for spectrum data.
     */
    public SpectrumDataUploadService getSpectrumDataUploadService(String dataDirectory,
    		Set<String> filesToUpload) throws UploadServiceFactoryException {
        
        if(dataDirectory == null) {
            throw new UploadServiceFactoryException("dataDirectory is null");
        }
        
        File dir = new File(dataDirectory);
        if(!dir.exists()) {
            throw new UploadServiceFactoryException("dataDirectory does not exist: "+dataDirectory);
        }
        
        if(!dir.isDirectory()) {
            throw new UploadServiceFactoryException(dataDirectory+" is not a directory");
        }
        
        File[] files = dir.listFiles();
        if(files == null)
        {
        	throw new UploadServiceFactoryException(dataDirectory+" is not accessible");
        }
        
        Set<RunFileFormat> formats = new HashSet<RunFileFormat>();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory())
                continue;
            
            // If we are given a list of file names (without extension) that we want to upload
            // ignore everything else.
            if(filesToUpload != null) {
            	if(!filesToUpload.contains(FileUtils.removeExtension(files[i].getName())))
            		continue;
            }
            
            RunFileFormat format = RunFileFormat.forFile(files[i].getName());
            if(format == RunFileFormat.UNKNOWN) 
                continue;
            
            formats.add(format);
        }
        
        if(formats.size() == 0) {
            throw new UploadServiceFactoryException("No valid spectrum files found in directory: "+dataDirectory);
        }
        
        if(formats.size() > 1) {
            // If multiple formats are found it may be that we have a combination of .ms2 and .cms2 files in the 
            // same directory.  In that case, we don't throw an exception.
            if(!isMs2Format(formats))
                throw new UploadServiceFactoryException("Multiple types of spectrum files found in directory: "+dataDirectory);
        }
        
        RunFileFormat format = formats.iterator().next();
        if(format == RunFileFormat.MS2 || format == RunFileFormat.CMS2) {
            SpectrumDataUploadService service = new MS2DataUploadService();
            service.setDirectory(dataDirectory);
            return service;
        }
        else if(format == RunFileFormat.MZXML) {
            SpectrumDataUploadService service = new MzXmlDataUploadService();
            service.setDirectory(dataDirectory);
            return service;
        }
        else if(format == RunFileFormat.MZML) {
            SpectrumDataUploadService service = new MzMlDataUploadService();
            service.setDirectory(dataDirectory);
            return service;
        
        }
        else {
            throw new UploadServiceFactoryException("We do not currently have support for the spectrum file format: "+format.toString());
        }
    }
    
    private boolean isMs2Format(Set<RunFileFormat> formats) {
        for(RunFileFormat fmt: formats) {
            if(fmt != RunFileFormat.MS2 && fmt != RunFileFormat.CMS2)
                return false;
        }
        return true;
    }
    
    // ------------------------------------------------------------------------------------------------------
    // SEARCH Data Upload service
    // ------------------------------------------------------------------------------------------------------
    /**
     * Looks for supported file formats for search results in the given input directory and returns the appropriate
     * upload service class.
     * @param dataDirectory
     * @return
     * @throws UploadServiceFactoryException if the directory does not contain any, or contains multiple supported
     *                                       file formats for search results.
     */
    public SearchDataUploadService getSearchDataUploadService(String dataDirectory) throws UploadServiceFactoryException {
        
        if(dataDirectory == null) {
            throw new UploadServiceFactoryException("dataDirectory is null");
        }
        
        File dir = new File(dataDirectory);
        if(!dir.exists()) {
            throw new UploadServiceFactoryException("dataDirectory does not exist: "+dataDirectory);
        }
        
        if(!dir.isDirectory()) {
            throw new UploadServiceFactoryException(dataDirectory+" is not a directory");
        }
        
        File[] files = dir.listFiles();
        Set<SearchFileFormat> formats = new HashSet<SearchFileFormat>();
        Set<String> filenames = new HashSet<String>();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory())
                continue;
            String fileName = files[i].getName();
            
            SearchFileFormat format = SearchFileFormat.forFile(fileName);
            if(format == SearchFileFormat.UNKNOWN) 
                continue;
            
            filenames.add(fileName);
            formats.add(format);
        }
        
        if(formats.size() == 0) {
            throw new UploadServiceFactoryException("No valid search result files found in directory: "+dataDirectory);
        }
        
        if(formats.size() > 1) {
        	if(formats.contains(SearchFileFormat.SQT)) {
        		formats.clear();
        		formats.add(SearchFileFormat.SQT);
        	}
        	else if(formats.contains(SearchFileFormat.XML) || formats.contains(SearchFileFormat.PEPXML)) {
        		formats.clear();
        		formats.add(SearchFileFormat.PEPXML);
        	}
        }
        
        if(formats.size() > 1) {
        	
        	String formatsFound = "";
        	for(SearchFileFormat format: formats)
        		formatsFound += ", "+format.name();
        	if(formatsFound.length() > 0)
        		formatsFound = formatsFound.substring(1);
            throw new UploadServiceFactoryException("Multiple types of search result files found in directory: "+dataDirectory+"; Found: "+formatsFound);
        }
        
        SearchFileFormat format = formats.iterator().next();
        if(format ==  SearchFileFormat.SQT) {
            // we know that we have SQT files in this directory
            // now figure out which program generated these files.
            SearchFileFormat sqtFormat = getSqtType(dataDirectory, filenames);
            if (SearchFileFormat.isSequestFormat(sqtFormat)) {
            	SearchDataUploadService service = new SequestSQTDataUploadService();
                ((SequestSQTDataUploadService) service).setSearchFileFormat(sqtFormat);
                service.setDirectory(dataDirectory);
                service.setDecoyDirectory(dataDirectory+File.separator+"decoy");
                return service;
            }
            else if (sqtFormat == SearchFileFormat.SQT_COMET) {
            	SearchDataUploadService service = new CometSQTDataUploadService();
                ((CometSQTDataUploadService) service).setSearchFileFormat(sqtFormat);
                service.setDirectory(dataDirectory);
                service.setDecoyDirectory(dataDirectory+File.separator+"decoy");
                return service;
            }
            else if (sqtFormat == SearchFileFormat.SQT_TIDE) {
                SearchDataUploadService service = new TideSQTDataUploadService();
                service.setDirectory(dataDirectory);
                //service.setDecoyDirectory(dataDirectory+File.separator+"decoy");
                return service;
            }
            else if (sqtFormat == SearchFileFormat.SQT_PLUCID) {
                SearchDataUploadService service = new ProlucidSQTDataUploadService();
                service.setDirectory(dataDirectory);
                return service;
            }
            else {
                throw new UploadServiceFactoryException("We do not currently have support for the search result file format: "+format.toString());
            }
        }
        else if(format == SearchFileFormat.PEPXML) {
            // we know that we have pepxml files in this directory
            // now we need to figure out which program (Sequest, Mascot, Xtandem, Comet) results these files contain.
            SearchFileFormat pepxmlFormat = getPepxmlType(dataDirectory, filenames);
            
            if(pepxmlFormat == SearchFileFormat.PEPXML_SEQ) {
            	SearchDataUploadService service = new PepXmlSequestDataUploadService();
                service.setDirectory(dataDirectory);
                return service;
            }
            else if (pepxmlFormat == SearchFileFormat.PEPXML_COMET)
            {
            	SearchDataUploadService service = new PepXmlCometDataUploadService();
                service.setDirectory(dataDirectory);
                return service;
            }
            else if(pepxmlFormat == SearchFileFormat.PEPXML_MASCOT) {
                SearchDataUploadService service = new PepXmlMascotDataUploadService();
                service.setDirectory(dataDirectory);
                return service;
            }
            else if(pepxmlFormat == SearchFileFormat.PEPXML_XTANDEM) {
                SearchDataUploadService service = new PepXmlXtandemDataUploadService();
                service.setDirectory(dataDirectory);
                return service;
            }
            else {
                throw new UploadServiceFactoryException("We do not currently have support for the search result file format: "+format.toString());
            }
        }
        else {
            throw new UploadServiceFactoryException("We do not currently have support for the search result file format: "+format.toString());
        }
    }
    
    // ------------------------------------------------------------------------------------------------------
    // ANALYSIS Data Upload service
    // ------------------------------------------------------------------------------------------------------
    /**
     * Looks for supported file formats for analysis results (post search results) in the given input directory
     * and returns the appropriate upload service class.
     * @param dataDirectory
     * @return
     * @throws UploadServiceFactoryException if the directory does not contain any, or contains multiple supported
     *                                       file formats for analysis results
     */
    public AnalysisDataUploadService getAnalysisDataUploadService(String dataDirectory) throws UploadServiceFactoryException {
        
        if(dataDirectory == null) {
            throw new UploadServiceFactoryException("dataDirectory is null");
        }
        
        File dir = new File(dataDirectory);
        if(!dir.exists()) {
            throw new UploadServiceFactoryException("dataDirectory does not exist: "+dataDirectory);
        }
        
        if(!dir.isDirectory()) {
            throw new UploadServiceFactoryException(dataDirectory+" is not a directory");
        }
        
        File[] files = dir.listFiles();
        Set<SearchFileFormat> formats = new HashSet<SearchFileFormat>();
        Set<String> filenames = new HashSet<String>();
        
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory())
                continue;
            String fileName = files[i].getName();
            
            SearchFileFormat format = SearchFileFormat.forFile(fileName);
            if(format == SearchFileFormat.UNKNOWN 
            		|| format == SearchFileFormat.PROTXML) // Ignore protxml files
            	continue;
            
            if(format == SearchFileFormat.XML) {
            	// Add only supported Percolator XML files (post version 1.14)
            	if(isPercolatorXml(dataDirectory+File.separator+fileName)) {
            		formats.add(SearchFileFormat.XML_PERC);
            	}
            }
            else {
            	formats.add(format);
            }
            
            filenames.add(fileName);
            
        }
        
        if(formats.size() == 0) {
            throw new UploadServiceFactoryException("No valid analysis result file types found in directory: "+dataDirectory);
        }
        
        if(formats.size() > 1) {
        	
        	
        	if(formats.contains(SearchFileFormat.SQT) && formats.contains(SearchFileFormat.XML_PERC)) {
	        	// We may have .sqt and Percolator xml file in the same directory.  If so we will upload the xml file
	        	// ONLY if the version is "UNOFFICIAL" or > 1.14
        		
        		formats.remove(SearchFileFormat.SQT);
        	}
        	
        	if(formats.size() > 1)
        		throw new UploadServiceFactoryException("Multiple analysis result file types found in directory: "+dataDirectory);
        }
        
        SearchFileFormat format = formats.iterator().next();
        if(format ==  SearchFileFormat.SQT) {
            // we know that we have SQT files in this directory
            // now figure out which program generated these files.
            SearchFileFormat sqtFormat = getSqtType(dataDirectory, filenames);
            if (sqtFormat == SearchFileFormat.SQT_PERC) {
                AnalysisDataUploadService service = new PercolatorSQTDataUploadService();
                service.setDirectory(dataDirectory);
                return service;
            }
            else {
                throw new UploadServiceFactoryException("We do not currently have support for the analysis result file format: "+format.toString());
            }
        }
        else if(format == SearchFileFormat.XML_PERC) {
        	AnalysisDataUploadService service = new PercolatorXmlDataUploadService();
            service.setDirectory(dataDirectory);
            return service;
        }
        
        else if(format == SearchFileFormat.PEPXML) {
            AnalysisDataUploadService service = new PepxmlAnalysisDataUploadService();
            service.setDirectory(dataDirectory);
            return service;
        }
        else {
            throw new UploadServiceFactoryException("We do not currently have support for the analysis result file format: "+format.toString());
        }
    }
    
    private boolean isPercolatorXml(String filePath) {
    	try {
			return PercolatorXmlFileReader.isSupportedPercolatorXml(filePath);
		} catch (DataProviderException e) {
			log.error("Error reading XML file: "+filePath, e);
			return false;
		}
	}

	private SearchFileFormat getSqtType(String fileDirectory, Set<String> filenames) throws UploadServiceFactoryException {
        
        SearchFileFormat sqtType = null;
        
        // make sure all files are of the same type
        for (String file: filenames) {
        	
        	if(!file.toLowerCase().endsWith(".sqt"))
        		continue;
        	
            String sqtFile = fileDirectory+File.separator+file;
            // first make sure the file exists
            if (!(new File(sqtFile).exists()))
                continue;
            SearchFileFormat myType = SQTFileReader.getSearchFileType(sqtFile);
            
            // For now we support sqt files generated by Sequest, Comet, EE-normalized SEQUEST, SEQUEST-NORM, 
            // ProLuCID, Tide and Percolator 
            if (SearchFileFormat.SQT_SEQ != myType &&
            		SearchFileFormat.SQT_COMET != myType &&
                    SearchFileFormat.SQT_NSEQ != myType &&
                    SearchFileFormat.SQT_EENSEQ != myType &&
                    SearchFileFormat.SQT_PLUCID != myType &&
                    SearchFileFormat.SQT_TIDE != myType &&
                    SearchFileFormat.SQT_PERC != myType) {
                throw new UploadServiceFactoryException("We do not currently have support for the SQT format: "+myType);
            }

            if (sqtType == null) sqtType = myType;
            if (myType != sqtType) {
                String errMsg = "Multiple file formats found in directory: "+fileDirectory+"\n"+
                "\tFound: "+sqtType.getFormatType()+" and "+myType.getFormatType();
                throw new UploadServiceFactoryException(errMsg);
            }
        }
        if (sqtType == null) {
            throw new UploadServiceFactoryException("No valid SQT file format found");
        }
        
        return sqtType;
    }
    
    private SearchFileFormat getPepxmlType(String fileDirectory, Set<String> filenames) throws UploadServiceFactoryException {
    
        SearchFileFormat pepxmlType = null;
        
        // make sure all files are of the same type
        for (String file: filenames) {
            String pepxmlFile = fileDirectory+File.separator+file;
            // first make sure the file exists
            if (!(new File(pepxmlFile).exists()))
                continue;
            if(!file.toLowerCase().endsWith(".pep.xml"))
                continue;
            if(file.startsWith("interact")) 
                continue;
            
            SearchFileFormat myType = null;
            try {
                myType = PepXmlUtils.getInstance().getSearchFileType(pepxmlFile);
            }
            catch (DataProviderException e) {
                throw new UploadServiceFactoryException("Exception getting file type: "+e.getErrorMessage());
            }
            
//            log.info("File type of "+pepxmlFile+" is: "+myType);
            
            // For now we support only SEQUEST, MASCOT and XTANDEM pepxml files. 
            if (SearchFileFormat.PEPXML_SEQ != myType && 
            	SearchFileFormat.PEPXML_COMET != myType &&
                SearchFileFormat.PEPXML_MASCOT != myType &&
                SearchFileFormat.PEPXML_XTANDEM != myType) {
                throw new UploadServiceFactoryException("We do not currently have support for the PEPXML format: "+myType);
            }

            if (pepxmlType == null) pepxmlType = myType;
            if (myType != pepxmlType) {
                String errMsg = "Multiple file formats found in directory: "+fileDirectory+"\n"+
                "\tFound: "+pepxmlType.getFormatType()+" and "+myType.getFormatType();
                throw new UploadServiceFactoryException(errMsg);
            }
        }
        if (pepxmlType == null) {
            throw new UploadServiceFactoryException("No valid PEPXML file format found");
        }
        
        return pepxmlType;
    }
    
    // ------------------------------------------------------------------------------------------------------
    // PROTEIN INFERENCE Data Upload service
    // ------------------------------------------------------------------------------------------------------
    public ProtinferUploadService getProtinferUploadService(String dataDirectory) throws UploadServiceFactoryException {
        
        if(dataDirectory == null) {
            throw new UploadServiceFactoryException("dataDirectory is null");
        }
        
        File dir = new File(dataDirectory);
        if(!dir.exists()) {
            throw new UploadServiceFactoryException("dataDirectory does not exist: "+dataDirectory);
        }
        
        if(!dir.isDirectory()) {
            throw new UploadServiceFactoryException(dataDirectory+" is not a directory");
        }
        
        File[] files = dir.listFiles();
        Set<SearchFileFormat> formats = new HashSet<SearchFileFormat>();
        Set<String> filenames = new HashSet<String>();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory())
                continue;
            String fileName = files[i].getName();
            
            String ext = null;
            if(fileName.toLowerCase().endsWith("prot.xml"))
                ext = "prot.xml";
            else if(fileName.toLowerCase().endsWith(".xml")) // Ignore all other XML files
            	continue;
            else {
                int idx = fileName.lastIndexOf(".");
                if(idx == -1)   continue;

                ext = fileName.substring(idx);
            }
           
            SearchFileFormat format = SearchFileFormat.forFileExtension(ext);
            if(format == SearchFileFormat.UNKNOWN) 
                continue;
            
            filenames.add(fileName);
            formats.add(format);
        }
        
        if(formats.size() == 0) {
            throw new UploadServiceFactoryException("No valid protein inference file format found in directory: "+dataDirectory);
        }
        
        if(formats.size() > 1) {
            throw new UploadServiceFactoryException("Multiple protein inference file formats found in directory: "+dataDirectory);
        }
        
        SearchFileFormat format = formats.iterator().next();
        if(format == SearchFileFormat.PROTXML) {
            ProtinferUploadService service = new ProtxmlDataUploadService();
            service.setDirectory(dataDirectory);
            return service;
        }
        else {
            throw new UploadServiceFactoryException("We do not currently have support for the format: "+format.toString());
        }
    }
}
