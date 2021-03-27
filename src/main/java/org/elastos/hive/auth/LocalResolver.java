package org.elastos.hive.auth;

import com.google.gson.Gson;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.utils.CryptoUtil;
import org.elastos.hive.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalResolver implements TokenResolver {
	public static final String TYPE_AUTH_TOKEN = "auth_token";
	public static final String TYPE_BACKUP_CREDENTIAL = "backup_credential";

	private static final String TOKEN_FOLDER = "/tokens";

	private String tokenPath;
	private TokenResolver nextResolver;
	private AuthToken token;

	public LocalResolver(String ownerDid, String providerAddress, String type, String cacheDir) {
		String rootDir = cacheDir + TOKEN_FOLDER;
		File root = new File(rootDir);

		if (!root.exists() && !root.mkdirs()) {
			throw new HiveSdkException("Cannot create token root path.");
		}
		this.tokenPath = String.format("%s/%s", rootDir, CryptoUtil.getSHA256(ownerDid + providerAddress + type));
	}

	@Override
	public AuthToken getToken() throws HiveException {
		if (token == null)
			token = restoreToken();

		if (token == null || token.isExpired()) {
			token = nextResolver.getToken();
			saveToken(token);
		}

		return token;
	}

	@Override
	public void invalidateToken() {
		if (token != null) {
			token = null;
			clearToken();
		}
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		this.nextResolver = resolver;
	}

	private AuthToken restoreToken() {
		Path path = Paths.get(tokenPath);

		if (!Files.exists(path))
			return null;

		try {
			LogUtil.d("Restore access token  from local cache");
			return new Gson().fromJson(new String(Files.readAllBytes(path)), AuthToken.class);
		} catch (IOException e) {
			LogUtil.e("Failed to restore access token from local cache");
			return null;
		}
	}

	private void saveToken(AuthToken token) {
		Path path = Paths.get(tokenPath);

		if (!Files.exists(path)) {
			// TODO: create path.
		}

		try {
			Files.write(path, new Gson().toJson(token).getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			LogUtil.e("Failed to save access token to local cache");
			e.printStackTrace();
		}
	}

	private void clearToken() {
		try {
			Files.deleteIfExists(Paths.get(tokenPath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
