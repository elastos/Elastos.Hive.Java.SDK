package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.Authenticator;
import org.elastos.hive.DriveParameters;
import org.elastos.hive.DriveType;
import org.elastos.hive.HiveDrive;
import org.elastos.hive.HiveFile;
import org.elastos.hive.exceptions.HiveException;
import org.elastos.hive.parameters.OneDriveParameters;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * OneDrive class
 */
public final class OneDrive extends HiveDrive {
	private final String API_URL = "https://graph.microsoft.com/v1.0/me/drive";

	private static OneDrive onedriveInstance;
	private final AuthHelper authHelper;

	private String driveId;

	private OneDrive(DriveParameters params) {
		OneDriveParameters parameters = (OneDriveParameters) params;
		authHelper = new OneDriveAuthHelper(parameters.getClientId(),
				parameters.getScopes(),
				parameters.getRedirectUrl());
	}

	public static OneDrive createInstance(DriveParameters parameters) {
		if (onedriveInstance == null) {
			onedriveInstance = new OneDrive(parameters);
		}

		return onedriveInstance;
	}

	public static OneDrive getInstance() {
		return onedriveInstance;
	}

	@Override
	protected AuthHelper getAuthHelper() {
		return authHelper;
	}

	@Override
	public DriveType getDriveType() {
		return DriveType.oneDrive;
	}

	@Override
	public boolean login(@NotNull Authenticator authenticator) throws HiveException {
		authHelper.login(authenticator);
		validateDrive();

		return true; //TODO
	}

	private void validateDrive() throws HiveException {
		try {
			HttpResponse<JsonNode> response = Unirest.get(API_URL)
					.header("Authorization", "bearer " + authHelper.getAuthInfo().getAccessToken())
					.asJson();
			if (response.getStatus() == 200) {
				JSONObject jsonObj = response.getBody().getObject();
				driveId = jsonObj.getString("id");
				System.out.println("driveId: " + driveId);

			} else {
				//TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}
	}

	@Override
	public void logout() {
		// TODO
	}

	@Override
	public @NotNull HiveFile getRootDir() throws HiveException {
		return getFile("/");
	}

	@Override
	@NotNull
	public HiveFile getFile(@NotNull String pathName) throws HiveException {
		authHelper.checkExpired();

		OneDriveFile file = new OneDriveFile(this, pathName);
		file.doHttpGet();

		return file;
	}

	@Override
	@NotNull
	protected HiveFile createFile(@NotNull String pathName) throws HiveException {
		authHelper.checkExpired();

		// TODO
		return null;
	}

	String getRootPath() {
		return API_URL + "/root";
	}
}
