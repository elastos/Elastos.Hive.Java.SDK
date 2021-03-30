package org.elastos.hive.service;

import org.elastos.hive.network.model.ChannelMessage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PubSubService {
    /**
     * Publish a new channel for the following subscript and message send/receive.
     * TODO:
     * @param channelName channel name
     * @return successfully
     */
    CompletableFuture<Boolean> publish(String channelName);

    CompletableFuture<Boolean> remove(String channelName);

    CompletableFuture<List<String>> getPublishedChannels();

    CompletableFuture<List<String>> getSubscribedChannels();

    CompletableFuture<Boolean> subscribe(String channelName, String pubDid, String pubAppId);

    CompletableFuture<Boolean> unsubscribe(String channelName, String pubDid, String pubAppId);

    CompletableFuture<Boolean> push(String channelName, String message);

    CompletableFuture<List<ChannelMessage>> pop(String channelName, String pubDid, String pubAppId, int limit);
}
