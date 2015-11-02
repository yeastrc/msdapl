/**
 * PepXmlProteinMatchBean.java
 * @author Vagisha Sharma
 * Jul 28, 2009
 * @version 1.0
 */
package org.yeastrc.ms.domain.search.pepxml.impl;

import org.yeastrc.ms.domain.search.pepxml.PepXmlProteinMatch;

/**
 * 
 */
public class PepXmlProteinMatchBean implements PepXmlProteinMatch {

    private int proteinMatchId;
    private char ntermResidue;
    private char ctermResidue;
    private int numEnzymaticTermini;
 
    @Override
    public char getCtermResidue() {
        return ctermResidue;
    }

    @Override
    public char getNtermResidue() {
        return ntermResidue;
    }

    @Override
    public int getNumEnzymaticTermini() {
        return numEnzymaticTermini;
    }

    @Override
    public int getProteinMatchId() {
        return proteinMatchId;
    }

    public void setProteinMatchId(int proteinMatchId) {
        this.proteinMatchId = proteinMatchId;
    }

    public void setNtermResidue(char ntermResidue) {
        this.ntermResidue = ntermResidue;
    }

    public void setCtermResidue(char ctermResidue) {
        this.ctermResidue = ctermResidue;
    }

    public void setNumEnzymaticTermini(int numEnzymaticTermini) {
        this.numEnzymaticTermini = numEnzymaticTermini;
    }

}
