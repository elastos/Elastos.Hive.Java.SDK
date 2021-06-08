package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;
import retrofit2.Call;
import retrofit2.http.*;

interface DatabaseAPI {
	@PUT("/api/v2/vault/db/collections/{collectionName}")
	Call<CreateCollectionResult> createCollection(@Path("collectionName") String collectionName);

	@DELETE("/api/v2/vault/db/{collectionName}")
	Call<Void> deleteCollection(@Path("collectionName") String collectionName);

	@POST("/api/v2/vault/db/collection/{collectionName}")
	Call<InsertResult> insert(@Path("collectionName") String collectionName,
							  @Body InsertRequest body);

	@PATCH("/api/v2/vault/db/collection/{collectionName}")
	Call<UpdateResult> update(@Path("collectionName") String collectionName,
							  @Body UpdateRequest body);

	@HTTP(method = "DELETE", path = "/api/v2/vault/db/collection/{collectionName}", hasBody = true)
	Call<Void> delete(@Path("collectionName") String collectionName,
					  @Body DeleteRequest body);

	@POST("/api/v2/vault/db/collection/{collectionName}?op=count")
	Call<CountResult> count(@Path("collectionName") String collectionName,
							@Body CountRequest body);

	@GET("/api/v2/vault/db/{collectionName}")
	Call<QueryResult> find(@Path("collectionName") String collectionName,
						   @Query("filter") String filter,
						   @Query("skip") String skip,
						   @Query("limit") String limit);

	@POST("/api/v2/vault/db/query")
	Call<QueryResult> query(@Body QueryRequest body);
}
