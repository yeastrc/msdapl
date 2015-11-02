/**
 * ProteinProphetMzidDataProvider.java
 * @author Vagisha Sharma
 * Sep 30, 2011
 */
package org.yeastrc.ms.writer.mzidentml.proteinprophet;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.yeastrc.ms.dao.DAOFactory;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinGroupDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetRunDAO;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetGroup;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetRun;
import org.yeastrc.ms.writer.mzidentml.AnalysisSoftwareMaker;
import org.yeastrc.ms.writer.mzidentml.MzidDataProviderException;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareListType;
import org.yeastrc.ms.writer.mzidentml.jaxb.AnalysisSoftwareType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ProteinAmbiguityGroupType;
import org.yeastrc.ms.writer.mzidentml.jaxb.ProteinDetectionHypothesisType;
import org.yeastrc.ms.writer.mzidentml.jaxb.UserParamType;
import org.yeastrc.ms.writer.mzidentml.peptideprophet.PeptideProphetMzidDataProvider;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.nrseq.domain.NrDbProtein;

/**
 * 
 */
public class ProteinProphetMzidDataProvider extends PeptideProphetMzidDataProvider {

	private ProteinferDAOFactory pinferDaoFactory = ProteinferDAOFactory.instance();
	
	private ProteinProphetRunDAO prophetResDao = pinferDaoFactory.getProteinProphetRunDao();
	private ProteinProphetProteinGroupDAO grpDao = pinferDaoFactory.getProteinProphetProteinGroupDao();
	private ProteinProphetProteinDAO proteinDao = pinferDaoFactory.getProteinProphetProteinDao();
	
	
	private ProteinProphetRun pinferRun;
	private int pinferId;
	private List<Integer> proteinGroupIds;
	private int lastGroupIndex = 0;
	
	private int nrseqFastaDbId;
	
	public int getPinferId() {
		return pinferId;
	}


	public void setPinferId(int pinferId) {
		this.pinferId = pinferId;
	}


	@Override
	public void initializeFieldsBeforeWrite() throws MzidDataProviderException {
		
		super.initializeFieldsBeforeWrite();
		
		if(pinferId <= 0) {
			throw new MzidDataProviderException("Invalid protein inference ID: "+pinferId);
		}
		
		// load the protein inference
		ProteinProphetRun pinferRun = prophetResDao.loadProteinferRun(pinferId);
		if(pinferRun == null) {
			throw new MzidDataProviderException("Protein inference with ID: "+pinferId+" was not found in the database");
		}
		
		this.proteinGroupIds = grpDao.loadProphetGroupIds(pinferId);
		
		nrseqFastaDbId = NrSeqLookupUtil.getDatabaseId(getFastaFileName());
	}
	
	
	@Override
	public AnalysisSoftwareListType getAnalysisSoftware() throws MzidDataProviderException {
		
		AnalysisSoftwareListType listType = super.getAnalysisSoftware();
		
		AnalysisSoftwareMaker softwareMaker = new AnalysisSoftwareMaker();
		
		// Add ProteinProphet to the analysis software list
		AnalysisSoftwareType software = softwareMaker.makeProteinProphetAnalysisSoftware(this.pinferRun.getProgram().getVersion());
		listType.getAnalysisSoftware().add(software);
		
		return listType;
	}


	@Override
	public ProteinAmbiguityGroupType getNextProteinAmbiguityGroup()
			throws MzidDataProviderException {
		
		/*
		 
		 <ProteinAmbiguityGroup id="PAG_hit_1" > 
		 <ProteinDetectionHypothesis id="PDH_HSP7D_MANSE" DBSequence_ref="DBSeq_HSP7D_MANSE" passThreshold="true"> 
			<PeptideHypothesis PeptideEvidence_Ref="PE_1_1_HSP7D_MANSE" /> 
			<PeptideHypothesis PeptideEvidence_Ref="PE_3_1_HSP7D_MANSE" /> 
			<cvParam accession="MS:1001171" name="mascot:score" cvRef="PSI-MS" value="104.854382332144" /> 
			<cvParam accession="MS:1001093" name="sequence coverage" cvRef="PSI-MS" value="4" /> 
			<cvParam accession="MS:1001097" name="distinct peptide sequences" cvRef="PSI-MS" value="2" />
		 ... 
		 </ProteinAmbiguityGroup>
		 */
		
		if(lastGroupIndex >= proteinGroupIds.size())
			return null;
		
		ProteinProphetGroup grp = pinferDaoFactory.getProteinProphetProteinGroupDao().load(proteinGroupIds.get(lastGroupIndex++));
		
		ProteinAmbiguityGroupType protAmbigGrp = new ProteinAmbiguityGroupType();
		
		protAmbigGrp.setId(String.valueOf(grp.getGroupNumber()));
		
		List<ProteinProphetProtein> proteins = proteinDao.loadProteinProphetGroupProteins(pinferId, grp.getId());
		
		// sort by indistinguishable group IDs;
		Collections.sort(proteins, new Comparator<ProteinProphetProtein>() {

			@Override
			public int compare(ProteinProphetProtein arg0,ProteinProphetProtein arg1) {
				return Integer.valueOf(arg0.getGroupId()).compareTo(arg1.getGroupId());
			}
		});
		
		
		for(ProteinProphetProtein protein: proteins) {
			
			ProteinDetectionHypothesisType pdht = new ProteinDetectionHypothesisType();
			
			pdht.setPassThreshold(true);
			pdht.setId(String.valueOf(protein.getNrseqProteinId()));
			
			List<NrDbProtein> nrproteins = NrSeqLookupUtil.getDbProteins(protein.getNrseqProteinId(), this.nrseqFastaDbId); 
			pdht.setName(nrproteins.get(0).getAccessionString());
			
			
			UserParamType param = new UserParamType();
			param.setName("ProteinProphet_group_probability");
			param.setValue(String.valueOf(grp.getProbability()));
			protAmbigGrp.getParamGroup().add(new UserParamType());
			
			
			param = new UserParamType();
			param.setName("ProteinProphet_probability");
			param.setValue(String.valueOf(protein.getProbability()));
			protAmbigGrp.getParamGroup().add(new UserParamType());
			
			
			protAmbigGrp.getProteinDetectionHypothesis().add(pdht);
		}
		
		return protAmbigGrp;
		
	}

	@Override
	public boolean hasProteinAnalysis() {
		return true;
	}
	

	
	

}
