/**
 * PercolatorXmlDataProvider.java
 * @author Vagisha Sharma
 * Sep 11, 2010
 */
package org.yeastrc.ms.parser;

import java.util.List;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorParam;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorPeptideResultIn;
import org.yeastrc.ms.domain.analysis.percolator.PercolatorResultIn;

/**
 * 
 */
public interface PercolatorXmlDataProvider {

	public abstract List<PercolatorParam> getPercolatorParams();
	
    public abstract boolean hasNextPsm() throws DataProviderException;
    
    public abstract PercolatorResultIn getNextPsm() throws DataProviderException;
    
    public abstract boolean hasNextPeptide() throws DataProviderException;
    
    public abstract PercolatorPeptideResultIn getNextPeptide() throws DataProviderException;
    
    public abstract void close();
}
