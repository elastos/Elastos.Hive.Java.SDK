package org.elastos.hive.vault.pubsub;

class PushMessageRequestBody extends PubsubRequestBody {
    private final String message;

    public PushMessageRequestBody(String channelName, String message) {
        super(channelName);
        this.message = message;
    }
}
