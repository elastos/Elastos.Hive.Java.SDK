package org.elastos.hive.network.request;

public class PushMessageRequestBody extends PubsubRequestBody {
    private final String message;

    public PushMessageRequestBody(String channelName, String message) {
        super(channelName);
        this.message = message;
    }
}
