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

package org.elastos.hive.vendor.onedrive;


import org.elastos.hive.ConnectHelper;
import org.elastos.hive.AuthServer;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendor.AuthInfoStoreImpl;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.connection.model.HeaderConfig;
import org.elastos.hive.vendor.onedrive.network.model.TokenResponse;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

public class OneDriveAuthHelper implements ConnectHelper {
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String EXPIRES_AT_KEY = "expires_at";
    private static final String TOKEN_TYPE_KEY = "token_type";

    private final String clientId;
    private final String scope;
    private final String redirectUrl;
    private final Persistent persistent;

    private AuthToken token;
    private AtomicBoolean connectState = new AtomicBoolean(false);

    OneDriveAuthHelper(String clientId, String scope, String redirectUrl, String persistentStorePath) {
        this.clientId = clientId;
        this.scope = scope;
        this.redirectUrl = redirectUrl;
        this.persistent = new AuthInfoStoreImpl(persistentStorePath, OneDriveConstance.CONFIG);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetAuthApi(OneDriveConstance.ONE_DRIVE_AUTH_BASE_URL, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> connectAsync(Authenticator authenticator) {
        return connectAsync(authenticator, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> connectAsync(Authenticator authenticator,
                                                Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                doLogin(authenticator);
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public CompletableFuture<Void> checkValid() {
        return checkValid(new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> checkValid(Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                doCheckExpired();
                callback.onSuccess(null);
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    private void doCheckExpired() throws Exception {
        connectState.set(false);
        if (token == null || token.isExpired())
            redeemToken();
        connectState.set(true);
    }

    private void doLogin(Authenticator authenticator) throws Exception {
        connectState.set(false);
        tryRestoreToken();

        if (token == null){
            String authCode = accessAuthCode(authenticator);
            accessToken(authCode);
            connectState.set(true);
            return ;
        }

        long current = System.currentTimeMillis() / 1000;
        //Check the expire time
        if (token.getExpiredTime() > current) {
            initConnection();
            connectState.set(true);
            return;
        }

        redeemToken();
        connectState.set(true);
    }

    private String accessAuthCode(Authenticator authenticator) throws Exception {
        Semaphore semph = new Semaphore(1);

        String[] hostAndPort = UrlUtil.decodeHostAndPort(redirectUrl, OneDriveConstance.DEFAULT_REDIRECT_URL, String.valueOf(OneDriveConstance.DEFAULT_REDIRECT_PORT));

        String host = hostAndPort[0];
        int port = Integer.valueOf(hostAndPort[1]);

        AuthServer server = new AuthServer(semph, host, port);
        server.start();

        String url = String.format("%s/%s?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
                OneDriveConstance.ONE_DRIVE_AUTH_URL,
                OneDriveConstance.AUTHORIZE,
                clientId,
                scope,
                redirectUrl)
                .replace(" ", "%20");

        authenticator.requestAuthentication(url);
        semph.acquire();

        String authCode = server.getAuthCode();
        server.stop();

        semph.release();
        connectState.set(true);
        return authCode;
    }

    private void accessToken(String authCode) throws Exception {
        Response response = ConnectionManager.getAuthApi()
                .getToken(clientId, authCode,
                        redirectUrl, OneDriveConstance.GRANT_TYPE_GET_TOKEN)
                .execute();
        handleTokenResponse(response);
    }

    private void redeemToken() throws Exception {
        String refreshToken = "";
        if (token != null)
            refreshToken = token.getRefreshToken();
        Response response = ConnectionManager.getAuthApi()
                .refreshToken(clientId, redirectUrl,
                        refreshToken, OneDriveConstance.GRANT_TYPE_REFRESH_TOKEN)
                .execute();
        handleTokenResponse(response);
    }

    private void handleTokenResponse(Response response) {
        TokenResponse tokenResponse = (TokenResponse) response.body();
        long expiresTime = System.currentTimeMillis() / 1000 + (tokenResponse != null ? tokenResponse.getExpires_in() : 0);

        token = new AuthToken(tokenResponse != null ? tokenResponse.getRefresh_token() : "",
                tokenResponse != null ? tokenResponse.getAccess_token() : "",
                expiresTime, tokenResponse != null ? tokenResponse.getToken_type() : "");

        //Store the local data.
        writebackToken();

        //init connection
        initConnection();
    }

    private void tryRestoreToken() throws HiveException {
        JSONObject json = persistent.parseFrom();
        String refreshToken = null;
        String accessToken = null;
        String tokenType = null;
        long expiresAt = -1;

        if (json.has(REFRESH_TOKEN_KEY))
            refreshToken = json.getString(REFRESH_TOKEN_KEY);
        if (json.has(ACCESS_TOKEN_KEY))
            accessToken = json.getString(ACCESS_TOKEN_KEY);
        if (json.has(EXPIRES_AT_KEY))
            expiresAt = json.getLong(EXPIRES_AT_KEY);
        if (json.has(TOKEN_TYPE_KEY))
            tokenType = json.getString(TOKEN_TYPE_KEY);

        if (refreshToken != null && accessToken != null && expiresAt > 0 && tokenType != null)
            this.token = new AuthToken(refreshToken, accessToken, expiresAt, tokenType);
    }

    private void writebackToken() {
        if (token == null)
            return;

        try {
            JSONObject json = new JSONObject();
            json.put(CLIENT_ID_KEY, clientId);
            json.put(REFRESH_TOKEN_KEY, token.getRefreshToken());
            json.put(ACCESS_TOKEN_KEY, token.getAccessToken());
            json.put(EXPIRES_AT_KEY, token.getExpiredTime());
            json.put(TOKEN_TYPE_KEY, token.getTokenType());

            persistent.upateContent(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initConnection() {
        HeaderConfig headerConfig = new HeaderConfig.Builder()
                .authToken(token)
                .build();
        BaseServiceConfig baseServiceConfig = new BaseServiceConfig.Builder()
                .headerConfig(headerConfig)
                .build();
        ConnectionManager.resetOneDriveApi(
                OneDriveConstance.ONE_DRIVE_API_BASE_URL,
                baseServiceConfig);
    }

    boolean getConnectState() {
        return connectState.get();
    }

    void dissConnect() {
        connectState.set(false);
    }
}