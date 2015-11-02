/**
 * MsRunUploadService.java
 * @author Vagisha Sharma
 * Jul 14, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.ms2file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeDependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ChargeIndependentAnalysisDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2ScanChargeDAO;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeDependentAnalysis;
import org.yeastrc.ms.domain.run.ms2file.MS2ChargeIndependentAnalysis;
import org.yeastrc.ms.domain.run.ms2file.MS2NameValuePair;
import org.yeastrc.ms.domain.run.ms2file.MS2RunIn;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanCharge;
import org.yeastrc.ms.domain.run.ms2file.MS2ScanIn;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeDependentAnalysisDb;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2ChargeIndependentAnalysisDb;
import org.yeastrc.ms.domain.run.ms2file.impl.MS2Header;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.MS2RunDataProvider;
import org.yeastrc.ms.parser.ms2File.Cms2FileReader;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.service.SpectrumDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.FileUtils;
import org.yeastrc.ms.util.Sha1SumCalculator;

/**
 * 
 */
public class MS2DataUploadService implements SpectrumDataUploadService {

    private static final Logger log = Logger.getLogger(MS2DataUploadService.class);

    private static final DAOFactory daoFactory = DAOFactory.instance();

    public static final int BUF_SIZE = 1000;
    
    // these are the things we will cache and do bulk-inserts
    private List<MS2ChargeDependentAnalysis> dAnalysisList;
    private List<MS2ChargeIndependentAnalysis> iAnalysisList;
    
    int lastUploadedRunId = 0;
    
    private int numRunsUploaded = 0;
    
    private int experimentId;
    private String dataDirectory;
    private String remoteDirectory;
    private StringBuilder preUploadCheckMsg;
    private List<String> filenames; // filenames WITH extension
    private List<RunFileFormat> fileFormats;
    private boolean preUploadCheckDone = false;
    
    private Set<String> filesToUpload;
    private boolean hasLabkeyBullseyeOutput = false;
    
    public MS2DataUploadService() {
        dAnalysisList = new ArrayList<MS2ChargeDependentAnalysis>();
        iAnalysisList = new ArrayList<MS2ChargeIndependentAnalysis>();
        
        filenames = new ArrayList<String>();
        fileFormats = new ArrayList<RunFileFormat>();
    }

    protected void resetCaches() {
        dAnalysisList.clear();
        iAnalysisList.clear();
        
        lastUploadedRunId = 0;
    }
    
    private void deleteLastUploadedRun() {
        if (lastUploadedRunId != 0)
            deleteRun(lastUploadedRunId);
    }
    
    private static void deleteRun(Integer runId) {
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();
        runDao.delete(runId);
    }
    
