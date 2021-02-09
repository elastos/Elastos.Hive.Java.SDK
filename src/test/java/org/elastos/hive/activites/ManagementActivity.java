package org.elastos.hive.activites;

import org.elastos.hive.Application;
import org.elastos.hive.Management;
import org.elastos.hive.controller.ManagementController;

import java.util.concurrent.ExecutionException;

public class ManagementActivity extends Activity{

	private Management management;

	@Override
	protected void onCreate(Application context) {
		super.onCreate(context);
		try {
			management = client.getManager(nodeConfig.ownerDid, nodeConfig.provider).get();
		}catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onResume(Application context) {
		super.onResume(context);
		registerController(ManagementController.newInstance(management));
	}
}
