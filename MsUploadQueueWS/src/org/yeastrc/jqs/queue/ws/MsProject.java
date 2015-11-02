package org.yeastrc.jqs.queue.ws;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.yeastrc.project.Project;
import org.yeastrc.project.Researcher;


@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)

public class MsProject {
	
	@XmlElement(required = true)
	/**
	 * Database ID of the project in MSDaPl
	 */
	private Integer id;
	
	@XmlElement(required = true)
	/**
	 * Title of the project in MSDaPl
	 */
	private String title;
	
	@XmlElement(required = true)
	/**
	 * Project PI
	 */
	private String pi;
	
	@XmlElement(required = true)
	/**
	 * Submit Date
	 */
	private Date submitDate;
	
	
	public MsProject() {}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public Date getSubmitDate() {
		return submitDate;
	}
	public void setSubmitDate(Date date) {
		this.submitDate = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPi() {
		return pi;
	}

	public void setPi(String pi) {
		this.pi = pi;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("ID: "+this.getId()+"\n");
		buf.append("Title: "+this.getTitle()+"\n");
		buf.append("PI: "+this.getPi()+"\n");
		buf.append("Submit Date: "+this.getSubmitDate()+"\n");
		return buf.toString();
	}
	
	public static MsProject create(Project msdaplProject) {
		
		MsProject project = new MsProject();
		project.setId(msdaplProject.getID());
		project.setTitle(msdaplProject.getTitle());
		project.setSubmitDate(msdaplProject.getSubmitDate());
		Researcher pi = msdaplProject.getPI();
		project.setPi(pi.getFirstName()+" "+pi.getLastName());
		return project;
	}
}