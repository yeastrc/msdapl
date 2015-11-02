package edu.uwpr.protinfer.infer.graph;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class ConnectedComponentFinder {

    
    private int componentIndex = 1;
    
    public int findAllConnectedComponents(IGraph graph) {
        componentIndex = 1;
        
        List<IVertex<?>> vertices = graph.getAllVertices();
        for (int i = 0; i < vertices.size(); i++) {
            IVertex<?> vertex = vertices.get(i);
            if (vertex.isVisited()) {
                continue;
            }
            else {
                dfs(graph, vertex); // this will give us one component
            }
            componentIndex++;
        }
        return componentIndex;
    }
    
    public void printConnectedComponents(IGraph graph) {
        List<IVertex<?>> allVertices = graph.getAllVertices();
        Collections.sort(allVertices, new Comparator<IVertex<?>>() {
            @Override
            public int compare(IVertex<?> o1, IVertex<?> o2) {
                return new Integer(o1.getComponentIndex()).compareTo(new Integer(o2.getComponentIndex()));
            }});
        int currComponent = -1;
        for (IVertex<?> vertex: allVertices) {
            int idx = vertex.getComponentIndex();
            if (idx != currComponent) {
                currComponent = idx;
                System.out.println("COMPONENT: "+currComponent);
            }
            System.out.print(vertex.getLabel()+" --> ");
            List<IVertex<?>> adjVertices = graph.getAdjacentVertices(vertex);
            for (IVertex<?> adj: adjVertices) {
                System.out.print(adj.getLabel()+", ");
            }
            System.out.println();
        }
    }
    
    private void dfs(IGraph graph, IVertex<?> root) {
        Stack<IVertex<?>> stack = new Stack<IVertex<?>>();
        stack.push(root);
        visitVertex(root);
        
        while (!stack.isEmpty()) {
            IVertex<?> vertex = stack.pop();
            List<IVertex<?>> adjVertices = graph.getAdjacentVertices(vertex);
            for (IVertex<?> child: adjVertices) {
                if (child.isVisited())
                    continue;
                stack.push(child);
                visitVertex(child);
            }
        }
    }
    
    private void visitVertex(IVertex<?> vertex) {
        vertex.setVisited(true);
        vertex.setComponentIndex(componentIndex);
    }
}
