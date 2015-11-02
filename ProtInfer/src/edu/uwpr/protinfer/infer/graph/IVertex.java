package edu.uwpr.protinfer.infer.graph;

import java.util.List;


public interface IVertex<T extends IVertex<?>> {

    public String getLabel();
    
    public int getComponentIndex();
    
    public void setComponentIndex(int index);
    
    public boolean isVisited();
    
    public void setVisited(boolean visited);
    
    public T combineWith(T vertex);
    
    public T combineWith(List<T> vertices);
    
}
