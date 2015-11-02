package org.yeastrc.ms.dao.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.BaseDAOTestCase;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsResultResidueMod;
import org.yeastrc.ms.domain.search.MsResultTerminalMod;
import org.yeastrc.ms.domain.search.MsTerminalModification;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.MsResidueModificationWrap;
import org.yeastrc.ms.domain.search.impl.MsTerminalModificationWrap;
import org.yeastrc.ms.domain.search.impl.ResultResidueModIds;

public class MsSearchModificationDAOImplTest extends BaseDAOTestCase {

    protected void setUp() throws Exception {
        super.setUp();
        resetDatabase();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testOperationsForStaticModifications() {

        // create some static modification objects
        MsResidueModificationIn mod1_1 = makeStaticResidueMod('A', "123.4");
        MsResidueModificationIn mod1_2 = makeStaticResidueMod('B', "56.7");

        MsResidueModificationIn mod2_1 = makeStaticResidueMod('X', "987.6");
        MsResidueModificationIn mod2_2 = makeStaticResidueMod('Y', "54.3");

        // save them to the database
        modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod1_1, 1)); // searchId = 1
        modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod1_2, 1));
        modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod2_1, 2)); // searchId = 2
        modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod2_2, 2));

        // load them back
        List<MsResidueModification> modList1 = modDao.loadStaticResidueModsForSearch(1);
        assertEquals(2, modList1.size());

        List<MsResidueModification> modList2 = modDao.loadStaticResidueModsForSearch(2);
        assertEquals(2, modList2.size());

        // sort by id
        Collections.sort(modList1, new MsSearchModComparator());
        Collections.sort(modList2, new MsSearchModComparator());

        // Make sure all fields were saved and read back accurately
        compareStaticMods(mod1_1, modList1.get(0));
        compareStaticMods(mod1_2, modList1.get(1));
        compareStaticMods(mod2_1, modList2.get(0));
        compareStaticMods(mod2_2, modList2.get(1));


        // now delete the modifications
        modDao.deleteStaticResidueModsForSearch(1);
        modList1 = modDao.loadStaticResidueModsForSearch(1);
        assertEquals(0, modList1.size());

        modDao.deleteStaticResidueModsForSearch(2);
        modList2 = modDao.loadStaticResidueModsForSearch(2);
        assertEquals(0, modList2.size());
    }

    public void testSaveNullValuesNotAllowed() {
        
        char nochar = 0;
        // test static residue mod
        MsResidueModificationIn mod = makeStaticResidueMod(nochar, "40.0");
        try {modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod, 1)); fail("residue cannot be null");}
        catch(RuntimeException e) {System.out.println(e.getMessage());}
        
        mod = makeStaticResidueMod('A', null);
        try {modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod, 1)); fail("mass shift cannot be null");}
        catch(RuntimeException e) {System.out.println(e.getMessage());}
        
        mod = makeStaticResidueMod('A', "40.0");
        modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod, 56));
        List<MsResidueModification> modsDb = modDao.loadStaticResidueModsForSearch(56);
        assertEquals(1, modsDb.size());
//        assertEquals(56, modsDb.get(0).getSearchId());
        assertEquals('A', modsDb.get(0).getModifiedResidue());
        assertEquals(40.0, modsDb.get(0).getModificationMass().doubleValue());
        assertEquals(nochar, modsDb.get(0).getModificationSymbol());
        
        // test static terminal mod
        MsTerminalModificationIn tmod = makeStaticTerminalMod(null, "40.0");
        try {modDao.saveStaticTerminalMod(new MsTerminalModificationWrap(tmod, 1)); fail("terminus cannot be null");}
        catch(RuntimeException e) {System.out.println(e.getMessage());}
        
        tmod = makeStaticTerminalMod(Terminal.NTERM, null);
        try {modDao.saveStaticTerminalMod(new MsTerminalModificationWrap(tmod, 1)); fail("mass shift cannot be null");}
        catch(RuntimeException e) {System.out.println(e.getMessage());}
        
        tmod = makeStaticTerminalMod(Terminal.CTERM, "40.123456789");
        modDao.saveStaticTerminalMod(new MsTerminalModificationWrap(tmod, 85));
        List<MsTerminalModification> tmodsDb = modDao.loadStaticTerminalModsForSearch(85);
        assertEquals(1, tmodsDb.size());
