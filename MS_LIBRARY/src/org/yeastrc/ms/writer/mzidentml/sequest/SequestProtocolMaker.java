/**
 * 
 */
package org.yeastrc.ms.writer.mzidentml.sequest;

import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.domain.general.MsEnzyme;
import org.yeastrc.ms.domain.search.MsResidueModification;
import org.yeastrc.ms.domain.search.MsSearch;
import org.yeastrc.ms.domain.search.Program;
import org.yeastrc.ms.parser.unimod.UnimodRepository;
import org.yeastrc.ms.parser.unimod.UnimodRepositoryException;
import org.yeastrc.ms.parser.unimod.jaxb.ModT;
import org.yeastrc.ms.writer.mzidentml.CvConstants;
import org.yeastrc.ms.writer.mzidentml.CvParamMaker;
import org.yeastrc.ms.writer.mzidentml.EnzymeTypeMaker;
import org.yeastrc.ms.writer.mzidentml.MzidConstants;
import org.yeastrc.ms.writer.mzidentml.MzidDataProviderException;
import org.yeastrc.ms.writer.mzidentml.SpectrumIdentificationProtocolMaker;
import org.yeastrc.ms.writer.mzidentml.jaxb.CVParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.EnzymeType;
import org.yeastrc.ms.writer.mzidentml.jaxb.EnzymesType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ModificationParamsType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ParamType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SearchModificationType;
import org.yeastrc.ms.writer.mzidentml.jaxb.SpectrumIdentificationProtocolType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ToleranceType;

/**
 * SequestProtocolMaker.java
 * @author Vagisha Sharma
 * Aug 4, 2011
 * 
 */
public class SequestProtocolMaker implements SpectrumIdentificationProtocolMaker {

	private final UnimodRepository unimodRepository;
	private final MsSearch search;
	
	public SequestProtocolMaker(UnimodRepository unimodRepository,
			MsSearch search) {
		
		this.unimodRepository = unimodRepository;
		this.search = search;
	}
	
	public SpectrumIdentificationProtocolType getProtocol() throws MzidDataProviderException {
		
		SpectrumIdentificationProtocolType specIdProtocol = new SpectrumIdentificationProtocolType();
		
		
		if(Program.isSequest(search.getSearchProgram())) {
			specIdProtocol.setId(MzidConstants.SEQUEST_PROTOCOL_ID);

			specIdProtocol.setAnalysisSoftwareRef(MzidConstants.SEQUEST_SW_ID);
		}
		else {
			throw new MzidDataProviderException("Do not know how to make AnalysisProtocolCollectionType for program: "+search.getSearchProgram());
		}
		
		ParamType searchType = new ParamType();
		searchType.setCvParam(CvParamMaker.getInstance().make("MS:1001083", "ms-ms search", CvConstants.PSI_CV));
		specIdProtocol.setSearchType(searchType);
		
		
		// modifications
		ModificationParamsType modParams = new ModificationParamsType();
		
		// dynamic modifications
		addDynamicResidueModifications(search, modParams);
		// static modifications
		addStaticResidueModifications(search, modParams);
		
		if(modParams.getSearchModification().size() > 0) {
			specIdProtocol.setModificationParams(modParams);
		}
		
		// TODO add other search params in the <AdditionalSearchParams>
		
		
		// Enzyme
		EnzymesType enzymesType = new EnzymesType();
		specIdProtocol.setEnzymes(enzymesType);
		List<MsEnzyme> searchEnzymes = search.getEnzymeList();
		if(searchEnzymes.size() > 1) {
			throw new MzidDataProviderException("Multiple enzymes found for search with ID: "+search.getId());
		}
		EnzymeType enzymeType = EnzymeTypeMaker.makeEnzymeType(searchEnzymes.get(0));
		enzymesType.getEnzyme().add(enzymeType);
		
		// TODO Mass table
		
		// Fragment tolerance
		specIdProtocol.setFragmentTolerance(makeFragmentTolerance());
		
		// Parent tolerance
		specIdProtocol.setParentTolerance(makeParentTolerance());
		
		// threshold
		ParamListType paramList = new ParamListType();
		paramList.getParamGroup().add(CvParamMaker.getInstance().make("MS:1001494", "no threshold", CvConstants.PSI_CV));
		specIdProtocol.setThreshold(paramList);
		
		
		return specIdProtocol;
	}

