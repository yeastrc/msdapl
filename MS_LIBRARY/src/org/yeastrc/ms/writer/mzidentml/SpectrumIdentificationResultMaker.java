/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.domain.analysis.peptideProphet.PeptideProphetResult;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResult;
import org.yeastrc.ms.domain.search.sequest.SequestSearchResultIn;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationItemType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationResultType;
import org.yeastrc.ms.writer.mzidentml.peptideprophet.PeptideProphetSpectrumIdentificationItemMaker;
import org.yeastrc.ms.writer.mzidentml.percolator.PercolatorSpectrumIdentificationItemMaker;
import org.yeastrc.ms.writer.mzidentml.sequest.SequestSpectrumIdentificationItemMaker;

/**
 * SpectrumIdentificationResultMaker.java
 * @author Vagisha Sharma
 * Aug 3, 2011
 * 
 */
public class SpectrumIdentificationResultMaker {

	private String filename;
	private int scanNumber;
	private int resultIndex = 0;
	private Program searchProgram;
	private SpectrumIdentificationResultType spectrumResult;

	public void initSpectrumResult(String filename, int scanNumber, Program searchProgram) {
		
		this.initSpectrumResult(filename, scanNumber);
		this.searchProgram = searchProgram;
		
	}

	public void initSpectrumResult(String filename, int scanNumber) {
		
		// Example: <SpectrumIdentificationResult id="SpIdLi1_Res1" spectrumID="file=DTA1" spectraData_ref="DTA1">
		
		this.filename = filename;
		this.scanNumber = scanNumber;
		
		spectrumResult = new SpectrumIdentificationResultType();
		
		// id: An identifier is an unambiguous string that is unique within the scope 
		// (i.e. a document, a set of related documents, or a repository) of its use. 
		spectrumResult.setId(filename+"_"+scanNumber);
		
		// spectrumID: The locally unique id for the spectrum in the spectra data set specified by SpectraData_ref. 
		// External guidelines are provided on the use of consistent identifiers for spectra in different external formats. 
		spectrumResult.setSpectrumID(filename+"_"+scanNumber);
		
		
		spectrumResult.setSpectraDataRef(filename);
		
	}
	
	public SpectrumIdentificationResultType getSpectrumResult() {
		return this.spectrumResult;
	}
	
	public void addSequestResult(SequestSearchResultIn result) 
			throws ModifiedSequenceBuilderException {
		
		SequestSpectrumIdentificationItemMaker itemmaker = new SequestSpectrumIdentificationItemMaker(filename);
		SpectrumIdentificationItemType item = itemmaker.make(result, ++resultIndex);
		
		addSpectrumIdentificationItem(item);
	}
	
	public void addSequestResult(SequestSearchResult result) 
				throws ModifiedSequenceBuilderException {

		SequestSpectrumIdentificationItemMaker itemmaker = new SequestSpectrumIdentificationItemMaker(filename);
		SpectrumIdentificationItemType item = itemmaker.make(result, this.scanNumber, ++resultIndex);

		addSpectrumIdentificationItem(item);
	}
	
	public void addPercolatorResult(PercolatorResult result) 
			throws ModifiedSequenceBuilderException, MzidDataProviderException {

		PercolatorSpectrumIdentificationItemMaker itemmaker = new PercolatorSpectrumIdentificationItemMaker(filename, searchProgram);
		SpectrumIdentificationItemType item = itemmaker.make(result, this.scanNumber, ++resultIndex);

		addSpectrumIdentificationItem(item);
	}
	
	public void addPercolatorResult(PeptideProphetResult result) 
			throws ModifiedSequenceBuilderException, MzidDataProviderException {

		PeptideProphetSpectrumIdentificationItemMaker itemmaker = new PeptideProphetSpectrumIdentificationItemMaker(filename, searchProgram);
		SpectrumIdentificationItemType item = itemmaker.make(result, this.scanNumber, ++resultIndex);

		addSpectrumIdentificationItem(item);
	}
	
	private void addSpectrumIdentificationItem(SpectrumIdentificationItemType item){

		spectrumResult.getSpectrumIdentificationItem().add(item);
	}
}
