package org.yeastrc.ms.parser.prolucidParams;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.general.impl.Enzyme;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.SearchDatabase;
import org.yeastrc.ms.domain.search.impl.TerminalModification;
import org.yeastrc.ms.domain.search.prolucid.ProlucidParamIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SearchParamsDataProvider;

public class ProlucidParamsParser implements SearchParamsDataProvider {

    public static enum Score {SP, BIN_PROB, XCORR, DELTA_CN, ZSCORE, BLANK};

    private String remoteServer;

    private List<ProlucidParamIn> parentParams; // normally we should have only one parent (the <parameter> element)

    private MsSearchDatabaseIn database;
    private MsEnzymeIn enzyme;
    private List<MsResidueModificationIn> staticResidueModifications;
    private List<MsTerminalModificationIn> staticTerminalModifications;
    private List<MsResidueModificationIn> dynamicResidueModifications;
    private List<MsTerminalModificationIn> dynamicTerminalModifications;

    private boolean foundPrimaryScoreType = false;
    private boolean foundSecondaryScoreType = false;
    private boolean foundAdditionalEstimate = false;
    private boolean foundconfidenceType = false;
    
    public List<ProlucidParamIn> getParentParamElement() {
        return parentParams;
    }

    public MsSearchDatabaseIn getSearchDatabase() {
        return database;
    }

    public MsEnzymeIn getSearchEnzyme() {
        return enzyme;
    }

    public List<MsResidueModificationIn> getDynamicResidueMods() {
        return dynamicResidueModifications;
    }

    public List<MsResidueModificationIn> getStaticResidueMods() {
        return staticResidueModifications;
    }

    public List<MsTerminalModificationIn> getStaticTerminalMods() {
        return staticTerminalModifications;
    }

    public List<MsTerminalModificationIn> getDynamicTerminalMods() {
        return dynamicTerminalModifications;
    }
    
    public Program getSearchProgram() {
        return Program.PROLUCID;
    }
    
    @Override
    public String paramsFileName() {
        return "search.xml";
    }
    
    public List<ProlucidParamIn> getParamList() {
        return this.parentParams;
    }
    
    public boolean isEnzymeUsedForSearch() {
        if (enzyme == null || enzyme.getName().equalsIgnoreCase("No_Enzyme"))
            return false;
        return true;
    }
    
    private void init(String remoteServer) {
        this.remoteServer = remoteServer;
        parentParams.clear();
        staticResidueModifications.clear();
        staticTerminalModifications.clear();
        dynamicResidueModifications.clear();
        dynamicTerminalModifications.clear();
        this.database = null;
        this.enzyme = null;
    }

    public ProlucidParamsParser() {
        parentParams = new ArrayList<ProlucidParamIn>();
        staticResidueModifications = new ArrayList<MsResidueModificationIn>();
        staticTerminalModifications = new ArrayList<MsTerminalModificationIn>();
        dynamicResidueModifications = new ArrayList<MsResidueModificationIn>();
        dynamicTerminalModifications = new ArrayList<MsTerminalModificationIn>();
    }
    
    public void parseParams(String remoteServer, String paramsFileDir) throws DataProviderException {

        init(remoteServer);

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setIgnoringComments(true);
        DocumentBuilder b = null;
        Document doc = null;
        try {
            b = f.newDocumentBuilder();
            doc = b.parse(paramsFileDir+File.separator+paramsFileName());
            parseDocument(doc);
        }
        catch (ParserConfigurationException e) {
            throw new DataProviderException("Error reading file: ", e);
        }
        catch (SAXException e) {
            throw new DataProviderException("Error reading file: ", e);
        }
        catch (IOException e) {
            throw new DataProviderException("Error reading file: ", e);
        }
        
        // make sure we found a database
        if (this.database == null)
            throw new DataProviderException("No database element found in ProLuCID's search.xml");
        
        // make sure we got our score types
        if ((foundPrimaryScoreType && foundSecondaryScoreType) ||
            (foundAdditionalEstimate && foundconfidenceType))
            return;
        else
            throw new DataProviderException("Could not determine primary and secondary score types from ProLuCID's search.xml");
    }

