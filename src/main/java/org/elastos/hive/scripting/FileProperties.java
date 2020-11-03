package org.elastos.hive.scripting;

public class FileProperties extends FileExecutable{
	private static final String TYPE = "fileProperties";

	public FileProperties(String name, String path) {
		super(TYPE, name, path);
	}

	public FileProperties(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
