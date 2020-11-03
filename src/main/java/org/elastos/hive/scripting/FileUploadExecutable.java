package org.elastos.hive.scripting;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class FileUploadExecutable extends Executable {

	private static final String TYPE = "fileUpload";
	private Query query;


	@JsonPropertyOrder({"path"})
	public static class Query {
		private String path;

		public Query(String path) {
			this.path = path;
		}

		@JsonGetter("path")
		public String getPath() {
			return path;
		}

	}

	public FileUploadExecutable(String name, String path) {
		super(TYPE, name);
		query = new Query(path);
	}

	public FileUploadExecutable(String name, String path, boolean output) {
		super(TYPE, name, output);
		query = new Query(path);
	}

	@Override
	public Object getBody() {
		return query;
	}
}
