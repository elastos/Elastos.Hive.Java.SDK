package org.elastos.hive;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DatabaseTest {

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
		CompletableFuture<Boolean> future = database.createCollection(collectionName, null)
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test05_InsertOneNoCallback() {
		ObjectNode docNode = JsonNodeFactory.instance.objectNode();
		docNode.put("author", "john doe1");
		docNode.put("title", "Eve for Dummies1");

		InsertOptions insertOptions = new InsertOptions();
		insertOptions.bypassDocumentValidation(false).ordered(true);

		CompletableFuture<Boolean> future =  database.insertOne(collectionName, docNode, insertOptions)
				.handle((success, ex) -> (ex == null));

		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test06_InsertManyNoCallback() {
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

		CompletableFuture<Boolean> future = database.insertMany(collectionName, nodes, insertOptions)
				.handle((success, ex) -> (ex == null));
		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test07_FindOneNoCallback() {
		ObjectNode query = JsonNodeFactory.instance.objectNode();
		query.put("author", "john doe1");

		FindOptions findOptions = new FindOptions();
		findOptions.skip(0)
				.allowPartialResults(false)
				.returnKey(false)
				.batchSize(0)
				.projection(jsonToMap("{\"_id\": false}"));

		CompletableFuture<Boolean> future = database.findOne(collectionName, query, findOptions)
				.handle((success, ex) -> (ex == null));
		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test08_FindManyNoCallback() {
		ObjectNode query = JsonNodeFactory.instance.objectNode();
		query.put("author", "john doe1");

		FindOptions findOptions = new FindOptions();
		findOptions.skip(0)
				.allowPartialResults(false)
				.returnKey(false)
				.batchSize(0)
				.projection(jsonToMap("{\"_id\": false}"));

		CompletableFuture<Boolean> future = database.findMany(collectionName, query, findOptions)
				.handle((success, ex) -> (ex == null));
		try {
			assertTrue(future.get());
			assertTrue(future.isCompletedExceptionally() == false);
			assertTrue(future.isDone());
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void test09_countDocNoCallback() throws ExecutionException, InterruptedException {
		ObjectNode filter = JsonNodeFactory.instance.objectNode();
		filter.put("author", "john doe1");

		CountOptions options = new CountOptions();
		options.limit(1).skip(0).maxTimeMS(1000000000);

		database.countDocuments(collectionName, filter, options).whenComplete((result, throwable) -> {
			assertNull(throwable);
			System.out.println("count=" + result);
		}).get();
	}

	@Test
	public void test10_UpdateOneNoCallback() throws JsonProcessingException, ExecutionException, InterruptedException {
        ObjectNode filter = JsonNodeFactory.instance.objectNode();
        filter.put("author", "john doe1");

        String updateJson = "{\"$set\":{\"author\":\"john doe1\",\"title\":\"Eve for Dummies2\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode update = objectMapper.readTree(updateJson);

        UpdateOptions updateOptions = new UpdateOptions();
        updateOptions.upsert(true).bypassDocumentValidation(false);

        database.updateOne(collectionName, filter, update, updateOptions).whenComplete((result, throwable) -> {
            assertNull(throwable);
            assertNotNull(result);
            System.out.println("matchedCount=" + result.matchedCount());
            System.out.println("modifiedCount=" + result.modifiedCount());
            System.out.println("upsertedCount=" + result.upsertedCount());
            System.out.println("upsertedId=" + result.upsertedId());
        }).get();
	}

	@Test
	public void test11_UpdateManyNoCallback() throws JsonProcessingException, ExecutionException, InterruptedException {
        ObjectNode filter = JsonNodeFactory.instance.objectNode();
        filter.put("author", "john doe1");

        String updateJson = "{\"$set\":{\"author\":\"john doe1\",\"title\":\"Eve for Dummies2\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode update = objectMapper.readTree(updateJson);

        UpdateOptions updateOptions = new UpdateOptions();
        updateOptions.upsert(true).bypassDocumentValidation(false);

        database.updateMany(collectionName, filter, update, updateOptions).whenComplete((result, throwable) -> {
            assertNull(throwable);
            assertNotNull(result);
            System.out.println("matchedCount=" + result.matchedCount());
            System.out.println("modifiedCount=" + result.modifiedCount());
            System.out.println("upsertedCount=" + result.upsertedCount());
            System.out.println("upsertedId=" + result.upsertedId());
        }).get();
	}

	@Test
	public void test12_DeleteOneNoCallback() throws ExecutionException, InterruptedException {
        ObjectNode filter = JsonNodeFactory.instance.objectNode();
        filter.put("author", "john doe2");

        DeleteOptions deleteOptions = new DeleteOptions();

        database.deleteOne(collectionName, filter, null).whenComplete((result, throwable) -> {
            assertNull(throwable);
            assertNotNull(result);
            System.out.println("delete count=" + result.deletedCount());
        }).get();
	}

	@Test
	public void test13_DeleteManyNoCallback() throws ExecutionException, InterruptedException {
        ObjectNode filter = JsonNodeFactory.instance.objectNode();
        filter.put("author", "john doe2");

        DeleteOptions deleteOptions = new DeleteOptions();

        database.deleteMany(collectionName, filter, null).whenComplete((result, throwable) -> {
            assertNull(throwable);
            assertNotNull(result);
            System.out.println("delete count=" + result.deletedCount());
        }).get();
	}

	@Test
	public void test14_deleteColNoCallback() throws ExecutionException, InterruptedException {
        database.deleteCollection(collectionName).whenComplete((result, throwable) -> {
			assertNull(throwable);
        	assertTrue(result);
        }).get();
	}

	private static Database database;

	private static Map<String, Object> jsonToMap(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> p = mapper.readValue(json, new TypeReference<Map<String, Object>>() {
			});
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@BeforeClass
	public static void setUp() {
		Vault vault = UserFactory.createUser2().getVault();
		database = vault.getDatabase();
	}
}
