package org.elastos.hive.config;

import org.elastos.did.DIDDocument;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.did.jwt.JwtParserBuilder;
import org.elastos.hive.*;
import org.elastos.hive.did.DApp;
import org.elastos.hive.did.DIDApp;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.service.BackupService;
import org.elastos.hive.service.HiveBackupContext;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This is used for representing 3rd-party application.
 */
public class TestData {
	private static TestData instance = null;

	private DIDApp userDid;
	private DIDApp userDidCaller;
	private String callerDid;
	private DApp appInstanceDid;
	private NodeConfig nodeConfig;
	private AppContext context;
	private AppContext contextCaller;

	public static TestData getInstance() throws HiveException, DIDException {
		if (instance == null)
			instance = new TestData();
		return instance;
	}

	private TestData() throws HiveException, DIDException {
		Logger.hive();
		init();
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
		AppContext.setupResolver(clientConfig.resolverUrl(), "data/didCache");

		DummyAdapter adapter = new DummyAdapter();
		ApplicationConfig applicationConfig = clientConfig.applicationConfig();
		appInstanceDid = new DApp(applicationConfig.name(),
				applicationConfig.mnemonic(),
				adapter,
				applicationConfig.passPhrase(),
				applicationConfig.storepass());

		UserConfig userConfig = clientConfig.userConfig();
		userDid = new DIDApp(userConfig.name(),
				userConfig.mnemonic(),
				adapter,
				userConfig.passPhrase(),
				userConfig.storepass());
		UserConfig userConfigCaller = clientConfig.crossConfig().userConfig();
		userDidCaller = new DIDApp(userConfigCaller.name(),
				userConfigCaller.mnemonic(),
				adapter,
				userConfigCaller.passPhrase(),
				userConfigCaller.storepass());

		nodeConfig = clientConfig.nodeConfig();

		//初始化Application Context
		context = AppContext.build(new AppContextProvider() {
			@Override
			public String getLocalDataDir() {
				return System.getProperty("user.dir") + File.separator + "data/store" + File.separator + nodeConfig.storePath();
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
				return System.getProperty("user.dir") + File.separator + "data/store" + File.separator + nodeConfig.storePath();
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
