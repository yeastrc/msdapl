/**
 * SequestParamsCreator.java
 * @author Vagisha Sharma
 * Apr 25, 2011
 */
package org.yeastrc.ms.parser.sequestParams;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.SQTParseException;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;
import org.yeastrc.ms.util.AminoAcidUtilsFactory;
import org.yeastrc.ms.util.SequestAminoAcidUtils;

/**
 * Takes as input a directory with .sqt files and produces a sequest.params file
 * based on the headers of the .sqt files
 */
public class SequestParamsCreator {

	
	// These are the parameters we will read
	private String databaseName = null;
	// H	Database	/scratch/yates/SGD_S-cerevisiae_na_12-16-2005_con_reversed.fasta
	
	private String parentMassType = null;
	// H	PrecursorMasses	AVG

	private String fragmentMassType = null;
	//H	FragmentMasses	MONO

	private String peptideMassTolerance = null;
	// H	Alg-PreMassTol	3.000
	
	private String fragmentIonTolerance = null;
	// H	Alg-FragMassTol	0.0
	
	private List<String> staticMods = new ArrayList<String>();
	// H	StaticMod	C=160.139
	// If there is more than 1 static residue modification we should see multiple headers
	
	private List<String> diffMods = new ArrayList<String>();
	// H	DiffMod	STY*=+80.000
	// If there is more than 1 dynamic residue modification we should see multiple headers
	
	private String ionSeries = null;
	// H	Alg-IonSeries	0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0
	
	private String enzyme = null;
	// H	EnzymeSpec	No_Enzyme
	
	private String maxNumDiffAAPerMod = null;
	// H       Alg-MaxDiffMod  4H      Alg-DisplayTop  5
	// No idea what the 'H' means in 4H
	
	private String xcorrMode = null;
	// H       Alg-XCorrMode   0
	
	private String displayTop = null;
	// H       Alg-MaxDiffMod  4H      Alg-DisplayTop  5
	// H       Alg-DisplayTop  5
	// Equivalient to the following in sequest.params
	// num_output_lines = 5                   ; # peptide results to show

		
	public void create(String inputDirectory) throws SQTParseException {
		create(inputDirectory, true);
	}
		
