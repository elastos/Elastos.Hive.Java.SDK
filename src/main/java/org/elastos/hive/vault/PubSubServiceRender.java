package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.vault.pubsub.ChannelMessage;
import org.elastos.hive.service.PubSubService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

class PubSubServiceRender implements PubSubService {
	@SuppressWarnings("unused")
	private ServiceEndpoint serviceEndpoint;

	public PubSubServiceRender(ServiceEndpoint vault) {
		this.serviceEndpoint = vault;
	}

	@Override
	public CompletableFuture<Boolean> publish(String channelName) {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<Boolean> remove(String channelName) {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<List<String>> getPublishedChannels() {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<List<String>> getSubscribedChannels() {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<Boolean> subscribe(String channelName, String pubDid, String pubAppId) {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<Boolean> unsubscribe(String channelName, String pubDid, String pubAppId) {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<Boolean> push(String channelName, String message) {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}

	@Override
	public CompletableFuture<List<ChannelMessage>> pop(String channelName, String pubDid, String pubAppId, int limit) {
		return CompletableFuture.supplyAsync(() -> {
			throw new NotImplementedException();
		});
	}
}
