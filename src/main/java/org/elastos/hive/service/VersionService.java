package org.elastos.hive.service;

/**
 * Get version information for services.
 */
public interface VersionService {
    /**
     * Get version information for service.
     *
     * @return version information
     */
    Version getVersion();

    /**
     * Get last commit id of git for service source code.
     *
     * @return last commit id
     */
    String getLastCommitId();
}
