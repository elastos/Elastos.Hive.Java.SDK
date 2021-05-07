package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.network.model.ChannelMessage;
import org.elastos.hive.service.PubSubService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

class PubSubServiceRender extends BaseServiceRender implements PubSubService, HttpExceptionHandler {
	public PubSubServiceRender(Vault vault) {
		super(vault);
	}

	@Override
	public CompletableFuture<Boolean> publish(String channelName) {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> remove(String channelName) {
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getPublishedChannels() {
		return null;
	}

	@Override
	public CompletableFuture<List<String>> getSubscribedChannels() {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> subscribe(String channelName, String pubDid, String pubAppId) {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> unsubscribe(String channelName, String pubDid, String pubAppId) {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> push(String channelName, String message) {
		return null;
	}

	@Override
	public CompletableFuture<List<ChannelMessage>> pop(String channelName, String pubDid, String pubAppId, int limit) {
		return null;
	}
}