	public void create(String inputDirectory, boolean writeFile) throws SQTParseException {
		
		File dir = new File(inputDirectory);
		File[] sqtFiles = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".sqt");
			}
		});
		
		int idx = 0;
		
		for(File sqtFile: sqtFiles) {
			
			// Dan is doing this check
//			if(!isCurrentSqt(sqtFile.getAbsolutePath()))
//				continue;
//			
//			if(!SearchFileFormat.isSequestFormat(SQTFileReader.getSearchFileType(sqtFile.getAbsolutePath()))) {
//				continue;
//			}
			
			
			SequestSQTFileReader reader = new SequestSQTFileReader();
			try {
				reader.open(sqtFile.getAbsolutePath());
				SQTHeader headerSection = reader.getSearchHeader();
				List<SQTHeaderItem> headers = headerSection.getHeaders();
				
				// reading from the first file
				if(idx == 0) {
					
					for(SQTHeaderItem header: headers) {
						if(header.getName().equals("Database"))
							this.databaseName = header.getValue();
						else if(header.getName().equalsIgnoreCase("PrecursorMasses"))
							this.parentMassType = header.getValue();
						else if(header.getName().equalsIgnoreCase("FragmentMasses"))
							this.fragmentMassType = header.getValue();
						else if(header.getName().equalsIgnoreCase("Alg-PreMassTol"))
							this.peptideMassTolerance = header.getValue();
						else if(header.getName().equalsIgnoreCase("Alg-FragMassTol"))
							this.fragmentIonTolerance = header.getValue();
						else if(header.getName().equalsIgnoreCase("Alg-IonSeries"))
							this.ionSeries = header.getValue();
						else if(header.getName().equalsIgnoreCase("EnzymeSpec"))
							this.enzyme = header.getValue();
						else if(header.getName().equalsIgnoreCase("StaticMod"))
							this.staticMods.add(header.getValue());
						else if(header.getName().equalsIgnoreCase("DiffMod"))
							this.diffMods.add(header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-XCorrMode"))
							this.xcorrMode = header.getValue();
						else if(header.getName().equalsIgnoreCase("Alg-MaxDiffMod")) {
							String[] tokens = header.getValue().split("\\s+");
							this.maxNumDiffAAPerMod = tokens[0];
							if(this.maxNumDiffAAPerMod.endsWith("H"))
								this.maxNumDiffAAPerMod = this.maxNumDiffAAPerMod.substring(0, this.maxNumDiffAAPerMod.length() - 1);
							
							if(tokens.length == 3) {
								if(tokens[1].equals("Alg-DisplayTop")) {
									this.displayTop = tokens[2];
								}
							}
						}
						else if(header.getName().equalsIgnoreCase("Alg-DisplayTop")) {
							this.displayTop = header.getValue();
						}
						
						else {
							if(!header.getName().equals("Comment") && !header.getName().equals("DBSeqLength") &&
							   !header.getName().equals("DBLocusCount") &&
							   !header.getName().equals("SQTGenerator") &&
							   !header.getName().equals("SQTGeneratorVersion") &&
							   !header.getName().equals("StartTime") &&
							   !header.getName().equals("EndTime")
							   )
								throw new RuntimeException("Unrecognized header "+header.getName()+" in file: "+sqtFile.getAbsolutePath());
						}
					}
				}
				// match with what was read from the first file
				else {
					
					List<String> sMods = new ArrayList<String>();
					List<String> dMods = new ArrayList<String>();
					
					for(SQTHeaderItem header: headers) {
						if(header.getName().equals("Database")) 
							match(this.databaseName,header.getValue());
						else if(header.getName().equalsIgnoreCase("PrecursorMasses"))
							match(this.parentMassType, header.getValue());
						else if(header.getName().equalsIgnoreCase("FragmentMasses"))
							match(this.fragmentMassType, header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-PreMassTol"))
							match(this.peptideMassTolerance,header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-FragMassTol"))
							match(this.fragmentIonTolerance,header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-IonSeries"))
							match(this.ionSeries,header.getValue());
						else if(header.getName().equalsIgnoreCase("EnzymeSpec"))
							match(this.enzyme,header.getValue());
						else if(header.getName().equalsIgnoreCase("StaticMod"))
							sMods.add(header.getValue());
						else if(header.getName().equalsIgnoreCase("DiffMod"))
							dMods.add(header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-XCorrMode"))
							match(this.xcorrMode, header.getValue());
						else if(header.getName().equalsIgnoreCase("Alg-MaxDiffMod")) {
							String[] tokens = header.getValue().split("\\s+");
							String tmp = tokens[0];
							if(tmp.endsWith("H"))
								tmp = tmp.substring(0, tmp.length() - 1);
							match(this.maxNumDiffAAPerMod, tmp);
							
							if(tokens.length == 3) {
								if(tokens[1].equals("Alg-DisplayTop")) {
									match(this.displayTop, tokens[2]);
								}
							}
						}
						else if(header.getName().equalsIgnoreCase("Alg-DisplayTop")) {
							match(this.displayTop, header.getValue());
						}
						else {
							if(!header.getName().equals("Comment") && !header.getName().equals("DBSeqLength") &&
							   !header.getName().equals("DBLocusCount") &&
							   !header.getName().equals("SQTGenerator") &&
							   !header.getName().equals("SQTGeneratorVersion") &&
							   !header.getName().equals("StartTime") &&
							   !header.getName().equals("EndTime")
							   )
								throw new RuntimeException("Unrecognized header "+header.getName()+" in file: "+sqtFile.getAbsolutePath());
						}
					}
					
					match(this.staticMods, sMods);
					match(this.diffMods, dMods);
				}
				idx++;
				
			} catch (DataProviderException e) {
				throw new SQTParseException("Error parsing file: "+sqtFile, e);
			}
			finally {
				reader.close();
			}
		}
		
		if(writeFile) {
			writeParamsFile(inputDirectory+File.separator+"sequest.params");
//			File mydir = new File(inputDirectory);
//			String runIdDir = mydir.getName();
//			String projectDir = mydir.getParentFile().getName();
//			writeParamsFile("/Users/silmaril/Desktop/Old_YRC_Conversion/SEQUEST_PARAMS/"+projectDir+"_"+runIdDir+"_"+"sequest.params");
		}
	}
	
	private void match(String s1, String s2) throws SQTParseException {
		
		boolean match;
		if(s1 == null)	    match = (s2 == null);
		else if(s2 == null)	match = (s1 == null);
		else				match = (s1.equals(s2));
		
		if(!match)
			throw new SQTParseException("File headers do not match: "+s1+" and "+s2);
	}
	
	private void match(List<String> list1, List<String> list2) throws SQTParseException {
		
		if(list1.size() != list2.size())
			throw new SQTParseException("Modification headers do not match");
		
		Collections.sort(list1);
		Collections.sort(list2);
		for(int i = 0; i < list1.size(); i++) {
			if( !list1.get(i).equals(list2.get(i)) )
				throw new SQTParseException("Modification headers do not match");
		}
	}
	
	
	private void writeParamsFile(String filePath) throws SQTParseException {
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(filePath));
			
			// write a dummy parameter so that we know this file was generated from the SQT headers
			writer.write("FILE_GENERATOR = SequestParamsCreator  ; File was generated from the SQT headers in the experiment");
			writer.write("\n\n");
			
			writer.write("[SEQUEST]\n");
			
			// write the database name
			if(this.databaseName == null || this.databaseName.trim().length() == 0) {
				throw new SQTParseException("Database name cannot be empty");
			}
			writer.write("database_name = "+this.databaseName);
			writer.write("\n\n");
			
			// write the parent mass type
			// eg. mass_type_parent = 0                   ; 0=average masses, 1=monoisotopic masses
			if(this.parentMassType == null || this.parentMassType.trim().length() == 0) {
				throw new SQTParseException("Could not get parent mass type");
			}
			writer.write("mass_type_parent = ");
			if(parentMassType.equalsIgnoreCase("AVG"))
				writer.write("0");
			else if(parentMassType.equalsIgnoreCase("MONO"))
				writer.write("1");
			else
				throw new SQTParseException("unknown parent mass type: "+parentMassType);

			writer.write("\t; 0=average masses, 1=monoisotopic masses");
			writer.newLine();
			
			
			// write the fragment mass type
			// e.g. mass_type_fragment = 1                 ; 0=average masses, 1=monoisotopic masses
			if(this.fragmentMassType == null || fragmentMassType.trim().length() == 0) {
				throw new SQTParseException("Could not get fragment mass type");
			}
			writer.write("mass_type_fragment = ");
			if(fragmentMassType.equalsIgnoreCase("AVG"))
				writer.write("0");
			else if(fragmentMassType.equalsIgnoreCase("MONO"))
				writer.write("1");
			else
				throw new SQTParseException("unknown fragment mass type: "+fragmentMassType);

			writer.write("\t; 0=average masses, 1=monoisotopic masses");
			writer.newLine();
			
			
			// write the parent mass tolerance
			if(this.peptideMassTolerance != null) {
				// e.g. peptide_mass_tolerance = 3.000
				writer.write("peptide_mass_tolerance = "+peptideMassTolerance);
				writer.newLine();
			}
			
			// write the fragment mass tolerance
			if(this.fragmentIonTolerance != null) {
				// e.g. fragment_ion_tolerance = 0.0
				writer.write("fragment_ion_tolerance = "+fragmentIonTolerance);
				writer.newLine();
			}
			
			
			// write num_output_lines, if available
			if(this.displayTop != null) {
				writer.write("num_output_lines = "+this.displayTop+"\t; # peptide results to show");
				writer.newLine();
			}
			
			// write max_num_differential_AA_per_mod = 0    ; max # of modified AA per diff. mod in a peptide
			if(this.maxNumDiffAAPerMod != null) {
				writer.write("max_num_differential_AA_per_mod = "+
						this.maxNumDiffAAPerMod+"\t; max # of modified AA per diff. mod in a peptide");
				writer.newLine();
			}
			
			// From Jimmy's email:
			// The Yates lab version had options to modify the xcorr 
			// such as normalizing the xcorr value by the autocorrelation (of input spectrum against itself).
			// EE, ET, and TT must by the correlation value of experimental spectrum against experimental spectrum, 
			// experimental vs theoretical, and theoretical vs theoretical.
			if(this.xcorrMode != null) {
				writer.write("\nxcorr_mode = "+xcorrMode.trim()+"     ; 0=regular xcorr (default), 1=EE, 2=ET, 3=TT\n");
				writer.newLine();
			}
			
			
			// write the ion series
			if(this.ionSeries != null) {
				// e.g. ion_series = 0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0
				writer.write("ion_series = "+this.ionSeries);
				writer.newLine();
			}
			
			
			writer.newLine();
			if(this.enzyme == null || this.enzyme.trim().length() == 0) {
				throw new SQTParseException("No enzyme information found");
			}
			// write the enzyme number
			writeEnzymNumber(writer);
			
			
			// write the print_duplicate_references param
			// This is a hack. We don't really know if this was set to 1 in the original Sequest run
			// But we need this to be set to 1 to get the data uploaded.
			writer.write("\nprint_duplicate_references = 1         ; 0=no, 1=yes\n");
			
			
			
			writer.newLine();
			// write the difff mods, if any
			writeDiffMods(writer);
			
			writer.newLine();
			// write the static mods, if any
			writeStaticMods(writer);
			
			writer.newLine();
			// write the enzyme information
			writeEnzymes(writer);
			
		}
		catch(IOException e) {
			throw new SQTParseException("Error writing file: "+filePath, e);
		}
		finally {
			if(writer != null) try {writer.close();} catch(IOException e){}
		}
	}

	private void writeStaticMods(BufferedWriter writer) throws SQTParseException, IOException {
		
		// Static mods from the SQT file header should look like this:
		// C=160.139
		
		// key = modified residue (e.g. C, K, R);  value = mass of the modification
		Map<String, String> modificationMap = new HashMap<String, String>();
		
		for(String modString: this.staticMods) {
			String[] tokens = modString.split("=");
			
	        if (tokens.length < 2)
	            throw new SQTParseException("Invalid static modification string: "+modString);
	        if (tokens.length > 2)
	            throw new SQTParseException("Invalid static modification string (appears to have > 1 static modification): "+modString);
		
		
	        // convert modification chars to upper case 
	        String modChars = tokens[0].trim().toUpperCase();
	        String modMass = tokens[1].trim();
	        
	        try {
	            Double.parseDouble(modMass);
	        }
	        catch(NumberFormatException e) {
	            throw new SQTParseException("Error parsing static modification mass: "+modMass);
	        }
	        
	        // this modification may be for multiple residues; 
	        // add one for each residue character
	        for (int i = 0; i < modChars.length(); i++) {
	        	modificationMap.put(String.valueOf(modChars.charAt(i)), modMass);
	        }
		}
		
		/*
		Example: 
		add_C_terminus = 0.0000                ; added to C-terminus (peptide mass & all Y"-ions)
		add_N_terminus = 0.0000                ; added to N-terminus (B-ions)
		add_G_Glycine = 0.0000                 ; added to G - avg.  57.0519, mono.  57.02146
		add_A_Alanine = 0.0000                 ; added to A - avg.  71.0788, mono.  71.03711
		add_S_Serine = 0.0000                  ; added to S - avg.  87.0782, mono.  87.02303
		add_P_Proline = 0.0000                 ; added to P - avg.  97.1167, mono.  97.05276
		add_V_Valine = 0.0000                  ; added to V - avg.  99.1326, mono.  99.06841
		add_T_Threonine = 0.0000               ; added to T - avg. 101.1051, mono. 101.04768
		add_C_Cysteine = 57.000                ; added to C - avg. 103.1388, mono. 103.00919
		add_L_Leucine = 0.0000                 ; added to L - avg. 113.1594, mono. 113.08406
		add_I_Isoleucine = 0.0000              ; added to I - avg. 113.1594, mono. 113.08406
		add_X_LorI = 0.0000                    ; added to X - avg. 113.1594, mono. 113.08406
		add_N_Asparagine = 0.0000              ; added to N - avg. 114.1038, mono. 114.04293
		add_O_Ornithine = 0.0000               ; added to O - avg. 114.1472, mono  114.07931
		add_B_avg_NandD = 0.0000               ; added to B - avg. 114.5962, mono. 114.53494
		add_D_Aspartic_Acid = 0.0000           ; added to D - avg. 115.0886, mono. 115.02694
		add_Q_Glutamine = 0.0000               ; added to Q - avg. 128.1307, mono. 128.05858
		add_K_Lysine = 0.0000                  ; added to K - avg. 128.1741, mono. 128.09496
		add_Z_avg_QandE = 0.0000               ; added to Z - avg. 128.6231, mono. 128.55059
		add_E_Glutamic_Acid = 0.0000           ; added to E - avg. 129.1155, mono. 129.04259
		add_M_Methionine = 0.0000              ; added to M - avg. 131.1926, mono. 131.04049
		add_H_Histidine = 0.0000               ; added to H - avg. 137.1411, mono. 137.05891
		add_F_Phenyalanine = 0.0000            ; added to F - avg. 147.1766, mono. 147.06841
		add_R_Arginine = 0.0000                ; added to R - avg. 156.1875, mono. 156.10111
		add_Y_Tyrosine = 0.0000                ; added to Y - avg. 163.1760, mono. 163.06333
		add_W_Tryptophan = 0.0000              ; added to W - avg. 186.2132, mono. 186.07931
				 
				 */
		
		writer.write("add_C_terminus = 0.0000                ; added to C-terminus (peptide mass & all Y\"-ions)\n");
		writer.write("add_N_terminus = 0.0000                ; added to N-terminus (B-ions)\n");
		
		SequestAminoAcidUtils aaUtils = AminoAcidUtilsFactory.getSequestAminoAcidUtils();
		char[] aminoAcids = aaUtils.getAminoAcidChars();
		for(char aa: aminoAcids) {
			
			StringBuilder buf = new StringBuilder();
			
			buf.append("add_"+aa+"_"+aaUtils.getFullName(aa)+" = ");
			String mod = modificationMap.get(String.valueOf(aa));
			if(mod == null)	buf.append("0.0000");
			else {
				double massDiff = Double.parseDouble(mod) - aaUtils.avgMass(aa);  // static mod in SQT headers is mass of amino acid + modification mass
				if(massDiff <= 0) {
					throw new RuntimeException("Calculated static modification mass is negative!!");
				}
				buf.append(String.format("%.4f", massDiff));
			}
			
			int length = buf.length();
			for (int i = length; i < 39; i++) {
				buf.append(" ");
			}
			writer.write(buf.toString());
			writer.write("; added to "+aa+" - avg. "+aaUtils.avgMass(aa)+", mono. "+aaUtils.monoMass(aa));
			writer.newLine();
		}
	}

	private void writeDiffMods(BufferedWriter writer) throws SQTParseException, IOException {

		// Diff mods string from the SQT file header should look like this
		// STY*=+80.000
	    // Multiple dynamic modifications should be present on separate DiffMod lines in a SQT file
		
		String asteriskMods = null; // modifications with symbol *
		String atMods = null; // modifications with symbol @
		String hashMods = null; // modifications with symbol #
		
		
		for(String modString: diffMods) {
			
			String[] tokens = modString.split("=");
			
			if (tokens.length < 2)
				throw new SQTParseException("Invalid dynamic modification string: "+modString);
			if (tokens.length > 2)
				throw new SQTParseException("Invalid dynamic modification string (appears to have > 1 dynamic modification): "+modString);

			String modChars = tokens[0].trim();
			// get the modification symbol (this character should follow the modification residue characters)
			// example S* -- S is the modified residue; * is the modification symbol
			if (modChars.length() < 2)
				throw new SQTParseException("No modification symbol found: "+modString);
			char modSymbol = modChars.charAt(modChars.length() - 1);
			if (!isValidDynamicModificationSymbol(modSymbol))
				throw new SQTParseException("Invalid modification symbol: "+(modChars.charAt(modChars.length() - 1)));

			// remove the modification symbol and convert modification chars to upper case 
			modChars = modChars.substring(0, modChars.length()-1).toUpperCase();
			if (modChars.length() < 1)
				throw new SQTParseException("No residues found for dynamic modification: "+modString);
			

			String modMass = tokens[1].trim();
			if (removeSign(modMass).length() < 1)
				throw new SQTParseException("No mass found for dynamic modification: "+modString);


			try { Double.parseDouble(modMass);}
			catch(NumberFormatException e) {
				throw new SQTParseException("Error parsing modification mass: "+modMass);
			}

			if(modSymbol == '*')
				asteriskMods = Double.parseDouble(modMass)+" "+modChars;
			else if(modSymbol == '@')
				atMods = Double.parseDouble(modMass)+" "+modChars;
			else if(modSymbol == '#')
				hashMods = Double.parseDouble(modMass)+" "+modChars;
		}
		
		writer.write("diff_search_options = ");
		if(asteriskMods != null)
			writer.write(asteriskMods);
		else
			writer.write("0.0 X");
		if(hashMods != null)
			writer.write(" "+hashMods);
		else
			writer.write(" 0.0 X");
		if(atMods != null)
			writer.write(" "+atMods);
		else
			writer.write(" 0.0 X");
		
		writer.newLine();
	}
	
	private boolean isValidDynamicModificationSymbol(char modSymbol) {
		return modSymbol == '*' || modSymbol == '@' || modSymbol == '#';
    }
	
	private String removeSign(String massStr) throws SQTParseException {
        if (massStr.length() == 0)  return massStr;
        if (massStr.charAt(0) == '+' || massStr.charAt(0) == '-')
            return massStr.substring(1);
        else
        	throw new SQTParseException("Did not find a + or - with the dynamic modification mass");
        //return massStr;
    }

	private void writeEnzymNumber(BufferedWriter writer) throws SQTParseException, IOException {
		
		// TODO these are the common enzymes that I know of.  Should I add others?
		// Most SQT files will have "No_Enzyme" or "Trypsin" as the enzyme
		// If the input SQT files have an enzyme other than the ones below we will
		// have to add it to the list.
		writer.write(("enzyme_number = "));
		if(this.enzyme.equalsIgnoreCase("No_Enzyme"))
			writer.write("0\n");
		else if(this.enzyme.equalsIgnoreCase("Trypsin"))
			writer.write("1\n");
		else if(this.enzyme.equalsIgnoreCase("Chymotrypsin"))
			writer.write("2\n");
		else if(this.enzyme.equalsIgnoreCase("Trypsin_K"))
			writer.write("8\n");
		else if(this.enzyme.equalsIgnoreCase("Trypsin_R"))
			writer.write("9\n");
		else if(this.enzyme.equalsIgnoreCase("Elastase"))
			writer.write("12\n");
		else if(this.enzyme.equalsIgnoreCase("Elastase/Tryp/Chymo"))
			writer.write("13\n");
		else
			throw new SQTParseException("Unrecognized enzyme: "+this.enzyme);
		
		// If we are using an enzyme we need the enzymatic termini information.  
		// I don't know what the SQT header for that looks like. 
		if(!this.enzyme.equalsIgnoreCase("No_Enzyme")) {
			
			if(this.enzyme.equalsIgnoreCase("Trypsin")) {
				writer.write("num_enzyme_termini = 1\n");  // Assume semi-tryptic search
			}
			else {
				throw new SQTParseException("Don't know how to get enzymatic termini information for the enzyme: "+this.enzyme);
			}
		}
	}

	private void writeEnzymes(BufferedWriter writer) throws IOException {
		
		/*
		 
		 [SEQUEST_ENZYME_INFO]
0.  No_Enzyme              0      -           -
1.  Trypsin                1      KR          P
2.  Chymotrypsin           1      FWY         P
3.  Clostripain            1      R           -
4.  Cyanogen_Bromide       1      M           -
5.  IodosoBenzoate         1      W           -
6.  Proline_Endopept       1      P           -
7.  Staph_Protease         1      E           -
8.  Trypsin_K              1      K           P
9.  Trypsin_R              1      R           P
10. AspN                   0      D           -
11. Cymotryp/Modified      1      FWYL        P
12. Elastase               1      ALIV        P
13. Elastase/Tryp/Chymo    1      ALIVKRWFY   P


		 */
		
		writer.write("[SEQUEST_ENZYME_INFO]\n");
		writer.write("0.  No_Enzyme              0      -           -\n");
		writer.write("1.  Trypsin                1      KR          P\n");
		writer.write("2.  Chymotrypsin           1      FWY         P\n");
		writer.write("3.  Clostripain            1      R           -\n");
		writer.write("4.  Cyanogen_Bromide       1      M           -\n");
		writer.write("5.  IodosoBenzoate         1      W           -\n");
		writer.write("6.  Proline_Endopept       1      P           -\n");
		writer.write("7.  Staph_Protease         1      E           -\n");
		writer.write("8.  Trypsin_K              1      K           P\n");
		writer.write("9.  Trypsin_R              1      R           P\n");
		writer.write("10. AspN                   0      D           -\n");
		writer.write("11. Cymotryp/Modified      1      FWYL        P\n");
		writer.write("12. Elastase               1      ALIV        P\n");
		writer.write("13. Elastase/Tryp/Chymo    1      ALIVKRWFY   P\n");
		
	}

	public static void main(String[] args) throws SQTParseException {
		String inputDir = args[0];
		SequestParamsCreator spc = new SequestParamsCreator();
		spc.create(inputDir);
		
		
		//String inputDir = "/Users/silmaril/WORK/UW/JOB_QUEUE/jq_w_mslib_r722_fix/data_dir/parc/test";
		//String inputDir = "./test_resources/EE-normalized_SEQUEST/1217/1129";
		
//		String baseDir = "/Volumes/FreeAgentDrive/Extracted_From_Prod_2011_05_02";
//		File[] files = new File(baseDir).listFiles();
//		for(File file: files) {
//			if(!file.isDirectory())
//				continue;
//			
//			File[] runIdDirs = file.listFiles();
//			for(File runIdDir: runIdDirs) {
//				if(!runIdDir.isDirectory())
//					continue;
//				
//				//System.out.println("Reading: "+runIdDir.getAbsolutePath());
//				SequestParamsCreator spc = new SequestParamsCreator();
//				try {
//					spc.create(runIdDir.getAbsolutePath(), true);
//				}
//				catch(SQTParseException e) {
//					System.out.println("\tError: "+e.getMessage());
//					continue;
//				}
//			}
//		}
	}
	
//	private boolean isCurrentSqt(String filePath) {
//    	
//		BufferedReader reader = null;
//		try {
//			
//			reader = new BufferedReader(new FileReader(filePath));
//			String line = null;
//			while((line = reader.readLine()) != null) {
//				if(line.startsWith("H")) {
//					return true;
//				}
//				else
//					return false;
//			}
//			
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		finally {
//			if(reader != null) try {reader.close();} catch(IOException e){}
//		}
//		return false;
//    }
}
