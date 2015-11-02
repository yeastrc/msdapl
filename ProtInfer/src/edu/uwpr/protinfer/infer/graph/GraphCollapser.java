package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphCollapser <L extends IVertex<L>, R extends IVertex<R>> {

    private Map<String, List<L>> leftVertexMap;
    private Map<String, List<R>> rightVertexMap;
    private BipartiteGraph<L, R> graph;
    
    public GraphCollapser() {}
    
    public void collapseGraph(BipartiteGraph<L, R> graph) throws InvalidVertexException  {
        this.graph = graph;
        leftVertexMap = new HashMap<String, List<L>>(graph.getLeftVertices().size());
        rightVertexMap = new HashMap<String, List<R>>(graph.getRightVertices().size());
        orderVerticesByAdjacentMembers();
        collapseVertices();
    }

    private void orderVerticesByAdjacentMembers() {
        List<L> nodesL = graph.getLeftVertices();
        for (L vertex: nodesL) {
            String adjSign = getVertexSignature(vertex);
            if (leftVertexMap.containsKey(adjSign)) {
                leftVertexMap.get(adjSign).add(vertex);
            }
            else {
                List<L> vertices = new ArrayList<L>();
                vertices.add(vertex);
                leftVertexMap.put(adjSign, vertices);
            }
        }
        
        List<R> nodesR = graph.getRightVertices();
        for (R vertex: nodesR) {
            String nodeSign = getVertexSignature(vertex);
            if (rightVertexMap.containsKey(nodeSign)) {
                rightVertexMap.get(nodeSign).add(vertex);
            }
            else {
                List<R> vertices = new ArrayList<R>();
                vertices.add(vertex);
                rightVertexMap.put(nodeSign, vertices);
            }
        }
    }
    
    private String getVertexSignature(IVertex<?> v) {
        StringBuilder buf = new StringBuilder();
        List<IVertex<?>> adjV = graph.getAdjacentVertices(v);
        Collections.sort(adjV, new Comparator<IVertex<?>>() {
            public int compare(IVertex<?> o1, IVertex<?> o2) {
                return o1.getLabel().compareTo(o2.getLabel());
            }});
        for (IVertex<?> adj: adjV) {
            buf.append(adj.getLabel()+",");
        }
        return buf.toString();
    }
    
    private void collapseVertices() throws InvalidVertexException {
        // replace each collapsed node with a single node
        Set<String> keys = leftVertexMap.keySet();
        for (String key: keys) {
            List<L> toCombine = leftVertexMap.get(key);
            if (toCombine.size() > 1) {
                L newVertex = toCombine.get(0).combineWith(toCombine.subList(1, toCombine.size()));
                
                graph.addLeftVertex(newVertex);
                List<R> adjV = graph.getAdjacentVerticesL(toCombine.get(0));
                for(R adj: adjV) {
                    graph.addEdge(newVertex, adj);
                }
                
                for(L v: toCombine) 
                    graph.removeLeftVertex(v);
                
            }
        }
        
        keys = rightVertexMap.keySet();
        for (String key: keys) {
            List<R> toCombine = rightVertexMap.get(key);
            if (toCombine.size() > 1) {
                R newVertex = toCombine.get(0).combineWith(toCombine.subList(1, toCombine.size()));
                
                graph.addRightVertex(newVertex);
                List<L> adjV = graph.getAdjacentVerticesR(toCombine.get(0));
                for(L adj: adjV) {
                    graph.addEdge(adj, newVertex);
                }
                
                for(R v: toCombine) 
                    graph.removeRightVertex(v);
            }
        }
    }
}
