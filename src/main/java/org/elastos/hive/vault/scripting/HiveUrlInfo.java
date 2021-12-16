package org.elastos.hive.vault.scripting;

import java.security.InvalidParameterException;

class HiveUrlInfo {
    private static final String HIVE_URL_PREFIX = "hive://";

    private String targetDid;
    private String targetAppDid;
    private String scriptName;
    private String params;

    HiveUrlInfo(String hiveUrl) {
        if (hiveUrl == null || !hiveUrl.startsWith(HIVE_URL_PREFIX))
            throw new InvalidParameterException("Invalid hive url: no hive prefix.");

        String[] parts = hiveUrl.substring(HIVE_URL_PREFIX.length()).split("/");
        if (parts.length < 2)
            throw new InvalidParameterException("Invalid hive url: must contain at least one slash.");

        String[] dids = parts[0].split("@");
        if (dids.length != 2)
            throw new InvalidParameterException("Invalid hive url: must contain two DIDs.");

        String[] values = hiveUrl.substring(HIVE_URL_PREFIX.length() + parts[0].length() + 1).split("\\?params=");
        if (values.length != 2)
            throw new InvalidParameterException("Invalid hive url: must contain script name and params.");

        targetDid = dids[0];
        targetAppDid = dids[1];
        scriptName = values[0];
        params = values[1];
    }

    public String getTargetDid() {
        return targetDid;
    }

    public String getTargetAppDid() {
        return targetAppDid;
    }

    public String getScriptName() {
        return scriptName;
    }

    public String getParams() {
        return params;
    }
}
