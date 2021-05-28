package org.elastos.hive.vault.files;

import java.io.IOException;
import java.util.List;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.exception.ExceptionHandler;
import org.elastos.hive.exception.HiveException;

public class FilesController extends ExceptionHandler {
	private ConnectionManager connection;
	private FilesAPI filesAPI;

	public FilesController(ConnectionManager connection) {
		this.connection = connection;
		this.filesAPI = connection.createService(FilesAPI.class);
	}

	public <T> T upload(String path, Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getRequestStream(
				connection.openConnectionWithUrl(FilesAPI.API_UPLOAD + "/" + path, "PUT"),
				resultType);
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public <T> T download(String path, Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getResponseStream(filesAPI.download(path).execute(), resultType);
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public List<FileInfo> listChildren(String path) throws HiveException {
		try {
			return filesAPI.listChildren(path).execute().body().getValue();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public void copyFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.copy(srcPath, destPath).execute();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public void moveFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.move(srcPath, destPath).execute();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public void delete(String path) throws HiveException {
		try {
			filesAPI.delete(path).execute();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public FileInfo getProperty(String path) throws HiveException {
		try {
			return filesAPI.getMetadata(path).execute().body();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}

	public String getHash(String path) throws HiveException {
		try {
			return filesAPI.getHash(path).execute().body().getHash();
		} catch (IOException e) {
			throw super.toHiveException(e);
		}
	}
}
