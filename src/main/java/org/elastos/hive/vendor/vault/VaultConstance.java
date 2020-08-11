package org.elastos.hive.vendor.vault;

class VaultConstance {

    static final String AULT_URL = "http://127.0.0.1:5000" ;
    static final String AULT_BASE_URL = AULT_URL +"/api/v1/";

    static final String CONFIG = "hivesault.json";

    static final String FILES_ROOT_PATH  = "/Files";
    static final String KEYVALUES_ROOT_PATH = "/KeyValues";

    static final String VAULT_AUTH_URL = "https://accounts.google.com/o/oauth2";
    static final String VAULT_AUTH_BASE_URL = VAULT_AUTH_URL+"/";
    static final String AUTH = "auth";

    static final String SCOPES = "https://www.googleapis.com/auth/drive";

    static final String GRANT_TYPE_GET_TOKEN = "authorization_code";
    static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    static final String DEFAULT_REDIRECT_URL = "localhost";
    static final int    DEFAULT_REDIRECT_PORT = 12345;

//    static final String DICT_SCHEMA = "{'KV': {\n" +
//            "        'type': 'dict',\n" +
//            "        'schema': {\n" +
//            "            'key': {'type': 'string'},\n" +
//            "            'values': {'type': 'string'}\n" +
//            "        },\n" +
//            "    }}";
  
}
