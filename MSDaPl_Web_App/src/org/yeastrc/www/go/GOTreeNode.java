/**
 * 
 */
package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.List;

import org.yeastrc.bio.go.GONode;

/**
 * GOTreeNode.java
 * @author Vagisha Sharma
 * Jun 25, 2010
 * 
 */
public class GOTreeNode {

	private final GONode goNode;
	private int numAnnotated;
	private int numExactAnnotated;
	private boolean isMarked = false;
	
	private List<GOTreeNode> children;
	
	public GOTreeNode(GONode node) {
		this.goNode = node;
		children = new ArrayList<GOTreeNode>();
	}
	public List<GOTreeNode> getChildren() {
		return children;
	}
	public void setChildren(List<GOTreeNode> children) {
		this.children = children;
	}
	public GONode getGoNode() {
		return goNode;
	}
	public int getNumAnnotated() {
		return numAnnotated;
	}
	public void setNumAnnotated(int numAnnotated) {
		this.numAnnotated = numAnnotated;
	}
	public int getNumExactAnnotated() {
		return numExactAnnotated;
	}
	public void setNumExactAnnotated(int numExactAnnotated) {
		this.numExactAnnotated = numExactAnnotated;
	}
	public boolean isLeaf() {
		return numAnnotated == numExactAnnotated;
	}
	public boolean isMarked() {
		return this.isMarked;
	}
	public void setMarked(boolean marked) {
		this.isMarked = true;
	}
	public boolean hasChild(String accession) {
		for(GOTreeNode node: children)
			if(node.getGoNode().getAccession().equals(accession))
				return true;
		return false;
	}
	public void addChild(GOTreeNode child) {
		if(!hasChild(child.getGoNode().getAccession()))
			children.add(child);
		
	}
	public boolean hasChildren() {
		return children.size() > 0;
	}

	public String toString() {
		return this.goNode.getAccession() +" : "+this.goNode.getName() +
		" [#Annot. "+this.getNumAnnotated()+" "+
		"#Exact "+this.getNumExactAnnotated()+"]";
	}
	
	public String getAnnotationLabel() {
		if(this.getNumAnnotated() == 0) {
			return "";
		}
		else {
			return " [#Annot. "+this.getNumAnnotated()+" "+
			"#Exact "+this.getNumExactAnnotated()+"]";
		}
	}
	
	public void print(int indent) {
		System.out.println(getIndent(indent)+toString());
		for(GOTreeNode child: children) {
			child.print(indent+1);
		}
	}
	
	private String getIndent(int indent) {
		StringBuilder buf = new StringBuilder();
		for(int i = 0; i < indent; i++)
			buf.append("\t");
		return buf.toString();
	}
}
