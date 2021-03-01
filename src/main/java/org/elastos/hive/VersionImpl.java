package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.ResponseHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.ResponseBody;
import retrofit2.Response;

class VersionImpl implements Version{
	private ConnectionManager connectionManager;

	VersionImpl(AuthHelper authHelper) {
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<String> getVersionName() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Response<ResponseBody> response = this.connectionManager.getVersionApi()
						.getVersion().execute();

				JsonNode ret = ResponseHelper.getValue(response, JsonNode.class);
				String version = ret.get("version").textValue();
				return version;
			} catch (Exception e) {
				HiveException exception = new HiveException(e.getLocalizedMessage());
				throw new CompletionException(exception);
			}
		});
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

}
