package org.elastos.hive.vault.files;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.List;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.connection.UploadOutputStream;
import org.elastos.hive.connection.UploadOutputStreamWriter;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.PathNotExistException;
import org.elastos.hive.exception.NodeRPCException;
import org.elastos.hive.exception.UnauthorizedException;
import org.elastos.hive.exception.UnknownServerException;

public class FilesController {
	private ConnectionManager connection;
	private FilesAPI filesAPI;

	public FilesController(ConnectionManager connection) {
		this.connection = connection;
		this.filesAPI = connection.createService(FilesAPI.class);
	}

	public OutputStream getUploadStream(String path) throws HiveException {
		try {
			HttpURLConnection urlConnection = connection.openConnection(FilesAPI.API_UPLOAD + path);
			return new UploadOutputStream(urlConnection, urlConnection.getOutputStream());
		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException(e);
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public Writer getUploadWriter(String path) throws HiveException {
		try {
			HttpURLConnection urlConnection = connection.openConnection(FilesAPI.API_UPLOAD + path);
			return new UploadOutputStreamWriter(urlConnection, urlConnection.getOutputStream());
		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException(e);
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public InputStream getDownloadStream(String path) throws HiveException {
		try {
			return HiveResponseBody.getResponseStream(filesAPI.download(path).execute(), InputStream.class);
		} catch (NodeRPCException e) {
			if (e.getCode() == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public Reader getDownloadReader(String path) throws HiveException {
		try {
			return HiveResponseBody.getResponseStream(filesAPI.download(path).execute(), Reader.class);
		} catch (NodeRPCException e) {
			if (e.getCode() == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public List<FileInfo> listChildren(String path) throws HiveException {
		try {
			return filesAPI.listChildren(path).execute().body().getValue();
		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public void copyFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.copy(srcPath, destPath).execute();
		} catch (NodeRPCException e) {
			if (e.getCode() == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public void moveFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.move(srcPath, destPath).execute();
		} catch (NodeRPCException e) {
			if (e.getCode() == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public void delete(String path) throws HiveException {
		try {
			filesAPI.delete(path).execute();
		} catch (NodeRPCException e) {
			if (e.getCode() == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public FileInfo getProperty(String path) throws HiveException {
		try {
			return filesAPI.getMetadata(path).execute().body();
		} catch (NodeRPCException e) {
			if (e.getCode() == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public String getHash(String path) throws HiveException {
		try {
			return filesAPI.getHash(path).execute().body().getHash();
		} catch (NodeRPCException e) {
			if (e.getCode() == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == NodeRPCException.NOT_FOUND)
				throw new PathNotExistException(e);
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
