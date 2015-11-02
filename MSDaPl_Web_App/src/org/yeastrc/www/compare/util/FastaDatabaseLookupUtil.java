/**
 * FastaDatabaseLookupUtil.java
 * @author Vagisha Sharma
 * Mar 4, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.util;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetSource;

/**
 * 
 */
public class FastaDatabaseLookupUtil {

	private FastaDatabaseLookupUtil() {}
	
	public static List<Integer> getFastaDatabaseIds(List<? extends Dataset> datasets) {
		return getFastaDatabaseIds(datasets, false); // don't add standard databases
	}
	
	public static List<Integer> getFastaDatabaseIds(List<? extends Dataset> datasets, boolean addStandardDatabases) {
		List<Integer> fastaDatabaseIds = new ArrayList<Integer>();
        List<Integer> pinferIds = new ArrayList<Integer>();
        for(Dataset dataset: datasets)
            if(dataset.getSource() != DatasetSource.DTA_SELECT)
                pinferIds.add(dataset.getDatasetId());
        fastaDatabaseIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInferences(pinferIds,
        		addStandardDatabases);
        return fastaDatabaseIds;
	}
}
