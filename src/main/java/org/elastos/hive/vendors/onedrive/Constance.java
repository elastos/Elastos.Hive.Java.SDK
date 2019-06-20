package org.elastos.hive.vendors.onedrive;

/**
 * Package: org.elastos.hive.vendors.onedrive
 * ClassName: Constance
 * Created by ranwang on 2019/6/18.
 */
public class Constance {
    public static final String ONE_DRIVE_AUTH_BASE_URL = OneDriveURL.AUTH+"/" ;
    public static final String ONE_DRIVE_API_BASE_URL = OneDriveURL.API+"/";

    public static final String AUTHORIZE = "authorize" ;
    public static final String TOKEN = "token";
    public static final String LOGOUT = "logout";

    public static final String AUTHORIZATION = "Authorization";

    public static final String CLIENT_ID = "client_id" ;
    public static final String REDIRECT_URL = "redirect_url" ;
    public static final String REFRESH_TOKEN = "refresh_token" ;
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String CODE = "code" ;
    public static final String LOGOUT_REDIRECT_URL = "post_logout_redirect_uri" ;

    public static final String DEFAULT_REDIRECT_URL = "localhost";
    public static final int DEFAULT_REDIRECT_PORT = 44316 ;

    public static final String GRANT_TYPE_GET_TOKEN = "authorization_code" ;
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

}
