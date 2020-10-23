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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionManager {

    private NodeApi hivevaultApi;

    private String hivevaultBaseUrl;

    private BaseServiceConfig hivevaultConfig = new BaseServiceConfig.Builder().build() ;

    public ConnectionManager(String baseUrl, BaseServiceConfig baseServiceConfig) {
        resetHiveVaultApi(baseUrl, baseServiceConfig);
    }

    public NodeApi getHiveVaultApi() {
        if(hivevaultApi == null) {
            hivevaultApi = BaseServiceUtil.createService(NodeApi.class,
                    this.hivevaultBaseUrl, this.hivevaultConfig);
        }
        return hivevaultApi;
    }

    private void updateHiveVaultConfig(BaseServiceConfig hivenvaultConfig) {
        this.hivevaultConfig = hivenvaultConfig;
    }

    private void updateHiveVaultBaseUrl(String hivevaultBaseUrl) {
        this.hivevaultBaseUrl = hivevaultBaseUrl;
    }

    public void resetHiveVaultApi(String baseUrl, BaseServiceConfig baseServiceConfig) {
        hivevaultApi = null;
        updateHiveVaultBaseUrl(baseUrl);
        updateHiveVaultConfig(baseServiceConfig);
    }

    public String getHivevaultBaseUrl() {
        return this.hivevaultBaseUrl;
    }

    public String getAccessToken() {
        return this.hivevaultConfig.getHeaderConfig().getAuthToken().getAccessToken();
    }

    public HttpURLConnection openURLConnection(String path) throws IOException {
        String url = this.getHivevaultBaseUrl() + Constance.API_PATH +"/files/upload/" + path;
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(5000);
        httpURLConnection.setReadTimeout(5000);

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestProperty("Transfer-Encoding", "chunked");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Authorization", "token " + this.getAccessToken());

        httpURLConnection.setChunkedStreamingMode(0);

        return httpURLConnection;
    }

}
