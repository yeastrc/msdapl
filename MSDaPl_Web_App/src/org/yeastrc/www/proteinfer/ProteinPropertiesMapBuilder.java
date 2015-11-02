/**
 * ProteinPropertiesMapBuilder.java
 * @author Vagisha Sharma
 * Mar 21, 2010
 * @version 1.0
 */
package org.yeastrc.www.proteinfer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.dao.protinfer.proteinProphet.ProteinProphetProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProtein;
import org.yeastrc.ms.domain.protinfer.proteinProphet.ProteinProphetProtein;
import org.yeastrc.ms.util.TimeUtils;
import org.yeastrc.www.compare.ProteinDatabaseLookupUtil;
import org.yeastrc.www.proteinfer.proteinProphet.ProphetProteinProperties;

/**
 * 
 */
public class ProteinPropertiesMapBuilder {

	private boolean getPi = false;
	private boolean getMolWt = false;
	private boolean getAccession = false;

	private static final Logger log = Logger.getLogger(ProteinPropertiesMapBuilder.class.getName());

	private List<Integer> fastaDbIds;
	
	public void setGetPi(boolean getPi) {
		this.getPi = getPi;
	}
	public void setGetMolWt(boolean getMolWt) {
		this.getMolWt = getMolWt;
	}
	public void setGetAccession(boolean getAccession) {
		this.getAccession = getAccession;
	}

	public void updateMap(int pinferId, Map<Integer, ? extends ProteinProperties> map) {

		log.info("Updating Protein Properties map");
		log.info("\tGetting pI: "+this.getPi);
		log.info("\tGetting molecularWt.: "+this.getMolWt);
		log.info("\tGetting Accession: "+this.getAccession);
		long s = System.currentTimeMillis();

		if(this.getAccession) {
			fastaDbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
		}
		
		ProteinPropertiesBuilder propsBuilder = new ProteinPropertiesBuilder();
		propsBuilder.setFastaDbIds(fastaDbIds);
		propsBuilder.setGetAccession(getAccession);
		propsBuilder.setGetMolWt(getMolWt);
		propsBuilder.setGetPi(getPi);

		for(Integer piProteinId: map.keySet()) {
			propsBuilder.update(map.get(piProteinId));
		}
		long e = System.currentTimeMillis();
		log.info("Time to update protein properties map: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");

	}
	
	public Map<Integer, ? extends ProteinProperties> buildMap(int pinferId) {

		Map<Integer, ProteinProperties> map = null;
		log.info("Building Protein Properties map");
		log.info("\tGetting pI: "+this.getPi);
		log.info("\tGetting molecularWt.: "+this.getMolWt);
		log.info("\tGetting Accession: "+this.getAccession);
		
		long s = System.currentTimeMillis();

		if(this.getAccession) {
			fastaDbIds = ProteinDatabaseLookupUtil.getInstance().getDatabaseIdsForProteinInference(pinferId);
		}
		
		ProteinPropertiesBuilder propsBuilder = new ProteinPropertiesBuilder();
		propsBuilder.setFastaDbIds(fastaDbIds);
		propsBuilder.setGetAccession(getAccession);
		propsBuilder.setGetMolWt(getMolWt);
		propsBuilder.setGetPi(getPi);
		
		
		ProteinferRun run = ProteinferDAOFactory.instance().getProteinferRunDao().loadProteinferRun(pinferId);

		// If this is a IDPicker run we will load the protein from the IDPicker tables so that 
		// we have the IDPicker groupIDs.
		if(ProteinInferenceProgram.isIdPicker(run.getProgram())) {

			IdPickerProteinDAO proteinDao = ProteinferDAOFactory.instance().getIdPickerProteinDao();

			List<IdPickerProtein> proteins = proteinDao.loadProteins(pinferId);
			map = new HashMap<Integer, ProteinProperties>((int) (proteins.size() * 1.5));

			long e = System.currentTimeMillis();
			log.info("Time to get all proteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");

			s = System.currentTimeMillis();
			for(IdPickerProtein protein: proteins) {
				ProteinProperties props = propsBuilder.build(pinferId, protein);
				props.setProteinGroupId(protein.getProteinGroupLabel());
				map.put(protein.getId(), props);
			}
			e = System.currentTimeMillis();
			log.info("Time to build protein properties map: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}

		// If this a ProteinProphet run we will load proteins from the ProteinProphet tables 
		// so that we have ProteinProphet protein group Ids.
		else if(run.getProgram() == ProteinInferenceProgram.PROTEIN_PROPHET) {

			ProteinProphetProteinDAO proteinDao = ProteinferDAOFactory.instance().getProteinProphetProteinDao();

			List<ProteinProphetProtein> proteins = proteinDao.loadProteins(pinferId);
			map = new HashMap<Integer, ProteinProperties>((int) (proteins.size() * 1.5));
			long e = System.currentTimeMillis();
			log.info("Time to get all proteins: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");

			s = System.currentTimeMillis();
			for(ProteinProphetProtein protein: proteins) {

				ProphetProteinProperties props = new ProphetProteinProperties(propsBuilder.build(pinferId, protein));
				props.setProteinGroupId(protein.getGroupId());
				props.setProteinProphetGroupId(protein.getProteinProphetGroupId());
				map.put(protein.getId(), props);
			}
			e = System.currentTimeMillis();
			log.info("Time to build protein properties map: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds");
		}

		return map;
	}

}
