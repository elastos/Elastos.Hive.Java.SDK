package org.elastos.hive.vault.database;

import retrofit2.Call;
import retrofit2.http.*;

interface DatabaseAPI {
	@PUT("/api/v2/vault/db/collections/{collection_name}")
	Call<CreateCollectionResult> createCollection(@Path("collection_name") String collectionName);

	@DELETE("/api/v2/vault/db/{collection_name}")
	Call<Void> deleteCollection(@Path("collection_name") String collectionName);

	@POST("/api/v2/vault/db/collection/{collection_name}")
	Call<InsertResult> insert(@Path("collection_name") String collectionName,
				@Body InsertRequest body);

	@PATCH("/api/v2/vault/db/collection/{collection_name}")
	Call<UpdateResult> update(@Path("collection_name") String collectionName,
				@Body UpdateRequest body);

	@HTTP(method = "DELETE", path = "/api/v2/vault/db/collection/{collection_name}", hasBody = true)
	Call<Void> delete(@Path("collection_name") String collectionName,
				@Body DeleteRequest body);

	@POST("/api/v2/vault/db/collection/{collection_name}?op=count")
	Call<CountResult> count(@Path("collection_name") String collectionName,
				@Body CountRequest body);

	@GET("/api/v2/vault/db/{collection_name}")
	Call<QueryResult> find(@Path("collection_name") String collectionName,
				@Query("filter") String filter,
				@Query("skip") String skip,
				@Query("limit") String limit);

	@POST("/api/v2/vault/db/query")
	Call<QueryResult> query(@Body QueryRequest body);
}
