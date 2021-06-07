package org.elastos.hive.vault.files;

import java.io.IOException;
import java.io.OutputStream;
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
import org.elastos.hive.exception.RPCException;
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
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public Writer getUploadWriter(String path) throws HiveException {
		try {
			HttpURLConnection urlConnection = connection.openConnection(FilesAPI.API_UPLOAD + path);
			return new UploadOutputStreamWriter(urlConnection, urlConnection.getOutputStream());
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public <T> T download(String path, Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getResponseStream(filesAPI.download(path).execute(), resultType);
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public List<FileInfo> listChildren(String path) throws HiveException {
		try {
			return filesAPI.listChildren(path).execute().body().getValue();
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void copyFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.copy(srcPath, destPath).execute();
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void moveFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.move(srcPath, destPath).execute();
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public void delete(String path) throws HiveException {
		try {
			filesAPI.delete(path).execute();
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public FileInfo getProperty(String path) throws HiveException {
		try {
			return filesAPI.getMetadata(path).execute().body();
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}

	public String getHash(String path) throws HiveException {
		try {
			return filesAPI.getHash(path).execute().body().getHash();
		} catch (RPCException e) {
			if (e.getCode() == RPCException.UNAUTHORIZED)
				throw new UnauthorizedException();
			else if (e.getCode() == RPCException.NOT_FOUND)
				throw new PathNotExistException(e.getMessage());
			else
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e.getMessage());
		}
	}
}
