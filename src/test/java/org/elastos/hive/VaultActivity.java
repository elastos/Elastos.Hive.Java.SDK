package org.elastos.hive;

import org.elastos.hive.controller.FileController;

public class VaultActivity extends Activity {

	@Override
	protected void onCreate(Application context) {
		super.onCreate(context);

	}

	@Override
	protected void onResume(Application context) {
		super.onResume(context);
		registerController(FileController.newInstance(files));
	}

}
