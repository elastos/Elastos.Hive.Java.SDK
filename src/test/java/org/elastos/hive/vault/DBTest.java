package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.elastos.did.DIDDocument;
import org.elastos.hive.Callback;
import org.elastos.hive.Client;
import org.elastos.hive.Database;
import org.elastos.hive.database.Collation;
import org.elastos.hive.database.Collation.Alternate;
import org.elastos.hive.database.Collation.CaseFirst;
import org.elastos.hive.database.CountOptions;
import org.elastos.hive.database.DeleteOptions;
import org.elastos.hive.database.DeleteResult;
import org.elastos.hive.database.FindOptions;
import org.elastos.hive.database.Index;
import org.elastos.hive.database.InsertOptions;
import org.elastos.hive.database.InsertResult;
import org.elastos.hive.database.UpdateOptions;
import org.elastos.hive.database.UpdateResult;
import org.elastos.hive.exception.HiveException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DBTest {

    private static final String localDataPath = System.getProperty("user.dir") + File.separator + "store";

    private static Database database;
    private static Client client;

	@Test
	public void testDbOptions() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"name\":\"mkyong\", \"age\":37, \"c\":[\"adc\",\"zfy\",\"aaa\"], \"d\": {\"foo\": 1, \"bar\": 2}}";

        JsonNode p = mapper.readTree(json);

		Collation co = new Collation();
		co.locale("en_us")
			.alternate(Alternate.SHIFTED)
			.backwards(true)
			.caseFirst(CaseFirst.OFF)
			.caseLevel(true);

		FindOptions fo = new FindOptions();

		fo.allowDiskUse(true)
			.batchSize(100)
			.collation(co)
			.hint(new Index[] { new Index("didurl", Index.Order.ASCENDING), new Index("type", Index.Order.DESCENDING)})
			.projection(p)
			.max(10);

		System.out.println(fo.serialize());
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
            List<JsonNode> nodes = new ArrayList();
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
            List<JsonNode> nodes = new ArrayList();
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

            ObjectMapper objectMapper = new ObjectMapper();

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(objectMapper.readTree("{\"_id\": false}"));

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

            ObjectMapper objectMapper = new ObjectMapper();

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(objectMapper.readTree("{\"_id\": false}"));

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

            ObjectMapper objectMapper = new ObjectMapper();

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(objectMapper.readTree("{\"_id\": false}"));

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

            ObjectMapper objectMapper = new ObjectMapper();

            FindOptions findOptions = new FindOptions();
            findOptions.skip(0)
                    .allowPartialResults(false)
                    .returnKey(false)
                    .batchSize(0)
                    .projection(objectMapper.readTree("{\"_id\": false}"));

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
            String json = TestConstance.DOC_STR;
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> TestConstance.ACCESS_TOKEN));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider(TestConstance.OWNERDID, TestConstance.PROVIDER);
            client = Client.createInstance(options);
            database = client.getVault(TestConstance.OWNERDID).get().getDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