//        assertEquals(85, tmodsDb.get(0).getSearchId());
        assertEquals(Terminal.CTERM, tmodsDb.get(0).getModifiedTerminal());
        assertEquals(40.123456789, tmodsDb.get(0).getModificationMass().doubleValue());
        assertEquals(nochar, tmodsDb.get(0).getModificationSymbol());
        
        // test dynamic residue mod
        mod = makeDynamicResidueMod(nochar, "98.4", nochar);
        try {modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod, 1));fail("residue cannot be null");}
        catch(RuntimeException e){}
        
        mod = makeDynamicResidueMod('B', null, nochar);
        try {modDao.saveStaticResidueMod(new MsResidueModificationWrap(mod, 1));fail("mass cannot be null");}
        catch(RuntimeException e){}
        
        mod = makeDynamicResidueMod('B', "98.4", nochar);
        modDao.saveDynamicResidueMod(new MsResidueModificationWrap(mod, 65));
        modsDb = modDao.loadDynamicResidueModsForSearch(65);
        assertEquals(1, modsDb.size());
        MsResidueModification m = modsDb.get(0);
        assertEquals('B', m.getModifiedResidue());
        assertEquals(98.4, m.getModificationMass().doubleValue());
        assertEquals('\u0000', m.getModificationSymbol());
        
        // test dynamic terminal mod
        tmod = makeDynamicTerminalMod(null, "40.0", 'p');
        try {modDao.saveStaticTerminalMod(new MsTerminalModificationWrap(tmod, 1)); fail("terminus cannot be null");}
        catch(RuntimeException e) {System.out.println(e.getMessage());}
        
        tmod = makeDynamicTerminalMod(Terminal.NTERM, null, nochar);
        try {modDao.saveStaticTerminalMod(new MsTerminalModificationWrap(tmod, 1)); fail("mass shift cannot be null");}
        catch(RuntimeException e) {System.out.println(e.getMessage());}
        
        tmod = makeDynamicTerminalMod(Terminal.NTERM, "40.123456789", 'p');
        modDao.saveDynamicTerminalMod(new MsTerminalModificationWrap(tmod, 97));
        tmodsDb = modDao.loadDynamicTerminalModsForSearch(97);
        assertEquals(1, tmodsDb.size());
