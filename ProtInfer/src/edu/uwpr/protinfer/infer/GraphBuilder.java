package edu.uwpr.protinfer.infer;

import java.util.List;

import org.apache.log4j.Logger;

import edu.uwpr.protinfer.infer.graph.BipartiteGraph;
import edu.uwpr.protinfer.infer.graph.InvalidVertexException;
import edu.uwpr.protinfer.infer.graph.PeptideVertex;
import edu.uwpr.protinfer.infer.graph.ProteinVertex;

public class GraphBuilder {

    private static final Logger log = Logger.getLogger(GraphBuilder.class);
    
    public <T extends SpectrumMatch> BipartiteGraph<ProteinVertex, PeptideVertex> 
        buildGraph(List<InferredProtein<T>> inferredProteins) {
        
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = new BipartiteGraph<ProteinVertex, PeptideVertex>();
        for (InferredProtein<T> protein: inferredProteins) {
            
            ProteinVertex protVertex = new ProteinVertex(protein.getProtein());
            
            if(protein.getPeptides().size() < 1) {
                log.warn("No Peptides found for protein: "+protVertex.getProteinStringLabel());
            }
            for(PeptideEvidence<T> peptide: protein.getPeptides()) {
                PeptideVertex peptVertex = new PeptideVertex(peptide.getPeptide());
                
                try {
                    if (!graph.addEdge(protVertex, peptVertex)) {
                        log.warn("Could not add edge between: "+protVertex.getProteinStringLabel()+
                                " to "+peptVertex.getPeptideStringLabel());
                    }
                }
                catch (InvalidVertexException e) {
                    e.printStackTrace();
                }
            }
        }
        return graph;
    }
}
