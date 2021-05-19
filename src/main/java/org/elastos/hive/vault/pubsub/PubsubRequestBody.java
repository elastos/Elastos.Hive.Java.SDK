package org.elastos.hive.vault.pubsub;

import com.google.gson.annotations.SerializedName;

class PubsubRequestBody {
    @SerializedName("channel_name")
    private final String channelName;

    public PubsubRequestBody(String channelName) {
        this.channelName = channelName;
    }
}
