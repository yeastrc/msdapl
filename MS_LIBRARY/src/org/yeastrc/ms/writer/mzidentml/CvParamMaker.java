/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.CvType;

/**
 * CvParamMaker.java
 * @author Vagisha Sharma
 * Aug 1, 2011
 * 
 */
public class CvParamMaker {

	private static CvParamMaker instance = null;
	
	private CvParamMaker(){}
	
	public static synchronized CvParamMaker getInstance() {
		
		if(instance == null)
			instance = new CvParamMaker();
		
		return instance;
	}
	
	public CVParamType make(String accession, String name, CvType cv) {
		
		CVParamType cvParam = new CVParamType();
        cvParam.setAccession(accession);
        cvParam.setName(name);
        
        if(cv == null) {
        	throw new IllegalArgumentException("CvType cannot be null");
        }
        String id = cv.getId();
        if(id == null) {
        	throw new IllegalArgumentException("CvType does not have an id");
        }
        
        cvParam.setCvRef(id);
        
        return cvParam;
	}
	
	public CVParamType make(String accession, String name, String value, CvType cv) {
		
		CVParamType cvParam = new CVParamType();
        cvParam.setAccession(accession);
        cvParam.setName(name);
        cvParam.setValue(value);
        
        if(cv == null) {
        	throw new IllegalArgumentException("CvType cannot be null");
        }
        String id = cv.getId();
        if(id == null) {
        	throw new IllegalArgumentException("CvType does not have an id");
        }
        
        cvParam.setCvRef(id);
        
        return cvParam;
	}
}
