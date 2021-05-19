package org.elastos.hive.vault.files;

import java.util.List;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.UnsupportedMethodException;

public class FilesController {
	private FilesAPI filesAPI = null;

	public FilesController(ServiceEndpoint serviceEndpoint) {
		// TODO:
	}

	public void copyFile(String path) throws HiveException {
		throw new UnsupportedMethodException();
	}

	public void moveFile(String path) throws HiveException {
		throw new UnsupportedMethodException();
	}

	public List<FileInfo> list(String path) throws HiveException {
		FileInfoList list = filesAPI.listChidren(path).execute().body();

		throw new UnsupportedMethodException();
	}

	public FileInfo getProperty(String path) throws HiveException {
		throw new UnsupportedMethodException();
	}

	public String getHash(String path) throws HiveException {
		throw new UnsupportedMethodException();
	}
}
