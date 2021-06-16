package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

class ScriptContext {
	@SerializedName("target_did")
	private String targetDid;
	@SerializedName("target_app_did")
	private String targetAppDid;

	public ScriptContext setTargetDid(String targetDid) {
		this.targetDid = targetDid;
		return this;
	}

	public ScriptContext setTargetAppDid(String targetAppDid) {
		this.targetAppDid = targetAppDid;
		return this;
	}
}
