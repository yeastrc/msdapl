/**
 * MzXmlDataUploadService.java
 * @author Vagisha Sharma
 * Jun 23, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.mzxml;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.general.MsExperimentDAO;
import org.yeastrc.ms.dao.run.MsRunDAO;
import org.yeastrc.ms.dao.run.MsScanDAO;
import org.yeastrc.ms.dao.run.ms2file.MS2RunDAO;
import org.yeastrc.ms.domain.run.MsRunIn;
import org.yeastrc.ms.domain.run.MsScanIn;
import org.yeastrc.ms.domain.run.RunFileFormat;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.MzXmlDataProvider;
import org.yeastrc.ms.parser.mzxml.MzXmlFileReader;
import org.yeastrc.ms.service.SpectrumDataUploadService;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;
import org.yeastrc.ms.util.FileUtils;
import org.yeastrc.ms.util.Sha1SumCalculator;

/**
 * 
 */
public class MzXmlDataUploadService implements SpectrumDataUploadService {

    
    
    private static final Logger log = Logger.getLogger(MzXmlDataUploadService.class.getName());

    private final DAOFactory daoFactory;
    private MsScanDAO scanDao;

    public static final int SCAN_BUF_SIZE = 100;
    
    private int experimentId;
    private String dataDirectory;
    private String remoteDirectory;
    private StringBuilder preUploadCheckMsg;
    private List<String> filenames; // filenames WITH extension
    private List<RunFileFormat> fileFormats;
    private boolean preUploadCheckDone = false;
    
    int lastUploadedRunId = 0;
    private int numRunsUploaded = 0;
    
