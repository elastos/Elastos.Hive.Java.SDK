package org.elastos.hive.network.model;

public class ChannelMessage {
    private String message;
    private Long time;

    public Long getTime() {
        return time;
    }

    public ChannelMessage setTime(Long time) {
        this.time = time;
        return this;
    }
}
