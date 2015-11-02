package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import edu.uwpr.protinfer.infer.Peptide;
import edu.uwpr.protinfer.infer.Protein;

public class ConnectedComponentFinderTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testFindAllConnectedComponents1() {
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = makeGraph();
        
        ConnectedComponentFinder compFinder = new ConnectedComponentFinder();
        compFinder.findAllConnectedComponents(graph);
        
        List<ProteinVertex> lVList = graph.getLeftVertices();
        List<PeptideVertex> rVList = graph.getRightVertices();
        Collections.sort(lVList, new Comparator<ProteinVertex>() {
            public int compare(ProteinVertex o1, ProteinVertex o2) {
                return Integer.valueOf(o1.getComponentIndex()).compareTo(Integer.valueOf(o2.getComponentIndex()));
            }});
        
        Collections.sort(rVList, new Comparator<PeptideVertex>() {
            public int compare(PeptideVertex o1, PeptideVertex o2) {
                return Integer.valueOf(o1.getComponentIndex()).compareTo(Integer.valueOf(o2.getComponentIndex()));
            }});
        
        assertEquals(1, lVList.get(0).getComponentIndex());
        assertEquals(3, lVList.get(lVList.size() - 1).getComponentIndex());
        assertEquals(1, rVList.get(0).getComponentIndex());
        assertEquals(3, rVList.get(rVList.size() - 1).getComponentIndex());
        
        // Component containing ProteinVertex with label "1"
        int compIdx = -1;
        for (ProteinVertex v: lVList) {
            if (v.getLabel().equals("1")){
                compIdx = v.getComponentIndex();
                break;
            }   
        }
        List<String> compLLabels = new ArrayList<String>();
        List<String> compRLabels = new ArrayList<String>();
        for (ProteinVertex v: lVList) {
            if (v.getComponentIndex() == compIdx)  compLLabels.add(v.getLabel());
        }
        for (PeptideVertex v: rVList) {
            if (v.getComponentIndex() == compIdx)  compRLabels.add(v.getLabel());
        }
        Collections.sort(compLLabels);
        Collections.sort(compRLabels);
        assertEquals(4, compLLabels.size());
        assertEquals(5, compRLabels.size());
        assertEquals("1", compLLabels.get(0));
        assertEquals("2", compLLabels.get(1));
        assertEquals("5", compLLabels.get(2));
        assertEquals("8", compLLabels.get(3));
        assertEquals("3", compRLabels.get(0));
        assertEquals("4", compRLabels.get(1));
        assertEquals("7", compRLabels.get(2));
        assertEquals("8", compRLabels.get(3));
        assertEquals("9", compRLabels.get(4));
        
        // Component containing ProteinVertex with label "3"
        compIdx = -1;
        for (ProteinVertex v: lVList) {
            if (v.getLabel().equals("3")){
                compIdx = v.getComponentIndex();
                break;
            }   
        }
        compLLabels = new ArrayList<String>();
        compRLabels = new ArrayList<String>();
        for (ProteinVertex v: lVList) {
            if (v.getComponentIndex() == compIdx)  compLLabels.add(v.getLabel());
        }
        for (PeptideVertex v: rVList) {
            if (v.getComponentIndex() == compIdx)  compRLabels.add(v.getLabel());
        }
        Collections.sort(compLLabels);
        Collections.sort(compRLabels);
        assertEquals(4, compLLabels.size());
        assertEquals(3, compRLabels.size());
        assertEquals("3", compLLabels.get(0));
        assertEquals("4", compLLabels.get(1));
        assertEquals("6", compLLabels.get(2));
        assertEquals("9", compLLabels.get(3));
        assertEquals("10", compRLabels.get(0));
        assertEquals("2", compRLabels.get(1));
        assertEquals("6", compRLabels.get(2));
        
