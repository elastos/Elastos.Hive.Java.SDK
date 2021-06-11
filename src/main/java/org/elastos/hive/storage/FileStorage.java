package org.elastos.hive.storage;

import org.elastos.hive.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Store/load access tokens and credentials to local file storage.
 */
public class FileStorage implements DataStorage {
	private static final String CREDENTIAL_BACKUP = "credential-backup";
	private static final String TOKENS = "tokens";

	private String basePath;

	public FileStorage(String rootPath, String userDid) {
		this.basePath = rootPath + File.separator + getRelativeDidStr(userDid);
		File root = new File(this.basePath);
		if (!root.exists() && !root.mkdirs()) {
			LogUtil.e("Failed to create root folder " + this.basePath);
		}
	}

	private boolean createParentDir(String filePath) {
		File parent = Paths.get(filePath).getParent().toFile();
		if (!parent.exists() && !parent.mkdirs()) {
			LogUtil.e("Failed to create parent for file " + filePath);
			return false;
		}
		return true;
	}

	private String getRelativeDidStr(String did) {
		String[] parts = did.split(":");
		return parts.length >= 3 ? parts[2] : did;
	}

	private String getFileContent(String path) {
		Path p = Paths.get(path);
		if (!Files.exists(p))
			return null;

		try {
			return new String(Files.readAllBytes(p));
		} catch (IOException e) {
			LogUtil.e("Failed to get content from file " + path);
			return null;
		}
	}

	private void saveFileContent(String path, String content) {
		if (!createParentDir(path))
			return;

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

	private String getFilePath(String folder, String fileName) {
		return this.basePath + File.separator + folder + File.separator + fileName;
	}

	@Override
	public String loadBackupCredential(String serviceDid) {
		return getFileContent(getFilePath(CREDENTIAL_BACKUP, getRelativeDidStr(serviceDid)));
	}

	@Override
	public String loadAccessToken(String serviceDid) {
		return getFileContent(getFilePath(TOKENS, getRelativeDidStr(serviceDid)));
	}

	@Override
	public String loadAccessTokenByAddress(String providerAddress) {
		return getFileContent(getFilePath(TOKENS, getSHA256(providerAddress)));
	}

	@Override
	public void storeBackupCredential(String serviceDid, String credential) {
		if (serviceDid == null)
			return;

		saveFileContent(getFilePath(CREDENTIAL_BACKUP, getRelativeDidStr(serviceDid)), credential);
	}

	@Override
	public void storeAccessToken(String serviceDid, String accessToken) {
		if (serviceDid == null)
			return;

		saveFileContent(getFilePath(TOKENS, getRelativeDidStr(serviceDid)), accessToken);
	}

	@Override
	public void storeAccessTokenByAddress(String providerAddress, String accessToken) {
		if (providerAddress == null)
			return;

		saveFileContent(getFilePath(TOKENS, getSHA256(providerAddress)), accessToken);
	}

	@Override
	public void clearBackupCredential(String serviceDid) {
		if (serviceDid == null)
			return;
		removeFile(getFilePath(CREDENTIAL_BACKUP, getRelativeDidStr(serviceDid)));
	}

	@Override
	public void clearAccessToken(String serviceDid) {
		if (serviceDid == null)
			return;
		removeFile(getFilePath(TOKENS, getRelativeDidStr(serviceDid)));
	}

	@Override
	public void clearAccessTokenByAddress(String providerAddress) {
		if (providerAddress == null)
			return;
		removeFile(getFilePath(TOKENS, getSHA256(providerAddress)));
	}

	private String getSHA256(String message) {
        byte[] bytes;

        try {
        	MessageDigest digest = MessageDigest.getInstance("SHA-256");
        	digest.update(message.getBytes("UTF-8"));
        	bytes = digest.digest();
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        String temp = null;

        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1)
            	buffer.append("0");

            buffer.append(temp);
        }
        return buffer.toString();
    }
}
