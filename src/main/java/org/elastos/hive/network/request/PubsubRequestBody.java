package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class PubsubRequestBody {
    @SerializedName("channel_name")
    private final String channelName;

    public PubsubRequestBody(String channelName) {
        this.channelName = channelName;
    }
}
