package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.databind.JsonNode;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.RPCException;
import org.elastos.hive.exception.UnknownServerException;

import java.io.IOException;

public class ScriptingController {
	private ConnectionManager connectionManager;
	private ScriptingAPI scriptingAPI;


	public ScriptingController(ConnectionManager connection) {
		this.connectionManager = connection;
		this.scriptingAPI = connection.createService(ScriptingAPI.class);
	}

	public void registerScript(String name,
							Condition condition, Executable executable,
							boolean allowAnonymousUser,
							boolean allowAnonymousApp) throws HiveException {
		try {
			scriptingAPI.registerScript(name, new RegisterScriptRequest()
							.setExecutable(executable)
							.setAllowAnonymousUser(allowAnonymousUser)
							.setAllowAnonymousApp(allowAnonymousApp)
							.setCondition(condition))
							.execute().body();
		} catch (RPCException e) {
			e.printStackTrace();
			// TODO:
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public <T> T callScript(String name, JsonNode params,
							String targetDid,
							String targetAppDid,
							Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
					scriptingAPI.runScript(name, new CallScriptRequest()
							.setContext(new ScriptContext()
							.setTargetDid(targetDid)
							.setTargetAppDid(targetAppDid))
							.setParams(HiveResponseBody.jsonNode2Map(params)))
							.execute(), false), resultType);
		} catch (RPCException e) {
			// TODO:
			throw new UnknownServerException(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}

	public <T> T callScriptUrl(String name, String params,
							   String targetDid,
							   String targetAppDid,
							   Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getValue(HiveResponseBody.validateBodyStr(
					scriptingAPI.runScriptUrl(name, targetDid, targetAppDid, params)
							.execute(), false), resultType);
		} catch (RPCException e) {
			// TODO:
			throw new UnknownServerException(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}

	public <T> T uploadFile(String transactionId, Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getRequestStream(
					connectionManager.openConnection(ScriptingAPI.API_SCRIPT_UPLOAD + "/" + transactionId),
					resultType);
		} catch (RPCException e) {
			// TODO:
			throw new UnknownServerException(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}

	public <T> T downloadFile(String transactionId, Class<T> resultType) throws HiveException {
		try {
			return HiveResponseBody.getResponseStream(scriptingAPI.downloadFile(transactionId).execute(), resultType);
		} catch (RPCException e) {
			// TODO:
			throw new UnknownServerException(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}
}
