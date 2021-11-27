package org.elastos.hive.demo.sdk;

import android.content.SharedPreferences;
import android.text.TextUtils;

import org.elastos.did.DIDDocument;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.Backup;
import org.elastos.hive.BackupSubscription;
import org.elastos.hive.ScriptRunner;
import org.elastos.hive.Vault;
import org.elastos.hive.VaultSubscription;
import org.elastos.hive.demo.MainActivity;
import org.elastos.hive.demo.sdk.config.ApplicationConfig;
import org.elastos.hive.demo.sdk.config.ClientConfig;
import org.elastos.hive.demo.sdk.config.NodeConfig;
import org.elastos.hive.demo.sdk.config.UserConfig;
import org.elastos.hive.demo.sdk.did.AppDID;
import org.elastos.hive.demo.sdk.did.UserDID;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.HiveBackupContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SdkContext {
    private static SdkContext instance = null;

    private static final String SETTING_KEY_MNEMONIC = "mnemonic";
    private static final String SETTING_KEY_PASS_PHRASE = "passPhrase";

    private MainActivity mainActivity;

    private NodeConfig nodeConfig;
    private AppDID appInstanceDid;
    private UserDID userDid;
    private AppContext context;
    private UserDID callerDid;
    private AppContext contextCaller;


    public static SdkContext getInstance(MainActivity mainActivity) throws HiveException, DIDException {
        if (instance == null)
            instance = new SdkContext(mainActivity);
        return instance;
    }

    private SdkContext(MainActivity mainActivity) throws HiveException, DIDException {
        this.mainActivity = mainActivity;
        Utils.assetManager = mainActivity.getAssets();
        init();
    }

    private String getLocalDidCacheDir(String envName) {
        return Utils.getLocalRootDir() + "/hive/" + envName + "/didCache";
    }

    private String getLocalDidStoreRootDir(String envName) {
        return Utils.getLocalRootDir() + "/hive/" + envName + "/store";
    }

    public void init() throws HiveException, DIDException {
        ClientConfig clientConfig = getClientConfig();
        nodeConfig = clientConfig.nodeConfig();

        AppContext.setupResolver(clientConfig.resolverUrl(), getLocalDidCacheDir(nodeConfig.storePath()));

        ApplicationConfig applicationConfig = clientConfig.applicationConfig();
        appInstanceDid = new AppDID(applicationConfig.name(),
                applicationConfig.mnemonic(),
                applicationConfig.passPhrase(),
                applicationConfig.storepass());

        this.initByUserDid(clientConfig);
        this.initByCallerDid(clientConfig);
    }

    public void initByUserDid(ClientConfig clientConfig) throws DIDException {
        UserConfig userConfig = clientConfig.userConfig();
        userDid = new UserDID(userConfig.name(),
                getMnemonicStr(userConfig),
                getPassPhraseStr(userConfig),
                userConfig.storepass());

        context = AppContext.build(new AppContextProvider() {
            @Override
            public String getLocalDataDir() {
                return getLocalDidStoreRootDir(nodeConfig.storePath());
            }

            @Override
            public DIDDocument getAppInstanceDocument() {
                try {
                    return appInstanceDid.getDocument();
                } catch (DIDException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public CompletableFuture<String> getAuthorization(String jwtToken) {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Claims claims = new JwtParserBuilder().build().parseClaimsJws(jwtToken).getBody();
                        if (claims == null)
                            throw new HiveException("Invalid jwt token as authorization.");
                        return appInstanceDid.createToken(appInstanceDid.createPresentation(
                                userDid.issueDiplomaFor(appInstanceDid),
                                claims.getIssuer(), (String) claims.get("nonce")), claims.getIssuer());
                    } catch (Exception e) {
                        throw new CompletionException(new HiveException(e.getMessage()));
                    }
                });
            }
        }, userDid.toString());
    }

    private void initByCallerDid(ClientConfig clientConfig) throws DIDException {
        UserConfig callerConfig = clientConfig.crossConfig().userConfig();
        callerDid = new UserDID(callerConfig.name(),
                callerConfig.mnemonic(),
                callerConfig.passPhrase(),
                callerConfig.storepass());


        contextCaller = AppContext.build(new AppContextProvider() {
            @Override
            public String getLocalDataDir() {
                return getLocalDidStoreRootDir(nodeConfig.storePath());
            }

            @Override
            public DIDDocument getAppInstanceDocument() {
                try {
                    return appInstanceDid.getDocument();
                } catch (DIDException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public CompletableFuture<String> getAuthorization(String jwtToken) {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Claims claims = new JwtParserBuilder().build().parseClaimsJws(jwtToken).getBody();
                        if (claims == null)
                            throw new HiveException("Invalid jwt token as authorization.");
                        return appInstanceDid.createToken(appInstanceDid.createPresentation(
                                callerDid.issueDiplomaFor(appInstanceDid),
                                claims.getIssuer(),
                                (String) claims.get("nonce")), claims.getIssuer());
                    } catch (Exception e) {
                        throw new CompletionException(new HiveException(e.getMessage()));
                    }
                });
            }
        }, callerDid.toString());
    }

    private SharedPreferences getSettings() {
        return this.mainActivity.getApplicationContext().getSharedPreferences("hive_sdk", 0);
    }

    private String getMnemonicStr(UserConfig userConfig) {
        String m = getSettings().getString(SETTING_KEY_MNEMONIC, null);
        return !TextUtils.isEmpty(m) ? m : userConfig.mnemonic();
    }

    private String getPassPhraseStr(UserConfig userConfig) {
        String p = getSettings().getString(SETTING_KEY_PASS_PHRASE, null);
        return !TextUtils.isEmpty(p) ? p : userConfig.passPhrase();
    }

    private void saveStringSetting(String key, String value) {
        SharedPreferences settings = getSettings();
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private ClientConfig getClientConfig() {
        //TODO set environment config
        String fileName = null;
        switch (EnvironmentType.DEVELOPING) {
            case DEVELOPING:
                fileName = "Developing.conf";
                break;
            case PRODUCTION:
                fileName = "Production.conf";
                break;
            case LOCAL:
                fileName = "Local.conf";
                break;
        }

        return ClientConfig.deserialize(Utils.getConfigure(fileName));
    }

    public AppContext getAppContext() {
        return this.context;
    }

    public String getVaultProviderAddress() {
        return nodeConfig.provider();
    }

    public String getBackupProviderAddress() {
        return nodeConfig.targetHost();
    }

    public VaultSubscription newVaultSubscription() throws HiveException {
        return new VaultSubscription(context, getVaultProviderAddress());
    }

    public BackupSubscription newBackupSubscription() throws HiveException {
        return new BackupSubscription(context, getBackupProviderAddress());
    }

    public Vault newVault() {
        return new Vault(context, getVaultProviderAddress());
    }

    public ScriptRunner newScriptRunner() {
        return new ScriptRunner(context, getVaultProviderAddress());
    }

    public ScriptRunner newCallerScriptRunner() {
        return new ScriptRunner(contextCaller, getVaultProviderAddress());
    }

    public Backup newBackup() {
        return new Backup(context, nodeConfig.targetHost());
    }

    public BackupService getBackupService() {
        BackupService bs = this.newVault().getBackupService();
        bs.setupContext(new HiveBackupContext() {
            @Override
            public String getType() {
                return null;
            }

            @Override
            public String getTargetProviderAddress() {
                return nodeConfig.targetHost();
            }

            @Override
            public String getTargetServiceDid() {
                return nodeConfig.targetDid();
            }

            @Override
            public CompletableFuture<String> getAuthorization(String srcDid, String targetDid, String targetHost) {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        return userDid.issueBackupDiplomaFor(srcDid, targetHost, targetDid).toString();
                    } catch (DIDException e) {
                        throw new CompletionException(new HiveException(e.getMessage()));
                    }
                });
            }
        });
        return bs;
    }

    public String getAppDid() {
        return appInstanceDid.getAppDid();
    }

    public String getUserDid() {
        return userDid.toString();
    }

    public String getCallerDid() {
        return this.callerDid.toString();
    }

    public void updateUserDid(String mnemonic, String passPhrase) throws DIDException {
        this.saveStringSetting(SETTING_KEY_MNEMONIC, mnemonic);
        this.saveStringSetting(SETTING_KEY_PASS_PHRASE, passPhrase);
        this.initByUserDid(getClientConfig());
    }

    private enum EnvironmentType {
        DEVELOPING,
        PRODUCTION,
        LOCAL
    }
}
