

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.ms2File.Ms2FileReader;
import org.yeastrc.ms.util.Sha1SumCalculator;

/**
 * 
 */
public class MS2FileValidator {

    private static final Logger log = Logger.getLogger(MS2FileValidator.class);

    public static final int VALIDATION_ERR_SHA1SUM = 1;
    public static final int VALIDATION_ERR_READ = 2;
    public static final int VALIDATION_ERR_HEADER = 3;
    public static final int VALIDATION_ERR_SCAN = 4;
    public static final int VALID = 0;

    public int validateFile(String filePath) {

        log.info("VALIDATING file: "+filePath);
        
        Ms2FileReader dataProvider = new Ms2FileReader();

        String sha1sum = getSha1Sum(filePath);
        if (sha1sum == null) {
            log.error("ERROR calculating sha1sum for file: "+filePath+". EXITING...");
            return VALIDATION_ERR_SHA1SUM;
        }
        // open the file
        try {
            dataProvider.open(filePath, sha1sum);
        }
        catch (DataProviderException e) {
            log.error("ERROR reading file: "+filePath+". EXITING...", e);
            dataProvider.close();
            return VALIDATION_ERR_READ;
        }

        // read the header
        try {
            dataProvider.getRunHeader();
        }
        catch (DataProviderException e) {
            log.error("ERROR reading file: "+filePath+". EXITING...", e);
            dataProvider.close();
            return VALIDATION_ERR_HEADER;
        }

        // read the scans
        while (true) {
            try {
                if(dataProvider.getNextScan() == null)
                    break;
            }
            catch (DataProviderException e) {
                log.error("ERROR reading file: "+filePath+". EXITING...", e);
                dataProvider.close();
                return VALIDATION_ERR_SCAN;
            }
        }
        dataProvider.close();
        return VALID;
    }

    private String getSha1Sum(String filePath) {
        try {
            return Sha1SumCalculator.instance().sha1SumFor(new File(filePath));
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
