package org.elastos.hive.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {

    public static String getJsonFromObject(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
