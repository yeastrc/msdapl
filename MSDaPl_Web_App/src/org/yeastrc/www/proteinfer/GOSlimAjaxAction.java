/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOAnalysisProtein;
import org.yeastrc.bio.go.slim.GOSlimAnalysis;
import org.yeastrc.bio.go.slim.GOSlimCalculator;
import org.yeastrc.bio.go.slim.GOSlimFilter;
import org.yeastrc.bio.go.slim.GOSlimFilterBuilder;
import org.yeastrc.bio.go.slim.GOSlimTermResult;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.GOSlimChartUrlCreator;

/**
 * GOSlimAjaxAction.java
 * @author Vagisha Sharma
 * Jul 2, 2010
 * 
 */
public class GOSlimAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(GOSlimAjaxAction.class.getName());

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		
		long s = System.currentTimeMillis();
		
        int goAspect = (Integer)request.getAttribute("goAspect");
        int goSlimTermId = (Integer) request.getAttribute("goSlimTermId");
        List<GOAnalysisProtein> proteins = (List<GOAnalysisProtein>) request.getAttribute("goSlimProteins");
        boolean proteinGroupAnalysis = (Boolean)(request.getAttribute("doGroupAnalysis"));
        
        
        GOSlimFilterBuilder filterBuilder = new GOSlimFilterBuilder();
        filterBuilder.setGoAspect(goAspect);
        filterBuilder.setGoSlimTermId(goSlimTermId);
        GOSlimFilter filter = filterBuilder.build();
        
        GOSlimCalculator calculator = new GOSlimCalculator();
        calculator.setCalculateForProteinGroups(proteinGroupAnalysis);
        calculator.setFilter(filter);
        
        for(GOAnalysisProtein protein: proteins)
        	calculator.addProtein(protein);
        
        GOSlimAnalysis goAnalysis = calculator.calculate();
		
        if(proteinGroupAnalysis) {
			
			Collections.sort(goAnalysis.getTermNodes(), new Comparator<GOSlimTermResult>() {
				@Override
				public int compare(GOSlimTermResult o1, GOSlimTermResult o2) {
					return Integer.valueOf(o2.getAnnotatedGroupCount()).compareTo(o1.getAnnotatedGroupCount());
				}
			});
		}
		else {
			
			Collections.sort(goAnalysis.getTermNodes(), new Comparator<GOSlimTermResult>() {
				@Override
				public int compare(GOSlimTermResult o1, GOSlimTermResult o2) {
					return Integer.valueOf(o2.getAnnotatedProteinCount()).compareTo(o1.getAnnotatedProteinCount());
				}
			});
		}

        request.setAttribute("goAnalysis",goAnalysis);
        
        if(goAnalysis.getNumAnnotated() > 0) {
        	
        	
        	String pieChartUrlGroup = GOSlimChartUrlCreator.getPieChartUrl(goAnalysis, 15, proteinGroupAnalysis);
        	request.setAttribute("pieChartUrlGroup", pieChartUrlGroup);
        	
        	String pieChartUrlProtein = GOSlimChartUrlCreator.getPieChartUrl(goAnalysis, 15, false);
        	request.setAttribute("pieChartUrlProtein", pieChartUrlProtein);

        	String barChartUrlGroup = GOSlimChartUrlCreator.getBarChartUrl(goAnalysis, 15, proteinGroupAnalysis);
        	request.setAttribute("barChartUrlGroup", barChartUrlGroup);
        	
        	String barChartUrlProtein = GOSlimChartUrlCreator.getBarChartUrl(goAnalysis, 15, false);
        	request.setAttribute("barChartUrlProtein", barChartUrlProtein);
        }
        
		long e = System.currentTimeMillis();
		log.info("GOSlimAjaxAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
		return mapping.findForward("Success");
	}
}
