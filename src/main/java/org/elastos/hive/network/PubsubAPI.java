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

public interface PubsubAPI {
    @POST("/api/v1/pubsub/publish")
    Call<HiveResponseBody> publish(@Body PubsubRequestBody body);

    @POST("/api/v1/pubsub/remove")
    Call<HiveResponseBody> remove(@Body PubsubRequestBody body);

    @GET("/api/v1/pubsub/pub/channels")
    Call<PubsubChannelsResponseBody> getPublishedChannels();

    @GET("/api/v1/pubsub/sub/channels")
    Call<PubsubChannelsResponseBody> getSubscribedChannels();

    @POST("/api/v1/pubsub/subscribe")
    Call<HiveResponseBody> subscribe(@Body PubsubSubscribeRequestBody body);

    @POST("/api/v1/pubsub/unsubscribe")
    Call<HiveResponseBody> unsubscribe(@Body PubsubSubscribeRequestBody body);

    @POST("/api/v1/pubsub/push")
    Call<HiveResponseBody> push(@Body PushMessageRequestBody body);

    @POST("/api/v1/pubsub/pop")
    Call<PopMessageResponseBody> pop(@Body PopMessageRequestBody body);
}
