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

package org.elastos.hive.did;

import org.elastos.did.DIDDocument;
import org.elastos.did.VerifiableCredential;
import org.elastos.did.VerifiablePresentation;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;


class PresentationInJWT {
	private DIDApp userDidApp = null;
	private DApp appInstanceDidApp = null;
	private DIDDocument doc = null;
	private BackupOptions backupOptions;

	public PresentationInJWT init(AppOptions userDidOpt, AppOptions appInstanceDidOpt, BackupOptions backupOptions) {
		try {
			this.backupOptions = backupOptions;

			userDidApp = new DIDApp(userDidOpt.name, userDidOpt.mnemonic, userDidOpt.phrasepass, userDidOpt.storepass);
			appInstanceDidApp = new DApp(appInstanceDidOpt.name, appInstanceDidOpt.mnemonic, appInstanceDidOpt.phrasepass, appInstanceDidOpt.storepass);

			doc = appInstanceDidApp.getDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	public DIDDocument getDoc() {
		return doc;
	}

	public String getAuthToken(String jwtToken) {
		try {
			Claims claims = new JwtParserBuilder().build().parseClaimsJws(jwtToken).getBody();
			String iss = claims.getIssuer();
			String nonce = (String) claims.get("nonce");

			VerifiableCredential vc = userDidApp.issueDiplomaFor(appInstanceDidApp);

			VerifiablePresentation vp = appInstanceDidApp.createPresentation(vc, iss, nonce);

			String token = appInstanceDidApp.createToken(vp, iss);
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getBackupVc(String sourceDID) {
		try {
			VerifiableCredential vc = userDidApp.issueBackupDiplomaFor(sourceDID,
					backupOptions.targetHost, backupOptions.targetDID);
			return vc.toString();
		} catch (DIDException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String getTargetHost() {
		return backupOptions.targetHost;
	}

	public String getTargetDid() {
		return backupOptions.targetDID;
	}

	public static class BackupOptions {
		private String targetDID;
		private String targetHost;

		public static BackupOptions create() {
			return new BackupOptions();
		}

		public BackupOptions targetDID(String targetDID) {
			this.targetDID = targetDID;
			return this;
		}

		public BackupOptions targetHost(String targetHost) {
			this.targetHost = targetHost;
			return this;
		}
	}

	public static class AppOptions {
		private String name;
		private String mnemonic;
		private String phrasepass;
		private String storepass;

		public static AppOptions create() {
			return new AppOptions();
		}

		public AppOptions setName(String name) {
			this.name = name;
			return this;
		}

		public AppOptions setMnemonic(String mnemonic) {
			this.mnemonic = mnemonic;
			return this;
		}

		public AppOptions setPhrasepass(String phrasepass) {
			this.phrasepass = phrasepass;
			return this;
		}

		public AppOptions setStorepass(String storepass) {
			this.storepass = storepass;
			return this;
		}
	}

}
