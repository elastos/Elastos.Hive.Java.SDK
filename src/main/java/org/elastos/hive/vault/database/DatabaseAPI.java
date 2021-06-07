package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;
import retrofit2.Call;
import retrofit2.http.*;

interface DatabaseAPI {
	@PUT("/api/v2/vault/db/collections/{collectionName}")
	Call<CreateCollectionResult> createCollection(@Path("collectionName") String collectionName);

	@DELETE("/api/v2/vault/db/{collectionName}")
	Call<Void> deleteCollection(@Path("collectionName") String collectionName);

	@POST("/api/v2/vault/db/collection/{collection_name}")
	Call<InsertResult> insert(@Path("collectionName") String collectionName,
							  @Body InsertRequest body);

	@PATCH("/api/v2/vault/db/collection/{collection_name}")
	Call<UpdateResult> update(@Path("collectionName") String collectionName,
							  @Body UpdateRequest body);

	@DELETE("/api/v2/vault/db/collection/{collection_name}")
	Call<Void> delete(@Path("collectionName") String collectionName,
					  @Body DeleteRequest body);

	@GET("/api/v2/vault/db/collection/{collection_name}?op=count")
	Call<CountResult> count(@Path("collectionName") String collectionName,
							@Body CountRequest body);

	@GET("/api/v2/vault/db/{collection_name}")
	Call<QueryResult> find(@Path("collectionName") String collectionName,
						   @Query("filter") KeyValueDict filter,
						   @Query("skip") Long skip,
						   @Query("limit") Long limit);

	@POST("/api/v2/vault/db/query")
	Call<QueryResult> query(@Body QueryRequest body);
}
