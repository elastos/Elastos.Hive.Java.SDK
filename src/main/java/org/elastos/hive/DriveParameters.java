package org.elastos.hive;

import org.elastos.hive.parameters.OneDriveParameters;

/**
 * The base class of Options used to create 'HiveDrive'.
 *
 */
public abstract class DriveParameters {
	/**
	 * Get hive drive type.
	 *
	 * @return The drive type.
	 */
	abstract protected DriveType getDriveType();

	public static DriveParameters createForOneDrive(String applicationId, String scopes, String redirectUrl) {
		return new OneDriveParameters(applicationId, scopes, redirectUrl);
	}
}
