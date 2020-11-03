package org.elastos.hive.scripting;

public class UploadExecutable extends FileExecutable {
	private static final String TYPE = "fileUpload";

	public UploadExecutable(String name, String path) {
		super(TYPE, name, path);
	}

	public UploadExecutable(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
