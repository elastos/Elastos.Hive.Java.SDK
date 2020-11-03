package org.elastos.hive.scripting;


public class DownloadExecutable extends FileExecutable {
	private static final String TYPE = "fileDownload";

	public DownloadExecutable(String name, String path) {
		super(TYPE, name, path);
	}

	public DownloadExecutable(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}

}
