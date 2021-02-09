package org.elastos.hive.activites;

import org.elastos.hive.Application;
import org.elastos.hive.Backup;
import org.elastos.hive.controller.BackupController;

import java.util.concurrent.ExecutionException;

public class BackupActivity extends Activity {

	private Backup backup;

	@Override
	protected void onCreate(Application context) {
		super.onCreate(context);
		try {
			backup = client.getBackup(nodeConfig.ownerDid, nodeConfig.provider).get();
		}catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onResume(Application context) {
		super.onResume(context);
		registerController(BackupController.newInstance(this, nodeConfig.targetDID, nodeConfig.targetHost, backup));
	}


}
