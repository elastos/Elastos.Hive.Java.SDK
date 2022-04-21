package org.elastos.hive.endpoint;

import retrofit2.Call;
import retrofit2.http.GET;

interface AboutAPI {
	@GET("/api/v2/node/version")
	Call<NodeVersion> version();

	@GET("/api/v2/node/commit_id")
	Call<CommitHash> commitId();

	@GET("/api/v2/node/info")
	Call<NodeInfo> info();
}
