package org.elastos.hive.vault.backup;

import com.google.gson.annotations.SerializedName;

import java.io.IOException;

public class BackupResult {
	enum State {
		STATE_STOP,
		STATE_BACKUP,
		STATE_RESTORE,
	}

	enum Result {
		RESULT_SUCCESS,
		RESULT_FAILED,
		RESULT_PROCESS,
	}

	@SerializedName("state")
	private String state;

	@SerializedName("result")
	private String result;

	@SerializedName("message")
	private String message;

	public State getState() {
		switch (state) {
			case "stop":
				return State.STATE_STOP;
			case "backup":
				return State.STATE_BACKUP;
			case "restore":
				return State.STATE_RESTORE;
			default:
				throw new RuntimeException("Unknown state :" + state);
		}
	}

	public void setState(String state) {
		this.state = state;
	}

	public Result getResult() {
		switch (result) {
			case "success":
				return Result.RESULT_SUCCESS;
			case "failed":
				return Result.RESULT_FAILED;
			case "process":
				return Result.RESULT_PROCESS;
			default:
				throw new RuntimeException("Unknown result :" + result);
		}
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
