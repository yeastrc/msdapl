/**
 * OldMS2Converter.java
 * @author Vagisha Sharma
 * Jan 26, 2011
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 */
public class OldMS2Converter {

	private static final DecimalFormat format = new DecimalFormat("#0.0000");
	
	private static final OldMS2Converter instance = new OldMS2Converter();
	
	private OldMS2Converter () {}
	
	public static OldMS2Converter getInstance() {
		return instance;
	}
	
	public void convert (String inputFilePath, String outputFilePath, boolean forPeptipedia) throws IOException {
		
		
		if(!inputFilePath.endsWith(".ms2")) {
			System.out.println("Not a MS2 file: "+inputFilePath);
			return;
		}
		
		
		if(isValidMs2(inputFilePath)) {
			System.out.println("File is already in a valid MS2 format: "+inputFilePath+". Skipping...");
			return;
		}
		
		System.out.println("Converting file: "+inputFilePath);
		BufferedReader reader = null;
		BufferedWriter writer = null;
		
		try {
			reader = new BufferedReader(new FileReader(inputFilePath));
			writer = new BufferedWriter(new FileWriter(outputFilePath));
			
			// write the header
			writer.write("H\tCreationDate\t"+(new Date())+"\n");
			writer.write("H\tComments\tConverted from old MS2 format\n");
			
			String line = null;
			double mz = -1.0;
			
			Set<Integer> scanNumbers = new HashSet<Integer>();
			
			int scanNum = 1;
			while((line = reader.readLine()) != null) {
				
				if(line.startsWith(":")) { // this line has the scan number and charge
					// Example:
					// :0002.0002.2
					// 1894.72 2
					
					line = line.substring(1); // remove the ":"
					String[] tokens1 = line.trim().split("\\.");
					String scanNumS = tokens1[0];
					String scanNumE = tokens1[1];
					String chgline1 = tokens1[2]; // charge string from the first line
					
					if(!scanNumS.equals(scanNumE)) {
						System.out.println("Start scan number not the same as the end scan number: "+line);
						return;
					}
					
					// read the next line
					String line2 = reader.readLine();
					String[] tokens2 = line2.split("\\s+");
					String mplusH = tokens2[0];
					String chgline2 = tokens2[1];
					
					if(!(chgline1.equals(chgline2))) {
						System.out.println("Charge not the same");
						System.out.println("\t"+line);
						System.out.println("\t"+line2);
						return;
					}
					
					int chg = Integer.parseInt(chgline2);
					if(mz == -1.0) {
						// m/z = ( neutralMass + (charge * MASS_PROTON) ) / charge;
						mz = MzMplusHConverter.toMz(Double.parseDouble(mplusH), chg);
						
						// Scan numbers can be repeated in the old ms2 files
						// This is a problem.  For the Peptipedia project, since the data
						// is being re-searched with Tide, I renumbered the scans.
						// For the old YRC data conversion project we need to keep the original scan numbers.
						// However, duplicate scan numbers will cause errors while uploading search results 
						// so we will throw an error
						if(forPeptipedia) {
							writer.write("S\t"+scanNum+"\t"+scanNum+"\t"+format.format(mz)+"\n");
							writer.write("I\tOriginalScan\t"+scanNumS+"-"+scanNumE+"\n");
							scanNum++;
						}
						else {
							Integer snum = Integer.parseInt(scanNumS);
							if(scanNumbers.contains(snum)) {
								throw new RuntimeException("Duplicate scan number found: "+scanNumS);
							}
							scanNumbers.add(snum);
							writer.write("S\t"+scanNumS+"\t"+scanNumE+"\t"+format.format(mz)+"\n");
							
						}
					}
					
					// In some old files the MH+ values for the two charge states do not correspond to the same 
					// m/z value.  For the Peptipedia project the MH+ value for the first charge state is 
					// used to calculate the m/z reported in the 'S' line. And, the MH+ for the second charge
					// state (if present) is re-calculated based on this m/z value.
					// We will not do this for the YEC data conversion project since the MH+ in the 'Z' lines 
					// will have to match the 'observedMass' in the sqt files. 
					if(forPeptipedia && mz != -1.0) {
						mplusH = getMplusH(mz, chg);
					}
					
					writer.write("Z\t"+chg+"\t"+mplusH+"\n");
					
				}
				else { // this is the peak line (m/z and intensity)
					mz = -1.0;
					writer.write(line+"\n");
				}
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e){}
			if(writer != null) try {writer.close();} catch(IOException e) {}
		}
		
		System.out.println("\tConverted file: "+outputFilePath);
	}

	private boolean isValidMs2(String inputFilePath) throws IOException {
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(inputFilePath));
			String line = reader.readLine();
			if(line.startsWith(":"))
				return false;
			else if(line.startsWith("H"))
				return true;
			else {
				throw new RuntimeException("Cannot recognize file: "+inputFilePath+". First line is: "+line);
			}
		}
		finally {
			if(reader != null) try {reader.close();} catch(IOException e) {}
		}
	}
	
	private static String getMplusH(double mz, int charge){
		double mph = MzMplusHConverter.toMplusH(mz, charge);
		return format.format(mph);
	}
	
	public static void main(String[] args) {
		
		OldMS2Converter converter = new OldMS2Converter();
		String file = "./resources/old_ms2/cotesmmsrapa-01.ms2";
		try {
			converter.convert(file, "./resources/old_ms2/cotesmmsrapa-01.converted.ms2",
					/* Use false for the YRC old data conversion project */ false);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
