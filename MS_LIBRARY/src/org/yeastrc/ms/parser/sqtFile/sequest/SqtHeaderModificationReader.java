/**
 * 
 */
package org.yeastrc.ms.parser.sqtFile.sequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.ms.domain.search.MsResidueModificationIn;
import org.yeastrc.ms.domain.search.impl.ResidueModification;
import org.yeastrc.ms.domain.search.sqtfile.SQTHeaderItem;
import org.yeastrc.ms.parser.DataProviderException;
import org.yeastrc.ms.parser.sqtFile.SQTHeader;
import org.yeastrc.ms.parser.sqtFile.sequest.SequestSQTFileReader;

/**
 * SqtHeaderModificationReader.java
 * @author Vagisha Sharma
 * Aug 4, 2011
 * 
 */
public class SqtHeaderModificationReader {

	private List<MsResidueModificationIn> dynamicResidueMods;
	private List<MsResidueModificationIn> staticResidueMods;
	
	
	public List<MsResidueModificationIn> getDynamicResidueMods() {
		return dynamicResidueMods;
	}

	public List<MsResidueModificationIn> getStaticResidueMods() {
		return staticResidueMods;
	}

	public void readSqtModifications(String sqtFilePath) throws DataProviderException {
		
		SequestSQTFileReader sqtReader = new SequestSQTFileReader();
		
		try {
        	
        	sqtReader.open(sqtFilePath);
        	SQTHeader sqtHeaders = sqtReader.getSearchHeader();
        	
        	// read modifications from the header
        	readModificationsFromSqtHeader(sqtHeaders);
		}
        finally {
        	sqtReader.close(); // close the file handle
        }
	}
	
	private void readModificationsFromSqtHeader(SQTHeader sqtHeaders) throws DataProviderException {
		
		this.dynamicResidueMods = new ArrayList<MsResidueModificationIn>();
		this.staticResidueMods = new ArrayList<MsResidueModificationIn>();
		
		for(SQTHeaderItem headerItem: sqtHeaders.getHeaders()) {
			
			if(headerItem.getName().equals("StaticMod")) {
				
				readStaticMod(headerItem);
				
			}
			
			else if(headerItem.getName().equals("DiffMod")) {
				
				readDynamicMod(headerItem);
				
			}
		}
	}
	
	private void readDynamicMod(SQTHeaderItem headerItem) throws DataProviderException {
		
		// Diff mods string from the SQT file header should look like this
		// STY*=+80.000
		// Multiple dynamic modifications should be present on separate DiffMod lines in a SQT file
		
		String modString = headerItem.getValue();
		
			
		String[] tokens = modString.split("=");

		if (tokens.length < 2)
			throw new DataProviderException("Invalid dynamic modification string in sqt file: "+modString);
		if (tokens.length > 2)
			throw new DataProviderException("Invalid dynamic modification string in sqt file (appears to have > 1 dynamic modification): "+modString);

		String modChars = tokens[0].trim();
		// get the modification symbol (this character should follow the modification residue characters)
		// example S* -- S is the modified residue; * is the modification symbol
		if (modChars.length() < 2)
			throw new DataProviderException("No modification symbol found: "+modString);

		// remove the modification symbol and convert modification chars to upper case 
		modChars = modChars.substring(0, modChars.length()-1).toUpperCase();
		if (modChars.length() < 1)
			throw new DataProviderException("No residues found for dynamic modification: "+modString);


		String modMass = tokens[1].trim();
		BigDecimal modMassBd;


		try { modMassBd = new BigDecimal(modMass); }
		catch(NumberFormatException e) {
			throw new DataProviderException("Error parsing dynamic modification mass in sqt file: "+modMass);
		}

		// this modification may be for multiple residues; 
		// add one for each residue character
		for (int i = 0; i < modChars.length(); i++) {

			ResidueModification mod = new ResidueModification();
			mod.setModificationMass(modMassBd);
			mod.setModifiedResidue(modChars.charAt(i));

			dynamicResidueMods.add(mod);
		}
		
	}

	private void readStaticMod(SQTHeaderItem headerItem) throws DataProviderException {
		
		
		// Example: C=160.139
		
		String modString = headerItem.getValue();
		
		String[] tokens = modString.split("=");
		
		if (tokens.length < 2)
		    throw new DataProviderException("Invalid static modification string in sqt file: "+modString);
		if (tokens.length > 2)
		    throw new DataProviderException("Invalid static modification string in sqt file (appears to have > 1 static modification): "+modString);


		// convert modification chars to upper case 
		String modChars = tokens[0].trim().toUpperCase();
		String modMass = tokens[1].trim();
		BigDecimal modMassBd;
		
		try {
		    modMassBd = new BigDecimal(modMass);
		}
		catch(NumberFormatException e) {
		    throw new DataProviderException("Error parsing static modification mass in sqt file: "+modMass);
		}
		
		// this modification may be for multiple residues; 
		// add one for each residue character
		for (int i = 0; i < modChars.length(); i++) {
			
			ResidueModification mod = new ResidueModification();
			mod.setModificationMass(modMassBd);
			mod.setModifiedResidue(modChars.charAt(i));
			
			staticResidueMods.add(mod);
		}
	}
}
