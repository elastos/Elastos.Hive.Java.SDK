package org.elastos.hive.network;

import org.elastos.hive.Constance;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface NodeApi {

	@GET(Constance.API_PATH + "/hive/version")
	Call<ResponseBody> getVersion();

	@GET(Constance.API_PATH + "/hive/commithash")
	Call<ResponseBody> getCommitId();

	@POST(Constance.API_PATH + "/db/create_collection")
	Call<ResponseBody> createCollection(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/delete_collection")
	Call<ResponseBody> deleteCollection(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/insert_one")
	Call<ResponseBody> insertOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/insert_many")
	Call<ResponseBody> insertMany(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/update_one")
	Call<ResponseBody> updateOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/update_many")
	Call<ResponseBody> updateMany(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/delete_one")
	Call<ResponseBody> deleteOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/delete_many")
	Call<ResponseBody> deleteMany(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/count_documents")
	Call<ResponseBody> countDocs(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/find_one")
	Call<ResponseBody> findOne(@Body RequestBody body);

	@POST(Constance.API_PATH + "/db/find_many")
	Call<ResponseBody> findMany(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/set_subcondition")
	Call<ResponseBody> registerCondition(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/set_script")
	Call<ResponseBody> registerScript(@Body RequestBody body);

	@POST(Constance.API_PATH + "/scripting/run_script")
	Call<ResponseBody> callScript(@Body RequestBody body);

	@Multipart
	@POST(Constance.API_PATH + "/scripting/run_script")
	Call<ResponseBody> callScript(@Part MultipartBody.Part file, @Part("metadata") RequestBody metadata);

	@GET(Constance.API_PATH + "/payment/vault_package_info")
	Call<ResponseBody> getPackageInfo();

	@GET(Constance.API_PATH + "/payment/vault_pricing_plan")
	Call<ResponseBody> getPricingPlan(@Query("name") String name);

	@POST(Constance.API_PATH + "/service/vault/create")
	Call<ResponseBody> createFreeVault();

	@POST(Constance.API_PATH + "/payment/create_vault_package_order")
	Call<ResponseBody> createOrder(@Body RequestBody body);

	@POST(Constance.API_PATH + "/payment/pay_vault_package_order")
	Call<ResponseBody> payOrder(@Body RequestBody body);

	@GET(Constance.API_PATH + "/payment/vault_package_order")
	Call<ResponseBody> getOrderInfo(@Query("order_id") String orderId);

	@GET(Constance.API_PATH + "/payment/vault_package_order_list")
	Call<ResponseBody> getOrderList();

	@GET(Constance.API_PATH + "/service/vault")
	Call<ResponseBody> getServiceInfo();

	@GET(Constance.API_PATH + "/payment/version")
	Call<ResponseBody> getPaymentVersion();
}
