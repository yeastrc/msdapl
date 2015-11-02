/**
 * 
 */
package org.yeastrc.ms.parser.unimod;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.yeastrc.ms.parser.unimod.jaxb.ModT;
import org.yeastrc.ms.parser.unimod.jaxb.ModificationsT;
import org.yeastrc.ms.parser.unimod.jaxb.SpecificityT;
import org.yeastrc.ms.parser.unimod.jaxb.UnimodT;

/**
 * UnimodRepository.java
 * @author Vagisha Sharma
 * Aug 2, 2011
 * 
 */
public class UnimodRepository {

	private List<ModT> modsList;
	
	public final static double DELTA_MASS_TOLERANCE_1 = 0.1; 
	
	public void initialize() throws UnimodRepositoryException {
		
		UnimodParser parser = new UnimodParser();
		UnimodT unimod;
		
		try {
			unimod = parser.read();
		} catch (FileNotFoundException e) {
			throw new UnimodRepositoryException("unimod.xml file was not found", e);
		} catch (JAXBException e) {
			throw new UnimodRepositoryException("Error reading unimod.xml file", e);
		}
		
		ModificationsT mods = unimod.getModifications();
		this.modsList = mods.getMod();
	}
	
	public ModT getModification(char modifiedResidue, double deltaMonoMass, double tolerance, boolean isMonoMass) throws UnimodRepositoryException {
		
		if(modsList == null) {
			throw new UnimodRepositoryException("Repository has not been initizlized");
		}
		
		boolean matchChar = false;
		
		String modResStr = String.valueOf(modifiedResidue);
		
		for(ModT mod: modsList) {
			
			List<SpecificityT> specifities = mod.getSpecificity();
			for(SpecificityT specificity: specifities) {
				
				if(specificity.getSite().equalsIgnoreCase(modResStr)) {
					matchChar = true;
				}
						
			}
			
			if(!matchChar) 
				continue;
			
			double mass;
			if(isMonoMass) {
				mass = mod.getDelta().getMonoMass();
			}
			else {
				mass = mod.getDelta().getAvgeMass();
			}
			
			double diff = Math.abs(mass - deltaMonoMass);
			if((diff <= tolerance)) {
				return mod;
			}
		}
		
		// did not find a modification
		throw new UnimodRepositoryException("No matching modification found for residue: "+modifiedResidue+"; mass: "+deltaMonoMass);
	}
	
	
	public static void main(String[] args) throws UnimodRepositoryException {
		
		UnimodRepository rep = new UnimodRepository();
		rep.initialize();
		
	}
	
}
