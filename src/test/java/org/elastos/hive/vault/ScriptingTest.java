package org.elastos.hive.vault;

import org.elastos.hive.Client;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.interfaces.Scripting;
import org.elastos.hive.scripting.conditions.Condition;
import org.elastos.hive.scripting.conditions.SubCondition;
import org.elastos.hive.scripting.conditions.database.QueryHasResultsCondition;
import org.elastos.hive.scripting.executables.Executable;
import org.elastos.hive.scripting.executables.ExecutionSequence;
import org.elastos.hive.scripting.executables.database.FindQuery;
import org.elastos.hive.vendor.vault.VaultOptions;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Desktop;
import java.net.URI;

import static org.junit.Assert.fail;

public class ScriptingTest {
    private static final String clientId = "1098324333865-q7he5l91a4pqnuq9s2pt5btj9kenebkl.apps.googleusercontent.com";
    private static final String clientSecret = "0Ekmgx8dPbSxnTxxF-fqxjnz";
    private static final String redirectUri = "http://localhost:12345";
    private static final String nodeUrl = "http://127.0.0.1:5000";

    private static final String authToken = "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAifQ.eyJpc3MiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJzdWIiOiAiRElEQXV0aENyZWRlbnRpYWwiLCAiYXVkIjogIkhpdmUiLCAiaWF0IjogMTU5Njc2NDk3NCwgImV4cCI6IDE1OTY3NzQ5NzQsICJuYmYiOiAxNTk2NzY0OTc0LCAidnAiOiB7InR5cGUiOiAiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsICJjcmVhdGVkIjogIjIwMjAtMDgtMDdUMDE6NDk6MzNaIiwgInZlcmlmaWFibGVDcmVkZW50aWFsIjogW3siaWQiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSNkaWRhcHAiLCAidHlwZSI6IFsiIl0sICJpc3N1ZXIiOiAiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsICJpc3N1YW5jZURhdGUiOiAiMjAyMC0wOC0wN1QwMTo0OTozM1oiLCAiZXhwaXJhdGlvbkRhdGUiOiAiMjAyNC0xMi0yN1QwODo1MzoyN1oiLCAiY3JlZGVudGlhbFN1YmplY3QiOiB7ImlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAiYXBwRGlkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0iLCAicHVycG9zZSI6ICJkaWQ6ZWxhc3RvczppZWFBNVZNV3lkUW1WSnRNNWRhVzVob1RRcGN1VjM4bUhNIiwgInNjb3BlIjogWyJyZWFkIiwgIndyaXRlIl0sICJ1c2VyRGlkIjogImRpZDplbGFzdG9zOmlXRkFVWWhUYTM1YzFmUGUzaUNKdmloWkh4NnF1dW1ueW0ifSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAic2lnbmF0dXJlIjogIlN4RlkxQW5GLXhsU2dCTDUzYW5YdDRFOHFWNEptd0NkYUNXQVo4QmFpdnFKSTkwV2xkQ3Q4XzdHejllSm0zSlRNQTMxQjBrem5sSmVEUkJ3LXcyUU53In19XSwgInByb29mIjogeyJ0eXBlIjogIkVDRFNBc2VjcDI1NnIxIiwgInZlcmlmaWNhdGlvbk1ldGhvZCI6ICJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNI3ByaW1hcnkiLCAicmVhbG0iOiAidGVzdGFwcCIsICJub25jZSI6ICI4NzMxNzJmNTg3MDFhOWVlNjg2ZjA2MzAyMDRmZWU1OSIsICJzaWduYXR1cmUiOiAidDYxV3dFM1pqR21EdktfZmtJM3h0ZkRGczFpNUFxVXVjZFIteEVDSVlzLTB4dHpNWGE2RTlkS0RFanJ3V2xwRjRUWElsTHduZlJWZXgzRl9KN0F6cUEifX19.";

    private static final String storePath = System.getProperty("user.dir");

    private static Database database;
    private static Scripting scripting;
    private static Client client;

    private static final String GROUPS_COLLECTION_NAME = "groups";

//    private void registerSubConditionUserInGroup() {
//        JSONObject queryParams = new JSONObject();
//        queryParams.put("id", "$groupid"); // $groupid is passed by the calling script
//        queryParams.put("friends", new JSONObject("{$contains: \"$callerdid\"}")); // Forgot the right mongo syntax here.
//
//        Condition condition = new QueryHasResultsCondition(GROUPS_COLLECTION_NAME, queryParams);
//        scripting.registerSubCondition("userInGroup", condition);
//    }
//
//    @Test
//    public void testRegisterGetGroups() {
//        try {
//            ExecutionSequence executionSequence = new ExecutionSequence(new Executable[]{
//                    new FindQuery(GROUPS_COLLECTION_NAME)
//            });
//
//            scripting.setScript("getGroups", executionSequence).get();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }
//
//    @Test
//    public void testRegisterGetGroupMessages() {
//        try {
//            registerSubConditionUserInGroup();
//
//            // Execution sequence
//            JSONObject queryParams = new JSONObject();
//            queryParams.put("id", "$groupid");
//            ExecutionSequence executionSequence = new ExecutionSequence( new Executable[] {
//                    new FindQuery(GROUPS_COLLECTION_NAME, queryParams)
//            });
//
//            // Access condition - user must be in the group to get messages
//            Condition accessCondition = new SubCondition("userInGroup");
//
//            // Register the script
//            scripting.setScript("getGroupMessages", executionSequence, accessCondition).get();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }
//
//    @Test
//    public void testAddFriendToGroup() {
//        try {
////            database.patch(GROUPS_COLLECTION_NAME, "TODO", "TODO", "TODO");
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail();
//        }
//    }
//
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
//            scripting = client.getScripting();
//            database = client.getDatabase();
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//    }
}
