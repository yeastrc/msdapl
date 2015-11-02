/**
 * ComparePeptidesAction.java
 * @author Vagisha Sharma
 * Apr 15, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class ComparePeptidesAjaxAction extends Action {

    
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

        
        // get the protein inference ids to compare
        String datasetIdString = request.getParameter("datasetIds");
        List<Dataset> piDatasets = getDatasets(datasetIdString);
        
        
        // Get the selected nrseqProteinId
        int nrseqProteinId = 0;
        if(request.getParameter("nrseqProteinId") != null) {
            try {nrseqProteinId = Integer.parseInt(request.getParameter("nrseqProteinId"));}
            catch(NumberFormatException e){}
        }
        if(nrseqProteinId <= 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>Invalid protein ID in request</b>");
            return null;
        }
        
        
        // Combine the datasets
        List<Dataset> datasets = new ArrayList<Dataset>(piDatasets.size());// + dtaDatasets.size());
        datasets.addAll(piDatasets);
        if(datasets.size() == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>No datasets found to compare</b>");
            return null;
        }
       
        
        // Do the comparison
        PeptideComparisonDataset comparison = DatasetPeptideComparer.instance().getComparisonPeptides(nrseqProteinId, datasets);
        request.setAttribute("pept_comparison", comparison);
        
        return mapping.findForward("Success");
    }

    private List<Integer> parseCommaSeparated(String idString) {
        String[] tokens = idString.split(",");
        List<Integer> ids = new ArrayList<Integer>(tokens.length);
        for(String tok: tokens) {
            String trimTok = tok.trim();
            if(trimTok.length() > 0)
                ids.add(Integer.parseInt(trimTok));
        }
        return ids;
    }
    
    private List<Dataset> getDatasets(String idString) {
        List<Integer> ids = parseCommaSeparated(idString);
        ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
        List<Dataset> datasets = new ArrayList<Dataset>(ids.size());
        for(int id: ids) {
            ProteinferRun run = runDao.loadProteinferRun(id);
            datasets.add(new Dataset(id, DatasetSource.getSourceForProtinferProgram(run.getProgram())));
        }
        return datasets;
    }
    
}