    private void parseDocument(Document doc) throws DataProviderException {
        NodeList nodes = doc.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i) == null)  continue;
            ProlucidParamNode pn = parseNode(nodes.item(i), null);
            if (pn != null)
                this.parentParams.add(pn);
        }
        //printParams();
    }

    private void printParams() {
        for (ProlucidParamIn n: this.parentParams) {
            printParam(n, 0);
        }
    }

    private void printParam(ProlucidParamIn param, int indent) {
        String tab = "";
        for (int i = 0; i < indent; i++) {
            tab += "\t";
        }
        System.out.print(tab+"<"+param.getParamElementName()+">");
        if (param.getParamElementValue() != null)
            System.out.print(param.getParamElementValue());
        System.out.println("");

        List<ProlucidParamIn> childNodes = param.getChildParamElements();
        for (ProlucidParamIn child: childNodes) {
            printParam(child, indent+1);
        }

        System.out.println(tab+"</"+param.getParamElementName()+">");
    }

    private ProlucidParamNode parseNode(Node node, ProlucidParamNode parent) throws DataProviderException {

        if (node.getNodeType() != Node.ELEMENT_NODE)
            return null;

        ProlucidParamNode thisNode = new ProlucidParamNode();
        thisNode.elName = node.getNodeName();

        NodeList nodes = node.getChildNodes();
        String nodeVal = "";
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeType() == Node.TEXT_NODE) {
                nodeVal += n.getNodeValue().trim();
            }
        }
        if (nodeVal.length() > 0)
            thisNode.elValue = nodeVal;

        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n == null)  continue;
            ProlucidParamNode childNode = parseNode(n, thisNode);
            if (childNode != null)
                thisNode.addChildParamElement(childNode);
        }
        extractRequiredInfo(thisNode);
        return thisNode;
    }

    private void extractRequiredInfo(ProlucidParamNode node) throws DataProviderException {
        if (node.getParamElementName().equalsIgnoreCase("database"))
            parseDatabaseInfo(node);
        else if (node.getParamElementName().equalsIgnoreCase("enzyme_info"))
            parseEnzymeInfo(node);
        else if (node.getParamElementName().equalsIgnoreCase("modifications"))
            parseModificationInfo(node);
        else if (node.getParamElementName().equalsIgnoreCase("primary_score_type"))
            foundPrimaryScoreType = true;
        else if (node.getParamElementName().equalsIgnoreCase("secondary_score_type"))
            foundSecondaryScoreType = true;
        else if (node.getParamElementName().equalsIgnoreCase("additional_estimate"))
            foundAdditionalEstimate = true;
        else if (node.getParamElementName().equalsIgnoreCase("confidence"))
            foundconfidenceType = true;
    }

    private void parseDatabaseInfo(ProlucidParamIn node) throws DataProviderException {
        String dbPath = null;
        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equals("database_name")) {
                // there should only be one database.
                if (dbPath != null)
                    throw new DataProviderException("Cannot handle more than one search databases");
                dbPath = child.getParamElementValue();
            }
        }
        if (dbPath == null) {
            throw new DataProviderException("No search database found");
        }
        SearchDatabase db = new SearchDatabase();
        db.setServerAddress(remoteServer);
        db.setServerPath(dbPath);
        this.database = db;
    }

    private void parseEnzymeInfo(ProlucidParamIn node) throws DataProviderException {
        String name = null;
        Sense sense = null;
        String cut = "";
        for (ProlucidParamIn child: node.getChildParamElements()) {
            // enzyme name
            if (child.getParamElementName().equalsIgnoreCase("name"))
                name = child.getParamElementValue();
            // residues at which this enzyme cuts
            else if (child.getParamElementName().equalsIgnoreCase("residues")) {
                for (ProlucidParamIn cr: child.getChildParamElements()) {
                    if (cr.getParamElementName().equals("residue"))
                        cut+= cr.getParamElementValue();
                }
            }
            // where does the enzyme cut: C-Term or N-Term
            else if (child.getParamElementName().equalsIgnoreCase("type")) {
                if (child.getParamElementValue().equalsIgnoreCase("true"))
                    sense = Sense.CTERM;
                else
                    sense = Sense.NTERM;
            }
        }
        if (name == null || sense == null || cut.length() == 0) {
            throw new DataProviderException("Invalid enzyme information. One or more required values (name, type, residue) are missing");
        }
        Enzyme e = new Enzyme();
        e.setName(name);
        e.setCut(cut);
        e.setSense(sense);
        this.enzyme = e;
    }

    // parse <modifications> element
    private void parseModificationInfo(ProlucidParamIn node) throws DataProviderException {
        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("n_term"))
                parseNtermMod(child);
            else if (child.getParamElementName().equalsIgnoreCase("c_term"))
                parseCtermMod(child);
            else if (child.getParamElementName().equalsIgnoreCase("static_mods"))
                parseStaticResidueMods(child);
            else if (child.getParamElementName().equalsIgnoreCase("diff_mods"))
                parseDynamicResidueMods(child);
        }
    }

    // parse <n_term> element
    private void parseNtermMod(ProlucidParamIn node) throws DataProviderException {

        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equals("static_mod")) {
                parseNtermModFormat2(node);
                return;
            }
        }
        parseNtermModFormat1(node);
    }

    /**
     * FORMAT 2 Example: 
     * <n_term>
     *      <static_mod>
     *          <symbol>*</symbol>
     *          <mass_shift>0</mass_shift>
     *      </static_mod>
     *      <diff_mods>
     *          <diff_mod>
     *              <symbol>*</symbol>
     *              <mass_shift>0</mass_shift>
     *          </diff_mod>
     *      </diff_mods>
     * </n_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseNtermModFormat2(ProlucidParamIn node) throws DataProviderException {

        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("static_mod")) {
                parseStaticTermModFormat2(Terminal.NTERM, child);
            }
            else if (child.getParamElementName().equalsIgnoreCase("diff_mods")) {
                for (ProlucidParamIn c: child.getChildParamElements()) {
                    if (c.getParamElementName().equalsIgnoreCase("diff_mod")) {
                        parseDynamicTermModFormat2(Terminal.NTERM, c);
                    }
                }
            }
        }
    }

    private void parseStaticTermModFormat2(Terminal term, ProlucidParamIn node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for terminal modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for terminal modification: "+child.getParamElementValue(), e);
                }
            }
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for terminal modification");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification();
        mod.setModificationMass(mass);
        mod.setModificationSymbol(symbol);
        mod.setModifiedTerminal(term);
        this.staticTerminalModifications.add(mod);
    }

    private void parseDynamicTermModFormat2(Terminal term, ProlucidParamIn node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for terminal modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for terminal modification: "+child.getParamElementValue(), e);
                }
            }
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for terminal modification");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification();
        mod.setModificationMass(mass);
        mod.setModificationSymbol(symbol);
        mod.setModifiedTerminal(term);
        this.dynamicTerminalModifications.add(mod);
    }

    /**
     * Example: 
     * <n_term>
     *      <symbol>*</symbol>
     *      <mass_shift>156.1011</mass_shift>
     *      <is_static_mod>false</is_static_mod>
     * </n_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseNtermModFormat1(ProlucidParamIn node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        Boolean isStatic = null;

        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for n_term modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for n_term modification: "+child.getParamElementValue(), e);
                }
            }
            else if (child.getParamElementName().equalsIgnoreCase("is_static_mod"))
                isStatic = Boolean.valueOf(child.getParamElementValue());
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for n_term modification");
        if (isStatic == null)
            throw new DataProviderException("Missing information if n-term modification is static or terminal");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification();
        mod.setModificationMass(mass);
        mod.setModificationSymbol(symbol);
        mod.setModifiedTerminal(Terminal.NTERM);
        if (isStatic)
            this.staticTerminalModifications.add(mod);
        else
            this.dynamicTerminalModifications.add(mod);
    }

    // parse <c_term> element
    private void parseCtermMod(ProlucidParamIn node) throws DataProviderException {

        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equals("static_mod")) {
                parseCtermModFormat2(node);
                return;
            }
        }
        parseCtermModFormat1(node);
    }

    /**
     * Example: 
     * <c_term>
     *      <static_mod>
     *          <symbol>*</symbol>
     *          <mass_shift>0</mass_shift>
     *      </static_mod>
     *      <diff_mods>
     *          <diff_mod>
     *              <symbol>*</symbol>
     *              <mass_shift>0</mass_shift>
     *          </diff_mod>
     *      </diff_mods>
     * </c_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseCtermModFormat2(ProlucidParamIn node) throws DataProviderException {

        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("static_mod")) {
                parseStaticTermModFormat2(Terminal.CTERM, child);
            }
            else if (child.getParamElementName().equalsIgnoreCase("diff_mods")) {
                for (ProlucidParamIn c: child.getChildParamElements()) {
                    if (c.getParamElementName().equalsIgnoreCase("diff_mod")) {
                        parseDynamicTermModFormat2(Terminal.CTERM, c);
                    }
                }
            }
        }
    }

    /**
     * Example: 
     * <c_term>
     *      <symbol>*</symbol>
     *      <mass_shift>156.1011</mass_shift>
     *      <is_static_mod>false</is_static_mod>
     * </c_term>
     * @param node
     * @throws DataProviderException
     */
    private void parseCtermModFormat1(ProlucidParamIn node) throws DataProviderException {
        char symbol = 0;
        BigDecimal mass = null;
        Boolean isStatic = null;

        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid modification symbol for c_term modification: "+s); 
                symbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")){
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for c_term modification: "+child.getParamElementValue(), e);
                }
            }
            else if (child.getParamElementName().equalsIgnoreCase("is_static_mod")) {
                String s = child.getParamElementValue();
                if (!s.equalsIgnoreCase("true") && !s.equalsIgnoreCase("false"))
                    throw new DataProviderException("Invalid value for is_static_mod element");
                isStatic = Boolean.valueOf(child.getParamElementValue());
            }
        }

        if (mass == null)
            throw new DataProviderException("No mass_shift found for c_term modification");
        if (isStatic == null)
            throw new DataProviderException("Missing information if c-term modification is static or terminal");

        // if mass shift is 0, ignore this modification
        if (mass.doubleValue() == 0)
            return;

        TerminalModification mod = new TerminalModification();
        mod.setModificationMass(mass);
        mod.setModificationSymbol(symbol);
        mod.setModifiedTerminal(Terminal.CTERM);
        if (isStatic)
            this.staticTerminalModifications.add(mod);
        else
            this.dynamicTerminalModifications.add(mod);
    }

    // parse <static_mods> element
    private void parseStaticResidueMods(ProlucidParamIn node) throws DataProviderException {
        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("static_mod"))
                parseStaticResidueMod(child);
        }
    }

    // parse <static_mod> element
    private void parseStaticResidueMod(ProlucidParamIn node) throws DataProviderException {
        BigDecimal mass = null;
        char residue = 0;
        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("residue")) {
                if (residue != 0)
                    throw new DataProviderException("Error parsing static residue modification. More than one residue found for static modification");
                String s = child.getParamElementValue();
                if (s == null || s.length() != 1)
                    throw new DataProviderException("Invalid residue for static modification: "+s); 
                residue = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for static residue modification: "+child.getParamElementValue(), e);
                }
                // if mass shift is 0, ignore this modification
                if (mass.doubleValue() == 0)
                    return;
            }
        }

        ResidueModification mod = new ResidueModification();
        mod.setModificationMass(mass);
        mod.setModifiedResidue(residue);
        this.staticResidueModifications.add(mod);
    }

    // parse <diff_mods> element
    private void parseDynamicResidueMods(ProlucidParamIn node) throws DataProviderException {
        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("diff_mod"))
                parseDynamicMod(child);
        }
    }

    // parse <diff_mod> element
    private void parseDynamicMod(ProlucidParamIn node) throws DataProviderException {
        char modSymbol = 0;
        BigDecimal mass = null;
        List<Character> modResidues = new ArrayList<Character>();

        for (ProlucidParamIn child: node.getChildParamElements()) {
            if (child.getParamElementName().equalsIgnoreCase("mass_shift")) {
                try {mass = new BigDecimal(child.getParamElementValue());}
                catch(NumberFormatException e) {
                    throw new DataProviderException("Invalid mass_shift for static residue modification: "+child.getParamElementValue(), e);
                }
                // if mass shift is 0, ignore this modification
                if (mass.doubleValue() == 0)
                    return;
            }
            else if (child.getParamElementName().equalsIgnoreCase("symbol")) {
                String s = child.getParamElementValue();
                // TODO  <symbol> can have a string value. e.g. "phosporylation"
//              if (s == null || s.length() != 1)
                if (s == null || s.length() < 1)
                    throw new DataProviderException("Invalid modification symbol for dynamic residue modification: "+s); 
                modSymbol = s.charAt(0);
            }
            else if (child.getParamElementName().equalsIgnoreCase("residues")) {
                for (ProlucidParamIn cr: child.getChildParamElements()) {
                    if (cr.getParamElementName().equals("residue")) {
                        String s = cr.getParamElementValue();
                        if (s == null || s.length() != 1)
                            throw new DataProviderException("Invalid residue for dynamic residue modification: "+s); 
                        modResidues.add(s.charAt(0));
                    }
                }
            }
        }
        for (Character res: modResidues) {
            ResidueModification mod = new ResidueModification();
            mod.setModificationMass(mass);
            mod.setModifiedResidue(res);
            mod.setModificationSymbol(modSymbol);
            dynamicResidueModifications.add(mod);
        }
    }

    private static final class ProlucidParamNode implements ProlucidParamIn {

        private String elName;
        private String elValue;
        private List<ProlucidParamIn> childElList = new ArrayList<ProlucidParamIn>();

        @Override
        public String getParamElementName() {
            return elName;
        }
        @Override
        public String getParamElementValue() {
            return elValue;
        }
        @Override
        public List<ProlucidParamIn> getChildParamElements() {
            return childElList;
        }

        public void addChildParamElement(ProlucidParamIn param) {
            childElList.add(param);
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append("Name: "+elName);
            buf.append("\n");
            buf.append("Value: "+elValue);
            return buf.toString();
        }
    }


    public static void main(String[] args) throws DataProviderException {
//        String file = "resources/prolucid_search_format1.xml";
        String dir = "resources/prolucid_params_format1";
        ProlucidParamsParser parser = new ProlucidParamsParser();
        parser.parseParams("remote.server", dir);
    }
}
