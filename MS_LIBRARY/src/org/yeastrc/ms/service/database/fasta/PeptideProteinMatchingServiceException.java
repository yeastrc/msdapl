/**
 * PeptideProteinMatchingServiceException.java
 * @author Vagisha Sharma
 * Jun 13, 2011
 */
package org.yeastrc.ms.service.database.fasta;

/**
 * 
 */
public class PeptideProteinMatchingServiceException extends Exception {

	public PeptideProteinMatchingServiceException(String message) {
		super(message);
	}
	
	public PeptideProteinMatchingServiceException(String message, Throwable t) {
		super(message, t);
	}
	
}
