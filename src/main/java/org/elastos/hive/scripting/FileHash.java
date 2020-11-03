package org.elastos.hive.scripting;

public class FileHash extends FileExecutable {
	private static final String TYPE = "fileHash";

	public FileHash(String name, String path) {
		super(TYPE, name, path);
	}

	public FileHash(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
