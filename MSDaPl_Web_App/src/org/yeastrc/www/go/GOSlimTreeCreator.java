/**
 * 
 */
package org.yeastrc.www.go;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.yeastrc.bio.go.GOAnalysisProtein;
import org.yeastrc.bio.go.GOAnnotation;
import org.yeastrc.bio.go.GONode;
import org.yeastrc.bio.go.slim.GOSlimException;
import org.yeastrc.bio.go.slim.GOSlimLookup;
import org.yeastrc.bio.go.slim.GOSlimTermResult;

/**
 * GOSlimTreeCreator.java
 * @author Vagisha Sharma
 * Jun 18, 2010
 * 
 */
public class GOSlimTreeCreator {

	private final int goSlimTermId;
	private List<GONode> slimTerms;
	private final List<Integer> nrseqProteinIds;
	private final Map<String, GOSlimTermResult> nodesWithAnnotations;
	private final int goAspect;
	
	private static final Logger log = Logger.getLogger(GOSlimTreeCreator.class.getName());
	
	public GOSlimTreeCreator(int goSlimTermId, List<Integer> nrseqProteinIds, int goAspect) {
		
		this.nrseqProteinIds = nrseqProteinIds;
		nodesWithAnnotations = new HashMap<String, GOSlimTermResult>();
		this.goAspect = goAspect;
		this.goSlimTermId = goSlimTermId;
		
	}

	public List<GONode> getSlimTerms() throws GOSlimException {
		
		if(slimTerms != null)
			return slimTerms;
		
		// Get the GO terms for the given GO Slim
		slimTerms = GOSlimLookup.getGOSlimTerms(goSlimTermId, goAspect);
		
		
		return slimTerms;
	}
	
	public List<GOTreeNode> getChildNodes(GONode node) throws GOException {
		
		Set<GONode> children;
		try {
			children = node.getChildren();
		} catch (Exception e) {
			throw new GOException("Error getting children for GO node: "+node.getAccession(), e);
		}
		
		// First get a list of nrseq protein IDs annotated to this node
		List<Integer> annotatedToNode = new ArrayList<Integer>();
		for(int nrseqId: nrseqProteinIds) {
			Annotation annot;
			try {
				annot = ProteinGOAnnotationChecker.isProteinAnnotated(nrseqId, node.getId());
			} catch (SQLException e) {
				throw new GOException("Error checking annotation for protein: "+nrseqId+" and go term ID: "+node.getId(), e);
			}
			if(annot != Annotation.NONE) {
				annotatedToNode.add(nrseqId);
			}
		}
		
		Map<String, GOTreeNode> childNodes = new HashMap<String, GOTreeNode>(children.size());
		// Now look for annotations to the children of this node
		int numAnnotated = 0;
		int numExactAnnotated = 0;
		for(GONode child: children) {
			
			numAnnotated = 0;
			numExactAnnotated = 0;
			
			GOTreeNode treeChild = childNodes.get(child.getAccession());
			if(treeChild == null) {
				treeChild = new GOTreeNode(child);
				childNodes.put(child.getAccession(), treeChild);
			}
			
			for(int nrseqProteinId: annotatedToNode) {
				
				Annotation annot;
				try {
					annot = ProteinGOAnnotationChecker.isProteinAnnotated(nrseqProteinId, child.getId());
				} catch (SQLException e) {
					throw new GOException("Error checking annotation for protein: "+nrseqProteinId+" and go term ID: "+child.getId(), e);
				}
				if(annot == Annotation.EXACT) {
					numExactAnnotated++;
					numAnnotated++;
				}
				else if(annot == Annotation.INDIRECT) {
					numAnnotated++;
				}
			}
			
			treeChild.setNumAnnotated(numAnnotated);
			treeChild.setNumExactAnnotated(numExactAnnotated);
		}
		return new ArrayList<GOTreeNode>(childNodes.values());
	}
	
