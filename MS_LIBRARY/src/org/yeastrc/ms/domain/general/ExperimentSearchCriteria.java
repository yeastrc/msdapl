/**
 * ExperimentSearchCriteria.java
 * @author Vagisha Sharma
 * Aug 9, 2010
 */
package org.yeastrc.ms.domain.general;

import java.sql.Date;
import java.util.List;

/**
 * 
 */
public class ExperimentSearchCriteria {

	private List<Integer> searchDatabaseIds;
	private Date startDate;
	private Date endDate;
	
	
	public List<Integer> getSearchDatabaseIds() {
		return searchDatabaseIds;
	}
	public void setSearchDatabaseIds(List<Integer> searchDatabaseIds) {
		this.searchDatabaseIds = searchDatabaseIds;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
}
