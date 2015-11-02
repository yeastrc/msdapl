/*
 * GOLinkTag.java
 * Created on Mar 31, 2006
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;
import org.yeastrc.bio.go.*;

/**
 * Description of class goes here.
 * 
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Mar 31, 2006
 */

public class GOLinkTag extends TagSupport {
	
    /**
     * Name of the bean that contains the data we will be rendering.
     */
    protected String name = null;

    public String getName() {
        return (this.name);
    }

    public void setName(String name) {
        this.name = name;
    }


    /**
     * Name of the property to be accessed on the specified bean.
     */
    protected String property = null;

    public String getProperty() {
        return (this.property);
    }

    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * The scope to be searched to retrieve the specified bean.
     */
    protected String scope = null;

    public String getScope() {
        return (this.scope);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
    
    
    /**
     * Process the start tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        // Look up the requested property value
        GONode node = (GONode)(RequestUtils.lookup(pageContext, name, property, scope));
        if (node== null)
            return (SKIP_BODY);  // Nothing to output
        
        String output = "";
        
        try {
 
        	output =  "<a href=\"http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query=" + node.getAccession() + "\" target=\"go_window\"";

        	String definition = node.getDefinition();
        	if (definition != null && !definition.equals( "" ) ) {
        		output += " onMouseover=\"ddrivetip('" + node.getDefinition() + "')\"";
        		output += " onMouseout=\"hideddrivetip()\"";
        	}
        	
        	output += ">";
        	output += node.getName() + "</a>";
        	
        } catch (Exception e) {
        	output = "ERROR";
        }
        

        // Print this property value to our output writer, suitably filtered
        ResponseUtils.write(pageContext, output);

        // Continue processing this page
        return (SKIP_BODY);

    }
    
    
    
    
    /**
     * Release all allocated resources.
     */
    public void release() {
        super.release();
        name = null;
        property = null;
        scope = null;
    }
    
	
}