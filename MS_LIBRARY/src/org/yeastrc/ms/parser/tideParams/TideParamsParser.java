/**
 * TideParamParser.java
 * @author Vagisha Sharma
 * Jan 15, 2011
 */
package org.yeastrc.ms.parser.tideParams;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.general.MsEnzymeIn;
import org.yeastrc.ms.domain.general.impl.Enzyme;
import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.MsSearchDatabaseIn;
import org.yeastrc.ms.domain.search.MsTerminalModificationIn;
import org.yeastrc.ms.domain.search.Param;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.domain.search.impl.ParamBean;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.impl.SearchDatabase;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.domain.search.sqtfile.SQTRunSearchIn;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.SearchParamsDataProvider;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;

/**
 * 
 */
public class TideParamsParser implements SearchParamsDataProvider {

	private String remoteServer;
	
	private List<Param> paramList;

    
    private MsSearchDatabaseIn database;
    private MsEnzymeIn enzyme;
    private boolean printAllProteinMatches = false;
    private List<MsResidueModificationIn> staticResidueModifications;
    private List<MsResidueModificationIn> dynamicResidueModifications;

    public TideParamsParser() {
        paramList = new ArrayList<Param>();
        staticResidueModifications = new ArrayList<MsResidueModificationIn>();
        dynamicResidueModifications = new ArrayList<MsResidueModificationIn>();
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
    	return new ArrayList<MsTerminalModificationIn>(0);
    }

    public List<MsTerminalModificationIn> getDynamicTerminalMods() {
        return new ArrayList<MsTerminalModificationIn>(0);
    }
    
    @Override
    public Program getSearchProgram() {
        return Program.TIDE;
    }
    
    public String paramsFileName() {
        throw new UnsupportedOperationException("Tide parameters are read from SQT file headers");
    }
    
    @Override
	public void parseParams(String remoteServer, String paramFileDir) throws DataProviderException {
		
    	this.remoteServer = remoteServer;
    	paramList = getSearchParams(paramFileDir);
    	parseParams(paramList);
		
	}

    private List<Param> getSearchParams(String paramFileDir) throws DataProviderException {

    	Map<String, String> headerMap = new HashMap<String, String>();

    	List<String> sqtFiles = getSqtFiles(new File(paramFileDir));
    	for(String fileName: sqtFiles) {

    		String filePath = paramFileDir+File.separator+fileName;

    		SequestSQTFileReader provider = new SequestSQTFileReader();

    		provider.open(filePath);
    		SQTRunSearchIn search = provider.getSearchHeader();

    		// These are the headers we are interested in
    		// H    StaticMod       C=57.0215
    		// H	CommandLineIndex	--digestion=full-digest........
    		// H	CommandLineSearch	--proteins=.......
    		// H	CommandLineResults	--results_file
    		// H 	MassWindow 3
    		// H 	TopMatches 5
    		// H 	ShowAllProteins 0
    		// H 	ShowMods 0
    		// H 	MinMass 600
    		// H 	MaxMass 1000
    		// H 	MinLength 6
    		// H 	MaxLength 50
    		// H 	Enzyme trypsin
    		// H 	FullDigestion 1
    		// H 	MissedCleavages 0
    		// H 	MonoisotopicPrecursor 0
    		for(SQTHeaderItem header: search.getHeaders()) {

    			// We are no longer interested in these headers for getting the Tide parameters
        		// Each parameter is now present in its own header line
        		if(header.getName().equalsIgnoreCase("CommandLineSearch") ||
        		   header.getName().equalsIgnoreCase("CommandLineResults") ||
        		   header.getName().equalsIgnoreCase("SQTGenerator")) {
        			continue;
        		}

        		// We want to make sure that all SQT files in an experiment directory have the 
        		// same header values
        		String oldValue = headerMap.get(header.getName());
        		if(oldValue != null) {
        			if(!(oldValue.equalsIgnoreCase(header.getValue()))) {
        				throw new DataProviderException("!!!SQT header mismatch. Old value: "+oldValue+"; New value: "+header.getValue());
        			}
        		}
        		else
        			headerMap.put(header.getName(), header.getValue());
    		}
    		provider.close(); // close the file
    	}

    	List<Param> params = new ArrayList<Param>();
    	for(String paramName: headerMap.keySet()) {
    		
    		// We only need to get the fasta file name from this header
    		if(paramName.equalsIgnoreCase("CommandLineIndex")) {
    			String cmdLine = headerMap.get(paramName);
    			String[] opts = cmdLine.split("\\s+");
    			for(String opt: opts) {
    				String[] optTokens = opt.split("=");
    				if(optTokens.length == 2) {
    					String name = optTokens[0];
    					name = name.replaceFirst("-*", "");
    					if(name.equalsIgnoreCase("fasta")) {
    						ParamBean param = new ParamBean(name, optTokens[1]);
    						params.add(param);
    					}
    				}
    			}
    		}
    		else {
    			ParamBean param = new ParamBean(paramName, headerMap.get(paramName));
    			params.add(param);
    		}
    	}
    	
    	return params;
    }
    
