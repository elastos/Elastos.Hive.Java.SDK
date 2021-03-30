package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class PopMessageRequestBody extends PubsubSubscribeRequestBody {
    @SerializedName("message_limit")
    private int messageLimit;

    public PopMessageRequestBody(String channelName, String pubDid, String pubAppId) {
        super(channelName, pubDid, pubAppId);
    }

    public PopMessageRequestBody setMessageLimit(int limit) {
        this.messageLimit = limit;
        return this;
    }
}
