package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elastos.hive.Client;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.interfaces.Database;
import org.elastos.hive.vendor.vault.DatabaseImp;
import org.elastos.hive.vendor.vault.VaultOptions;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

public class DBTest {
    private static final String NODEURL = "http://127.0.0.1:5000";

    private static Database database;
    private String testCollection = "people";
    private String testSchema = "{\n" +
            "    \"firstname\": {\n" +
            "        \"type\":  \"string\",\n" +
            "        \"minlength\": 1,\n" +
            "        \"maxlength\": 10\n" +
            "    },\n" +
            "    \"lastname\": {\n" +
            "        \"type\": \"string\",\n" +
            "        \"minlength\": 1,\n" +
            "        \"maxlength\": 15,\n" +
            "        \"required\": true,\n" +
            "        \"unique\": true\n" +
            "    }\n" +
            "}\n";

    private String item = "{\"firstname\": \"barack\", \"lastname\": \"obama\"}";

    private String itemPut = "{\"firstname\": \"barack01\", \"lastname\": \"obama01\"}";

    private String itemPatch = "{\"firstname\": \"barack02\", \"lastname\": \"obama02\"}";

    private String queryParams = "where=lastname==\"obama\""; //sort=-lastname，max_results=1&page=1，where={"lastname":"obama"}

    @BeforeClass
    public static void  setUp() {

        try {
            Client.Options options = new VaultOptions
                    .Builder()
                    .setNodeUrl(NODEURL)
                    .build();
        } catch (HiveException e) {
            e.printStackTrace();
        }

        database = new DatabaseImp();
    }

    @Test
    public void testCreate() {
        database.createCol(testCollection, testSchema);
    }

    @Test
    public void testPost() {
        database.post(testCollection, item);
    }

    @Test
    public void testGet() {
        database.get(testCollection, queryParams);
    }

    @Test
    public void testPut() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = database.get(testCollection, queryParams).get();
            Map<String, Object> testMapDes = mapper.readValue(ret, Map.class);
            System.out.println("map:" + testMapDes);
            String _id = (String) testMapDes.get("_id");
            String _etag = (String) testMapDes.get("_etag");
            database.put(testCollection, _id, _etag, itemPut);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPatch() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = database.get(testCollection, queryParams).get();
            Map<String, Object> testMapDes = mapper.readValue(ret, Map.class);
            System.out.println("map:" + testMapDes);
            String _id = (String) testMapDes.get("_id");
            String _etag = (String) testMapDes.get("_etag");
            database.patch(testCollection, _id, _etag, itemPatch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = database.get(testCollection, queryParams).get();
            Map<String, Object> testMapDes = mapper.readValue(ret, Map.class);
            System.out.println("map:" + testMapDes);
            String _id = (String) testMapDes.get("_id");
            String _etag = (String) testMapDes.get("_etag");
            database.delete(testCollection, _id, _etag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDrop() {
        database.dropCol(testCollection);
    }
}
