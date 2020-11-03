package org.elastos.hive.scripting;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

public class FileExecutable extends Executable {
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

	public FileExecutable(String type, String name, String path) {
		super(type, name);
		query = new Query(path);
	}

	public FileExecutable(String type, String name, String path, boolean output) {
		super(type, name, output);
		query = new Query(path);
	}

	@Override
	public Object getBody() {
		return query;
	}
}
