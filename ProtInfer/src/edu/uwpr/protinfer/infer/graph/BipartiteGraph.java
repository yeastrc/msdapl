package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BipartiteGraph <L extends IVertex<L>, R extends IVertex<R>> implements IGraph {
    
    private Map<String, L> verticesL;
    private Map<String, R> verticesR;
    
    private Map<String, Set<R>> adjListL;
    private Map<String, Set<L>> adjListR;
    
    
    public BipartiteGraph() {
        verticesL = new HashMap<String, L>();
        verticesR = new HashMap<String, R>();
        adjListL = new HashMap<String, Set<R>>();
        adjListR = new HashMap<String, Set<L>>();
    }
    
    public List<IVertex<?>> getAllVertices() {
        List<IVertex<?>> all = new ArrayList<IVertex<?>>(verticesL.size() + verticesR.size());
        all.addAll(verticesL.values());
        all.addAll(verticesR.values());
        return all;
    }
    
    public List<L> getLeftVertices() {
        List<L> vList = new ArrayList<L>(verticesL.size());
        vList.addAll(verticesL.values());
        return vList;
    }
    
    public List<R> getRightVertices() {
        List<R> vList = new ArrayList<R>(verticesR.size());
        vList.addAll(verticesR.values());
        return vList;
    }
    
    /**
     * If a vertex with the same label is already in the set of "left" vertices
     * it is returned, otherwise the given vertex is added and returned. 
     * @param vertex
     * @return
     * @throws InvalidVertexException if the vertex with the same label is part of 
     * the set of "right" vertices.
     */
    L addLeftVertex(L vertex) throws InvalidVertexException {
         // don't add if this vertex is already in the set of "right" vertices
        if (verticesR.get(vertex.getLabel()) == vertex)
            throw new InvalidVertexException("Vertex "+vertex.getLabel()+" exists in the other set.");
        L v = verticesL.get(vertex.getLabel());
        if (v != null)
            return v;
        verticesL.put(vertex.getLabel(), vertex);
        return vertex;
    }
    
    /**
     * If a vertex with the same label is already in the set of "right" vertices
     * it is returned, otherwise the given vertex is added and returned. 
     * @param vertex
     * @return
     * @throws InvalidVertexException if the vertex with the same label is part of 
     * the set of "left" vertices.
     */
    R addRightVertex(R vertex) throws InvalidVertexException {
        // don't add if this vertex is already in the set of "right" vertices
        if(verticesL.get(vertex.getLabel()) == vertex)
            throw new InvalidVertexException("Vertex "+vertex.getLabel()+" exists in the other set.");
        R v = verticesR.get(vertex.getLabel());
        if (v != null)
            return v;
        verticesR.put(vertex.getLabel(), vertex);
        return vertex;
    }
    
    /**
     * If v1 is not already in the set of "left" vertices it is added.
     * If v2 is not already in the set of "right" vertices it is added.
     * If either v1 or v2 could not be added to the set of vertices, 
     * the edge is not added.
     * An edge is added between v1 and v2 it is does not already exist.
     * @throws InvalidVertexException 
     */
    public boolean addEdge(L v1, R v2) throws InvalidVertexException {
       v1 = addLeftVertex(v1);
       v2 = addRightVertex(v2);
       
       if (v1 == null || v2 == null)
           return false;
       addGraphEdgeL2R(v1, v2);
       addGraphEdgeR2L(v2, v1);
       return true;
    }
    
    private void addGraphEdgeL2R(L from, R to) {
        Set<R> adjVertices = adjListL.get(from.getLabel());
        if (adjVertices == null) {
            adjVertices = new HashSet<R>();
            adjListL.put(from.getLabel(), adjVertices);
        }
        adjVertices.add(to);
    }
    
    private void addGraphEdgeR2L(R from, L to) {
        Set<L> adjVertices = adjListR.get(from.getLabel());
        if (adjVertices == null) {
            adjVertices = new HashSet<L>();
            adjListR.put(from.getLabel(), adjVertices);
        }
        adjVertices.add(to);
    }
    
    public int getEdgeCount() {
        int count = 0;
        for (Set<R> adjV: adjListL.values())
            count += adjV.size();
        return count;
    }
    
    /**
     * Returns a list of vertices adjacent to a vertex in the "left" set of vertices.
     * @param vertex
     * @return
     */
    public List<R> getAdjacentVerticesL(L vertex) {
        if (adjListL.get(vertex.getLabel()) == null)
            return new ArrayList<R>(0);
        List<R> list = new ArrayList<R>(adjListL.get(vertex.getLabel()).size());
        list.addAll(adjListL.get(vertex.getLabel()));
        return list;
    }
    
    public Set<R> getAdjacentSetL(L vertex) {
        if (adjListL.get(vertex.getLabel()) == null)
            return new HashSet<R>(0);
        return adjListL.get(vertex.getLabel());
    }
    
    public List<L> getAdjacentVerticesR(R vertex) {
        if (adjListR.get(vertex.getLabel()) == null)
            return new ArrayList<L>(0);
        List<L> list = new ArrayList<L>(adjListR.get(vertex.getLabel()).size());
        list.addAll(adjListR.get(vertex.getLabel()));
        return list;
    }
    
    public Set<L> getAdjacentSetR(R vertex) {
        if (adjListR.get(vertex.getLabel()) == null)
            return new HashSet<L>(0);
        return adjListR.get(vertex.getLabel());
    }
    
    public <I extends IVertex<?>, O extends IVertex<?>> List<O> getAdjacentVertices(I vertex) {
        if (verticesL.get(vertex.getLabel()) == vertex) 
            return (List<O>) getAdjacentVerticesL((L) vertex);
        else if (verticesR.get(vertex.getLabel()) == vertex)
            return (List<O>) getAdjacentVerticesR((R) vertex);
        return null;
    }
    
    public boolean containsEdge(L v1, R v2) {
        if (adjListL.containsKey(v1.getLabel())) {
            return adjListL.get(v1.getLabel()).contains(v2);
        }
        return false;
    }
    
    public boolean removeLeftVertex(L v) {
        if (!verticesL.containsKey(v.getLabel()))
            return false;
        L toRemove = verticesL.get(v.getLabel());
        Set<R> adj = adjListL.get(v.getLabel());
        for(R av: adj) {
            adjListR.get(av.getLabel()).remove(toRemove);
        }
        adjListL.remove(v.getLabel());
        verticesL.remove(v.getLabel());
        return true;
    }
    
    public boolean removeRightVertex(R v) {
        if (!verticesR.containsKey(v.getLabel()))
            return false;
        R toRemove = verticesR.get(v.getLabel());
        Set<L> adj = adjListR.get(v.getLabel());
        for(L av: adj) {
            adjListL.get(av.getLabel()).remove(toRemove);
        }
        adjListR.remove(v.getLabel());
        verticesR.remove(v.getLabel());
        return true;
    }
    
    public L combineLeftVertices(L v1, L v2) throws InvalidVertexException {
        L v1_o = verticesL.get(v1.getLabel());
        if (v1_o == null) {
            throw new InvalidVertexException("Vertex "+v1.getLabel()+" not found in graph");
        }
        L v2_o = verticesL.get(v2.getLabel());
        if (v2_o == null) {
            throw new InvalidVertexException("Vertex "+v2.getLabel()+" not found in graph");
        }
        
        L v_combined = v1_o.combineWith(v2_o);
        Set<R> adjV = getCommonAdjVerticesL(v1_o, v2_o);
        for (R av: adjV) {
            addEdge(v_combined, av);
        }
        removeLeftVertex(v1_o);
        removeLeftVertex(v2_o);
        return v_combined;
    }
    
    public R combineRightVertices(R v1, R v2) throws InvalidVertexException {
        R v1_o = verticesR.get(v1.getLabel());
        if (v1_o == null) {
            throw new InvalidVertexException("Vertex "+v1.getLabel()+" not found in graph");
        }
        R v2_o = verticesR.get(v2.getLabel());
        if (v2_o == null) {
            throw new InvalidVertexException("Vertex "+v2.getLabel()+" not found in graph");
        }
        
        R v_combined = v1_o.combineWith(v2_o);
        Set<L> adjV = getCommonAdjVerticesR(v1_o, v2_o);
        for (L av: adjV) {
            addEdge(av, v_combined);
        }
        removeRightVertex(v1_o);
        removeRightVertex(v2_o);
        return v_combined;
    }
    
    private Set<R> getCommonAdjVerticesL(L v1, L v2) {
        Set<R> adj1 = adjListL.get(v1.getLabel());
        Set<R> adj2 = adjListL.get(v2.getLabel());
        
        Set<R> allAdj = new HashSet<R>(adj1.size() + adj2.size());
        allAdj.addAll(adj1);
        allAdj.addAll(adj2);
        return allAdj;
    }
    
    private Set<L> getCommonAdjVerticesR(R v1, R v2) {
        Set<L> adj1 = adjListR.get(v1.getLabel());
        Set<L> adj2 = adjListR.get(v2.getLabel());
        
        Set<L> allAdj = new HashSet<L>(adj1.size() + adj2.size());
        allAdj.addAll(adj1);
        allAdj.addAll(adj2);
        return allAdj;
    }
}
