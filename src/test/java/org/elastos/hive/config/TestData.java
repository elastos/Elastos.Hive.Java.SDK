package org.elastos.hive.config;

import org.elastos.did.DIDDocument;
import org.elastos.did.VerifiableCredential;
import org.elastos.did.VerifiablePresentation;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.*;
import org.elastos.hive.did.DApp;
import org.elastos.hive.did.DIDApp;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.JwtUtil;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * This is used for representing 3rd-party application.
 */
public class TestData {
	private static TestData instance = null;

	private DIDApp userDid;
	private DApp appInstanceDid;
	private NodeConfig nodeConfig;
	private AppContext context;

	public static TestData getInstance() throws HiveException, DIDException {
		if (instance == null) instance = new TestData();
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
		appInstanceDid = new DApp(applicationConfig.name(), applicationConfig.mnemonic(), adapter, applicationConfig.passPhrase(), applicationConfig.storepass());

		UserConfig userConfig = clientConfig.userConfig();
		userDid = new DIDApp(userConfig.name(), userConfig.mnemonic(), adapter, userConfig.passPhrase(), userConfig.storepass());

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
				return CompletableFuture.supplyAsync(() -> signAuthorization(jwtToken));
			}
		}, nodeConfig.ownerDid(), nodeConfig.provider());


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

	public CompletableFuture<Vault> getVault() {
		return context.getVault(nodeConfig.ownerDid(), nodeConfig.provider());
	}

	public String signAuthorization(String jwtToken) {
		try {
			Claims claims = JwtUtil.getBody(jwtToken);
			String iss = claims.getIssuer();
			String nonce = (String) claims.get("nonce");

			VerifiableCredential vc = userDid.issueDiplomaFor(appInstanceDid);
			VerifiablePresentation vp = appInstanceDid.createPresentation(vc, iss, nonce);
			return appInstanceDid.createToken(vp, iss);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private enum EnvironmentType {
		DEVELOPING,
		PRODUCTION,
		LOCAL
	}
}
