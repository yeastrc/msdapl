/**
 * 
 */
package org.yeastrc.www.proteinfer;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.slim.GOSlimLookup;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.go.GOSlimTreeCreator;
import org.yeastrc.www.go.GOTree;
import org.yeastrc.www.go.GOTreeNode;

/**
 * GOSlimTreeAjaxAction.java
 * @author Vagisha Sharma
 * Jul 2, 2010
 * 
 */
public class GOSlimTreeAjaxAction extends Action {

	private static final Logger log = Logger.getLogger(GOSlimTreeAjaxAction.class.getName());

	public ActionForward execute( ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response )
	throws Exception {


		long s = System.currentTimeMillis();
		
		int pinferId = (Integer)request.getAttribute("pinferId");
		int goAspect = (Integer)request.getAttribute("goAspect");
		int goSlimTermId = (Integer) request.getAttribute("goSlimTermId");
		List<Integer> nrseqIds = (List<Integer>) request.getAttribute("nrseqProteinIds");


		GOSlimTreeCreator treeCreator = new GOSlimTreeCreator(goSlimTermId, nrseqIds, goAspect);
		//        	BufferedImage img = treeCreator.createGraph();
		//        	request.setAttribute("image", img);
		GOTree tree = treeCreator.createTree();
		StringBuilder openInit = new StringBuilder();

		for(GOTreeNode root: tree.getRoots()) {
			openInit.append(",");
			openInit.append("\"");
			openInit.append(root.getGoNode().getAccession().replaceAll("GO:", ""));
			openInit.append("\"");
		}
		if(openInit.length() > 0)
			openInit.deleteCharAt(0); // remove the first comma
		request.setAttribute("openNodes", openInit);

		List<GONode> slimTerms = treeCreator.getSlimTerms();
		StringBuilder slimTermString = new StringBuilder();
		for(GONode slimTerm: slimTerms) {
			slimTermString.append(",");
			slimTermString.append("\"");
			slimTermString.append(slimTerm.getAccession().replaceAll("GO:", ""));
			slimTermString.append("\"");
		}
		if(slimTermString.length() > 0)
			slimTermString.deleteCharAt(0); // remove first comma
		request.setAttribute("slimTermIds", slimTermString);

		request.setAttribute("goTree", tree);
		request.setAttribute("pinferId", pinferId);
		request.setAttribute("goAspect", goAspect);

		List<GONode> slimNodes = GOSlimLookup.getGOSlims();
		for(GONode node: slimNodes) {
			if(node.getId() == goSlimTermId)
				request.setAttribute("slimName", node.getName());
		}

		long e = System.currentTimeMillis();
		log.info("GOSlimTreeAjaxAction results in: "+TimeUtils.timeElapsedMinutes(s,e)+" minutes");
		return mapping.findForward("Success");
		
	}
}
