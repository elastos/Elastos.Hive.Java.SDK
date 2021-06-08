package org.elastos.hive.vault.scripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Convenient class to store and serialize a sequence of executables.
 */
public class AggregatedExecutable extends ExecutableV2 {
	private static final String TYPE = "aggregated";

	private List<ExecutableV2> executables;

    public AggregatedExecutable(String name, ExecutableV2[] executables) {
    	super(TYPE, name);

        this.executables = new ArrayList<>();
        if (executables != null && executables.length > 0)
        	this.executables.addAll(Arrays.asList(executables));
    }

    public AggregatedExecutable(String name) {
    	this(name, null);
    }

    public AggregatedExecutable append(ExecutableV2 executable) {
    	if (executable instanceof AggregatedExecutable) {
    		AggregatedExecutable ae = (AggregatedExecutable)executable;
    		executables.addAll(ae.executables);
    	} else if (executable instanceof RawExecutable) {
    		throw new UnsupportedOperationException("Can not handle the RawExecutable");
    	} else {
    		executables.add(executable);
    	}

    	return this;
    }

	@Override
	public Executable[] getBody() {
		return executables.toArray(new Executable[0]);
	}
}