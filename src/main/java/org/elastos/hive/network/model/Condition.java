package org.elastos.hive.network.model;

public class Condition extends Executable {
	@Override
	public Condition setName(String name) {
		super.name = name;
		return this;
	}

	@Override
	public Condition setType(String type) {
		super.type = type;
		return this;
	}

	@Override
	public Condition setBody(Object body) {
		super.body = body;
		return this;
	}
}
