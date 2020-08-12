package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.hive.Client;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.vendor.vault.VaultOptions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Desktop;
import java.net.URI;
import java.util.Map;

import static org.junit.Assert.fail;

public class DBTest {
    private static final String clientId = "1098324333865-q7he5l91a4pqnuq9s2pt5btj9kenebkl.apps.googleusercontent.com";
    private static final String clientSecret = "0Ekmgx8dPbSxnTxxF-fqxjnz";
    private static final String redirectUri = "http://localhost:12345";
    private static final String nodeUrl = "http://127.0.0.1:5000";

    private static final String authToken = "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAifQ.eyJpc3MiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJzdWIiOiAiRElEQXV0aENyZWRlbnRpYWwiLCAiYXVkIjogIkhpdmUiLCAiaWF0IjogMTU5Njc2NDk3NCwgImV4cCI6IDE1OTY3NzQ5NzQsICJuYmYiOiAxNTk2NzY0OTc0LCAidnAiOiB7InR5cGUiOiAiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsICJjcmVhdGVkIjogIjIwMjAtMDgtMDdUMDE6NDk6MzNaIiwgInZlcmlmaWFibGVDcmVkZW50aWFsIjogW3siaWQiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSNkaWRhcHAiLCAidHlwZSI6IFsiIl0sICJpc3N1ZXIiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJpc3N1YW5jZURhdGUiOiAiMjAyMC0wOC0wN1QwMTo0OTozM1oiLCAiZXhwaXJhdGlvbkRhdGUiOiAiMjAyNC0xMi0yN1QwODo1MzoyN1oiLCAiY3JlZGVudGlhbFN1YmplY3QiOiB7ImlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAiYXBwRGlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAicHVycG9zZSI6ICJkaWQ6ZWxhc3RvczppZWFBNVZNV3lkUW1WSnRNNWRhVzVob1RRcGN1VjM4bUhNIiwgInNjb3BlIjogWyJyZWFkIiwgIndyaXRlIl0sICJ1c2VyRGlkIjogImRpZDplbGFzdG9zOmlXRkFVWWhUYTM1YzFmUGUzaUNKdmloWkh4NnF1dW1ueW0ifSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAic2lnbmF0dXJlIjogIlN4RlkxQW5GLXhsU2dCTDUzYW5YdDRFOHFWNEptd0NkYUNXQVo4QmFpdnFKSTkwV2xkQ3Q4XzdHejllSm0zSlRNQTMxQjBrem5sSmVEUkJ3LXcyUU53In19XSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAicmVhbG0iOiAidGVzdGFwcCIsICJub25jZSI6ICI4NzMxNzJmNTg3MDFhOWVlNjg2ZjA2MzAyMDRmZWU1OSIsICJzaWduYXR1cmUiOiAidDYxV3dFM1pqR21EdktfZmtJM3h0ZkRGczFpNUFxVXVjZFIteEVDSVlzLTB4dHpNWGE2RTlkS0RFanJ3V2xwRjRUWElsTHduZlJWZXgzRl9KN0F6cUEifX19.";

    private static final String storePath = System.getProperty("user.dir");

    private static Database database;
    private static Client client;

    private String testCollection = "people10";
    private String testSchema = "{\"firstname\":{\"type\":\"string\",\"minlength\":1,\"maxlength\":10},\"lastname\":{\"type\":\"string\",\"minlength\":1,\"maxlength\":15,\"required\":true,\"unique\":true}}";

    private String item = "{\"firstname\": \"barack06\", \"lastname\": \"obama06\"}";

    private String itemPut = "{\"firstname\": \"barack01\", \"lastname\": \"obama01\"}";

    private String itemPatch = "{\"firstname\": \"barack02\", \"lastname\": \"obama02\"}";

    private String queryParams = "where=lastname==\"obama\""; //sort=-lastname，max_results=1&page=1，where={"lastname":"obama"}

    @Test
    public void testCreate() {
        try {
            database.createCol(testCollection, testSchema).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testInsert() {
        try {
            database.insert(testCollection, item).get();
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testQuery() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = database.query(testCollection, queryParams).get();
            Map<String, Object> testMapDes = mapper.readValue(ret, Map.class);
            System.out.println("map:" + testMapDes);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testPut() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = database.query(testCollection, queryParams).get();
            Map<String, Object> testMapDes = mapper.readValue(ret, Map.class);
            System.out.println("map:" + testMapDes);
            String _id = (String) testMapDes.get("_id");
            String _etag = (String) testMapDes.get("_etag");
            database.put(testCollection, _id, _etag, itemPut);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testPatch() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = database.query(testCollection, queryParams).get();
            Map<String, Object> testMapDes = mapper.readValue(ret, Map.class);
            System.out.println("map:" + testMapDes);
            String _id = (String) testMapDes.get("_id");
            String _etag = (String) testMapDes.get("_etag");
            database.patch(testCollection, _id, _etag, itemPatch);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDelete() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = database.query(testCollection, queryParams).get();
            Map<String, Object> testMapDes = mapper.readValue(ret, Map.class);
            System.out.println("map:" + testMapDes);
            String _id = (String) testMapDes.get("_id");
            String _etag = (String) testMapDes.get("_etag");
            database.delete(testCollection, _id, _etag);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testDrop() {
        try {
            database.dropCol(testCollection);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            Client.Options options = new VaultOptions
                    .Builder()
                    .setNodeUrl(nodeUrl)
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRedirectURL(redirectUri)
                    .setAuthToken(authToken)
                    .setStorePath(storePath)
                    .setAuthenticator(requestUrl -> {
                        try {
                            Desktop.getDesktop().browse(new URI(requestUrl));
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail();
                        }
                    })
                    .build();

            client = Client.createInstance(options);
            client.connect();
            database = client.getDatabase();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
