package edu.uwpr.protinfer.infer.graph;

import junit.framework.TestCase;
import edu.uwpr.protinfer.infer.graph.BipartiteGraphTest2.TestVertex;

public class BipartiteGraphTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public final void testAddVertex() throws InvalidVertexException {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        
        TestVertex added = graph.addLeftVertex(v1);
        assertTrue(v1 == added);
        
        added = graph.addLeftVertex(v2);
        assertTrue(v2 == added);
        
        graph.addLeftVertex(added);
        assertEquals(2, graph.getAllVertices().size());
        
        graph.addLeftVertex(v1);
        graph.addLeftVertex(v2);
        assertEquals(2, graph.getAllVertices().size());
        assertEquals(2, graph.getLeftVertices().size());
        assertEquals(0, graph.getRightVertices().size());
        
        TestVertex v3 = new TestVertex("vertex3");
        TestVertex v3_a = graph.addLeftVertex(v3);
        TestVertex v3_b = graph.addLeftVertex(new TestVertex("vertex3"));
        assertTrue(v3_a == v3_b);
    }

    public final void testAddLeftVertex() {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        
        assertEquals(0, graph.getLeftVertices().size());
        try {
            graph.addLeftVertex(v1);
        }
        catch (InvalidVertexException e) {
            fail("Could not add valid vertex");
        }
        assertEquals(1, graph.getLeftVertices().size());
        try {
            graph.addLeftVertex(v1);
        }
        catch (InvalidVertexException e) {
            fail("Could not add valid vertex");
        }
        assertEquals(1, graph.getLeftVertices().size());
        try {
            graph.addLeftVertex(v2);
        }
        catch (InvalidVertexException e) {
            fail("Could not add valid vertex");
        }
        assertEquals(2, graph.getLeftVertices().size());
        
        assertEquals(0, graph.getRightVertices().size());
        // should NOT be able to add the same vertex to the set of right vertices.
        try {
            graph.addRightVertex(v1);
            fail("This vertex is already part of \"Left\" vertices. Should not be able to add it");
        }
        catch (InvalidVertexException e) {}
        
        assertEquals(0, graph.getRightVertices().size());
        assertEquals(2, graph.getLeftVertices().size());
        assertEquals(2, graph.getAllVertices().size());
    }

    public final void testAddRightVertex() {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        
        assertEquals(0, graph.getRightVertices().size());
        try {
            graph.addRightVertex(v1);
        }
        catch (InvalidVertexException e) {
            fail("Could not add valid vertex");
        }
        assertEquals(1, graph.getRightVertices().size());
        try {
            graph.addRightVertex(v1);
        }
        catch (InvalidVertexException e) {
            fail("Could not add valid vertex");
        }
        assertEquals(1, graph.getRightVertices().size());
        try {
            graph.addRightVertex(v2);
        }
        catch (InvalidVertexException e) {
            fail("Could not add valid vertex");
        }
        assertEquals(2, graph.getRightVertices().size());
        
        assertEquals(0, graph.getLeftVertices().size());
        // should NOT be able to add the same vertex to the set of left vertices.
        try {
            graph.addLeftVertex(v1);
            fail("This vertex is already part of \"Right\" vertices. Should not be able to add it");
        }
        catch (InvalidVertexException e) {}
        
        assertEquals(0, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        assertEquals(2, graph.getAllVertices().size());
    }

    public final void testAddEdge()  {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        
        // add an edge v1 <--> v2
        try {graph.addEdge(v1, v2);} catch(InvalidVertexException e){fail("Could not add valid edge");}
        assertEquals(2, graph.getAllVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(1, graph.getRightVertices().size());
        
        // add another edge v1 <--> v3
        TestVertex v3 = new TestVertex("vertex3");
        try {graph.addEdge(v1, v3);} catch(InvalidVertexException e){fail("Could not add valid edge");}
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getAdjacentVertices(v3).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        
        // try to add v1 <--> v2 edge again. It should not get added a second time
        // No exception will be thrown
        try {graph.addEdge(v1, v2);} catch(InvalidVertexException e){fail("Could not add valid edge");}
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        
        // add an edge with vertices reversed (SHOULD NOT ADD THE EDGE)
        try {graph.addEdge(v2, v1); fail("v1 --> v2 already exists. Trying to add v2 --> v1 should fail");} 
        catch(InvalidVertexException e){}
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        
        // add an edge with a new Vertex object having the same label as v2. Edge should not get added
        // There should be no exception
        try {graph.addEdge(v1, new TestVertex("vertex2"));} catch(InvalidVertexException e){fail("Could not add valid edge");}
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
    }

    public final void testContainsEdge() {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        
        try {graph.addEdge(v1, v2);} catch(InvalidVertexException e){fail("Could not add valid edge");}
        assertTrue(graph.containsEdge(v1, v2));
        assertFalse(graph.containsEdge(v2, v1));
        // try with new vertex objects having same labels as v1 and v2
        // this will return false
        assertFalse(graph.containsEdge(new TestVertex(v1.getLabel()), new TestVertex(v2.getLabel())));
    }

    public final void testRemoveVertex() {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        TestVertex v3 = new TestVertex("vertex3");
        TestVertex v4 = new TestVertex("vertex4");
        try {assertTrue(graph.addEdge(v1, v2));} catch(InvalidVertexException e){fail("Could not add valid edge");}
        try {assertTrue(graph.addEdge(v1, v3));} catch(InvalidVertexException e){fail("Could not add valid edge");}
        try {assertFalse(graph.addEdge(v2, v3)); fail("v2 is alreay in left vertices. Should not be able to add edge v2 --> v3");} 
        catch(InvalidVertexException e){}
        try {assertTrue(graph.addEdge(v4, v2));} catch(InvalidVertexException e){fail("Could not add valid edge");}
        
        assertEquals(4, graph.getAllVertices().size());
        assertEquals(3, graph.getEdgeCount());
        assertTrue(graph.containsEdge(v1, v2));
        assertTrue(graph.containsEdge(v1, v3));
        assertTrue(graph.containsEdge(v4, v2));
        
        // This is a different object than the one we put in but it has the same label as v3
        // This should remove v3.
        assertTrue(graph.removeRightVertex(new TestVertex("vertex3"))); 
        assertFalse(graph.removeLeftVertex(v2));
        assertFalse(graph.removeRightVertex(v1));
        assertTrue(graph.removeLeftVertex(v1));
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(1, graph.getRightVertices().size());
        assertEquals(2, graph.getAllVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertFalse(graph.containsEdge(v1, v2));
        assertFalse(graph.containsEdge(v1, v3));
        assertTrue(graph.containsEdge(v4, v2));
    }
    
    public final void testCombineVertices() throws InvalidVertexException {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        TestVertex v3 = new TestVertex("vertex3");
        TestVertex v4 = new TestVertex("vertex4");
        TestVertex v5 = new TestVertex("vertex5");
        graph.addEdge(v1, v3);
        graph.addEdge(v1, v4);
        graph.addEdge(v2, v4);
        graph.addEdge(v2, v5);
        
        assertEquals(5, graph.getAllVertices().size());
        assertTrue(graph.containsEdge(v1, v3));
        assertTrue(graph.containsEdge(v1, v4));
        assertTrue(graph.containsEdge(v2, v4));
        assertTrue(graph.containsEdge(v2, v5));
        
        TestVertex comboVertexL = null;
        try {
            comboVertexL = graph.combineLeftVertices(v1, v2);
        }
        catch (InvalidVertexException e) {
            e.printStackTrace();
        }
        assertNotNull(comboVertexL);
        assertEquals(4, graph.getAllVertices().size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(3, graph.getRightVertices().size());
        assertEquals(3, graph.getEdgeCount());
        assertTrue(graph.containsEdge(comboVertexL, v3));
        assertTrue(graph.containsEdge(comboVertexL, v4));
        assertTrue(graph.containsEdge(comboVertexL, v5));
        
        TestVertex comboVertexR = null;
        try {
            comboVertexR = graph.combineRightVertices(v3, v5);
        }
        catch (InvalidVertexException e) {
            e.printStackTrace();
        }
        assertNotNull(comboVertexR);
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(1, graph.getLeftVertices().size());
        assertEquals(2, graph.getRightVertices().size());
        assertEquals(1, graph.getAdjacentVertices(comboVertexR).size());
        assertEquals(2, graph.getEdgeCount());
        assertTrue(graph.containsEdge(comboVertexL, comboVertexR));
        assertTrue(graph.containsEdge(comboVertexL, v4));
    }
}
