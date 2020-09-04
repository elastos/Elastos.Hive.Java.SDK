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

	@Test
    public void testDbCreate() {
	    try {
            database.createCollection("works", null, new Callback<Boolean>() {
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
    public void deleteCollection() {
        try {
            database.deleteCollection("works", new Callback<Boolean>() {
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
    public void testInsertOne() {
	    try {
            ObjectNode docNode = JsonNodeFactory.instance.objectNode();
            docNode.put("author", "john doe1");
            docNode.put("title", "Eve for Dummies2");

//            InsertOptions insertOptions = new InsertOptions();
//            insertOptions.bypassDocumentValidation(false).ordered(true);

            database.insertOne("works", docNode, /*insertOptions*/null, new Callback<InsertResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(InsertResult result) {
                    assertNotNull(result);
                    System.out.println("acknowledged="+result.get("acknowledged"));
                    System.out.println("inserted_id="+result.get("inserted_id"));
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testInsertMany() {
        try {
            List<JsonNode> nodes = new ArrayList();
            ObjectNode docNode = JsonNodeFactory.instance.objectNode();
            docNode.put("author", "john doe1");
            docNode.put("title", "Eve for Dummies2");
            nodes.add(docNode);

            InsertOptions insertOptions = new InsertOptions();
            insertOptions.bypassDocumentValidation(false).ordered(true);

            database.insertMany("works", nodes, insertOptions, new Callback<InsertResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(InsertResult result) {
                    assertNotNull(result);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindOne() {
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

            database.findOne("works", query, null, new Callback<JsonNode>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(JsonNode result) {
                    assertNotNull(result);
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testFindMany() {
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

            database.findMany("works", query, findOptions, new Callback<List<JsonNode>>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(List<JsonNode> result) {

                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdateOne() {
	    try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            ObjectNode update = JsonNodeFactory.instance.objectNode();
            update.put("author", "john doe2");
            update.put("title", "Eve for Dummies2_1");

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true).bypassDocumentValidation(false);

            database.updateOne("works", filter, update, updateOptions, new Callback<UpdateResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(UpdateResult result) {
                    assertNotNull(result);
                    System.out.println("modifiedCount="+result.modifiedCount());
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testUpdateMany() {
	    try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe1");

            ObjectNode update = JsonNodeFactory.instance.objectNode();
            update.put("author", "john doe2");
            update.put("title", "Eve for Dummies2_1");

            UpdateOptions updateOptions = new UpdateOptions();
            updateOptions.upsert(true).bypassDocumentValidation(false);

            database.updateMany("works", filter, update, updateOptions, new Callback<UpdateResult>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(UpdateResult result) {

                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testDeleteOne() {
	    try {

            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            DeleteOptions deleteOptions = new DeleteOptions();

            database.deleteOne("works", filter, null, new Callback<DeleteResult>() {
                @Override
                public void onError(HiveException e) {
                    fail();
                }

                @Override
                public void onSuccess(DeleteResult result) {
                    assertNotNull(result);
                    System.out.println("deletedCount="+result.deletedCount());
                }
            }).get();
        } catch (Exception e) {
	        e.printStackTrace();
        }
    }

    @Test
    public void testDeleteMany() {
        try {
            ObjectNode filter = JsonNodeFactory.instance.objectNode();
            filter.put("author", "john doe2");

            DeleteOptions deleteOptions = new DeleteOptions();

            database.deleteMany("works", filter, null, new Callback<DeleteResult>() {
                @Override
                public void onError(HiveException e) {

                }

                @Override
                public void onSuccess(DeleteResult result) {

                }
            }).get();
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

            Client.Options options = new Client.Options();
            options.setAuthenticationHandler(jwtToken -> CompletableFuture.supplyAsync(()
                    -> "eyJhbGciOiAiRVMyNTYiLCAidHlwZSI6ICJKV1QiLCAidmVyc2lvbiI6ICIxLjAiLCAia2lkIjogImRpZDplbGFzdG9zOmlkZnBLSkoxc29EeFQyR2NnQ1JuRHQzY3U5NFpuR2Z6TlgjcHJpbWFyeSJ9.eyJpc3MiOiJkaWQ6ZWxhc3RvczppZGZwS0pKMXNvRHhUMkdjZ0NSbkR0M2N1OTRabkdmek5YIiwic3ViIjoiRElEQXV0aFJlc3BvbnNlIiwiYXVkIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsImlhdCI6MTU5OTE4MzMwMCwiZXhwIjoxNTk5MTgzMzYwLCJuYmYiOjE1OTkxODMzMDAsInByZXNlbnRhdGlvbiI6eyJ0eXBlIjoiVmVyaWZpYWJsZVByZXNlbnRhdGlvbiIsImNyZWF0ZWQiOiIyMDIwLTA5LTA0VDAxOjM1OjAwWiIsInZlcmlmaWFibGVDcmVkZW50aWFsIjpbeyJpZCI6ImRpZDplbGFzdG9zOmlkZnBLSkoxc29EeFQyR2NnQ1JuRHQzY3U5NFpuR2Z6TlgjZGlkYXBwIiwidHlwZSI6WyJBcHBBdXRoQ3JlZGVudGlhbCJdLCJpc3N1ZXIiOiJkaWQ6ZWxhc3RvczppajhrckFWUkppdFpLSm1jQ3Vmb0xIUWpxN01lZjNaalROIiwiaXNzdWFuY2VEYXRlIjoiMjAyMC0wOS0wNFQwMTozNTowMFoiLCJleHBpcmF0aW9uRGF0ZSI6IjIwMjUtMDktMDFUMTk6NDc6MjRaIiwiY3JlZGVudGlhbFN1YmplY3QiOnsiaWQiOiJkaWQ6ZWxhc3RvczppZGZwS0pKMXNvRHhUMkdjZ0NSbkR0M2N1OTRabkdmek5YIiwiYXBwSWQiOiJhcHBJZCJ9LCJwcm9vZiI6eyJ0eXBlIjoiRUNEU0FzZWNwMjU2cjEiLCJ2ZXJpZmljYXRpb25NZXRob2QiOiJkaWQ6ZWxhc3RvczppajhrckFWUkppdFpLSm1jQ3Vmb0xIUWpxN01lZjNaalROI3ByaW1hcnkiLCJzaWduYXR1cmUiOiIwZndRcXlITXpRZG54bTNpNWZIaXo2aWtCNHBmdnRKU1hsZ0R2My02NWJVWDJLYW43SGhRXzkzVXBxeDRVaEZoZ3R0Y0luVE1UcVk5UFRkWm9BbDVaQSJ9fV0sInByb29mIjp7InR5cGUiOiJFQ0RTQXNlY3AyNTZyMSIsInZlcmlmaWNhdGlvbk1ldGhvZCI6ImRpZDplbGFzdG9zOmlkZnBLSkoxc29EeFQyR2NnQ1JuRHQzY3U5NFpuR2Z6TlgjcHJpbWFyeSIsInJlYWxtIjoiZGlkOmVsYXN0b3M6aWpVbkQ0S2VScGVCVUZtY0VEQ2JoeE1USlJ6VVlDUUNaTSIsIm5vbmNlIjoiZDViNTQ5ZmMtZWU0ZS0xMWVhLWFmNDEtNjQ1YWVkZWIwNzYzIiwic2lnbmF0dXJlIjoiUkxSWExlOXQxNENpcWFlZXBsb19iNENLVkJiVUhNZ2JhbW5PYnc5bHFlUmpQMHFqbm5UV0lJWmNtY3BEU1Fyc2JPRHdOT1ZYNGd1Mi11ZjhVaDg1SkEifX19.5EfIK3XI0dQTsR9GU07uhJNtNGEivBv-UpN-ViS04VFaKI9Gi_rKSwhRatHrADAO0DybN3Dy2oMCLgn5kayC7w"));
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
