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


import org.elastos.hive.AuthHelper;
import org.elastos.hive.AuthServer;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.connection.model.HeaderConfig;
import org.elastos.hive.vendor.onedrive.network.model.TokenResponse;
import org.json.JSONObject;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

public class OneDriveAuthHelper implements AuthHelper {
    private static final String clientIdKey = "client_id";
    private static final String accessTokenKey = "access_token";
    private static final String refreshTokenKey = "refresh_token";
    private static final String expireAtKey = "expires_at";

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
        this.persistent = new AuthInfoStoreImpl(persistentStorePath);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetAuthApi(OneDriveConstance.ONE_DRIVE_AUTH_BASE_URL, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> loginAsync(Authenticator authenticator) {
        return loginAsync(authenticator, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> loginAsync(Authenticator authenticator,
                                              Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                doLogin(authenticator);
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
            }
        });
    }

    @Override
    public CompletableFuture<Void> checkExpired() {
        return checkExpired(new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> checkExpired(Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {
                doCheckExpired();
                callback.onSuccess(null);
            } catch (Exception e) {
                callback.onError(new HiveException(e.getLocalizedMessage()));
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
        if (token != null) {
            long current = System.currentTimeMillis() / 1000;
            //Check the expire time
            if (token.getExpiredTime() > current) {
                connectState.set(true);
                return;
            }
            redeemToken();
            connectState.set(true);
            return;
        }

        String authCode = accessAuthCode(authenticator);
        accessToken(authCode);
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
        long experitime = System.currentTimeMillis() / 1000 + (tokenResponse != null ? tokenResponse.getExpires_in() : 0);

        token = new AuthToken(tokenResponse != null ? tokenResponse.getRefresh_token() : "",
                tokenResponse != null ? tokenResponse.getAccess_token() : "",
                experitime);

        //Store the local data.
        writebackToken();

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

    private void tryRestoreToken() throws HiveException {
        JSONObject json = persistent.parseFrom();
        String refreshToken = null;
        String accessToken = null;
        long expiresAt = -1;

        if (json.has(refreshTokenKey))
            refreshToken = json.getString(refreshTokenKey);
        if (json.has(accessTokenKey))
            accessToken = json.getString(accessTokenKey);
        if (json.has(expireAtKey))
            expiresAt = json.getLong(expireAtKey);

        if (refreshToken != null && accessToken != null && expiresAt > 0)
            this.token = new AuthToken(refreshToken, accessToken, expiresAt);
    }

    private void writebackToken() {
        if (token == null)
            return;

        try {
            JSONObject json = new JSONObject();
            json.put(clientIdKey, clientId);
            json.put(refreshTokenKey, token.getRefreshToken());
            json.put(refreshTokenKey, token.getAccessToken());
            json.put(expireAtKey, token.getExpiredTime());

            persistent.upateContent(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean getConnectState() {
        return connectState.get();
    }

    void dissConnect() {
        connectState.set(false);
    }
}