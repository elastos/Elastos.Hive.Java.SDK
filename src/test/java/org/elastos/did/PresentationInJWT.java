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

package org.elastos.did;

import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.utils.JwtUtil;

import java.io.File;

public class PresentationInJWT {
    DIDApp didapp = null;
    DApp testapp = null;
    String docStr = null;
    DIDDocument doc = null;

	private static DummyAdapter adapter;

	private void initDIDBackend() {
		final String cacheDir = System.getProperty("user.dir") + File.separator + "store" + File.separator + "cache";

		adapter = new DummyAdapter();
		DIDBackend.initialize(adapter, cacheDir);
	}

	public PresentationInJWT init() {
		try {
			initDIDBackend();
			didapp = new DIDApp("didapp", "provide zero slab drink patient tape private paddle unaware catch virtual stone", adapter, "password", "password");
			testapp = new DApp("testapp", "polar degree weapon crouch alarm scorpion between stand glow round catalog marine", adapter, "password", "password");

			doc = testapp.getDocument();
			docStr = doc.toJson(true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this;
	}

	public String getDocStr() {
		return docStr;
	}

	public DIDDocument getDoc() {
		return doc;
	}

	public String getAuthToken(String jwtToken) {
		try {
			Claims claims = JwtUtil.getBody(jwtToken);
			String iss = claims.getIssuer();
			String nonce = (String) claims.get("nonce");

			VerifiableCredential vc = didapp.issueDiplomaFor(testapp);

			VerifiablePresentation vp = testapp.createPresentation(vc, iss, nonce);

			String token = testapp.createToken(vp, iss);
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
