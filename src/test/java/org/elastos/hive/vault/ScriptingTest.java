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
import org.elastos.hive.exception.HiveException;
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
import java.util.concurrent.CompletableFuture;

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
            scripting.registerScript("script_no_condition", new RawExecutable(json)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerScriptWithCondition() {
        try {
            String json = "{\"type\":\"find\",\"name\":\"get_groups\",\"body\":{\"collection\":\"test_group\",\"filter\":{\"*caller_did\":\"friends\"}}}";
            scripting.registerScript("script_condition", null, new RawExecutable(json));
        } catch (HiveException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void callScriptNoParams() {
        try {
            String ret = scripting.call("script_no_condition", String.class).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public @Test void callScriptWithParams() {
        try {
//            scripting.call("script_condition", );
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

//            DID did = new DID("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX");
//            DIDBackend.initialize("http://api.elastos.io:21606", localDataPath);
//            ResolverCache.reset();
//            DIDDocument doc = did.resolve();
//            String json = doc.toString();

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAiLCAia2lkIjogImRpZDplbGFzdG9zOmlkZnBLSkoxc29EeFQyR2NnQ1JuRHQzY3U5NFpuR2Z6TlgjcHJpbWFyeSJ9.eyJpc3MiOiJkaWQ6ZWxhc3RvczppZGZwS0pKMXNvRHhUMkdjZ0NSbkR0M2N1OTRabkdmek5YIiwic3ViIjoiRElEQXV0aFJlc3BvbnNlIiwiYXVkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlhdCI6MTU5OTE1MDg1MSwiZXhwIjoxNTk5MTUwOTExLCJuYmYiOjE1OTkxNTA4NTEsInByZXNlbnRhdGlvbiI6eyJ0eXBlIjoiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsImNyZWF0ZWQiOiIyMDIwLTA5LTAzVDE2OjM0OjExWiIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbeyJpZCI6ImRpZDplbGFzdG9zOmlkZnBLSkoxc29EeFQyR2NnQ1JuRHQzY3U5NFpuR2Z6TlgjZGlkYXBwIiwidHlwZSI6WyJBcHBBdXRoQ3JlZGVudGlhbCJdLCJpc3N1ZXIiOiJkaWQ6ZWxhc3RvczppajhrckFWUkppdFpLSm1jQ3Vmb0xIUWpxN01lZjNaalROIiwiaXNzdWFuY2VEYXRlIjoiMjAyMC0wOS0wM1QxNjozNDoxMVoiLCJleHBpcmF0aW9uRGF0ZSI6IjIwMjUtMDktMDFUMTk6NDc6MjRaIiwiY3JlZGVudGlhbFN1YmplY3QiOnsiaWQiOiJkaWQ6ZWxhc3RvczppZGZwS0pKMXNvRHhUMkdjZ0NSbkR0M2N1OTRabkdmek5YIiwiYXBwSWQiOiJhcHBJZCJ9LCJwcm9vZiI6eyJ0eXBlIjoiRUNEU0FzZWNwMjU2cjEiLCJ2ZXJpZmljYXRpb25NZXRob2QiOiJkaWQ6ZWxhc3RvczppajhrckFWUkppdFpLSm1jQ3Vmb0xIUWpxN01lZjNaalROI3ByaW1hcnkiLCJzaWduYXR1cmUiOiIyT1pNNkVvTTJ0LU5LdDEwLWtQZFpnbjlHdXBna3pKdlYyX2lmMzVlMjVHSnlTYmp3cHVDcUJaQjFWa2ZieldMY0VHaEwyeHQxTWZWY1JQWFBkZUN2ZyJ9fV0sInByb29mIjp7InR5cGUiOiJFQ0RTQXNlY3AyNTZyMSIsInZlcmlmaWNhdGlvbk1ldGhvZCI6ImRpZDplbGFzdG9zOmlkZnBLSkoxc29EeFQyR2NnQ1JuRHQzY3U5NFpuR2Z6TlgjcHJpbWFyeSIsInJlYWxtIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsIm5vbmNlIjoiNDlhMTJlMjItZWUwMy0xMWVhLWI4NzItNjQ1YWVkZWIwNzYzIiwic2lnbmF0dXJlIjoiVnR0bDlLQmRjQXhIMDlMSVR0bDMzVTlnZTJQWjRWUWMzbWdNLW5uX1g4RllINEg4VDVzQlMzZ2ZvbEYzZ3VxYmRNWmF6U0laenByY0NCR0ZiY0N2bGcifX19.0HpH0g3Z1QzUEisLkg4NTwxgne4Sz7bsNSuFVmN6legvZu0W_ActGOylV9ojE5Ulk4bwxsizci_Sg9vV_ihI4Q"));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX", "http://localhost:5000");
            client = Client.createInstance(options);
            scripting = client.getVault("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX").get().getScripting();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
