package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.hive.database.Date;
import org.elastos.hive.database.MaxKey;
import org.elastos.hive.database.MinKey;
import org.elastos.hive.database.ObjectId;
import org.elastos.hive.database.RegularExpression;
import org.elastos.hive.database.Timestamp;
import org.elastos.hive.scripting.AggregatedExecutable;
import org.elastos.hive.scripting.AndCondition;
import org.elastos.hive.scripting.Condition;
import org.elastos.hive.scripting.DbFindQuery;
import org.elastos.hive.scripting.DbInsertQuery;
import org.elastos.hive.scripting.Executable;
import org.elastos.hive.scripting.FileDownload;
import org.elastos.hive.scripting.FileHash;
import org.elastos.hive.scripting.FileProperties;
import org.elastos.hive.scripting.FileUpload;
import org.elastos.hive.scripting.OrCondition;
import org.elastos.hive.scripting.QueryHasResultsCondition;
import org.elastos.hive.scripting.RawCondition;
import org.elastos.hive.scripting.RawExecutable;
import org.elastos.hive.utils.JsonUtil;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.Reader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptingTest {
    private String testTextFilePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/test.txt";
    private String testCacheTextFilePath = System.getProperty("user.dir") + "/src/resources/org/elastos/hive/cache/script/test.txt";

    private String noConditionName = "get_groups";
    private String withConditionName = "get_group_messages";

    private static Scripting scripting;

    @Test
    public void test01_condition() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

        ObjectNode n = (ObjectNode) mapper.readTree(json);
        n.putPOJO("dateField", new Date());
        n.putPOJO("idField", new ObjectId("123123123123123123"));
        n.putPOJO("minKeyField", new MinKey(100));
        n.putPOJO("maxKeyField", new MaxKey(200));
        n.putPOJO("regexField", new RegularExpression("testpattern", "i"));
        n.putPOJO("tsField", new Timestamp(100000, 1234));

        Condition cond1 = new QueryHasResultsCondition("cond1", "c1", n);
        Condition cond2 = new QueryHasResultsCondition("cond2", "c2", n);
        Condition cond3 = new QueryHasResultsCondition("cond3", "c3", n);
        Condition cond4 = new QueryHasResultsCondition("cond4", "c4", n);
        Condition cond5 = new RawCondition(json);

        OrCondition orCond = new OrCondition("abc", new Condition[]{cond1, cond2});
        AndCondition andCond = new AndCondition("xyz", new Condition[]{cond3, cond4});

        OrCondition cond = new OrCondition("root");
        cond.append(orCond).append(cond5).append(andCond);

//        System.out.println(cond.serialize());
    }

    @Test
    public void test02_executable() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

        JsonNode n = mapper.readTree(json);

        Executable exec1 = new DbFindQuery("exec1", "c1", n);
        Executable exec2 = new DbFindQuery("exec2", "c2", n);
        Executable exec3 = new DbInsertQuery("exec3", "c3", n);
        Executable exec4 = new RawExecutable(json);

        AggregatedExecutable ae = new AggregatedExecutable("ae");
        ae.append(exec1).append(exec2).append(exec3);

//        System.out.println(ae.serialize());

        AggregatedExecutable ae2 = new AggregatedExecutable("ae2");
        ae2.append(exec1).append(exec2).append(ae).append(exec3);

