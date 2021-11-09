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
	private static final String RESOLVE_CACHE = "data/didCache";
	private static TestData instance = null;

	private UserDID userDid;
	private UserDID callerDid;
	private AppDID appInstanceDid;
	private NodeConfig nodeConfig;
	private AppContext context;
	private AppContext callerContext;

	public static TestData getInstance() throws HiveException, DIDException {
		if (instance == null)
			instance = new TestData();
		return instance;
	}

	private TestData() throws HiveException, DIDException {
		init();
	}

	public void init() throws HiveException, DIDException {
		ClientConfig clientConfig = getClientConfig();
		AppContext.setupResolver(clientConfig.resolverUrl(), RESOLVE_CACHE);

		ApplicationConfig applicationConfig = clientConfig.applicationConfig();
		appInstanceDid = new AppDID(applicationConfig.name(),
				applicationConfig.mnemonic(),
				applicationConfig.passPhrase(),
				applicationConfig.storepass());

		UserConfig userConfig = clientConfig.userConfig();
		userDid = new UserDID(userConfig.name(),
				userConfig.mnemonic(),
				userConfig.passPhrase(),
				userConfig.storepass());
		UserConfig userConfigCaller = clientConfig.crossConfig().userConfig();
		callerDid = new UserDID(userConfigCaller.name(),
				userConfigCaller.mnemonic(),
				userConfigCaller.passPhrase(),
				userConfigCaller.storepass());

		nodeConfig = clientConfig.nodeConfig();

		//初始化Application Context
		context = AppContext.build(new AppContextProvider() {
			@Override
			public String getLocalDataDir() {
				return getLocalStorePath();
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

		callerContext = AppContext.build(new AppContextProvider() {
			@Override
			public String getLocalDataDir() {
				return getLocalStorePath();
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
		else if ("local".equals(hiveEnv))
			fileName = "Local.conf";
		else
			fileName = "Developing.conf";
		log.info(">>>>>> Current config file: " + fileName + " <<<<<<");
		return ClientConfig.deserialize(Utils.getConfigure(fileName));
	}

	private String getLocalStorePath() {
		return System.getProperty("user.dir") + File.separator + "data/store" + File.separator + nodeConfig.storePath();
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
