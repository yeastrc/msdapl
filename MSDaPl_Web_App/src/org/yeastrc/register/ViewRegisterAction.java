/* ViewRegisterAction.java
 * Created on Mar 4, 2004
 */
package org.yeastrc.register;

import javax.servlet.http.*;
import org.apache.struts.action.*;
import org.yeastrc.utils.*;

/**
 * Controller for showing the registration form.
 * 
 * @author Michael Riffle <mriffle@alumni.washington.edu>
 * @version 1.0, Mar 4, 2004
 *
 */
public class ViewRegisterAction extends Action {

	public ActionForward execute( ActionMapping mapping,
								  ActionForm form,
								  HttpServletRequest request,
								  HttpServletResponse response )
	throws Exception {
		HttpSession session = request.getSession();
		
		StatesBean sb = StatesBean.getInstance();
		session.setAttribute("states", sb.getStates());

		CountriesBean cb = CountriesBean.getInstance();
		session.setAttribute("countries", cb.getCountries());

		// Go!
		return mapping.findForward("Done");

	}


}
