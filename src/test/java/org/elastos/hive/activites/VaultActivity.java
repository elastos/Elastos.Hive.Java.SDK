package org.elastos.hive.activites;

import org.elastos.hive.Application;
import org.elastos.hive.Vault;
import org.elastos.hive.controller.DatabaseController;
import org.elastos.hive.controller.FileController;
import org.elastos.hive.controller.HiveUrlController;
import org.elastos.hive.controller.ScriptController;

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
		registerController(FileController.newInstance(vault.getFiles()));
		registerController(DatabaseController.newInstance(vault.getDatabase()));
		registerController(ScriptController.newInstance(vault.getScripting()));
		registerController(HiveUrlController.newInstance(client, vault.getScripting()));
	}

}
