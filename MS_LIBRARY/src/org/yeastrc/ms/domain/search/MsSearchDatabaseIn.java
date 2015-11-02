package org.yeastrc.ms.domain.search;

public interface MsSearchDatabaseIn {

    /**
     * @return the serverAddress
     */
    public abstract String getServerAddress();

    /**
     * @return the serverPath
     */
    public abstract String getServerPath();

    /**
     * The name of the fasta file
     * @return
     */
    public abstract String getDatabaseFileName();
}