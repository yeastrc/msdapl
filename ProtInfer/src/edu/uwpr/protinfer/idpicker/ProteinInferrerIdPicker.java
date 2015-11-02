package edu.uwpr.protinfer.idpicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.infer.GraphBuilder;
import edu.uwpr.protinfer.infer.InferredProtein;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.PeptideSpectrumMatch;
import edu.uwpr.protinfer.infer.Protein;
import edu.uwpr.protinfer.infer.ProteinInferrer;
import edu.uwpr.protinfer.infer.ProteinInferrerMaximal;
import edu.uwpr.protinfer.infer.SpectrumMatch;
import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.ConnectedComponentFinder;
import edu.uwpr.protinfer.infer.graph.GraphCollapser;
import edu.uwpr.protinfer.infer.graph.InvalidVertexException;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.infer.graph.ProteinVertex;
import edu.uwpr.protinfer.infer.graph.SetCoverFinder;
import edu.uwpr.protinfer.util.TimeUtils;

public class ProteinInferrerIdPicker implements ProteinInferrer {

    private static final Logger log = Logger.getLogger(ProteinInferrerIdPicker.class);


    public <S extends SpectrumMatch> List<InferredProtein<S>> 
            inferProteins(List<InferredProtein<S>>  allProteins, IDPickerParams params) {
        
        long s = System.currentTimeMillis();
        
        
        // build a graph
        GraphBuilder graphBuilder = new GraphBuilder();
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = graphBuilder.buildGraph(allProteins);
        if(graph.getLeftVertices().size() != allProteins.size()) {
            log.error("Numbers don't match! All proteins: "+allProteins.size()+"; Vertices: "+graph.getLeftVertices().size()+"\nCannot continue...");
            return null;
        }
        log.info("# proteins in graph: "+graph.getLeftVertices().size());
        log.info("# peptides in graph: "+graph.getRightVertices().size());
        

        // collapse vertices
        GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
        try {
            collapser.collapseGraph(graph);
        }
        catch (InvalidVertexException e) {
            log.error("Error building graph: "+e.getMessage());
            return null;
        }
        log.info("# proteins GROUPS in graph: "+graph.getLeftVertices().size());
        log.info("# peptides GROUPS in graph: "+graph.getRightVertices().size());
        
        
        // FILTER!!
        log.info("Number of proteins BEFORE filtering: "+allProteins.size());
        log.info("Number of protein GROUPS BEFORE filtering: "+graph.getLeftVertices().size());
        
        // mark all proteins as accepted to begin with
        markAllProteinsAccepted(graph);
        
        // mark unique peptides
        markUniquePeptides(graph);
        
        // mark proteins un-accepted by coverage.
        if(params.getMinCoverage() > 0.0) {
            markProteinsUnacceptedByCoverage(graph, allProteins, params.getMinCoverage());
        }
        
        // mark proteins un-accepted by min peptides
        if(params.getMinPeptides() > 1) {
            markProteinsUnacceptedByMinPeptides(graph, params.getMinPeptides());
        }
        
        // mark proteins un-accepted by min unique peptides
        if(params.getMinUniquePeptides() > 0) {
            markProteinsUnacceptedByMinUniquePeptides(graph, params.getMinUniquePeptides());
        }
        
        // remove un-accepted proteins
        removeUnacceptedProteins(graph, allProteins);
        
        log.info("Number of proteins AFTER filtering: "+allProteins.size());
        log.info("Number of protein GROUPS AFTER filtering: "+graph.getLeftVertices().size());
        
        // mark all remaining proteins unaccepted -- for parsimony analysis in the last step
        markAllProteinsUnaccepted(graph);
        
        
        // set the protein and peptide group ids.
        int groupId = 1;
        for(ProteinVertex vertex: graph.getLeftVertices()) {
            for(Protein prot: vertex.getProteins()) {
                prot.setProteinGroupLabel(groupId);
            }
            groupId++;
        }
        groupId = 1;
        for(PeptideVertex vertex: graph.getRightVertices()) {
            for(Peptide pept: vertex.getPeptides()) {
                pept.setPeptideGroupLabel(groupId);
            }
            groupId++;
        }

        // find protein clusters
        ConnectedComponentFinder connCompFinder = new ConnectedComponentFinder();
        connCompFinder.findAllConnectedComponents(graph);


        // do parsimony analysis
        SetCoverFinder<ProteinVertex, PeptideVertex> setCoverFinder = new SetCoverFinder<ProteinVertex, PeptideVertex>();
        List<ProteinVertex> cover = setCoverFinder.getGreedySetCover(graph);
        for (ProteinVertex vertex: cover) 
            vertex.setAccepted(true);

        int parsimCount = 0;
        for(InferredProtein<S> prot: allProteins) 
            if(prot.getProtein().isAccepted())  parsimCount++;
        
        long e = System.currentTimeMillis();
        log.info("Inferred proteins in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds \nAll: "+
                allProteins.size()+" Parsimonious Groups: "+cover.size()+"; Parsimonious Proteins: "+parsimCount);

        
        // Mark subset proteins
        s = System.currentTimeMillis();
        
        SubsetProteinFinder subsetFinder = new SubsetProteinFinder();
        try {
			subsetFinder.markSubsetProteins(allProteins);
		} catch (SubsetProteinFinderException e1) {
			log.error(e1.getMessage());
			return null;
		}
        int subsetCount = 0;
        int subsetGrpCount = 0;
        Set<Integer> grpIdSeen = new HashSet<Integer>();
        for(InferredProtein<S> prot: allProteins) {
        	if(prot.getProtein().isSubset()) {
        		if(prot.getProtein().isAccepted()) {
        			log.error("Protein cannot be both parsimonious and subset!. "+prot.getAccession());
        		}
        		subsetCount++;
        	}
//        	else if(!prot.getProtein().isAccepted()) {
//        		log.info("NOT parsimonious AND NOT subset: "+prot.getAccession());
//        	}
        	if(grpIdSeen.contains(prot.getProteinGroupLabel()))
        		continue;
        	grpIdSeen.add(prot.getProteinGroupLabel());
        	if(prot.getProtein().isSubset())
        		subsetGrpCount++;
        }
        log.info("Marked subset proteins in: "+TimeUtils.timeElapsedSeconds(s, e)+" seconds \nAll: "+
                allProteins.size()+" Subset Groups: "+subsetGrpCount+"; Subset Proteins: "+subsetCount);
        e = System.currentTimeMillis();
        
        return allProteins;
    }

