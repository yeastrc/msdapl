/*
 * ViewGOTreeAction.java
 * Created on Mar 28, 2005
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.go;

import java.awt.image.BufferedImage;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.yeastrc.bio.go.GOCache;
import org.yeastrc.bio.go.GOGraphGenerator;
import org.yeastrc.bio.go.GOUtils;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Mar 28, 2005
 */

public class ViewGOTreeAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		
		String goAcc = request.getParameter("id");
		
		String[] gos = goAcc.split(",");
		
		
		HashSet goSet = new HashSet();
		HashSet seedSet = new HashSet();
		
		for (int i = 0; i < gos.length; i++) {
			goSet.add( GOCache.getInstance().getGONode(gos[i]));
			seedSet.add( GOCache.getInstance().getGONode(gos[i]));
			
			goSet.addAll( GOUtils.getAllParents( GOCache.getInstance().getGONode(gos[i]) ) );
		}
		
		GOGraphGenerator gen = new GOGraphGenerator();
		gen.setSeedNodes(seedSet);
		
		BufferedImage bi = gen.getGOGraphImage(goSet);
	    
	    gen = null;
	    System.gc();
	    
	    
	    request.setAttribute("image", bi);
		
		return mapping.findForward("Success");
	}

}