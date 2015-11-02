/**
 * MS2Run.java
 * Created by: Michael Riffle <mriffle@u.washington.edu>
 * Created on: May 10, 2007 at 1:45:07 PM
 */

package org.yeastrc.ms2.spectra;

/**
 * @author Michael Riffle <mriffle@u.washington.edu>
 * @version May 10, 2007
 *
 * Represents the whole MS2 file, holds meta deta pertaining to all data
 * in a particular MS2 file
 */
public abstract class MS2 {

	private int id;						// unique id for this run (database id)
	private String filename;			// filename for this run (should also be unique per run)
	private String creationDate;		// date the MS2 file was created
	private String extractor;			// the name of the software used to create the MS2 file
	private String extractorVersion;	// the version of the extractor software
	private String extractorOptions;	// the options used in running the extractor software
	private String instrumentType;		// type of mass analyzer used
	private String instrumentSN;		// serial number of mass analyzer
	private String comment;				// remarks, ownership and copyright information
	private String iAnalyzer;			// software used to conduct charge-state-independent analysis of spectra
	private String iAnalyzerVersion;	// version number of the iAnalyzer
	private String iAnalyzerOptions;	// options used for the iAnalyzer
	private String dAnalyzer;			// software used to conduct charge-state-dependent analysis of spectra
	private String dAnalyzerVersion;	// version number of the dAnalyzer
	private String dAnalyzerOptions;	// options used for the dAnalyzer
	
	/**
	 * Get a ScanReader that reads scans for this MS2 object
	 * @return An appropriate ScanReader for this MS2 object
	 * @throws Exception if there is a problem
	 */
	public abstract MS2ScanReader getScanReader() throws Exception;
	
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the dAnalyzer
	 */
	public String getDAnalyzer() {
		return dAnalyzer;
	}
	/**
	 * @param analyzer the dAnalyzer to set
	 */
	public void setDAnalyzer(String analyzer) {
		dAnalyzer = analyzer;
	}
	/**
	 * @return the dAnalyzerOptions
	 */
	public String getDAnalyzerOptions() {
		return dAnalyzerOptions;
	}
	/**
	 * @param analyzerOptions the dAnalyzerOptions to set
	 */
	public void setDAnalyzerOptions(String analyzerOptions) {
		dAnalyzerOptions = analyzerOptions;
	}
	/**
	 * @return the dAnalyzerVersion
	 */
	public String getDAnalyzerVersion() {
		return dAnalyzerVersion;
	}
	/**
	 * @param analyzerVersion the dAnalyzerVersion to set
	 */
	public void setDAnalyzerVersion(String analyzerVersion) {
		dAnalyzerVersion = analyzerVersion;
	}
	/**
	 * @return the extractor
	 */
	public String getExtractor() {
		return extractor;
	}
	/**
	 * @param extractor the extractor to set
	 */
	public void setExtractor(String extractor) {
		this.extractor = extractor;
	}
	/**
	 * @return the extractorOptions
	 */
	public String getExtractorOptions() {
		return extractorOptions;
	}
	/**
	 * @param extractorOptions the extractorOptions to set
	 */
	public void setExtractorOptions(String extractorOptions) {
		this.extractorOptions = extractorOptions;
	}
	/**
	 * @return the extractorVersion
	 */
	public String getExtractorVersion() {
		return extractorVersion;
	}
	/**
	 * @param extractorVersion the extractorVersion to set
	 */
	public void setExtractorVersion(String extractorVersion) {
		this.extractorVersion = extractorVersion;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the iAnalyzer
	 */
	public String getIAnalyzer() {
		return iAnalyzer;
	}
	/**
	 * @param analyzer the iAnalyzer to set
	 */
	public void setIAnalyzer(String analyzer) {
		iAnalyzer = analyzer;
	}
	/**
	 * @return the iAnalyzerOptions
	 */
	public String getIAnalyzerOptions() {
		return iAnalyzerOptions;
	}
	/**
	 * @param analyzerOptions the iAnalyzerOptions to set
	 */
	public void setIAnalyzerOptions(String analyzerOptions) {
		iAnalyzerOptions = analyzerOptions;
	}
	/**
	 * @return the iAnalyzerVersion
	 */
	public String getIAnalyzerVersion() {
		return iAnalyzerVersion;
	}
	/**
	 * @param analyzerVersion the iAnalyzerVersion to set
	 */
	public void setIAnalyzerVersion(String analyzerVersion) {
		iAnalyzerVersion = analyzerVersion;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the instrumentSN
	 */
	public String getInstrumentSN() {
		return instrumentSN;
	}
	/**
	 * @param instrumentSN the instrumentSN to set
	 */
	public void setInstrumentSN(String instrumentSN) {
		this.instrumentSN = instrumentSN;
	}
	/**
	 * @return the instrumentType
	 */
	public String getInstrumentType() {
		return instrumentType;
	}
	/**
	 * @param instrumentType the instrumentType to set
	 */
	public void setInstrumentType(String instrumentType) {
		this.instrumentType = instrumentType;
	}
}