//        assertEquals(97, tmodsDb.get(0).getSearchId());
        assertEquals(Terminal.NTERM, tmodsDb.get(0).getModifiedTerminal());
        assertEquals(40.123456789, tmodsDb.get(0).getModificationMass().doubleValue());
        assertEquals('p', tmodsDb.get(0).getModificationSymbol());
    }

    public void testOperationsForDynamicModifications() {

        // create some dynamic modification objects with the following values
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'*', '#', '&', '@'};

        doDynamicModTest(residue, mass, symbol);
    }

    public void testOperationsForDynamicModificationsWithEmptySymbol() {

        // create some dynamic modification objects with the following values
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'\u0000', '\u0000', '*', '\u0000'};
        createDynaMods(mass, residue, symbol);

        doDynamicModTest(residue, mass, symbol);
    }

    private MsResidueModificationIn[] createDynaMods(String[] mass, char[] residue, char[] symbol) {
        assertTrue(residue.length > 0);
        assertEquals(mass.length, residue.length);
        assertEquals(residue.length, symbol.length);

        MsResidueModificationIn[] mods = new MsResidueModificationIn[residue.length];
        for (int i = 0; i < residue.length; i++) {
            mods[i] = makeDynamicResidueMod(residue[i], mass[i], symbol[i]);
        }
        return mods;
    }
    
    private MsTerminalModificationIn[] createDynaTermMods(String[] mass, Terminal[] terminals) {
        assertTrue(mass.length > 0);
        assertEquals(mass.length, terminals.length);

        MsTerminalModificationIn[] mods = new MsTerminalModificationIn[mass.length];
        for (int i = 0; i < mass.length; i++) {
            mods[i] = makeDynamicTerminalMod(terminals[i], mass[i], '\u0000');
        }
        return mods;
    }

    private void doDynamicModTest(char[] residue, String[] mass, char[] symbol) {

        MsResidueModificationIn[] mods = createDynaMods(mass, residue, symbol);
        assertEquals(residue.length, mods.length);

        // save them
        int wid1 = 0;
        int wid2 = 0;
        int searchId = 0;
        for (int i = 0; i < mods.length; i++) {
            searchId = i % 2  == 0 ? 2 : 1; // even numbers get a search id of 2; odd numbers get 1
            modDao.saveDynamicResidueMod(new MsResidueModificationWrap(mods[i], searchId));
            searchId = searchId == 1 ? wid1++ : wid2++;
        }

        // read them back and make sure inserted values were accurate
        List<MsResidueModification> modList1 = modDao.loadDynamicResidueModsForSearch(1);
        assertEquals(2, modList1.size());

        List<MsResidueModification> modList2 = modDao.loadDynamicResidueModsForSearch(2);
        assertEquals(2, modList2.size());

        // sort them
        Collections.sort(modList1, new MsSearchModComparator());
        Collections.sort(modList2, new MsSearchModComparator());


        // combine the two lists and sort by id
        List<MsResidueModification> modList = new ArrayList<MsResidueModification>(modList1.size() + modList2.size());
        modList.addAll(modList1);
        modList.addAll(modList2);
        Collections.sort(modList, new MsSearchModComparator());

        // Make sure all fields were saved and read back accurately
        for (int i = 0; i < mods.length; i++) {
            searchId = i % 2  == 0 ? 2 : 1; // even numbers get a search id of 2; odd numbers get 1
            compareDynamicMods(mods[i], modList.get(i));
        }

        // now delete the modifications
        modDao.deleteDynamicResidueModsForSearch(1);
        modList1 = modDao.loadDynamicResidueModsForSearch(1);
        assertEquals(0, modList1.size());

        modDao.deleteDynamicResidueModsForSearch(2);
        modList2 = modDao.loadDynamicResidueModsForSearch(2);
        assertEquals(0, modList2.size());
    }


    public void testOperationsForDynaResModsForSearchResult() {

        // create some dynamic mods 
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        char[] residue = new char[]{'A', 'B', 'X', 'Y'};
        char[] symbol = new char[] {'*', '#', '&', '\u0000'};
        MsResidueModificationIn[] mods = createDynaMods(mass, residue, symbol);
        assertEquals(residue.length, mods.length);


        // save them to the database
        int mod1_1Id = modDao.saveDynamicResidueMod(new MsResidueModificationWrap(mods[0], 1)); // searchId = 1
        int mod1_2Id = modDao.saveDynamicResidueMod(new MsResidueModificationWrap(mods[1], 1));
        int mod2_1Id = modDao.saveDynamicResidueMod(new MsResidueModificationWrap(mods[2], 2)); // searchId = 2
        int mod2_2Id = modDao.saveDynamicResidueMod(new MsResidueModificationWrap(mods[3], 2));

        // save some dynamic modifications for two search results
        MsResultResidueMod rmod1_1 = makeResultDynamicResidueMod(residue[0], mass[0], symbol[0], 10);
        MsResultResidueMod rmod1_2 = makeResultDynamicResidueMod(residue[1], mass[1], symbol[1], 20);
        MsResultResidueMod rmod2_1 = makeResultDynamicResidueMod(residue[2], mass[2], symbol[2], 1);
        MsResultResidueMod rmod2_2 = makeResultDynamicResidueMod(residue[3], mass[3], symbol[3], 2);
        
        modDao.saveDynamicResidueModForResult(3, getDynaResModId(1, mods[0]), rmod1_1.getModifiedPosition()); // resultId, modificationId, modified Position
        modDao.saveDynamicResidueModForResult(new ResultResidueModIds(3, getDynaResModId(1, mods[1]), rmod1_2.getModifiedPosition()));
        modDao.saveDynamicResidueModForResult(4, getDynaResModId(2, mods[2]), rmod2_1.getModifiedPosition());
        modDao.saveDynamicResidueModForResult(new ResultResidueModIds(4, getDynaResModId(2, mods[3]), rmod2_2.getModifiedPosition()));

        // load dynamic modifications for the two search results
        List<MsResultResidueMod> resultMods1 = modDao.loadDynamicResidueModsForResult(3);
        assertEquals(2, resultMods1.size());
        List<MsResultResidueMod> resultMods2 = modDao.loadDynamicResidueModsForResult(4);
        assertEquals(2, resultMods2.size());


        // make sure the values saved and read back are accurate
        // NOTE: sort by position; msDynamicModResult table does not have a id field so we sort by position.
        // make sure to save dynamic modifications for search result in increasing order of position.
        Collections.sort(resultMods1, new MsSearchResultDynamicModComparator());

        compareResultResidueMods(mods[0], resultMods1.get(0));
        compareResultResidueMods(mods[1], resultMods1.get(1));


        Collections.sort(resultMods2, new MsSearchResultDynamicModComparator());
        compareResultResidueMods(mods[2], resultMods2.get(0));
        compareResultResidueMods(mods[3], resultMods2.get(1));


        // delete the search and result modification entries
        modDao.deleteDynamicResidueModsForSearch(1);
        assertEquals(0, modDao.loadDynamicResidueModsForSearch(1).size());
        assertEquals(0, modDao.loadDynamicResidueModsForResult(3).size());
        // the other one should still be there
        assertEquals(2, modDao.loadDynamicResidueModsForSearch(2).size());
        assertEquals(2, modDao.loadDynamicResidueModsForResult(4).size());

        modDao.deleteDynamicResidueModsForSearch(2);
        assertEquals(0, modDao.loadDynamicResidueModsForSearch(2).size());
        assertEquals(0, modDao.loadDynamicResidueModsForResult(4).size());

    }

    
    public void testOperationsForDynaTermModsForSearchResult() {

        // create some dynamic mods 
        String[] mass = new String[] {"123.4", "56.7","987.6","54.3"};
        Terminal[] terminals = new Terminal[]{Terminal.NTERM, Terminal.CTERM, Terminal.NTERM, Terminal.NTERM};
        MsTerminalModificationIn[] mods = createDynaTermMods(mass, terminals);
        assertEquals(mass.length, mods.length);


        // save them to the database
        int mod1_1Id = modDao.saveDynamicTerminalMod(new MsTerminalModificationWrap(mods[0], 1)); // searchId = 1
        int mod1_2Id = modDao.saveDynamicTerminalMod(new MsTerminalModificationWrap(mods[1], 1));
        int mod2_1Id = modDao.saveDynamicTerminalMod(new MsTerminalModificationWrap(mods[2], 2)); // searchId = 2
        int mod2_2Id = modDao.saveDynamicTerminalMod(new MsTerminalModificationWrap(mods[3], 2));

        // save some dynamic modifications for two search results
        modDao.saveDynamicTerminalModForResult(3, getDynaTermModId(1, mods[0])); // mod, resultId, modificationId
        modDao.saveDynamicTerminalModForResult(3, getDynaTermModId(1, mods[1]));
        modDao.saveDynamicTerminalModForResult(4, getDynaTermModId(2, mods[2]));
        modDao.saveDynamicTerminalModForResult(4, getDynaTermModId(2, mods[3]));

        // load dynamic terminal modifications for the two search results
        List<MsResultTerminalMod> resultMods1 = modDao.loadDynamicTerminalModsForResult(3);
        assertEquals(2, resultMods1.size());
        List<MsResultTerminalMod> resultMods2 = modDao.loadDynamicTerminalModsForResult(4);
        assertEquals(2, resultMods2.size());


        // make sure the values saved and read back are accurate
        compareResultTerminalMods(mods[0], resultMods1.get(0));
        compareResultTerminalMods(mods[1], resultMods1.get(1));

        compareResultTerminalMods(mods[2], resultMods2.get(0));
        compareResultTerminalMods(mods[3], resultMods2.get(1));


        // delete the search and result modification entries
        modDao.deleteDynamicTerminalModsForSearch(1);
        assertEquals(0, modDao.loadDynamicTerminalModsForSearch(1).size());
        assertEquals(0, modDao.loadDynamicTerminalModsForResult(3).size());
        // the other one should still be there
        assertEquals(2, modDao.loadDynamicTerminalModsForSearch(2).size());
        assertEquals(2, modDao.loadDynamicTerminalModsForResult(4).size());

        modDao.deleteDynamicTerminalModsForSearch(2);
        assertEquals(0, modDao.loadDynamicTerminalModsForSearch(2).size());
        assertEquals(0, modDao.loadDynamicTerminalModsForResult(4).size());

    }
    
    private int getDynaResModId(int searchId, MsResidueModificationIn mod) {
        return modDao.loadMatchingDynamicResidueModId(new MsResidueModificationWrap(mod, searchId));
    }
    
    private int getDynaTermModId(int searchId, MsTerminalModificationIn mod) {
        return modDao.loadMatchingDynamicTerminalModId(new MsTerminalModificationWrap(mod, searchId));
    }

    private void compareResultResidueMods(MsResidueModificationIn searchMod,
            MsResultResidueMod resultMod) {
        assertEquals(searchMod.getModificationMass().doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(searchMod.getModifiedResidue(), resultMod.getModifiedResidue());
        assertEquals(searchMod.getModificationSymbol(), resultMod.getModificationSymbol());

    }
    
    private void compareResultTerminalMods(MsTerminalModificationIn searchMod,
            MsResultTerminalMod resultMod) {
        assertEquals(searchMod.getModificationMass().doubleValue(), resultMod.getModificationMass().doubleValue());
        assertEquals(searchMod.getModifiedTerminal(), resultMod.getModifiedTerminal());
        assertEquals(searchMod.getModificationSymbol(), resultMod.getModificationSymbol());

    }

    private void compareStaticMods(MsResidueModificationIn input, MsResidueModification output) {
        assertEquals(input.getModifiedResidue(), output.getModifiedResidue());
        assertEquals(input.getModificationMass().doubleValue(), output.getModificationMass().doubleValue());
        assertEquals('\u0000', output.getModificationSymbol());
    }

    private void compareDynamicMods(MsResidueModificationIn input, MsResidueModification output) {
        assertEquals(input.getModifiedResidue(), output.getModifiedResidue());
        assertEquals(input.getModificationMass().doubleValue(), output.getModificationMass().doubleValue());
        assertEquals(input.getModificationSymbol(), output.getModificationSymbol());
    }

    private static final class MsSearchResultDynamicModComparator implements
    Comparator<MsResultResidueMod> {
        public int compare(MsResultResidueMod o1,
                MsResultResidueMod o2) {
            return new Integer(o1.getModifiedPosition()).compareTo(new Integer(o2.getModifiedPosition()));
        }
    }

    private static final class MsSearchModComparator implements Comparator<MsResidueModification> {
        public int compare(MsResidueModification o1, MsResidueModification o2) {
            return new Integer(o1.getId()).compareTo(o2.getId());
        }
    }
}
