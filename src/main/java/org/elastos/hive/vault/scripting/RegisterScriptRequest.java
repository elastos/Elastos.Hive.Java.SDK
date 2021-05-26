package org.elastos.hive.vault.scripting;

import com.google.gson.annotations.SerializedName;

class RegisterScriptRequest {
    @SerializedName("executable")
    private Executable executable;
    @SerializedName("allowAnonymousUser")
    private Boolean allowAnonymousUser;
    @SerializedName("allowAnonymousApp")
    private Boolean allowAnonymousApp;
    @SerializedName("condition")
    private Condition condition;

    public RegisterScriptRequest setExecutable(Executable executable) {
        this.executable = executable;
        return this;
    }

    public RegisterScriptRequest setAllowAnonymousUser(Boolean allowAnonymousUser) {
        this.allowAnonymousUser = allowAnonymousUser;
        return this;
    }

    public RegisterScriptRequest setAllowAnonymousApp(Boolean allowAnonymousApp) {
        this.allowAnonymousApp = allowAnonymousApp;
        return this;
    }

    public RegisterScriptRequest setCondition(Condition condition) {
        this.condition = condition;
        return this;
    }
}
