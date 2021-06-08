package org.elastos.hive.vault.scripting;

public class FilePropertiesObtain extends FileExecutable{
	private static final String TYPE = "fileProperties";

	public FilePropertiesObtain(String name, String path) {
		super(TYPE, name, path);
	}

	public FilePropertiesObtain(String name, String path, boolean output) {
		super(TYPE, name, path, output);
	}
}
