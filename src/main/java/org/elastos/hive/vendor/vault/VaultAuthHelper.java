package org.elastos.hive.vendor.vault;

import org.elastos.hive.AuthenticationHandler;
import org.elastos.hive.Callback;
import org.elastos.hive.ConnectHelper;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.oauth.AuthServer;
import org.elastos.hive.oauth.AuthToken;
import org.elastos.hive.oauth.Authenticator;
import org.elastos.hive.utils.DateUtil;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendor.AuthInfoStoreImpl;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.connection.model.HeaderConfig;
import org.elastos.hive.vendor.vault.network.model.AuthResponse;
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
    private final String did;

    private AuthToken token;
    private AtomicBoolean connectState = new AtomicBoolean(false);
    private AtomicBoolean syncState = new AtomicBoolean(false);
    private String accessToken;
    private final Persistent persistent;

    public VaultAuthHelper(String nodeUrl, String did, String storePath, String clientId, String clientSecret, String redirectUrl, String scope) {
        this.nodeUrl = nodeUrl;
        this.did = did;
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
        this.clientSecret = clientSecret;

        this.persistent = new AuthInfoStoreImpl(storePath, VaultConstance.CONFIG);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetHiveVaultApi(nodeUrl, config);
            ConnectionManager.resetAuthApi(VaultConstance.TOKEN_URI, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> authrizeAsync(AuthenticationHandler handler, Authenticator authenticator) {
        return authrizeAsync(handler, authenticator, new NullCallback<>());
    }

    @Override
    public CompletableFuture<Void> authrizeAsync(AuthenticationHandler handler, Authenticator authenticator, Callback<Void> callback) {
        return CompletableFuture.runAsync(() -> {
            try {

                if(null != authenticator) {
                    cloudAccess(authenticator);
                }
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
        if (token == null || token.isExpired()) {
            nodeAuth();
            redeemToken();
        }
        connectState.set(true);
    }

    private void redeemToken() throws Exception {
        String refreshToken = "";
        if (token != null)
            refreshToken = token.getRefreshToken();
        Response response = ConnectionManager.getVaultAuthApi()
                .refreshToken(clientId, clientSecret,
                        refreshToken, VaultConstance.GRANT_TYPE_REFRESH_TOKEN)
                .execute();
        handleTokenResponse(response);
    }

    private void cloudAccess(Authenticator authenticator) throws Exception {
        connectState.set(false);
        tryRestoreToken();

        if (token == null){
            nodeAuth();
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

    private void authChallenge(AuthenticationHandler handler) throws Exception {
        //TODO
    }

    private void nodeAuth() throws Exception {
        Map map = new HashMap<>();
        map.put("jwt", this.did);
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
        syncGoogleDrive(response);
    }

    private void handleTokenResponse(Response response) {
        TokenResponse tokenResponse = (TokenResponse) response.body();
        long expiresTime = System.currentTimeMillis() / 1000 + (tokenResponse != null ? tokenResponse.getExpires_in() : 0);

        token = new AuthToken(tokenResponse != null ? tokenResponse.getRefresh_token() : "",
                accessToken,
                expiresTime, tokenResponse != null ? tokenResponse.getToken_type() : "");

        //Store the local data.
        writebackToken();

        //init connection
        initConnection();
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

        String url = String.format("%s?client_id=%s&scope=%s&response_type=code&redirect_uri=%s",
                VaultConstance.AUTH_URI,
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

    private void syncGoogleDrive(Response response) throws IOException {
        TokenResponse tokenResponse = (TokenResponse) response.body();
        long expiresTime = System.currentTimeMillis() + (tokenResponse != null ? tokenResponse.getExpires_in() : 0);
        Map map = new HashMap<>();
        map.put("token", tokenResponse.getAccess_token());
        map.put("refresh_token", tokenResponse.getRefresh_token());
        map.put("token_uri", VaultConstance.TOKEN_URI);
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("scopes", VaultConstance.SCOPES);
        map.put("expiry", DateUtil.getCurrentEpochTimeStamp(expiresTime));

        String json = new JSONObject(map).toString();
        ConnectionManager.getHiveVaultApi()
                .googleDrive(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                .execute();
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
