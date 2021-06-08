package org.elastos.hive.vault.scripting;

public class FileUpload extends FileExecutable {
	private static final String TYPE = "fileUpload";

	public FileUpload(String name, String path) {
		super(TYPE, name, path);
	}

	public FileUpload(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
