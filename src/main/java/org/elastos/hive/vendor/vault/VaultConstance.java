package org.elastos.hive.vendor.vault;

public class VaultConstance {

    static final String CONFIG = "hivesault.json";

    static final String AUTH_URI = "https://accounts.google.com/o/oauth2/auth";

    static final String TOKEN_URI = "https://oauth2.googleapis.com";

    public static final String SCOPE = "https://www.googleapis.com/auth/drive";

    static final String SCOPES = "[https://www.googleapis.com/auth/drive]";

    static final String GRANT_TYPE_GET_TOKEN = "authorization_code";
    static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    static final String DEFAULT_REDIRECT_URL = "localhost";
    static final int    DEFAULT_REDIRECT_PORT = 12345;

    static final String NODE_URL = "https://hive.trinity-tech.io";

    public static final String DEFAULT_VAULT_PROVIDER = "did:elastos:iWFAUYhTa35c1fPe3iCJvihZHx6quumnym";

//    static final String DICT_SCHEMA = "{'KV': {\n" +
//            "        'type': 'dict',\n" +
//            "        'schema': {\n" +
//            "            'key': {'type': 'string'},\n" +
//            "            'values': {'type': 'string'}\n" +
//            "        },\n" +
//            "    }}";
  
}
