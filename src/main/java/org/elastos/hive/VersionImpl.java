package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.ResponseHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Response;

class VersionImpl implements Version{
	private ConnectionManager connectionManager;

	VersionImpl(AuthHelper authHelper) {
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<String> getFullName() {
		return CompletableFuture.supplyAsync(() -> getVersionImpl());
	}

	@Override
	public CompletableFuture<String> getLastCommitId() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<ResponseBody> response = this.connectionManager.getVersionApi()
						.getCommitId().execute();

				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				String commit = ret.get("commit_hash").textValue();
				return commit;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
	}

	private String version = null;
	private String getVersionImpl() {
		if(null != version) {
			return version;
		}
		try {
			Response<ResponseBody> response = this.connectionManager.getVersionApi()
					.getVersion().execute();

			JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
			version = ret.get("version").textValue();
			return version;
		} catch (Exception e) {
			HiveException exception = new HiveException(e.getLocalizedMessage());
			throw new CompletionException(exception);
		}
	}

	@Override
	public CompletableFuture<Integer> getMajorNumber() {
		return getFullName().thenApply(s -> getNumber(1));
	}

	@Override
	public CompletableFuture<Integer> getMinorNumber() {
		return getFullName().thenApply(s -> getNumber(2));
	}

	@Override
	public CompletableFuture<Integer> getFixNumber() {
		return getFullName().thenApply(s -> getNumber(3));
	}

	@Override
	public CompletableFuture<Integer> getFullNumber() {
		return CompletableFuture.supplyAsync(() -> 1);
	}

	private int getNumber(int index) {
		Pattern pattern = Pattern.compile("(^\\d)\\.(\\d)\\.(\\d)");
		Matcher matcher = pattern.matcher(version);
		matcher.find();
		return Integer.valueOf(matcher.group(index));
	}

}
