/**
 * AmbiguousSpectraFilterTest.java
 * @author Vagisha Sharma
 * Apr 3, 2012
 */
package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.analysis.percolator.PercolatorResult;
import org.yeastrc.ms.domain.analysis.percolator.impl.PercolatorResultBean;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.impl.SearchResultPeptideBean;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

/**
 * 
 */
public class AmbiguousSpectraFilterTest extends TestCase {

	public final void testFilterSpectraWithMultipleResults1() 
	{
		AmbiguousSpectraFilter filter = AmbiguousSpectraFilter.instance();
		
		List<PercolatorResult> psmList = new ArrayList<PercolatorResult>();
		
		PercolatorResultBean result = new PercolatorResultBean();
		result.setCharge(1);
		result.setScanId(101);
		result.setId(201);
		MsSearchResultPeptide resultPeptide = new SearchResultPeptideBean();
		resultPeptide.setPeptideSequence("PEPTIDE");
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		result = new PercolatorResultBean();
		result.setCharge(1);
		result.setScanId(101);
		result.setId(202);
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		result = new PercolatorResultBean();
		result.setCharge(1);
		result.setScanId(102);
		result.setId(203);
		resultPeptide = new SearchResultPeptideBean();
		resultPeptide.setPeptideSequence("EDITPEP");
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		
		try {
			assertEquals(3, psmList.size());
			filter.filterSpectraWithMultipleResults(psmList);
			assertEquals(3, psmList.size()); // nothing should have been removed
			
		} catch (ModifiedSequenceBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public final void testFilterSpectraWithMultipleResults2() 
	{
		AmbiguousSpectraFilter filter = AmbiguousSpectraFilter.instance();
		
		List<PercolatorResult> psmList = new ArrayList<PercolatorResult>();
		
		PercolatorResultBean result = new PercolatorResultBean();
		result.setCharge(1);
		result.setScanId(101);
		result.setId(201);
		MsSearchResultPeptide resultPeptide = new SearchResultPeptideBean();
		resultPeptide.setPeptideSequence("PEPTIDE");
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		result = new PercolatorResultBean();
		result.setCharge(2);
		result.setScanId(101);
		result.setId(202);
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		result = new PercolatorResultBean();
		result.setCharge(1);
		result.setScanId(102);
		result.setId(203);
		resultPeptide = new SearchResultPeptideBean();
		resultPeptide.setPeptideSequence("EDITPEP");
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		result = new PercolatorResultBean();
		result.setCharge(2);
		result.setScanId(103);
		result.setId(204);
		resultPeptide = new SearchResultPeptideBean();
		resultPeptide.setPeptideSequence("REMOVE_1");
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		result = new PercolatorResultBean();
		result.setCharge(3);
		result.setScanId(103);
		result.setId(204);
		resultPeptide = new SearchResultPeptideBean();
		resultPeptide.setPeptideSequence("REMOVE_2");
		result.setResultPeptide(resultPeptide);
		psmList.add(result);
		
		
		try {
			assertEquals(5, psmList.size());
			filter.filterSpectraWithMultipleResults(psmList);
			assertEquals(3, psmList.size()); // last two entries should have been removed -- 
			                                 // same scan matching two different peptides
			
			assertEquals("PEPTIDE", psmList.get(0).getResultPeptide().getPeptideSequence());
			assertEquals("PEPTIDE", psmList.get(1).getResultPeptide().getPeptideSequence());
			assertEquals("EDITPEP", psmList.get(2).getResultPeptide().getPeptideSequence());
			
		} catch (ModifiedSequenceBuilderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
