/*
 * Project.java
 *
 * Created October 15, 2003
 *
 * Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.project;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.grant.Grant;
import org.yeastrc.group.Group;
import org.yeastrc.project.payment.PaymentMethod;
import org.yeastrc.www.user.Groups;


public class Project implements Comparable<Project> {	

    // The id this Project has in the database
    int id;
    
    // The submit date of the project (actually a time stamp of it's creation)
    java.sql.Date submitDate;
    

    // the Principle Investigator
    private Researcher PI;
    
    // researchers
    private List<Researcher> researchers;
    
    // grants
    private List<Grant> grants; 
    
    // groups (that have read access to this project)
    // TODO add option for write access
    private List<Group> groups;
    
    // Title of the project
    private String title;
    
    // Abstract for the project
    private String projectAbstract;
    
    // A description of the progress of the project
    private String progress;
    
    // A list of publications (free text) associated with the project
    private String publications;
    
    // Any comments entered for this project
    private String comments;
    
    // When the project last changed (a timestamp from the database)
    java.sql.Date lastChange;
    
    // When the progress was last changed
    java.sql.Date progressLastChange;
    
    private Affiliation affiliation;
    
    private List<PaymentMethod> paymentMethods;
    
	/**
	 * Instantiate a new project.
	 */
	public Project() {		
		this.id = 0;
		
		this.PI = null;
		this.researchers = new ArrayList<Researcher>();
		
		this.grants = new ArrayList<Grant>();
		this.groups = new ArrayList<Group>();

		this.submitDate = null;
		this.lastChange = null;
		
		this.title = null;
		this.projectAbstract = null;
		this.progress = null;
		this.publications = null;
		this.comments = null;
	}

	/**
	 * Determine whether or not a Researcher has READ access to this project, that is
	 * are they affiliated with the project.
	 * researchers have read access if:
	 * 1. researcher is an administrator
	 * 2. researcher is the project PI
	 * 3. researcher is listed as a researcher on the project
	 * 4. researcher is a member of one of the groups associated with the project
	 * @param researcher The Researcher to check
	 * @return true if they have access, false if not
	 */
	public boolean checkReadAccess(Researcher researcher) {		
		if (researcher == null) { return false; }
		
		Groups groupsMan = Groups.getInstance();
		
		// Admins have access to all projects.
		if (groupsMan.isMember(researcher.getID(), "administrators"))
			return true;
		
		// User has access if he/she is the project PI
		if(this.PI != null && this.PI.equals(researcher))
		    return true;
		
		// If the user is listed as a researcher on the project they have read access
		for(Researcher r: this.researchers) {
		    if(r != null && r.equals(researcher))
		        return true;
		}
		
		// If the user is a member of one of the groups associated with the project
		// they have read access
		String[] tGroups = this.getGroupsArray();
		for (int i = 0; i < tGroups.length; i++) {
			if (groupsMan.isMember(researcher.getID(), tGroups[i]))
				return true;
		}
		
		// Access -DENIED-
		return false;
	}
	
	
	/**
	 * Determine whether or not a Researcher has write access to this project.
	 * researchers have read access if:
	 * 1. researcher is an administrator
	 * 2. researcher is the project PI
	 * 3. researcher is listed as a researcher on the project
	 * @param researcher The Researcher to check
	 * @return true if they have access, false if not
	 */
	public boolean checkAccess(Researcher researcher) {		
	    
		if (researcher == null) { return false; }
		
		Groups groupsMan = Groups.getInstance();
		
		// Admins have access to all projects.
		if (groupsMan.isMember(researcher.getID(), "administrators"))
			return true;
		
		// User has write access if he/she is the project PI
		if(this.PI != null && this.PI.equals(researcher))
		    return true;
		
		// If the user is listed as a researcher on the project they have write access
		for(Researcher r: this.researchers) {
		    if(r != null && r.equals(researcher))
		        return true;
		}
		
		// Access -DENIED-
		return false;
	}


	public List<Group> getGroups() {
	    return groups;
	}
	
	
	public void setGroups(List<Group> groups) {
	    this.groups = groups;
	}

	/**
	 * Will return a String, listing the groups to which this project belongs.
	 * @return A String list of all the groups to which this project belongs.
	 */
	public String getGroupsString() { 
//	    return Projects.getGroupsString(this.getGroups());
	    StringBuilder buf = new StringBuilder();
	    for(Group grp: groups) {
	        if(grp != null)
	            buf.append(","+grp.getName()+" ");
	    }
	    if(buf.length() > 0)
	        buf.deleteCharAt(0);
	    return buf.toString();
	}	

	
	public List<Grant> getGrants() {
	    return grants;
	}
	
	public void setGrants(List<Grant> grants) {
	    this.grants = grants;
	}
	
	
	/**
	 * For Comparable.  Does a comparison of project IDs.
	 * @param o The project to compare this one to.
	 */
	public int compareTo(Project p) {

	    if(p == null)
	        return -1;
	    if(this == p)
	        return 0;
	    
		if (this.id > p.getID()) { return 1; }
		if (this.id < p.getID()) { return -1; }
		return 0;
	}


	// SET METHODS
	
	/**
	 * set the PI
	 * @param PI The PI for the project.
	 */
	public void setPI(Researcher PI) { this.PI = PI; }

	public void setResearchers(List<Researcher> researchers) {
	    this.researchers = researchers;
	}
	
	
	/**
	 * Set the title for the project.
	 * @param title The project title.
	 */
	public void setTitle(String title) { this.title = title; }
	
	
	/**
	 * Set the text for the abstract for this project
	 * @param arg The text for the abstract of this project
	 */
	public void setAbstract(String arg) { this.projectAbstract = arg; }
	
	
	/**
	 * Set the text for a description of the progress for this project.  Note, this will
	 * override the currently text for the progress.  To add, use addProgress()
	 * @param arg the text for the progress
	 */
	public void setProgress(String arg) {
		if (this.progress != null && this.progress.equals(arg)) { return; }

		// The progress is being changed.
		this.progress = arg;
		
		java.util.Date tDate = new java.util.Date();
		this.progressLastChange = new java.sql.Date(tDate.getTime());
	}
	
	/**
	 * Set the value for publications for this project.  This is a list of
	 * references or publications attached to this project.  It is a single
	 * descriptive String.
	 * @param arg The text for the publication list.
	 */
	public void setPublications(String arg) { this.publications = arg; }
	
	/**
	 * Set the value for any comments associated to this project.
	 * This will override the current value for comments.
	 * @param arg The comments for this project.
	 */
	public void setComments(String arg) { this.comments = arg; }
	
	// GET METHODS
	/**
	 * Get the Project ID number
	 * @return the project ID number
	 */
	public int getID() { return this.id; }

	/**
	 * Get the PI for this project
	 * @return the project PI
	 */
	public Researcher getPI() { return this.PI; }

	public List<Researcher> getResearchers() {return this.researchers;}
	
	/**
	 * Returns the project submit date as a String
	 * @return the project submit date in string form
	 */
	public java.util.Date getSubmitDate() { return this.submitDate; }
	
	/**
	 * Returns the project title.
	 * @return the project title.
	 */
	public String getTitle() {
		return this.title;
	}
	
	

	/**
	 * Returns the abstract for the project
	 * @return The abstract (text)
	 */
	public String getAbstract() { return this.projectAbstract; }
	
	/**
	 * Returns the abstract in HTML form
	 * @return The abstract in HTML form
	 */
	public String getAbstractAsHTML() {
		return org.yeastrc.utils.HTML.convertToHTML(this.projectAbstract);
	}
	
	/**
	 * Returns the progress for the project
	 * @return The progress (Text)
	 */
	public String getProgress() { return this.progress; }
	
	/**
	 * Get the progress as HTML
	 * @return the progress as HTML
	 */
	public String getProgressAsHTML() {
		return org.yeastrc.utils.HTML.convertToHTML(this.progress);
	}
	
	/**
	 * Returns the publications associated with this project.
	 * @return The publications
	 */
	public String getPublications() { return this.publications; }
	
	/**
	 * Get the publications as HTML
	 * @return the publications as HTML
	 */
	public String getPublicationsAsHTML() {
		return org.yeastrc.utils.HTML.convertToHTML(this.getPublications());
	}
	
	/**
	 * Returns the comments associated with this project.
	 * @return The comments
	 */
	public String getComments() { return this.comments; }
	
	/**
	 * Returns the comments as HTML
	 * @return The comments as HTML
	 */
	public String getCommentsAsHTML() {
		return org.yeastrc.utils.HTML.convertToHTML(this.getComments());
	}
	
	/**
	 * Returns the last changed date (when the project was last modified in the database)
	 * @return The date of the last change to this project.
	 */
	public java.util.Date getLastChange() { return this.lastChange; }

	
	/**
     * Returns the date the progress was last modified and saved
     * @return The date of the last progress change to this project.
     */
    public Date getProgressLastChange() { return this.progressLastChange; }
    
    
    /**
     * Get the actual array of Strings representing the YRC groups (short form)
     * @return an array of strings
     */
    public String[] getGroupsArray() {
        if (this.getGroups() == null) { return null; }
        
        String[] grpNames = new String[groups.size()];
        int i = 0;
        for(Group group: groups) {
            grpNames[i++] = group.getName();
        }
        return grpNames;
    }
    
	/**
	 * Get a label to use to identify this project.
	 * @return A label used to identify this project, to a human being
	 */
	public String getLabel() {
		String label;
		String pTitle = this.getTitle();

		label = String.valueOf(this.id);

		if (this.PI != null) {
			label += " - " + this.PI.getLastName();
		}
		
		label += " - " + this.getTitle();
		
		// Truncate if necessary
		if (label.length() > 70) {
			label = label.substring(0,66) + "...";
		}

		return label;
	}

	public Affiliation getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(Affiliation affiliation) {
		this.affiliation = affiliation;
	}

	public List<PaymentMethod> getPaymentMethods() {
		return paymentMethods;
	}

	public void setPaymentMethods(List<PaymentMethod> paymentMethods) {
		this.paymentMethods = paymentMethods;
	}

	/**
	 * A string representation of a Project
	 */
	public String toString() {
		return "Project #" + this.id + " - " + this.title;
	}

}