        // Component containing ProteinVertex with label "7"
        compIdx = -1;
        for (ProteinVertex v: lVList) {
            if (v.getLabel().equals("7")){
                compIdx = v.getComponentIndex();
                break;
            }   
        }
        compLLabels = new ArrayList<String>();
        compRLabels = new ArrayList<String>();
        for (ProteinVertex v: lVList) {
            if (v.getComponentIndex() == compIdx)  compLLabels.add(v.getLabel());
        }
        for (PeptideVertex v: rVList) {
            if (v.getComponentIndex() == compIdx)  compRLabels.add(v.getLabel());
        }
        Collections.sort(compLLabels);
        Collections.sort(compRLabels);
        assertEquals(1, compLLabels.size());
        assertEquals(2, compRLabels.size());
        assertEquals("7", compLLabels.get(0));
        assertEquals("1", compRLabels.get(0));
        assertEquals("5", compRLabels.get(1));
    }
    
    public final void testFindAllConnectedComponents2() {
        BipartiteGraph<ProteinVertex, PeptideVertex> graph = makeGraph();
        GraphCollapser<ProteinVertex, PeptideVertex> collapser = new GraphCollapser<ProteinVertex, PeptideVertex>();
        try {collapser.collapseGraph(graph);} catch(InvalidVertexException e){fail("No invalid vertex in graph.\n\t"+e.getMessage());}
        
        ConnectedComponentFinder compFinder = new ConnectedComponentFinder();
        compFinder.findAllConnectedComponents(graph);
        
        List<ProteinVertex> lVList = graph.getLeftVertices();
        List<PeptideVertex> rVList = graph.getRightVertices();
        Collections.sort(lVList, new Comparator<ProteinVertex>() {
            public int compare(ProteinVertex o1, ProteinVertex o2) {
                return Integer.valueOf(o1.getComponentIndex()).compareTo(Integer.valueOf(o2.getComponentIndex()));
            }});
        
        Collections.sort(rVList, new Comparator<PeptideVertex>() {
            public int compare(PeptideVertex o1, PeptideVertex o2) {
                return Integer.valueOf(o1.getComponentIndex()).compareTo(Integer.valueOf(o2.getComponentIndex()));
            }});
        
        assertEquals(1, lVList.get(0).getComponentIndex());
        assertEquals(3, lVList.get(lVList.size() - 1).getComponentIndex());
        assertEquals(1, rVList.get(0).getComponentIndex());
        assertEquals(3, rVList.get(rVList.size() - 1).getComponentIndex());
        
        // Component containing ProteinVertex with label "1"
        int compIdx = -1;
        for (ProteinVertex v: lVList) {
            if (v.getLabel().equals("1")){
                compIdx = v.getComponentIndex();
                break;
            }   
        }
        List<String> compLLabels = new ArrayList<String>();
        List<String> compRLabels = new ArrayList<String>();
        for (ProteinVertex v: lVList) {
            if (v.getComponentIndex() == compIdx)  compLLabels.add(v.getLabel());
        }
        for (PeptideVertex v: rVList) {
            if (v.getComponentIndex() == compIdx)  compRLabels.add(v.getLabel());
        }
        Collections.sort(compLLabels);
        Collections.sort(compRLabels);
        assertEquals(3, compLLabels.size());
        assertEquals(3, compRLabels.size());
        assertEquals("1", compLLabels.get(0));
        assertEquals("2_8", compLLabels.get(1));
        assertEquals("5", compLLabels.get(2));
        assertEquals("3_7_9", compRLabels.get(0));
        assertEquals("4", compRLabels.get(1));
        assertEquals("8", compRLabels.get(2));
        
        // Component containing ProteinVertex with label "3"
        compIdx = -1;
        for (ProteinVertex v: lVList) {
            if (v.getLabel().equals("3")){
                compIdx = v.getComponentIndex();
                break;
            }   
        }
        compLLabels = new ArrayList<String>();
        compRLabels = new ArrayList<String>();
        for (ProteinVertex v: lVList) {
            if (v.getComponentIndex() == compIdx)  compLLabels.add(v.getLabel());
        }
        for (PeptideVertex v: rVList) {
            if (v.getComponentIndex() == compIdx)  compRLabels.add(v.getLabel());
        }
        Collections.sort(compLLabels);
        Collections.sort(compRLabels);
        assertEquals(3, compLLabels.size());
        assertEquals(3, compRLabels.size());
        assertEquals("3", compLLabels.get(0));
        assertEquals("4_9", compLLabels.get(1));
        assertEquals("6", compLLabels.get(2));
        assertEquals("10", compRLabels.get(0));
        assertEquals("2", compRLabels.get(1));
        assertEquals("6", compRLabels.get(2));
        
        // Component containing ProteinVertex with label "7"
        compIdx = -1;
        for (ProteinVertex v: lVList) {
            if (v.getLabel().equals("7")){
                compIdx = v.getComponentIndex();
                break;
            }   
        }
        compLLabels = new ArrayList<String>();
        compRLabels = new ArrayList<String>();
        for (ProteinVertex v: lVList) {
            if (v.getComponentIndex() == compIdx)  compLLabels.add(v.getLabel());
        }
        for (PeptideVertex v: rVList) {
            if (v.getComponentIndex() == compIdx)  compRLabels.add(v.getLabel());
        }
        Collections.sort(compLLabels);
        Collections.sort(compRLabels);
        assertEquals(1, compLLabels.size());
        assertEquals(1, compRLabels.size());
        assertEquals("7", compLLabels.get(0));
        assertEquals("1_5", compRLabels.get(0));
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
