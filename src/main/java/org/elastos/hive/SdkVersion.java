package org.elastos.hive;

public class SdkVersion {

	private static final String SDK_VERSION = "2.0.4";

	private static final String COMMIT_BRANCH = "master";

	/**
	 * Get version
	 */
	public static String getVersion() {
		return SDK_VERSION;
	}


	/**
	 * Get commit branch
	 */
	public static String getCommitBranch() {
		return COMMIT_BRANCH;
	}

}
