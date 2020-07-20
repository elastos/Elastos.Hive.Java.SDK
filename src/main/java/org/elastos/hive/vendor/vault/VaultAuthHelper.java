package org.elastos.hive.vendor.vault;

import com.google.gson.Gson;

import org.elastos.did.DIDDocument;
import org.elastos.did.DIDURL;
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
import org.elastos.hive.vendor.vault.network.model.AuthResponse;
import org.elastos.hive.vendor.vault.network.model.TokenResponse;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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

    private AuthToken token;
    private AtomicBoolean loginState = new AtomicBoolean(false);
    private final Persistent persistent;

    private VaultOptions options;
    private DIDDocument doc;

    VaultAuthHelper(VaultOptions options) {
        this.options = options;
        this.persistent = new AuthInfoStoreImpl(options.storePath(), VaultConstance.CONFIG);

        try {
            DIDData didData = new DIDData(options);
            didData.setup(true);
            didData.initIdentity();
            doc = didData.loadDocument();

            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetHiveVaultApi(options.nodeUrl(), config);
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
            auth();
        loginState.set(true);
    }

    private void doLogin(Authenticator authenticator) throws Exception {
        loginState.set(false);
        tryRestoreToken();

        if (token == null) {
            auth();
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

    private void auth() throws Exception {
        Map map = new HashMap<>();
        map.put("iss", getIss(this.options.did()));
        String json = new JSONObject(map).toString();
        Response response = ConnectionManager.getHiveVaultApi()
                .auth(RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                .execute();
        handleAuthResponse(response);
    }

    private void handleAuthResponse(Response response) throws Exception {
        AuthResponse authResponse = (AuthResponse) response.body();
        String nonce = authResponse.getNonce();
        if(null != nonce) {
            DIDURL pkid = new DIDURL(doc.getSubject(), options.keyName());
            String sig = doc.sign(pkid, options.storePass(), nonce.getBytes());
            callback(authResponse.getSubject(), getIss(options.did()), nonce, authResponse.getIss(), sig);
        }
    }

    private void callback(String subject, String iss, String nonce, String realm, String sig) throws Exception {
        Map map = new HashMap<>();
        map.put("subject", subject);
        map.put("iss", iss);
        map.put("realm", realm);
        map.put("nonce", nonce);
        map.put("key_name", this.options.keyName());
        map.put("sig", sig);
        String json = new JSONObject(map).toString();
        Response response = ConnectionManager.getHiveVaultApi()
                .authCallback(this.options.did(), RequestBody.create(MediaType.parse("Content-Type, application/json"), json))
                .execute();
        handleCallbackResponse(response);
    }

    private String getIss(String did) {
        return "did:elastos:" + did;
    }

    private void handleCallbackResponse(Response response) throws Exception {
        TokenResponse tokenResponse = (TokenResponse) response.body();
        if(tokenResponse.get_error() != null) {
            throw new HiveException(HiveException.ERROR);
        }
        long expiresTime = System.currentTimeMillis() / 1000 + (tokenResponse != null ? tokenResponse.getExpires_in() : 0);

        token = new AuthToken(tokenResponse != null ? tokenResponse.getRefresh_token() : "",
                tokenResponse != null ? tokenResponse.getToken() : "",
                expiresTime, "token");

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
        ConnectionManager.resetHiveVaultApi(this.options.nodeUrl(),
                baseServiceConfig);
    }

    boolean getConnectState() {
        return loginState.get();
    }

    void dissConnect() {
        loginState.set(false);
    }
}
