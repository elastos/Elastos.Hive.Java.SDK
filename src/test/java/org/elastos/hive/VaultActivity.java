package org.elastos.hive;

import org.elastos.hive.controller.FileController;

public class VaultActivity extends Activity {

	@Override
	protected void onCreate(ApplicationContext context) {
		super.onCreate(context);

	}

	@Override
	protected void onResume(ApplicationContext context) {
		super.onResume(context);
		registerController(FileController.newInstance(files));
	}

}
