package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class BipartiteGraphTest2 extends TestCase {

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
        
        TestVertex v3 = new TestVertex("vertex3");
        TestVertex v3_a = graph.addLeftVertex(v3);
        TestVertex v3_b = graph.addLeftVertex(new TestVertex("vertex3"));
        assertTrue(v3_a == v3_b);
    }

    public final void testAddEdge() throws InvalidVertexException {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        
        // add an edge v1 <--> v2
        graph.addEdge(v1, v2);
        assertEquals(2, graph.getAllVertices().size());
        assertEquals(1, graph.getEdgeCount());
        assertEquals(1, graph.getAdjacentVerticesL(v1).size());
        assertEquals(1, graph.getAdjacentVerticesR(v2).size());
        
        // add another edge v1 <--> v3
        TestVertex v3 = new TestVertex("vertex3");
        graph.addEdge(v1, v3);
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        assertEquals(1, graph.getAdjacentVertices(v3).size());
        
        // try to add v1 <--> v2 edge again. It should not get added a second time
        graph.addEdge(v1, v2);
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        
        // add an edge with vertices reversed
        try {graph.addEdge(v2, v1); fail("Cannot add edge with vertices reversed");} 
        catch(InvalidVertexException e){}
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
        
        // add an edge with a new Vertex object having the same label as v1. Edge should not get added
        graph.addEdge(v1, new TestVertex("vertex2"));
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertEquals(2, graph.getAdjacentVertices(v1).size());
        assertEquals(1, graph.getAdjacentVertices(v2).size());
    }
    
    public final void testList() {
        List<TestVertex> vertices = new ArrayList<TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        vertices.add(v1);
        vertices.add(v2);
        assertEquals(2, vertices.size());
        if (!vertices.contains(v1))
            vertices.add(v1); // will not get added again
        assertEquals(2, vertices.size());
        
        TestVertex v1_b = new TestVertex(v1.getLabel());
        assertFalse(v1.equals(v1_b));
        
        if (!vertices.contains(v1_b))
            vertices.add(v1_b); // this will get added since v1.equals(v1_b) == false
        assertEquals(3, vertices.size());
    }
    
    public final void testSet() {
        Set<TestVertex> vertices = new HashSet<TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        vertices.add(v1);
        vertices.add(v2);
        assertEquals(2, vertices.size());
        vertices.add(v1); // will not get added since the set already contains it.
        assertEquals(2, vertices.size());
        vertices.add(new TestVertex("vertex1")); // this will have a different hashValue than v1, so it will get added
        assertEquals(3, vertices.size());
    }
    
    public final void testContainsEdge() throws InvalidVertexException {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        
        graph.addEdge(v1, v2);
        assertTrue(graph.containsEdge(v1, v2));
        assertFalse(graph.containsEdge(v2, v1));
        // try with new vertex objects having same labels as v1 and v2
        // this will return false
        assertFalse(graph.containsEdge(new TestVertex(v1.getLabel()), new TestVertex(v2.getLabel())));
    }
    
    public final void testRemoveVertex() throws InvalidVertexException {
        BipartiteGraph<TestVertex, TestVertex> graph = new BipartiteGraph<TestVertex, TestVertex>();
        TestVertex v1 = new TestVertex("vertex1");
        TestVertex v2 = new TestVertex("vertex2");
        TestVertex v3 = new TestVertex("vertex3");
        graph.addEdge(v1, v2);
        graph.addEdge(v1, v3);
        try {graph.addEdge(v2, v3); fail("v2 is already part of right edges. Cannot add edge v2 --> v3");}
        catch(InvalidVertexException e){}
        
        assertEquals(3, graph.getAllVertices().size());
        assertEquals(2, graph.getEdgeCount());
        assertTrue(graph.containsEdge(v1, v2));
        assertTrue(graph.containsEdge(v1, v3));
        assertFalse(graph.containsEdge(v2, v3));
        
        assertTrue(graph.removeLeftVertex(v1));
        
        assertEquals(2, graph.getAllVertices().size());
        assertEquals(0, graph.getEdgeCount());
        assertFalse(graph.containsEdge(v1, v2));
        assertFalse(graph.containsEdge(v1, v3));
        assertFalse(graph.containsEdge(v2, v3));
    }

    public final void testCombineVertex() throws InvalidVertexException {
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
        
        TestVertex comboVertex = null;
        try {
            comboVertex = graph.combineLeftVertices(v1, v2);
        }
        catch (InvalidVertexException e) {
            e.printStackTrace();
        }
        assertNotNull(comboVertex);
        assertEquals(4, graph.getAllVertices().size());
        assertEquals(3, graph.getEdgeCount());
        assertTrue(graph.containsEdge(comboVertex, v3));
        assertTrue(graph.containsEdge(comboVertex, v4));
        assertTrue(graph.containsEdge(comboVertex, v5));
    }
    
    public static final class TestVertex implements IVertex<TestVertex> {
        private String label;
        private int componentIndex = 0;
        private boolean visited = false;
        
        public TestVertex(String label) {
            this.label = label;
        }
        @Override
        public int getComponentIndex() {
            return componentIndex;
        }
        @Override
        public String getLabel() {
            return label;
        }
        @Override
        public void setComponentIndex(int index) {
            this.componentIndex = index;
        }
        @Override
        public boolean isVisited() {
            return visited;
        }
        @Override
        public void setVisited(boolean visited) {
            this.visited = visited;
        }
        @Override
        public TestVertex combineWith(TestVertex v) {
            TestVertex newVertex = new TestVertex(this.getLabel()+"_"+v.getLabel());
            return newVertex;
        }
        @Override
        public TestVertex combineWith(List<TestVertex> vertices) {
            StringBuilder buf = new StringBuilder();
            buf.append(this.getLabel());
            for(TestVertex v: vertices)
                buf.append(v.getLabel()+"_");
            if(vertices.size() > 0)
                buf.deleteCharAt(buf.length() - 1);
            return new TestVertex(buf.toString());
        }
    }
}
