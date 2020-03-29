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

package org.elastos.hive.vendors.connection;

import org.elastos.hive.vendors.connection.model.BaseServiceConfig;
import org.elastos.hive.vendors.ipfs.network.IPFSApi;
import org.elastos.hive.vendors.onedrive.network.AuthApi;
import org.elastos.hive.vendors.onedrive.network.OneDriveApi;

public class ConnectionManager {

    private static AuthApi authApi ;
    private static OneDriveApi oneDriveApi ;
    private static IPFSApi ipfsApi ;

    private static String onedriveBaseUrl ;
    private static String authBaseUrl ;
    private static String ipfsBaseUrl ="https://www.elastos.org/";

    private static BaseServiceConfig onedriveConfig = new BaseServiceConfig.Builder().build();
    private static BaseServiceConfig authConfig = new BaseServiceConfig.Builder().build();
    private static BaseServiceConfig ipfsConfig = new BaseServiceConfig.Builder().build();

    public static OneDriveApi getOnedriveApi() throws Exception {
        if (oneDriveApi == null){
            oneDriveApi = BaseServiceUtil.createService(OneDriveApi.class ,
                    ConnectionManager.onedriveBaseUrl , ConnectionManager.onedriveConfig);
        }
        return oneDriveApi ;
    }

    public static AuthApi getAuthApi() throws Exception {
        if (authApi == null){
            authApi = BaseServiceUtil.createService(AuthApi.class,
                    ConnectionManager.authBaseUrl,ConnectionManager.authConfig);
        }
        return authApi;
    }

    public static IPFSApi getIPFSApi() throws Exception {
        if (ipfsApi == null){
            ipfsApi = BaseServiceUtil.createService(IPFSApi.class,
                    ConnectionManager.ipfsBaseUrl,ipfsConfig);
        }
        return ipfsApi ;
    }

    private static void updateOneDriveConfig(BaseServiceConfig onedriveConfig){
        ConnectionManager.onedriveConfig = onedriveConfig ;
    }

    private static void updateAuthConfig(BaseServiceConfig authConfig){
        ConnectionManager.authConfig = authConfig ;
    }

    private static void updateIPFSConfig(BaseServiceConfig ipfsConfig){
        ConnectionManager.ipfsConfig = ipfsConfig ;
    }

    private static void updateOneDriveBaseUrl(String onedriveBaseUrl){
        ConnectionManager.onedriveBaseUrl = onedriveBaseUrl ;
    }

    private static void updateAuthBaseUrl(String authBaseUrl){
        ConnectionManager.authBaseUrl = authBaseUrl ;
    }

    private static void updateIPFSBaseUrl(String ipfsBaseUrl){
        ConnectionManager.ipfsBaseUrl = ipfsBaseUrl ;
    }

    public static void resetAuthApi(String baseUrl , BaseServiceConfig baseServiceConfig) throws Exception {
        authApi = null ;
        updateAuthConfig(baseServiceConfig);
        updateAuthBaseUrl(baseUrl);
    }

    public static void resetOneDriveApi(String baseUrl , BaseServiceConfig baseServiceConfig) throws Exception {
        oneDriveApi = null ;
        updateOneDriveBaseUrl(baseUrl);
        updateOneDriveConfig(baseServiceConfig);
    }

    public static void resetIPFSApi(String baseUrl) throws Exception {
        ipfsApi = null ;
        updateIPFSBaseUrl(baseUrl);
        updateIPFSConfig(new BaseServiceConfig.Builder().build());
    }
}
