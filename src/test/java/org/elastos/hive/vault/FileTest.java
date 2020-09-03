package org.elastos.hive.vault;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Files;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.Writer;
import java.util.concurrent.CompletableFuture;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {

    private static final String localDataPath = System.getProperty("user.dir");

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
                    e.printStackTrace();
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
            String json = "{\"id\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX\",\"publicKey\":[{\"id\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX#primary\",\"type\":\"ECDSAsecp256r1\",\"controller\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX\",\"publicKeyBase58\":\"xNoB1aRBgZqG3fLMmNzK5wkuNwwDmXDYm44cu2n8siSz\"}],\"authentication\":[\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX#primary\"],\"expires\":\"2025-09-01T20:18:27Z\",\"proof\":{\"type\":\"ECDSAsecp256r1\",\"created\":\"2020-09-02T04:18:27Z\",\"creator\":\"did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX#primary\",\"signatureValue\":\"Gq6ookLCWlfsib3NttV5pR6zXZFk6AHSoauYil-RWTS1Z-4l_u_UFk7gn7TObdHS650dMwcqezHlzLsiFbVOOw\"}}";

            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(() -> jwtToken));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            client = Client.createInstance(options);
            filesApi = client.getVault("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX").get().getFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
