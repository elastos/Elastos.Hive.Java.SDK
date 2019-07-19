/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.elastos.hive.vendors.onedrive;

public class OneDriveConstance {
    public static final String ONE_DRIVE_AUTH_URL = "https://login.microsoftonline.com/common/oauth2/v2.0" ;
    public static final String ONE_DRIVE_AUTH_BASE_URL = ONE_DRIVE_AUTH_URL+"/";
    public static final String ONE_DRIVE_API_BASE_URL  = "https://graph.microsoft.com/v1.0/me/";

    public static final String AUTHORIZE = "authorize";
    public static final String TOKEN = "token";
    public static final String LOGOUT = "logout";

    public static final String DRIVE = "drive" ;

    public static final String CLIENT_ID = "client_id" ;
    public static final String REDIRECT_URL = "redirect_url" ;
    public static final String REFRESH_TOKEN = "refresh_token" ;
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String CODE = "code";
    public static final String LOGOUT_REDIRECT_URL = "post_logout_redirect_uri";

    public static final String DEFAULT_REDIRECT_URL = "localhost";
    public static final int    DEFAULT_REDIRECT_PORT = 12345;

    public static final String GRANT_TYPE_GET_TOKEN = "authorization_code";
    public static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    public static final String ROOT = "root";

    public static final String CONFIG = "onedrive.json";

}
