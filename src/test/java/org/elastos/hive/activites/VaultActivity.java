package org.elastos.hive.activites;

import org.elastos.hive.Application;
import org.elastos.hive.Vault;
import org.elastos.hive.controller.DatabaseController;
import org.elastos.hive.controller.FileController;
import org.elastos.hive.controller.ScriptController;
import org.elastos.hive.controller.VersionController;

import java.util.concurrent.ExecutionException;

public class VaultActivity extends Activity {

	private Vault vault;

	@Override
	protected void onCreate(Application context) {
		super.onCreate(context);
		try {
			vault = client.getVault(nodeConfig.ownerDid, nodeConfig.provider).get();
		}catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume(Application context) {
		super.onResume(context);
		registerController(VersionController.newInstance(vault));
		registerController(FileController.newInstance(vault.getFiles()));
		registerController(DatabaseController.newInstance(vault.getDatabase()));
		registerController(ScriptController.newInstance(vault.getScripting()));
		//TODO waiting for endpoint to link
//		registerController(HiveUrlController.newInstance(client, vault.getScripting()));
	}

}
