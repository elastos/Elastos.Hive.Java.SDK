package org.elastos.hive;

import org.elastos.did.VerifiableCredential;
import org.elastos.did.VerifiablePresentation;
import org.elastos.did.exception.DIDException;
import org.elastos.did.jwt.Claims;
import org.elastos.hive.controller.Controller;
import org.elastos.hive.didhelper.DIDApp;
import org.elastos.hive.utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Activity {

	private List<Controller> controllers = new ArrayList<>();

	protected ApplicationContext applicationContext;
	protected DIDApp userDid = null;

	protected void onCreate(Application context) {
		UserConfig userConfig = getUserConfig(Application.NetType.MAIN_NET);
		try {
			userDid = new DIDApp(userConfig.userName, userConfig.userMn, context.adapter, userConfig.userPhrasePass, userConfig.userStorepass);
		} catch (DIDException e) {
			e.printStackTrace();
		}
	}

	public String userAuthorization(Application context, String jwtToken) {
		try {
			Claims claims = JwtUtil.getBody(jwtToken);
			String iss = claims.getIssuer();
			String nonce = (String) claims.get("nonce");

			VerifiableCredential vc = userDid.issueDiplomaFor(context.appInstanceDid);

			VerifiablePresentation vp = context.appInstanceDid.createPresentation(vc, iss, nonce);

			String token = context.appInstanceDid.createToken(vp, iss);
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onResume(Application context) {

	}

	protected void onDestroy(Application context) {
	}

	protected void start(Application context) {
		onCreate(context);
		onResume(context);
		for(Controller controller : controllers) {
			controller.start(controller.getClass().getSimpleName());
		}
		onDestroy(context);
	}

	public void registerController(Controller controller) {
		controllers.add(controller);
	}


	protected enum NodeType {
		DEVELOPING,
		PRODUCTION,
		LOCAL
	}

	protected NodeConfig getNodeConfig(NodeType type) {
		String fileName = null;
		switch (type) {
			case DEVELOPING:
				fileName = "DevelopingNode.conf";
				break;
			case PRODUCTION:
				fileName = "ProductionNode.conf";
				break;
			case LOCAL:
				fileName = "TestingNode.conf";
				break;
			default:
				throw new IllegalArgumentException("Node type is invalid");
		}
		Properties properties = Utils.getProperties(fileName);

		return NodeConfig.create()
				.setOwnerDid(properties.getProperty("ownerDid"))
				.setProvider(properties.getProperty("provider"))
				.setTargetDID(properties.getProperty("targetDID"))
				.setTargetHost(properties.getProperty("targetHost"))
				.setStorePath(properties.getProperty("storePath"));
	}


	protected static class NodeConfig {
		protected String ownerDid;
		protected String provider;
		protected String targetDID;
		protected String targetHost;
		protected String storePath;

		public static NodeConfig create() {
			return new NodeConfig();
		}

		public NodeConfig setOwnerDid(String ownerDid) {
			this.ownerDid = ownerDid;
			return this;
		}

		public NodeConfig setProvider(String provider) {
			this.provider = provider;
			return this;
		}

		public NodeConfig setTargetDID(String targetDID) {
			this.targetDID = targetDID;
			return this;
		}

		public NodeConfig setTargetHost(String targetHost) {
			this.targetHost = targetHost;
			return this;
		}

		public NodeConfig setStorePath(String storePath) {
			this.storePath = storePath;
			return this;
		}
	}

	private static class UserConfig {
		private String userDid;
		private String userName;
		private String userMn;
		private String userPhrasePass;
		private String userStorepass;

		public static UserConfig create() {
			return new UserConfig();
		}

		public UserConfig setUserDid(String userDid) {
			this.userDid = userDid;
			return this;
		}

		public UserConfig setUserName(String userName) {
			this.userName = userName;
			return this;
		}

		public UserConfig setUserMn(String userMn) {
			this.userMn = userMn;
			return this;
		}

		public UserConfig setUserPhrasePass(String userPhrasePass) {
			this.userPhrasePass = userPhrasePass;
			return this;
		}

		public UserConfig setUserStorepass(String userStorepass) {
			this.userStorepass = userStorepass;
			return this;
		}
	}

	protected UserConfig getUserConfig(Application.NetType type) {
		String fileName = null;
		switch (type) {
			case MAIN_NET:
				fileName = "MainNetUser.conf";
				break;
			case TEST_NET:
				fileName = "TestNetUser.conf";
				break;
			default:
				throw new IllegalArgumentException("User type is invalid");
		}
		Properties properties = Utils.getProperties(fileName);

		return UserConfig.create()
				.setUserDid(properties.getProperty("userDid"))
				.setUserName(properties.getProperty("userName"))
				.setUserMn(properties.getProperty("userMn"))
				.setUserPhrasePass(properties.getProperty("userPhrasePass"))
				.setUserStorepass(properties.getProperty("userStorepass"));
	}

}
