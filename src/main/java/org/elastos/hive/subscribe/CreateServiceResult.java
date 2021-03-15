package org.elastos.hive.subscribe;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

public class CreateServiceResult extends Result<CreateServiceResult> {

	@JsonProperty("existing")
	private boolean existing;

	public boolean existing() {
		return existing;
	}

}
