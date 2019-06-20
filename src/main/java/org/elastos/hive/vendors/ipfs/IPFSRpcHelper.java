package org.elastos.hive.vendors.ipfs;

import java.util.concurrent.CompletableFuture;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Status;

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
	public CompletableFuture<Status> loginAsync(Authenticator authenticator) {
		return loginAsync(authenticator, new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> loginAsync(Authenticator authenticator, Callback<Status> callback) {
		return checkExpired(callback);
	}

	@Override
	public CompletableFuture<Status> logoutAsync() {
		return logoutAsync(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> logoutAsync(Callback<Status> callback) {
		isValid = false;
		CompletableFuture<Status> future = new CompletableFuture<Status>();
		Status status = new Status(1);
	    callback.onSuccess(status);
	    future.complete(status);
		return future;
	}

	@Override
	public CompletableFuture<Status> checkExpired() {
		return checkExpired(new NullCallback<Status>());
	}

	@Override
	public CompletableFuture<Status> checkExpired(Callback<Status> callback) {
		if (isValid) {
			CompletableFuture<Status> future = new CompletableFuture<Status>();
			Status status = new Status(1);
		    callback.onSuccess(status);
		    future.complete(status);
			return future;
		}

		//get home hash and login
		CompletableFuture<Status> future = CompletableFuture.supplyAsync(() -> {
			Status status = null;
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
					status = new Status(0);
				    callback.onSuccess(status);
					return status;
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

			status = new Status(0);
		    callback.onSuccess(status);
			return status;
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
