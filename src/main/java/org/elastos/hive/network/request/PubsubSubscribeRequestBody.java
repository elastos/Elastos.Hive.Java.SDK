package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class PubsubSubscribeRequestBody extends PubsubRequestBody {
    @SerializedName("pub_did")
    private final String pubDid;
    @SerializedName("pub_app_id")
    private final String pubAppId;

    public PubsubSubscribeRequestBody(String channelName, String pubDid, String pubAppId) {
        super(channelName);
        this.pubDid = pubDid;
        this.pubAppId = pubAppId;
    }
}
