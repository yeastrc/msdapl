/**
 * SequestPepxmlDownloadFormAction.java
 * @author Vagisha Sharma
 * Apr 13, 2009
 * @version 1.0
 */
package org.yeastrc.www.project.experiment;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.experiment.ExperimentFile;
import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.search.MsRunSearchDAO;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class SequestPepxmlDownloadFormAction extends Action {

    
    public ActionForward execute( ActionMapping mapping,
            ActionForm form,
            HttpServletRequest request,
            HttpServletResponse response )
    throws Exception {



        // User making this request
        User user = UserUtils.getUser(request);
        if (user == null) {
            ActionErrors errors = new ActionErrors();
            errors.add("username", new ActionMessage("error.login.notloggedin"));
            saveErrors( request, errors );
            return mapping.findForward("authenticate");
        }

        // TODO Does the user have access to look at these results? 

        int searchId = 0;
        try {
            String strID = request.getParameter("ID");
            if(strID != null)
                searchId = Integer.parseInt(strID);


        } catch (NumberFormatException nfe) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Sequest search"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        // If we still don't have a valid id, return an error
        if(searchId == 0) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.invalid.id", "Sequest search"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }

        // Create the form
        SequestPepxmlDownloadForm myForm =  new SequestPepxmlDownloadForm();
        
        myForm.setSearchId(searchId);
        myForm.setMinXCorr_1("0.0");
        myForm.setMinXCorr_2("0.0");
        myForm.setMinXCorr_3("0.0");
        myForm.setMinXCorr_H("0.0");

        myForm.setFileList(getFiles(searchId));


        // required attributes in the request
        request.setAttribute("filterForm", myForm);
        request.setAttribute("searchId", searchId);


        // Forward them on to the happy success view page!
        return mapping.findForward("Success");

    }

    
    private List<ExperimentFile> getFiles(int searchId) {

        MsRunSearchDAO saDao = DAOFactory.instance().getMsRunSearchDAO();
        List<Integer> runSearchIds = saDao.loadRunSearchIdsForSearch(searchId);

        List<ExperimentFile> files = new ArrayList<ExperimentFile>(runSearchIds.size());
        for(int runSearchId: runSearchIds) {
            String filename = saDao.loadFilenameForRunSearch(runSearchId);
            ExperimentFile file = new ExperimentFile(runSearchId, filename);
            file.setSelected(true);
            files.add(file);
        }
        return files;
    }
}
