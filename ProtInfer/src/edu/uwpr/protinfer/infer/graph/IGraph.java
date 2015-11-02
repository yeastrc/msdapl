package edu.uwpr.protinfer.infer.graph;

import java.util.List;

public interface IGraph {

    public abstract <I extends IVertex<?>, O extends IVertex<?>> List<O> getAdjacentVertices(I vertex);

    public abstract List<IVertex<?>> getAllVertices();

}