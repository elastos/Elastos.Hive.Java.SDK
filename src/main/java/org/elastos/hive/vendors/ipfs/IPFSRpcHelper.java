/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.ipfs;

import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.Directory;
import org.elastos.hive.File;
import org.elastos.hive.HiveException;
import org.elastos.hive.IPFSEntry;
import org.elastos.hive.Length;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Void;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendors.connection.BaseServiceUtil;
import org.elastos.hive.vendors.connection.Model.BaseServiceConfig;
import org.elastos.hive.vendors.ipfs.network.IPFSApi;
import org.elastos.hive.vendors.ipfs.network.model.ResolveResponse;
import org.elastos.hive.vendors.ipfs.network.model.StatResponse;
import org.elastos.hive.vendors.ipfs.network.model.UIDResponse;

import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Response;

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
					String requestUrl = UrlUtil.checkPort(validAddress,IPFSConstance.DEFAULT_PORT);
					String url = String.format(IPFSConstance.URLFORMAT, requestUrl);
					homeHash = getHomeHash(url,entry.getUid());
					BASEURL = url;
					if (homeHash == null) {
						validAddress = null;
					}
				}

				if (homeHash == null) {
					String[] addrs = entry.getRcpAddrs();
					for (int i = 0; i < addrs.length; i++) {
						String requestUrl = UrlUtil.checkPort(addrs[i],IPFSConstance.DEFAULT_PORT);
						String url = String.format(IPFSConstance.URLFORMAT, requestUrl);
						homeHash = getHomeHash(url,entry.getUid());
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

				if (login(BASEURL , entry.getUid() , homeHash)){
					isValid = true;
				}
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
					String requestUrl = UrlUtil.checkPort(validAddress,IPFSConstance.DEFAULT_PORT);
					String url = String.format(IPFSConstance.URLFORMAT, requestUrl);
					homeHash = getHomeHash(url,entry.getUid());
					BASEURL = url;
					if (homeHash == null) {
						validAddress = null;
					}
				}

				if (homeHash == null) {
					String[] addrs = entry.getRcpAddrs();
					for (int i = 0; i < addrs.length; i++) {
						String requestUrl = UrlUtil.checkPort(addrs[i],IPFSConstance.DEFAULT_PORT);
						String url = String.format(IPFSConstance.URLFORMAT, requestUrl);
						homeHash = getHomeHash(url,entry.getUid());
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

				if (login(BASEURL , entry.getUid() , homeHash)) {
					isValid = true;
				}
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
		getStat(future,value,getBaseUrl(),getIpfsEntry().getUid(),path);
		return future;
	}

	CompletableFuture<PackValue> publishHash(PackValue value) {
		CompletableFuture<PackValue> future = new CompletableFuture<PackValue>();

		if (value.getException() != null) {
			future.completeExceptionally(value.getException());
			return future;
		}

		publish(future,value,getBaseUrl(),getIpfsEntry().getUid(),value.getHash().getValue());

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

	CompletableFuture<Length> invokeLengthCallback(PackValue value) {
		CompletableFuture<Length> future = new CompletableFuture<Length>();

		if (value.getException() != null) {
			value.getCallback().onError(value.getException());
			future.completeExceptionally(value.getException());
			return future;
		}

		Length object = (Length) value.getValue();
		@SuppressWarnings("unchecked")
		Callback<Length> callback = (Callback<Length>)value.getCallback();
		callback.onSuccess(object);
		future.complete(object);
		return future;
	}
	
	boolean isFile(String type) {
		return type != null && type.equals("file");
	}

	boolean isFolder(String type) {
		return type != null && type.equals("directory");
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

   String getHomeHash(String baseUrl , String uid) {
	   String peerID = getUidInfo(baseUrl , uid);
	   return resolve(baseUrl , peerID);
	}

	private String getUidInfo(String url , String uid){
		String peerId = null;
		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();

			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class , url , config);
			Call call = ipfsApi.getUidInfo(uid);

			Response response = call.execute();

			if (response.code() == 200){
				UIDResponse uidResponse = (UIDResponse) response.body();
				peerId = uidResponse.getPeerID();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return peerId ;
	}

	private boolean login(String url , String uid , String hash){
		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder()
					.ignoreReturnBody(true)
					.build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class , url , config);
			Call call = ipfsApi.login(uid , hash);
			Response response = call.execute();

			if (response.code()==200){
				return true ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false ;
	}

	private String resolve(String url , String peerId){
		String path = null;
		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();
			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class , url , config);
			Call call = ipfsApi.resolve(peerId);
			Response response = call.execute();

			if (response.code() == 200){
				ResolveResponse resolveResponse = (ResolveResponse) response.body();
				path = resolveResponse.getPath();
			}else {
				//TODO callback exception
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return path ;
	}

	private void getStat(CompletableFuture future , PackValue value ,
						 String url , String uid , String path){
		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();

			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class , url , config);
			Call call = ipfsApi.getStat(uid,path);
			call.enqueue(new IPFSRpcCallback(future,value,IPFSConstance.Type.GET_STAT));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}

	}

	private void publish(CompletableFuture future , PackValue value ,
						 String url , String uid , String path){
		try {
			BaseServiceConfig config = new BaseServiceConfig.Builder().build();

			IPFSApi ipfsApi = BaseServiceUtil.createService(IPFSApi.class , url , config);
			Call call = ipfsApi.publish(uid,path);
			call.enqueue(new IPFSRpcCallback(future,value,IPFSConstance.Type.PUBLISH));
		} catch (Exception ex) {
			HiveException e = new HiveException(ex.getMessage());
			value.setException(e);
			future.completeExceptionally(e);
		}
	}

	private class IPFSRpcCallback implements retrofit2.Callback{
		private final CompletableFuture future;
		private final PackValue value;
		private final IPFSConstance.Type type;

		public IPFSRpcCallback(CompletableFuture future , PackValue value , IPFSConstance.Type type) {
			this.future = future;
			this.value = value;
			this.type = type;
		}

		@Override
		public void onResponse(Call call, Response response) {
			if (response.code() != 200) {
				HiveException e = new HiveException("Server Error: " + response.message());
				value.setException(e);
				future.completeExceptionally(e);
				return;
			}

			switch (type){
				case GET_STAT:
					StatResponse statResponse = (StatResponse) response.body();
					IPFSHash hash = new IPFSHash(statResponse.getHash());
					value.setHash(hash);
					future.complete(value);
				case PUBLISH:
					future.complete(value);
					break;
			}
		}

		@Override
		public void onFailure(Call call, Throwable t) {
			HiveException e = new HiveException(t.getMessage());
			this.value.setException(e);
			future.completeExceptionally(e);
		}
	}
}
