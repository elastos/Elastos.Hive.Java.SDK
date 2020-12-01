package org.elastos.hive;

public class SdkVersion {

	private static final String SDK_VERSION = "alpha-v2.0.10";

	private static final String LAST_COMMIT_ID = "6908aaa";

	private static final String COMMIT_BRANCH = "master";

	/**
	 * Get version
	 */
	public static String getVersion() {
		return SDK_VERSION;
	}


	/**
	 * Get last commitId
	 */
	public static String getLastCommitId() {
		return LAST_COMMIT_ID;
	}


	/**
	 * Get last commitId
	 */
	public static String getCommitBranch() {
		return COMMIT_BRANCH;
	}

}