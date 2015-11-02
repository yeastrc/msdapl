/**
 * 
 */
package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.EvidenceCode;
import org.yeastrc.bio.go.EvidenceUtils;

/**
 * GOEvidenceCodeConverter.java
 * @author Vagisha Sharma
 * Sep 15, 2010
 * 
 */
public class GOEvidenceCodeConverter {

	private static final Logger log = Logger.getLogger(GOEvidenceCodeConverter.class);
	
	private GOEvidenceCodeConverter() {}
	
	public static List<EvidenceCode> convert(List<String> evidenceCodesStrings) {
		
		if(evidenceCodesStrings == null || evidenceCodesStrings.size() == 0)
			return new ArrayList<EvidenceCode>(0);
		
		List<EvidenceCode> evidenceCodes = new ArrayList<EvidenceCode>();
    	for(String codeStr: evidenceCodesStrings) {
    		int id = EvidenceUtils.getEvidenceCodeId(codeStr);
    		if(id == -1) {
    			log.error("NO EvidenceCode found for :"+codeStr);
    		}
    		else {
    			EvidenceCode code = EvidenceUtils.getEvidenceCodeInstance(id);
    			evidenceCodes.add(code);
    		}
    	}
		return evidenceCodes;
	}

}
