package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.ResponseBody;
import retrofit2.Response;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.connection.UploadOutputStream;
import org.elastos.hive.connection.UploadOutputStreamWriter;

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.NotFoundException;
import org.elastos.hive.exception.ScriptNotFoundException;
import org.elastos.hive.exception.ServerUnknownException;
import org.elastos.hive.exception.UnauthorizedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.util.Map;

public class ScriptingController {
	private WeakReference<NodeRPCConnection> connection;
	private ScriptingAPI scriptingAPI;

	public ScriptingController(NodeRPCConnection connection) {
		this.connection = new WeakReference<>(connection);
		this.scriptingAPI = connection.createService(ScriptingAPI.class, true);
	}

	public void registerScript(String name,
							Condition condition,
							Executable executable,
							boolean allowAnonymousUser,
							boolean allowAnonymousApp) throws HiveException {
		try {
			scriptingAPI.registerScript(name, new RegScriptParams()
							.setExecutable(executable)
							.setAllowAnonymousUser(allowAnonymousUser)
							.setAllowAnonymousApp(allowAnonymousApp)
							.setCondition(condition))
							.execute().body();

		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException(e);
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new ScriptNotFoundException(e);
			else
				throw new ServerUnknownException(e);

		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public <T> T callScript(String name, JsonNode params,
							String targetDid,
							String targetAppDid,
							Class<T> resultType) throws HiveException {
		try {
			Map<String, Object> map = new ObjectMapper()
							.convertValue(params, new TypeReference<Map<String, Object>>() {});
			String json = scriptingAPI.runScript(name, new RunScriptParams()
							.setContext(new Context()
							.setTargetDid(targetDid)
							.setTargetAppDid(targetAppDid))
							.setParams(map))
							.execute().body().string();

			Object obj = null;
			try {
				if(resultType.isAssignableFrom(String.class)) {
					obj = json;
				} else if(resultType.isAssignableFrom(byte[].class)) {
					obj = json.getBytes();
				} else if(resultType.isAssignableFrom(JsonNode.class)) {
					obj = new ObjectMapper().readTree(json);
				} else if(resultType.isAssignableFrom(Reader.class)) {
					obj = new StringReader(json);
				} else {
					obj = new ObjectMapper().readValue(json, resultType);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Unsupported result Type class.");
			}

			return resultType.cast(obj);

		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException(e);
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new ScriptNotFoundException(e);
			else
				throw new ServerUnknownException(e);
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
			String json =  scriptingAPI.runScriptUrl(name, targetDid, targetAppDid, params)
								.execute().body().string();

			Object obj = null;
			try {
				if(resultType.isAssignableFrom(String.class)) {
					obj = json;
				} else if(resultType.isAssignableFrom(byte[].class)) {
					obj = json.getBytes();
				} else if(resultType.isAssignableFrom(JsonNode.class)) {
					obj = new ObjectMapper().readTree(json);
				} else if(resultType.isAssignableFrom(Reader.class)) {
					obj = new StringReader(json);
				} else {
					obj = new ObjectMapper().readValue(json, resultType);
				}
			} catch (Exception e) {
				throw new RuntimeException("unsupported result type for call script.");
			}
			return resultType.cast(obj);
		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException(e);
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new ScriptNotFoundException(e);
			else
				throw new ServerUnknownException(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}

	public <T> T uploadFile(String transactionId, Class<T> resultType) throws HiveException {
		try {
			HttpURLConnection conn = connection.get().openConnection(
										ScriptingAPI.API_SCRIPT_UPLOAD + "/" + transactionId);
			return getRequestStream(conn, resultType);

		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException(e);
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new NotFoundException(e);
			else
				throw new ServerUnknownException(e);
		} catch (IOException e) {
			// TODO
			throw new NetworkException(e);
		}
	}

	public <T> T downloadFile(String transactionId, Class<T> resultType) throws HiveException {
		try {
			return getResponseStream(scriptingAPI.downloadFile(transactionId).execute(), resultType);
		} catch (NodeRPCException e) {
			int httpCode = e.getCode();

			if (httpCode == NodeRPCException.UNAUTHORIZED)
				throw new UnauthorizedException(e);
			else if (httpCode == NodeRPCException.NOT_FOUND)
				throw new NotFoundException(e);
			else
				throw new ServerUnknownException(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}

	private <T> T getRequestStream(HttpURLConnection connection, Class<T> resultType) throws IOException {
		OutputStream outputStream = connection.getOutputStream();
		if (resultType.isAssignableFrom(OutputStream.class)) {
			UploadOutputStream uploader = new UploadOutputStream(connection, outputStream);
			return resultType.cast(uploader);
		} else if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
			OutputStreamWriter writer = new UploadOutputStreamWriter(connection, outputStream);
			return resultType.cast(writer);
		} else {
			return null;
		}
	}

	@SuppressWarnings("resource")
	private <T> T getResponseStream(Response<ResponseBody> response, Class<T> resultType) {
		ResponseBody body = response.body();
		if (body == null)
			throw new RuntimeException("Failed to get response body");

		if (resultType.isAssignableFrom(Reader.class))
			return resultType.cast(new InputStreamReader(body.byteStream()));
		else if (resultType.isAssignableFrom(InputStream.class))
			return resultType.cast(body.byteStream());
		else
			return null;
	}

	public void unregisterScript(String name) throws HiveException {
		try {
			scriptingAPI.unregisterScript(name).execute();
		} catch (NodeRPCException e) {
			// TODO:
			throw new ServerUnknownException(e);
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
			throw new NetworkException(e);
		}
	}
}
