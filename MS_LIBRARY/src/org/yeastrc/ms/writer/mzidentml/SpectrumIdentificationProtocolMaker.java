/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml;

import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationProtocolType;

/**
 * SpectrumIdentificationProtocolMaker.java
 * @author Vagisha Sharma
 * Aug 4, 2011
 * 
 */
public interface SpectrumIdentificationProtocolMaker {

	public SpectrumIdentificationProtocolType getProtocol() throws MzidDataProviderException;
}