//        System.out.println(ae2.serialize());
    }

    @Test
    public void test03_registerNoCondition() {
        try {
            JsonNode filter = JsonUtil.deserialize("{\"friends\":\"$caller_did\"}");
            JsonNode options = JsonUtil.deserialize("{\"projection\":{\"_id\":false,\"name\":true}}");
            Executable executable = new DbFindQuery("get_groups", "groups", filter, options);
            boolean success = scripting.registerScript(noConditionName, executable).get();
            assertTrue(success);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test04_registerWithCondition() {
        try {
            JsonNode filter = JsonUtil.deserialize("{\"_id\":\"$params.group_id\",\"friends\":\"$caller_did\"}");
            Executable executable = new DbFindQuery("get_groups", "test_group", filter);
            Condition condition = new QueryHasResultsCondition("verify_user_permission", "test_group", filter);
            boolean success = scripting.registerScript(withConditionName, condition, executable).get();
            assertTrue(success);
        } catch (Exception e) {
            fail();
        }
    }


    @Test
    public void test05_callStringType() {
        try {
            String ret = scripting.call(noConditionName, String.class).get();
            System.out.println("return=" + ret);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test06_callByteArrType() {
        try {
            byte[] ret = scripting.call(noConditionName, byte[].class).get();
            System.out.println("return=" + ret);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test07_callJsonNodeType() {
        try {
            JsonNode ret = scripting.call(noConditionName, JsonNode.class).get();
            System.out.println("return=" + ret);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test08_callReaderType() {
        try {
            Reader ret = scripting.call(noConditionName, Reader.class).get();
            System.out.println("return=" + ret);
        } catch (Exception e) {
            fail();
        }
    }

    //TODO CU-4ttmvx Test case: add test cases to fully verify supplementary functions
//    @Test
//    public void test09_callWithParams() {
//        try {
//            String param = "{\"group_id\":{\"$oid\":\"5f98e2a22c0b7f0520f29cbe\"}}";
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode params = objectMapper.readTree(param);
//
//            String ret = scripting.call(withConditionName, params, String.class).get();
//            System.out.println("return=" + ret);
//        } catch (Exception e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void test10_callOtherScript() {
//        try {
//            String ret = scripting.call(noConditionName, "appid", String.class).get();
//            System.out.println("return=" + ret);
//        } catch (Exception e) {
//            fail();
//        }
//    }

    @Test
    public void test11_setUploadScript() {
        try {
            Executable executable = new FileUpload("upload_file", "$params.path", true);
            boolean success = scripting.registerScript("upload_file", executable).get();
            assertTrue(success);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test12_uploadFile() {
        try {
            String metadata = "{\"name\":\"upload_file\",\"params\":{\"group_id\":{\"$oid\":\"5f8d9dfe2f4c8b7a6f8ec0f1\"},\"path\":\"test.txt\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode params = objectMapper.readTree(metadata);
            String ret = scripting.call(testTextFilePath, params, Scripting.Type.UPLOAD, String.class).get();
            assertNotNull(ret);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test13_setDownloadScript() {
        try {
            Executable executable = new FileDownload("download_file", "$params.path", true);
            boolean success = scripting.registerScript("download_file", executable).get();
            assertTrue(success);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test14_downloadFile() {
        try {
            String path = "{\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"test.txt\"}";
            JsonNode params = JsonUtil.deserialize(path);
            Reader reader = scripting.call("download_file", params, Scripting.Type.DOWNLOAD, Reader.class).get();
            Utils.cacheTextFile(reader, testCacheTextFilePath);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test15_setInfoScript() {
        try {
            FileHash fileHash = new FileHash("file_hash", "$params.path");
            FileProperties fileProperties = new FileProperties("file_properties", "$params.path");
            AggregatedExecutable executable = new AggregatedExecutable("file_properties_and_hash", new Executable[]{fileHash, fileProperties});

            boolean success = scripting.registerScript("get_file_info", executable).get();
            assertTrue(success);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void test16_getFileInfo() {
        try {
            String executable = "{\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"},\"path\":\"test.txt\"}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode params = objectMapper.readTree(executable);
            String ret = scripting.call("get_file_info", params, Scripting.Type.PROPERTIES, String.class).get();
            assertNotNull(ret);
        } catch (Exception e) {
            fail();
        }
    }

    @BeforeClass
    public static void setUp() {
        Vault vault = TestFactory.createFactory().getVault();
        scripting = vault.getScripting();
    }

//    @BeforeClass
//    public static void setUp() {
//        try {
//            String json = TestData.DOC_STR1;
//            DIDDocument doc = DIDDocument
//                    .fromJson(json);
//
//            Client.Options options = new Client.Options();
//            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
//                    -> TestData.ACCESS_TOKEN1));
//            options.setAuthenticationDIDDocument(doc);
//            options.setDIDResolverUrl("http://api.elastos.io:21606");
//            options.setLocalDataPath(localDataPath);
//
//            Client.setVaultProvider(TestData.OWNERDID1, TestData.PROVIDER1);
//            client = Client.createInstance(options);
//            scripting = client.getVault(TestData.OWNERDID1).get().getScripting();
//        } catch (Exception e) {
//            fail();
//        }
//    }
}
