package org.elastos.hive;

import org.elastos.did.exception.DIDException;
import org.elastos.hive.controller.Controller;
import org.elastos.hive.didhelper.AppInstanceFactory;
import org.elastos.hive.didhelper.DIDApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Activity {

	protected Database database;
	protected Files files;
	protected Management management;
	protected Payment payment;
	protected Scripting scripting;
	protected Backup backup;

	private List<Controller> controllers = new ArrayList<>();

	private DIDApp userDidApp = null;

	protected void onCreate(Application context) {
		UserConfig userConfig = getUserConfig(UserType.MAIN_NET);
		try {
			userDidApp = new DIDApp(userConfig.userName, userConfig.userMn, context.adapter, userConfig.userPhrasePass, userConfig.userStorepass);
		} catch (DIDException e) {
			e.printStackTrace();
		}

		controllers.clear();
		Vault vault = AppInstanceFactory.configSelector().getVault();
		backup = AppInstanceFactory.configSelector().getBackup();
		database = vault.getDatabase();
		files = vault.getFiles();
		scripting = vault.getScripting();

		management = AppInstanceFactory.configSelector().getManagement();
		payment = management.getPayment();

	}

	protected void onResume(Application context) {

	}

	protected void onDestroy(Application context) {
		backup = null;
		database = null;
		files = null;
		scripting = null;
		management = null;
		payment = null;
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
				.setProvider(properties.getProperty("provider"))
				.setTargetDID(properties.getProperty("targetDID"))
				.setTargetHost(properties.getProperty("targetHost"))
				.setStorePath(properties.getProperty("storePath"));
	}


	private static class NodeConfig {
		private String provider;
		private String targetDID;
		private String targetHost;
		private String storePath;

		public static NodeConfig create() {
			return new NodeConfig();
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

	protected enum UserType {
		MAIN_NET,
		TEST_NET,
	}

	private static class UserConfig {
		private String userDid;
		private String userName;
		private String userMn;
		private String userPhrasePass;
		private String userStorepass;
		private String ownerDid;

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

		public UserConfig setOwnerDid(String ownerDid) {
			this.ownerDid = ownerDid;
			return this;
		}
	}

	protected UserConfig getUserConfig(UserType type) {
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
				.setUserStorepass(properties.getProperty("userStorepass"))
				.setOwnerDid(properties.getProperty("ownerDid"));
	}

}
