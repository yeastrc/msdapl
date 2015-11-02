/*
 * RegisterForm.java
 *
 * Created on October 17, 2003
 *
 * Created by Michael Riffle <mriffle@u.washington.edu>
 *
 */

package org.yeastrc.www.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.grant.Grant;
import org.yeastrc.project.Affiliation;
import org.yeastrc.project.Researcher;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version 2004-01-21
 */
public class EditProjectForm extends ActionForm {

    // The form variables we'll be tracking
    private String title = null;
    private String projectAbstract = null;
    private String progress = null;
    private String comments = null;
    private String publications = null;
    
    private int pi = 0;
    private List<Researcher> researchers = new ArrayList<Researcher>();
    private List<Integer> groups = new ArrayList<Integer>();

    private List<Grant> grants = new ArrayList<Grant>();
    
    private int ID = 0;
    
    private Date submitDate = null;
    
    private Affiliation affiliation;
    

	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (this.getPI() == 0) {
			errors.add("PI", new ActionMessage("error.project.nopi"));
		}
		
		// need at least one researcher
//		if (validResearcherCount() < 1) {
//            errors.add("researchers", new ActionMessage("error.project.noresearchers"));
//		}

		if (this.getTitle() == null || this.getTitle().length() < 1) {
            errors.add("title", new ActionMessage("error.project.notitle"));
        }
        
        if (this.getAbstract() == null || this.getAbstract().length() < 1) {
            errors.add("project", new ActionMessage("error.project.noabstract"));
        }
        
//        String[] groups = this.getGroups();
//        if (groups == null || groups.length < 1) {
//            errors.add("groups", new ActionMessage("error.collaboration.nogroups"));
//        }
        
		// we need at least one grant
        if (validGrantCount() < 1) {
                errors.add("grants", new ActionMessage("error.grant.nogrants"));
        }
        
		return errors;
	}
	
	/** Set the title */
	public void setTitle(String arg) { this.title = arg; }

	/** Set the abstract */
	public void setAbstract(String arg) { this.projectAbstract = arg; }
	
	/** Set the progress */
	public void setProgress(String arg) { this.progress = arg; }

	/** Set the comments */
	public void setComments(String arg) { this.comments = arg; }

	/** Set the publications */
	public void setPublications(String arg) { this.publications = arg; }

	/** Set the PI ID */
	public void setPI(int arg) { this.pi = arg; }
	



	/** Get the title */
	public String getTitle() { return this.title; }

	/** Get the abstract */
	public String getAbstract() { return this.projectAbstract; }
	
	/** Get the progress */
	public String getProgress() { return this.progress; }

	/** Get the comments */
	public String getComments() { return this.comments; }

	/** Get the publications */
	public String getPublications() { return this.publications; }

	/** Get the PI ID */
	public int getPI() { return this.pi; }
	
	
	//----------------------------------------------------------------
	// Grants
	//----------------------------------------------------------------
	public Grant getGrant(int index) {
	    //System.out.println("Getting grant id at index: "+index);
		while(index >= grants.size())
			grants.add(new Grant());
		return grants.get(index);
	}
	
	public List <Grant> getGrantList() {
		List<Grant> validGrants = new ArrayList<Grant>();
		for (Grant grant: grants)
			if (grant != null && grant.getID() > 0)
				validGrants.add(grant);
		return validGrants;
	}
	
	public void setGrantList(List <Grant> grants) {
		this.grants = grants;
	}
	
	private int validGrantCount() {
		int i = 0;
		for (Grant grant: grants) {
			if (grant != null && grant.getID() > 0)	i++;
		}
		return i;
	}
	
	//----------------------------------------------------------------
    // Researchers
    //----------------------------------------------------------------
	public Researcher getResearcher(int index) {
	    //System.out.println("Getting researcher id at index: "+index);
	    while(index >= researchers.size())
	        researchers.add(new Researcher());
	    return researchers.get(index);
	}
	
	public List<Researcher> getResearcherList() {
	    //System.out.println("Getting researcher list");
	    List<Researcher> rList = new ArrayList<Researcher>();
	    for(Researcher r: researchers) {
	        if(r != null && r.getID() > 0)
	            rList.add(r);
	    }
	    return rList;
	}
	
	public void setResearcherList(List<Researcher> researchers) {
	    //System.out.println("Setting researcher");
	    this.researchers = researchers;
	}
	
	private int validResearcherCount() {
        int i = 0;
        for (Researcher researcher: researchers) {
            if (researcher != null && researcher.getID() > 0) i++;
        }
        return i;
    }
	
	//----------------------------------------------------------------
    // Groups
    //----------------------------------------------------------------
    public int getGroupId(int index) {
        while(index > groups.size())
            groups.add(0);
        return groups.get(index);
    }
    
    public List<Integer> getGroups() {
        List<Integer> ids = new ArrayList<Integer>();
        for(Integer id: groups) {
            if(id != null && id > 0)
                ids.add(id);
        }
        return ids;
    }
    
    public void setGroups(List<Integer> groups) {
        this.groups = groups;
    }
	
	
    
	public int getID() {
		return ID;
	}
	
	public void setID(int ID) {
		this.ID = ID;
	}
	
	public Date getSubmitDate() {
		return submitDate;
	}
	
	public void setSubmitDate(Date date) {
		this.submitDate = date;
	}
	
	//----------------------------------------------------------------
    // Affiliation
    //----------------------------------------------------------------
	public void setAffiliation(Affiliation affiliation) {
        this.affiliation = affiliation;
    }
    
    public Affiliation getAffiliation() {
        return this.affiliation;
    }
    
    public String getAffiliationName() {
        if(affiliation == null)
            return "None";
        else
            return this.affiliation.name();
    }
    
    public void setAffiliationName(String name) {
        this.affiliation = Affiliation.forName(name);
    }
}