/**
 * SequestParamsParser.java
 * @author Vagisha Sharma
 * Aug 22, 2008
 * @version 1.0
 */
package org.yeastrc.ms.parser.sequestParams;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.MsEnzyme.Sense;
import org.yeastrc.ms.domain.general.impl.Enzyme;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.MsTerminalModification.Terminal;
import org.yeastrc.ms.domain.search.impl.ParamBean;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.SearchDatabase;
import org.yeastrc.ms.domain.search.impl.TerminalModification;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SearchParamsDataProvider;


/**
 * 
 */
public class SequestParamsParser implements SearchParamsDataProvider {

    private String remoteServer;

    private List<Param> paramList;

    private boolean reportEvalue = false;
    private boolean printDuplicateReferences = false;
    private MsSearchDatabaseIn database;
    private String enzymeCode;
    private MsEnzymeIn enzyme;
    private List<MsResidueModificationIn> staticResidueModifications;
    private List<MsTerminalModificationIn> staticTerminalModifications;
    private List<MsResidueModificationIn> dynamicResidueModifications;
    private List<MsTerminalModificationIn> dynamicTerminalModifications;

    private int currentLineNum = 0;
    private String currentLine;

    static final Pattern paramLinePattern = Pattern.compile("^([\\S&&[^=]]+)\\s*=\\s*([^;]*)\\s*;?(.*)");
    static final Pattern staticTermModPattern = Pattern.compile("^add\\_([N|C])term\\_peptide$");
    static final Pattern staticTermModPattern_old = Pattern.compile("^add\\_([N|C])_terminus$");
    static final Pattern dynamicTermModPattern = Pattern.compile("^variable\\_([N|C])\\_terminus");
    static final Pattern staticResidueModPattern = Pattern.compile("^add\\_([A-Z])\\_[\\w]+");
    static final Pattern enzymePattern = Pattern.compile("^(\\d+)\\.\\s+(\\S+)\\s+([0|1])\\s+([[\\-]|[A-Z]]+)\\s+([[\\-]|[A-Z]]+)$");
    protected static final Pattern modCharsPattern = Pattern.compile("[A-Z]+");


    protected static final char[] modSymbols = {'*', '#', '@', '^', '~', '$'};

    
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
    
    // NOTE: sequest.params does not tell us if the algorithm used was sequest
    // or ee-normalized sequest.  That information is available in the sqt files
    // This method returns sequest as the SearchProgram.
    @Override
    public Program getSearchProgram() {
        return Program.SEQUEST;
    }
    
    public String paramsFileName() {
        return "sequest.params";
    }
    
    public boolean reportEvalue() {
        return reportEvalue;
    }
    
    public boolean printDuplicateReferences() {
        return printDuplicateReferences;
    }

    public List<Param> getParamList() {
        return this.paramList;
    }
    
    public boolean isEnzymeUsedForSearch() {
        if (enzyme == null || enzyme.getName().equalsIgnoreCase("No_Enzyme"))
            return false;
        else return true;
    }
    
    public SequestParamsParser() {
        paramList = new ArrayList<Param>();
        staticResidueModifications = new ArrayList<MsResidueModificationIn>();
        staticTerminalModifications = new ArrayList<MsTerminalModificationIn>();
        dynamicResidueModifications = new ArrayList<MsResidueModificationIn>();
        dynamicTerminalModifications = new ArrayList<MsTerminalModificationIn>();
    }

    private void init(String remoteServer) {
        this.remoteServer = remoteServer;
        paramList.clear();
        staticResidueModifications.clear();
        staticTerminalModifications.clear();
        dynamicResidueModifications.clear();
        dynamicTerminalModifications.clear();
    }
    
