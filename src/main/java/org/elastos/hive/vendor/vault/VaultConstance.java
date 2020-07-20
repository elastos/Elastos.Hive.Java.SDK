package org.elastos.hive.vendor.vault;

class VaultConstance {

    static final String AULT_URL = "http://127.0.0.1:5000" ;
    static final String AULT_BASE_URL = AULT_URL +"/api/v1/";

    static final String CONFIG = "hivesault.json";

    static final String FILES_ROOT_PATH  = "/Files";
    static final String KEYVALUES_ROOT_PATH = "/KeyValues";

    static final String COLLECTION_NAME = "key_values";
    static final String COLLECTION_SCHEMA = "{\"key\": {\"type\": \"string\"}, \"value\": {\"type\": \"string\"}}";

    public static String networkConfig = "MainNet";
    public static String resolver = "http://api.elastos.io:20606";

    //dummystore.verbose
    public static boolean verbose = true;
    //temp.dir
    public static String tempDir = "TEMP";
    //store
    public static String storeRoot = "DIDStore";
    //wallet.dir
    public static String walletDir = "";
    public static String walletId = "test";

//    static final String DICT_SCHEMA = "{'KV': {\n" +
//            "        'type': 'dict',\n" +
//            "        'schema': {\n" +
//            "            'key': {'type': 'string'},\n" +
//            "            'values': {'type': 'string'}\n" +
//            "        },\n" +
//            "    }}";
  
}
