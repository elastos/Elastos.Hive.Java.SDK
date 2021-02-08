package org.elastos.hive.controller;

import org.elastos.hive.Logger;

public abstract class Controller {

	public final void start(String simpleName) {
		Logger.controllerStart(simpleName);
		setUp();
		execute();
		Logger.controllerEnd(simpleName);
	}

	/**
	 * set up params before execute test case
	 */
	protected void setUp() {

	}

	/**
	 * execute test case
	 */
	abstract void execute();

}
