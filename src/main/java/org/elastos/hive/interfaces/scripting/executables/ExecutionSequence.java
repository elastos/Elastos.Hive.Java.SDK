package org.elastos.hive.interfaces.scripting.executables;

import org.json.JSONArray;

public class ExecutionSequence {
    private Executable[] executables;

    public ExecutionSequence(Executable[] executables) {
        this.executables = executables;
    }

    public JSONArray toJSON() {
        JSONArray jsonExecutables = new JSONArray();

        for (Executable e : executables) {
            jsonExecutables.put(e.toJSON());
        }

        return jsonExecutables;
    }
}
