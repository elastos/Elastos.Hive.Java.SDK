package org.elastos.hive.vault;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
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
import org.elastos.hive.Callback;
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
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.InsertResult;
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
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DatabaseTest {

    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private static Database database;
    private static Client client;

    private static Map<String, Object> jsonToMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> p = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
        return p;
    }

	@Test
	public void testDbOptions() throws Exception {
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
		co.hint(new Index[] { new Index("idx_01", Index.Order.ASCENDING),
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
			.hint(new Index[] { new Index("didurl", Index.Order.ASCENDING), new Index("type", Index.Order.DESCENDING)})
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
	public void testDbResults() throws Exception {
		String json = "{\"deleted_count\":1000}";
		DeleteResult ds = DeleteResult.deserialize(json);
		assertEquals(1000, ds.deletedCount());
		json = ds.serialize();
		ds = DeleteResult.deserialize(json);
		assertEquals(1000, ds.deletedCount());

		json = "{\"acknowledged\":true,\"inserted_id\":\"test_inserted_id\"}";
		InsertResult ir = InsertResult.deserialize(json);
		assertTrue(ir.acknowledged());
		assertEquals("test_inserted_id", ir.insertedId());
		assertNull(ir.insertedIds());
		json = ir.serialize();
		ir = InsertResult.deserialize(json);
		assertTrue(ir.acknowledged());
		assertEquals("test_inserted_id", ir.insertedId());
		assertNull(ir.insertedIds());

		json = "{\"acknowledged\":false,\"inserted_ids\":[\"test_inserted_id1\",\"test_inserted_id2\"]}";
		ir = InsertResult.deserialize(json);
		assertFalse(ir.acknowledged());
		List<String> ids = ir.insertedIds();
		assertNotNull(ids);
		assertEquals(2, ids.size());
		assertNull(ir.insertedId());
		json = ir.serialize();
		ir = InsertResult.deserialize(json);
		assertFalse(ir.acknowledged());
		ids = ir.insertedIds();
		assertNotNull(ids);
		assertEquals(2, ids.size());
		assertNull(ir.insertedId());

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

		protected TestDBDataTypes() {}
	}

	@Test
	public void testDbDataTypes() throws Exception {
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
    public void testCreateColNoCallback() {
        try {
            Boolean success = database.createCollection(collectionName, null).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Test
    public void testCreateColWithCallback() {
	    try {
            database.createCollection(collectionName, null, new Callback<Boolean>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Boolean result) {
                    assertTrue(result);
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void deleteColNoCallback() {
        try {
            database.deleteCollection(collectionName, new Callback<Boolean>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Boolean result) {
                    assertTrue(result);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteColWithCallback() {
        try {
            Boolean success = database.deleteCollection(collectionName).get();
            assertTrue(success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertOneNoCallback() {
        try {
            ObjectNode docNode = JsonNodeFactory.instance.objectNode();
            docNode.put("author", "john doe1");
            docNode.put("title", "Eve for Dummies2");

            InsertOptions insertOptions = new InsertOptions();
            insertOptions.bypassDocumentValidation(false).ordered(true);

            InsertResult result = database.insertOne(collectionName, docNode, insertOptions).get();
            assertNotNull(result);
            System.out.println("acknowledged="+result.acknowledged());
            List<String> ids = result.insertedIds();
            assertNotNull(ids);
            assertTrue(ids.size()>0);
            for(String id : ids) {
                System.out.println("id="+id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertOneWithCallback() {
	    try {
            ObjectNode docNode = JsonNodeFactory.instance.objectNode();
            docNode.put("author", "john doe1");
            docNode.put("title", "Eve for Dummies2");

            InsertOptions insertOptions = new InsertOptions();
            insertOptions.bypassDocumentValidation(false).ordered(true);

            database.insertOne(collectionName, docNode, insertOptions, new Callback<InsertResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(InsertResult result) {
                    System.out.println("acknowledged="+result.acknowledged());
                    List<String> ids = result.insertedIds();
                    assertNotNull(ids);
                    assertTrue(ids.size()>0);
                    for(String id : ids) {
                        System.out.println("id="+id);
                    }
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testInsertManyNoCallback() {
        try {
            List<JsonNode> nodes = new ArrayList<JsonNode>();
            ObjectNode docNode = JsonNodeFactory.instance.objectNode();
            docNode.put("author", "john doe1");
            docNode.put("title", "Eve for Dummies2");
            nodes.add(docNode);

            InsertOptions insertOptions = new InsertOptions();
            insertOptions.bypassDocumentValidation(false).ordered(true);

            InsertResult result = database.insertMany(collectionName, nodes, insertOptions).get();
            assertNotNull(result);
            System.out.println("acknowledged="+result.acknowledged());
            List<String> ids = result.insertedIds();
            assertNotNull(ids);
            assertTrue(ids.size()>0);
            for(String id : ids) {
                System.out.println("id="+id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertManyWithCallback() {
        try {
            List<JsonNode> nodes = new ArrayList<JsonNode>();
            ObjectNode docNode = JsonNodeFactory.instance.objectNode();
            docNode.put("author", "john doe1");
            docNode.put("title", "Eve for Dummies2");
            nodes.add(docNode);

            InsertOptions insertOptions = new InsertOptions();
            insertOptions.bypassDocumentValidation(false).ordered(true);

            database.insertMany(collectionName, nodes, insertOptions, new Callback<InsertResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(InsertResult result) {
                    assertNotNull(result);
                    System.out.println("acknowledged="+result.acknowledged());
                    List<String> ids = result.insertedIds();
                    assertNotNull(ids);
                    assertTrue(ids.size()>0);
                    for(String id : ids) {
                        System.out.println("id="+id);
                    }
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindOneNoCallback() {
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
            System.out.println("result="+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindOneWithCallback() {
	    try {
            ObjectNode query = JsonNodeFactory.instance.objectNode();
            query.put("author", "john doe1");

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(jsonToMap("{\"_id\": false}"));

            database.findOne(collectionName, query, findOptions, new Callback<JsonNode>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(JsonNode result) {
                    assertNotNull(result);
                    assertNotNull(result);
                    System.out.println("result="+result.toString());
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testFindManyNoCallback() {
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
            assertTrue(result.size()>0);
            System.out.println("result="+result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindManyWithCallback() {
        try {
            ObjectNode query = JsonNodeFactory.instance.objectNode();
            query.put("author", "john doe1");

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(jsonToMap("{\"_id\": false}"));

            database.findMany(collectionName, query, findOptions, new Callback<List<JsonNode>>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(List<JsonNode> result) {
                    assertNotNull(result);
                    assertTrue(result.size()>0);
                    System.out.println("result="+result.toString());
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateOneNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            String updateJson = "{\"$set\":{\"author\":\"john doe1\",\"title\":\"Eve for Dummies2\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode update = objectMapper.readTree(updateJson);

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true).bypassDocumentValidation(false);

            UpdateResult result = database.updateOne(collectionName, filter, update, updateOptions).get();
            if(null == result) return;
            System.out.println("matchedCount="+result.matchedCount());
            System.out.println("modifiedCount="+result.modifiedCount());
            System.out.println("upsertedCount="+result.upsertedCount());
            System.out.println("upsertedId="+result.upsertedId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateOneWithCallback() {
	    try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            ObjectNode update = JsonNodeFactory.instance.objectNode();
            update.put("author", "john doe2");
            update.put("title", "Eve for Dummies2_1");

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true).bypassDocumentValidation(false);

            database.updateOne(collectionName, filter, update, updateOptions, new Callback<UpdateResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(UpdateResult result) {
                    assertNotNull(result);
                    System.out.println("matchedCount="+result.matchedCount());
                    System.out.println("modifiedCount="+result.modifiedCount());
                    System.out.println("upsertedCount="+result.upsertedCount());
                    System.out.println("upsertedId="+result.upsertedId());
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testUpdateManyNoCallback() {
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
            System.out.println("matchedCount="+result.matchedCount());
            System.out.println("modifiedCount="+result.modifiedCount());
            System.out.println("upsertedCount="+result.upsertedCount());
            System.out.println("upsertedId="+result.upsertedId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateManyWithCallback() {
	    try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            String updateJson = "{\"$set\":{\"author\":\"john doe1\",\"title\":\"Eve for Dummies2\"}}";
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode update = objectMapper.readTree(updateJson);

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true).bypassDocumentValidation(false);

            database.updateMany(collectionName, filter, update, updateOptions, new Callback<UpdateResult>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(UpdateResult result) {
                    assertNotNull(result);
                    System.out.println("matchedCount="+result.matchedCount());
                    System.out.println("modifiedCount="+result.modifiedCount());
                    System.out.println("upsertedCount="+result.upsertedCount());
                    System.out.println("upsertedId="+result.upsertedId());
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void countDocNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            CountOptions options = new CountOptions();
            options.limit(1).skip(0).maxTimeMS(1000000000);

            long count = database.countDocuments(collectionName, filter, options).get();
            System.out.println("count="+count);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void countDocWithCallback() {
	    try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");


            database.countDocuments(collectionName, filter, null, new Callback<Long>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(Long result) {
                    System.out.println("count="+result);
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testDeleteOneNoCallback() {
        try {

            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            DeleteOptions deleteOptions = new DeleteOptions();

            DeleteResult deleteResult = database.deleteOne(collectionName, filter, null).get();
            System.out.println("delete count="+deleteResult.deletedCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteOneWithCallback() {
        try {

            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            database.deleteOne(collectionName, filter, null, new Callback<DeleteResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(DeleteResult result) {
                    assertNotNull(result);
                    System.out.println("delete count="+result.deletedCount());
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteManyNoCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            DeleteOptions deleteOptions = new DeleteOptions();

            DeleteResult result = database.deleteMany(collectionName, filter, null).get();
            System.out.println("delete count="+result.deletedCount());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteManyWithCallback() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            DeleteOptions deleteOptions = new DeleteOptions();

            database.deleteMany(collectionName, filter, null, new Callback<DeleteResult>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(DeleteResult result) {
                    System.out.println("delete count="+result.deletedCount());
                }
            }).get();
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
