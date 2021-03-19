package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.model.ScriptContext;

public class CallScriptRequestBody {
    @SerializedName("name")
    private String name;
    @SerializedName("context")
    private ScriptContext context;
    @SerializedName("params")
    private Object params;

    public CallScriptRequestBody setName(String name) {
        this.name = name;
        return this;
    }

    public CallScriptRequestBody setContext(ScriptContext context) {
        this.context = context;
        return this;
    }

    public CallScriptRequestBody setParams(Object params) {
        this.params = params;
        return this;
    }
}
