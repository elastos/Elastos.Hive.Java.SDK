package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;

import org.elastos.hive.exception.HiveException;

public abstract class Executable extends Condition {
//	public static final String TYPE_FIND = "find";
//	private static final String TYPE_INSERT = "insert";
//	private static final String TYPE_UPDATE = "update";
//	private static final String TYPE_DELETE = "delete";
//	private static final String TYPE_FILE_UPLOAD = "fileUpload";
//	private static final String TYPE_FILE_DOWNLOAD = "fileDownload";
//	private static final String TYPE_FILE_PROPERTIES = "fileProperties";
//	private static final String TYPE_FILE_HASH = "fileHash";

	protected enum Type {
		AGGREGATED("aggregated"), // TODO:
		FIND("find"),
		INSERT("insert"),
		UPDATE("update"),
		DELETE("delete"),
		FILE_UPLOAD("fileUpload"),
		FILE_DOWNLOAD("fileDownload"),
		FILE_PROPERTIES("fileProperties"),
		FILE_HASH("fileHash");

		private String value;

		Type(String value) {
			this.value = value;
		}

		String getValue() {
			return value;
		}
	}

	@SerializedName("output")
	private Boolean output;

	protected Executable(String name, Type type, Object body) {
		super(name, type.getValue(), body);
	}

	public Executable setOutput(Boolean output) {
		this.output = output;
		return this;
	}

	protected abstract class DatabaseBody {
		@SerializedName("collection")
		String collection;
		DatabaseBody(String collection) {
			this.collection = collection;
		}
	}

	protected class FileBody {
		@SerializedName("path")
		private String path;

		public FileBody() {
			this.path = "$params.path";
		}
	}

//	public static Executable createFileUploadExecutable(String name) {
//		return new Executable(name, Executable.TYPE_FILE_UPLOAD,
//				new ScriptFileUploadBody("$params.path")).setOutput(true);
//	}

	public static JsonNode createFileUploadParams(String groupId, String path) throws HiveException {
		try {
			return new ObjectMapper().readTree(String.format("{\"group_id\":{\"$oid\":\"%s\"},\"path\":\"%s\"}", groupId, path));
		} catch (Exception e) {
			throw new HiveException("invalid groupId or path");
		}
	}

//	public static Executable createFileDownloadExecutable(String name) {
//		return new Executable(name, Executable.TYPE_FILE_DOWNLOAD,
//				new ScriptFileUploadBody("$params.path")).setOutput(true);
//	}

	public static JsonNode createFileDownloadParams(String groupId, String path) {
		try {
			return createFileUploadParams(groupId, path);
		} catch (HiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

//	public static Executable createFilePropertiesExecutable(String name) {
//		return new Executable(name, Executable.TYPE_FILE_PROPERTIES,
//				new ScriptFileUploadBody("$params.path")).setOutput(true);
//	}

	public static JsonNode createFilePropertiesParams(String groupId, String path) {
		try {
			return createFileUploadParams(groupId, path);
		} catch (HiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

//	public static Executable createFileHashExecutable(String name) {
//		return new Executable(name, Executable.TYPE_FILE_HASH,
//				new ScriptFileUploadBody("$params.path")).setOutput(true);
//	}

	public static JsonNode createFileHashParams(String groupId, String path) {
		try {
			return createFileUploadParams(groupId, path);
		} catch (HiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
