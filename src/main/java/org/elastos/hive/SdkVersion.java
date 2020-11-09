package org.elastos.hive;

public class SdkVersion {

	private static final String SDK_VERSION = "0.1.0";

	private static final String LAST_COMMIT_ID = "0d421cc";

	/**
	 * Get hive sdk version
	 * @return
	 */
	public static String getVersion() {
		return SDK_VERSION;
	}

	/**
	 * Get the last commit ID on the hive sdk git repository
	 * @return
	 */
	public static String getLastCommitId() {
		return LAST_COMMIT_ID;
	}
}
