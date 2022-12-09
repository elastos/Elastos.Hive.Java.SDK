package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class GetScriptResult {
	@SerializedName("scripts")
	private List<ScriptContent> scripts;

	public List<ScriptContent> getScripts() {
		return scripts;
	}
}
