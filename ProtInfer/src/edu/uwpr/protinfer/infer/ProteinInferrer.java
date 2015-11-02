package edu.uwpr.protinfer.infer;

import java.util.List;


public interface ProteinInferrer {

    public <S extends SpectrumMatch, T extends PeptideSpectrumMatch<S>> List<InferredProtein<S>> inferProteins(List<T> psmList);
}
