package org.elastos.hive;

import org.elastos.hive.didhelper.AppInstanceFactory;

public class Activity {

	private Database database;
	private Files files;
	private Management management;
	private Payment payment;
	private Scripting scripting;
	private Backup backup;

	protected void onCreate(ApplicationContext context) {
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

}
