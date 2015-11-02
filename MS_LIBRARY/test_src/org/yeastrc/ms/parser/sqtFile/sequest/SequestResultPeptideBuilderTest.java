/**
 * MsSearchResultPeptideBuilderTest.java
 * @author Vagisha Sharma
 * Jul 13, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sqtFile.sequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsSearchResultPeptide;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.TerminalModification;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;

/**
 * 
 */
public class SequestResultPeptideBuilderTest extends TestCase {

    SequestResultPeptideBuilder builder = SequestResultPeptideBuilder.instance();
    
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGetPreResidue() {
        String sequence = "ABCD";
        try {builder.getPreResidue(sequence); fail("No Pre Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: ABCD", e.getMessage());}
        sequence = ".ABCD";
        try {builder.getPreResidue(sequence); fail("No Pre Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: "+sequence, e.getMessage());}
        sequence = "ABCD.EF";
        try{builder.getPreResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get PRE residue: "+sequence, e.getMessage());}
        sequence = "A.BCDE.F";
        char preRes = 0;
        try {
            preRes = builder.getPreResidue(sequence);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('A', preRes);
        
        sequence = "-]ABC[D";
        try {
            preRes = builder.getPreResidue(sequence);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('-', preRes);
    }
    
    public void testGetPostResidue() {
        String sequence = "ABCD";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = ".ABCD";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = "ABCD.EF";
        try {builder.getPostResidue(sequence); fail("No Post Residue");}
        catch(SQTParseException e) {assertEquals("Invalid peptide sequence; cannot get POST residue: "+sequence, e.getMessage());}
        sequence = "A.BCDE.F";
        char postRes = 0;
        try {
            postRes = builder.getPostResidue(sequence);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('F', postRes);
        
        sequence = "-]ABC[D";
        try {
            postRes = builder.getPostResidue(sequence);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('D', postRes);
    }
    
    public void testRemoveDots() {
        
        String sequence = "A.BC#DE@F.G";
        String dotless = null;
        try {
            dotless = SequestResultPeptideBuilder.removeDots(sequence);
        }
        catch (SQTParseException e1) {
            fail("Valid peptide sequence");
        }
        assertEquals("BC#DE@F", dotless);
        
        sequence = "-]ABC[-";
        try {
            dotless = SequestResultPeptideBuilder.removeDots(sequence);
        }
        catch (SQTParseException e1) {
            fail("Valid peptide sequence");
        }
        assertEquals("ABC", dotless);
        
        sequence = "ABCD";
        String exceptionMsg = "Sequence does not have .(dots) or terminam modification characters (']', '[') in the expected position: ";
		try{SequestResultPeptideBuilder.removeDots(sequence);fail("No dots");}
        catch(SQTParseException e){assertEquals(exceptionMsg+sequence, e.getMessage());}
        
        sequence = ".ABCD";
        try{SequestResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals(exceptionMsg+sequence, e.getMessage());}
        
        sequence = "A.BCD";
        try{SequestResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals(exceptionMsg+sequence, e.getMessage());}
        
        sequence = "ABCD.";
        try{SequestResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals(exceptionMsg+sequence, e.getMessage());}
        
        sequence = "ABC.D";
        try{SequestResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals(exceptionMsg+sequence, e.getMessage());}
        
        sequence = "A.BCD.EF";
        try {SequestResultPeptideBuilder.removeDots(sequence); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals(exceptionMsg+sequence, e.getMessage());}
        
        sequence = "AB.CD.F";
        try {SequestResultPeptideBuilder.removeDots(sequence); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals(exceptionMsg+sequence, e.getMessage());}
        
        sequence = "AB.CD";
        try {SequestResultPeptideBuilder.removeDots(sequence); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals(exceptionMsg+sequence, e.getMessage());}
    }
    
    public void testGetOnlyPeptideSequence() {
        String seq = "B123CD#E*";
        try {
            assertEquals("BCDE", SequestResultPeptideBuilder.getOnlyPeptideSequence(seq));
        }
        catch (SQTParseException e) {
            fail("Valid sequence for this method");
        }
    }
    
    public void testGetResultMods() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        dynaMods.add(new ResidueModification('Y', '*', "80.0"));
        dynaMods.add(new ResidueModification('M', '#', "16.0"));
        
        String seq = "A.S*M#.Z";
        try{builder.getResultMods(seq, dynaMods); fail("Sequence still has dots");}
        catch(SQTParseException e) {assertEquals("No matching modification found: A.; sequence: "+seq, e.getMessage());}
        
        try {
            seq = SequestResultPeptideBuilder.removeDots(seq);
        }
        catch (SQTParseException e) {
            fail("Valid sequence");
        }
        List<MsResultResidueMod> resultMods = new ArrayList<MsResultResidueMod>(0);
        try {
            resultMods = builder.getResultMods(seq, dynaMods);
        }
        catch (SQTParseException e) {
            fail("Valid sequence");
        }
        assertEquals(2, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsResultResidueMod mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(0, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(1, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
    }
    
    public void testGetResultModsInvalid() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        dynaMods.add(new ResidueModification('Y', '*', "80.0"));
        dynaMods.add(new ResidueModification('M', '#', "16.0"));
        
        String seq = "S*M#C*AB";
        try{builder.getResultMods(seq, dynaMods); fail("Invalid mod char");}
        catch(SQTParseException e) {assertEquals("No matching modification found: C*; sequence: "+seq, e.getMessage());}
    
        seq = "S*M@C*AB";
        try{builder.getResultMods(seq, dynaMods); fail("Invalid mod char");}
        catch(SQTParseException e) {assertEquals("No matching modification found: M@; sequence: "+seq, e.getMessage());}
    }
    
    public void testGetResultModsValidEmbeddedMass() {
    	
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        dynaMods.add(new ResidueModification('Y', '*', "80.0"));
        dynaMods.add(new ResidueModification('M', '#', "16.0"));
  
        String seq = "S(80)ABM(16.0)A";
        try{
        	builder.getResultMods(seq, dynaMods); 
        }catch(SQTParseException e) {
        	fail("Valid sequence. Exception message: " + e.getMessage());
        }
        
        
        seq = "S(80)ABM(16.0)A";
        try{
        	builder.getResultMods(seq, dynaMods); 
        }catch(SQTParseException e) {
        	fail("Valid sequence. Exception message: " + e.getMessage());
        }
        
        seq = "S(80)ABM(16.0)";
        try{
        	builder.getResultMods(seq, dynaMods); 
        }catch(SQTParseException e) {
        	fail("Valid sequence. Exception message: " + e.getMessage());
        }

    }
    
    
    public void testGetResultModsInvalidEmbeddedMass() {
    	
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('A', '*', "80.0E-2"));
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        dynaMods.add(new ResidueModification('Y', '*', "80.0"));
        dynaMods.add(new ResidueModification('M', '#', "16.0"));
  
        String seq = "S(80.5)ABM(16.0)A";
        try{
        	builder.getResultMods(seq, dynaMods); 
        	fail("Invalid sequence. seq: " + seq);
        }catch(SQTParseException e) {
        	assertEquals("No matching modification found: S(80.5); sequence: " + seq, e.getMessage());
        }
        
        seq = "A(80.0)ABM(16.0)A";
        try{
        	builder.getResultMods(seq, dynaMods); 
        	fail("Invalid sequence. seq: " + seq);
        }catch(SQTParseException e) {
        	String message = e.getMessage();
//        	System.out.println(message);
        	assertEquals("No matching modification found: A(80.0); sequence: " + seq, e.getMessage());
        }
        
        
        seq = "S(80.5";
        try{
        	builder.getResultMods(seq, dynaMods); 
        	fail("Invalid sequence. seq: " + seq);
        }catch(SQTParseException e) {
        	String message = e.getMessage();
        	assertEquals("Failed to find closing dynamic modification character ')' before end of sequence.  Processing modified residue 'S' at position 0; sequence: " + seq, e.getMessage());
        }
 
        
        seq = "S)";
        try{
        	builder.getResultMods(seq, dynaMods); 
        	fail("Invalid sequence. seq: " + seq);
        }catch(SQTParseException e) {
        	String message = e.getMessage();
//        	System.out.println(message);
        	assertEquals("Found closing dynamic modification character ')' without preceding matching start dynamic modification character.  Processing modified residue 'S' at position 0; sequence: " + seq, e.getMessage());
        }
        
    }
    
    public void testGetResultTerminalMods() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        dynaMods.add(new ResidueModification('Y', '*', "80.0"));
        dynaMods.add(new ResidueModification('M', '#', "16.0"));
        
        List<TerminalModification> dynaTermMods = new ArrayList<TerminalModification>(2);
        dynaTermMods.add(new TerminalModification(Terminal.NTERM, MsTerminalModificationIn.NTERM_MOD_CHAR_SEQUEST, new BigDecimal(100.0)));
        dynaTermMods.add(new TerminalModification(Terminal.CTERM, MsTerminalModificationIn.CTERM_MOD_CHAR_SEQUEST, new BigDecimal(150.0)));
   
        String seq = "A]S*M#[Z";
        List<MsResultTerminalMod> resultTermMods = new ArrayList<MsResultTerminalMod>();
        try {
        	resultTermMods = builder.getTerminalMods(seq, dynaTermMods);
        }
        catch(SQTParseException e) {
        	fail("Valid terminal mods in sequence");
        }
        assertEquals(2, resultTermMods.size());
        assertEquals(Terminal.NTERM, resultTermMods.get(0).getModifiedTerminal());
        assertEquals(100.0, resultTermMods.get(0).getModificationMass().doubleValue());
        assertEquals(Terminal.CTERM, resultTermMods.get(1).getModifiedTerminal());
        assertEquals(150.0, resultTermMods.get(1).getModificationMass().doubleValue());
        
        
        seq = "A]S*M#.Z";
        // get the terminal mods
        resultTermMods = new ArrayList<MsResultTerminalMod>();
        try {
        	resultTermMods = builder.getTerminalMods(seq, dynaTermMods);
        }
        catch(SQTParseException e) {
        	fail("Valid terminal mods in sequence");
        }
        assertEquals(1, resultTermMods.size());
        assertEquals(Terminal.NTERM, resultTermMods.get(0).getModifiedTerminal());
        
        
        // get the residue mods
        try{builder.getResultMods(seq, dynaMods); fail("Sequence still has dots");}
        catch(SQTParseException e) {assertEquals("No matching modification found: A]; sequence: "+seq, e.getMessage());}
        
        try {
            seq = SequestResultPeptideBuilder.removeDots(seq);
        }
        catch (SQTParseException e) {
            fail("Valid sequence");
        }
        List<MsResultResidueMod> resultMods = new ArrayList<MsResultResidueMod>(0);
        try {
            resultMods = builder.getResultMods(seq, dynaMods);
        }
        catch (SQTParseException e) {
            fail("Valid sequence");
        }
        assertEquals(2, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsResultResidueMod mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(0, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(1, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
    }
    
    public void testGetResultTermModsInvalid() {
    	
    	List<TerminalModification> dynaTermMods = new ArrayList<TerminalModification>(2);
        
        String seq = "A]S*M#.Z";;
        try{builder.getTerminalMods(seq, dynaTermMods); fail("No terminal mods in settings");}
        catch(SQTParseException e) {assertEquals("No variable N-terminus modification found for peptide "+seq, e.getMessage());}
        
        seq = "A.S*M#[Z";;
        try{builder.getTerminalMods(seq, dynaTermMods); fail("No terminal mods in settings");}
        catch(SQTParseException e) {assertEquals("No variable C-terminus modification found for peptide "+seq, e.getMessage());}
   
    }
    
    
    public void testBuild1() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        dynaMods.add(new ResidueModification('Y', '*', "80.0"));
        dynaMods.add(new ResidueModification('M', '#', "16.0"));
        
        String seq = "I.QKLRNY*FEAFEM#PG.S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, dynaMods, null);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRNYFEAFEMPG", resultPeptide.getPeptideSequence());
        
        List<MsResultResidueMod> resultMods = (List<MsResultResidueMod>) resultPeptide.getResultDynamicResidueModifications();
        assertEquals(2, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsResultResidueMod mod = resultMods.get(0);
        assertEquals('Y', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(5, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(11, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
        
    }
    
    public void testBuild2() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(0);
        
        String seq = "L.GI|62822520|gb|AAY15068.1|SGVSIVNAVTYEPTVAGRPNAV.H";
        String dotLess = null;
        try {
            dotLess = SequestResultPeptideBuilder.removeDots(seq);
        }
        catch (SQTParseException e1) {
            fail("Valid sequence for removeDots method");
        }
        
        try{builder.build(seq, dynaMods, null); fail("Invalid sequence");}
        catch(SQTParseException e) {assertEquals("No matching modification found: I|; sequence: "+dotLess, e.getMessage());}
    }
    
    public void testBuild3() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(0);
        
        String seq = "L.Gi|62822520|gb|AAY15068.1|SGVSIVNAVTYEPTVAGRPNAV.H";
        String dotLess = null;
        try {
            dotLess = SequestResultPeptideBuilder.removeDots(seq);
        }
        catch (SQTParseException e1) {
            fail("Valid sequence for removeDots method");
        }
        
        try{builder.build(seq, dynaMods, null); fail("Invalid sequence");}
        catch(SQTParseException e) {assertEquals("No matching modification found: Gi; sequence: "+dotLess, e.getMessage());}
    }
    
    public void testBuild4() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        String seq = "A.*SCDS*.Z";
        try {builder.build(seq, dynaMods, null);fail("Invalid sequence");}
        catch(SQTParseException e){
            String errMsg = "No matching modification found: \u0000*; sequence: *SCDS*";
            assertEquals(errMsg.length(), e.getMessage().length());
            assertEquals(errMsg, e.getMessage());
        }
    }
    
    public void testBuild5() {
    	
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        dynaMods.add(new ResidueModification('Y', '*', "80.0"));
        dynaMods.add(new ResidueModification('M', '#', "16.0"));
        
        List<TerminalModification> dynaTermMods = new ArrayList<TerminalModification>(2);
        dynaTermMods.add(new TerminalModification(Terminal.NTERM, MsTerminalModificationIn.NTERM_MOD_CHAR_SEQUEST, new BigDecimal(100.0)));
        dynaTermMods.add(new TerminalModification(Terminal.CTERM, MsTerminalModificationIn.CTERM_MOD_CHAR_SEQUEST, new BigDecimal(150.0)));
   
        
        String seq = "I]QKLRNY*FEAFEM#PG[S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, dynaMods, dynaTermMods);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRNYFEAFEMPG", resultPeptide.getPeptideSequence());
        
        // dynamic residue modifications
        List<MsResultResidueMod> resultMods = (List<MsResultResidueMod>) resultPeptide.getResultDynamicResidueModifications();
        assertEquals(2, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsResultResidueMod mod = resultMods.get(0);
        assertEquals('Y', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(5, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(11, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
        
        // dynamic terminal modifications
        List<MsResultTerminalMod> resultTermMods = resultPeptide.getResultDynamicTerminalModifications();
        assertEquals(2, resultTermMods.size());
        assertEquals(Terminal.NTERM, resultTermMods.get(0).getModifiedTerminal());
        assertEquals(100.0, resultTermMods.get(0).getModificationMass().doubleValue());
        assertEquals(Terminal.CTERM, resultTermMods.get(1).getModifiedTerminal());
        assertEquals(150.0, resultTermMods.get(1).getModificationMass().doubleValue());
        
    }
    
    public void testUpperCase() {
        assertEquals("|", String.valueOf('|').toUpperCase());
        String x = "!@#$%^&*()_+-=,.?:;\"\'~`|{}[]";
        String y = x.toUpperCase();
        assertEquals(x, y);
    }
    
    public void testBuildInvalidCharacter() {
        List<MsResidueModificationIn> resMods = new ArrayList<MsResidueModificationIn>(0);
        String seq = "I.QKLpRSFEAFSMPG.S";
        try {
            builder.build(seq, resMods, null);
            fail("Invalid character 'p' n sequence");
        }
        catch (SQTParseException e) {
//            e.printStackTrace();
        }
    }
    
    public void testBuildResidueWithTwoPossibleMods() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', "80.0"));
        dynaMods.add(new ResidueModification('S', '#', "111.0"));
        dynaMods.add(new ResidueModification('T', '*', "80.0"));
        
        String seq = "I.QKLRS*FEAFS#MPGT*.S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, dynaMods, null);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRSFEAFSMPGT", resultPeptide.getPeptideSequence());
        
        List<MsResultResidueMod> resultMods = (List<MsResultResidueMod>) resultPeptide.getResultDynamicResidueModifications();
        assertEquals(3, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsResultResidueMod mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(9, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(111.0), mod.getModificationMass());
        
        mod = resultMods.get(2);
        assertEquals('T', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(13, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());

    }
    
    public void testBuildOneCharTwoMods() {
        List<ResidueModification> dynaMods = new ArrayList<ResidueModification>(4);
        dynaMods.add(new ResidueModification('S', '*', new BigDecimal("80.0")));
        dynaMods.add(new ResidueModification('S', '#', new BigDecimal("111.0")));
        dynaMods.add(new ResidueModification('T', '*', new BigDecimal("80.0")));
        
        String seq = "I.QKLRS*#FEAFS#MPG.S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, dynaMods, null);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRSFEAFSMPG", resultPeptide.getPeptideSequence());
        
        List<MsResultResidueMod> resultMods = (List<MsResultResidueMod>) resultPeptide.getResultDynamicResidueModifications();
        assertEquals(3, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsResultResidueMod mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('*', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(111.0), mod.getModificationMass());
        
        mod = resultMods.get(2);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('#', mod.getModificationSymbol());
        assertEquals(9, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(111.0), mod.getModificationMass());
    }
    
//    private static class Mod implements MsResidueModificationIn {
//
//        private char modResidue;
//        private char modSymbol;
//        private BigDecimal modMass;
//
//        public Mod(char modResidue, char modSymbol, String modMass) {
//            this.modResidue = modResidue;
//            this.modSymbol = modSymbol;
//            this.modMass = new BigDecimal(modMass);
//        }
//        public BigDecimal getModificationMass() {
//            return modMass;
//        }
//
//        public char getModificationSymbol() {
//            return modSymbol;
//        }
//
////        public ModificationType getModificationType() {
////            return ModificationType.DYNAMIC;
////        }
//
//        public char getModifiedResidue() {
//            return modResidue;
//        }
//    }
    
}