    private void markUniquePeptides(BipartiteGraph<ProteinVertex, PeptideVertex> graph) {
        
        for(ProteinVertex v: graph.getLeftVertices()) {
            
            List<PeptideVertex> peptList = graph.getAdjacentVerticesL(v);
            for(PeptideVertex pept: peptList) {
                if(graph.getAdjacentVerticesR(pept).size() == 1) {
                    // mark all the peptides in this vertex as unique
                    for(Peptide p: pept.getPeptides())
                        p.markUnique(true);
                }
            }
        }
    }

    private void markAllProteinsUnaccepted(BipartiteGraph<ProteinVertex, PeptideVertex> graph) {
        for(ProteinVertex v: graph.getLeftVertices()) {
            v.setAccepted(false);
        }
    }
    
    private void markAllProteinsAccepted(BipartiteGraph<ProteinVertex, PeptideVertex> graph) {
        for(ProteinVertex v: graph.getLeftVertices()) {
            v.setAccepted(true);
        }
    }
    
    // Coverage is at a protein level but the graph we are given has protein groups as vertices
    // We will mark a vertex un-accepted if ALL proteins in the vertex are below the min coverage
    private <S extends SpectrumMatch> void markProteinsUnacceptedByCoverage(
            BipartiteGraph<ProteinVertex, PeptideVertex> graph, 
            List<InferredProtein<S>> proteins, 
            float coverage) {
        
        int numUnaccepted = 0;
        
        Iterator<InferredProtein<S>> iter = proteins.iterator();
        while(iter.hasNext()) {
            InferredProtein<S> prot = iter.next();
            if(prot.getPercentCoverage() < coverage) {
                prot.getProtein().setAccepted(false);
                numUnaccepted++;
            }
        }
        log.info("Num proteins below COVERAGE: "+numUnaccepted);
        
        numUnaccepted = 0;
        for(ProteinVertex v: graph.getLeftVertices()) {
            int belowCoverage = 0;
            for(Protein prot: v.getProteins()) {
                if(!prot.isAccepted()) belowCoverage++;
            }
            if(belowCoverage == v.getProteins().size()) {
                v.setAccepted(false);
                numUnaccepted++;
            }
        }
        log.info("Num protein GROUPS below COVERAGE: "+numUnaccepted);
    }

    
    // At this point peptide vertices are also collapsed.  We will count all the peptides in each peptide vertex
    private void markProteinsUnacceptedByMinPeptides(
            BipartiteGraph<ProteinVertex, PeptideVertex> graph, int minPeptides) {
        
        int numUnaccepted = 0;
        for(ProteinVertex v: graph.getLeftVertices()) {
            List<PeptideVertex> peptideGroups = graph.getAdjacentVerticesL(v);
            int peptCount = 0;
            for(PeptideVertex peptV: peptideGroups)
                peptCount += peptV.getPeptides().size();
            if(peptCount < minPeptides) {
                v.setAccepted(false);
                numUnaccepted++;
            }
        }
        log.info("Num protein GROUPS below MIN PEPTIDES: "+numUnaccepted);
    }
    
