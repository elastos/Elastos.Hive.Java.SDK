package org.elastos.hive.vendor.vault;

import org.elastos.did.DIDDocument;
import org.elastos.did.jwt.Claims;
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
import org.elastos.hive.utils.JwtUtil;
import org.elastos.hive.utils.UrlUtil;
import org.elastos.hive.vendor.AuthInfoStoreImpl;
import org.elastos.hive.vendor.connection.ConnectionManager;
import org.elastos.hive.vendor.connection.model.BaseServiceConfig;
import org.elastos.hive.vendor.connection.model.HeaderConfig;
import org.elastos.hive.vendor.vault.network.model.AuthResponse;
import org.elastos.hive.vendor.vault.network.model.SignResponse;
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

    private static final String USER_DID_KEY = "user_did";
    private static final String APP_ID_KEY = "app_id";
    private static final String APP_INSTANCE_DID_KEY = "app_instance_did";

    private String redirectUrl;
    private String clientId;
    private String scope;
    private String clientSecret;

    private String ownerDid;
    private String userDid;
    private String appId;
    private String appInstanceDid;

    private String nodeUrl;

    private AuthToken token;
    private AtomicBoolean connectState = new AtomicBoolean(false);
    private AtomicBoolean syncState = new AtomicBoolean(false);
    private String accessToken;
    private Persistent persistent;

    private DIDDocument authenticationDIDDocument;
    private AuthenticationHandler authenticationHandler;

    public VaultAuthHelper(String ownerDid, String nodeUrl, String storePath, DIDDocument authenticationDIDDocument, AuthenticationHandler handler) {
        this.authenticationDIDDocument = authenticationDIDDocument;
        this.authenticationHandler = handler;
        this.ownerDid = ownerDid;
        this.nodeUrl = nodeUrl;

        this.persistent = new AuthInfoStoreImpl(ownerDid, nodeUrl, storePath);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetHiveVaultApi(this.nodeUrl, config);
            ConnectionManager.resetAuthApi(VaultConstance.TOKEN_URI, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VaultAuthHelper(String ownerDid, String nodeUrl, String storePath, String clientId, String clientSecret, String redirectUrl, String scope) {
        this.nodeUrl = nodeUrl;
        this.clientId = clientId;
        this.redirectUrl = redirectUrl;
        this.scope = scope;
        this.clientSecret = clientSecret;

        this.persistent = new AuthInfoStoreImpl(ownerDid, nodeUrl, storePath);

        try {
            BaseServiceConfig config = new BaseServiceConfig.Builder().build();
            ConnectionManager.resetHiveVaultApi(nodeUrl, config);
            ConnectionManager.resetAuthApi(VaultConstance.TOKEN_URI, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            } catch (Exception e) {
                HiveException exception = new HiveException(e.getLocalizedMessage());
                callback.onError(exception);
                throw new CompletionException(exception);
            }
        });
    }

    @Override
    public void connect() {
        try {
            connectState.set(false);
            tryRestoreToken();
            if (token == null || token.isExpired()) {
                signIn();
            }
            initConnection();
            connectState.set(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCheckExpired() throws Exception {
        connectState.set(false);
        tryRestoreToken();
        if (token == null || token.isExpired()) {
            signIn();
        }
        initConnection();
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

        if (token == null) {
            nodeAuth("");
            String authCode = accessAuthCode(authenticator);
            accessToken(authCode);
            connectState.set(true);
            return;
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

    private void signIn() throws Exception {
        Map map = new HashMap<>();
        JSONObject docJsonObject = new JSONObject(authenticationDIDDocument.toString());
        map.put("document", docJsonObject);

        String json = new JSONObject(map).toString();
        Response response = ConnectionManager.getHiveVaultApi()
                .signIn(getJsonRequestBoy(json))
                .execute();
        SignResponse signResponse = (SignResponse) response.body();
        if (null == signResponse) {
            throw new HiveException("Sign in challenge failed");
        }
        String jwtToken = signResponse.getChallenge();
        if (null != this.authenticationHandler && verifyToken(jwtToken)) {
            String approveJwtToken = this.authenticationHandler.authenticationChallenge(jwtToken).get();
            nodeAuth(approveJwtToken);
        }
    }

    private void nodeAuth(String token) throws Exception {
        Map map = new HashMap<>();
        map.put("jwt", token);
        String json = new JSONObject(map).toString();
        Response response = ConnectionManager.getHiveVaultApi()
                .auth(getJsonRequestBoy(json))
                .execute();
        handleAuthResponse(response);
    }

    private boolean verifyToken(String jwtToken) {
        try {
            Claims claims = JwtUtil.getBody(jwtToken);
            long exp = claims.getExpiration().getTime();
//            String subject = claims.getSubject();
//            String iss = claims.getIssuer();
//            String nonce = (String) claims.get("nonce");
            String aud = claims.getAudience();

            if (null == ownerDid
                    || null == aud
                    || !ownerDid.equals(aud))
                return false;

            long currentTime = System.currentTimeMillis();
            if (currentTime > exp) return false;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
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
        if (null == authResponse) {
            throw new HiveException("Authorize failed");
        }

        String access_token = authResponse.getAccess_token();
        if (null == access_token) return;
        Claims claims = JwtUtil.getBody(access_token);
        long exp = claims.getExpiration().getTime();
        this.userDid = (String) claims.get("userDid");
        this.appId = (String) claims.get("appId");
        this.appInstanceDid = (String) claims.get("appInstanceDid");

        long expiresTime = System.currentTimeMillis() / 1000 + exp / 1000;

        token = new AuthToken("",
                access_token,
                expiresTime, "token");

        //Store the local data.
        writebackToken();

        //init connection
        initConnection();

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
                .googleDrive(getJsonRequestBoy(json))
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
        if (json.has(USER_DID_KEY))
            this.userDid = json.getString(USER_DID_KEY);
        if (json.has(APP_ID_KEY))
            this.appId = json.getString(APP_ID_KEY);
        if (json.has(APP_INSTANCE_DID_KEY))
            this.appInstanceDid = json.getString(APP_INSTANCE_DID_KEY);
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
            json.put(USER_DID_KEY, this.userDid);
            json.put(APP_ID_KEY, this.appId);
            json.put(APP_INSTANCE_DID_KEY, this.appInstanceDid);

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

    public String getUserDid() {
        return userDid;
    }

    public void setUserDid(String userDid) {
        this.userDid = userDid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppInstanceDid() {
        return appInstanceDid;
    }

    public void setAppInstanceDid(String appInstanceDid) {
        this.appInstanceDid = appInstanceDid;
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

    private RequestBody getJsonRequestBoy(String json) {
        return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
    }

    public void checkResponseCode(Response response) throws HiveException {
        if (response == null)
            throw new HiveException("response is null");

        int code = response.code();
        if (code >= 300 || code<200) {
            if(code==401) {
                connect();
            } else {
                String message  = response.message();
                throw new HiveException(message);
            }
        }
    }
}
