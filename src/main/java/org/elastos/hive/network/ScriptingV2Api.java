package org.elastos.hive.network;

import retrofit2.Call;
import retrofit2.http.*;

public interface ScriptingV2Api {
	@DELETE(BaseApi.API_VERSION_V2 + "/vault/scripting/{script_name}")
	Call<Void> deleteScript(@Path("script_name") String scriptName);
}