    private List<String> getSqtFiles(File dir) {

    	List<String> mySqtFiles = new ArrayList<String>();

    	File[] files = dir.listFiles(new FilenameFilter() {
    		@Override
    		public boolean accept(File dir, String name) {
    			String name_lc = name.toLowerCase();
    			return (name_lc.endsWith(".sqt") && !(name_lc.endsWith("reverse.sqt")));
    		}});
    	for (int i = 0; i < files.length; i++) {
    		mySqtFiles.add(files[i].getName());
    	}
    	return mySqtFiles;
    }
    
    public boolean printAllProteinMatches() {
        return printAllProteinMatches;
    }

    public List<Param> getParamList() {
        return this.paramList;
    }
    
    public boolean isEnzymeUsedForSearch() {
        return enzyme != null;
    }
    
    private void parseParams(List<Param> params) throws DataProviderException {
    	
    	for(Param param: params) {
    		
    		
    		// fasta file used for search
    		if(param.getParamName().equalsIgnoreCase("fasta")) {
    			SearchDatabase db = new SearchDatabase();
                db.setServerAddress(remoteServer);
                db.setServerPath(param.getParamValue());
                database = db;
    		}
    		
    		// enzyme
            else if (param.getParamName().equalsIgnoreCase("Enzyme")) {
            	if(!param.getParamValue().equalsIgnoreCase("none")) {
            		Enzyme enz = new Enzyme();
            		enz.setName(param.getParamValue());
            		this.enzyme = enz;
            	}
            }
    		
    		// static modifications
    		if(param.getParamName().equalsIgnoreCase("StaticMod")) {
    			getStaticResidueMods(param);
    		}
    		
    		// dynamic modifications
    		if(param.getParamName().equalsIgnoreCase("DiffMod")) {
    			getDynamicResidueMods(param);
    		}
    		
    		// are all protein matches for a peptide printed
    		if(param.getParamName().equalsIgnoreCase("ShowAllProteins")) {
    			int val = 0;
    			try {
    				val = Integer.parseInt(param.getParamValue());
    				this.printAllProteinMatches = Boolean.parseBoolean(param.getParamValue());
    			}
    			catch (NumberFormatException e) {
    				throw new DataProviderException("Error parsing ShowAllProteins parameter", e);
    			}
    			this.printAllProteinMatches = val == 1 ? true : false;
    		}
    	}
    }


	private void getStaticResidueMods(Param param) throws DataProviderException {
		
		// StaticMod       C=57.0215
		List<ResidueModification> modifications = parseModifications(param);
		staticResidueModifications.addAll(modifications);
	}
	
	private void getDynamicResidueMods(Param param) throws DataProviderException {
		
		// DiffMod SYT=+80.000
		List<ResidueModification> modifications = parseModifications(param);
		dynamicResidueModifications.addAll(modifications);
	}
	
	private List<ResidueModification> parseModifications(Param param) throws DataProviderException {
		
		if(param.getParamValue() == null || param.getParamValue().trim().length() == 0)
			return new ArrayList<ResidueModification>(0);
		
		List<ResidueModification> modifications = new ArrayList<ResidueModification>();
		// Example: StaticMod       C=57.0215
		// paramName = StaticMod; paramValue = C=57.0215
		// OR
		// DiffMod SYT=+80.000
		// paramName = DiffMod; paramValue = SYT=+80.000
		String[] mods = param.getParamValue().split(",");
		for(String modStr: mods) {
			int idx = modStr.indexOf("=");
			
			if(idx == -1) {
				throw new DataProviderException("Cannot parse modification string: "+modStr);
			}
			
			BigDecimal modMass = null;
	        try {modMass = new BigDecimal(modStr.substring(idx+1));}
	        catch(NumberFormatException e) {throw new DataProviderException("Error parsing modification mass: "+modStr);}
			
	        if(modMass.doubleValue() == 0.0)
	        	continue;
	        
			for(int j = 0; j < idx; j++) {
				char modResidue = modStr.charAt(j);
				
				ResidueModification mod = new ResidueModification();
				mod.setModificationMass(modMass);
				mod.setModifiedResidue(modResidue);
				modifications.add(mod);
			}
	           
		}
		return modifications;
	}
	
	
	public static void main(String[] args) throws DataProviderException {
		String dirPath = "/Users/silmaril/WORK/UW/MSDaPl_data/YRC_PEPTIDE_ATLAS/1174";
		TideParamsParser parser = new TideParamsParser();
		parser.parseParams("remote", dirPath);
		
		List<Param> params = parser.getParamList();
		for(Param param: params) {
			System.out.println(param.getParamName()+"\t"+param.getParamValue());
		}
		System.out.println(parser.getSearchDatabase().getDatabaseFileName());
		System.out.println(parser.getSearchEnzyme());
		System.out.println(parser.getSearchProgram());
		System.out.println("Enzyme used: "+parser.isEnzymeUsedForSearch());
		System.out.println("HAS all Protein matches: "+parser.printAllProteinMatches());
		
		for(MsResidueModificationIn mod: parser.getStaticResidueMods())
			System.out.println("STATIC "+mod.getModifiedResidue()+"; "+mod.getModificationMass());
		
		for(MsResidueModificationIn mod: parser.getDynamicResidueMods())
			System.out.println("DYNAMIC "+mod.getModifiedResidue()+"; "+mod.getModificationMass());
	}
	
