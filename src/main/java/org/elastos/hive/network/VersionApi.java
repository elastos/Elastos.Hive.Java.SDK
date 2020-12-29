package org.elastos.hive.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface VersionApi {
	@GET(Constance.API_PATH + "/hive/version")
	Call<ResponseBody> getVersion();

	@GET(Constance.API_PATH + "/hive/commithash")
	Call<ResponseBody> getCommitId();
}
