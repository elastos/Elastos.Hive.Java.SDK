package org.elastos.hive.vendors.ipfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.NullCallback;
import org.elastos.hive.UnirestAsyncCallback;
import org.elastos.hive.Void;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class IPFSRpcHelper implements AuthHelper {
	static final String CONFIG      = "ipfs.json";
	static final String LASTUID     = "last_uid";
	static final String UIDS        = "uids";

	private final IPFSEntry entry;
	private boolean isValid = false;
	private String BASEURL  = null;
	private String validAddress;

	IPFSRpcHelper(IPFSEntry entry) {
		this.entry = entry;
	}

	IPFSEntry getIpfsEntry() {
		return entry;
	}

	@Override
	public AuthToken getToken() {
		return null;
	}

	@Override
	public CompletableFuture<Void> loginAsync(Authenticator authenticator) {
		return loginAsync(authenticator, new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> loginAsync(Authenticator authenticator, Callback<Void> callback) {
		return checkExpired(callback);
	}

	@Override
	public CompletableFuture<Void> logoutAsync() {
		return logoutAsync(new NullCallback<Void>());
	}

	@Override
	public CompletableFuture<Void> logoutAsync(Callback<Void> callback) {
		isValid = false;
		CompletableFuture<Void> future = new CompletableFuture<Void>();
		Void placeHolder = new Void();
	    callback.onSuccess(placeHolder);
	    future.complete(placeHolder);
		return future;
	}

	@Override
	public CompletableFuture<Void> checkExpired() {
		return checkExpired(new NullCallback<Void>());
	}

	public CompletableFuture<PackValue> checkExpiredNew() {
		if (isValid) {
			CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();
			PackValue padding = new PackValue();
		    future.complete(padding);
			return future;
		}

		//get home hash and login
		CompletableFuture<PackValue> future = CompletableFuture.supplyAsync(() -> {
			PackValue padding = new PackValue();
			try {
				String homeHash = null;
				//Using the older validAddress try to get the home hash.
				if (validAddress != null && !validAddress.isEmpty()) {
					String url = String.format(IPFSURL.URLFORMAT, validAddress);
					homeHash = getHomeHash(url);
					BASEURL = url;
					if (homeHash == null) {
						validAddress = null;
					}
				}

				if (homeHash == null) {
					String[] addrs = entry.getRcpAddrs();
					for (int i = 0; i < addrs.length; i++) {
						String url = String.format(IPFSURL.URLFORMAT, addrs[i]);
						homeHash = getHomeHash(url);
						if (homeHash != null && !homeHash.isEmpty()) {
							BASEURL = url;
							validAddress = addrs[i];
							break;
						}
					}
				}

				if (homeHash == null) {
					return padding;
				}

				Unirest.get(BASEURL + IPFSMethod.LOGIN)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, entry.getUid())
					.queryString(IPFSURL.HASH, homeHash)
					.asJson();

				isValid = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return padding;
		});

		return future;
	}

	@Override
	public CompletableFuture<Void> checkExpired(Callback<Void> callback) {
		if (isValid) {
			CompletableFuture<Void> future = new CompletableFuture<Void>();
			Void placeHolder = new Void();
		    callback.onSuccess(placeHolder);
		    future.complete(placeHolder);
			return future;
		}

		//get home hash and login
		CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
			Void placeHolder = null;
			try {
				String homeHash = null;
				//Using the older validAddress try to get the home hash.
				if (validAddress != null && !validAddress.isEmpty()) {
					String url = String.format(IPFSURL.URLFORMAT, validAddress);
					homeHash = getHomeHash(url);
					BASEURL = url;
					if (homeHash == null) {
						validAddress = null;
					}
				}

				if (homeHash == null) {
					String[] addrs = entry.getRcpAddrs();
					for (int i = 0; i < addrs.length; i++) {
						String url = String.format(IPFSURL.URLFORMAT, addrs[i]);
						homeHash = getHomeHash(url);
						if (homeHash != null && !homeHash.isEmpty()) {
							BASEURL = url;
							validAddress = addrs[i];
							break;
						}
					}
				}

				if (homeHash == null) {
					placeHolder = new Void();
				    callback.onSuccess(placeHolder);
					return placeHolder;
				}

				Unirest.get(BASEURL + IPFSMethod.LOGIN)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.queryString(IPFSURL.UID, entry.getUid())
					.queryString(IPFSURL.HASH, homeHash)
					.asJson();

				isValid = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

			placeHolder = new Void();
		    callback.onSuccess(placeHolder);
			return placeHolder;
		});

		return future;
	}

	CompletableFuture<PackValue> getRootHash(PackValue value) {
		return getPathHash(value, "/");
	}

	CompletableFuture<PackValue> getPathHash(PackValue value, String path) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		String url = String.format("%s%s", getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, getIpfsEntry().getUid())
				.queryString(IPFSURL.PATH, path)
				.asJsonAsync(new GetPathHashCallback(value, future));

		return future;
	}

	CompletableFuture<PackValue> publishHash(PackValue value) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		String url = String.format("%s%s", getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, value.getHash().getValue())
			.asJsonAsync(new PublishRootHashCallback(value, future));

		return future;
	}

	CompletableFuture<Directory> invokeDirectoryCallback(PackValue value) {
		CompletableFuture<Directory> future = new CompletableFuture<Directory>();

		if (value.getException() != null) {
			value.getCallback().onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		Directory object = (Directory)value.getValue();
		@SuppressWarnings("unchecked")
		Callback<Directory> callback = (Callback<Directory>)value.getCallback();
		callback.onSuccess(object);
		future.complete(object);
		return future;
	}

	CompletableFuture<File> invokeFileCallback(PackValue value) {
		CompletableFuture<File> future = new CompletableFuture<File>();

		if (value.getException() != null) {
			value.getCallback().onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		File object = (File)value.getValue();
		@SuppressWarnings("unchecked")
		Callback<File> callback = (Callback<File>)value.getCallback();
		callback.onSuccess(object);
		future.complete(object);
		return future;
	}

	CompletableFuture<Void> invokeVoidCallback(PackValue value) {
		CompletableFuture<Void> future = new CompletableFuture<Void>();

		if (value.getException() != null) {
			value.getCallback().onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		Void object = new Void();
		@SuppressWarnings("unchecked")
		Callback<Void> callback = (Callback<Void>)value.getCallback();
		callback.onSuccess(object);
		future.complete(object);
		return future;
	}
	
	CompletableFuture<PackValue> stat(PackValue value) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		String url = String.format("%s%s", getBaseUrl(), IPFSMethod.PUBLISH);
		Unirest.get(url)
			.header(IPFSURL.ContentType, IPFSURL.Json)
			.queryString(IPFSURL.UID, getIpfsEntry().getUid())
			.queryString(IPFSURL.PATH, value.getHash().getValue())
			.asJsonAsync(new PublishRootHashCallback(value, future));

		return future;
	}
	
	CompletableFuture<String> stat(String path) {
		CompletableFuture<String> future = new CompletableFuture<String>();

		String url = String.format("%s%s", getBaseUrl(), IPFSMethod.STAT);
		Unirest.get(url)
				.header(IPFSURL.ContentType, IPFSURL.Json)
				.queryString(IPFSURL.UID, getIpfsEntry().getUid())
				.queryString(IPFSURL.PATH, path)
				.asJsonAsync(new StatCallback(future));

		return future;
	}

	boolean isFile(String type) {
		return type != null && type.equals("file");
	}

	boolean isFolder(String type) {
		return type != null && type.equals("directory");
	}

	private class StatCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<String> future;

		StatCallback(CompletableFuture<String> future) {
			this.future = future;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				future.completeExceptionally(e);
				return;
			}

			String hash = response.getBody().getObject().getString("Hash");
			future.complete(hash);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			future.completeExceptionally(e);
		}
	}

	private class GetPathHashCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<PackValue> future;
		private final PackValue value;

		GetPathHashCallback(PackValue value, CompletableFuture<PackValue> future) {
			this.value = value;
			this.future = future;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			JSONObject jsonObject = response.getBody().getObject();
			IPFSHash hash = new IPFSHash(jsonObject.getString("Hash"));
			value.setHash(hash);
			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}

	private class PublishRootHashCallback implements UnirestAsyncCallback<JsonNode> {
		private final CompletableFuture<PackValue> future;
		private final PackValue value;

		private PublishRootHashCallback(PackValue value, CompletableFuture<PackValue> future) {
			this.value = value;
			this.future = future;
		}

		@Override
		public void cancelled() {}

		@Override
		public void completed(HttpResponse<JsonNode> response) {
			if (response.getStatus() != 200) {
				HiveException e = new HiveException("Server Error: " + response.getStatusText());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			future.complete(value);
		}

		@Override
		public void failed(UnirestException exception) {
			HiveException e = new HiveException(exception.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}

	String getBaseUrl() {
		return BASEURL;
	}

	void setStatus(boolean invalid) {
		isValid = invalid;
	}

	void setValidAddress(String validAddress) {
		this.validAddress = validAddress;
	}

   String getHomeHash(String baseUrl) {
		String url = baseUrl + IPFSMethod.NAMERESOLVE;
		try {
			HttpResponse<JsonNode> json = Unirest.get(url)
					.header(IPFSURL.ContentType, IPFSURL.Json)
					.asJson();
			return json.getBody().getObject().getString("Path");
		} catch (UnirestException e) {
			e.printStackTrace();
		}

		return null;
	}
}
