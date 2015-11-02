import java.io.File;
import java.util.Date;

import org.yeastrc.ms.service.MsDataUploader;
import org.yeastrc.ms.service.UploadException;

/**
 * MsDataUploaderTestForDan.java
 * @author Vagisha Sharma
 * Apr 22, 2011
 */

/**
 * 
 */
public class MsDataUploaderTestForDan {

	public static void main(String[] args) throws UploadException {
		
        long start = System.currentTimeMillis();

        String directory = "./test_resources/validSequestData_dir";
        
        if(directory == null || directory.length() == 0 || !(new File(directory).exists()))
            System.out.println("Invalid directory: "+directory);
        
        
        MsDataUploader uploader = new MsDataUploader();
        uploader.setRemoteServer("local");
        uploader.setRemoteSpectrumDataDirectory(directory);
        
        // Where the .ms2 files live
        uploader.setSpectrumDataDirectory(directory);
        
        // Where the .sqt files live
        uploader.setSearchDirectory(directory);
        
        
        // Where the Percolator / PeptideProphet results live
        // Yates lab data does not have these results, so we don't need this part
        // uploader.setAnalysisDirectory(directory+File.separator+"pipeline"+File.separator+"percolator");
        
        // Where the protein inference results live (example: results from ProteinProphet)
        // Yates lab's program for protein inference is called DTASelect
        // Since we don't yet have support for DTASelect results in the msData schema we don't need this part either
        // uploader.setProtinferDirectory(directory);
        
        uploader.setSearchDate(new Date());
        
        uploader.uploadData();
        
        long end = System.currentTimeMillis();
        System.out.println("TOTAL TIME: "+((end - start)/(1000L))+"seconds.");
    }
}
