package org.elastos.hive.scripting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Convenient class to store and serialize a sequence of executables.
 */
public class AggregatedExecutable extends Executable {
	private static final String TYPE = "aggregated";

	private List<Executable> executables;

    public AggregatedExecutable(String name, Executable[] executables) {
    	super(TYPE, name);

        this.executables = new ArrayList<>();
        if (executables != null && executables.length > 0)
        	this.executables.addAll(Arrays.asList(executables));
    }

    public AggregatedExecutable(String name) {
    	this(name, null);
    }

    public AggregatedExecutable append(Executable executable) {
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
