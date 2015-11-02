package org.yeastrc.ms.domain.analysis.percolator;

import java.util.List;

import org.yeastrc.ms.domain.search.sqtfile.SQTSearchScanIn;

public interface PercolatorSearchScan extends SQTSearchScanIn<PercolatorResultIn> {
    
    public abstract List<PercolatorResultIn> getScanResults();
}
