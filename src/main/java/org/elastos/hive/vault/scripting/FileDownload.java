package org.elastos.hive.vault.scripting;


public class FileDownload extends FileExecutable {
	private static final String TYPE = "fileDownload";

	public FileDownload(String name, String path) {
		super(TYPE, name, path);
	}

	public FileDownload(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
