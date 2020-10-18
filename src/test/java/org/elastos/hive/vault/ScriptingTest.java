package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.Scripting;
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
import org.elastos.hive.scripting.OrCondition;
import org.elastos.hive.scripting.QueryHasResultsCondition;
import org.elastos.hive.scripting.RawCondition;
import org.elastos.hive.scripting.RawExecutable;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.Reader;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ScriptingTest {
    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private String noConditionName = "get_groups";
    private String withConditionName = "get_group_messages";

    private static Scripting scripting;
    private static Client client;

    private static Scripting scriptingOther;
    private static Client clientOther;

	@Test
	public void test01_condition() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

        ObjectNode n = (ObjectNode)mapper.readTree(json);
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

        OrCondition orCond = new OrCondition("abc", new Condition[] { cond1, cond2});
        AndCondition andCond = new AndCondition("xyz", new Condition[] { cond3, cond4});

        OrCondition cond = new OrCondition("root");
        cond.append(orCond).append(cond5).append(andCond);

        System.out.println(cond.serialize());
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

        System.out.println(ae.serialize());

        AggregatedExecutable ae2 = new AggregatedExecutable("ae2");
        ae2.append(exec1).append(exec2).append(ae).append(exec3);

        System.out.println(ae2.serialize());
	}

	@Test
    public void test03_registerNoCondition() {
        try {
            String executable = "{\"type\":\"find\",\"name\":\"get_groups\",\"output\":true,\"body\":{\"collection\":\"groups\",\"filter\":{\"friends\":\"$caller_did\"},\"options\":{\"projection\":{\"_id\":false,\"name\":true}}}}";
            boolean success = scripting.registerScript(noConditionName, new RawExecutable(executable)).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test04_registerWithCondition() {
        try {
            String executable = "{\"type\":\"find\",\"name\":\"get_groups\",\"body\":{\"collection\":\"test_group\",\"filter\":{\"friends\":\"$caller_did\"}}}";
            String condition = "{\"type\":\"queryHasResults\",\"name\":\"verify_user_permission\",\"body\":{\"collection\":\"test_group\",\"filter\":{\"_id\":\"$params.group_id\",\"friends\":\"$caller_did\"}}}";
            boolean success = scripting.registerScript(withConditionName, new RawCondition(condition), new RawExecutable(executable)).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test05_callStringType() {
        try {
            String ret = scripting.call(noConditionName, String.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test06_callByteArrType() {
        try {
            byte[] ret = scripting.call(noConditionName, byte[].class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test07_callJsonNodeType() {
        try {
            JsonNode ret = scripting.call(noConditionName, JsonNode.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test08_callReaderType() {
        try {
            Reader ret = scripting.call(noConditionName, Reader.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test09_callWithParams() {
        try {
            String param = "{\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode params = objectMapper.readTree(param);

            String ret = scripting.call(withConditionName, params, String.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test10_callOtherScript() {
        try {
            String ret = scripting.call(noConditionName,"appid", String.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @BeforeClass
//    public static void setUp() {
//        try {
//            String json = TestData.DOC_STR;
//            DIDDocument doc = DIDDocument
//                    .fromJson(json);
//
//            Client.Options options = new Client.Options();
//            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
//                    -> TestData.ACCESS_TOKEN));
//            options.setAuthenticationDIDDocument(doc);
//            options.setDIDResolverUrl("http://api.elastos.io:21606");
//            options.setLocalDataPath(localDataPath);
//
//            Client.setVaultProvider(TestData.OWNERDID, TestData.PROVIDER);
//            client = Client.createInstance(options);
//            scripting = client.getVault(TestData.OWNERDID).get().getScripting();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @BeforeClass
    public static void setUp() {
        try {
            String json = TestData.DOC_STR1;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestData.ACCESS_TOKEN1));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestData.OWNERDID1, TestData.PROVIDER1);
            client = Client.createInstance(options);
            scripting = client.getVault(TestData.OWNERDID1).get().getScripting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
