package org.yeastrc.ms.domain.search.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.service.ModifiedSequenceBuilderException;

import junit.framework.TestCase;


public class SearchResultPeptideBeanTest extends TestCase {

	protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
	public void testGetModifiedPeptidePS()
	{
		// Modified sequence: R]EY*AFRS*LVT*AM#EK[Y
		
		// Dynamic modifications
        ResidueModification S_mod = new ResidueModification('S', '*', "80.0");
        ResidueModification T_mod = new ResidueModification('T', '*', "-80.0");
        ResidueModification Y_mod = new ResidueModification('Y', '*', "80.0");
        ResidueModification M_mod = new ResidueModification('M', '#', "16.0");
        
        List<MsResultResidueMod> resultDynaMods = new ArrayList<MsResultResidueMod>(4);
        resultDynaMods.add(new ResultResidueModBean(Y_mod.getModifiedResidue(), 
									                Y_mod.getModificationSymbol(),
									                Y_mod.getModificationMass(), 
									                1));
        resultDynaMods.add(new ResultResidueModBean(S_mod.getModifiedResidue(), 
        		                                    S_mod.getModificationSymbol(),
        		                                    S_mod.getModificationMass(), 
        		                                    5));
        resultDynaMods.add(new ResultResidueModBean(T_mod.getModifiedResidue(), 
									                T_mod.getModificationSymbol(),
									                T_mod.getModificationMass(), 
									                8));
        resultDynaMods.add(new ResultResidueModBean(M_mod.getModifiedResidue(), 
									                M_mod.getModificationSymbol(),
									                M_mod.getModificationMass(), 
									                10));
        
        
        // Dynamic terminal modifications
        List<MsResultTerminalMod> dynaTermMods = new ArrayList<MsResultTerminalMod>();
        dynaTermMods.add(new ResultTerminalModBean(Terminal.NTERM, MsTerminalModificationIn.NTERM_MOD_CHAR_SEQUEST, new BigDecimal(-100.0)));
        dynaTermMods.add(new ResultTerminalModBean(Terminal.CTERM, MsTerminalModificationIn.CTERM_MOD_CHAR_SEQUEST, new BigDecimal(150.0)));
        
        
        
        
        
		SearchResultPeptideBean resBean = new SearchResultPeptideBean();
		String fullModifiedSequencePS = "R.]EY*AFRS*LVT*AM#EK[.Y";
		String fullModifiedSequencePSNoTermMods = "R.EY*AFRS*LVT*AM#EK.Y";
		String modifiedSequencePS = "]EY*AFRS*LVT*AM#EK[";
		String modifiedSequencePSNoTermMods = "EY*AFRS*LVT*AM#EK";
		String fullModifiedSequenceMassDiffOnly = "R.n[-100]EY[+80]AFRS[+80]LVT[-80]AM[+16]EKc[+150].Y";
		String fullModifiedSequence = "R.n[-99]EY[243]AFRS[167]LVT[21]AM[147]EKc[167].Y";
		String modifiedSequenceMassDiffOnly = "n[-100]EY[+80]AFRS[+80]LVT[-80]AM[+16]EKc[+150]";
		String modifiedSequence = "n[-99]EY[243]AFRS[167]LVT[21]AM[147]EKc[167]";
		
		String sequence = "EYAFRSLVTAMEK";
		resBean.setPeptideSequence(sequence);
		resBean.setDynamicResidueModifications(resultDynaMods);
		resBean.setDynamicTerminalModifications(dynaTermMods);
		resBean.setPostResidue('Y');
		resBean.setPreResidue('R');
		
		assertEquals(sequence, resBean.getPeptideSequence());
		assertEquals(fullModifiedSequencePS, resBean.getFullModifiedPeptidePS());
		assertEquals(fullModifiedSequencePS, resBean.getFullModifiedPeptidePS(true));
		assertEquals(fullModifiedSequencePSNoTermMods, resBean.getFullModifiedPeptidePS(false));
		assertEquals(modifiedSequencePS, resBean.getModifiedPeptidePS());
		assertEquals(modifiedSequencePSNoTermMods, resBean.getModifiedPeptidePS(false));
		assertEquals(modifiedSequencePS, resBean.getModifiedPeptidePS(true));
		
		
		try {
			assertEquals(fullModifiedSequenceMassDiffOnly, resBean.getFullModifiedPeptide(true));
		} catch (ModifiedSequenceBuilderException e) {
			fail("Should not fail geting full modified peptide");
		}
		try {
			assertEquals(modifiedSequenceMassDiffOnly, resBean.getModifiedPeptide(true));
		} catch (ModifiedSequenceBuilderException e) {
			fail("Should not fail geting full modified peptide");
		}
		
		try {
			assertEquals(fullModifiedSequence, resBean.getFullModifiedPeptide(false));
		} catch (ModifiedSequenceBuilderException e) {
			fail("Should not fail geting full modified peptide");
		}
		try {
			assertEquals(fullModifiedSequence, resBean.getFullModifiedPeptide());
		} catch (ModifiedSequenceBuilderException e) {
			fail("Should not fail geting full modified peptide");
		}
		try {
			assertEquals(modifiedSequence, resBean.getModifiedPeptide(false));
		} catch (ModifiedSequenceBuilderException e) {
			fail("Should not fail geting full modified peptide");
		}
		try {
			assertEquals(modifiedSequence, resBean.getModifiedPeptide());
		} catch (ModifiedSequenceBuilderException e) {
			fail("Should not fail geting full modified peptide");
		}
	}
}
