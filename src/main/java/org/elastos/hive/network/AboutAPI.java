package org.elastos.hive.network;

import org.elastos.hive.network.response.NodeCommitHashResponseBody;
import org.elastos.hive.network.response.NodeVersionResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface AboutAPI {
	@GET("/api/v2/about/version")
	Call<NodeVersionResponseBody> version();

	@GET("/api/v2/about/commithash")
	Call<NodeCommitHashResponseBody> commitHash();
}
