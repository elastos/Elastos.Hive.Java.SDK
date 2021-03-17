package org.elastos.hive.auth;

import com.google.gson.Gson;
import org.elastos.hive.connection.model.AuthToken;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.HiveSdkException;
import org.elastos.hive.utils.CryptoUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LocalResolver implements TokenResolver {
	private static final String TOKEN_FOLDER = "/token";

	private String tokenPath;
	private TokenResolver nextResolver;
	private AuthToken token;

	public LocalResolver(String ownerDid, String providerAddress, String rootPath) {
		String rootDir = rootPath + TOKEN_FOLDER;
		File root = new File(rootDir);
		if (!root.exists() && !root.mkdirs()) {
			throw new HiveSdkException("Cannot create token root path.");
		}
		this.tokenPath = String.format("%s/%s", rootDir, CryptoUtil.getSHA256(ownerDid + providerAddress));
	}

	@NotNull
	@Override
	public AuthToken getToken() throws HiveException {
		if (this.token != null)
			return this.token;
		AuthToken authToken = getLocalToken();
		if (authToken == null) {
			authToken = nextResolver.getToken();
			saveToken(authToken);
		}
		return authToken;
	}

	private AuthToken getLocalToken() throws HiveException {
		if (!Files.exists(Paths.get(this.tokenPath))) {
			return null;
		}
		try {
			String content = new String(Files.readAllBytes(Paths.get(this.tokenPath)));
			return new Gson().fromJson(content, AuthToken.class);
		} catch (IOException e) {
			throw new HiveException("Failed to load token from local file.");
		}
	}

	@Override
	public void saveToken() throws HiveException {
		saveToken(this.token);
	}

	private void saveToken(AuthToken token) throws HiveException {
		try {
			Files.write(Paths.get(this.tokenPath), new Gson().toJson(this.token).getBytes(StandardCharsets.UTF_8));
			this.token = token;
		} catch (IOException e) {
			throw new HiveException("Failed to save token to local file.");
		}
	}

	@Override
	public void setNextResolver(TokenResolver resolver) {
		this.nextResolver = resolver;
	}
}
