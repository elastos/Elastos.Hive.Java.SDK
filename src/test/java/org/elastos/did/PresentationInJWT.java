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

import org.elastos.did.Issuer.CredentialBuilder;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.Header;
import org.elastos.hive.utils.JwtUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PresentationInJWT {
    private static DummyAdapter adapter;
    public static class Entity {
        private final static String passphrase = "password";
        protected final static String storepass = "password";

        private String name;
        private DIDStore store;
        private DID did;

        protected Entity(String name, String mnemonic) throws DIDException {
            this.name = name;

            initPrivateIdentity(mnemonic);
            initDid();
        }

        protected void initPrivateIdentity(String mnemonic) throws DIDException {
            final String storePath = System.getProperty("user.dir") + File.separator + "store" + File.separator + name;

            store = DIDStore.open("filesystem", storePath, adapter);

            if (store.containsPrivateIdentity())
                return; // Already exists

            store.initPrivateIdentity(null, mnemonic, passphrase, storepass);
        }

        protected void initDid() throws DIDException {
            List<DID> dids = store.listDids(DIDStore.DID_HAS_PRIVATEKEY);
            if (dids.size() > 0) {
                for (DID did : dids) {
                    if (did.getMetadata().getAlias().equals("me")) {
                        System.out.format("[%s] My DID: %s%n", name, did);
                        this.did = did;

                        store.publishDid(did, storepass);
                        return;
                    }
                }
            }

            DIDDocument doc = store.newDid("me", storepass);
            this.did = doc.getSubject();
            System.out.format("[%s] My new DID created: %s%n", name, did);
            store.publishDid(did, storepass);
        }

        protected DIDStore getDIDStore() {
            return store;
        }

        public DID getDid() {
            return did;
        }

        public DIDDocument getDocument() throws DIDException {
            return store.loadDid(did);
        }

        public String getName() {
            return name;
        }

        protected String getStorePassword() {
            return storepass;
        }
    }

    public static class DIDApp extends Entity {
        private Issuer issuer;

        public DIDApp(String name, String mnemonic) throws DIDException {
            super(name, mnemonic);

            issuer = new Issuer(getDocument());
        }

        public VerifiableCredential issueDiplomaFor(DApp dapp) throws DIDException {
            Map<String, String> subject = new HashMap<String, String>();
            subject.put("appDid", dapp.appId);

            Calendar exp = Calendar.getInstance();
            exp.add(Calendar.YEAR, 5);

            CredentialBuilder cb = issuer.issueFor(dapp.getDid());
            VerifiableCredential vc = cb.id("didapp")
                    .type("AppIdCredential")
                    .properties(subject)
                    .expirationDate(exp.getTime())
                    .seal(getStorePassword());

            System.out.println("VerifiableCredential:");
            String vcStr = vc.toJson(true, true);
            System.out.println(vcStr);

            return vc;
        }
    }

    public static class DApp extends Entity {
        public String appId = "appId";

        public DApp(String name, String mnemonic) throws DIDException {
            super(name, mnemonic);
        }

        public VerifiablePresentation createPresentation(VerifiableCredential vc, String realm, String nonce) throws DIDException {
            VerifiablePresentation.Builder vpb = VerifiablePresentation.createFor(getDid(), getDIDStore());
            List<VerifiableCredential> vcs = new ArrayList<VerifiableCredential>(1);
            vcs.add(vc);

            VerifiablePresentation vp = vpb.credentials(vcs.toArray(new VerifiableCredential[vcs.size()]))
                    .realm(realm)
                    .nonce(nonce)
                    .seal(getStorePassword());

            System.out.println("VerifiableCredential:");
            String vpStr = vp.toJson(true);
            System.out.println(vpStr);

            return vp;
        }

        public String createToken(VerifiablePresentation vp, String hiveDid) throws DIDException {

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.MILLISECOND, 0);
            Date iat = cal.getTime();
            Date nbf = cal.getTime();
            cal.add(Calendar.MONTH, 3);
            Date exp = cal.getTime();

            // Create JWT token with presentation.
            String token = getDocument().jwtBuilder()
                    .addHeader(Header.TYPE, Header.JWT_TYPE)
                    .addHeader("version", "1.0")

                    .setSubject("DIDAuthResponse")
                    .setAudience(hiveDid)
                    .setIssuedAt(iat)
                    .setExpiration(exp)
                    .setNotBefore(nbf)
                    .claimWithJson("presentation", vp.toString())
                    .sign(storepass)
                    .compact();

            System.out.println("JWT Token:");
            System.out.println("  " + token);
            return token;
        }

    }

    static void initDIDBackend() {
        final String cacheDir = System.getProperty("user.dir") + File.separator + "store" + File.separator + "cache";

        adapter = new DummyAdapter();
        DIDBackend.initialize(adapter, cacheDir);
    }

    DIDApp didapp = null;
    DApp testapp = null;
    String docStr = null;
    DIDDocument doc = null;
    public PresentationInJWT init() {
        try {
            initDIDBackend();
            didapp = new DIDApp("didapp", "provide zero slab drink patient tape private paddle unaware catch virtual stone");
            testapp = new DApp("testapp", "polar degree weapon crouch alarm scorpion between stand glow round catalog marine");

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
