package org.elastos.hive.controller;

public abstract class Controller {

	public void start() {
		setUp();
		loadTest();
	}

	protected void setUp() {

	}

	abstract void loadTest();

}
