package org.elastos.hive.vault.scripting;

public class FileHashObtain extends FileExecutable {
	private static final String TYPE = "fileHash";

	public FileHashObtain(String name, String path) {
		super(TYPE, name, path);
	}

	public FileHashObtain(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
