/**
 * ProteinInferSessionManager.java
 * @author Vagisha Sharma
 * Mar 20, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.yeastrc.ms.domain.protinfer.ProteinFilterCriteria;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetFilterCriteria;

/**
 * 
 */
public class ProteinInferSessionManager {

	private List<Integer> idsInSession;
	
	private final int maxCount = 4;
	private final String filterCriteriaStr = "filterCriteria";
	private final String proteinListStr = "proteinList";
	
	private static ProteinInferSessionManager instance = new ProteinInferSessionManager();
	
	private ProteinInferSessionManager () {
		idsInSession = new ArrayList<Integer>();
	}
	
	public static ProteinInferSessionManager getInstance() {
		return instance;
	}
	
	public ProteinFilterCriteria getFilterCriteriaForIdPicker(HttpServletRequest request, int proteinInferId) {
		
		return (ProteinFilterCriteria) request.getSession().getAttribute(filterCriteriaStr+"_"+proteinInferId);
	}
	
	public ProteinProphetFilterCriteria getFilterCriteriaForProteinProphet(HttpServletRequest request, int proteinInferId) {
		
		return (ProteinProphetFilterCriteria) request.getSession().getAttribute(filterCriteriaStr+"_"+proteinInferId);
	}
	
	public void putForIdPicker(HttpServletRequest request, int proteinInferId, 
			ProteinFilterCriteria filterCriteria, List<Integer> proteinIds) {
		
		synchronized (idsInSession) {
			
			if(idsInSession.size() >= maxCount) {
				removeFirst(request);
			}
			request.getSession().setAttribute(filterCriteriaStr+"_"+proteinInferId, filterCriteria);
			request.getSession().setAttribute(proteinListStr+"_"+proteinInferId, proteinIds);
			idsInSession.add(proteinInferId);
		}
	}
	
	public void putForProteinProphet(HttpServletRequest request, int proteinInferId, 
			ProteinProphetFilterCriteria filterCriteria, List<Integer> proteinIds) {
		
		synchronized (idsInSession) {
			
			if(idsInSession.size() >= maxCount) {
				removeFirst(request);
			}
			request.getSession().setAttribute(filterCriteriaStr+"_"+proteinInferId, filterCriteria);
			request.getSession().setAttribute(proteinListStr+"_"+proteinInferId, proteinIds);
			idsInSession.add(proteinInferId);
		}
	}
	
	private void removeFirst(HttpServletRequest request) {
			
		int proteinInferId = idsInSession.get(0);
		request.getSession().removeAttribute(filterCriteriaStr+"_"+proteinInferId);
		request.getSession().removeAttribute(proteinListStr+"_"+proteinInferId);
		idsInSession.remove(0);
	}

	
	
	public List<Integer> getStoredProteinIds(HttpServletRequest request, int proteinInferId) {
		
		return (List<Integer>) request.getSession().getAttribute(proteinListStr+"_"+proteinInferId);
	}
	
	public boolean hasIdPickerInformation(HttpServletRequest request, int pinferId) {
		
		return (getStoredProteinIds(request, pinferId) != null &&
				getFilterCriteriaForIdPicker(request, pinferId) != null);
	}
	
	public boolean hasProteinProphetInformation(HttpServletRequest request, int pinferId) {
		
		return (getStoredProteinIds(request, pinferId) != null &&
				getFilterCriteriaForProteinProphet(request, pinferId) != null);
	}
	
	public boolean isCurrent(int pinferId) {
		synchronized(idsInSession) {
			return idsInSession.contains(pinferId);
		}
	}
	
}
