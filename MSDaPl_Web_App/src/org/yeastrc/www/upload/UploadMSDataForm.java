/*
 * UploadYatesForm.java
 * Created on Oct 12, 2004
 * Created by Michael Riffle <mriffle@u.washington.edu>
 */

package org.yeastrc.www.upload;

import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.yeastrc.bio.taxonomy.TaxonomySearcher;


public class UploadMSDataForm extends ActionForm {

	private static final Logger log = Logger.getLogger(UploadMSDataForm.class);
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	private Date experimentDate;
    private String month;
    private String day;
    private String year;
    private String directory;
    private int species;
    private int otherSpecies;
    private String comments;
    
    
    private int projectID;
    
    private int instrumentId;
    
    private Pipeline pipeline;
    
    private String dataServer = "local";
    
	
    /**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();
		DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
		ParsePosition pp = new ParsePosition(0);
		
		if (this.projectID == 0)
			errors.add("upload", new ActionMessage("error.upload.noproject"));
		
		if (this.directory == null || directory.trim().length() == 0) {
			errors.add("upload", new ActionMessage("error.upload.nodirectoryname"));
		}
		
		/*
		if (this.targetSpecies1 == 0) {
			errors.add("upload", new ActionMessage("error.upload.notargetspecies"));
		}
		*/
		
		if (this.day == null || this.month == null || this.year == null) {
			errors.add("upload", new ActionMessage("error.upload.noexperimentdate"));
		}
		
		if (day.equals("0") || month.equals("0") || year.equals("0")) {
			errors.add("upload", new ActionMessage("error.upload.noexperimentdate"));
		}
		
		this.experimentDate = df.parse(this.month + "/" + this.day + "/" + this.year, pp);
		if (pp.getIndex() == 0) {
			errors.add("upload", new ActionMessage("error.upload.invaliddate"));
		}
		
		int speciesID = this.getSpecies();
        // user may not have selected a species, but if a species is selected make sure we recognize it
        if(speciesID > 0) {
            TaxonomySearcher ts = TaxonomySearcher.getInstance();
            try {
                if (ts.getName(speciesID) == null)
                    errors.add("upload", new ActionMessage("error.upload.invalidspecies"));
            } catch (Exception e) {
            	log.error("Exception getting species name for id: " + speciesID, e );
            	
                errors.add("upload", new ActionMessage("error.upload.invalidspecies"));
            }
        }
		
//		if(this.instrumentId == 0) {
//		    errors.add("upload", new ActionMessage("error.upload.noinstrument"));
//		}
		
		return errors;
	}


    public Date getExperimentDate() {
        return experimentDate;
    }


    public void setExperimentDate(Date experimentDate) {
        this.experimentDate = experimentDate;
    }


    public String getDirectory() {
        return directory;
    }


    public void setDirectory(String directory) {
        this.directory = directory;
    }


    public int getSpecies() {
        if(species > 0)
            return species;
        else
            return otherSpecies;
    }


    public void setSpecies(int species) {
        this.species = species;
    }


    public int getOtherSpecies() {
        return otherSpecies;
    }


    public void setOtherSpecies(int otherSpecies) {
        this.otherSpecies = otherSpecies;
    }


    public String getComments() {
        return comments;
    }


    public String getMonth() {
        return month;
    }


    public String getDay() {
        return day;
    }


    public String getYear() {
        return year;
    }


    public int getInstrumentId() {
        return instrumentId;
    }


    public void setInstrumentId(int instrumentId) {
        this.instrumentId = instrumentId;
    }


    public void setComments(String comments) {
        this.comments = comments;
    }


    public int getProjectID() {
        return projectID;
    }


    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }


    public void setMonth(String month) {
        this.month = month;
    }


    public void setDay(String day) {
        this.day = day;
    }


    public void setYear(String year) {
        this.year = year;
    }

    public Pipeline getPipeline() {
        return pipeline;
    }

    public String getPipelineLongName() {
        return pipeline.getLongName();
    }
    
    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }
    
    public String getPipelineName() {
        return pipeline.name();
    }
    
    public void setPipelineName(String pipeline) {
        this.pipeline = Pipeline.forName(pipeline);
    }
    
    public String getDataServer() {
        return dataServer;
    }

    public void setDataServer(String dataServer) {
        this.dataServer = dataServer;
    }
}