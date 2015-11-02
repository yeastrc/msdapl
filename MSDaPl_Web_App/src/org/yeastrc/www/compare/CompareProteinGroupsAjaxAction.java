/**
 * CompareProteinGroups.java
 * @author Vagisha Sharma
 * Apr 18, 2009
 * @version 1.0
 */
package org.yeastrc.www.compare;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinBaseDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProteinBase;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProtein;
import org.yeastrc.www.compare.dataset.Dataset;
import org.yeastrc.www.compare.dataset.DatasetSource;
import org.yeastrc.www.misc.TableHeader;
import org.yeastrc.www.misc.TableRow;
import org.yeastrc.www.misc.Tabular;
import org.yeastrc.www.user.User;
import org.yeastrc.www.user.UserUtils;

/**
 * 
 */
public class CompareProteinGroupsAjaxAction extends Action{

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
        List<Dataset> datasets = new ArrayList<Dataset>(piDatasets.size());
        datasets.addAll(piDatasets);
        if(datasets.size() == 0) {
            response.setContentType("text/html");
            response.getWriter().write("<b>No datasets found to compare</b>");
            return null;
        }
       
        // Get the protein groups for this protein in the given datasets.
        IdPickerProteinBaseDAO protDao = ProteinferDAOFactory.instance().getIdPickerProteinBaseDao();
        ProteinProphetProteinDAO ppProtDao = ProteinferDAOFactory.instance().getProteinProphetProteinDao();
        
        ArrayList<Integer> nrseqIds = new ArrayList<Integer>(1);
        nrseqIds.add(nrseqProteinId);
        
        
        List<String> groupProteinNames = new ArrayList<String>(datasets.size());
        for(Dataset dataset: datasets) {
            
            List<Integer> pinferProteinIds = protDao.getProteinIdsForNrseqIds(dataset.getDatasetId(), nrseqIds);
            if(pinferProteinIds.size() == 0) {
                groupProteinNames.add("");
                continue;
            }
            
            int pinferProteinId = pinferProteinIds.get(0); // use the first one.  The others should be part of this group
            
            Set<Integer> groupNrseqIds = new HashSet<Integer>();
            
            if(dataset.getSource() == DatasetSource.PROTINFER) {
                // get the protein
                IdPickerProteinBase idpProtein = protDao.loadProtein(pinferProteinId);

                // get all the proteins in the group
                List<IdPickerProteinBase> groupProteins = protDao.loadIdPickerGroupProteins(dataset.getDatasetId(), 
                        idpProtein.getProteinGroupLabel());

                for(IdPickerProteinBase gprot: groupProteins) {
                    groupNrseqIds.add(gprot.getNrseqProteinId());
                }
            }
            else if(dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
                
                // get the protein
                ProteinProphetProtein ppProtein = ppProtDao.loadProtein(pinferProteinId);

                // get all the proteins in the group
                List<ProteinProphetProtein> groupProteins = ppProtDao.loadProteinProphetIndistinguishableGroupProteins(dataset.getDatasetId(), 
                        ppProtein.getGroupId());

                
                for(ProteinProphetProtein gprot: groupProteins) {
                    groupNrseqIds.add(gprot.getNrseqProteinId());
                }
            }
            
            // get the names of all the proteins in the group
            if(dataset.getSource() == DatasetSource.PROTINFER || dataset.getSource() == DatasetSource.PROTEIN_PROPHET) {
                List<Integer> dbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(dataset.getDatasetId());
                
                StringBuilder buf = new StringBuilder();
                for(int nrseqId: groupNrseqIds) {
                	List<NrDbProtein> prots = NrSeqLookupUtil.getDbProteins(nrseqId, dbIds);
                	// There may be multiple entries for the given nrseqId, display the first one only
                	if(prots != null && prots.size() >0)
                		buf.append(", "+prots.get(0).getAccessionString());
                }
                if(buf.length() > 0) buf.deleteCharAt(0);
                groupProteinNames.add(buf.toString());
            }
        }
        
        request.setAttribute("datasets", datasets);
        request.setAttribute("groupProteins", groupProteinNames);
        return mapping.findForward("Success");
    }
    
    private List<Integer> parseCommaSeparated(String idString) {
        if(idString == null || idString.trim().length() == 0)
            return new ArrayList<Integer>(0);
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
    
    
    public static class ComparisonProteinGroup implements Tabular {
        
        private List<Dataset> datasets;
        private List<ComparisonProtein> proteins;
        
        
        public List<Dataset> getDatasets() {
            return datasets;
        }

        public List<ComparisonProtein> getProteins() {
            return proteins;
        }
        
        public void setDatasets(List<Dataset> datasets) {
            this.datasets = datasets;
        }
        
        public void addProtein(ComparisonProtein protein) {
            this.proteins.add(protein);
        }
        
        public int getDatasetCount() {
            return datasets.size();
        }
        
        @Override
        public int columnCount() {
            return datasets.size() + 3;
        }
        @Override
        public TableRow getRow(int row) {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public int rowCount() {
            return proteins.size();
        }
        @Override
        public List<TableHeader> tableHeaders() {
            List<TableHeader> headers = new ArrayList<TableHeader>(columnCount());
            TableHeader header = new TableHeader("ID");
            header.setWidth(5);
            header.setSortable(false);
            headers.add(header);
            
            header = new TableHeader("Name");
            header.setWidth(10);
            header.setSortable(false);
            headers.add(header);
            
            header = new TableHeader("Description");
            header.setWidth(100 - (15 + datasets.size()*2));
            header.setSortable(false);
            headers.add(header);
            
            for(Dataset dataset: datasets) {
                header = new TableHeader(String.valueOf(dataset.getDatasetId()));
                header.setWidth(2);
                header.setSortable(false);
                headers.add(header);
            }
            return headers;
        }
        
        @Override
        public void tabulate() {
            // TODO Auto-generated method stub
            
        }
        
        
    }
}
