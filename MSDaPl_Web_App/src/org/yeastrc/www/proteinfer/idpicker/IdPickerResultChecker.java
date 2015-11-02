package org.yeastrc.www.proteinfer.idpicker;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;
import org.yeastrc.experiment.ProjectExperimentDAO;
import org.yeastrc.ms.dao.ProteinferDAOFactory;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferProteinDAO;
import org.yeastrc.ms.dao.protinfer.ibatis.ProteinferRunDAO;
import org.yeastrc.ms.dao.protinfer.idpicker.ibatis.IdPickerProteinDAO;
import org.yeastrc.ms.domain.protinfer.ProteinInferenceProgram;
import org.yeastrc.ms.domain.protinfer.ProteinferPeptide;
import org.yeastrc.ms.domain.protinfer.ProteinferProtein;
import org.yeastrc.ms.domain.protinfer.ProteinferRun;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerPeptide;
import org.yeastrc.ms.domain.protinfer.idpicker.IdPickerProtein;
import org.yeastrc.nrseq.dao.NrSeqLookupUtil;
import org.yeastrc.www.proteinfer.job.ProteinInferJobSearcher;

public class IdPickerResultChecker {

	
	public static void main(String[] args) throws SQLException {
		
		
		try {
            // Create initial context
            System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            "org.apache.naming.java.javaURLContextFactory");
            System.setProperty(Context.URL_PKG_PREFIXES, 
            "org.apache.naming");            
            InitialContext ic = new InitialContext();

            ic.createSubcontext("java:");
            ic.createSubcontext("java:comp");
            ic.createSubcontext("java:comp/env");
            ic.createSubcontext("java:comp/env/jdbc");

            // Construct YRC_NRSEQ DataSource
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/YRC_NRSEQ");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/nrseq", ds);

