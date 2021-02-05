package org.elastos.hive;

import org.elastos.hive.controller.Controller;
import org.elastos.hive.didhelper.AppInstanceFactory;

import java.util.ArrayList;
import java.util.List;

public class Activity {

	private Database database;
	private Files files;
	private Management management;
	private Payment payment;
	private Scripting scripting;
	private Backup backup;

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
		for(Controller controller : controllers) {

		}
	}

	protected void onDestroy(ApplicationContext context) {
		backup = null;
		database = null;
		files = null;
		scripting = null;
		management = null;
		payment = null;
	}

	public void registerController(Controller controller) {

	}

}
