package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.ResponseBody;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.model.ScriptContext;
import org.elastos.hive.network.request.CallScriptRequestBody;
import org.elastos.hive.network.response.ResponseBodyBase;
import retrofit2.Response;

import java.util.concurrent.CompletionException;

public class ScriptRunner extends ServiceEndpoint {
	private String targetDid;
	private String targetAppDid;
	private ConnectionManager connectionManager;

	public ScriptRunner(AppContext context, String providerAddress, String userDid, String targetDid, String targetAppDid) {
		super(context, providerAddress, userDid);
		this.targetDid = targetDid;
		this.targetAppDid = targetAppDid;
		this.connectionManager = getConnectionManager();
	}

	public <T> T callScript(String name, JsonNode params, String appDid, Class<T> resultType) {
		try {
			Response<ResponseBody> response = this.connectionManager.getScriptingApi()
					.callScript(new CallScriptRequestBody()
							.setName(name)
							.setContext(new ScriptContext()
									.setTargetDid(this.targetDid)
									.setTargetAppDid(appDid)).setParams(params))
					.execute();
			return ResponseBodyBase.getValue(ResponseBodyBase.validateBodyStr(response), resultType);
		} catch (Exception e) {
			throw new CompletionException(new HiveException(e.getMessage()));
		}
	}
}
