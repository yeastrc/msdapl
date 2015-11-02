/**
 * GenericProteinferIonDAO.java
 * @author Vagisha Sharma
 * Jan 2, 2009
 * @version 1.0
 */
package org.yeastrc.ms.dao.protinfer;

import java.util.List;

import org.yeastrc.ms.domain.protinfer.GenericProteinferIon;

/**
 * 
 */
public interface GenericProteinferIonDAO <T extends GenericProteinferIon<?>>{

    public abstract int save(GenericProteinferIon<?> ion);

    public abstract T load(int pinferIonId);
    
    public abstract List<T> loadIonsForPeptide(int pinferPeptideId);
}