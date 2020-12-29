package org.elastos.hive.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;

public interface VaultApi {
	@POST(Constance.API_PATH + "/service/vault/create")
	Call<ResponseBody> createFreeVault();
}
