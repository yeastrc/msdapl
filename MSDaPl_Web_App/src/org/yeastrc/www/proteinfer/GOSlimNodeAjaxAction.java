/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.GOSlimTreeCreator;
import org.yeastrc.www.go.GOTree;
import org.yeastrc.www.go.GOTreeNode;
import org.yeastrc.www.proteinfer.ProteinInferSessionManager;

/**
 * GOSlimNodeAjaxAction.java
 * @author Vagisha Sharma
 * Jul 1, 2010
 * 
 */
public class GOSlimNodeAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(GOSlimNodeAjaxAction.class.getName());

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {

		log.info("Got request for GO Slim Node details for protein inference");
		
		// get the protein inference id
		int pinferId = 0;
		try {
			String param = request.getParameter("pinferId");
			if(param != null)
				pinferId = Integer.parseInt(param);
		}
		catch(NumberFormatException e) {}
		if(pinferId == 0) {
			log.info("NO valid protein inference Id found in request");
            response.setContentType("text/html");
            response.getWriter().write("<ul><li style='color:red;font-weight-bold;'><a href=\"#\">ERROR: Invalid protein inference ID</a></li></ul>");
            return null;
		}

		long s = System.currentTimeMillis();

        // protein Ids
        List<Integer> proteinIds = null;
        
        ProteinInferSessionManager sessionManager = ProteinInferSessionManager.getInstance();
        // Check if we already have information in the session
        proteinIds = sessionManager.getStoredProteinIds(request, pinferId);
        
        
        // If we don't have a filtering criteria in the session return an error
        if(proteinIds == null) {
        	
        	log.info("NO information in session for: "+pinferId);
            response.setContentType("text/html");
            response.getWriter().write("<ul><li style='color:red;font-weight-bold;'><a href=\"#\">ERROR: Stale protein inference ID</a></li></ul>");
            return null;
        }
        else {
        	
        	log.info("Found information in session for: "+pinferId);
        	log.info("# stored protein ids: "+proteinIds.size());
        	 
        }
		
        int goAspect = -1;
        try {
			String param = request.getParameter("goAspect");
			if(param != null)
				goAspect = Integer.parseInt(param);
		}
		catch(NumberFormatException e) {}
		if(goAspect == -1) {
			log.info("NO valid GO Aspect found in request");
            response.setContentType("text/html");
            response.getWriter().write("<ul><li style='color:red;font-weight-bold;'><a href=\"#\">ERROR: Invalid GO Aspect</a></li></ul>");
            return null;
		}
		
		String goAccession = request.getParameter("goAccession");
		if(goAccession == null || goAccession.trim().length() == 0) {
			log.info("NO valid GO Accesion found in request");
            response.setContentType("text/html");
            response.getWriter().write("<ul><li style='color:red;font-weight-bold;'><a href=\"#\">ERROR: Invalid GO Accession</a></li></ul>");
            return null;
		}
		
        
		
        // We have the protein inference protein IDs; Get the corresponding nrseq protein IDs
        List<Integer> nrseqIds = new ArrayList<Integer>(proteinIds.size());
        ProteinferDAOFactory factory = ProteinferDAOFactory.instance();
        ProteinferProteinDAO protDao = factory.getProteinferProteinDao();
        for(int proteinId: proteinIds) {
            ProteinferProtein protein = protDao.loadProtein(proteinId);
            nrseqIds.add(protein.getNrseqProteinId());
        }
        
        
        // Set the first param to constructor (goSlimTermId) to -1
        // We don't need to know the goSlimTermId for the getChildNodes() function
        GOSlimTreeCreator treeCreator = new GOSlimTreeCreator(-1, nrseqIds, goAspect);
        GONode node = GOCache.getInstance().getGONode(goAccession);
        if(node == null) {
        	log.info("Error looking up GONode for accession "+goAccession);
            response.setContentType("text/html");
            response.getWriter().write("<ul><li style='color:red;font-weight-bold;'><a href=\"#\">ERROR: Cannot find GO node</a></li></ul>");
            return null;
        }
        
        List<GOTreeNode> children = treeCreator.getChildNodes(node);
        GOTree tree = new GOTree();
        for(GOTreeNode child: children)
        	tree.addRoot(child);
        
        request.setAttribute("goTree", tree);

        
		long e = System.currentTimeMillis();
		log.info("GOSlimNodeAjaxAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
		return mapping.findForward("Success");
	}
}
