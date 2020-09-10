package org.elastos.hive.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.Vault;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertNotNull;

public class VaultInstanceTest {
    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private static Client client;

    @Test
    public void testGetVaultInstance() {
        try {
            Vault vault = client.getVault("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX").get();
            assertNotNull(vault);
            System.out.println("appId="+vault.getAppDid());
            System.out.println("userDid="+vault.getUserDid());
            System.out.println("appInstanceDid="+vault.getAppInstanceDid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            String json = "{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"publicKey\":[{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"type\":\"ECDSAsecp256r1\",\"controller\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"publicKeyBase58\":\"tgmQDrEGg8QKNjy7hgm2675QFh7qUkfd4nDZ2AgZxYy5\"}],\"authentication\":[\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\"],\"verifiableCredential\":[{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#email\",\"type\":[\"BasicProfileCredential\",\"EmailCredential\",\"InternetAccountCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"email\":\"john@gmail.com\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"qEAxxPzAeSS7umKKKL-T0bMD7iUgUMnoHRsROupMjnXojLZdPF6KGmU80f7iy1nLDyuRx-dQLyIqBi0a1-vHaQ\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#passport\",\"type\":[\"BasicProfileCredential\",\"SelfProclaimedCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"nation\":\"Singapore\",\"passport\":\"S653258Z07\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"qbb8YXBp5DiOMsBur5iwOW0eJtnnEi2P_EznGG0rSg5daR6hvuSXKjywgBi-GShTCK1QOQMiBC2LINn-XyjXJg\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#profile\",\"type\":[\"BasicProfileCredential\",\"SelfProclaimedCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"email\":\"john@example.com\",\"language\":\"English\",\"name\":\"John\",\"nation\":\"Singapore\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"OOtRiXrGMnrmAu8D_2nwPkRhO6Qj8Hkp9qKbRiKTxSLA4wzbRtXesLav1n1FR3jFzddSSbsBGDXBzVD88B5tnw\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#twitter\",\"type\":[\"InternetAccountCredential\",\"TwitterCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"twitter\":\"@john\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"PE4NlCm1gk_dGRxJBb2XWVkYisuwsXmC_06oS7vBAnVOGpA_qYX1JWar7xTS6_oCzLSLus3IVfEXdG3xVK8gow\"}}],\"expires\":\"2024-12-27T08:53:27Z\",\"proof\":{\"type\":\"ECDSAsecp256r1\",\"created\":\"2019-12-27T08:53:27Z\",\"creator\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signatureValue\":\"2p-wukVhrDfu0N-xe2ANqMDUbAfZ4ntLcTVvL4IXkB5jD7ZJhrnyqtAsF9kT6kVkHBSKFgcxPavo7Nws7x4JMQ\"}}";

            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAiLCAia2lkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jcHJpbWFyeSJ9.eyJpc3MiOiJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNIiwic3ViIjoiRElEQXV0aFJlc3BvbnNlIiwiYXVkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlhdCI6MTU5OTY5ODAxNCwiZXhwIjoxNTk5Njk4MDc0LCJuYmYiOjE1OTk2OTgwMTQsInByZXNlbnRhdGlvbiI6eyJ0eXBlIjoiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsImNyZWF0ZWQiOiIyMDIwLTA5LTEwVDAwOjMzOjM0WiIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbeyJpZCI6ImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jZGlkYXBwIiwidHlwZSI6WyJBcHBJZENyZWRlbnRpYWwiXSwiaXNzdWVyIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlzc3VhbmNlRGF0ZSI6IjIwMjAtMDktMTBUMDA6MzM6MzRaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDI0LTEyLTI3VDA4OjUzOjI3WiIsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImFwcERpZCI6ImFwcElkIn0sInByb29mIjp7InR5cGUiOiJFQ0RTQXNlY3AyNTZyMSIsInZlcmlmaWNhdGlvbk1ldGhvZCI6ImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jcHJpbWFyeSIsInNpZ25hdHVyZSI6Imh2NlNKY1pRcFR5YllUTFpmOVVUdF81WmRDTW4tWVBFQ1VZbHRuN1M3cXl3b2lxV1UzZkl6aW9OZ0laTWZQVU9NdUZ0S050bG0yQV9sZnl1N3IwbWdBIn19XSwicHJvb2YiOnsidHlwZSI6IkVDRFNBc2VjcDI1NnIxIiwidmVyaWZpY2F0aW9uTWV0aG9kIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSNwcmltYXJ5IiwicmVhbG0iOiJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNIiwibm9uY2UiOiIzYmE1MzMwYS1mMmZkLTExZWEtODFkYi02NDVhZWRlYjA3NjMiLCJzaWduYXR1cmUiOiJ1dVF2RkU4anprb0xtWFBucTlhZVFvTUxLWXdPeDdTZkRXVG9pVGtqeWdrQ1BINXBOU3UtWGNkdVR5Z0EzRFVpek1yOFZLVFFTU1gtRGtKTldQdDdsQSJ9fX0.8vE_U9gOmQ8g1B3x0DlGOBBMSSHfCooeQo2UCVe_8BI1OYTUmPM3PExRQ0_EZ1KUILtedD2Ei8FK4L6SkkUVzQ"));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX", "http://localhost:5000");
            client = Client.createInstance(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        client = null;
    }

}
