package org.elastos.hive.vault.files;

import java.io.*;
import java.net.HttpURLConnection;
import java.security.InvalidParameterException;
import java.util.List;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.connection.UploadStream;
import org.elastos.hive.connection.UploadWriter;
import org.elastos.hive.exception.*;

/**
 * The wrapper class is to access the files module of the hive node.
 */
public class FilesController {
	private NodeRPCConnection connection;
	private FilesAPI filesAPI;

	/**
	 * Create by the RPC connection.
	 *
	 * @param connection The RPC connection.
	 */
	public FilesController(NodeRPCConnection connection) {
		this.connection = connection;
		this.filesAPI = connection.createService(FilesAPI.class, true);
	}

	/**
	 * Get the upload stream for uploading the content of the file.
	 *
	 * @param path The uploading file path.
	 * @param isPublic The uploading file is for public.
	 * @return The output stream.
	 * @throws HiveException The error comes from the hive node.
	 */
	public UploadStream getUploadStream(String path, boolean isPublic) throws HiveException {
		try {
			String params = isPublic ? "?public=true" : "";
			HttpURLConnection urlConnection = connection.openConnection(FilesAPI.API_UPLOAD + path + params);
			return new UploadStream(urlConnection, urlConnection.getOutputStream());
		} catch (NodeRPCException e) {
			// INFO: The error code and message can be found on stream closing.
			throw new ServerUnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the upload writer for uploading the content of the file.
	 *
	 * @param path The uploading file path.
	 * @param isPublic The uploading file is for public.
	 * @return The writer.
	 * @throws HiveException The error comes from the hive node.
	 */
	public UploadWriter getUploadWriter(String path, boolean isPublic) throws HiveException {
		try {
			String params = isPublic ? "?public=true" : "";
			HttpURLConnection urlConnection = connection.openConnection(FilesAPI.API_UPLOAD + path + params);
			return new UploadWriter(urlConnection, urlConnection.getOutputStream());
		} catch (NodeRPCException e) {
			// INFO: The error code and message can be found on stream closing.
			throw new ServerUnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the input stream for downloading the content of the file.
	 *
	 * @param path The download file path.
	 * @return The input stream.
	 * @throws HiveException The error comes from the hive node.
	 */
	public InputStream getDownloadStream(String path) throws HiveException {
		try {
			return filesAPI.download(path).execute().body().byteStream();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the download reader for downloading the content of the file.
	 *
	 * @param path The download file path.
	 * @return The reader.
	 * @throws HiveException The error comes from the hive node.
	 */
	public Reader getDownloadReader(String path) throws HiveException {
		try {
			return new InputStreamReader(filesAPI.download(path).execute().body().byteStream());
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * List the files on the remote folder.
	 *
	 * @param path The path of the folder.
	 * @return The info. of the file list.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<FileInfo> listChildren(String path) throws HiveException {
		try {
			return filesAPI.listChildren(path).execute().body().getValue();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the details of the remote file.
	 *
	 * @param path The path of the remote file.
	 * @return The details of the remote file.
	 * @throws HiveException The error comes from the hive node.
	 */
	public FileInfo getProperty(String path) throws HiveException {
		try {
			return filesAPI.getMetadata(path).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the hash code of the remote file content.
	 *
	 * @param path The path of the remote file.
	 * @return The hash code.
	 * @throws HiveException The error comes from the hive node.
	 */
	public String getHash(String path) throws HiveException {
		try {
			return filesAPI.getHash(path).execute().body().getHash();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Copy file from srcPath to destPath.
	 *
	 * @param srcPath The source path.
	 * @param destPath The destination path.
	 * @throws HiveException The error comes from the hive node.
	 */
	public void copyFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.copy(srcPath, destPath).execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Move file from srcPath to destPath.
	 *
	 * @param srcPath The source path.
	 * @param destPath The destination path.
	 * @throws HiveException The error comes from the hive node.
	 */
	public void moveFile(String srcPath, String destPath) throws HiveException {
		try {
			filesAPI.move(srcPath, destPath).execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Delete the remote file.
	 *
	 * @param path The path of the file.
	 * @throws HiveException The error comes from the hive node.
	 */
	public void delete(String path) throws HiveException {
		try {
			filesAPI.delete(path).execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
