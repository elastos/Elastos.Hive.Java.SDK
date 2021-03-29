package org.elastos.hive.network;

import org.elastos.hive.network.response.NodeCommitHashResponseBody;
import org.elastos.hive.network.response.NodeVersionResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface NodeManageApi {
	@GET(BaseApi.API_VERSION + "/hive/version")
	Call<NodeVersionResponseBody> version();

	@GET(BaseApi.API_VERSION + "/hive/commithash")
	Call<NodeCommitHashResponseBody> commitHash();
}
