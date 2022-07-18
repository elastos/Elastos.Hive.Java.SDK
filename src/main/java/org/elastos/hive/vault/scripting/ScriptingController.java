package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.ResponseBody;
import org.elastos.hive.AppContext;
import org.elastos.hive.ScriptRunner;
import org.elastos.hive.exception.*;
import retrofit2.Response;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.connection.UploadStream;
import org.elastos.hive.connection.UploadWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.security.InvalidParameterException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * The wrapper class to access the scripting APIs of the hive node.
 */
public class ScriptingController {
	private WeakReference<NodeRPCConnection> connection;
	private ScriptingAPI scriptingAPI;

	/**
	 * Create by the RPC connection.
	 *
	 * @param connection The RPC connection.
	 */
	public ScriptingController(NodeRPCConnection connection, boolean anonymous) {
		this.connection = new WeakReference<>(connection);
		this.scriptingAPI = connection.createService(ScriptingAPI.class, !anonymous);
	}

	/**
	 * Register a script on the hive node.
	 *
	 * @param name The name of the script.
	 * @param condition The condition of the script. To run the script normally, the condition must be matched.
	 * @param executable The executable represents an executed action.
	 * @param allowAnonymousUser If allow the anonymous user to run the script.
	 * @param allowAnonymousApp If allow the anonymous application to run the script.
	 * @throws HiveException The error comes from the hive node.
	 */
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
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Run the registered script. The runner is not the owner of the script normally.
	 *
	 * @param name The name of the script.
	 * @param params The parameters to run the script.
	 * @param targetDid The owner of the script.
	 * @param targetAppDid The application DID owns the script.
	 * @param resultType Supported type: String, byte[], JsonNode, Reader, other Json relating types.
	 * @param <T> Same as result type.
	 * @return The instance of the result type.
	 * @throws HiveException See {@link HiveException}
	 */
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
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Call the script by the URL parameters.
	 * Same as {@link ScriptingController#callScript(String, JsonNode, String, String, Class)}.
	 *
	 * @param name The name of the script.
	 * @param params The parameters to run the script.
	 * @param targetDid The owner of the script.
	 * @param targetAppDid The application DID owns the script.
	 * @param resultType Supported type: String, byte[], JsonNode, Reader, other Json relating types.
	 * @param <T> Same as resultType.
	 * @return The instance of the result type.
	 * @throws HiveException See {@link HiveException}
	 */
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
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Upload file really by transaction ID.
	 *
	 * @param transactionId The transaction ID got by calling script.
	 * @param resultType Supported type: {@link java.io.OutputStream}, {@link java.io.OutputStreamWriter}
	 * @param <T> The result type.
	 * @return The instance of the result type.
	 * @throws HiveException See {@link HiveException}
	 */
	public <T> T uploadFile(String transactionId, Class<T> resultType) throws HiveException {
		try {
			HttpURLConnection conn = connection.get().openConnection(
										ScriptingAPI.API_SCRIPT_UPLOAD + "/" + transactionId);
			return getRequestStream(conn, resultType);

		} catch (NodeRPCException e) {
			// INFO: The error code and message can be found on stream closing.
			throw new ServerUnknownException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Download file really by transaction ID.
	 *
	 * @param transactionId The transaction ID got by calling script.
	 * @param resultType Supported type: {@link java.io.InputStream}, {@link java.io.InputStreamReader}
	 * @param <T> The result type.
	 * @return The instance of the result type.
	 * @throws HiveException See {@link HiveException}
	 */
	public <T> T downloadFile(String transactionId, Class<T> resultType) throws HiveException {
		try {
			return getResponseStream(scriptingAPI.downloadFile(transactionId).execute(), resultType);
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	private <T> T getRequestStream(HttpURLConnection connection, Class<T> resultType) throws IOException {
		OutputStream outputStream = connection.getOutputStream();
		if (resultType.isAssignableFrom(OutputStream.class)) {
			UploadStream uploader = new UploadStream(connection, outputStream);
			return resultType.cast(uploader);
		} else if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
			OutputStreamWriter writer = new UploadWriter(connection, outputStream);
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

	public static  <T> T downloadFileByHiveUrl(String hiveUrl, Class<T> resultType, AppContext context) throws HiveException {
		HiveUrlInfo info = new HiveUrlInfo(hiveUrl);
		String targetUrl = null;

		// Get the provider address for targetDid.
		try {
			targetUrl = AppContext.getProviderAddress(info.getTargetDid(), null, true).get();
		} catch (InterruptedException|ExecutionException e) {
			throw new NetworkException("Failed to resolve targetDid on the hive url.");
		}

		// Prepare the new scripting service for targetDid with current user's appContext.
		ScriptRunner runner = new ScriptRunner(context, targetUrl);
		ScriptingController controller = new ScriptingController(runner, false);

		JsonNode result = controller.callScriptUrl(info.getScriptName(), info.getParams(),
					info.getTargetDid(), info.getTargetAppDid(), JsonNode.class);
		return controller.downloadFile(controller.getTransactionIdByJsonNode(result), resultType);
	}

	private String getTransactionIdByJsonNode(JsonNode jsonNode) {
		JsonNode node = searchForEntity(jsonNode, "transaction_id");
		if (node == null)
			throw new InvalidParameterException("Can't get transaction id by calling script.");
		return node.asText();
	}

	private JsonNode searchForEntity(JsonNode node, String entityName) {
		// A naive depth-first search implementation using recursion. Useful
		// **only** for small object graphs. This will be inefficient
		// (stack overflow) for finding deeply-nested needles or needles
		// toward the end of a forest with deeply-nested branches.
		if (node == null) {
			return null;
		}
		if (node.has(entityName)) {
			return node.get(entityName);
		}
		if (!node.isContainerNode()) {
			return null;
		}
		for (JsonNode child : node) {
			if (child.isContainerNode()) {
				JsonNode childResult = searchForEntity(child, entityName);
				// The mission node is virtual node.
				if (childResult != null && !childResult.isMissingNode()) {
					return childResult;
				}
			}
		}
		// not found fall through
		return null;
	}

	/**
	 * Unregister the script.
	 *
	 * @param name The name of the script.
	 * @throws HiveException See {@link HiveException}
	 */
	public void unregisterScript(String name) throws HiveException {
		try {
			scriptingAPI.unregisterScript(name).execute();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
