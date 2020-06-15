package org.elastos.hive.vendor.vault;

import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.ConnectHelper;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.AuthInfoStoreImpl;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.connection.model.HeaderConfig;
import org.elastos.hive.vendor.vault.network.model.TokenResponse;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

public class VaultAuthHelper implements ConnectHelper {

    private static final String CLIENT_ID_KEY = "client_id";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String REFRESH_TOKEN_KEY = "refresh_token";
    private static final String EXPIRES_AT_KEY = "expires_at";
    private static final String TOKEN_TYPE_KEY = "token_type";

    private final String storePath;
    private final String did;
    private final String pwd;

    private AuthToken token;
    private AtomicBoolean loginState = new AtomicBoolean(false);
    private final Persistent persistent;

    VaultAuthHelper(String did, String pwd, String storePath) {
        this.did = did;
        this.pwd = pwd;
        this.storePath = storePath;
        this.persistent = new AuthInfoStoreImpl(storePath, VaultConstance.CONFIG);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetHiveVaultApi(VaultConstance.AULT_BASE_URL, config);
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
        loginState.set(false);
        if (token == null || token.isExpired())
            accessToken();
        loginState.set(true);
    }

    private void doLogin(Authenticator authenticator) throws Exception {
        loginState.set(false);
        tryRestoreToken();

        if (token == null) {
            accessToken();
            loginState.set(true);
            return;
        }

        long current = System.currentTimeMillis() / 1000;
        //Check the expire time
        if (token.getExpiredTime() > current) {
            initConnection();
            loginState.set(true);
            return;
        }
    }

    private void accessToken() throws Exception {
        Map map = new HashMap<>();
        map.put("did", did);
        map.put("password", pwd);
        Response response = ConnectionManager.getHiveVaultApi()
                .login(map)
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
        ConnectionManager.resetHiveVaultApi(VaultConstance.AULT_BASE_URL,
                baseServiceConfig);
    }

    boolean getConnectState() {
        return loginState.get();
    }

    void dissConnect() {
        loginState.set(false);
    }
}
