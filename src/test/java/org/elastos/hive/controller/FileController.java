package org.elastos.hive.controller;

import org.elastos.hive.Files;

public class FileController implements Controller<Files> {

	private static FileController mInstance = null;

	public static FileController newInstance() {
		if(mInstance == null) {
			mInstance = new FileController();
		}

		return mInstance;
	}

	@Override
	public void loadTest(Files api) {

	}

}
