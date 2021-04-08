package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.InvalidParameterException;
import org.elastos.hive.network.ScriptingApi;
import org.elastos.hive.network.model.ScriptContext;
import org.elastos.hive.network.request.CallScriptRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;

import java.io.IOException;

public class ScriptRunner extends ServiceEndpoint {
	private String targetDid;
	private String targetAppDid;
	private ConnectionManager connectionManager;

	public ScriptRunner(AppContext context, String providerAddress,
						String targetDid, String targetAppDid) {
		super(context, providerAddress, targetDid, targetAppDid);
		this.targetDid = targetDid;
		this.targetAppDid = targetAppDid;
		this.connectionManager = super.getConnectionManager();
	}

	public <T> T callScript(String name, JsonNode params, String appDid, Class<T> resultType)
			throws IOException {
		return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
				connectionManager.getScriptingApi()
						.callScript(new CallScriptRequestBody()
								.setName(name)
								.setContext(new ScriptContext()
										.setTargetDid(this.targetDid)
										.setTargetAppDid(appDid))
								.setParams(HiveResponseBody.jsonNode2Map(params)))
						.execute()
		), resultType);
	}

	public <T> T callScriptUrl(String name, String params, String appDid, Class<T> resultType)
			throws IOException {
		return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
				connectionManager.getScriptingApi()
						.callScriptUrl(this.targetDid, appDid, name, params)
						.execute()
		), resultType);
	}

	public <T> T downloadFile(String transactionId, Class<T> resultType)
			throws IOException {
		if (transactionId == null)
			throw new InvalidParameterException("Invalid parameter transactionId.");

		return HiveResponseBody.getResponseStream(
				connectionManager.getScriptingApi()
						.callDownload(transactionId)
						.execute(),
				resultType);
	}

	public <T> T uploadFile(String transactionId, Class<T> resultType)
			throws IOException {
		if (transactionId == null)
			throw new InvalidParameterException("Invalid parameter transactionId.");

		return HiveResponseBody.getRequestStream(
				connectionManager.openConnection(ScriptingApi.API_SCRIPT_UPLOAD + "/" + transactionId),
				resultType);
	}
}
