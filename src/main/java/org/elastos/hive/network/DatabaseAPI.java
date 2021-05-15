package org.elastos.hive.network;

import org.elastos.hive.network.request.*;
import org.elastos.hive.network.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface DatabaseAPI {
	@POST("/api/v1/db/create_collection")
	Call<HiveResponseBody> createCollection(@Body CreateCollectionRequestBody body);

	@POST("/api/v1/db/delete_collection")
	Call<HiveResponseBody> deleteCollection(@Body DeleteCollectionRequestBody body);

	@POST("/api/v1/db/insert_one")
	Call<InsertDocResponseBody> insertOne(@Body InsertDocRequestBody body);

	@POST("/api/v1/db/insert_many")
	Call<InsertDocsResponseBody> insertMany(@Body InsertDocsRequestBody body);

	@POST("/api/v1/db/update_one")
	Call<UpdateDocResponseBody> updateOne(@Body UpdateDocRequestBody body);

	@POST("/api/v1/db/update_many")
	Call<UpdateDocResponseBody> updateMany(@Body UpdateDocRequestBody body);

	@POST("/api/v1/db/delete_one")
	Call<DeleteDocResponseBody> deleteOne(@Body DeleteDocRequestBody body);

	@POST("/api/v1/db/delete_many")
	Call<DeleteDocResponseBody> deleteMany(@Body DeleteDocRequestBody body);

	@POST("/api/v1/db/count_documents")
	Call<CountDocResponseBody> countDocs(@Body CountDocRequestBody body);

	@POST("/api/v1/db/find_one")
	Call<FindDocResponseBody> findOne(@Body FindDocRequestBody body);

	@POST("/api/v1/db/find_many")
	Call<FindDocsResponseBody> findMany(@Body FindDocsRequestBody body);
}
