package org.elastos.hive.vendors.onedrive;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.HiveFile;
import org.elastos.hive.exceptions.HiveException;
import org.jetbrains.annotations.NotNull;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

final class OneDriveFile extends HiveFile {
	private final AuthHelper authHelper;

	private String pathName;
	private OneDrive oneDrive;
	private String createdDateTime;
	private String lastModifiedDateTime;
	private boolean isFile;
	private boolean isDirectory;

	public OneDriveFile(OneDrive oneDrive, String pathName) {
		super(oneDrive);
		authHelper = oneDrive.getAuthHelper();
		this.pathName = pathName;
		this.oneDrive = oneDrive;
	}

	@Override
	public @NotNull String getPath() {
		return pathName;
	}

	@Override
	@NotNull
	public String getParentPath() {
		return pathName.substring(0, pathName.lastIndexOf("/") + 1);
	}

	@Override
	@NotNull
	public HiveFile getParent() throws HiveException {
		return super.getDrive().getFile(getParentPath());
	}

	@Override
	@NotNull
	public String getCreatedTimeDate() {
		return createdDateTime;
	}

	@Override
	@NotNull
	public String getLastModifiedDateTime() {
		return lastModifiedDateTime;
	}

	@Override
	public void updateDatetime(@NotNull String newDateTime) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public boolean isFile() {
		return isFile;
	}

	@Override
	public boolean isDirectory() {
		return isDirectory;
	}

	@Override
	public void copyTo(@NotNull String newPath) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void copyTo(@NotNull HiveFile newFile) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void renameTo(@NotNull String newPath) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void renameTo(@NotNull HiveFile newFile) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void delete() throws HiveException {
		authHelper.checkExpired();
		doHttpDelete();
	}

	@Override
	public @NotNull HiveFile[] list() throws HiveException {
		authHelper.checkExpired();
		// TODO
		return null;
	}

	@Override
	public void mkdir(@NotNull String pathName) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void mkdirs(@NotNull String pathName) throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public void close() throws HiveException {
		authHelper.checkExpired();
		// TODO
	}

	@Override
	public String toString() {
		// TODO
		return null;
	}

	void doHttpGet() throws HiveException {
		HttpResponse<JsonNode> response;

		try {
			response = Unirest.get(oneDrive.getRootPath() + pathName)
					.header("accept", "application/json")
					.header("Authorization", "bearer ") // TODO:
					.asJson();
			if (response.getStatus() == 200) {
				// TODO;
			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}

		// TOOD: response;
	}

	void doHttpDelete() throws HiveException {
		HttpResponse<JsonNode> response;

		try {
			response = Unirest.delete(oneDrive.getRootPath() + ":/" + pathName)
					.header("Authorization", "bearer") //TODO
					.asJson();
			if (response.getStatus() == 200) {
				// TODO;
			} else {
				// TODO;
			}
		} catch (UnirestException e) {
			// TODO
			e.printStackTrace();
		}
	}
}
