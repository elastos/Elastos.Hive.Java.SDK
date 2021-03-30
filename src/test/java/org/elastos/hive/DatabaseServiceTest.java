package org.elastos.hive;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.elastos.did.exception.DIDException;
import org.elastos.hive.config.TestData;
import org.elastos.hive.database.*;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.DatabaseService;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseServiceTest {
	private static final String COLLECTION_NAME = "works";

	private static DatabaseService databaseService;

	@BeforeAll
	public static void setUp() {
		try {
			databaseService = TestData.getInstance().newVault().getDatabaseService();
		} catch (HiveException | DIDException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(1)
	public void testCreateCollection() {
		try {
			Boolean isSuccess = databaseService.createCollection(COLLECTION_NAME, null)
					.exceptionally(e-> {
						fail();
						return null;
					})
					.get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(2)
	public void testInsertOne() {
		try {
			ObjectNode docNode = JsonNodeFactory.instance.objectNode();
			docNode.put("author", "john doe1");
			docNode.put("title", "Eve for Dummies1");
			InsertOneResult result = databaseService.insertOne(COLLECTION_NAME, docNode, new InsertOptions(false, true)).exceptionally(e->{
				fail();
				return null;
			}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(3)
	public void testInsertMany() {
		try {
			List<JsonNode> nodes = new ArrayList<>();
			ObjectNode docNode1 = JsonNodeFactory.instance.objectNode();
			docNode1.put("author", "john doe2");
			docNode1.put("title", "Eve for Dummies2");
			nodes.add(docNode1);
			ObjectNode docNode2 = JsonNodeFactory.instance.objectNode();
			docNode2.put("author", "john doe3");
			docNode2.put("title", "Eve for Dummies3");
			nodes.add(docNode1);
			InsertManyResult result = databaseService.insertMany(COLLECTION_NAME, nodes,
					new InsertOptions(false, true))
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(4)
	public void testFindOne() {
		try {
			ObjectNode query = JsonNodeFactory.instance.objectNode();
			query.put("author", "john doe1");
			JsonNode doc = databaseService.findOne(COLLECTION_NAME, query,
					new FindOptions().setSkip(0L)
							.setAllowPartialResults(false)
							.setReturnKey(false)
							.setBatchSize(0)
							.setProjection(Collections.singletonMap("_id", false)))
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
			assertNotNull(doc);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(5)
	public void testFindMany() {
		try {
			ObjectNode query = JsonNodeFactory.instance.objectNode();
			query.put("author", "john doe1");
			List<JsonNode> docs = databaseService.findMany(COLLECTION_NAME, query,
					new FindOptions().setSkip(0L)
							.setAllowPartialResults(false)
							.setReturnKey(false)
							.setBatchSize(0)
							.setProjection(Collections.singletonMap("_id", false)))
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(6)
	public void testCountDoc() {
		try {
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe1");
			Long count = databaseService.countDocuments(COLLECTION_NAME, filter,
					new CountOptions().setLimit(1L).setSkip(0L).setMaxTimeMS(1000000000))
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(7)
	public void testUpdateOne() {
		try {
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe1");
			ObjectNode doc = JsonNodeFactory.instance.objectNode();
			doc.put("author", "john doe1");
			doc.put("title", "Eve for Dummies2");
			ObjectNode update = JsonNodeFactory.instance.objectNode();
			update.put("$set", doc);
			UpdateResult result = databaseService.updateOne(COLLECTION_NAME, filter, update,
					new UpdateOptions().setBypassDocumentValidation(false).setUpsert(true))
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(8)
	public void testUpdateMany() {
		try {
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe1");
			ObjectNode doc = JsonNodeFactory.instance.objectNode();
			doc.put("author", "john doe1");
			doc.put("title", "Eve for Dummies2");
			ObjectNode update = JsonNodeFactory.instance.objectNode();
			update.put("$set", doc);
			UpdateResult result = databaseService.updateMany(COLLECTION_NAME, filter, update,
					new UpdateOptions().setBypassDocumentValidation(false).setUpsert(true))
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(9)
	public void testDeleteOne() {
		try {
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe2");
			DeleteResult result = databaseService.deleteOne(COLLECTION_NAME, filter, new DeleteOptions())
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(10)
	public void testDeleteMany() {
		try {
			ObjectNode filter = JsonNodeFactory.instance.objectNode();
			filter.put("author", "john doe2");
			DeleteResult result = databaseService.deleteMany(COLLECTION_NAME, filter, new DeleteOptions())
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
			assertNotNull(result);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Order(11)
	public void testDeleteCollection() {
		try {
			Boolean isSuccess = databaseService.deleteCollection(COLLECTION_NAME)
					.exceptionally(e -> {
						fail();
						return null;
					}).get();
			assertTrue(isSuccess);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
