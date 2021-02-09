package org.elastos.hive.activites;

import org.elastos.hive.Application;
import org.elastos.hive.Backup;
import org.elastos.hive.Client;
import org.elastos.hive.Management;
import org.elastos.hive.controller.MigrationController;

import java.util.concurrent.ExecutionException;

public class MigrationActivity extends Activity {

	private Management management;
	private Backup backup;

	@Override
	protected void onCreate(Application context) {
		super.onCreate(context);
		try {
			management = client.getManager(nodeConfig.ownerDid, nodeConfig.provider).get();
			backup = client.getBackup(nodeConfig.ownerDid, nodeConfig.provider).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume(Application context) {
		super.onResume(context);
		registerController(MigrationController.newInstance(this, nodeConfig.targetDID, nodeConfig.targetHost));
	}


	public Management getManagement() {
		return this.management;
	}

	public Backup getBackup() {
		return this.backup;
	}

	public Client getClient() {
		return client;
	}
}
