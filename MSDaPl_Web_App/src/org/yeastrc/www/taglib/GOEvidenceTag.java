/**
 * GOEvidenceTag.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: Apr 20, 2007 at 5:19:47 PM
 */

package org.yeastrc.www.taglib;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.util.RequestUtils;
import org.apache.struts.util.ResponseUtils;

import org.yeastrc.bio.go.EvidenceCode;
import org.yeastrc.bio.go.GOAnnotation;
import org.yeastrc.bio.go.GOEvidence;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version Apr 20, 2007
 *
 * Class definition goes here
 */
public class GOEvidenceTag extends TagSupport {
	
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
        GOAnnotation annotation = (GOAnnotation)(RequestUtils.lookup(pageContext, name, property, scope));
        if (annotation == null)
            return (SKIP_BODY);  // Nothing to output
        
        String output = "";
        
        String contextPath = pageContext.getServletContext().getContextPath();
        
        try {
 
        	output = "";
        	
        	for ( EvidenceCode ec : annotation.getEvidence().keySet() ) {

            	output += "[<span style=\"cursor:help\" title=\""+ec.getEvidenceCodeName()+"\">";
            	output += ec.getEvidenceCode();
            	output += "</span>&nbsp;";
        		
            	if ( annotation.getEvidence().get( ec ) != null && annotation.getEvidence().get( ec ).size() > 0 ) {
            		
            		for ( GOEvidence goe : annotation.getEvidence().get( ec ) ) {
                		output += "<a title=\"Click to view the article on which this annotation was based. (PMID:"+ goe.getPMID() + ")\")";
                		output += " target=\"pub_window\" href=\"http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Search&db=PubMed&term=" + goe.getPMID() + "\">";
                		output += "<img style=\"margin-top:0px;margin-bottom:0px;\" border=\"0\" src=\""+contextPath+"/images/pubmed.gif\" width=\"14\" height=\"15\" alt=\"Link to PubMed article\"></a>";            			
            		}
            		
            	}
            	
            	output += "]";
        	}

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
