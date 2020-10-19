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

package org.elastos.hive.connection;

import org.elastos.hive.connection.model.BaseServiceConfig;
import org.elastos.hive.vault.Constance;
import org.elastos.hive.vault.network.NodeApi;
import org.elastos.hive.vault.network.VaultAuthApi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionManager {

    private static VaultAuthApi vaultAuthApi;
    private static NodeApi hivevaultApi;

    private static String hivevaultBaseUrl;
    private static String authBaseUrl;

    private static BaseServiceConfig hivevaultConfig = new BaseServiceConfig.Builder().build() ;
    private static BaseServiceConfig authConfig = new BaseServiceConfig.Builder().build();

    public static NodeApi getHiveVaultApi() {
        if(hivevaultApi == null) {
            hivevaultApi = BaseServiceUtil.createService(NodeApi.class,
                    ConnectionManager.hivevaultBaseUrl, ConnectionManager.hivevaultConfig);
        }
        return hivevaultApi;
    }

    public static void resetAuthApi(String baseUrl, BaseServiceConfig baseServiceConfig) {
        vaultAuthApi = null;
        updateAuthConfig(baseServiceConfig);
        updateAuthBaseUrl(baseUrl);
    }

    public static VaultAuthApi getVaultAuthApi() {
        if(vaultAuthApi == null) {
            vaultAuthApi = BaseServiceUtil.createService(VaultAuthApi.class,
                    ConnectionManager.authBaseUrl, ConnectionManager.authConfig);
        }
        return vaultAuthApi;
    }

    private static void updateHiveVaultConfig(BaseServiceConfig hivenvaultConfig) {
        ConnectionManager.hivevaultConfig = hivenvaultConfig;
    }

    private static void updateAuthConfig(BaseServiceConfig authConfig) {
        ConnectionManager.authConfig = authConfig;
    }

    private static void updateHiveVaultBaseUrl(String hivevaultBaseUrl) {
        ConnectionManager.hivevaultBaseUrl = hivevaultBaseUrl;
    }

    private static void updateAuthBaseUrl(String authBaseUrl) {
        ConnectionManager.authBaseUrl = authBaseUrl;
    }

    public static void resetHiveVaultApi(String baseUrl, BaseServiceConfig baseServiceConfig) {
        hivevaultApi = null;
        updateHiveVaultBaseUrl(baseUrl);
        updateHiveVaultConfig(baseServiceConfig);
    }

    public static String getHivevaultBaseUrl() {
        return ConnectionManager.hivevaultBaseUrl;
    }

    public static String getAccessToken() {
        return ConnectionManager.hivevaultConfig.getHeaderConfig().getAuthToken().getAccessToken();
    }

    public static HttpURLConnection openURLConnection(String path) throws IOException {
        String url = ConnectionManager.getHivevaultBaseUrl() + Constance.API_PATH +"/files/upload/" + path;
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestProperty("Transfer-Encoding", "chunked");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        //httpURLConnection.setRequestProperty("Charset", "UTF-8");
        httpURLConnection.setRequestProperty("Authorization", "token " + ConnectionManager.getAccessToken());

        httpURLConnection.setChunkedStreamingMode(0);

        return httpURLConnection;
    }

}
