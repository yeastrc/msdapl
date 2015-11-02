/**
 * 
 */
package org.yeastrc.www.go;

import java.util.ArrayList;
import java.util.List;

/**
 * GOTree.java
 * @author Vagisha Sharma
 * Jun 25, 2010
 * 
 */
public class GOTree {

	private List<GOTreeNode> roots;

	public GOTree() {
		roots = new ArrayList<GOTreeNode>();
	}
	public List<GOTreeNode> getRoots() {
		return roots;
	}

	public void addRoot(GOTreeNode root) {
		this.roots.add(root);
	}
	
	public void print() {
		for(GOTreeNode root: roots)
			root.print(0);
	}
}
