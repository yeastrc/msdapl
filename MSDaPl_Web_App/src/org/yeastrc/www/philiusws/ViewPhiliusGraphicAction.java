package org.yeastrc.www.philiusws;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ViewPhiliusGraphicAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
	
		// if we have no philius result, this shouldn't be able to be requested.  Just give back something empty for now
		if ( request.getSession().getAttribute( "philiusResult" ) == null ) {
			return null;
		}
		
		PhiliusResultPlus result = (PhiliusResultPlus)request.getSession().getAttribute( "philiusResult" );
		int length = result.getSequence().length();
		
		PhiliusPanel panel = PhiliusPanel.getInstance(result);
		panel.setSize( panel.getPreferredSize() );				// force it to be painted

		BufferedImage bi = null;
		
		if ( length <= 760 ) {
//			height = 100;
			int height = panel.getPreferredSize().height;
			bi = ScreenImage.createImage( panel, new Rectangle( 0, 0, length + 40, height ) ,null );
		}
		else
			bi = ScreenImage.createImage( panel, null );
		
		request.setAttribute( "image",bi );
		
		return mapping.findForward( "Success" );
	}
}

