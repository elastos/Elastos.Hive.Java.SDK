package org.elastos.hive.vault;

import org.elastos.did.DID;
import org.elastos.did.DIDBackend;
import org.elastos.did.DIDDocument;
import org.elastos.did.backend.ResolverCache;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Files;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.Writer;

import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {
    private static final String clientId = "1098324333865-q7he5l91a4pqnuq9s2pt5btj9kenebkl.apps.googleusercontent.com";
    private static final String clientSecret = "0Ekmgx8dPbSxnTxxF-fqxjnz";
    private static final String redirectUri = "http://localhost:12345";
    private static final String nodeUrl = "http://127.0.0.1:5000";

    private static final String did = "Instance DID public authentication key";

    private static final String localDataPath = System.getProperty("user.dir");
    private static final String storePath = "did store pass";
    private String stringReader = "this is test for reader";

    private static Client client;

    private String testFile = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";

    private static Files filesApi;

    private static final String folder = "cache/";
    private static final String remoteFile = folder + "test.txt";

    private static String uploadUrl = "api/v1/files/uploader/cache/test.txt"; ///api/v1/files/uploader/test.txt

    @Test
    public void testUpload() {
        try {
            filesApi.upload(uploadUrl, new Callback<Writer>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Writer result) {
                    try {
                        result.write("aaaaaaaaaa");
                        result.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            String json = "{\"id\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab\",\"publicKey\":[{\"id\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab#primary\",\"type\":\"ECDSAsecp256r1\",\"controller\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab\",\"publicKeyBase58\":\"21YM84C9hbap4GfFSB3QbjauUfhAN4ETKg2mn4bSqx4Kp\"}],\"authentication\":[\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab#primary\"],\"verifiableCredential\":[{\"id\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab#name\",\"type\":[\"BasicProfileCredential\",\"SelfProclaimedCredential\"],\"issuer\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab\",\"issuanceDate\":\"2020-07-01T00:46:40Z\",\"expirationDate\":\"2025-06-30T00:46:40Z\",\"credentialSubject\":{\"id\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab\",\"name\":\"KP Test\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab#primary\",\"signature\":\"jQ1OGwpkYqjxooyaPseqyr_1MncOZDrMS_SvwYzqkCHVrRfjv_b7qfGCjxy7Gbx-LS3bvxZKeMxU1B-k3Ysb3A\"}}],\"expires\":\"2025-07-01T00:46:40Z\",\"proof\":{\"type\":\"ECDSAsecp256r1\",\"created\":\"2020-07-01T00:47:20Z\",\"creator\":\"did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab#primary\",\"signatureValue\":\"TOpNt-pWeQDJFaS5EkpMOuCqnZKhPCizf7LYQQDBrNLVIZ_7AR73m-KJk7Aja0wmZWXd7S4n7SC2W4ZQayJlMA\"}}";
            DID did = new DID("did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab");

            DIDBackend.initialize("http://api.elastos.io:20606", localDataPath);
            ResolverCache.reset();

            DIDDocument doc = did.resolve();

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler((jwtToken) -> null);
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            client = Client.createInstance(options);
            filesApi = client.getVault("did:elastos:idFKwBpj3Buq3XbLAFqTy8LMAW8K7kp3Ab").get().getFiles();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
