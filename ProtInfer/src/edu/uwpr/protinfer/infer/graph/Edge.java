package edu.uwpr.protinfer.infer.graph;

public class Edge {

    private IVertex v1;
    private IVertex v2;
    
    public Edge(IVertex v1, IVertex v2) {
        this.v1 = v1;
        this.v2 = v2;
    }
    
    public IVertex getVertex1() {
        return v1;
    }
    
    public IVertex getVertex2() {
        return v2;
    }
    
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || (o.getClass() != this.getClass()))
            return false;
        Edge that = (Edge)o;
        
        return (same(this.v1, that.v1) && same(this.v2, that.v2)) ||
               (same(this.v2, that.v1) && same(this.v1, that.v2)); 
    }
    
    public int hashCode() {
       return v1.getLabel().hashCode() + v2.getLabel().hashCode(); 
    }
    
    private boolean same(IVertex v1, IVertex v2) {
        return v1.getLabel().equals(v2.getLabel());
    }
}
