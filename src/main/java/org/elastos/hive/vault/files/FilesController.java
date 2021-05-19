package org.elastos.hive.vault.files;

import java.io.IOException;
import java.util.List;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.network.response.HiveResponseBody;

public class FilesController {
	private FilesAPI filesAPI;
	private ConnectionManager connectionManager;

	public FilesController(ServiceEndpoint serviceEndpoint) {
		filesAPI = serviceEndpoint.getConnectionManager().createService(FilesAPI.class, true);
		connectionManager = serviceEndpoint.getConnectionManager();
	}

	public List<FileInfo> listChildren(String path) throws IOException {
		return HiveResponseBody.validateBody(filesAPI.listChildren(path).execute().body()).getFileInfoList();
	}

	public void copyFile(String srcPath, String dstPath) throws IOException {
		HiveResponseBody.validateBody(filesAPI.copy(new FilesCopyRequestBody(srcPath, dstPath)).execute());
	}

	public void moveFile(String srcPath, String dstPath) throws IOException {
		HiveResponseBody.validateBody(filesAPI.move(new FilesMoveRequestBody(srcPath, dstPath)).execute());
	}

	public void delete(String path) throws IOException {
		HiveResponseBody.validateBody(filesAPI.delete(new FilesDeleteRequestBody(path)).execute());
	}

	public FileInfo getProperty(String path) throws IOException {
		return HiveResponseBody.validateBody(filesAPI.properties(path).execute().body()).getFileInfo();
	}

	public String getHash(String path) throws IOException {
		return HiveResponseBody.validateBody(filesAPI.hash(path).execute().body()).getSha256();
	}

	public <T> T upload(String path, Class<T> resultType) throws IOException {
		return HiveResponseBody.getRequestStream(
				connectionManager.openConnection(FilesAPI.API_UPLOAD + "/" + path),
				resultType);
	}

	public <T> T download(String path, Class<T> resultType) throws IOException {
		return HiveResponseBody.getResponseStream(filesAPI.download(path).execute(), resultType);
	}
}
