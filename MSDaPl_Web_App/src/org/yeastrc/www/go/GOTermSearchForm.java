/**
 * GOTermSearchForm.java
 * @author Vagisha Sharma
 * Jul 5, 2010
 */
package org.yeastrc.www.go;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * 
 */
public class GOTermSearchForm extends ActionForm {

	private String terms;
	private boolean searchTermSynonyms = true;
	
	private boolean matchAllTerms = false;

	private boolean biologicalProcess = false;
	private boolean molecularFunction = false;
	private boolean cellularComponent = false;

	
	/**
	 * Validate the properties that have been sent from the HTTP request,
	 * and return an ActionErrors object that encapsulates any
	 * validation errors that have been found.  If no errors are found, return
	 * an empty ActionErrors object.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		
		ActionErrors errors = new ActionErrors();

		return errors;
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		searchTermSynonyms = false;
	}
	
	public String getTerms() {
		return terms;
	}
	public void setTerms(String terms) {
		this.terms = terms;
	}
	public boolean isSearchTermSynonyms() {
		return searchTermSynonyms;
	}
	public void setSearchTermSynonyms(boolean searchTermSynonyms) {
		this.searchTermSynonyms = searchTermSynonyms;
	}
	public boolean isMatchAllTerms() {
		return matchAllTerms;
	}
	public void setMatchAllTerms(boolean matchAllTerms) {
		this.matchAllTerms = matchAllTerms;
	}
	public boolean isBiologicalProcess() {
		return biologicalProcess;
	}
	public void setBiologicalProcess(boolean biologicalProcess) {
		this.biologicalProcess = biologicalProcess;
	}
	public boolean isMolecularFunction() {
		return molecularFunction;
	}
	public void setMolecularFunction(boolean molecularFunction) {
		this.molecularFunction = molecularFunction;
	}
	public boolean isCellularComponent() {
		return cellularComponent;
	}
	public void setCellularComponent(boolean cellularComponent) {
		this.cellularComponent = cellularComponent;
	}
	
}
