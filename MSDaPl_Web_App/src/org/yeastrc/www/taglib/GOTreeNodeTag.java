/**
 * 
 */
package org.yeastrc.www.taglib;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;
import org.apache.struts.util.RequestUtils;
import org.yeastrc.www.go.GOTree;
import org.yeastrc.www.go.GOTreeNode;


/**
 * GOTreeNodeTag.java
 * @author Vagisha Sharma
 * Jun 25, 2010
 * 
 */
public class GOTreeNodeTag extends TagSupport {

	private String treeName;  // name of the bean that contains the GO Tree
	
	private static final Logger log = Logger.getLogger(GOTreeNodeTag.class.getName());
    
    public String getTreeName() {
		return treeName;
	}

	public void setTreeName(String treeName) {
		this.treeName = treeName;
	}

	public int doStartTag() throws JspException{
        
        if(this.treeName == null)   return SKIP_BODY;
        
        GOTree goTree = (GOTree)RequestUtils.lookup(pageContext, treeName, null);
        if(goTree == null)     return SKIP_BODY;
        
        ServletContext context = pageContext.getServletContext();
        String contextPath = context.getContextPath();
        contextPath = contextPath + "/";
        
        try {
            // Get our writer for the JSP page using this tag
            JspWriter writer = pageContext.getOut();

            
            for(GOTreeNode root: goTree.getRoots()) {
            	writer.print("<ul>");
            	printNode(root, writer);
            	writer.print("</ul>");	
            }
            
            // They are authenticated
            return SKIP_BODY;

        }
        catch (Exception e) {
            log.error("Exception in GOTreeNodeTag", e);
            throw new JspException("Error: Exception while writing to client: " + e.getMessage());
        }
    }

	private void printNode(GOTreeNode node, JspWriter writer) throws IOException {
		
		String nodeId = node.getGoNode().getAccession();
		nodeId = nodeId.replaceAll("GO:", "");
			
		// A node should be displayed as a leaf node only if:
		// 1. It is indeed a leaf node in the GO DAG
		// 2. the number of annoations == # exact annotations.
		// Normally a node will be displayed as a leaf node if node.hasChildren() == false
		// However, since we are not loading the entire tree we may have nodes that have children but they have not been loaded in the tree
		// node.hasChildren() will return false
		// The children of such nodes will be AJAX-loaded. 
		// We set the class for such nodes here so that jstree displays them as closed nodes.
		if(!node.hasChildren() && !node.isLeaf())
			writer.print("<li class='jstree-closed' id='"+nodeId+"'>");
		else
			writer.print("<li id='"+nodeId+"'>");
		
		if(node.isMarked()) {
			writer.print("<span class='slim-node'>");
		}
		writer.write("<span class='searchable'>");
		writer.write(node.getGoNode().getAccession()+": "+node.getGoNode().getName());
		writer.write("</span>");
		
		writer.print("<span ");
		if(node.isLeaf()) // a node with # annotations = # exact annotations (this should be true of "real" GO leaf nodes)
			writer.print("class=\'green\'>");
		else
			writer.print("class=\'red\'>");
		writer.print(node.getAnnotationLabel());
		
		writer.write("&nbsp;<a style='font-size:8pt;font-weight:normal;' target='go_window' href='http://www.godatabase.org/cgi-bin/amigo/go.cgi?action=query&view=query&search_constraint=terms&query="+node.getGoNode().getAccession()+"'>");
		writer.write("[Amigo]");
		writer.write("</a>");
		
		writer.print("</span>");
		writer.print("</span>");
		//writer.write("\n");
		
		if(node.hasChildren()) {
			writer.print("<ul>");
			for(GOTreeNode child: node.getChildren()) {
	    		printNode(child, writer);
	    	}
			writer.print("</ul>");
		}
		writer.write("</li>");
	}

	public int doEndTag() throws JspException {
        return EVAL_PAGE;
    }

    public void release() {
        super.release();
    }
}
