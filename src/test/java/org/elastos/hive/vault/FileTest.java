package org.elastos.hive.vault;

import static org.junit.Assert.fail;

import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Files;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.Writer;

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
            Client.Options options = new Client.Options();
            options.setAuthenticationHandler((jwtToken) -> null);
            options.setLocalDataPath(localDataPath);

            client = Client.createInstance(options);
            filesApi = client.getVault("did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM").get().getFiles();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
