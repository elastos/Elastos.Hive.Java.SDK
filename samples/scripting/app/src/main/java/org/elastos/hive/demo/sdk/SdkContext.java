package org.elastos.hive.demo.sdk;

import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;

import org.elastos.did.DIDDocument;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.AppContext;
import org.elastos.hive.AppContextProvider;
import org.elastos.hive.Backup;
import org.elastos.hive.ScriptRunner;
import org.elastos.hive.Vault;
import org.elastos.hive.demo.sdk.config.ApplicationConfig;
import org.elastos.hive.demo.sdk.config.ClientConfig;
import org.elastos.hive.demo.sdk.config.NodeConfig;
import org.elastos.hive.demo.sdk.config.UserConfig;
import org.elastos.hive.demo.sdk.did.DApp;
import org.elastos.hive.demo.sdk.did.DIDApp;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.HiveBackupContext;
import org.elastos.hive.utils.JwtUtil;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class SdkContext {
    private static SdkContext instance = null;

    private DIDApp userDid;
    private DIDApp userDidCaller;
    private String callerDid;
    private DApp appInstanceDid;
    private NodeConfig nodeConfig;
    private AppContext context;
    private AppContext contextCaller;

    private AssetManager assetManager;

    public static SdkContext getInstance(AssetManager assetManager) throws HiveException, DIDException {
        if (instance == null)
            instance = new SdkContext(assetManager);
        return instance;
    }

    private SdkContext(AssetManager assetManager) throws HiveException, DIDException {
        this.assetManager = assetManager;
        Utils.assetManager = assetManager;
        init();
    }

    private String getLocalRootDir() {
        String rootDir = Environment.getDataDirectory().getAbsolutePath() + "/data/org.elastos.hive.demo";
        System.setProperty("user.dir", rootDir);
        return rootDir;
    }

    private String getLocalDidCacheDir(String envName) {
        return getLocalRootDir() + "/hive/" + envName + "/didCache";
    }

    private String getLocalDidStoreRootDir(String envName) {
        return getLocalRootDir() + "/hive/" + envName + "/store";
    }

    public void init() throws HiveException, DIDException {
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

        ClientConfig clientConfig = ClientConfig.deserialize(Utils.getConfigure(fileName));
        nodeConfig = clientConfig.nodeConfig();

        AppContext.setupResolver(clientConfig.resolverUrl(), getLocalDidCacheDir(nodeConfig.storePath()));

        DummyAdapter adapter = new DummyAdapter();
        ApplicationConfig applicationConfig = clientConfig.applicationConfig();
        appInstanceDid = new DApp(applicationConfig.name(),
                applicationConfig.mnemonic(),
                adapter,
                applicationConfig.passPhrase(),
                applicationConfig.storepass(),
                getLocalDidStoreRootDir(nodeConfig.storePath()));

        UserConfig userConfig = clientConfig.userConfig();
        userDid = new DIDApp(userConfig.name(),
                userConfig.mnemonic(),
                adapter,
                userConfig.passPhrase(),
                userConfig.storepass(),
                getLocalDidStoreRootDir(nodeConfig.storePath()));
        UserConfig userConfigCaller = clientConfig.crossConfig().userConfig();
        userDidCaller = new DIDApp(userConfigCaller.name(),
                userConfigCaller.mnemonic(),
                adapter,
                userConfigCaller.passPhrase(),
                userConfigCaller.storepass(),
                getLocalDidStoreRootDir(nodeConfig.storePath()));

        //初始化Application Context
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

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public CompletableFuture<String> getAuthorization(String jwtToken) {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Claims claims = JwtUtil.getBody(jwtToken);
                        if (claims == null)
                            throw new HiveException("Invalid jwt token as authorization.");
                        return appInstanceDid.createToken(appInstanceDid.createPresentation(
                                userDid.issueDiplomaFor(appInstanceDid),
                                claims.getIssuer(),
                                (String) claims.get("nonce")), claims.getIssuer());
                    } catch (Exception e) {
                        throw new CompletionException(new HiveException(e.getMessage()));
                    }
                });
            }
        }, nodeConfig.ownerDid());

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
                        Claims claims = JwtUtil.getBody(jwtToken);
                        if (claims == null)
                            throw new HiveException("Invalid jwt token as authorization.");
                        return appInstanceDid.createToken(appInstanceDid.createPresentation(
                                userDidCaller.issueDiplomaFor(appInstanceDid),
                                claims.getIssuer(),
                                (String) claims.get("nonce")), claims.getIssuer());
                    } catch (Exception e) {
                        throw new CompletionException(new HiveException(e.getMessage()));
                    }
                });
            }
        }, userConfigCaller.did());
        callerDid = userConfigCaller.did();
    }

    public AppContext getAppContext() {
        return this.context;
    }

    public String getOwnerDid() {
        return nodeConfig.ownerDid();
    }

    public String getProviderAddress() {
        return nodeConfig.provider();
    }

    public Vault newVault() {
        return new Vault(context, nodeConfig.provider());
    }

    public ScriptRunner newScriptRunner() {
        return new ScriptRunner(context, nodeConfig.provider());
    }

    public ScriptRunner newCallerScriptRunner() {
        return new ScriptRunner(contextCaller, nodeConfig.provider());
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

    public String getAppId() {
        return appInstanceDid.appId;
    }

    public String getCallerDid() {
        return this.callerDid;
    }

    private enum EnvironmentType {
        DEVELOPING,
        PRODUCTION,
        LOCAL
    }
}
