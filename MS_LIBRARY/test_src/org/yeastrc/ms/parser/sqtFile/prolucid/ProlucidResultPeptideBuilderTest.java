package org.yeastrc.ms.parser.sqtFile.prolucid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class ProlucidResultPeptideBuilderTest extends TestCase {

    ProlucidResultPeptideBuilder builder = ProlucidResultPeptideBuilder.instance();
    
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
    }

    public void testRemoveDots() {
        
        String sequence = "A.(0.976)BC(123.456)DE(98.76)F(0.976).G";
        String dotless = null;
        try {
            dotless = ProlucidResultPeptideBuilder.removeDots(sequence);
        }
        catch (SQTParseException e1) {
            fail("Valid peptide sequence");
        }
        assertEquals("(0.976)BC(123.456)DE(98.76)F(0.976)", dotless);
        
        sequence = "ABCD";
        try{ProlucidResultPeptideBuilder.removeDots(sequence);fail("No dots");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = ".(0.98)ABCD";
        try{ProlucidResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "A.BCD";
        try{ProlucidResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "ABCD.";
        try{ProlucidResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        sequence = "ABC.D";
        try{ProlucidResultPeptideBuilder.removeDots(sequence);fail("Only 1 dot");}
        catch(SQTParseException e){assertEquals("Sequence does not have .(dots) in the expected position: "+sequence, e.getMessage());}
        
        try {ProlucidResultPeptideBuilder.removeDots("A.BCD.EF"); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals("Sequence does not have .(dots) in the expected position: A.BCD.EF", e.getMessage());}
        
        try {ProlucidResultPeptideBuilder.removeDots("AB.CD.F"); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals("Sequence does not have .(dots) in the expected position: AB.CD.F", e.getMessage());}
        
        try {ProlucidResultPeptideBuilder.removeDots("AB.CD"); fail("Dot in the wrong position");}
        catch(SQTParseException e) {assertEquals("Sequence does not have .(dots) in the expected position: AB.CD", e.getMessage());}
    }
    
    public void testGetOnlyPeptideSequence() {
        String seq = "BCD(123.4)E(5.67)";
        try {
            assertEquals("BCDE", ProlucidResultPeptideBuilder.getOnlyPeptideSequence(seq));
        }
        catch (SQTParseException e) {
            fail("Valid sequence for this method");
        }
        
        seq = "BCD()E(5.67)";
        try {
            assertEquals("BCDE", ProlucidResultPeptideBuilder.getOnlyPeptideSequence(seq));
            fail("Invalid sequence");
        }
        catch (SQTParseException e) {}
        
        seq = "BCD(56.E(5.67)";
        try {
            assertEquals("BCDE", ProlucidResultPeptideBuilder.getOnlyPeptideSequence(seq));
            fail("Invalid sequence");
        }
        catch (SQTParseException e) {}
    }
    
    public void testModPattern() {
        String seq = "(10.0)(20.0)ABCDE(30.0)(40.0)FGHIJ(80.0)(90.0)";
        Matcher m = ProlucidResultPeptideBuilder.multipleMods.matcher(seq);
        assertEquals("ABCDEFGHIJ", m.replaceAll(""));
        
        seq = "ABCDEFGHIJ";
        m = ProlucidResultPeptideBuilder.multipleMods.matcher(seq);
        assertEquals("ABCDEFGHIJ", m.replaceAll(""));
    }
    
    public void testModPattern2() {
        String seq = "(10.0)(20.0)ABCDE(30.0)(40.0)FGHIJ(80.0)(90.0)";
        Pattern single = Pattern.compile("\\((\\d+\\.?\\d*)\\)");
        Pattern multiple = Pattern.compile("("+single.toString()+")+");
        Pattern nTermModPattern = Pattern.compile("^"+multiple);
        Pattern cTermModPattern = Pattern.compile(multiple+"$");
        
        // test nterm pattern
        Matcher m = nTermModPattern.matcher(seq);
        int numMatchesFound = 0;
        while (m.find()) { // should only find one match but we put it in a while loop
            numMatchesFound++;
            String match = m.group();
            Matcher sm = single.matcher(match);
            String[] mass = new String[]{"10.0", "20.0"};
            int i = 0;
            while (sm.find()) {
//                System.out.println("\t"+sm.group(1)+" s: "+sm.start()+"; e: "+sm.end());
                assertEquals(mass[i++], sm.group(1));
            }
            assertEquals(2, i);
        }
        assertEquals(1, numMatchesFound);
        
        // test cterm pattern
        numMatchesFound = 0;
        m = cTermModPattern.matcher(seq);
        while (m.find()) { // should only find one match but we put it in a while loop
            numMatchesFound++;
            String match = m.group();
            Matcher sm = single.matcher(match);
            String[] mass = new String[]{"80.0", "90.0"};
            int i = 0;
            while (sm.find()) {
//                System.out.println("\t"+sm.group(1)+" s: "+sm.start()+"; e: "+sm.end());
                assertEquals(mass[i++], sm.group(1));
            }
            assertEquals(2, i);
        }
        assertEquals(1, numMatchesFound);
        
        // test multiple mods pattern
        m = multiple.matcher(seq);
        numMatchesFound = 0;
        int modifiedIndex = 0;
        int modPatternLength = 0;
        int i = 0;
        String[] mass = new String[]{"10.0", "20.0", "30.0", "40.0", "80.0", "90.0"};
        int[] modIndices = new int[]{0,4,9};
        while (m.find()) { 
            numMatchesFound++;
            String match = m.group();
            modPatternLength += match.length();
            Matcher sm = single.matcher(match);
            
            if (i == 0) {
                modifiedIndex = 0;
            }
            else {
                modifiedIndex = m.end() - modPatternLength - 1;
            }
            assertEquals(modifiedIndex, modIndices[i/2]);
            
            while (sm.find()) {
//                System.out.println("\t"+sm.group(1)+" s: "+sm.start()+"; e: "+sm.end());
                assertEquals(mass[i++], sm.group(1));
            }
        }
        assertEquals(3, numMatchesFound);
        assertEquals(6, i);
    }
    
    public void testGetResultResidueMods() {
        
        List<MsResidueModificationIn> resMods = new ArrayList<MsResidueModificationIn>(5);
        resMods.add(makeResidueModification('S', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('T', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('Y', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('M', new BigDecimal("16.0")));
        resMods.add(makeResidueModification('M', new BigDecimal("80.0")));
        
        List<MsTerminalModificationIn> termMods = new ArrayList<MsTerminalModificationIn>(5);
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("10.10")));
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("20.20")));
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("80.0")));
        termMods.add(makeTerminalModification(Terminal.CTERM, new BigDecimal("80.0")));
        termMods.add(makeTerminalModification(Terminal.CTERM, new BigDecimal("90.90")));
        
        
        // no matching residue modification
        String seq = "S(0.0)M(16.0)";
        try{builder.getResultResidueMods(seq, resMods, termMods); fail("invalid mod on S");}
        catch(SQTParseException e) {
            assertEquals("No matching modification found for modified char: S; mass: 0.0 in sequence: "+seq, e.getMessage());
        }
        // no matching residue modification at n-terminal
        seq = "(45.0)SM(16.0)";
        try{builder.getResultResidueMods(seq, resMods, termMods); fail("invalid mod on S");}
        catch(SQTParseException e) {
            assertEquals("No matching modification found for modified char: S; mass: 45.0 in sequence: "+seq, e.getMessage());
        }
        
        // no matching residue modification at c-terminal
        seq = "SM(16.0)(45.0)";
        try{builder.getResultResidueMods(seq, resMods, termMods); fail("invalid mod on M");}
        catch(SQTParseException e) {
            assertEquals("No matching modification found for modified char: M; mass: 45.0 in sequence: "+seq, e.getMessage());
        }
        
        List<MsResultResidueMod> resultMods = new ArrayList<MsResultResidueMod>();
        
        // conflicting n-terminal and residue modifications.-- getResultResidueMods will 
        // treat it as a terminal modification
        seq = "(80.0)SM(16.0)";
        try{resultMods = builder.getResultResidueMods(seq, resMods, termMods);}
        catch(SQTParseException e) {
            fail("conflicting mod on S but getResultResidueMod should treat it as a terminal mod");
        }
        assertEquals(1, resultMods.size());
        assertEquals('M', resultMods.get(0).getModifiedResidue());
        assertEquals(new BigDecimal("16.0"), resultMods.get(0).getModificationMass());
        
        // conflicting c-terminal and residue modifications.-- getResultResidueMods will 
        // treat it as a terminal modification
        seq = "(80.0)SM(16.0)(80.0)";
        resultMods.clear();
        try{resultMods = builder.getResultResidueMods(seq, resMods, termMods);}
        catch(SQTParseException e) {
            fail("conflicting mod on M and S but getResultResidueMod should treat it as a terminal mod");
        }
        assertEquals(1, resultMods.size());
        assertEquals('M', resultMods.get(0).getModifiedResidue());
        assertEquals(new BigDecimal("16.0"), resultMods.get(0).getModificationMass());
        
        
        seq = "(10.10)(20.20)S(80.0)M(16.0)(80.0)(90.90)";
        resultMods.clear();
        try {
            resultMods = builder.getResultResidueMods(seq, resMods, termMods);
        }
        catch (SQTParseException e) {
            e.printStackTrace();
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
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(0, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(1, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
    }
    
    public void testGetResultTerminalMods() {
        
        List<MsResidueModificationIn> resMods = new ArrayList<MsResidueModificationIn>(5);
        resMods.add(makeResidueModification('S', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('T', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('Y', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('M', new BigDecimal("16.0")));
        resMods.add(makeResidueModification('M', new BigDecimal("80.0")));
        
        List<MsTerminalModificationIn> termMods = new ArrayList<MsTerminalModificationIn>(5);
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("10.10")));
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("20.20")));
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("80.0")));
        termMods.add(makeTerminalModification(Terminal.CTERM, new BigDecimal("80.0")));
        termMods.add(makeTerminalModification(Terminal.CTERM, new BigDecimal("90.90")));
        termMods.add(makeTerminalModification(Terminal.CTERM, new BigDecimal("100.0")));
        
        
        // no matching n-terminal modification; will be ignored by getResultTerminalMods
        String seq = "(50.0)SM(16.0)";
        try{builder.getResultTerminalMods(seq, termMods, resMods);}
        catch(SQTParseException e) {
            e.printStackTrace();
            fail("invalid n-term mod should be ignored");
        }
        
        // no matching c-terminal modification; will be ignored by getResultTerminalMods
        seq = "SM(16.0)(50.0)";
        try{builder.getResultTerminalMods(seq, termMods, resMods);}
        catch(SQTParseException e) {
            e.printStackTrace();
            fail("invalid c-term mod should be ignored");
        }
        
        // conflicting n-term modification
        seq = "(10.10)(20.20)(80.0)SM(100.0)(90.90)";
        try{
            builder.getResultTerminalMods(seq, termMods, resMods);
            fail("conflicting n-term mod on S");
        }
        catch(SQTParseException e) {
            String err = "Conflicting modification at n-terminus: "+seq+
            "\n\tFound n-term modification with mass: 80.0 and modification for residue: S and mass: 80.0";
            assertEquals(err, e.getMessage());
        }
        
        // conflicting c-term modification
        seq = "(10.10)(20.20)SM(100.0)(80.0)(90.90)";
        try{
            builder.getResultTerminalMods(seq, termMods, resMods);
            fail("conflicting c-term mod on M");
        }
        catch(SQTParseException e) {
            String err = "Conflicting modification at c-terminus: "+seq+
                         "\n\tFound c-term modification with mass: 80.0 and modification for residue: M and mass: 80.0";
            assertEquals(err, e.getMessage());
        }
        
        
        List<MsResultTerminalMod> resultMods = new ArrayList<MsResultTerminalMod>();
        
        seq = "SM";
        resultMods.clear();
        try{resultMods = builder.getResultTerminalMods(seq, termMods, resMods);}
        catch(SQTParseException e) {fail("valid sequence");}
        assertEquals(0, resultMods.size());
        
        
        seq = "(10.10)(20.20)SM(100.0)(90.90)";
        resultMods.clear();
        try{resultMods = builder.getResultTerminalMods(seq, termMods, resMods);}
        catch(SQTParseException e) {
            e.printStackTrace();
            fail("valid sequence");
        }
        assertEquals(4, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsTerminalModificationIn>() {
            public int compare(MsTerminalModificationIn o1,
                    MsTerminalModificationIn o2) {
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        MsTerminalModificationIn mod = resultMods.get(0);
        assertEquals(Terminal.NTERM, mod.getModifiedTerminal());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(new BigDecimal("10.10"), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals(Terminal.NTERM, mod.getModifiedTerminal());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(new BigDecimal("20.20"), mod.getModificationMass());
        
        mod = resultMods.get(2);
        assertEquals(Terminal.CTERM, mod.getModifiedTerminal());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(new BigDecimal("90.90"), mod.getModificationMass());
        
        mod = resultMods.get(3);
        assertEquals(Terminal.CTERM, mod.getModifiedTerminal());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(new BigDecimal("100.0"), mod.getModificationMass());
        
    }
    
    
    public void testBuild() {
        List<MsResidueModificationIn> resMods = new ArrayList<MsResidueModificationIn>(5);
        resMods.add(makeResidueModification('S', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('T', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('Y', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('M', new BigDecimal("16.0")));
        
        List<MsTerminalModificationIn> termMods = new ArrayList<MsTerminalModificationIn>(5);
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("10.0")));
        termMods.add(makeTerminalModification(Terminal.NTERM, new BigDecimal("80.0")));
        termMods.add(makeTerminalModification(Terminal.CTERM, new BigDecimal("90.0")));
        termMods.add(makeTerminalModification(Terminal.CTERM, new BigDecimal("16.0")));
        
        String seq = "I.(10.0)S(80.0)KLRNY(80.0)FEAFEM(16.0)PM(90.0).S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, resMods, termMods);
        }
        catch (SQTParseException e) {
            e.printStackTrace();
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("SKLRNYFEAFEMPM", resultPeptide.getPeptideSequence());
        
        // RESIDUE MODS
        List<MsResultResidueMod> resultResMods = (List<MsResultResidueMod>) resultPeptide.getResultDynamicResidueModifications();
        assertEquals(3, resultResMods.size());
        Collections.sort(resultResMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        for (MsResultResidueMod mod: resultResMods)
            assertEquals('\u0000', mod.getModificationSymbol());
        
        MsResultResidueMod mod = resultResMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals(0, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultResMods.get(1);
        assertEquals('Y', mod.getModifiedResidue());
        assertEquals(5, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultResMods.get(2);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals(11, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(16.0), mod.getModificationMass());
        
        // TERMINAL MODS
        List<MsResultTerminalMod> resultTermMods = resultPeptide.getResultDynamicTerminalModifications();
        assertEquals(2, resultTermMods.size());
        Collections.sort(resultTermMods, new Comparator<MsTerminalModificationIn>() {
            public int compare(MsTerminalModificationIn o1,
                    MsTerminalModificationIn o2) {
                return o1.getModificationMass().compareTo(o2.getModificationMass());
            }});
        
        MsTerminalModificationIn tmod = resultTermMods.get(0);
        assertEquals(Terminal.NTERM, tmod.getModifiedTerminal());
        assertEquals(new BigDecimal("10.0"), tmod.getModificationMass());
        
        tmod = resultTermMods.get(1);
        assertEquals(Terminal.CTERM, tmod.getModifiedTerminal());
        assertEquals(new BigDecimal("90.0"), tmod.getModificationMass());
    }
    

    
    public void testUpperCase() {
        assertEquals("|", String.valueOf('|').toUpperCase());
        String x = "!@#$%^&*()_+-=,.?:;\"\'~`|{}[]1123456789";
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
    
    public void testBuildOneCharTwoMods() {
        List<MsResidueModificationIn> resMods = new ArrayList<MsResidueModificationIn>(5);
        resMods.add(makeResidueModification('S', new BigDecimal("80.0")));
        resMods.add(makeResidueModification('S', new BigDecimal("90.0")));
        resMods.add(makeResidueModification('M', new BigDecimal("16.0")));
        resMods.add(makeResidueModification('M', new BigDecimal("80.0")));
        
        
        String seq = "I.QKLRS(80.0)(90.0)FEAFS(90.0)M(80.0)PG.S";
        MsSearchResultPeptide resultPeptide = null;
        try {
            resultPeptide = builder.build(seq, resMods, null);
        }
        catch (SQTParseException e) {
            fail("Valid peptide sequence");
        }
        assertEquals('I', resultPeptide.getPreResidue());
        assertEquals('S', resultPeptide.getPostResidue());
        assertEquals("QKLRSFEAFSMPG", resultPeptide.getPeptideSequence());
        
        List<MsResultResidueMod> resultMods = (List<MsResultResidueMod>) resultPeptide.getResultDynamicResidueModifications();
        assertEquals(4, resultMods.size());
        Collections.sort(resultMods, new Comparator<MsResultResidueMod>() {
            public int compare(MsResultResidueMod o1,
                    MsResultResidueMod o2) {
                return Integer.valueOf(o1.getModifiedPosition()).compareTo(Integer.valueOf(o2.getModifiedPosition()));
            }});
        MsResultResidueMod mod = resultMods.get(0);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
        
        mod = resultMods.get(1);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(4, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(90.0), mod.getModificationMass());
        
        mod = resultMods.get(2);
        assertEquals('S', mod.getModifiedResidue());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(9, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(90.0), mod.getModificationMass());
        
        mod = resultMods.get(3);
        assertEquals('M', mod.getModifiedResidue());
        assertEquals('\u0000', mod.getModificationSymbol());
        assertEquals(10, mod.getModifiedPosition());
        assertEquals(BigDecimal.valueOf(80.0), mod.getModificationMass());
    }
    
    private MsResidueModificationIn makeResidueModification(char modResidue, BigDecimal mass) {
        ResidueModification mod = new ResidueModification();
        mod.setModificationMass(mass);
        mod.setModifiedResidue(modResidue);
        return mod;
    }
    
    private MsTerminalModificationIn makeTerminalModification(Terminal modTerminal, BigDecimal mass) {
        TerminalModification mod = new TerminalModification();
        mod.setModificationMass(mass);
        mod.setModifiedTerminal(modTerminal);
        return mod;
    }
}
