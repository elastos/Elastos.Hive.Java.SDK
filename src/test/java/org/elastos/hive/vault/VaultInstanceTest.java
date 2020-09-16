package org.elastos.hive.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.Vault;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;

public class VaultInstanceTest {
    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private static Client client;

    @Test
    public void testGetVaultInstance1() {
        try {
            String json = TestConstance.DOC_STR;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestConstance.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestConstance.OWNERDID, TestConstance.PROVIDER);
            client = Client.createInstance(options);

            Vault vault = client.getVault("did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM").get();
            assertNotNull(vault);
            System.out.println("appId="+vault.getAppId());
            System.out.println("userDid="+vault.getUserDid());
            System.out.println("appInstanceDid="+vault.getAppInstanceDid());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetVaultInstance2() {
        try {
            String json = TestConstance.DOC_STR;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestConstance.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestConstance.OWNERDID, TestConstance.PROVIDER1);
            client = Client.createInstance(options);

            Vault vault = client.getVault("did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM").get();
            assertNotNull(vault);
            System.out.println("appId="+vault.getAppId());
            System.out.println("userDid="+vault.getUserDid());
            System.out.println("appInstanceDid="+vault.getAppInstanceDid());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void tearDown() {
        client = null;
    }

}
