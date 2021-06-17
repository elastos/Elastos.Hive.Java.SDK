package org.elastos.hive.vault.scripting;

import java.util.HashMap;

public class RunFileParams extends HashMap<String, String> {
    public RunFileParams(String path) {
        super();
        super.put("path", path);
    }
}
