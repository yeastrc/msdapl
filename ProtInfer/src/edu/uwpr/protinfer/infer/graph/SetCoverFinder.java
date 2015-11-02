package edu.uwpr.protinfer.infer.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;


public class SetCoverFinder <L extends Vertex<L>, R extends Vertex<R>>{

    private static final Logger log = Logger.getLogger(SetCoverFinder.class.getName());
    
    // map of R vertex labels and adjacent L vertex labels
    private Map<String, Set<String>> rToLMap;
    
    // For quick lookup of CoverVertices.
    private Map<String, CoverVertex<L,R>> qVerticesMap= new HashMap<String, CoverVertex<L,R>> ();
    
    // Array of adjacency counts. adjCounts[5] will contain a list of vertices with adjacency count = 5
    private List<String>[] adjCounts;
    
    private void initSetCoverFinder(BipartiteGraph<L, R> graph) {
        int maxAdjCount = initMaps(graph);
        initUniqAdjacencyCounts();
        initAdjacencyCountArray(maxAdjCount);
    }

    private void initUniqAdjacencyCounts() {
        for(CoverVertex<L,R> qv: qVerticesMap.values()) {
            Set<String> adjR = qv.getAdjRLabels();
            int uniqCount = 0;
            for(String r: adjR) {
                if(rToLMap.get(r).size() == 1) {
                    uniqCount++;
                }
            }
            qv.setUniqAdjacentcount(uniqCount);
        }
    }
    
    private int initMaps(BipartiteGraph<L, R> graph) {

        List<L> verticesL = graph.getLeftVertices();
        rToLMap = new HashMap<String, Set<String>>(graph.getRightVertices().size());
        qVerticesMap= new HashMap<String, CoverVertex<L,R>> (verticesL.size());
        
        int maxAdjCount = 0;
        for (L vertex: verticesL) {
            CoverVertex<L, R> qv = new CoverVertex<L,R>(vertex, graph);
            qVerticesMap.put(vertex.getLabel(), qv);
            
            Set<String> adjRLabels = qv.getAdjRLabels();
            for (String rLabel: adjRLabels) {
                Set<String> adjLLabels = rToLMap.get(rLabel);
                if (adjLLabels == null) {
                    adjLLabels = new HashSet<String>();
                    rToLMap.put(rLabel, adjLLabels);
                }
                adjLLabels.add(vertex.getLabel());
            }
            
            maxAdjCount = Math.max(maxAdjCount, adjRLabels.size());
        }
        return maxAdjCount;
    }
    
    private void initAdjacencyCountArray(int maxAdjCount) {
        adjCounts = new ArrayList[maxAdjCount+1];
        for(CoverVertex<L,R> qv: qVerticesMap.values()) {
            
            List<String> qvWithAdjCount = adjCounts[qv.getAdjacentCount()];
            if (qvWithAdjCount == null) {
                qvWithAdjCount = new ArrayList<String>();
                adjCounts[qv.getAdjacentCount()] = qvWithAdjCount;
            }
            qvWithAdjCount.add(qv.getVertex().getLabel());
        }
    }
    
    public List<L> getGreedySetCover(BipartiteGraph<L, R> graph) {
        
        initSetCoverFinder(graph);
        
        List<L> setCover = new ArrayList<L>();
        
        // as a first step add all ALL L nodes that have at least one unique R node (i.e a R node that 
        // is connected only to this L node) to the set cover
        addVerticesWithUniqAdjacent(setCover);
        
        
        // now go over the remaining vertices
        addRemainingSetCoverVertices(setCover);
        
        return setCover;
    }

    private void addRemainingSetCoverVertices(List<L> setCover) {
        
        for (int i = adjCounts.length - 1 ; i >= 0; i--) {
            
            List<String> qvWithAdjCount = adjCounts[i];
            
            // if there are no vertices with this adjacency count go to the next iteration
            if (qvWithAdjCount == null || qvWithAdjCount.size() == 0)
                continue;
            
            // This is where we resolve ties.
            // getBestVertex will also sort so that the best vertex is at index 0.
            String lLabel = getBestVertex(qvWithAdjCount);
//            String lLabel = qvWithAdjCount.get(0);
            qvWithAdjCount.remove(0);
            
            // add this to the set cover
            CoverVertex<L,R> qv = qVerticesMap.get(lLabel);
            setCover.add(qv.getVertex());
            
            // get all the labels of all vertices adjacent to this vertex
            // and remove them from the graph. 
            for (String rLabel: qv.getAdjRLabels()) {
                Set<String> lLabels = rToLMap.get(rLabel);
                // other L vertices adjacent to this R vertex
                for (String label: lLabels) {
                    CoverVertex<L, R> qvl = qVerticesMap.get(label);
                    if (qvl == qv)  continue;
                    removeAdjacentVertx(qvl, rLabel);
                }
            }
            
            if (qvWithAdjCount.size() > 0) i++;
        }
    }

