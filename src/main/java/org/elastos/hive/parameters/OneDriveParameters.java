package org.elastos.hive.parameters;

import org.elastos.hive.DriveParameters;
import org.elastos.hive.DriveType;
import org.jetbrains.annotations.NotNull;

public final class OneDriveParameters extends DriveParameters {
	private @NotNull String clientId;
	private @NotNull String scopes;
	private @NotNull String redirectUrl;

	/**
	 * Class constructor
	 *
	 * @param applicationId The registered application Id.
	 * @param scopes        The all permissions.
	 * @param redirectUrl   The built-in redirect URL
	 */
	public OneDriveParameters(String clientId, String scopes, String redirectUrl) {
		this.scopes = scopes;
		this.clientId = clientId;
		this.redirectUrl = redirectUrl;
	}

	@Override
	protected DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	public @NotNull String getClientId() {
		return this.clientId;
	}

	public @NotNull String getScopes() {
		return this.scopes;
	}

	public @NotNull String getRedirectUrl() {
		return this.redirectUrl;
	}
}
