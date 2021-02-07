package org.elastos.hive;

import org.elastos.hive.controller.Controller;
import org.elastos.hive.didhelper.AppInstanceFactory;

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

	protected void onCreate(ApplicationContext context) {
		controllers.clear();
		Vault vault = AppInstanceFactory.configSelector().getVault();
		backup = AppInstanceFactory.configSelector().getBackup();
		database = vault.getDatabase();
		files = vault.getFiles();
		scripting = vault.getScripting();

		management = AppInstanceFactory.configSelector().getManagement();
		payment = management.getPayment();
	}

	protected void onResume(ApplicationContext context) {

	}

	protected void onDestroy(ApplicationContext context) {
		backup = null;
		database = null;
		files = null;
		scripting = null;
		management = null;
		payment = null;
	}

	protected void start(ApplicationContext context) {
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

	protected void getNodeConfig(NodeType type) {
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
	}


	protected enum UserType {
		MAIN_NET,
		TEST_NET,
	}

	protected void getUserConfig(UserType type) {
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
	}

}