    private void markProteinsUnacceptedByMinUniquePeptides(
            BipartiteGraph<ProteinVertex, PeptideVertex> graph, int minUniqPeptides) {
        
        int numUnaccepted = 0;
        for(ProteinVertex v: graph.getLeftVertices()) {
            int uniqCount = 0;
            List<PeptideVertex> peptGroups = graph.getAdjacentVerticesL(v);
            for(PeptideVertex peptV: peptGroups) {
                // unique peptides have already been marked. Look at the first peptide in this group
                if(peptV.getPeptides().get(0).isUniqueToProtein()) 
                    uniqCount += peptV.getPeptides().size();
            }
            if(uniqCount < minUniqPeptides) {
                v.setAccepted(false);
                numUnaccepted++;
            }
        }
        log.info("Num protein GROUPS below MIN UNIQUE PEPTIDES: "+numUnaccepted);
    }
    
    private <S extends SpectrumMatch> void removeUnacceptedProteins(
            BipartiteGraph<ProteinVertex, PeptideVertex> graph,
            List<InferredProtein<S>> proteins) {
        
        // remove nodes from the graph
        List<ProteinVertex> toRemove = new ArrayList<ProteinVertex>();
        for(ProteinVertex v: graph.getLeftVertices()) {
            if(!v.isAccepted()) {
                toRemove.add(v);
            }
        }
        for(ProteinVertex v: toRemove) {
            graph.removeLeftVertex(v);
        }
        
        // remove proteins from the list
        Iterator<InferredProtein<S>> iter = proteins.iterator();
        while(iter.hasNext()) {
            InferredProtein<S> prot = iter.next();
            if(!prot.getProtein().isAccepted()) {
                iter.remove();
            }
        }
    }


    @Override
    public <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> List<InferredProtein<S>> inferProteins(List<T> psms) {


        ProteinInferrerMaximal inferrer = new ProteinInferrerMaximal();
        List<InferredProtein<S>> allProteins = inferrer.inferProteins(psms);

        return inferProteins(allProteins, null);
    }

}
