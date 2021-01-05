package org.elastos.hive;

public class SdkVersion {

	private static final String SDK_VERSION = "2.0.1";

	private static final String LATEST_COMMIT_ID = "fbb0a50";

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