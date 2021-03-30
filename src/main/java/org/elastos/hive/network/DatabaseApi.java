package org.elastos.hive.network;

import org.elastos.hive.network.request.*;
import org.elastos.hive.network.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DatabaseApi {
	@POST(BaseApi.API_VERSION + "/db/create_collection")
	Call<HiveResponseBody> createCollection(@Body CreateCollectionRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/delete_collection")
	Call<HiveResponseBody> deleteCollection(@Body DeleteCollectionRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/insert_one")
	Call<InsertDocResponseBody> insertOne(@Body InsertDocRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/insert_many")
	Call<InsertDocsResponseBody> insertMany(@Body InsertDocsRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/update_one")
	Call<UpdateDocResponseBody> updateOne(@Body UpdateDocRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/update_many")
	Call<UpdateDocResponseBody> updateMany(@Body UpdateDocRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/delete_one")
	Call<DeleteDocResponseBody> deleteOne(@Body DeleteDocRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/delete_many")
	Call<DeleteDocResponseBody> deleteMany(@Body DeleteDocRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/count_documents")
	Call<CountDocResponseBody> countDocs(@Body CountDocRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/find_one")
	Call<FindDocResponseBody> findOne(@Body FindDocRequestBody body);

	@POST(BaseApi.API_VERSION + "/db/find_many")
	Call<FindDocsResponseBody> findMany(@Body FindDocsRequestBody body);
}
