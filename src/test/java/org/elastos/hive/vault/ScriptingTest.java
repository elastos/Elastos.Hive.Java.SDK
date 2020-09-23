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
import org.junit.Test;

import java.io.File;
import java.io.Reader;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;

public class ScriptingTest {
    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private static Scripting scripting;
    private static Client client;

	@Test
	public void testCondition() throws Exception {
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
	public void testExecutable() throws Exception {
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
    public void registerScriptNoCondition() {
        try {
            String json = "{\"type\":\"find\",\"name\":\"get_groups\",\"body\":{\"collection\":\"test_group\",\"filter\":{\"*caller_did\":\"friends\"}}}";
            boolean success = scripting.registerScript("script_no_condition", new RawExecutable(json)).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerScriptWithCondition() {
        try {
            String json = "{\"type\":\"find\",\"name\":\"get_groups\",\"body\":{\"collection\":\"test_group\",\"filter\":{\"*caller_did\":\"friends\"}}}";
            boolean success = scripting.registerScript("script_condition", null, new RawExecutable(json)).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callScriptNoParams() {
        try {
            String ret = scripting.call("script_no_condition", String.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callScriptStringType() {
        try {
            String ret = scripting.call("script_no_condition", String.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callScriptByteArrType() {
        try {
            byte[] ret = scripting.call("script_no_condition", byte[].class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callScriptJsonNodeType() {
        try {
            JsonNode ret = scripting.call("script_no_condition", JsonNode.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callScriptReaderType() {
        try {
            Reader ret = scripting.call("script_no_condition", Reader.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callScriptWithParams() {
        try {
            String param = "{\"group_id\":{\"$oid\":\"5f497bb83bd36ab235d82e6a\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode update = objectMapper.readTree(param);

            String ret = scripting.call("script_condition", update, String.class).get();
            System.out.println("return="+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeClass
    public static void setUp() {
        try {
            String json = TestData.DOC_STR;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestData.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestData.OWNERDID, TestData.PROVIDER);
            client = Client.createInstance(options);
            scripting = client.getVault(TestData.OWNERDID).get().getScripting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
