package org.elastos.hive.network.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.annotations.SerializedName;
import org.elastos.hive.exception.HiveSdkException;

public class Executable extends Condition {
	public static final String TYPE_FIND = "find";
	private static final String TYPE_FILE_UPLOAD = "fileUpload";
	private static final String TYPE_FILE_DOWNLOAD = "fileDownload";

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
}