    public void parseParams(String remoteServer, String paramsFileDir) throws DataProviderException {
        
        init(remoteServer);
        
        String paramsFile = paramsFileDir+File.separator+paramsFileName();
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(paramsFile));
            while ((currentLine = reader.readLine())!= null) {
                currentLineNum++;
                currentLine = currentLine.trim();

                // ignore comment lines
                if (currentLine.startsWith("#")) // comment line
                    continue;
                
                // match a param = value pair, if we can
                Param param = matchParamValuePair(currentLine);
                if (param != null) {
                    addParam(param);
                }
                // look for the sequest enzyme info section so that we can get the details for the enzyme
                // used for the search.
                else if (currentLine.startsWith(getEnzymeInfoHeader())){
                    parseEnzymes(reader);
                }
            }
        }
        catch (FileNotFoundException e) {
            throw new DataProviderException("Cannot find file: "+paramsFileDir, e);
        }
        catch (IOException e) {
            throw new DataProviderException("Error reading file: "+paramsFileDir, e);
        }
        finally{
            if (reader != null) try {
                reader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (database == null)
        	throw new DataProviderException("No database information found in file: "+paramsFile);
        if (enzyme == null)
        	throw new DataProviderException("No enzyme information found in file: "+paramsFile);
            
    }
    
    protected String getEnzymeInfoHeader()
    {
    	return "[SEQUEST_ENZYME_INFO]";
    }

    void parseEnzymes(BufferedReader reader) throws IOException {
        while ((currentLine = reader.readLine())!= null) {
            currentLineNum++;
            Matcher m = enzymePattern.matcher(currentLine);
            // if we don't get a match it means we are no longer looking at enzymes. 
            if (!m.matches())
                break;
            MsEnzymeIn enz = matchEnzyme(m, getSearchEnzymeCode());
            if (enz != null) {
                this.enzyme = enz;
                break;
            }
        }
    }
    
    protected String getSearchEnzymeCode()
    {
    	return this.enzymeCode;
    }
    
    MsEnzymeIn matchEnzyme(Matcher m, String enzymeCode) {
        String enzCode = m.group(1);
        if (!enzCode.equals(enzymeCode))
            return null;
        final String enzName = m.group(2);
        final String sense = m.group(3);
        final String cut = m.group(4);
        final String noCut = m.group(5);
        Enzyme enz = new Enzyme();
        enz.setCut(cut);
        enz.setName(enzName);
        enz.setNocut(noCut);
        enz.setSense(Sense.instance(Short.parseShort(sense)));
        return enz;
    }

	private void addParam(Param param) throws DataProviderException {
    	
        paramList.add(param);
        // e-value
        if (param.getParamName().equalsIgnoreCase("print_expect_score")) {
            if (param.getParamValue().equals("1"))
                reportEvalue = true;
        }
        // database
        else if (param.getParamName().equalsIgnoreCase("database_name")) {
            SearchDatabase db = new SearchDatabase();
            db.setServerAddress(remoteServer);
            db.setServerPath(param.getParamValue());
            database = db;
        }
        
        else if (parsedStaticTerminalModParam(param.getParamName(), param.getParamValue())) return;
        else if (parsedStaticResidueModParam(param.getParamName(), param.getParamValue()))  return;
        else if (parsedDynamicTerminalModParam(param.getParamName(), param.getParamValue()))  return;
        
        else
        	addCustomParam(param);
    }
    
    protected void addCustomParam(Param param) throws DataProviderException 
    {
    	// enzyme number (actual enzyme information will be parsed later in the file
    	if (param.getParamName().equalsIgnoreCase("enzyme_number")) {
            enzymeCode = param.getParamValue();
        }
        else if (param.getParamName().equalsIgnoreCase("diff_search_options")) {
            parseDynamicResidueMods(param.getParamValue());
        }
    	// print_duplicate_references
        else if (param.getParamName().equalsIgnoreCase("print_duplicate_references")) {
            if (param.getParamValue().equals("1"))
                printDuplicateReferences = true;
        }
    }

    boolean parsedStaticTerminalModParam(String paramName, String paramValue) throws DataProviderException {
        Matcher m = staticTermModPattern.matcher(paramName);
        if (!m.matches()) {
        	m = staticTermModPattern_old.matcher(paramName);
        	if(!m.matches())
        		return false;
        }

        Terminal term = Terminal.instance(m.group(1).charAt(0));

        BigDecimal modMass = null;
        try {modMass = new BigDecimal(paramValue);}
        catch(NumberFormatException e) {throw new DataProviderException("Error parsing modification mass: "+paramValue);}

        if (modMass.doubleValue() != 0.0) {
            TerminalModification mod = new TerminalModification();
            mod.setModificationMass(modMass);
            mod.setModifiedTerminal(term);
            staticTerminalModifications.add(mod);
        }
        return true;
    }
    
    boolean parsedDynamicTerminalModParam(String paramName, String paramValue) throws DataProviderException {
        Matcher m = dynamicTermModPattern.matcher(paramName);
        if (!m.matches())
            return false;

        Terminal term = Terminal.instance(m.group(1).charAt(0));

        BigDecimal modMass = null;
        try {modMass = new BigDecimal(paramValue);}
        catch(NumberFormatException e) {throw new DataProviderException("Error parsing modification mass: "+paramValue);}

        if (modMass.doubleValue() != 0.0) {
            TerminalModification mod = new TerminalModification();
            mod.setModificationMass(modMass);
            mod.setModifiedTerminal(term);
            if(term == Terminal.NTERM)
            	mod.setModificationSymbol(MsTerminalModificationIn.NTERM_MOD_CHAR_SEQUEST);
            else if(term == Terminal.CTERM)
            	mod.setModificationSymbol(MsTerminalModificationIn.CTERM_MOD_CHAR_SEQUEST);
            dynamicTerminalModifications.add(mod);
        }
        return true;
    }
    
    boolean parsedStaticResidueModParam (String paramName, String paramValue) throws DataProviderException {
        Matcher m = staticResidueModPattern.matcher(paramName);
        if (!m.matches())
            return false;

        if(staticTermModPattern_old.matcher(paramName).matches())
        	return false;
        
        char modResidue = m.group(1).charAt(0);
        BigDecimal modMass = null;
        try {modMass = new BigDecimal(paramValue);}
        catch(NumberFormatException e) {throw new DataProviderException("Error parsing modification mass: "+paramValue);}

        if (modMass.doubleValue() != 0.0) {
            ResidueModification mod = new ResidueModification();
            mod.setModificationMass(modMass);
            mod.setModifiedResidue(modResidue);
            staticResidueModifications.add(mod);
        }
        return true;
    }

    void parseDynamicResidueMods(String modString) throws DataProviderException {
        
        dynamicResidueModifications.clear();
        // e.g. diff_search_options = 0.0000 S 9.0 C 16.0 M 0.0000 X 0.0000 T 0.0000 Y
        // modString is: 0.0000 S 9.0 C 16.0 M 0.0000 X 0.0000 T 0.0000 Y
        // SEQUEST assigns modification symbols in the following order:
        // '*', '#', '@', '^', '~', '$'
        final String[] tokens = modString.split("\\s+");

        // expect an even number of tokens.
        if (tokens.length % 2 != 0) {
            throw new DataProviderException(currentLineNum, "Error parsing dynamic residue modification string", currentLine);
        }

        int modCharIdx = -1;
        for (int i = 0; i < tokens.length; i+=2) {
            
            modCharIdx++;
            
            BigDecimal mass = null;
            try {mass = new BigDecimal((tokens[i]));}
            catch(NumberFormatException e) {throw new DataProviderException(currentLineNum, "Error parsing modification mass: "+tokens[i], currentLine, e);}

            // don't consider modifications with mass-shift of 0;
            if (mass.doubleValue() == 0.0) continue; 

            // modified residues(s) can only be upper-case characters
            String modChars = tokens[i+1];
            if (!modCharsPattern.matcher(modChars).matches())
                throw new DataProviderException(currentLineNum, "Invalid char(s) for modified residue: "+tokens[i+1], currentLine);

            // if we have already used up all the modifications characters throw an exception.
            // We should have had only 6 dynamic residue modifications
            if (modCharIdx == modSymbols.length)
                throw new DataProviderException(currentLineNum, "Only "+modSymbols.length+" modifications are supported", currentLine);
            
            
            for (int j = 0; j < modChars.length(); j++) {
                ResidueModification mod = new ResidueModification();
                mod.setModificationMass(mass);
                mod.setModifiedResidue(modChars.charAt(j));
                mod.setModificationSymbol(modSymbols[modCharIdx]);
                addDynamicResidueModification(mod);
            }
//            modCharIdx++;
        }
    }
    
    protected void addDynamicResidueModification(ResidueModification mod)
    {
    	dynamicResidueModifications.add(mod);
    }

    // parameter_name = parameter_value ; parameter_description
    // e.g. create_output_files = 1                ; 0=no, 1=yes
    // e.g. database_name = /net/maccoss/vol2/software/pipeline/dbase/mouse-contam.fasta
    Param matchParamValuePair(String line) {
        Matcher m = getParamLinePattern().matcher(line);
        if (m.matches()) {
            final String paramName = m.group(1).trim();
            final String paramVal = m.group(2).trim();
            ParamBean param = new ParamBean(paramName, paramVal);
            return param;
        }
        else {
            return null;
        }
    }
    
    protected Pattern getParamLinePattern()
    {
    	return paramLinePattern;
    }
    
    protected String getCurrentLine()
    {
    	return this.currentLine;
    }
    
    protected int getCurrentLineNumber()
    {
    	return this.currentLineNum;
    }

    /**
     * @param args
     * @throws DataProviderException 
     */
    public static void main(String[] args) throws DataProviderException {
        //String paramFile = "resources/sequest.params";
        String paramsDir = "resources";
        SequestParamsParser parser = new SequestParamsParser();
        parser.parseParams("remote.server", paramsDir);
    }
}
