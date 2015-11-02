import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;


public class YatesRunMS2Validator {

    private static final Logger log = Logger.getLogger(YatesRunMS2Validator.class);
    
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        List<Integer> yatesRunIds = YatesTablesUtils.getAllYatesRunIds("ORDER BY runID DESC");
        if (yatesRunIds.size() == 0) {
            log.error("No yates runids found!!");
            return;
        }
        
        String dataDir = "/Users/vagisha/WORK/MS_LIBRARY/YATES_CYCLE_DUMP/ms2_validation";
        
        for (Integer runId: yatesRunIds) {
            List<YatesTablesUtils.YatesCycle> cycles = YatesTablesUtils.getCyclesForRun(runId);
            if (cycles.size() == 0) {
                log.fatal("\n"+runId+": INVALID No cycles found");
                continue;
            }
            
            String runDir = dataDir+File.separator+runId;
            new File(runDir).mkdir();
            
            // download the ms2 files first
            for (YatesTablesUtils.YatesCycle cycle: cycles) {
                YatesCycleDownloader downloader = new YatesCycleDownloader();
                downloader.downloadMS2File(cycle.cycleId, runDir, cycle.cycleName+".ms2");
            }
            
            // validate ms2 files in directory
            if (validateMs2InDirectory(runDir)) {
                log.info("\n"+runId+": VALID");
                // delete the files first
                for (YatesTablesUtils.YatesCycle cycle: cycles) {
                    log.info("Deleting yates cycle ms2 files......");
                    new File(runDir+File.separator+cycle.cycleName+".ms2").delete();
                }
                // delete the directory
                new File(runDir).delete();
            }
            else {
                log.fatal("\n"+runId+": INVALID one or more runs is invalid");  
            }
        }
    }
    
    private static boolean validateMs2InDirectory(String directory) {
        if (!(new File(directory).exists())) {
            log.error("Directory does not exist: "+directory);
            return false;
        }
        
        String files[] = new File(directory).list();
        if (files.length == 0) {
            log.error("No files found in directory: "+directory);
            return false;
        }
        
        MS2FileValidator validator = new MS2FileValidator();
        boolean allValid = true;
        for (String file: files) {
            String path = directory+File.separator+file;
            int validationCode = validator.validateFile(path);
            switch(validationCode) {
                case MS2FileValidator.VALID : 
                    // do nothing
                    break;
                default: 
                    allValid = false;
            }
        }
        
        return allValid;
    }
}
