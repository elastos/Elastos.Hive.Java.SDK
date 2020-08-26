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

        this.executables = new ArrayList<Executable>();
        if (executables != null && executables.length > 0)
        	this.executables.addAll(Arrays.asList(executables));
    }

    public AggregatedExecutable(String name) {
    	this(name, null);
    }

    public AggregatedExecutable append(Executable condition) {
    	executables.add(condition);
    	return this;
    }

	@Override
	public Executable[] getBody() {
		return executables.toArray(new Executable[0]);
	}
}
