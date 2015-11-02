/**
 * SQTDataUploadService.java
 * @author Vagisha Sharma
 * Jul 15, 2008
 * @version 1.0
 */
package org.yeastrc.ms.service.sqtfile;

import java.io.File;

import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.cometParams.CometParamsParser;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public final class CometSQTDataUploadService extends SequestSQTDataUploadService {

    
    public CometSQTDataUploadService() {
        super();
    }
    
    public Program getSearchProgram() {
        return Program.COMET;
    }
    
    protected CometParamsParser parseParamsFile(String fileDirectory, final String remoteServer) throws UploadException {
        
        // parse the parameters file
        final CometParamsParser parser = new CometParamsParser();
        log.info("BEGIN Comet search UPLOAD -- parsing parameters file: "+parser.paramsFileName());
        if (!(new File(fileDirectory+File.separator+parser.paramsFileName()).exists())) {
            UploadException ex = new UploadException(ERROR_CODE.MISSING_COMET_PARAMS);
            throw ex;
        }
        try {
            parser.parseParams(remoteServer, fileDirectory);
            return parser;
        }
        catch (DataProviderException e) {
            UploadException ex = new UploadException(ERROR_CODE.PARAM_PARSING_ERROR);
            ex.setFile(fileDirectory+File.separator+parser.paramsFileName());
            ex.setErrorMessage(e.getMessage());
            throw ex;
        }
    }
    
    @Override
    String searchParamsFile() {
        CometParamsParser parser = new CometParamsParser();
        return parser.paramsFileName();
    }
}
