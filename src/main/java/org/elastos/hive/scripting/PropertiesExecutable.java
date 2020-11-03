package org.elastos.hive.scripting;

public class PropertiesExecutable extends FileExecutable{
	private static final String TYPE = "fileProperties";

	public PropertiesExecutable(String name, String path) {
		super(TYPE, name, path);
	}

	public PropertiesExecutable(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
