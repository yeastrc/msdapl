import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProteinInferInputMaker {

	// private static final Pattern psmPattern = Pattern.compile("^(.+)_(\\d+)$");
	
	public void makeProtInferInput(String psmFile, String peptideProtMapFile, String outputFile, boolean separateIons) throws IOException, Exception {
		
		// 1. Read the ions (charge state and spectrum count) for each peptide
		Map<String, List<Ion>> peptideIonMap = new HashMap<String, List<Ion>>();
		
		System.out.println("Reading ions");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(psmFile));
			// Format: Peptide	charge	PSM_qvalue	filename_scan
			//         LGEHNIDVLEGNEQFINAAK    2       0.0     12Oct2011-PES-fract12-GF5G12-SPE9-YA-01_16080
			String line = null;
			while((line = reader.readLine()) != null) {
				
				if(line.toLowerCase().startsWith("peptide"))
					continue;
				
				String[] tokens = line.split("\\s+");
				
				String peptide = tokens[0];
				int charge = Integer.parseInt(tokens[1]);
					
				List<Ion> ions = peptideIonMap.get(peptide);
				if(ions == null) {
					ions = new ArrayList<Ion>();
					peptideIonMap.put(peptide, ions);
				}

				boolean found = false;
				for(Ion ion: ions) {
					if(ion.charge == charge) {
						ion.spectrumCount++;
						found = true;
						break;
					}
				}
				if(!found) {
					Ion ion = new Ion();
					ion.charge = charge;
					ion.spectrumCount = 1;
					ions.add(ion);
				}
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
		}
		
		
		// 2. Read the file with the peptide and protein matches
		System.out.println("Writing protein inference input");
		BufferedWriter writer = null;
		try {
			
			reader = new BufferedReader(new FileReader(peptideProtMapFile));
			writer = new BufferedWriter(new FileWriter(outputFile));
			
			// Peptide Protein Unique
			// LGEHNIDVLEGNEQFINAAK    gi|136429|sp|P00761|TRYP_PIG:   1
			String line = null;
			while((line = reader.readLine()) != null) {
				
				if(line.toLowerCase().startsWith("peptide"))
					continue;
				
				String[] tokens = line.split("\\s+");
				
				String peptide = tokens[0];
				
				List<Ion> ions = peptideIonMap.get(peptide);
				if(ions == null || ions.size() == 0) {
					continue;
				}
				
				if(separateIons) {
					for(Ion ion: ions) {
						writer.write(peptide+"_"+ion.charge);
						writer.write("\t");
						writer.write(tokens[1]); // Accessions
						//					writer.write("\t");
						//					writer.write(ion.spectrumCount);
						//					writer.write("\t");
						//					writer.write(tokens[1]); // IsUniq
						//					writer.write("\t");
						//					writer.write(tokens[2]); // ProteinId
						writer.newLine();
					}
				}
				else {
					writer.write(peptide);
					writer.write("\t");
					writer.write(tokens[1]); // Accessions
					writer.newLine();
				}
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
			if(writer != null) try {writer.close();} catch(IOException e){}
		}
		
	}
	
	private static class Ion {
		int charge;
		int spectrumCount;
	}
}
