package org.elastos.hive.scripting;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class AggregatedCondition extends Condition {
	private ArrayList<Condition> conditions;

    public AggregatedCondition(String type, String name, Condition[] conditions) {
    	super(type, name);
        this.conditions = new ArrayList<Condition>();
        if (conditions != null && conditions.length > 0)
        	this.conditions.addAll(Arrays.asList(conditions));
    }

    public AggregatedCondition(String type, String name) {
    	this(type, name, null);
    }

    public AggregatedCondition append(Condition condition) {
    	conditions.add(condition);
    	return this;
    }

	@Override
	public Condition[] getBody() {
		return conditions.toArray(new Condition[0]);
	}
}
