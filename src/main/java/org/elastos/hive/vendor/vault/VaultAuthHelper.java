package org.elastos.hive.vendor.vault;

import org.elastos.hive.AuthServer;
import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.ConnectHelper;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendor.AuthInfoStoreImpl;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.connection.model.HeaderConfig;
import org.elastos.hive.vendor.vault.network.model.AuthResponse;
import org.elastos.hive.vendor.vault.network.model.BaseResponse;
import org.elastos.hive.vendor.vault.network.model.TokenResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;

public class VaultAuthHelper implements ConnectHelper {

    private static final String CLIENT_ID_KEY = "client_id";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String EXPIRES_AT_KEY = "expires_at";
    private static final String TOKEN_TYPE_KEY = "token_type";

    private final String redirectUrl;
    private final String clientId;
    private final String scope;
    private final String clientSecret;

    private final String nodeUrl;
    private final String authToken;

    private AuthToken token;
    private AtomicBoolean connectState = new AtomicBoolean(false);
    private AtomicBoolean syncState = new AtomicBoolean(false);
    private String accessToken;
    private final Persistent persistent;

    VaultAuthHelper(String nodeUrl, String authToken, String storePath, String clientId, String clientSecret, String redirectUrl, String scope) {
        this.nodeUrl = nodeUrl;
        this.authToken = authToken;
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
        this.clientSecret = clientSecret;

        this.persistent = new AuthInfoStoreImpl(storePath, VaultConstance.CONFIG);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetHiveVaultApi(nodeUrl, config);
            ConnectionManager.resetAuthApi(VaultConstance.VAULT_AUTH_BASE_URL, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> connectAsync(Authenticator authenticator) {
        return connectAsync(authenticator, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> connectAsync(Authenticator authenticator, Callback<Void> callback) {
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

    private void redeemToken() throws Exception {
        String refreshToken = "";
        if (token != null)
            refreshToken = token.getRefreshToken();
        Response response = ConnectionManager.getVaultAuthApi()
                .refreshToken(clientId, redirectUrl,
                        refreshToken, VaultConstance.GRANT_TYPE_REFRESH_TOKEN)
                .execute();
        handleTokenResponse(response);
    }

    private void doLogin(Authenticator authenticator) throws Exception {
        connectState.set(false);
        tryRestoreToken();

        if(accessToken == null) {
            nodeAuth();
        }

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

    private void nodeAuth() throws Exception {
        Map map = new HashMap<>();
        map.put("jwt", this.authToken);
        String json = new JSONObject(map).toString();
        Response response = ConnectionManager.getHiveVaultApi()
                .auth(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                .execute();
        handleAuthResponse(response);
    }

    private void accessToken(String authCode) throws Exception {
        String formatCode = authCode.replace("%2F", "/");
        Response response = ConnectionManager.getVaultAuthApi()
                .getToken(formatCode, clientId, clientSecret,
                        redirectUrl, VaultConstance.GRANT_TYPE_GET_TOKEN)
                .execute();
        handleTokenResponse(response);
    }

    private void handleTokenResponse(Response response) throws IOException {
        TokenResponse tokenResponse = (TokenResponse) response.body();
        long expiresTime = System.currentTimeMillis() / 1000 + 10*1000;

        token = new AuthToken(tokenResponse != null ? tokenResponse.getRefresh_token() : "",
                accessToken,
                expiresTime, "token");

        //Store the local data.
        writebackToken();

        //init connection
        initConnection();

        syncGoogleDrive(tokenResponse.getToken(),
                tokenResponse.getRefresh_token(),
                tokenResponse.getToken_uri(),
                tokenResponse.getClient_id(),
                tokenResponse.getClient_secret(),
                tokenResponse.getScopes(),
                tokenResponse.getExpiry());
    }

    private void handleAuthResponse(Response response) throws Exception {
        AuthResponse authResponse = (AuthResponse) response.body();
        if(authResponse.get_error() != null) {
            throw new HiveException(authResponse.get_error().getMessage());
        }
        accessToken = authResponse.getToken();
    }

    private String accessAuthCode(Authenticator authenticator) throws Exception {
        Semaphore semph = new Semaphore(1);

        String[] hostAndPort = UrlUtil.decodeHostAndPort(redirectUrl, VaultConstance.DEFAULT_REDIRECT_URL, String.valueOf(VaultConstance.DEFAULT_REDIRECT_PORT));

        String host = hostAndPort[0];
        int port = Integer.valueOf(hostAndPort[1]);

        AuthServer server = new AuthServer(semph, host, port);
        server.start();

        String url = String.format("%s/%s?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
                VaultConstance.VAULT_AUTH_URL,
                VaultConstance.AUTH,
                this.clientId,
                this.scope,
                this.redirectUrl)
                .replace(" ", "%20");

        authenticator.requestAuthentication(url);
        semph.acquire();

        String authCode = server.getAuthCode();
        server.stop();

        semph.release();
        connectState.set(true);
        return authCode;
    }

    private void syncGoogleDrive(String token, String refreshToken, String tokenUri, String clientId, String clientSecret, String scopes, String expiry) throws IOException {
        Map map = new HashMap<>();
        map.put("token", token);
        map.put("refresh_token", refreshToken);
        map.put("token_uri", tokenUri);
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("scopes", scopes);
        map.put("expiry", expiry);

        String json = new JSONObject(map).toString();
        Response response = ConnectionManager.getHiveVaultApi()
                .googleDrive(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                .execute();
        handleDriveResponse(response);
    }

    private void handleDriveResponse(Response response) {
        BaseResponse baseResponse = (BaseResponse) response.body();
        syncState.set(baseResponse.get_error()!=null);
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
            json.put(ACCESS_TOKEN_KEY, token);

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
        ConnectionManager.resetHiveVaultApi(this.nodeUrl,
                baseServiceConfig);
    }

    boolean getConnectState() {
        return connectState.get();
    }

    void dissConnect() {
        connectState.set(false);
    }

    boolean getSyncState() {
        return syncState.get();
    }

}