	public GOTree createTree() throws Exception {
		
		log.info("Creating GOTree... for "+nrseqProteinIds.size()+" proteins");
		
		getSlimTerms();
		
		Map<String, GOTreeNode> seen = new HashMap<String, GOTreeNode>();
		Map<String, GOTreeNode> roots = new HashMap<String, GOTreeNode>();
		
		// get all annotations (exact and otherwise) for all proteins in our set
		getAllAnnotations(nrseqProteinIds, goAspect);
		
		log.info("# GO annotations found: "+nodesWithAnnotations.size());
		
		// build a tree with the annotations we have now; mark the terms that are in the GO Slim set.
		for(GONode term: slimTerms) {
			
			GOTreeNode treeNode = seen.get(term.getAccession());
			if(treeNode == null) {
				treeNode = new GOTreeNode(term);
			}
			
			// this is a GO Slim term mark it.
			treeNode.setMarked(true);
			
			GOTreeNode root = getRoot(treeNode, seen);
			if(!roots.containsKey(root.getGoNode().getAccession())) {
				roots.put(root.getGoNode().getAccession(), root);
			}
		}
		GOTree tree = new GOTree();
		for(GOTreeNode root: roots.values()) {
			if(root.getGoNode().isRoot()) { // this will get rid of the root "all" node.
				for(GOTreeNode child: root.getChildren()) {
					tree.addRoot(child);
				}
			}
			else
				tree.addRoot(root);
		}
		
		//getChildren(tree, seen);
		
		//tree.print();
		return tree;
	}
	
	
	private void getAllAnnotations(List<Integer> nrseqProteinIds, int goAspect) throws GOException {
		
		for(int nrseqProteinId: nrseqProteinIds) {
			
			Set<GOAnnotation> annotNodes = null;
			try {
				annotNodes = ProteinGOAnnotationSearcher.getAnnotationsForProtein(nrseqProteinId, goAspect, false);
			} catch (SQLException e) {
				throw new GOException("Error getting terms for GO annotations for protein: "+nrseqProteinId, e);
			}
			
			for(GOAnnotation annot: annotNodes) {
				GOSlimTermResult node = nodesWithAnnotations.get(annot.getNode().getAccession());
				
				// If we haven't seen this node yet add it to the map
				if(node == null) {
					node = new GOSlimTermResult(annot.getNode());
					nodesWithAnnotations.put(node.getAccession(), node);
				}
				
				GOAnalysisProtein protein = new GOAnalysisProtein(nrseqProteinId);
				if(annot.isExact()) {
					protein.setExactAnnotation(annot.getNode().getId());
				}
				node.addProtein(protein);
			}
		}
	}
	
	private GOTreeNode getRoot(GOTreeNode treeNode, Map<String, GOTreeNode> seenNodes) throws Exception {
		
		if(!seenNodes.containsKey(treeNode.getGoNode().getAccession())) {
			seenNodes.put(treeNode.getGoNode().getAccession(), treeNode);
		}
		
		setAnnotations(treeNode);
		
		Set<GONode> parents = treeNode.getGoNode().getParents();
		if(parents == null || parents.size() == 0)
			return treeNode; // this is the root
		
		GOTreeNode root = null;
		for(GONode parent: parents) {
			GOTreeNode pnode = seenNodes.get(parent.getAccession());
			if(pnode == null) {
				pnode = new GOTreeNode(parent);
			}
			pnode.addChild(treeNode);
			root = getRoot(pnode, seenNodes);
		}
		return root;
	}

	private void setAnnotations(GOTreeNode treeNode) {
		GOSlimTermResult annotNode = nodesWithAnnotations.get(treeNode.getGoNode().getAccession());
		if(annotNode == null) {
			treeNode.setNumAnnotated(0);
			treeNode.setNumExactAnnotated(0);
		}
		else {
			treeNode.setNumAnnotated(annotNode.getAnnotatedProteinCount());
			treeNode.setNumExactAnnotated(annotNode.getExactAnnotatedProteinCount());
		}
	}
}