	private ToleranceType makeParentTolerance() throws MzidDataProviderException {
		
		double tolerance = -1.0;
		boolean unitIsDaltons = true;
		
		if(Program.isSequest(search.getSearchProgram())) {
			
			String val = DAOFactory.instance().getSequestSearchDAO().getSearchParamValue(search.getId(), SequestParamsConstants.PEPTIDE_MASS_TOLERANCE);
			if(val != null) {
				tolerance = Double.parseDouble(val);
			}
			
			// peptide_mass_units = 0                  ; 0=amu, 1=mmu, 2=ppm
			val = DAOFactory.instance().getSequestSearchDAO().getSearchParamValue(search.getId(), SequestParamsConstants.PEPTIDE_MASS_UNITS);
			if("0".equals(val)) {
				unitIsDaltons = true;
			}
			else if("1".equals(val) || "2".equals(val)) {
				unitIsDaltons = false;
			}
		}
		else {
			throw new MzidDataProviderException("Do not know how to get parent tolerance for program: "+search.getSearchProgram());
		}

		if(tolerance == -1.0) {
			throw new MzidDataProviderException("Did not find parent  tolerance for search ID "+search.getId());
		}
		
		return makeTolerance(tolerance, unitIsDaltons);
	}

	private ToleranceType makeFragmentTolerance() throws MzidDataProviderException {
		
		double tolerance = -1.0;
		
		if(Program.isSequest(search.getSearchProgram())) {
			
			String val = DAOFactory.instance().getSequestSearchDAO().getSearchParamValue(search.getId(), SequestParamsConstants.FRAGMENT_ION_TOLERANCE);
			if(val != null) {
				tolerance = Double.parseDouble(val);
			}
		}
		else {
			throw new MzidDataProviderException("Do not know how to get fragment tolerance for program: "+search.getSearchProgram());
		}
		
		
		if(tolerance == -1.0) {
			throw new MzidDataProviderException("Did not find fragment tolerance for search ID "+search.getId());
		}
		
		return makeTolerance(tolerance, true);
	}
	
	private ToleranceType makeTolerance(double tolerance, boolean unitIsDalton) {
		
		ToleranceType tolType = new ToleranceType();
		
		// Example: 
		// <cvParam accession="MS:1001412" name="search tolerance plus value" cvRef="PSI-MS" value="1.5" unitAccession="UO:0000221" unitName="dalton" unitCvRef="UO"/>
	    // <cvParam accession="MS:1001413" name="search tolerance minus value" cvRef="PSI-MS" value="1.5" unitAccession="UO:0000221" unitName="dalton" unitCvRef="UO"/>

		CVParamType param = CvParamMaker.getInstance().make("MS:1001412", "search tolerance plus value", String.valueOf(tolerance), CvConstants.PSI_CV);
		param.setUnitCvRef(CvConstants.UNIT_ONTOLOGY_CV.getId());
		tolType.getCvParam().add(param);
		
		if(unitIsDalton) {
			param.setUnitAccession("UO:0000221");
			param.setUnitName("dalton");
		}
		else {
			param.setUnitAccession("UO:0000166");
			param.setUnitName("parts per notation unit");
		}
		return tolType;
	}
	
	private void addDynamicResidueModifications(MsSearch search,
			ModificationParamsType modParams) throws MzidDataProviderException {
		
		List<MsResidueModification> mods = search.getDynamicResidueMods();
		addResidueModifications(modParams, mods);
	}
	
	private void addStaticResidueModifications(MsSearch search,
			ModificationParamsType modParams) throws MzidDataProviderException {
		
		List<MsResidueModification> mods = search.getStaticResidueMods();
		addResidueModifications(modParams, mods);
	}

	private void addResidueModifications(ModificationParamsType modParams,
			List<MsResidueModification> mods) throws MzidDataProviderException {
		
		
		for(MsResidueModification mod: mods) { 
			
			SearchModificationType modType = new SearchModificationType();
			modParams.getSearchModification().add(modType);
			
			modType.setFixedMod(false);
			modType.setMassDelta(mod.getModificationMass().floatValue());
			modType.getResidues().add(String.valueOf(mod.getModifiedResidue()));
			
			
			// get the Unimod entry for this modification
			try {
				ModT unimod_mod = unimodRepository.getModification(mod.getModifiedResidue(), 
						mod.getModificationMass().doubleValue(), 
						UnimodRepository.DELTA_MASS_TOLERANCE_1, true);

				if(unimod_mod != null) {

					CvParamMaker paramMaker = CvParamMaker.getInstance();
					CVParamType param = paramMaker.make("UNIMOD:" + unimod_mod.getRecordId(), unimod_mod.getTitle(), CvConstants.UNIMOD_CV);

					modType.getCvParam().add(param);
				}
			}
			catch(UnimodRepositoryException e) {
				
				throw new MzidDataProviderException("Unimod repository lookup failed for modification  "+
						mod.getModifiedResidue()+" with delta mass: "+mod.getModificationMass(), e);
			}
		}
	}
}
