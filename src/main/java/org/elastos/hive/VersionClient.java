package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.NodeApi;
import org.elastos.hive.utils.ResponseHelper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import okhttp3.ResponseBody;
import retrofit2.Response;

class VersionClient implements Version{
	private ConnectionManager connectionManager;

	VersionClient(AuthHelper authHelper) {
		this.connectionManager = authHelper.getConnectionManager();
	}

	@Override
	public CompletableFuture<String> getVersion() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				NodeApi api = this.connectionManager.getVaultApi();
				Response<ResponseBody> response = api.getVersion().execute();

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
				NodeApi api = this.connectionManager.getVaultApi();
				Response<ResponseBody> response = api.getCommitId().execute();

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
