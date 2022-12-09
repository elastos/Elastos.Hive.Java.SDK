package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;

public class ScriptContent {
    @SerializedName("name")
    private String name;
    @SerializedName("condition")
    private JSONObject condition;
    @SerializedName("executable")
    private Object executable;
    @SerializedName("allowAnonymousUser")
    private Boolean allowAnonymousUser;
    @SerializedName("allowAnonymousApp")
    private Boolean allowAnonymousApp;

    public ScriptContent setName(String name) {
        this.name = name;
        return this;
    }

    public ScriptContent setCondition(JSONObject condition) {
        this.condition = condition;
        return this;
    }

    public ScriptContent setExecutable(Object executable) {
        this.executable = executable;
        return this;
    }

    public ScriptContent setAllowAnonymousUser(Boolean anonymous) {
        this.allowAnonymousUser = anonymous;
        return this;
    }

    public ScriptContent setAllowAnonymousApp(Boolean anonymous) {
        this.allowAnonymousApp = anonymous;
        return this;
    }
}
