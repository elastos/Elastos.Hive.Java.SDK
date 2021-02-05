package org.elastos.hive;

import org.elastos.hive.controller.Controller;
import org.elastos.hive.didhelper.AppInstanceFactory;

import java.util.ArrayList;
import java.util.List;

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
			controller.start();
		}
		onDestroy(context);
	}

	public void registerController(Controller controller) {
		controllers.add(controller);
	}

}
