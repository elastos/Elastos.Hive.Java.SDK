package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

class RunScriptParams {
	@SerializedName("context")
	private ScriptContext context;
	@SerializedName("params")
	private Object params;

	public RunScriptParams setContext(ScriptContext context) {
		this.context = context;
		return this;
	}

	public RunScriptParams setParams(Object params) {
		this.params = params;
		return this;
	}
}
