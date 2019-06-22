package org.elastos.hive.vendors.ipfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

class IPFSRpcHelper implements AuthHelper {
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
