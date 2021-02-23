package org.elastos.hive.config;

import org.elastos.did.DIDDocument;
import org.elastos.did.VerifiableCredential;
import org.elastos.did.VerifiablePresentation;
import org.elastos.did.adapter.DummyAdapter;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.ApplicationContext;
import org.elastos.hive.Client;
import org.elastos.hive.Utils;
import org.elastos.hive.didhelper.DApp;
import org.elastos.hive.didhelper.DIDApp;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.utils.JwtUtil;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class TestData {

	private DApp appInstanceDid;

	private DIDApp userDid = null;

	public Client client;


	private ApplicationContext applicationContext;

	public void init() throws HiveException, DIDException {

		NetType netType = NetType.TEST_NET;
		NodeType nodeType = NodeType.DEVELOPING;

		Client.setupResolver((netType == NetType.MAIN_NET) ? "http://api.elastos.io:20606" : "http://api.elastos.io:21606", "data/didCache");

		DummyAdapter adapter = new DummyAdapter();
		AppConfig appConfig = AppConfig.deserialize(Utils.getConfigure((netType == NetType.MAIN_NET) ? "MainNetApp.conf" : "TestNetApp.conf"));
		appInstanceDid = new DApp(appConfig.name(), appConfig.mnemonic(), adapter, appConfig.passPhrase(), appConfig.storepass());

		UserConfig userConfig = UserConfig.deserialize(Utils.getConfigure((netType == NetType.MAIN_NET) ? "MainNetUser.conf" : "TestNetUser.conf"));
		userDid = new DIDApp(userConfig.name(), userConfig.mnemonic(), adapter, userConfig.passPhrase(), userConfig.storePass());

		String fileName = null;
		switch (nodeType) {
			case DEVELOPING:
				fileName = "DevelopingNode.conf";
				break;
			case PRODUCTION:
				fileName = "ProductionNode.conf";
				break;
			case LOCAL:
				fileName = "LocalNode.conf";
				break;
		}
		NodeConfig nodeConfig = NodeConfig.deserialize(fileName);

		//初始化Application Context
		applicationContext = new ApplicationContext() {
			@Override
			public String getLocalDataDir() {
				return System.getProperty("user.dir") + File.separator + "data/store/" + File.separator + nodeConfig.storePath();
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
		};

		client = Client.createInstance(applicationContext);
	}

	public String signAuthorization(String jwtToken) {
		try {
			Claims claims = JwtUtil.getBody(jwtToken);
			String iss = claims.getIssuer();
			String nonce = (String) claims.get("nonce");

			VerifiableCredential vc = userDid.issueDiplomaFor(appInstanceDid);

			VerifiablePresentation vp = appInstanceDid.createPresentation(vc, iss, nonce);

			String token = appInstanceDid.createToken(vp, iss);
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public enum NodeType {
		DEVELOPING,
		PRODUCTION,
		LOCAL
	}

	public enum NetType {
		MAIN_NET,
		TEST_NET,
	}
}
