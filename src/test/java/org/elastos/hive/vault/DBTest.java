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
            String json = "{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"publicKey\":[{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"type\":\"ECDSAsecp256r1\",\"controller\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"publicKeyBase58\":\"tgmQDrEGg8QKNjy7hgm2675QFh7qUkfd4nDZ2AgZxYy5\"}],\"authentication\":[\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\"],\"verifiableCredential\":[{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#email\",\"type\":[\"BasicProfileCredential\",\"EmailCredential\",\"InternetAccountCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"email\":\"john@gmail.com\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"qEAxxPzAeSS7umKKKL-T0bMD7iUgUMnoHRsROupMjnXojLZdPF6KGmU80f7iy1nLDyuRx-dQLyIqBi0a1-vHaQ\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#passport\",\"type\":[\"BasicProfileCredential\",\"SelfProclaimedCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"nation\":\"Singapore\",\"passport\":\"S653258Z07\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"qbb8YXBp5DiOMsBur5iwOW0eJtnnEi2P_EznGG0rSg5daR6hvuSXKjywgBi-GShTCK1QOQMiBC2LINn-XyjXJg\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#profile\",\"type\":[\"BasicProfileCredential\",\"SelfProclaimedCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"email\":\"john@example.com\",\"language\":\"English\",\"name\":\"John\",\"nation\":\"Singapore\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"OOtRiXrGMnrmAu8D_2nwPkRhO6Qj8Hkp9qKbRiKTxSLA4wzbRtXesLav1n1FR3jFzddSSbsBGDXBzVD88B5tnw\"}},{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#twitter\",\"type\":[\"InternetAccountCredential\",\"TwitterCredential\"],\"issuer\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"issuanceDate\":\"2019-12-27T08:53:27Z\",\"expirationDate\":\"2024-12-27T08:53:27Z\",\"credentialSubject\":{\"id\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM\",\"twitter\":\"@john\"},\"proof\":{\"type\":\"ECDSAsecp256r1\",\"verificationMethod\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signature\":\"PE4NlCm1gk_dGRxJBb2XWVkYisuwsXmC_06oS7vBAnVOGpA_qYX1JWar7xTS6_oCzLSLus3IVfEXdG3xVK8gow\"}}],\"expires\":\"2024-12-27T08:53:27Z\",\"proof\":{\"type\":\"ECDSAsecp256r1\",\"created\":\"2019-12-27T08:53:27Z\",\"creator\":\"did:elastos:ijUnD4KeRpeBUFmcEDCbhxMTJRzUYCQCZM#primary\",\"signatureValue\":\"2p-wukVhrDfu0N-xe2ANqMDUbAfZ4ntLcTVvL4IXkB5jD7ZJhrnyqtAsF9kT6kVkHBSKFgcxPavo7Nws7x4JMQ\"}}";
            DIDDocument doc = DIDDocument
                    .fromJson(json);

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAiLCAia2lkIjogImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jcHJpbWFyeSJ9.eyJpc3MiOiJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNIiwic3ViIjoiRElEQXV0aFJlc3BvbnNlIiwiYXVkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlhdCI6MTU5OTUzNTQ2MywiZXhwIjoxNjAwMTM1NDYzLCJuYmYiOjE1OTk1MzU0NjMsInByZXNlbnRhdGlvbiI6eyJ0eXBlIjoiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsImNyZWF0ZWQiOiIyMDIwLTA5LTA4VDAzOjI0OjIzWiIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbeyJpZCI6ImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jZGlkYXBwIiwidHlwZSI6WyJBcHBJZENyZWRlbnRpYWwiXSwiaXNzdWVyIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlzc3VhbmNlRGF0ZSI6IjIwMjAtMDktMDhUMDM6MjQ6MjNaIiwiZXhwaXJhdGlvbkRhdGUiOiIyMDI0LTEyLTI3VDA4OjUzOjI3WiIsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImlkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImFwcERpZCI6ImFwcElkIn0sInByb29mIjp7InR5cGUiOiJFQ0RTQXNlY3AyNTZyMSIsInZlcmlmaWNhdGlvbk1ldGhvZCI6ImRpZDplbGFzdG9zOmlqVW5ENEtlUnBlQlVGbWNFRENiaHhNVEpSelVZQ1FDWk0jcHJpbWFyeSIsInNpZ25hdHVyZSI6Ik9PUFpwSXIzQmc4czQ0TUJjWHEwYW5oTk11aXFYS2ZpZzNiWXNyUkhJYmV1YVBLcUx6TTBGMkgxQnRhUEVfdVVsZWVKLUdZUkdoeENTVU1JcUd3Q2d3In19XSwicHJvb2YiOnsidHlwZSI6IkVDRFNBc2VjcDI1NnIxIiwidmVyaWZpY2F0aW9uTWV0aG9kIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSNwcmltYXJ5IiwicmVhbG0iOiJkaWQ6ZWxhc3RvczppalVuRDRLZVJwZUJVRm1jRURDYmh4TVRKUnpVWUNRQ1pNIiwibm9uY2UiOiJjN2JmYTlhYS1mMTgyLTExZWEtYjQ1YS02NDVhZWRlYjA3NjMiLCJzaWduYXR1cmUiOiJKQWR1ei0zRnJnUEI1dnZuXzFJVk5RUjJNendGWjVTZGViRDhIcDA1dmVqc3ZMSi1aNDZfSGl3RWJHakRIY0MxMEN0Mkdfc3IxaVBjY1VYWnhLeVNhUSJ9fX0.fl4W4FWKvHaJeL0ryTf0v7jZJD3WZyq2hH8p2tDJl9oiN9HumO2mHx6b7WVWMEelllwUzTM1gRhpTe-wObXyug"));
            options.setAuthenticationDIDDocument(doc);
            options.setDIDResolverUrl("http://api.elastos.io:21606");
            options.setLocalDataPath(localDataPath);

            Client.setVaultProvider("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX", "http://localhost:5000");
            client = Client.createInstance(options);
            database = client.getVault("did:elastos:idfpKJJ1soDxT2GcgCRnDt3cu94ZnGfzNX").get().getDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
