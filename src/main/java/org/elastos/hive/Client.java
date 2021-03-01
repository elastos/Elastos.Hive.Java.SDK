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
package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.did.exception.DIDException;
import org.elastos.did.exception.DIDResolveException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.ParseHiveUrlException;
import org.elastos.hive.exception.ProviderNotSetException;

import java.net.URLDecoder;
import java.nio.file.ProviderNotFoundException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client {
	private static boolean resolverDidSetup;

	private AuthenticationAdapter authenticationAdapter;
	private ApplicationContext context;

	private static class AuthenticationAdapterImpl implements AuthenticationAdapter {
		@Override
		public synchronized CompletableFuture<String> getAuthorization(ApplicationContext context, String jwtToken) {
			return context.getAuthorization(jwtToken);
		}
	}

	private Client(ApplicationContext context) {
		this.context = context;
		this.authenticationAdapter = new AuthenticationAdapterImpl();
	}


	/**
	 * Recommendation for cache dir:
	 * - Laptop/standard Java
	 * System.getProperty("user.home") + "/.cache.did.elastos"
	 * - Android Java
	 * Context.getFilesDir() + "/.cache.did.elastos"
	 *
	 * @param resolver the DIDResolver object
	 * @param cacheDir the cache path name
	 */
	public static void setupResolver(String resolver, String cacheDir) throws HiveException {
		if (cacheDir == null || resolver == null)
			throw new IllegalArgumentException();
		if (resolverDidSetup)
			throw new HiveException("Resolver already setup before");

		try {
			DIDBackend.initialize(resolver, cacheDir);
			ResolverCache.reset();
			resolverDidSetup = true;
		} catch (DIDResolveException e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}


	public static Client createInstance(ApplicationContext context) throws HiveException {
		if (context == null)
			throw new IllegalArgumentException("Missing Application context");

		if (context.getLocalDataDir() == null)
			throw new IllegalArgumentException("Can not acquire data cache location from Application context");

		if (context.getAppInstanceDocument() == null)
			throw new IllegalArgumentException("Can not acquire App instance document from Application context");

		if (!resolverDidSetup)
			throw new HiveException("Setup DID resolver first");

		return new Client(context);
	}

	/**
	 * get Vault instance with specified DID.
	 * Try to get a vault on target provider address with following steps:
	 *  - Get the target provider address;
	 *  - Create a new vaule of local instance..
	 *
	 * @param ownerDid  the owner did related to target vault
	 * @param preferredProviderAddress the preferred target provider address
	 * @return a new vault instance.
	 */
	public CompletableFuture<Vault> getVault(String ownerDid, String preferredProviderAddress) {
		return getVaultProvider(ownerDid, preferredProviderAddress)
				.thenApplyAsync(provider -> {
					AuthHelper authHelper = new AuthHelper(this.context,
							ownerDid,
							provider,
							this.authenticationAdapter);
					return new Vault(authHelper, provider, ownerDid);
				});
	}

	/**
	 * get Backup instance with specified DID.
	 * Try to get a vault on target provider address with following steps:
	 *  - Get the target provider address;
	 *  - Create a new vaule of local instance..
	 *
	 * @param ownerDid  the owner did related to target vault
	 * @param providerAddress the preferred target provider address
	 * @return a new vault instance.
	 */
	public CompletableFuture<Backup> getBackup(String ownerDid, String providerAddress) {

		if (ownerDid == null)
			throw new IllegalArgumentException("Missing ownerDid");

		if (providerAddress == null)
			throw new IllegalArgumentException("Missing provider address");

		return CompletableFuture.supplyAsync(() -> {
			AuthHelper authHelper = new AuthHelper(this.context,
					ownerDid,
					providerAddress,
					this.authenticationAdapter);
			return new BackupImpl(authHelper);
		});
	}

	/**
	 * get Manager instance with specified DID.
	 * Try to get a vault on target provider address with following steps:
	 *  - Get the target provider address;
	 *  - Create a new vaule of local instance..
	 *
	 * @param ownerDid  the owner did related to target vault
	 * @param preferredProviderAddress the preferred target provider address
	 * @return a new vault instance.
	 */
	public CompletableFuture<Management> getManager(String ownerDid, String preferredProviderAddress) {
		return getVaultProvider(ownerDid, preferredProviderAddress)
				.thenApplyAsync(provider -> {
					AuthHelper authHelper = new AuthHelper(this.context,
							ownerDid,
							provider,
							this.authenticationAdapter);
					return new Management(authHelper, provider, ownerDid);
				});
	}

	/**
	 * Try to acquire provider address for the specific user DID with rules with sequence orders:
	 *  - Use 'preferredProviderAddress' first when it's being with real value; Otherwise
	 *  - Resolve DID document according to the ownerDid from DID sidechain,
	 *    and find if there are more than one "HiveVault" services, then would
	 *    choose the first one service point as target provider address. Otherwise
	 *  - It means no service endpoints declared on this DID Document, then would throw the
	 *    corresponding exception.
	 *
	 * @param ownerDid the owner did that want be set provider address
	 * @param preferredProviderAddress the preferred provider address to use
	 * @return the provider address
	 */
	public CompletableFuture<String> getVaultProvider(String ownerDid, String preferredProviderAddress) {
		if (ownerDid == null)
			throw new IllegalArgumentException("Missing ownerDid to get the provider for");

		return CompletableFuture.supplyAsync(() -> {
			/* Choose 'preferredProviderAddress' as target provider address if it's with value;
			 */
			if (preferredProviderAddress != null)
				return preferredProviderAddress;

			try {
				List<DIDDocument.Service> services = null;
				DID did = new DID(ownerDid);
				DIDDocument doc;

				doc = did.resolve();
				if (doc == null)
					throw new ProviderNotFoundException(
							String.format("The DID document %s has not published", ownerDid));

				services = doc.selectServices((String) null, "HiveVault");
				if (services == null || services.size() == 0)
					throw new ProviderNotSetException(
							String.format("No 'HiveVault' services declared on DID document %s", ownerDid));

				/* TODO: should we throw special exception when it has more than one
				 *       endpoints of service "HiveVault";
				 */
				return services.get(0).getServiceEndpoint();
			} catch (DIDException e) {
				throw new CompletionException(new HiveException(e.getLocalizedMessage()));
			}
		});
	}

	/**
	 * run script by hive url
	 *
	 * @param scriptUrl  hive://target_did@target_app_did/scripting/script_name?params={key=value}
	 * @param resultType
	 * @param <T>
	 * @return
	 */
	public <T> CompletableFuture<T> callScriptUrl(String scriptUrl, Class<T> resultType) {
		return parseHiveURL(scriptUrl).thenComposeAsync(hiveURLInfo -> hiveURLInfo.callScript(resultType));
	}

	/**
	 * Convenient method that first calls a script by url using callScriptURL(), and expects the
	 * JSON output to contain a file download information. If this is the case, the file download is
	 * starting and a file reader is returned.
	 */
	public <T> CompletableFuture<T> downloadFileByScriptUrl(String scriptUrl, Class<T> resultType) {

		CompletableFuture<HiveURLInfo> hiveUrlInfo = parseHiveURL(scriptUrl);

		return hiveUrlInfo.thenApplyAsync(hiveURLInfo -> hiveURLInfo.deserialize(scriptUrl).getScriptName())
				.thenComposeAsync(scriptName ->
						callScriptUrl(scriptUrl, JsonNode.class)
								.thenComposeAsync(jsonNode -> CompletableFuture.supplyAsync(() ->
										jsonNode.get(scriptName).get("transaction_id").textValue())))
				.thenComposeAsync(txId -> hiveUrlInfo.thenComposeAsync(hiveURLInfo ->
						hiveURLInfo.getVault())
						.thenComposeAsync(vault -> vault.getScripting().downloadFile(txId, resultType)));
	}

	/**
	 * Parses a Hive standard url into a url info that can later be executed to get the result or the
	 * target url.
	 * <p>
	 * For example, later calling a url such as ...
	 * hive://userdid:appdid/getAvatar
	 * <p>
	 * ... results in a call to the "getAvatar" script, previously registered by "userdid" on his vault,
	 * in the "appdid" scope. This is similar to calling:
	 * hiveClient.getVault(userdid).getScripting().call("getAvatar");
	 * <p>
	 * Usage example (assuming the url is a call to a getAvatar script that contains a FileDownload
	 * executable named "download"):
	 * <p>
	 * - let hiveURLInfo = hiveclient.parseHiveURL(urlstring)
	 * - let scriptOutput = await hiveURLInfo.callScript();
	 * - hiveURLInfo.getVault().getScripting().downloadFile(scriptOutput.items["download"].getTransferID())
	 */
	public CompletableFuture<HiveURLInfo> parseHiveURL(String scriptUrl) {
		return CompletableFuture.supplyAsync(() -> new HiveURLInfoImpl(scriptUrl));
	}

	class HiveURLInfoImpl implements HiveURLInfo {

		private UrlInfo urlInfo;

		/**
		 * HiveURLInfo
		 *
		 * @param hiveUrl hive://target_did@target_app_did/scripting/script_name?params={key=value}
		 */
		public HiveURLInfoImpl(String hiveUrl) {
			try {
				urlInfo = deserialize(hiveUrl);
			} catch (Exception e) {
				throw new ParseHiveUrlException(e.getLocalizedMessage());
			}
		}

		@Override
		public <T> CompletableFuture<T> callScript(Class<T> resultType) {
			return getVault().thenComposeAsync((Function<Vault, CompletionStage<T>>) vault -> vault.getScripting()
					.callScriptUrl(urlInfo.getScriptName(), urlInfo.getParams(), urlInfo.getAppDid(), resultType));
		}

		@Override
		public CompletableFuture<Vault> getVault() {
			return getVaultProvider(urlInfo.getTargetDid(), null)
					.thenApplyAsync(provider -> {
						AuthHelper authHelper = new AuthHelper(context,
								urlInfo.getTargetDid(),
								provider,
								authenticationAdapter);
						return new Vault(authHelper, provider, urlInfo.getTargetDid());
					});
		}

		@Override
		public UrlInfo deserialize(String hiveUrl) {
			if(urlInfo != null) return urlInfo;
			try {
				urlInfo = new UrlInfo();
				String decodeUrl = URLDecoder.decode(hiveUrl, "utf-8");
				Pattern pattern = Pattern.compile("(\\w+):\\/\\/([^@]+)@([^/ ]*)\\/([^/]*)\\/([^?]*)(\\?)?(params=)?([^=]*)?");
				Matcher matcher = pattern.matcher(decodeUrl);
				matcher.find();
				urlInfo.setTargetDid(matcher.group(2));
				urlInfo.setAppDid(matcher.group(3));
				urlInfo.setScriptName(matcher.group(5));
				urlInfo.setParams(matcher.group(8));
			} catch (Exception e) {
				throw new ParseHiveUrlException(e.getLocalizedMessage());
			}
			return urlInfo;
		}
	}


}