	/* FLAGS used by the program - index
	-fasta (Input FASTA file) type: string default: ""
	-peptides (File of peptides to create) type: string default: ""
	-proteins (File of raw proteins to create, as raw_proteins.proto)
				type: string default: ""
	-digestion (Digestion completeness. May be full-digest or partial-digest)
				type: string default: "full-digest"
	-enzyme (Digestion enzyme. May be none, trypsin, chymotrypsin, elastase,
				clostripain, cyanogen-bromide, idosobenzoate, proline-endopeptidase,
				staph-protease, modified-chymotrypsin, elastase-trypsin-chymotrypsin,
				aspn.) type: string default: "none"
	-max_length (Peptide length inclusion threshold) type: int32 default: 50
	-max_mass (Peptide mass inclusion threshold) type: double default: 7200
	-min_length (Peptide length inclusion threshold) type: int32 default: 6
	-min_mass (Peptide mass inclusion threshold) type: double default: 200
	-missed_cleavages (Allow missed cleavages in enzymatic digestion)
				type: bool default: false
	-mods_spec (Expression for static modifications to include. Specify a
				comma-separated list of the form --mods=C+57.0,...) type: string
				default: ""
	-mods_table (Modification specification filename. May be given instead of
	--mods_spec.) type: string default: ""
	-monoisotopic_precursor (Use monoisotopic precursor masses rather than
				average masses for residues.) type: bool default: false
	 */
	
	/* FLAGS used by the program - search
	-mass_window (Precursor mass tolerance in Daltons) type: double default: 3
	-peptides (File of unfragmented peptides, as peptides.proto) type: string
				default: ""
	-proteins (File of proteins corresponding to peptides, as
				raw_proteins.proto) type: string default: ""
	-results (Results format. Can be text or protobuf.) type: string
				default: "text"
	-results_file (Results output file) type: string default: "results.tideres"
	-spectra (Spectrum input file) type: string default: ""
	-top_matches (Number of matches to report for each spectrum) type: int32
				default: 5
	 */
	
	/* FLAGS used by program - results
	-aux_locations (File of auxiliary locations corresponding to peptides in
				the results) type: string default: ""
	-match_fields (A comma delimited set of fields to show for a matching
				peptide in the order listed. Available options are: xcorr,sequence)
				type: string default: "xcorr,sequence"
	-out_filename (Name of the output file to generate. An extension will be
				added based on the out_format.) type: string default: ""
	-out_format (The output format to be generated. Can be text, pep.xml or
				sqt. Default is text.) type: string default: "text"
	-protein_fields (A comma delimited set of fields to show for a protein
				associated with a matching peptide. Available options are:
				protein_name,pos, aa_before,aa_after) type: string
				default: "protein_name,pos,aa_before,aa_after"
	-proteins (File of proteins corresponding to peptides, as
				raw_proteins.proto) type: string default: ""
	-results_file (Results file generated via Search, as results.proto)
				type: string default: ""
	-show_all_proteins (Display all the proteins the peptide was found in.)
				type: bool default: false
	-show_mods (Display modifications in the peptide sequence.) type: bool
				default: false
	-spectra (Spectrum input file) type: string default: ""
	-spectrum_fields (A comma delimited set of fields to show for an
				experimental spectrum in the order listed. Available options are:
				spectrum_num,mz,charge) type: string default: "spectrum_num,mz,charge"
	 */
    
}
