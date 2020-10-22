package org.elastos.hive.vault;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Client;
import org.elastos.hive.Database;
import org.elastos.hive.database.Collation;
import org.elastos.hive.database.Collation.Alternate;
import org.elastos.hive.database.Collation.CaseFirst;
import org.elastos.hive.database.Collation.MaxVariable;
import org.elastos.hive.database.Collation.Strength;
import org.elastos.hive.database.CountOptions;
import org.elastos.hive.database.CreateCollectionOptions;
import org.elastos.hive.database.Date;
import org.elastos.hive.database.DeleteOptions;
import org.elastos.hive.database.DeleteResult;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.Index;
import org.elastos.hive.database.InsertManyResult;
import org.elastos.hive.database.InsertOneResult;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.MaxKey;
import org.elastos.hive.database.MinKey;
import org.elastos.hive.database.ObjectId;
import org.elastos.hive.database.ReadConcern;
import org.elastos.hive.database.ReadPreference;
import org.elastos.hive.database.RegularExpression;
import org.elastos.hive.database.Timestamp;
import org.elastos.hive.database.UpdateOptions;
import org.elastos.hive.database.UpdateResult;
import org.elastos.hive.database.WriteConcern;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseTest {

    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private static Database database;
    private static Client client;

    private static Map<String, Object> jsonToMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> p = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
        });
        return p;
    }

    @Test
    public void test01_DbOptions() throws Exception {
        Collation collation = new Collation();
        collation.locale("en_us")
                .alternate(Alternate.SHIFTED)
                .backwards(true)
                .caseFirst(CaseFirst.OFF)
                .caseLevel(true)
                .maxVariable(MaxVariable.PUNCT)
                .normalization(true)
                .numericOrdering(false)
                .strength(Strength.PRIMARY);

        CountOptions co = new CountOptions();
        co.collation(collation)
                .hint(new Index("idx_01", Index.Order.ASCENDING))
                .limit(100)
                .maxTimeMS(1000)
                .skip(50);

        String json = co.serialize();
        co = CountOptions.deserialize(json);
        String json2 = co.serialize();
        assertEquals(json, json2);

        co = new CountOptions();
        co.hint(new Index[]{new Index("idx_01", Index.Order.ASCENDING),
                new Index("idx_02", Index.Order.DESCENDING)})
                .limit(100);

        json = co.serialize();
        co = CountOptions.deserialize(json);
        json2 = co.serialize();
        assertEquals(json, json2);

        collation = new Collation();
        collation.locale("en_us")
                .alternate(Alternate.SHIFTED)
                .normalization(true)
                .numericOrdering(false)
                .strength(Strength.PRIMARY);

        CreateCollectionOptions cco = new CreateCollectionOptions();
        cco.capped(true)
                .collation(collation)
                .max(10)
                .readConcern(ReadConcern.AVAILABLE)
                .readPreference(ReadPreference.PRIMARY_PREFERRED)
                .writeConcern(new WriteConcern(10, 100, true, false))
                .size(123456);

        json = cco.serialize();
        cco = CreateCollectionOptions.deserialize(json);
        json2 = cco.serialize();
        assertEquals(json, json2);

        WriteConcern wc = new WriteConcern();
        wc.fsync(true);
        wc.w(10);

        cco = new CreateCollectionOptions();
        cco.capped(true)
                .collation(collation)
                .readPreference(ReadPreference.PRIMARY_PREFERRED)
                .writeConcern(wc);

        json = cco.serialize();
        cco = CreateCollectionOptions.deserialize(json);
        json2 = cco.serialize();
        assertEquals(json, json2);

        DeleteOptions dopt = new DeleteOptions();
        dopt.collation(collation);

        json = dopt.serialize();
        dopt = DeleteOptions.deserialize(json);
        json2 = dopt.serialize();
        assertEquals(json, json2);

        FindOptions fo = new FindOptions();
        String projection = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

        fo.allowDiskUse(true)
                .batchSize(100)
                .collation(collation)
                .hint(new Index[]{new Index("didurl", Index.Order.ASCENDING), new Index("type", Index.Order.DESCENDING)})
                .projection(jsonToMap(projection))
                .max(10);

        json = fo.serialize();
        fo = FindOptions.deserialize(json);
        json2 = fo.serialize();
        assertEquals(json, json2);

        InsertOptions io = new InsertOptions();
        io.bypassDocumentValidation(true);

        json = io.serialize();
        io = InsertOptions.deserialize(json);
        json2 = io.serialize();
        assertEquals(json, json2);

        UpdateOptions uo = new UpdateOptions();
        uo.bypassDocumentValidation(true)
                .collation(collation)
                .upsert(true);

        json = uo.serialize();
        uo = UpdateOptions.deserialize(json);
        json2 = uo.serialize();
        assertEquals(json, json2);
    }

    @Test
    public void test02_DbResults() throws Exception {
        String json = "{\"deleted_count\":1000}";
        DeleteResult ds = DeleteResult.deserialize(json);
        assertEquals(1000, ds.deletedCount());
        json = ds.serialize();
        ds = DeleteResult.deserialize(json);
        assertEquals(1000, ds.deletedCount());

        json = "{\"acknowledged\":true,\"inserted_id\":\"test_inserted_id\"}";
        InsertOneResult ior = InsertOneResult.deserialize(json);
        assertTrue(ior.acknowledged());
        assertEquals("test_inserted_id", ior.insertedId());
        json = ior.serialize();
        ior = InsertOneResult.deserialize(json);
        assertTrue(ior.acknowledged());
        assertEquals("test_inserted_id", ior.insertedId());

        json = "{\"acknowledged\":false,\"inserted_ids\":[\"test_inserted_id1\",\"test_inserted_id2\"]}";
        InsertManyResult imr = InsertManyResult.deserialize(json);
        assertFalse(imr.acknowledged());
        List<String> ids = imr.insertedIds();
        assertNotNull(ids);
        assertEquals(2, ids.size());
        json = imr.serialize();
        imr = InsertManyResult.deserialize(json);
        assertFalse(imr.acknowledged());
        ids = imr.insertedIds();
        assertNotNull(ids);
        assertEquals(2, ids.size());

        json = "{\"matched_count\":10,\"modified_count\":5,\"upserted_count\":3,\"upserted_id\":\"test_id\"}";
        UpdateResult ur = UpdateResult.deserialize(json);
        assertEquals(10, ur.matchedCount());
        assertEquals(5, ur.modifiedCount());
        assertEquals(3, ur.upsertedCount());
        assertEquals("test_id", ur.upsertedId());
        json = ur.serialize();
        ur = UpdateResult.deserialize(json);
        assertEquals(10, ur.matchedCount());
        assertEquals(5, ur.modifiedCount());
        assertEquals(3, ur.upsertedCount());
        assertEquals("test_id", ur.upsertedId());
    }

    public static class TestDBDataTypes {
        @JsonProperty("testDate")
        protected Date date;
        @JsonProperty("testMaxKey")
        protected MaxKey maxKey;
        @JsonProperty("testMinKey")
        protected MinKey minKey;
        @JsonProperty("testObjectId")
        protected ObjectId oid;
        @JsonProperty("testTimestamp")
        protected Timestamp ts;
        @JsonProperty("testRegex")
        protected RegularExpression regex;

        protected TestDBDataTypes() {
        }
    }

    @Test
    public void test03_DbDataTypes() throws Exception {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("testDate", new Date());
        values.put("testMaxKey", new MaxKey(10000));
        values.put("testMinKey", new MinKey(10));
        values.put("testObjectId", new ObjectId("iiiiiiiidddddddd"));
        values.put("testTimestamp", new Timestamp(123456, 789));
        values.put("testRegex", new RegularExpression("*FooBar", "all"));

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(values);

        TestDBDataTypes tdt = mapper.readValue(json, TestDBDataTypes.class);
        json = mapper.writeValueAsString(tdt);

        TestDBDataTypes tdt2 = mapper.readValue(json, TestDBDataTypes.class);
        String json2 = mapper.writeValueAsString(tdt2);
        assertEquals(json, json2);
    }

    private static final String collectionName = "works";

    @Test
    public void test04_CreateColNoCallback() {
        try {
            Boolean success = database.createCollection(collectionName, null).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test05_InsertOneNoCallback() {
        try {
            ObjectNode docNode = JsonNodeFactory.instance.objectNode();
            docNode.put("author", "john doe1");
            docNode.put("title", "Eve for Dummies1");

            InsertOptions insertOptions = new InsertOptions();
            insertOptions.bypassDocumentValidation(false).ordered(true);

            InsertOneResult result = database.insertOne(collectionName, docNode, insertOptions).get();
            assertNotNull(result);
            System.out.println("acknowledged=" + result.acknowledged());
            String id = result.insertedId();
            assertNotNull(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test06_InsertManyNoCallback() {
        try {
            List<JsonNode> nodes = new ArrayList<JsonNode>();
            ObjectNode docNode1 = JsonNodeFactory.instance.objectNode();
            docNode1.put("author", "john doe2");
            docNode1.put("title", "Eve for Dummies2");
            nodes.add(docNode1);
            ObjectNode docNode2 = JsonNodeFactory.instance.objectNode();
            docNode2.put("author", "john doe3");
            docNode2.put("title", "Eve for Dummies3");
            nodes.add(docNode1);

            InsertOptions insertOptions = new InsertOptions();
            insertOptions.bypassDocumentValidation(false).ordered(true);

            InsertManyResult result = database.insertMany(collectionName, nodes, insertOptions).get();
            assertNotNull(result);
            System.out.println("acknowledged=" + result.acknowledged());
            List<String> ids = result.insertedIds();
            assertNotNull(ids);
            assertTrue(ids.size() > 0);
            for (String id : ids) {
                System.out.println("id=" + id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test07_FindOneNoCallback() {
        try {
            ObjectNode query = JsonNodeFactory.instance.objectNode();
            query.put("author", "john doe1");

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(jsonToMap("{\"_id\": false}"));

            JsonNode result = database.findOne(collectionName, query, findOptions).get();
            assertNotNull(result);
            System.out.println("result=" + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test08_FindManyNoCallback() {
        try {
            ObjectNode query = JsonNodeFactory.instance.objectNode();
            query.put("author", "john doe1");

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(jsonToMap("{\"_id\": false}"));

            List<JsonNode> result = database.findMany(collectionName, query, findOptions).get();
            assertNotNull(result);
            System.out.println("result=" + result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test09_countDocNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            CountOptions options = new CountOptions();
            options.limit(1).skip(0).maxTimeMS(1000000000);

            long count = database.countDocuments(collectionName, filter, options).get();
            System.out.println("count=" + count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test10_UpdateOneNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            String updateJson = "{\"$set\":{\"author\":\"john doe1\",\"title\":\"Eve for Dummies2\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode update = objectMapper.readTree(updateJson);

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true).bypassDocumentValidation(false);

            UpdateResult result = database.updateOne(collectionName, filter, update, updateOptions).get();
            if (null == result) return;
            System.out.println("matchedCount=" + result.matchedCount());
            System.out.println("modifiedCount=" + result.modifiedCount());
            System.out.println("upsertedCount=" + result.upsertedCount());
            System.out.println("upsertedId=" + result.upsertedId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test11_UpdateManyNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            String updateJson = "{\"$set\":{\"author\":\"john doe1\",\"title\":\"Eve for Dummies2\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode update = objectMapper.readTree(updateJson);

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true).bypassDocumentValidation(false);

            UpdateResult result = database.updateMany(collectionName, filter, update, updateOptions).get();
            assertNotNull(result);
            System.out.println("matchedCount=" + result.matchedCount());
            System.out.println("modifiedCount=" + result.modifiedCount());
            System.out.println("upsertedCount=" + result.upsertedCount());
            System.out.println("upsertedId=" + result.upsertedId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test12_DeleteOneNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            DeleteOptions deleteOptions = new DeleteOptions();

            DeleteResult deleteResult = database.deleteOne(collectionName, filter, null).get();
            System.out.println("delete count=" + deleteResult.deletedCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test13_DeleteManyNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            DeleteOptions deleteOptions = new DeleteOptions();

            DeleteResult result = database.deleteMany(collectionName, filter, null).get();
            System.out.println("delete count=" + result.deletedCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test14_deleteColNoCallback() {
        try {
            Boolean success = database.deleteCollection(collectionName).get();
            assertTrue(success);
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
            database = client.getVault(TestData.OWNERDID).get().getDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