    /**
     * Uploaded the ms2 files in the directory to the database.
     * This method returns the experimentId that was set for this uploader via the 
     * setExperimentId method.
     * @throws UploadException
     */
    @Override
    public void upload() throws UploadException {
        
        if(!preUploadCheckDone) {
            if(!preUploadCheckPassed()) {
                UploadException ex = new UploadException(ERROR_CODE.PREUPLOAD_CHECK_FALIED);
                ex.appendErrorMessage(this.getPreUploadCheckMsg());
                ex.appendErrorMessage("\n\tMS2/CMS2 FILES WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        for (String filename: filenames) {
        	
        	// Upload only those files we are required to upload.
        	// filenames already contain only those files that we should upload.
//        	if(filesToUpload != null && !filesToUpload.contains(FileUtils.removeExtension(filename))) {
//        		log.info("Skipping file "+filename);
//        		continue;
//        	}
        	
            try {
                String filepath = dataDirectory+File.separator+filename;
                
                int runId = uploadMS2Run(experimentId, filepath, remoteDirectory);
                // link experiment and run
                linkExperimentAndRun(experimentId, runId);
                numRunsUploaded++;
            }
            catch (UploadException e) {
                deleteLastUploadedRun();
                throw e;
            }
        }
    }
    
    private String getBaseFileName(String filename, boolean removeExt) {
    	
    	String name = FileUtils.removeExtension(filename);
    	if(this.hasLabkeyBullseyeOutput && name.endsWith(".matches")) {
    		name = FileUtils.removeExtension(name);
    	}
    	return name;	
    }
    
    private String getBaseFileName(String filename) {
    	return getBaseFileName(filename, true);
    }
    
    private int uploadMS2Run(int experimentId, String filePath, String serverDirectory) throws UploadException {
        
        // first check if the file in already in the database. If it is, return its database id
        // If a run with the same file name and SHA-1 hash code already exists in the 
        // database we will not upload it
        String sha1Sum = calculateSha1Sum(filePath);
        String fileName = new File(filePath).getName();
        int runId = getMatchingRunId(getBaseFileName(fileName), sha1Sum);
        if (runId > 0) {
            // If this run was uploaded from a different location, upload the location
            saveRunLocation(serverDirectory, runId);
            log.info("Run with name: "+getBaseFileName(fileName)+" and sha1Sum: "+sha1Sum+
                    " found in the database; runID: "+runId);
            log.info("END MS2/CMS2 FILE UPLOAD: "+fileName+"\n");
            return runId;
        }
        
        // this is a new file so we will upload it.
        RunFileFormat format = RunFileFormat.forFile(fileName); // get the format for this file
        MS2RunDataProvider ms2Provider = getMS2DataProvider(format);
        try {
            ms2Provider.open(filePath, sha1Sum);
            runId = uploadMS2Run(ms2Provider, serverDirectory);
            return runId;
        }
        catch (DataProviderException e) {
            UploadException ex = makeUploadException(ERROR_CODE.READ_ERROR_MS2, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = makeUploadException(ERROR_CODE.RUNTIME_MS2_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch(UploadException e) {
            e.setFile(filePath);
            throw e;
        }
        finally {
            ms2Provider.close();
        }
    }

    private MS2RunDataProvider getMS2DataProvider(RunFileFormat format) {
        if(format == RunFileFormat.MS2) {
            return new Ms2FileReader();
        }
        else if(format == RunFileFormat.CMS2) {
            return new Cms2FileReader();
        }
        return null;
    }
    
    private void saveRunLocation(String serverDirectory, int runId) {
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();
        // Save the original location (on remote server) of the MS2 file, if the location is not in the database already.
        int runLocs = runDao.loadMatchingRunLocations(runId, serverDirectory);
        if (runLocs == 0) {
            runDao.saveRunLocation(serverDirectory, runId);
        }
    }
    
    private void linkExperimentAndRun(int experimentId, int runId) {
        MsExperimentDAO exptDao = daoFactory.getMsExperimentDAO();
        // an entry will be made in the msExperimentRun table only if 
        // it does not already exists. 
        exptDao.saveExperimentRun(experimentId, runId);
    }
    
    private String calculateSha1Sum(String filePath) throws UploadException {
        String sha1Sum;
        try {
            sha1Sum = Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        }
        catch (Exception e) {
            UploadException ex = makeUploadException(ERROR_CODE.SHA1SUM_CALC_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        return sha1Sum;
    }
    
    int getMatchingRunId(String fileName, String sha1Sum) {

        fileName = FileUtils.removeExtension(fileName);
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();
        return runDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
    }
    
    /**
     * provider should be closed after this method returns
     * @param provider
     * @param serverDirectory
     * @return
     * @throws UploadException 
     */
    protected int uploadMS2Run(MS2RunDataProvider provider, final String serverDirectory) throws UploadException  {

        log.info("BEGIN MS2 FILE UPLOAD: "+provider.getFileName());
        long startTime = System.currentTimeMillis();
        
        // reset all caches.
        resetCaches();
        
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();


        // Get the top-level run information and upload it
        MS2RunIn header;
        try {
            header = provider.getRunHeader();
            
            // Support for LabKey pipeline. Bullseye generates two files: <filename>.matches.cms2 and <filename>.nomatches.cms2.
            // <filename>.matches.cms2 is used as input to Sequest but the output SQT file is called <filename>.sqt. 
            // To avoid this file name mismatch we will remove the ".matches" string when saving the filename for the run.
            if(header instanceof MS2Header)
            {
            	String fileName = getBaseFileName(header.getFileName(), false);
            	((MS2Header)header).setFileName(fileName);
            }
        }
        catch (DataProviderException e) { // this should only happen if there was an IOException while reading the file
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_MS2);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        int runId = runDao.saveRun(header, serverDirectory);
        lastUploadedRunId = runId;
        log.info("Uploaded top-level run information with runId: "+runId);

        // upload each of the scans
        MsScanDAO scanDao = daoFactory.getMsScanDAO();
        int all = 0;
        int uploaded = 0;
        while(true) {
            MS2ScanIn scan;
            try {
                scan = provider.getNextScan();
                if(scan == null)
                    break;
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_MS2_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            // MS2 file scans may have a precursor scan number but the precursor scans are not in the database
            // so we do not have a database id for the precursor scan. 
            // int precursorScanId = scanDao.loadScanIdForScanNumRun(scan.getPrecursorScanNum(), runId);
            int precursorScanId = 0;
            int scanId = scanDao.save(scan, runId, precursorScanId); 

            // save charge independent analysis
            saveChargeIndependentAnalysis(scan, scanId);

            // save the scan charge states for this scan
            MS2ScanChargeDAO chargeDao = daoFactory.getMS2FileScanChargeDAO();
            for (MS2ScanCharge scanCharge: scan.getScanChargeList()) {
                int scanChargeId = chargeDao.saveScanChargeOnly(scanCharge, scanId);
                saveChargeDependentAnalysis(scanCharge, scanChargeId);
            }

            uploaded++;
            all++;
        }
        
        // if no scans were uploaded for this run throw an exception
        if (uploaded == 0) {
            log.error("END MS2 FILE UPLOAD: !!!No scans were uploaded for file: "+provider.getFileName()+"("+runId+")"+"\n");
            UploadException ex = new UploadException(ERROR_CODE.INVALID_MS2_SCAN);
            ex.setErrorMessage("No scans were uploaded for runID: "+runId);
        }
        
        flush(); // save any cached data
        
        long endTime = System.currentTimeMillis();
        log.info("Uploaded "+uploaded+" out of "+all+" scans for runId: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        log.info("END MS2 FILE UPLOAD: "+provider.getFileName()+"\n");
        return runId;
    }

    protected void saveChargeDependentAnalysis(MS2ScanCharge scanCharge, final int scanChargeId) {
        if (dAnalysisList.size() > BUF_SIZE) {
            saveChargeDependentAnalysis();
        }
        
        for (final MS2NameValuePair dAnalysis: scanCharge.getChargeDependentAnalysisList()) {
            dAnalysisList.add(new MS2ChargeDependentAnalysisDb(dAnalysis, scanChargeId));
        }
    }

    private void saveChargeDependentAnalysis() {
        MS2ChargeDependentAnalysisDAO dao = daoFactory.getMs2FileChargeDAnalysisDAO();
        dao.saveAll(dAnalysisList);
        dAnalysisList.clear();
    }

    protected void saveChargeIndependentAnalysis(MS2ScanIn scan, final int scanId) {
        if (iAnalysisList.size() > BUF_SIZE) {
            saveChargeIndependentAnalysis();
        }
        
        for (final MS2NameValuePair iAnalysis: scan.getChargeIndependentAnalysisList()) {
            iAnalysisList.add(new MS2ChargeIndependentAnalysisDb(iAnalysis, scanId));
        }
    }

    private void saveChargeIndependentAnalysis() {
        MS2ChargeIndependentAnalysisDAO dao = daoFactory.getMs2FileChargeIAnalysisDAO();
        dao.saveAll(iAnalysisList);
        iAnalysisList.clear();
    }
    
    protected void flush() {
        if (iAnalysisList.size() > 0)
            saveChargeIndependentAnalysis();
        if (dAnalysisList.size() > 0)
            saveChargeDependentAnalysis();
    }
    
    private UploadException makeUploadException(ERROR_CODE errCode, Exception sourceException, String file, String directory, String message) {
        UploadException ex = null;
        if (sourceException == null)
            ex = new UploadException(errCode);
        else
            ex = new UploadException(errCode, sourceException);
        ex.setFile(file);
        ex.setDirectory(directory);
        ex.setErrorMessage(message);
//        log.error(ex.getMessage(), ex);
        return ex;
    }

    
    
    @Override
    public void setExperimentId(int experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public void setDirectory(String directory) {
        this.dataDirectory = directory;
    }
    
    @Override
    public void setRemoteDirectory(String remoteDirectory) {
        this.remoteDirectory = remoteDirectory;
    }
    
    @Override
    public boolean preUploadCheckPassed() {
        
        preUploadCheckMsg = new StringBuilder();
        
        // checks for
        // 1. valid data directory
        File dir = new File(dataDirectory);
        if(!dir.exists()) {
            appendToMsg("Data directory does not exist: "+dataDirectory);
            return false;
        }
        if(!dir.isDirectory()) {
            appendToMsg(dataDirectory+" is not a directory");
            return false;
        }
        
        // 2. Get a list of files
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toUpperCase();
                if(hasLabkeyBullseyeOutput) // Support for LabKey pipeline
                	return name_uc.endsWith(".MATCHES."+RunFileFormat.MS2) || name_uc.endsWith(".MATCHES."+RunFileFormat.CMS2);
                else
                	return name_uc.endsWith("."+RunFileFormat.MS2) || name_uc.endsWith("."+RunFileFormat.CMS2);
            }});
        
        
        // 3. valid and supported raw data format
        Set<RunFileFormat> formats = new HashSet<RunFileFormat>();
        for (int i = 0; i < files.length; i++) {
            if(files[i].isDirectory())
                continue;
            RunFileFormat format = RunFileFormat.forFile(files[i].getName());
            if(format == RunFileFormat.UNKNOWN) 
                continue;
            
            formats.add(format);
            filenames.add(files[i].getName()); // storing filename WITH extension
        }
        
        if(formats.size() == 0) {
            appendToMsg("No valid MS2 / CMS2 files found in directory: "+dataDirectory);
            return false;
        }
        this.fileFormats = new ArrayList<RunFileFormat>(formats);
        
        
        // 4. If we were given a set of file names, keep only those
        if(this.filesToUpload != null && this.filesToUpload.size() > 0) {
        	
        	Iterator<String> filenameIter = filenames.iterator();
        	while(filenameIter.hasNext()) {
        		String name = filenameIter.next();
        		String noExtName = getBaseFileName(name);
        		if(!filesToUpload.contains(noExtName)) {
        			filenameIter.remove();
        			log.info("Skipping file: "+name);
        		}
        	}
        }
        
        
        // 5. Make sure we do not have a .ms2 and a .cms2 file with the same name
        Set<String> uniqFiles = new HashSet<String>(filenames.size());
        for(String filename: filenames) {
            String fileNoExt = FileUtils.removeExtension(filename);
            if(uniqFiles.contains(fileNoExt)) {
                appendToMsg("Found two files with the same name: "+fileNoExt);
                return false;
            }
            uniqFiles.add(fileNoExt);
        }
        
        preUploadCheckDone = true;
        
        return true;
    }
    
    private void appendToMsg(String msg) {
        preUploadCheckMsg.append(msg+"\n");
    }
    
    @Override
    public String getPreUploadCheckMsg() {
        return preUploadCheckMsg.toString();
    }

    @Override
    public String getUploadSummary() {
        StringBuilder buf = new StringBuilder("\tRun file format: ");
        for(RunFileFormat fmt: fileFormats)
            buf.append(fmt.name()+" ");
        buf.append("\n\t#Runs in Directory: ");
        buf.append(filenames.size());
        buf.append("; #Uploaded: "+numRunsUploaded);
        return buf.toString();
    }

    @Override
    public void setRemoteServer(String remoteServer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getFileNames() {
        List<String> filesNoExt = new ArrayList<String>(filenames.size());
        for(String filename: filenames) {
            filesNoExt.add(getBaseFileName(filename));
        }
        return filesNoExt;
    }

	@Override
	public void setUploadFileNames(Set<String> fileNames) {
		this.filesToUpload = fileNames;
	}

	public boolean isHasLabkeyBullseyeOutput() {
		return hasLabkeyBullseyeOutput;
	}

	public void setHasLabkeyBullseyeOutput(boolean hasLabkeyBullseyeOutput) {
		this.hasLabkeyBullseyeOutput = hasLabkeyBullseyeOutput;
	}
}
