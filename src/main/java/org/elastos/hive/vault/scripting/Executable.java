package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;
import org.elastos.hive.exception.HiveSdkException;

public class Executable extends Condition {
	public static final String TYPE_FIND = "find";
	private static final String TYPE_INSERT = "insert";
	private static final String TYPE_UPDATE = "update";
	private static final String TYPE_DELETE = "delete";
	private static final String TYPE_FILE_UPLOAD = "fileUpload";
	private static final String TYPE_FILE_DOWNLOAD = "fileDownload";
	private static final String TYPE_FILE_PROPERTIES = "fileProperties";
	private static final String TYPE_FILE_HASH = "fileHash";

	@SerializedName("output")
	private Boolean output;

	public Executable(String name, String type, Object body) {
		super(name, type, body);
	}

	public Executable setOutput(boolean output) {
		this.output = output;
		return this;
	}

	public static Executable createFileUploadExecutable(String name) {
		return new Executable(name, Executable.TYPE_FILE_UPLOAD,
				new ScriptFileUploadBody("$params.path")).setOutput(true);
	}

	public static JsonNode createFileUploadParams(String groupId, String path) {
		try {
			return new ObjectMapper().readTree(String.format("{\"group_id\":{\"$oid\":\"%s\"},\"path\":\"%s\"}", groupId, path));
		} catch (Exception e) {
			throw new HiveSdkException("invalid groupId or path");
		}
	}

	public static Executable createFileDownloadExecutable(String name) {
		return new Executable(name, Executable.TYPE_FILE_DOWNLOAD,
				new ScriptFileUploadBody("$params.path")).setOutput(true);
	}

	public static JsonNode createFileDownloadParams(String groupId, String path) {
		return createFileUploadParams(groupId, path);
	}

	public static Executable createFilePropertiesExecutable(String name) {
		return new Executable(name, Executable.TYPE_FILE_PROPERTIES,
				new ScriptFileUploadBody("$params.path")).setOutput(true);
	}

	public static JsonNode createFilePropertiesParams(String groupId, String path) {
		return createFileUploadParams(groupId, path);
	}

	public static Executable createFileHashExecutable(String name) {
		return new Executable(name, Executable.TYPE_FILE_HASH,
				new ScriptFileUploadBody("$params.path")).setOutput(true);
	}

	public static JsonNode createFileHashParams(String groupId, String path) {
		return createFileUploadParams(groupId, path);
	}

	public static Executable createInsertExecutable(String colletion, ScriptInsertExecutableBody body) {
		return new Executable(colletion, TYPE_INSERT, body).setOutput(true);
	}

	public static Executable createUpdateExecutable(String collection, ScriptUpdateExecutableBody body) {
		return new Executable(collection, TYPE_UPDATE, body).setOutput(true);
	}

	public static Executable createDeleteExecutable(String collection, ScriptDeleteExecutableBody body) {
		return new Executable(collection, TYPE_DELETE, body).setOutput(true);
	}
}
