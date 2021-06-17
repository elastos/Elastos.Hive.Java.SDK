package org.elastos.hive.vault.scripting;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.annotations.SerializedName;

class RunScriptParams {
	@SerializedName("context")
	private Context context;
	@SerializedName("params")
	private Object params;

	public RunScriptParams setContext(Context context) {
		this.context = context;
		return this;
	}

	public RunScriptParams setParams(Object params) {
		this.params = params;
		return this;
	}
}
