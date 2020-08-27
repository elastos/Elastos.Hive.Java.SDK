package org.elastos.hive.vault;

import org.elastos.hive.Client;
import org.elastos.hive.Database;
import org.elastos.hive.database.Collation;
import org.elastos.hive.database.Collation.Alternate;
import org.elastos.hive.database.Collation.CaseFirst;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.Index;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DBTest {
    private static final String clientId = "1098324333865-q7he5l91a4pqnuq9s2pt5btj9kenebkl.apps.googleusercontent.com";
    private static final String clientSecret = "0Ekmgx8dPbSxnTxxF-fqxjnz";
    private static final String redirectUri = "http://localhost:12345";
    private static final String nodeUrl = "http://127.0.0.1:5000";

    private static final String authToken = "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAifQ.eyJpc3MiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJzdWIiOiAiRElEQXV0aENyZWRlbnRpYWwiLCAiYXVkIjogIkhpdmUiLCAiaWF0IjogMTU5Njc2NDk3NCwgImV4cCI6IDE1OTY3NzQ5NzQsICJuYmYiOiAxNTk2NzY0OTc0LCAidnAiOiB7InR5cGUiOiAiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsICJjcmVhdGVkIjogIjIwMjAtMDgtMDdUMDE6NDk6MzNaIiwgInZlcmlmaWFibGVDcmVkZW50aWFsIjogW3siaWQiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSNkaWRhcHAiLCAidHlwZSI6IFsiIl0sICJpc3N1ZXIiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJpc3N1YW5jZURhdGUiOiAiMjAyMC0wOC0wN1QwMTo0OTozM1oiLCAiZXhwaXJhdGlvbkRhdGUiOiAiMjAyNC0xMi0yN1QwODo1MzoyN1oiLCAiY3JlZGVudGlhbFN1YmplY3QiOiB7ImlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAiYXBwRGlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAicHVycG9zZSI6ICJkaWQ6ZWxhc3RvczppZWFBNVZNV3lkUW1WSnRNNWRhVzVob1RRcGN1VjM4bUhNIiwgInNjb3BlIjogWyJyZWFkIiwgIndyaXRlIl0sICJ1c2VyRGlkIjogImRpZDplbGFzdG9zOmlXRkFVWWhUYTM1YzFmUGUzaUNKdmloWkh4NnF1dW1ueW0ifSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAic2lnbmF0dXJlIjogIlN4RlkxQW5GLXhsU2dCTDUzYW5YdDRFOHFWNEptd0NkYUNXQVo4QmFpdnFKSTkwV2xkQ3Q4XzdHejllSm0zSlRNQTMxQjBrem5sSmVEUkJ3LXcyUU53In19XSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAicmVhbG0iOiAidGVzdGFwcCIsICJub25jZSI6ICI4NzMxNzJmNTg3MDFhOWVlNjg2ZjA2MzAyMDRmZWU1OSIsICJzaWduYXR1cmUiOiAidDYxV3dFM1pqR21EdktfZmtJM3h0ZkRGczFpNUFxVXVjZFIteEVDSVlzLTB4dHpNWGE2RTlkS0RFanJ3V2xwRjRUWElsTHduZlJWZXgzRl9KN0F6cUEifX19.";

    private static final String storePath = System.getProperty("user.dir");

    private static Database database;
    private static Client client;

    private String testCollection = "people08";
    private String testSchema = "{\"firstname\":{\"type\":\"string\",\"minlength\":1,\"maxlength\":10},\"lastname\":{\"type\":\"string\",\"minlength\":1,\"maxlength\":15,\"required\":true,\"unique\":true}}";

    private String item = "{\"firstname\": \"barack\", \"lastname\": \"obama\"}";

    private String itemPut = "{\"firstname\": \"barack\", \"lastname\": \"obama\"}";

    private String itemPatch = "{\"firstname\": \"barack\", \"lastname\": \"obama\"}";

    private String queryParams = "where=lastname==\"obama\""; //sort=-lastname，max_results=1&page=1，where={"lastname":"obama"}

	@Test
	public void testDbOptions() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

        JsonNode p = mapper.readTree(json);

		Collation co = new Collation();
		co.locale("en_us")
			.alternate(Alternate.SHIFTED)
			.backwards(true)
			.caseFirst(CaseFirst.OFF)
			.caseLevel(true);

		FindOptions fo = new FindOptions();

		fo.allowDiskUse(true)
			.batchSize(100)
			.collation(co)
			.hint(new Index[] { new Index("didurl", Index.Order.ASCENDING), new Index("type", Index.Order.DESCENDING)})
			.projection(p)
			.max(10);

		System.out.println(fo.serialize());
	}
//    @BeforeClass
//    public static void setUp() {
//        try {
//            Client.Options options = new VaultOptions
//                    .Builder()
//                    .setNodeUrl(nodeUrl)
//                    .setClientId(clientId)
//                    .setClientSecret(clientSecret)
//                    .setRedirectURL(redirectUri)
//                    .setAuthToken(authToken)
//                    .setStorePath(storePath)
//                    .setAuthenticator(requestUrl -> {
//                        try {
//                            Desktop.getDesktop().browse(new URI(requestUrl));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            fail();
//                        }
//                    })
//                    .build();
//
//            client = Client.createInstance(options);
//            client.connect();
//            database = client.getDatabase();
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
}
