package org.elastos.hive.scripting;

public class HashExecutable extends FileExecutable {
	private static final String TYPE = "fileHash";

	public HashExecutable(String name, String path) {
		super(TYPE, name, path);
	}

	public HashExecutable(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
