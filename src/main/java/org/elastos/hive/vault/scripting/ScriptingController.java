package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;

import java.io.IOException;

public class ScriptingController {
	private ServiceEndpoint serviceEndpoint;
	private ScriptingAPI scriptingAPI;

	public ScriptingController(ServiceEndpoint serviceEndpoint) {
		this.serviceEndpoint = serviceEndpoint;
		this.scriptingAPI = serviceEndpoint.getConnectionManager().createService(ScriptingAPI.class, true);
	}

	public void registerScript(String name, Condition condition, Executable executable,
							   boolean allowAnonymousUser, boolean allowAnonymousApp) throws IOException {
		HiveResponseBody.validateBody(
				scriptingAPI.registerScript(new RegisterScriptRequestBody().setName(name)
						.setExecutable(executable)
						.setAllowAnonymousUser(allowAnonymousUser)
						.setAllowAnonymousApp(allowAnonymousApp)
						.setCondition(condition))
						.execute().body());
	}

	public <T> T callScript(String name, JsonNode params,
							String targetDid, String targetAppDid, Class<T> resultType) throws IOException {
		return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
				scriptingAPI.callScript(new CallScriptRequestBody()
						.setName(name)
						.setParams(HiveResponseBody.jsonNode2Map(params)))
						.execute()
		), resultType);
	}

	public <T> T callScriptUrl(String name, String params,
							   String targetDid, String targetAppDid, Class<T> resultType) throws IOException {
		return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
				scriptingAPI.callScriptUrl(targetDid, targetAppDid, name, params)
						.execute()
		), resultType);
	}

	public <T> T uploadFile(String transactionId, Class<T> resultType) throws IOException {
		return HiveResponseBody.getRequestStream(
				serviceEndpoint.getConnectionManager().openConnection(ScriptingAPI.API_SCRIPT_UPLOAD + "/" + transactionId),
				resultType);
	}

	public <T> T downloadFile(String transactionId, Class<T> resultType) throws IOException {
		return HiveResponseBody.getResponseStream(scriptingAPI.callDownload(transactionId).execute(), resultType);
	}
}
