/**
 * LinkTag.java
 * @author Vagisha Sharma
 * Mar 10, 2009
 * @version 1.0
 */
package org.yeastrc.www.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.ResponseUtils;

/**
 * 
 */
public class LinkTag extends TagSupport {

    private String path = "";
    
    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    /**
     * Process the start tag.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {

        ServletContext context = pageContext.getServletContext();
        String contextPath = context.getContextPath();
        String output = contextPath + "/" + path;
        
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
        path = null;
    }
    
}
