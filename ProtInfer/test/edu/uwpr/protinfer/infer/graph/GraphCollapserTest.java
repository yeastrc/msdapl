package edu.uwpr.protinfer.infer.graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.Protein;

public class GraphCollapserTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testCollapseGraph() {
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
        
        assertEquals(9, graph.getLeftVertices().size());
        assertEquals(10, graph.getRightVertices().size());
        assertEquals(18, graph.getEdgeCount());
        
        GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
        try {collapser.collapseGraph(graph);}
        catch (InvalidVertexException e) {fail("Graph has no invalid vertices.\n\t"+e.getMessage());}
        
        
        assertEquals(7, graph.getLeftVertices().size());
        assertEquals(7, graph.getRightVertices().size());
        
        
        System.out.println("ProteinVertex --");
        List<ProteinVertex> lvList = graph.getLeftVertices();
        Collections.sort(lvList, new Comparator<ProteinVertex>() {
            public int compare(ProteinVertex o1, ProteinVertex o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }});
        for (ProteinVertex v: lvList) {
            System.out.print(v.getLabel()+"  ");
        }
        int i = 0;
        ProteinVertex pv1 = lvList.get(i++);
        assertEquals("1", pv1.getLabel());
        ProteinVertex pv2 = lvList.get(i++);
        assertEquals("2_8", pv2.getLabel());
        ProteinVertex pv3 = lvList.get(i++);
        assertEquals("3", pv3.getLabel());
        ProteinVertex pv4 = lvList.get(i++);
        assertEquals("4_9", pv4.getLabel());
        ProteinVertex pv5 = lvList.get(i++);
        assertEquals("5", pv5.getLabel());
        ProteinVertex pv6 = lvList.get(i++);
        assertEquals("6", pv6.getLabel());
        ProteinVertex pv7 = lvList.get(i++);
        assertEquals("7", pv7.getLabel());
        
        
        List<PeptideVertex> rvList = graph.getRightVertices();
        Collections.sort(rvList, new Comparator<PeptideVertex>() {
            public int compare(PeptideVertex o1, PeptideVertex o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }});
        System.out.println("\nPeptide Vertex -- ");
        for (PeptideVertex v: rvList) {
            System.out.print(v.getLabel()+"  ");
        }
        i = 0;
        PeptideVertex v1 = rvList.get(i++);
        assertEquals("10", v1.getLabel());
        PeptideVertex v2 = rvList.get(i++);
        assertEquals("1_5", v2.getLabel());
        PeptideVertex v3 = rvList.get(i++);
        assertEquals("2", v3.getLabel());
        PeptideVertex v4 = rvList.get(i++);
        assertEquals("3_7_9", v4.getLabel());
        PeptideVertex v5 = rvList.get(i++);
        assertEquals("4", v5.getLabel());
        PeptideVertex v6 = rvList.get(i++);
        assertEquals("6", v6.getLabel());
        PeptideVertex v7 = rvList.get(i++);
        assertEquals("8", v7.getLabel());
        
        
        assertEquals(12, graph.getEdgeCount());
        assertTrue(graph.containsEdge(pv1, v4));
        assertTrue(graph.containsEdge(pv1, v5));
        assertTrue(graph.containsEdge(pv1, v7));
        
        assertTrue(graph.containsEdge(pv2, v7));
        
        assertTrue(graph.containsEdge(pv3, v6));
        
        assertTrue(graph.containsEdge(pv4, v3));
        assertTrue(graph.containsEdge(pv4, v1));
        
        assertTrue(graph.containsEdge(pv5, v5));
        assertTrue(graph.containsEdge(pv5, v7));
        
        assertTrue(graph.containsEdge(pv6, v3));
        assertTrue(graph.containsEdge(pv6, v6));
        
        assertTrue(graph.containsEdge(pv7, v2));
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
