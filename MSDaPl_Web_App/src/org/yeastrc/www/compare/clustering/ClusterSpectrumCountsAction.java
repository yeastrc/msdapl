/**
 * ClusterSpectrumCountsAction.java
 * @author Vagisha Sharma
 * Apr 18, 2010
 * @version 1.0
 */
package org.yeastrc.www.compare.clustering;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.ProteinComparisonDataset;
import org.yeastrc.www.compare.ProteinGroupComparisonDataset;
import org.yeastrc.www.compare.ProteinSetComparisonForm;
import org.yeastrc.www.compare.clustering.ClusteringConstants.GRADIENT;
import org.yeastrc.www.compare.clustering.SpectrumCountClusterer.ROptions;

/**
 * 
 */
public class ClusterSpectrumCountsAction extends Action {

	private static final Logger log = Logger.getLogger(ClusterSpectrumCountsAction.class.getName());
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        log.info("Clustering spectrum counts for comparison results");
        
        ProteinSetComparisonForm myForm = (ProteinSetComparisonForm) request.getAttribute("comparisonForm");
        if(myForm == null) {
            ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison form not found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        String jobToken = myForm.getClusteringToken();
        // Look for it in the session
        if(jobToken == null || jobToken.trim().length() == 0) {
        	jobToken = (String)request.getSession().getAttribute("clustering_token");
        }
        if(jobToken == null || jobToken.trim().length() == 0) {
        	ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Token for clustering not found in request"));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // put the token in the session
        request.getSession().setAttribute("clustering_token", jobToken);
        
        // put it in the form
        myForm.setClusteringToken(jobToken);
        
        // now mark the token as old
		myForm.setNewToken(false);
        
		StringBuilder errorMessage = new StringBuilder();
        String baseDir = request.getSession().getServletContext().getRealPath(ClusteringConstants.BASE_DIR);
        baseDir = baseDir+File.separator+jobToken;
        
        ROptions ropts = new ROptions();
        ropts.setDoLog(myForm.isUseLogScale());
        ropts.setLogBase(myForm.getLogBase());
        ropts.setValueForMissing(myForm.getReplaceMissingWithValueDouble());
        ropts.setGradient(myForm.getHeatMapGradient());
        ropts.setClusterColumns(myForm.isClusterColumns());
        ropts.setScaleRows(myForm.isScaleRows());
        
        // DONT GROUP INDISTINGUISHABLE PROTEINS
        if(!myForm.getGroupIndistinguishableProteins()) {
            ProteinComparisonDataset comparison = (ProteinComparisonDataset) request.getAttribute("comparisonDataset");
            if(comparison == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            long s = System.currentTimeMillis();
            
            ropts.setNumCols(comparison.getDatasetCount());
            ropts.setNumRows(comparison.getTotalProteinCount());
            
            ProteinComparisonDataset clusteredComparison = 
            SpectrumCountClusterer.getInstance().clusterProteinComparisonDataset(comparison, ropts, errorMessage, baseDir);
            if(clusteredComparison == null) {
            	ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Clustering error: "+errorMessage.toString()));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            clusteredComparison.setDisplayColumns(myForm.getDisplayColumns());
            
            long e = System.currentTimeMillis();
            log.info("Time to culster ProteinComparisonDataset: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            // Serialize the ProteinComparisonDataset
            if(!serializeObject(clusteredComparison, ClusteringConstants.PROT_SER,
            		errorMessage, baseDir)) {
            	ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", errorMessage.toString()));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
        }
        
        // GROUP INDISTINGUISHABLE PROTEINS
        else {
            ProteinGroupComparisonDataset grpComparison = (ProteinGroupComparisonDataset) request.getAttribute("comparisonGroupDataset");
            if(grpComparison == null) {
                ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Comparison dataset not found in request"));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
            long s = System.currentTimeMillis();
            
            ropts.setNumCols(grpComparison.getDatasetCount());
            ropts.setNumRows(grpComparison.getTotalProteinGroupCount());
            
            ProteinGroupComparisonDataset clusteredGrpComparison = 
            	SpectrumCountClusterer.getInstance().clusterProteinGroupComparisonDataset(grpComparison, ropts, errorMessage, baseDir);
            if(clusteredGrpComparison == null) {
            	ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", "Clustering error: "+errorMessage.toString()));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            clusteredGrpComparison.setDisplayColumns(myForm.getDisplayColumns());
            
            long e = System.currentTimeMillis();
            log.info("Time to culster ProteinGroupComparisonDataset: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
            
            
            // Serialize the ProteinGroupComparisonDataset
            if(!serializeObject(clusteredGrpComparison, ClusteringConstants.PROT_GRP_SER,
            		errorMessage, baseDir)) {
            	ActionErrors errors = new ActionErrors();
                errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", errorMessage.toString()));
                saveErrors( request, errors );
                return mapping.findForward("Failure");
            }
            
        }
        
        // Serialize the ProteinSetComparisonForm
        if(!serializeObject(myForm, ClusteringConstants.FORM_SER,
        		errorMessage, baseDir)) {
        	ActionErrors errors = new ActionErrors();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionMessage("error.general.errorMessage", errorMessage.toString()));
            saveErrors( request, errors );
            return mapping.findForward("Failure");
        }
        
        // redirect to the ReadClusteredSpectrumCountsAction
        ActionForward fwd = mapping.findForward("ReadSaved");
        String anchor = "";
        if(myForm.getRowIndex() != -1) {
        	anchor = "#"+myForm.getRowIndex();
        }
        String noDispCols = ((ProteinSetComparisonForm) form).getDisplayColumns().getNoDisplayColCommaSeparated();
		if(noDispCols.length() > 0)
			noDispCols = "&noDispCol="+noDispCols;
		
		String gradient = "";
		if(((ProteinSetComparisonForm) form).getHeatMapGradient() == GRADIENT.BY)
			gradient="&gradient="+GRADIENT.BY.name();
		else
			gradient = "&gradient="+GRADIENT.GR.name();
			
		
		ActionForward newFwd = new ActionForward(fwd.getPath()+
				"?token="+jobToken+"&page=1"+
				"&count="+myForm.getNumPerPage()+
				noDispCols+
				anchor+
				gradient, 
				fwd.getRedirect());
		return newFwd;
        
    }

	private boolean serializeObject(
			Object object, String outFile,
			StringBuilder errorMessage, String dir) {
		
		String file = dir+File.separator+outFile;
		ObjectOutputStream oo = null;
		try {
			oo = new ObjectOutputStream(new FileOutputStream(file));
			oo.writeObject(object);
		}
		catch (IOException e) {
			errorMessage.append("Error writing file: "+outFile+" "+e.getMessage());
			log.error("Error writing file: "+outFile+" "+e.getMessage(), e);
			return false;
		}
		finally {
			if(oo != null) try {oo.close();} catch(IOException e){}
		}
		return true;
	}
	
}
