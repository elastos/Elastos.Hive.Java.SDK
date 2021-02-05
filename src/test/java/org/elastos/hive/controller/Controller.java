package org.elastos.hive.controller;

import org.elastos.hive.Logger;

public abstract class Controller {

	public void start(String simpleName) {
		Logger.controllerStart(simpleName);
		setUp();
		loadTest();
		Logger.controllerEnd(simpleName);
	}

	protected void setUp() {

	}

	abstract void loadTest();

}
