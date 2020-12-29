package org.elastos.hive;

public class SdkVersion {

	private static final String SDK_VERSION = "v1.1.0";

	private static final String LATEST_COMMIT_ID = "bff4eb8";

	private static final String COMMIT_BRANCH = "master";

	/**
	 * Get version
	 */
	public static String getVersion() {
		return SDK_VERSION;
	}


	/**
	 * Get latest commitId
	 */
	public static String getLatestCommitId() {
		return LATEST_COMMIT_ID;
	}


	/**
	 * Get commit branch
	 */
	public static String getCommitBranch() {
		return COMMIT_BRANCH;
	}

}