    private void addVerticesWithUniqAdjacent(List<L> setCover) {
        
        Iterator<CoverVertex<L,R>> iter = qVerticesMap.values().iterator();
        while(iter.hasNext()) {
            CoverVertex<L,R> qv = iter.next();
            if(qv.getUniqAdjacentcount() > 0) {
                setCover.add(qv.getVertex());
            }
        }
        
        // remove the vertices already added (and their adjacent vertices) from the graph
        for(L v: setCover) {
            CoverVertex<L,R> qv = qVerticesMap.get(v.getLabel());
            // get all the labels of all vertices adjacent to this vertex
            // and remove them from the graph. 
            for (String rLabel: qv.getAdjRLabels()) {
                Set<String> lLabels = rToLMap.get(rLabel);
                // other L vertices adjacent to this R vertex
                for (String label: lLabels) {
                    CoverVertex<L, R> qvl = qVerticesMap.get(label);
                    if (qvl == qv)  continue;
                    removeAdjacentVertx(qvl, rLabel);
                }
            }
        }
        
        // remove these vertices from the adjacency count list
        // This should be done AFTER the previous step (removing all R vertices for vertices in set cover
        // and updating L vertices that matched to them).
        for(L v: setCover) {
            CoverVertex<L,R> qv = qVerticesMap.get(v.getLabel());
            List<String> qvWithAdjCount = adjCounts[qv.getAdjacentCount()];
            Iterator<String> iterL = qvWithAdjCount.iterator();
            boolean removed = false;
            while(iterL.hasNext()) {
                String label = iterL.next();
                if(label.equals(qv.getVertex().getLabel())) {
                   iterL.remove();
                   removed = true;
                   break;
                }
            }
            if(!removed) throw new IllegalArgumentException("Vertex not removed; "+qv.getVertex().getLabel());
        }
    }
    
    
    // the purpose is to sort the vertices for a particular adjacency count
    // such that the vertex with the maximum number of unique nodes is at the 
    // top of the list
    // For example: adjCounts[5].get(0) should return the vertex with the maximum
    // number of unique nodes among all vertices with adjacency count of 5
    private String getBestVertex(List<String> qvWithAdjCount) {
        
        Collections.sort(qvWithAdjCount, new Comparator<String>() {
            
            // NOTE: we are sorting in reverse order. 
            public int compare(String vlabel1, String vlabel2) {
                CoverVertex<L, R> qv1 = qVerticesMap.get(vlabel1);
                CoverVertex<L, R> qv2 = qVerticesMap.get(vlabel2);
                // first look at the unique adjacent count
                if(qv1.origUniqAdjCount > qv2.origUniqAdjCount)
                    return -1;
                if(qv1.origUniqAdjCount < qv2.origUniqAdjCount)
                    return 1;
                
                // now look at the number of vertices that were
                // adjacent to this vertex when the graph was initialized
                if(qv1.origAdjCount > qv2.origAdjCount)
                    return -1;
                if(qv1.origAdjCount < qv2.origAdjCount)
                    return 1;
                
                // if there is still a tie return the vertex with fewer member proteins
                if(qv1.getVertex().getMemberCount() < qv2.getVertex().getMemberCount())
                    return -1;
                if(qv1.getVertex().getMemberCount() > qv2.getVertex().getMemberCount())
                    return 1;
                
                // if there is still a tie compare the labels (this should be the
                // concatenation of the nrseq protein ids for protein vertices
                log.debug("Looking at vertex labels to resolve ties!!! Labels: "
                        +qv1.getVertex().getLabel()+" AND "+qv2.getVertex().getLabel());
                return qv1.getVertex().getLabel().compareTo(qv2.getVertex().getLabel());
                
            }});
        return qvWithAdjCount.get(0);
    }


    private void removeAdjacentVertx(CoverVertex<L,R> qv, String adjLabel) {
        int oldAdjCount = qv.getAdjacentCount();
        qv.removeAdjLabel(adjLabel);
        
        // adjacency count has changed, remove it from the original bin
        this.adjCounts[oldAdjCount].remove(qv.getVertex().getLabel());
        
        // if no adjacent vertices are left, remove this vertex
        if (qv.getAdjacentCount() == 0) {
            qVerticesMap.remove(qv.getVertex().getLabel());
            return;
        }
        
        // put this in the appropriate adjacency count bin
        List<String> adjList = this.adjCounts[qv.getAdjacentCount()];
        // the bin might not exist yet, we my need to create a new one.
        if (adjList == null) {
            adjList = new ArrayList<String>();
            adjCounts[qv.getAdjacentCount()] = adjList;
        }
        adjList.add(qv.getVertex().getLabel());
    }
    
    private static final class CoverVertex <L extends Vertex<L>, R extends Vertex<R>>{
        
        private final L vertex;
        private Set<String> adjRLabels;
        private int origUniqAdjCount = 0;
        private int origAdjCount = 0;
        
        public CoverVertex(L vertex, BipartiteGraph<L, R> graph) {
            this.vertex = vertex;
            Set<R> adjR = graph.getAdjacentSetL(vertex);
            adjRLabels = new HashSet<String>(adjR.size());
            for (R adj: adjR) 
                adjRLabels.add(adj.getLabel());
            origAdjCount = getAdjacentCount();
        }
        
        public int getAdjacentCount() {
            return adjRLabels.size();
        }
        
        // returns the number of vertices that were uniquely adjacent to this vertex
        // when the graph was built
        public int getUniqAdjacentcount() {
            return origUniqAdjCount;
        }
        
        public void setUniqAdjacentcount(int count) {
            this.origUniqAdjCount = count;
        }
        
        public boolean hasUniqueAdjacent() {
            return origUniqAdjCount > 0;
        }
        
        // returns the number of vertices that were adjacent to this vertex when the graph was built
        public int getOrigAdjCount() {
            return origAdjCount;
        }
        
        public void removeAdjLabel(String label) {
            adjRLabels.remove(label);
        }
        
        public Set<String> getAdjRLabels() {
            return adjRLabels;
        }
        
        public L getVertex() {
            return vertex;
        }
        
    }
}
