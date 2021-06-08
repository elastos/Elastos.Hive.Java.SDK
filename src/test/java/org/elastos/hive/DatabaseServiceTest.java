package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elastos.hive.config.TestData;
import org.elastos.hive.service.DatabaseService;
import org.elastos.hive.vault.database.*;
import org.junit.jupiter.api.*;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseServiceTest {
	private static final String COLLECTION_NAME = "works";

	private static DatabaseService databaseService;

	@BeforeAll public static void setUp() {
		Assertions.assertDoesNotThrow(()->databaseService = TestData.getInstance()
				.newVault().getDatabaseService());
	}

	@Test @Order(1) void testCreateCollection() {
		Assertions.assertDoesNotThrow(()->{
			Boolean isSuccess = databaseService.createCollection(COLLECTION_NAME).get();
			Assertions.assertTrue(isSuccess);
		});
	}

	@Test @Order(2) void testInsertOne() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode docNode = JsonNodeFactory.instance.objectNode();
			docNode.put("author", "john doe1");
			docNode.put("title", "Eve for Dummies1");
			Assertions.assertNotNull(databaseService.insertOne(COLLECTION_NAME, docNode,
					new InsertOptions().setBypassDocumentValidation(false)).get());
		});
	}

	@Test @Order(3) void testInsertMany() {
		Assertions.assertDoesNotThrow(()->{
			List<JsonNode> nodes = new ArrayList<>();
			ObjectNode docNode1 = JsonNodeFactory.instance.objectNode();
			docNode1.put("author", "john doe2");
			docNode1.put("title", "Eve for Dummies2");
			nodes.add(docNode1);
			ObjectNode docNode2 = JsonNodeFactory.instance.objectNode();
			docNode2.put("author", "john doe3");
			docNode2.put("title", "Eve for Dummies3");
			nodes.add(docNode2);
			Assertions.assertNotNull(databaseService.insertMany(COLLECTION_NAME, nodes,
					new InsertOptions().setBypassDocumentValidation(false).setOrdered(true)).get());
		});
	}

	@Test @Order(4) void testFindOne() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode query = JsonNodeFactory.instance.objectNode();
			query.put("author", "john doe1");
			Assertions.assertNotNull(databaseService.findOne(COLLECTION_NAME, query,
					new FindOptions().setSkip(0).setLimit(0)).get());
		});
	}

	@Test @Order(5) void testFindMany() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode query = JsonNodeFactory.instance.objectNode();
			query.put("author", "john doe1");
			List<JsonNode> docs = databaseService.findMany(COLLECTION_NAME, query,
					new FindOptions().setSkip(0).setLimit(0)).get();
		});
	}

	@Test @Order(6) void testQuery() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode query = JsonNodeFactory.instance.objectNode();
			query.put("author", "john doe1");
			Assertions.assertNotNull(databaseService.query(COLLECTION_NAME, query, null).get());
		});
	}

	@Test @Order(7) void testCountDoc() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe1");
			Assertions.assertNotNull(databaseService.countDocuments(COLLECTION_NAME, filter,
					new CountOptions().setLimit(1L).setSkip(0L).setMaxTimeMS(1000000000L)).get());
		});
	}

	@Test @Order(8) void testUpdateOne() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe1");
			ObjectNode doc = JsonNodeFactory.instance.objectNode();
			doc.put("author", "john doe1");
			doc.put("title", "Eve for Dummies1_1");
			ObjectNode update = JsonNodeFactory.instance.objectNode();
			update.put("$set", doc);
			Assertions.assertNotNull(databaseService.updateOne(COLLECTION_NAME, filter, update,
					new UpdateOptions().setBypassDocumentValidation(false).setUpsert(true)).get());
		});
	}

	@Test @Order(9) void testUpdateMany() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe1");
			ObjectNode doc = JsonNodeFactory.instance.objectNode();
			doc.put("author", "john doe1");
			doc.put("title", "Eve for Dummies1_2");
			ObjectNode update = JsonNodeFactory.instance.objectNode();
			update.put("$set", doc);
			Assertions.assertNotNull(databaseService.updateMany(COLLECTION_NAME, filter, update,
					new UpdateOptions().setBypassDocumentValidation(false).setUpsert(true)).get());
		});
	}

	@Test @Order(10) void testDeleteOne() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe2");
			databaseService.deleteOne(COLLECTION_NAME, filter).get();
		});
	}

	@Test @Order(11) void testDeleteMany() {
		Assertions.assertDoesNotThrow(()->{
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe2");
			databaseService.deleteMany(COLLECTION_NAME, filter).get();
		});
	}

	@Test @Order(12) void testDeleteCollection() {
		Assertions.assertDoesNotThrow(()->Assertions.assertTrue(
				databaseService.deleteCollection(COLLECTION_NAME).get()));
	}
}
