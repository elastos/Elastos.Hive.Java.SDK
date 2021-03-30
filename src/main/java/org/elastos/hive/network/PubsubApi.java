package org.elastos.hive.network;

import org.elastos.hive.network.request.PopMessageRequestBody;
import org.elastos.hive.network.request.PubsubRequestBody;
import org.elastos.hive.network.request.PubsubSubscribeRequestBody;
import org.elastos.hive.network.request.PushMessageRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.PopMessageResponseBody;
import org.elastos.hive.network.response.PubsubChannelsResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PubsubApi {
    @POST(BaseApi.API_VERSION + "/pubsub/publish")
    Call<HiveResponseBody> publish(@Body PubsubRequestBody body);

    @POST(BaseApi.API_VERSION + "/pubsub/remove")
    Call<HiveResponseBody> remove(@Body PubsubRequestBody body);

    @GET(BaseApi.API_VERSION + "/pubsub/pub/channels")
    Call<PubsubChannelsResponseBody> getPublishedChannels();

    @GET(BaseApi.API_VERSION + "/pubsub/sub/channels")
    Call<PubsubChannelsResponseBody> getSubscribedChannels();

    @POST(BaseApi.API_VERSION + "/pubsub/subscribe")
    Call<HiveResponseBody> subscribe(@Body PubsubSubscribeRequestBody body);

    @POST(BaseApi.API_VERSION + "/pubsub/unsubscribe")
    Call<HiveResponseBody> unsubscribe(@Body PubsubSubscribeRequestBody body);

    @POST(BaseApi.API_VERSION + "/pubsub/push")
    Call<HiveResponseBody> push(@Body PushMessageRequestBody body);

    @POST(BaseApi.API_VERSION + "/pubsub/pop")
    Call<PopMessageResponseBody> pop(@Body PopMessageRequestBody body);
}
