package org.elastos.hive.config;

import org.elastos.did.DIDDocument;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.*;
import org.elastos.hive.did.AppDID;
import org.elastos.hive.did.UserDID;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.HiveBackupContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This is used for representing 3rd-party application.
 */
public class TestData {
	private static final Logger log = LoggerFactory.getLogger(TestData.class);
	private static TestData instance = null;

	private String network;
	private UserDID userDid;
	private UserDID callerDid;
	private AppDID appInstanceDid;
	private NodeConfig nodeConfig;
	private AppContext context;
	private AppContext callerContext;
	private String ipfsGatewayUrl;

	public static TestData getInstance() throws HiveException, DIDException {
		if (instance == null)
			instance = new TestData();
		return instance;
	}

	private TestData() throws HiveException, DIDException {
		ClientConfig clientConfig = getClientConfig();
		this.network = clientConfig.resolverUrl();
		AppContext.setupResolver(clientConfig.resolverUrl(), this.getLocalCachePath(true));
		this.ipfsGatewayUrl = clientConfig.getIpfsGateUrl();

		ApplicationConfig appCfg = clientConfig.applicationConfig();
		appInstanceDid = new AppDID(appCfg.name(), appCfg.mnemonic(), appCfg.passPhrase(), appCfg.storepass(), network);

		UserConfig usrCfg = clientConfig.userConfig();
		userDid = new UserDID(usrCfg.name(), usrCfg.mnemonic(), usrCfg.passPhrase(), usrCfg.storepass(), network);

		UserConfig calCfg = clientConfig.crossConfig().userConfig();
		callerDid = new UserDID(calCfg.name(), calCfg.mnemonic(), calCfg.passPhrase(), calCfg.storepass(), network);

		nodeConfig = clientConfig.nodeConfig();

		//初始化Application Context
		context = this.createAppContext(userDid);
		callerContext = this.createAppContext(callerDid);
	}

	/**
	 * If run test cases for production environment, please try this:
	 * 	- HIVE_ENV=production ./gradlew build
	 *
	 * @return Client configuration.
	 */
	private ClientConfig getClientConfig() {
		String fileName, hiveEnv = System.getenv("HIVE_ENV");
		if ("production".equals(hiveEnv))
			fileName = "Production.conf";
		else
			fileName = "Developing.conf";
		log.info(">>>>>> Current config file: " + fileName + " <<<<<<");
		return ClientConfig.deserialize(Utils.getConfigure(fileName));
	}

	private String getLocalCachePath(boolean isDidCache) {
		if (isDidCache) {
			return "data/" + this.network + "/didCache";
		}
		return System.getProperty("user.dir") + File.separator +
				"data/" + this.network + "/store" + File.separator + nodeConfig.storePath();
	}

	private AppContext createAppContext(UserDID did) {
		return AppContext.build(new AppContextProvider() {
			@Override
			public String getLocalDataDir() {
				return getLocalCachePath(false);
			}

			@Override
			public DIDDocument getAppInstanceDocument() {
				try {
					return appInstanceDid.getDocument();
				} catch (DIDException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}

			@Override
			public CompletableFuture<String> getAuthorization(String jwtToken) {
				return CompletableFuture.supplyAsync(() -> {
					try {
						Claims claims = new JwtParserBuilder().setAllowedClockSkewSeconds(300).build().parseClaimsJws(jwtToken).getBody();
						if (claims == null)
							throw new HiveException("Invalid jwt token as authorization.");
						return appInstanceDid.createToken(appInstanceDid.createPresentation(
								did.issueDiplomaFor(appInstanceDid),
								claims.getIssuer(), (String) claims.get("nonce")), claims.getIssuer());
					} catch (Exception e) {
						throw new CompletionException(new HiveException(e.getMessage()));
					}
				});
			}
		}, did.toString());
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

	public String getIpfsGatewayUrl() { return this.ipfsGatewayUrl; }

	public VaultSubscription newVaultSubscription() throws HiveException {
		return new VaultSubscription(context, getVaultProviderAddress());
	}

	public Vault newVault() {
		return new Vault(context, getVaultProviderAddress());
	}

	public ScriptRunner newScriptRunner() {
		return new ScriptRunner(context, getVaultProviderAddress());
	}

	public ScriptRunner newCallerScriptRunner() {
		return new ScriptRunner(callerContext, getVaultProviderAddress());
	}

	public BackupSubscription newBackupSubscription() throws HiveException {
		return new BackupSubscription(context, getBackupProviderAddress());
	}

	public Backup newBackup() {
		return new Backup(context, getBackupProviderAddress());
	}

	public Provider newProvider() {
		return new Provider(context, getVaultProviderAddress());
	}

	public BackupService getBackupService() {
		BackupService backupService = this.newVault().getBackupService();
		backupService.setupContext(new HiveBackupContext() {
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
			public CompletableFuture<String> getAuthorization(String serviceDid, String targetDid, String targetHost) {
				return CompletableFuture.supplyAsync(() -> {
					try {
						return userDid.issueBackupDiplomaFor(serviceDid, targetHost, targetDid).toString();
					} catch (DIDException e) {
						throw new CompletionException(new HiveException(e.getMessage()));
					}
				});
			}
		});
		return backupService;
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
}
