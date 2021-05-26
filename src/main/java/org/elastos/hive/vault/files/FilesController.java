package org.elastos.hive.vault.files;

import java.io.IOException;
import java.util.List;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.exception.HiveException;

public class FilesController {
	private FilesAPI filesAPI;
	private ConnectionManager connectionManager;

	public FilesController(ServiceEndpoint serviceEndpoint) {
		filesAPI = serviceEndpoint.getConnectionManager().createService(FilesAPI.class, true);
		connectionManager = serviceEndpoint.getConnectionManager();
	}

	public <T> T upload(String path, Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getRequestStream(
				connectionManager.openConnection(FilesAPI.API_UPLOAD + "/" + path),
				resultType);
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		return null;
	}

	public <T> T download(String path, Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getResponseStream(filesAPI.download(path).execute(), resultType);
		} catch (IOException e) {
			// TODO:
			e.printStackTrace();
		}
		return null;
	}

	public List<FileInfo> listChildren(String path) throws HiveException {
		try {
			return filesAPI.listChildren(path).execute().body().getFileInfoList();
		} catch (IOException e) {
			// TODO: throw exception.
			e.printStackTrace();
		}
		return null;
	}

	public void copyFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.copy(new FilesCopyRequestBody(srcPath, destPath)).execute();
		} catch (IOException e) {
			// TODO: throw exception.
			e.printStackTrace();
		}
	}

	public void moveFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.move(new FilesMoveRequestBody(srcPath, destPath)).execute();
		} catch (IOException e) {
			// TODO: throw exception.
			e.printStackTrace();
		}
	}

	public void delete(String path) throws HiveException {
		try {
			filesAPI.delete(new FilesDeleteRequestBody(path)).execute();
		} catch (IOException e) {
			// TODO: throw exception.
			e.printStackTrace();
		}
	}

	public FileInfo getProperty(String path) throws HiveException {
		try {
			return filesAPI.properties(path).execute().body().getFileInfo();
		} catch (IOException e) {
			// TODO: throw exception.
			e.printStackTrace();
		}
		return null;
	}

	public String getHash(String path) throws HiveException {
		try {
			return filesAPI.hash(path).execute().body().getSha256();
		} catch (IOException e) {
			// TODO: throw exception.
			e.printStackTrace();
		}
		return null;
	}
}
