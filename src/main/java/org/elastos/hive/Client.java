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

import org.elastos.hive.exception.HiveException;
import org.elastos.hive.oauth.Authenticator;
import org.elastos.hive.vendor.vault.VaultAuthHelper;
import org.elastos.hive.vendor.vault.VaultConstance;

import java.util.concurrent.CompletableFuture;

public class Client {

	private Options opts;

	public Client(Options options) {
		this.opts = options;
	}

	public static class Options {

		private String did;
		private String clientId;
		private String clientSecret;
		private String redirectURL;
		private String nodeUrl;
		private Authenticator authenticator;

		private boolean enableCloudSync;

		private AuthenticationHandler authentcationHandler;
		private String localPath;

		public void setDid(String did) {
			this.did = did;
		}

		public String getDid() {return this.did;}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String clientId() {return this.clientId;}

		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}

		public String clientSecret() {
			return this.clientSecret;
		}

		public void setRedirectURL(String redirectURL) {
			this.redirectURL = redirectURL;
		}

		public String redirectURL() {return this.redirectURL;}

		public void setNodeUrl(String url) {
			this.nodeUrl = url;
		}

		public String nodeUrl() {
			return nodeUrl;
		}

		public void setEnableCloudSync(boolean enable) {
			this.enableCloudSync = enable;
		}

		public boolean enableCloudSync() {
			return this.enableCloudSync;
		}

		public Options setAuthenticationHandler(AuthenticationHandler authentcationHandler) {
			this.authentcationHandler = authentcationHandler;
			return this;
		}

		public AuthenticationHandler authenticationHandler() {
			return this.authentcationHandler;
		}

		public Options setLocalDataPath(String path) {
			this.localPath = path;
			return this;
		}

		public String localDataPath() {
			return this.localPath;
		}

        protected boolean checkValid(boolean all) {
            return /*(storePath != null) && (!all || authenticator != null)*/true;
        }

	}

	public static Client createInstance(Options options) {

		return new Client(options);
	}

	private VaultAuthHelper authHelper;
	public void authrize() throws HiveException {
		try {
			this.authHelper = new VaultAuthHelper(opts.nodeUrl(),
					opts.getDid(),
					opts.localDataPath(),
					opts.clientId(),
					opts.clientSecret(),
					opts.redirectURL(),
					VaultConstance.SCOPE);
			authHelper.authrizeAsync(opts.authentcationHandler, opts.authenticator).get();
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	public CompletableFuture<Vault> getVault(String vaultProvider, String ownerDid) {
		return CompletableFuture.supplyAsync(() -> new Vault(this.authHelper, vaultProvider, ownerDid));
	}
}