            // Construct msData DataSource
            ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/msData");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/msData", ds);
            
            
            // Construct yrc DataSource
            ds = new BasicDataSource();
            ds.setUrl("jdbc:mysql://localhost/mainDb");
            ds.setUsername("root");
            ds.setPassword("");
            ic.bind("java:comp/env/jdbc/yrc", ds);
        }
        catch (NamingException ex) {
            ex.printStackTrace();
        }
        
		IdPickerResultChecker checker = new IdPickerResultChecker();
		
		String projIds = args[0];
		String[] projectIds = projIds.split(",");
		for(String projId: projectIds) {
			int id = 0;
			try {id = Integer.parseInt(projId);}catch(Exception e){}
			if(id != 0)
				checker.check(id);
		}
		
		
	}
	
	public void check(int projectId) throws SQLException {
		
		System.out.println("PROJECT: "+projectId);
		List<Integer> experimentIds = ProjectExperimentDAO.instance().getExperimentIdsForProject(projectId);
		
		Collections.sort(experimentIds);
		for(Integer experimentId: experimentIds) {
			checkExperiment(experimentId);
		}
	}
	
	private void checkExperiment(Integer experimentId) {

		System.out.println("\tEXPERIMENT ID: "+experimentId);
		
		List<Integer> piRunIds = ProteinInferJobSearcher.getInstance().getProteinferIdsForMsExperiment(experimentId);
        Collections.sort(piRunIds);
        
        for(Integer piRunId: piRunIds) {
        	checkPiRun(piRunId);
        }
	}

	private void checkPiRun(Integer piRunId) {

		ProteinferRunDAO runDao = ProteinferDAOFactory.instance().getProteinferRunDao();
		ProteinferProteinDAO protDao = ProteinferDAOFactory.instance().getProteinferProteinDao();
		
		ProteinferRun run = runDao.loadProteinferRun(piRunId);
	    if(ProteinInferenceProgram.isIdPicker(run.getProgram())) { // is this a IdPicker run?
	    	System.out.println("\t\tPROTEIN INFERENCE: "+piRunId);
	    	
	    	System.out.println("\t\t\tChecking Peptide to Protein assignment...");
	    	List<Integer> proteinIds = protDao.getProteinferProteinIds(piRunId);
	    	for(Integer proteinId: proteinIds) {
	    		checkProtein(protDao, proteinId);
	    	}
	    	
	    	System.out.println("\t\t\tChecking Indistinguishable protein groups...");
	    	checkIdPickerGroups(piRunId);
	    	
	    	System.out.println("\t\t\tChecking clusters...");
	    	checkIdPickerClusters(piRunId);
	    	
	    }
	}

	private void checkIdPickerClusters(Integer piRunId) {
		IdPickerProteinDAO iprotDao = ProteinferDAOFactory.instance().getIdPickerProteinDao();
		List<Integer> clusterLabels = iprotDao.getClusterLabels(piRunId);
		
		for(Integer clusterLabel: clusterLabels) {
			
			List<Integer> groupLabels = iprotDao.getGroupLabelsForCluster(piRunId, clusterLabel);
			//System.out.println("Cluster has # groups: "+groupIds.size());
			
			Set<Integer> proteinIds = new HashSet<Integer>();
			Map<String, IdPickerPeptide> peptides = new HashMap<String, IdPickerPeptide>();
			
			for(Integer groupLabel: groupLabels) {
				
				List<IdPickerProtein> proteins = iprotDao.loadIdPickerGroupProteins(piRunId, groupLabel);
				
				for(IdPickerProtein protein: proteins) {
					proteinIds.add(protein.getId());
						
					for(IdPickerPeptide peptide: protein.getPeptides()) {
						peptides.put(peptide.getSequence(), peptide);
					}
				}
			}
			
			// Make sure the peptides don't match proteins outside the cluster
			for(IdPickerPeptide pept: peptides.values()) {
				List<Integer> myIds = iprotDao.getProteinsForPeptide(piRunId, pept.getSequence(), true);
				
				for(Integer myId: myIds) {
					if(!proteinIds.contains(myId)) {
						System.out.println("ERROR: Peptide: "+pept.getSequence()+" should not match "+myId);
						//System.exit(1);
					}
				}
			}
		}
				
	}
	
	private void checkIdPickerGroups(Integer piRunId) {
		IdPickerProteinDAO iprotDao = ProteinferDAOFactory.instance().getIdPickerProteinDao();
		List<Integer> clusterLabels = iprotDao.getClusterLabels(piRunId);
		int groupCount = iprotDao.getIdPickerGroupCount(piRunId);
		int count = 0;
		Set<Integer> seenGroupLabels = new HashSet<Integer>(groupCount);
		for(Integer clusterLabel: clusterLabels) {
			
			List<Integer> groupLabels = iprotDao.getGroupLabelsForCluster(piRunId, clusterLabel);
			//System.out.println("Cluster has # groups: "+groupIds.size());
			
			for(Integer groupLabel: groupLabels) {
				
				count++;
				if(seenGroupLabels.contains(groupLabel)) {
					System.out.println("ERROR: Already seen this groupLabel: "+groupLabel+"; cluster: "+clusterLabel);
					// System.exit(1);
				}
				seenGroupLabels.add(groupLabel);
				List<IdPickerProtein> proteins = iprotDao.loadIdPickerGroupProteins(piRunId, groupLabel);
				//System.out.println("Group has # proteins: "+proteins.size());
				
				// Make sure all proteins in the group match exactly the same peptides
				Set<String> peptides = null;
				Set<Integer> proteinIds = new HashSet<Integer>();
				for(IdPickerProtein protein: proteins) {
					proteinIds.add(protein.getId());
					if(peptides == null) {
						peptides = new HashSet<String>();
						
						for(IdPickerPeptide peptide: protein.getPeptides()) {
							peptides.add(peptide.getSequence());
						}
						//System.out.println("Group has # peptides: "+peptides.size());
					}
					else {
						if(protein.getPeptideCount() != peptides.size()) {
							System.out.println("ERROR: Num peptides for protein: "+protein.getPeptideCount()+"; previously seen: "+peptides.size());
							System.out.println("ERROR: GroupLabel: "+groupLabel+"; proteinId: "+protein.getId());
							// System.exit(1);
						}
						for(IdPickerPeptide peptide: protein.getPeptides()) {
							if(!peptides.contains(peptide.getSequence())) {
								System.out.println("ERROR: GroupLabel: "+groupLabel+"; proteinId: "+protein.getId());
								System.out.println("ERROR: Peptide not seen: "+peptide.getSequence());
								// System.exit(1);
							}
								
						}
					}
				}
				
				// Make sure all these "unique" peptides match only these proteins;
				List<IdPickerPeptide> pepts = proteins.get(0).getPeptides();
				for(IdPickerPeptide pept: pepts) {
					if(!pept.isUniqueToProtein())
						continue;
					List<Integer> myIds = iprotDao.getProteinsForPeptide(piRunId, pept.getSequence(), true);
					if(myIds.size() != proteinIds.size()) {
						System.out.println("ERROR: Peptide "+pept.getSequence()+" matches "+myIds.size()+" proteins; should have matched: "+proteinIds.size());
						//System.exit(1);
					}
					for(Integer myId: myIds) {
						if(!proteinIds.contains(myId)) {
							System.out.println("ERROR: Peptide: "+pept.getSequence()+" should not match "+myId);
							//System.exit(1);
						}
					}
				}
			}
		}
		
		if(count != groupCount) {
			System.out.println("ERROR: group count: "+groupCount+" does not equal groups seen: "+count);
			//System.exit(1);
		}
	}

	private void checkProtein(ProteinferProteinDAO protDao, Integer proteinId) {
		
		ProteinferProtein protein = protDao.loadProtein(proteinId);
		
		// get the sequence for this protein
		int nrseqId = protein.getNrseqProteinId();
		String sequence = NrSeqLookupUtil.getProteinSequence(nrseqId);
		
		// get the peptides assigned to this protein
		Set<Integer> matchPeptideIds = new HashSet<Integer>();
		for(ProteinferPeptide peptide: protein.getPeptides()) {
			String peptideSeq = peptide.getSequence();
			if(!sequence.contains(peptideSeq)) {
				System.out.println("ERROR: Peptide: "+peptideSeq+"; nrseqId: "+nrseqId+"; pinferProteinId: "+proteinId);
				//System.exit(1);
			}
			matchPeptideIds.add(peptide.getId());
		}
		
		// get ALL the peptides and make sure there is no missed assignment
//		ProteinferPeptideDAO peptDao = ProteinferDAOFactory.instance().getProteinferPeptideDao();
//		List<Integer> peptideIds = peptDao.getPeptideIdsForProteinferRun(protein.getProteinferId());
//		for(Integer peptideId: peptideIds) {
//			ProteinferPeptide pept = peptDao.load(peptideId);
//			if(sequence.contains(pept.getSequence())) {
//				if(!matchPeptideIds.contains(pept.getId())) {
//					System.out.println("Peptide not assigned to protein: "+nrseqId+"; peptide: "+pept.getSequence());
//					System.exit(1);
//				}
//			}
//		}
		
	}
	
	
}
