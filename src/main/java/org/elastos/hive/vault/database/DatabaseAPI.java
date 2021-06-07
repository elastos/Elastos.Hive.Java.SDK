package org.elastos.hive.vault.database;

import org.elastos.hive.connection.KeyValueDict;
import retrofit2.Call;
import retrofit2.http.*;

interface DatabaseAPI {
	@PUT("/api/v2/vault/db/collections/{collectionName}")
	Call<CreateCollectionResponse> createCollection(@Path("collectionName") String collectionName);

	@DELETE("/api/v2/vault/db/{collectionName}")
	Call<Void> deleteCollection(@Path("collectionName") String collectionName);

	@POST("/api/v2/vault/db/collection/{collection_name}")
	Call<InsertDocumentsResponse> insertDocuments(@Path("collectionName") String collectionName,
							                      @Body InsertDocumentsRequest body);

	@PATCH("/api/v2/vault/db/collection/{collection_name}")
	Call<UpdateDocumentsResponse> updateDocuments(@Path("collectionName") String collectionName,
							                      @Body UpdateDocumentsRequest body);

	@DELETE("/api/v2/vault/db/collection/{collection_name}")
	Call<Void> deleteDocuments(@Path("collectionName") String collectionName,
							   @Body DeleteDocumentsRequest body);

	@GET("/api/v2/vault/db/collection/{collection_name}?op=count")
	Call<CountDocumentResponse> countDocuments(@Path("collectionName") String collectionName,
							                   @Body CountDocumentRequest body);

	@GET("/api/v2/vault/db/{collection_name}")
	Call<FindDocumentsResponse> findDocuments(@Path("collectionName") String collectionName,
											  @Query("filter") KeyValueDict filter,
											  @Query("skip") Long skip,
											  @Query("limit") Long limit);

	@POST("/api/v2/vault/db/query")
	Call<FindDocumentsResponse> queryDocuments(@Body QueryDocumentsRequest body);
}
