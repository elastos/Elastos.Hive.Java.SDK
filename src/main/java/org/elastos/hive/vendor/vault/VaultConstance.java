package org.elastos.hive.vendor.vault;

class VaultConstance {

    static final String AULT_URL = "http://127.0.0.1:5000" ;
    static final String AULT_BASE_URL = AULT_URL +"/api/v1/";

    static final String CONFIG = "hivesault.json";

    static final String FILES_ROOT_PATH  = "/Files";
    static final String KEYVALUES_ROOT_PATH = "/KeyValues";

    static final String COLLECTION_NAME = "key_values";
    static final String COLLECTION_SCHEMA = "{\"key\": {\"type\": \"string\"}, \"value\": {\"type\": \"binary\"}}";

//    static final String DICT_SCHEMA = "{'KV': {\n" +
//            "        'type': 'dict',\n" +
//            "        'schema': {\n" +
//            "            'key': {'type': 'string'},\n" +
//            "            'values': {'type': 'string'}\n" +
//            "        },\n" +
//            "    }}";
  
}
