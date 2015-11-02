/**
 * PepXmlSequestDataUploadService.java
 * @author Vagisha Sharma
 * Oct 6, 2009
 * @version 1.0
 */
package org.yeastrc.ms.service.pepxml;

import java.io.File;

import org.apache.log4j.Logger;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.cometParams.CometParamsParser;
import org.yeastrc.ms.service.UploadException;
import org.yeastrc.ms.service.UploadException.ERROR_CODE;

/**
 * 
 */
public class PepXmlCometDataUploadService extends PepXmlSequestDataUploadService {

    private static final Logger log = Logger.getLogger(PepXmlCometDataUploadService.class.getName());
    
    
    public PepXmlCometDataUploadService() {
        
        super();
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.COMET;
    }
    
    protected String searchParamsFile() {
        CometParamsParser parser = new CometParamsParser();
        return parser.paramsFileName();
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
}
