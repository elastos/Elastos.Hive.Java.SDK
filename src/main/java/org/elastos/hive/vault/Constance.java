package org.elastos.hive.vault;

public class Constance {

    public static final String API_PATH = "/api/v1";

    static final String AUTH_URI = "https://accounts.google.com/o/oauth2/auth";

    static final String TOKEN_URI = "https://oauth2.googleapis.com";

    public static final String SCOPE = "https://www.googleapis.com/auth/drive";

    static final String SCOPES = "[https://www.googleapis.com/auth/drive]";

    static final String GRANT_TYPE_GET_TOKEN = "authorization_code";
    static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    static final String DEFAULT_REDIRECT_URL = "localhost";
    static final int    DEFAULT_REDIRECT_PORT = 12345;

    public static final String MAIN_NET_RESOLVER = "http://api.elastos.io:20606";

}
