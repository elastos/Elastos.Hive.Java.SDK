package org.elastos.hive.storage;

import org.elastos.hive.utils.CryptoUtil;
import org.elastos.hive.utils.LogUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Store/load access tokens and credentials to local file storage.
 */
public class FileStorage implements DataStorage {
	private static final String CREDENTIAL_BACKUP = "credential-backup";
	private static final String CREDENTIAL_SIGNIN = "credential-signin";
	private static final String TOKENS = "tokens";

	private String basePath;

	public FileStorage(String rootPath, String userDid) {
		this.basePath = rootPath + File.pathSeparator + getRelativeDidStr(userDid);
	}

	private String getRelativeDidStr(String did) {
		String[] parts = did.split(":");
		return parts.length >= 3 ? parts[2] : did;
	}

	private String getFileContent(String path) {
		try {
			return new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			LogUtil.e("Failed to get content from file " + path);
			return null;
		}
	}

	private void saveFileContent(String path, String content) {
		try {
			Files.write(Paths.get(path), content.getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			LogUtil.e("Failed to save content to file " + path);
		}
	}

	private void removeFile(String path) {
		try {
			Files.deleteIfExists(Paths.get(path));
		} catch (IOException e) {
			LogUtil.e("Failed to remove file " + path);
		}
	}

	@NotNull
	private String getFilePath(String folder, String fileName) {
		return this.basePath + File.pathSeparator + folder + File.pathSeparator + fileName;
	}

	@NotNull
	private String getFilePath(String fileName) {
		return this.basePath + File.pathSeparator + fileName;
	}

	@Override
	public String loadBackupCredential(String serviceDid) {
		return getFileContent(getFilePath(CREDENTIAL_BACKUP, getRelativeDidStr(serviceDid)));
	}

	@Override
	public String loadSignInCredential() {
		return getFileContent(getFilePath(CREDENTIAL_SIGNIN));
	}

	@Override
	public String loadAccessToken(String serviceDid) {
		return getFileContent(getFilePath(TOKENS, getRelativeDidStr(serviceDid)));
	}

	@Override
	public String loadAccessTokenByAddress(String providerAddress) {
		return getFileContent(getFilePath(TOKENS, CryptoUtil.getSHA256(providerAddress)));
	}

	@Override
	public void storeBackupCredential(String serviceDid, String credential) {
		saveFileContent(getFilePath(CREDENTIAL_BACKUP, getRelativeDidStr(serviceDid)), credential);
	}

	@Override
	public void storeSignInCredential(String credential) {
		saveFileContent(getFilePath(CREDENTIAL_SIGNIN), credential);
	}

	@Override
	public void storeAccessToken(String serviceDid, String accessToken) {
		saveFileContent(getFilePath(TOKENS, getRelativeDidStr(serviceDid)), accessToken);
	}

	@Override
	public void storeAccessTokenByAddress(String providerAddress, String accessToken) {
		saveFileContent(getFilePath(TOKENS, CryptoUtil.getSHA256(providerAddress)), accessToken);
	}

	@Override
	public void clearBackupCredential(String serviceDid) {
		removeFile(getFilePath(CREDENTIAL_BACKUP, getRelativeDidStr(serviceDid)));
	}

	@Override
	public void clearSignInCredential() {
		removeFile(getFilePath(CREDENTIAL_SIGNIN));
	}

	@Override
	public void clearAccessToken(String serviceDid) {
		removeFile(getFilePath(TOKENS, getRelativeDidStr(serviceDid)));
	}

	@Override
	public void clearAccessTokenByAddress(String providerAddress) {
		removeFile(getFilePath(TOKENS, CryptoUtil.getSHA256(providerAddress)));
	}
}
