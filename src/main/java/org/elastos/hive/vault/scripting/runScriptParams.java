package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

class runScriptParams {
	@SerializedName("context")
	private ScriptContext context;
	@SerializedName("params")
	private Object params;

	public runScriptParams setContext(ScriptContext context) {
		this.context = context;
		return this;
	}

	public runScriptParams setParams(Object params) {
		this.params = params;
		return this;
	}
}
