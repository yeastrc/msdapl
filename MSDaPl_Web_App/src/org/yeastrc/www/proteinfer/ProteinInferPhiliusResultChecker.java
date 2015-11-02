/**
 * ProteinInferPhiliusResultChecker.java
 * @author Vagisha Sharma
 * Sep 26, 2010
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import org.yeastrc.philius.dao.PhiliusDAOFactory;
import org.yeastrc.philius.dao.PhiliusResultDAO;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;

/**
 * 
 */
public class ProteinInferPhiliusResultChecker {

	private ProteinInferPhiliusResultChecker() {}
	
	//private static final Logger log = Logger.getLogger(ProteinInferPhiliusResultChecker.class);
	
	private static ProteinInferPhiliusResultChecker instance = null;
	
	public static synchronized ProteinInferPhiliusResultChecker getInstance() {
		if(instance == null)
			instance = new ProteinInferPhiliusResultChecker();
		return instance;
	}
	
	public boolean hasPhiliusResults(int pinferId) {
		
		List<Integer> fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
		
		PhiliusResultDAO philiusDao = PhiliusDAOFactory.getInstance().getPhiliusResultDAO();
		
		 boolean getPhiliusResults = true;
	        for(int dbId: fastaDatabaseIds)
	        	if(!philiusDao.hasResultsForDatabase(dbId))
	        		getPhiliusResults = false;
	        
	    return getPhiliusResults;
	}
}
