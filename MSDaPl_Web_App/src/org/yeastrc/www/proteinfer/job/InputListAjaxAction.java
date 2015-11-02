/**
 * InputListAjaxAction.java
 * @author Vagisha Sharma
 * Jan 30, 2009
 * @version 1.0
 */
package org.yeastrc.www.proteinfer.job;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.domain.protinfer.ProteinferInput.InputType;
import org.yeastrc.www.proteinfer.ProteinInferInputSummary;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class InputListAjaxAction extends Action {


    private static final Logger log = Logger.getLogger(InputListAjaxAction.class);
    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {
        
        
        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            response.getWriter().write("You are not logged in!");
            response.setStatus(HttpServletResponse.SC_SEE_OTHER); // Status code (303) indicating that the response to the request can be found under a different URI.
            return null;
        }

        // look for the input ids in the request
        String inputIds = request.getParameter("inputIds");
        List<Integer> inputIdList = new ArrayList<Integer>();
        if(inputIds != null) {
            inputIds = inputIds.replaceAll("\\s", "");
            String[] tokens = inputIds.split(",");
            for(String tok: tokens) {
                inputIdList.add(Integer.parseInt(tok));
            }
        }
        if(inputIdList.size() == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>No input search / analysis IDs found in request</b>");
            return null;
        }
        
        // look for the type of input requested
        String inputTypeParam = request.getParameter("inputType");
        InputType inputType = null;
        if(inputTypeParam != null) {
            inputType = InputType.getInputTypeForChar(inputTypeParam.charAt(0));
        }
        if(inputType == null) {
            response.setContentType("text/html");
            response.getWriter().write("<b>No input type found in request</b>");
            return null;
        }
        request.setAttribute("inputType", inputType);
        
        
        // input files use Struts' indexed properties. Look for the current index in the requesting page
        String currIdxStr = request.getParameter("index");
        int index = -1;
        try {
            index = Integer.parseInt(currIdxStr);
        }
        catch(NumberFormatException e) {}
        if(index == -1) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid index "+index+" found in request</b>");
            return null;
        }
        request.setAttribute("index", index);
        
        // get the input files
        ProteinInferInputGetter inputGetter = ProteinInferInputGetter.instance();
        List<ProteinInferInputSummary> inputSummaryList = new ArrayList<ProteinInferInputSummary>();
        if(inputType == InputType.SEARCH) {
            for(int inputId: inputIdList) {
                inputSummaryList.add(inputGetter.getInputSearchSummary(inputId));
            }
        }
        else if(inputType == InputType.ANALYSIS) {
            for(int inputId: inputIdList) {
                inputSummaryList.add(inputGetter.getInputAnalysisSummary(inputId));
            }
        }
        request.setAttribute("inputList", inputSummaryList);
        
        
        // Go!
        return mapping.findForward("Success");
    }
}
