package org.elastos.hive.vendor.hivevault;

import org.elastos.hive.AuthToken;
import org.elastos.hive.Authenticator;
import org.elastos.hive.Callback;
import org.elastos.hive.ConnectHelper;
import org.elastos.hive.NullCallback;
import org.elastos.hive.Persistent;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.connection.model.HeaderConfig;
import org.elastos.hive.vendor.hivevault.network.model.TokenResponse;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit2.Response;

public class HiveVaultAuthHelper implements ConnectHelper {

    private static final String ACCESS_TOKEN_KEY = "access_token";

    private final String storePath;
    private final String did;
    private final String pwd;
    private final long expiration;

    private AuthToken token;
    private AtomicBoolean loginState = new AtomicBoolean(false);
    private final Persistent persistent;

    HiveVaultAuthHelper(String did, String pwd, String storePath, long expiration) {
        this.did = did;
        this.pwd = pwd;
        this.storePath = storePath;
        this.expiration = expiration;
        this.persistent = new AuthInfoStoreImpl(storePath);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetHiveVaultApi(HiveVaultConstance.HIVE_AULT_BASE_URL, config);
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
        long current = System.currentTimeMillis() / 1000;
        if (token == null || expiration > current)
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
        if (expiration < current) {
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
        token = new AuthToken(null,
                tokenResponse != null ? tokenResponse.getToken() : "",
                expiration, "token");

        //Store the local data.
        writebackToken();

        //init connection
        initConnection();
    }

    private void tryRestoreToken() throws HiveException {
        JSONObject json = persistent.parseFrom();
        String accessToken = null;

        if (json.has(ACCESS_TOKEN_KEY))
            accessToken = json.getString(ACCESS_TOKEN_KEY);

        if (accessToken != null)
            this.token = new AuthToken(null, accessToken, expiration, "token");
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
        ConnectionManager.resetHiveVaultApi(HiveVaultConstance.HIVE_AULT_BASE_URL,
                baseServiceConfig);
    }

    boolean getConnectState() {
        return loginState.get();
    }

    void dissConnect() {
        loginState.set(false);
    }
}
