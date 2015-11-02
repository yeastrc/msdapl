package edu.uwpr.protinfer.infer.graph;

import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.Protein;

public class SetCoverFinderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testGetGreedySetCover() {
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = makeGraph();
        GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
        try {collapser.collapseGraph(graph);} catch(InvalidVertexException e){fail("No invalid vertex in graph.\n\t"+e.getMessage());}
    
        SetCoverFinder<ProteinVertex, PeptideVertex> coverFinder = new SetCoverFinder<ProteinVertex, PeptideVertex>();
        List<ProteinVertex> cover = coverFinder.getGreedySetCover(graph);
        for (ProteinVertex v: cover)
            System.out.println(v.getLabel());
        assertEquals(4, cover.size());
    }

    private BipartiteGraph<ProteinVertex, PeptideVertex> makeGraph() {
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = new BipartiteGraph<ProteinVertex, PeptideVertex>();
        addGraphEdge(1, 3, graph);
        addGraphEdge(1, 4, graph);
        addGraphEdge(1, 7, graph);
        addGraphEdge(1, 8, graph);
        addGraphEdge(1, 9, graph);
        
        addGraphEdge(2, 8, graph);
        
        addGraphEdge(3, 6, graph);
        
        addGraphEdge(4, 2, graph);
        addGraphEdge(4, 10, graph);
        
        addGraphEdge(5, 4, graph);
        addGraphEdge(5, 8, graph);
        
        addGraphEdge(6, 2, graph);
        addGraphEdge(6, 6, graph);
        
        addGraphEdge(7, 1, graph);
        addGraphEdge(7, 5, graph);
        
        addGraphEdge(8, 8, graph);
        
        addGraphEdge(9, 2, graph);
        addGraphEdge(9, 10, graph);
        return graph;
    }

    private void addGraphEdge(int protIndex, int peptIndex, BipartiteGraph<ProteinVertex, PeptideVertex> graph) {
        ProteinVertex protVertex = makeProteinVertex(protIndex);
        PeptideVertex peptVertex = makePeptideVertex(peptIndex);
        try {
            graph.addEdge(protVertex, peptVertex);
        }
        catch (InvalidVertexException e) {
            fail("Could not add valid edge between "+protIndex+" --> "+peptIndex+"\n\t"+e.getMessage());
        }
    }
    
    private ProteinVertex makeProteinVertex(int index) {
        return makeProteinVertex(String.valueOf(index), index);
    }
    
    private ProteinVertex makeProteinVertex(String index, int idx) {
        Protein prot = new Protein("accession_"+index, idx);
        ProteinVertex pv = new ProteinVertex(prot);
        return pv;
    }
    
    private PeptideVertex makePeptideVertex(int index) {
        return makePeptideVertex(String.valueOf(index), index);
    }
    
    private PeptideVertex makePeptideVertex(String index, int idx) {
        Peptide pept = new Peptide("peptide_"+index, "peptide_"+index, idx);
        return new PeptideVertex(pept);
    }
}
