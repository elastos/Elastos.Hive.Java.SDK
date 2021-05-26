package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

class CallScriptRequest {
    @SerializedName("context")
    private ScriptContext context;
    @SerializedName("params")
    private Object params;

    public CallScriptRequest setContext(ScriptContext context) {
        this.context = context;
        return this;
    }

    public CallScriptRequest setParams(Object params) {
        this.params = params;
        return this;
    }
}