    public MzXmlDataUploadService() {
        daoFactory = DAOFactory.instance();
        scanDao = daoFactory.getMsScanDAO();
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
    public void setRemoteServer(String remoteServer) {
        throw new UnsupportedOperationException();
    }

    
    @Override
    public List<String> getFileNames() {
        List<String> filesNoExt = new ArrayList<String>(filenames.size());
        for(String filename: filenames)
            filesNoExt.add(FileUtils.removeExtension(filename));
        return filesNoExt;
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
        
        // 2. valid and supported raw data format
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                String name_uc = name.toUpperCase();
                return name_uc.endsWith("."+RunFileFormat.MZXML);
            }});
        
        Set<RunFileFormat> formats = new HashSet<RunFileFormat>();
        filenames = new ArrayList<String>();
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
            appendToMsg("No valid mzXML files found in directory: "+dataDirectory);
            return false;
        }
        this.fileFormats = new ArrayList<RunFileFormat>(formats);
        
        preUploadCheckDone = true;
        
        return true;
    }

    private void appendToMsg(String msg) {
        preUploadCheckMsg.append(msg+"\n");
    }
   
    /**
     * Uploaded the mzXML files in the directory to the database.
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
                ex.appendErrorMessage("\n\tmzXML FILES WILL NOT BE UPLOADED\n");
                throw ex;
            }
        }
        
        //  process the files in the sorted order
        Collections.sort(filenames);
        
        for (String filename: filenames) {
            try {
                String filepath = dataDirectory+File.separator+filename;
                
                int runId = uploadMzXmlRun(experimentId, filepath, remoteDirectory);
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
    
    private void deleteLastUploadedRun() {
        if (lastUploadedRunId != 0)
            deleteRun(lastUploadedRunId);
    }
    
    private static void deleteRun(Integer runId) {
        MS2RunDAO runDao = DAOFactory.instance().getMS2FileRunDAO();
        runDao.delete(runId);
    }
    
    private int uploadMzXmlRun(int experimentId, String filePath, String serverDirectory) throws UploadException {
        
        // first check if the file in already in the database. If it is, return its database id
        // If a run with the same file name and SHA-1 hash code already exists in the 
        // database we will not upload it
        String sha1Sum = calculateSha1Sum(filePath);
        String fileName = new File(filePath).getName();
        int runId = getMatchingRunId(fileName, sha1Sum);
        if (runId > 0) {
            // If this run was uploaded from a different location, upload the location
            saveRunLocation(serverDirectory, runId);
            log.info("Run with name: "+FileUtils.removeExtension(fileName)+" and sha1Sum: "+sha1Sum+
                    " found in the database; runID: "+runId);
            log.info("END mzXML FILE UPLOAD: "+fileName+"\n");
            return runId;
        }
        
        // this is a new file so we will upload it.
        MzXmlDataProvider mzxmlProvider = new MzXmlFileReader();
        try {
            mzxmlProvider.open(filePath, sha1Sum);
            runId = uploadMzXmlRun(mzxmlProvider, serverDirectory);
            return runId;
        }
        catch (DataProviderException e) {
            UploadException ex = makeUploadException(ERROR_CODE.READ_ERROR_MZXML, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch (RuntimeException e) { // most likely due to SQL exception
            UploadException ex = makeUploadException(ERROR_CODE.RUNTIME_MZXML_ERROR, e, filePath, null, e.getMessage());
            throw ex;
        }
        catch(UploadException e) {
            e.setFile(filePath);
            throw e;
        }
        finally {
            mzxmlProvider.close();
        }
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
    
    int getMatchingRunId(String fileName, String sha1Sum) {

        fileName = FileUtils.removeExtension(fileName);
        MS2RunDAO runDao = daoFactory.getMS2FileRunDAO();
        return runDao.loadRunIdForFileNameAndSha1Sum(fileName, sha1Sum);
    }
    
    private void saveRunLocation(String serverDirectory, int runId) {
        MsRunDAO runDao = daoFactory.getMsRunDAO();
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
    
    /**
     * provider should be closed after this method returns
     * @param provider
     * @param experimentId
     * @param sha1Sum
     * @return
     * @throws UploadException 
     */
    protected int uploadMzXmlRun(MzXmlDataProvider provider, final String serverDirectory) throws UploadException  {

        log.info("BEGIN mzXML FILE UPLOAD: "+provider.getFileName());
        long startTime = System.currentTimeMillis();
        
        MsRunDAO runDao = daoFactory.getMsRunDAO();


        // Get the top-level run information and upload it
        MsRunIn header;
        try {
            header = provider.getRunHeader();
        }
        catch (DataProviderException e) { // this should only happen if there was an IOException while reading the file
            UploadException ex = new UploadException(ERROR_CODE.READ_ERROR_MZXML);
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
        int runId = runDao.saveRun(header, serverDirectory);
        lastUploadedRunId = runId;
        log.info("Uploaded top-level run information with runId: "+runId);

        // upload each of the scans
        int uploaded = 0;
        
        // maintain a list of scans
        List<MsScanIn> scans = new ArrayList<MsScanIn>(SCAN_BUF_SIZE);
        
        while(true) {
            MsScanIn scan;
            try {
                scan = provider.getNextScan();
                
                if(scan == null)
                    break;
                if(scan.getMsLevel() == 1)  {
                    scanDao.save(scan, runId);
                }
                else
                    scans.add(scan);
            }
            catch (DataProviderException e) {
                UploadException ex = new UploadException(ERROR_CODE.INVALID_MZXML_SCAN);
                ex.setErrorMessage(e.getMessage());
                throw ex;
            }
            
            if(scans.size() == SCAN_BUF_SIZE) {
                uploaded += uploadScans(scans, runId);
                scans.clear();
            }
            
            uploaded++;
        }
        
        // upload any remaining scans
        if(scans.size() > 0)
            uploaded += uploadScans(scans, runId);
        scans.clear();
        
        // if no scans were uploaded for this run throw an exception
        if (uploaded == 0) {
            log.error("END mzXML FILE UPLOAD: !!!No scans were uploaded for file: "+provider.getFileName()+"("+runId+")"+"\n");
            UploadException ex = new UploadException(ERROR_CODE.INVALID_MZXML_SCAN);
            ex.setErrorMessage("No scans were uploaded for runID: "+runId);
        }
        
        long endTime = System.currentTimeMillis();
        log.info("Uploaded "+uploaded+" scans for runId: "+runId+ " in "+(endTime - startTime)/(1000L)+"seconds");
        log.info("END mzXML FILE UPLOAD: "+provider.getFileName()+"\n");
        return runId;
    }
    
    

    private int uploadScans(List<MsScanIn> scans, int runId) {
        List<Integer> autoIncrIds = scanDao.save(scans, runId);
        return scans.size();
    }
    
    public static void main(String[] args) throws UploadException {
        String directoryPath = "/Users/silmaril/WORK/UW/FLINT/mascot_test/"; // 090715_EPO-iT_1M_HCD
        MzXmlDataUploadService service = new MzXmlDataUploadService();
        service.setDirectory(directoryPath);
        service.setExperimentId(43);
        service.setRemoteDirectory("remote.directory");
//        service.setRemoteServer("remote.server");
        service.upload();
    }

	@Override
	public void setUploadFileNames(Set<String> fileNames) {
		// Do nothing. Assume all mzXML files in the directory should be uploaded. 
	}
}
