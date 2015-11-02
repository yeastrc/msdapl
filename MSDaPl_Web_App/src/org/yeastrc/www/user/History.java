/*
 * History.java
 *
 * Created February 10, 2004
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.user;

import java.util.*;

/**
 * This will serve to hold the history of links a User to the site has visited.
 * 
 * @version 2004-02-10
 * @author Michael Riffle <mriffle@u.washington.edu>
 */
public class History {	

	// The size we'll allow the history to grow to, before starting to chop off the last node on adding new links
	private int MAX_SIZE = 100;

	// The LinkedList containing our HistoryNodes
	private LinkedList historyList;

	/** Our constructor */
	public History() {
		this.historyList = new LinkedList();
	}

	/**
	 * Add a node with the supplied URL and Title to the history.
	 * @param url The URL to add.
	 * @param title The title of the URL to add.
	 */
	public void addLink(String url, String title) {
		if (url == null || title == null)
			return;
		
		// Set up the new node to add to the history list
		HistoryNode newNode = new HistoryNode(url, title);
		
		// Do not add this node if the first node in the list is equal to this one
		if (this.historyList.size() > 0) {
			HistoryNode oldNode = (HistoryNode)(this.historyList.getFirst());
			if (oldNode.getTitle().equals(newNode.getTitle()) &&
				oldNode.getURL().equals(newNode.getURL())) {
					return;
			}
		}		

		// Add this node to the beginning of the list.
		this.historyList.addFirst(newNode);
		
		// Truncate the list if it is over MAX_SIZE
		if (this.historyList.size() > this.MAX_SIZE) {
			this.historyList.removeLast();
		}
	}

	/**
	 * Set the last link in the history list to be the node
	 * This will happen when a person clicks on a previous link listed on their history
	 * Note, the most recent link will ALWAYS be removed, in case the 2 most recent links are identical.
	 * @param url The URL of the link
	 * @param title The title of the link
	 */
	public void setLast(String title, String url) {
		if (this.historyList.size() < 2)
			return;

		// Remove the first link.
		this.historyList.removeFirst();

		// Test remaining links.
		HistoryNode testNode = (HistoryNode)(this.historyList.removeFirst());
		while ( (!testNode.getTitle().equals(title)) && (!testNode.getURL().equals(url)) && this.historyList.size() > 0) {
			testNode = (HistoryNode)(this.historyList.removeFirst());
		}
				
		this.historyList.addFirst(testNode);
		return;
	}


	/**
	 * Return a List of HistoryNodes of the given size, which should correspond to the last
	 * X links they've visited on the site, where X is the size supplied.
	 * @param size The number of links to return.  If size exceeds the number of nodes, size is set to the number of nodes.
	 * @return A list of HistoryNodes, with the most recently visited links first.
	 */
	public List getList(int size) {
		LinkedList retList = new LinkedList();
		
		if (size > this.historyList.size()) { size = this.historyList.size(); }
		if (size <= 0)
			return retList;
		
		for (int i = 0; i < size; i++) {
			retList.addLast(this.historyList.get(i));
		}
		
		return retList;
	}


	public class HistoryNode {
		private String url;
		private String title;

		private HistoryNode(String url, String title) {
			this.url = url;
			this.title = title;
		}
		
		public String getURL() { return this.url; }
		public String getTitle() { return this.title; }
	}

}