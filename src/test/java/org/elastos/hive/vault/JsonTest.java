package org.elastos.hive.vault;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.util.Map;

public class JsonTest {

    @Test
    public void testJsonToMap() {
        ObjectMapper mapper = new ObjectMapper();

        String json = "{\n" +
                "    \"_items\": [\n" +
                "        {\n" +
                "            \"_id\": \"5f1533aaacf2b82a648489b2\",\n" +
                "            \"firstname\": \"barack\",\n" +
                "            \"lastname\": \"obama\",\n" +
                "            \"_updated\": \"Mon, 20 Jul 2020 06:03:22 GMT\",\n" +
                "            \"_created\": \"Mon, 20 Jul 2020 06:03:22 GMT\",\n" +
                "            \"_etag\": \"b7645b7045d7579d4d29da47fe06d35bde920978\",\n" +
                "            \"_links\": {\n" +
                "                \"self\": {\n" +
                "                    \"title\": \"person\",\n" +
                "                    \"href\": \"people/5f1533aaacf2b82a648489b2\"\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "    ],\n" +
                "    \"_links\": {\n" +
                "        \"parent\": {\n" +
                "            \"title\": \"home\",\n" +
                "            \"href\": \"/\"\n" +
                "        },\n" +
                "        \"self\": {\n" +
                "            \"title\": \"people\",\n" +
                "            \"href\": \"people?where={\\\"lastname\\\":\\\"obama\\\"}\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"_meta\": {\n" +
                "        \"page\": 1,\n" +
                "        \"max_results\": 25,\n" +
                "        \"total\": 1\n" +
                "    }\n" +
                "}";

        try {
            Map<String, Object> testMapDes = mapper.readValue(json, Map.class);
            System.out.println("map:" + testMapDes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
