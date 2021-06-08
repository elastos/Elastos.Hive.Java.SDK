package org.elastos.hive.vault.scripting;

import org.elastos.hive.exception.HiveException;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"type", "name", "output", "body"})
public abstract class ExecutableV2 {
	private String type;
	private String name;
	private boolean output;

	protected ExecutableV2(String type, String name) {
		this.type = type;
		this.name = name;
		this.output = false;
	}

	protected ExecutableV2(String type, String name, boolean output) {
		this.type = type;
		this.name = name;
		this.output = output;
	}

	@JsonGetter("type")
	public String getType() {
		return type;
	}

	@JsonGetter("name")
	public String getName() {
		return name;
	}

	@JsonGetter("output")
	public boolean getOutput() {
		return output;
	}

	@JsonGetter("body")
	public abstract Object getBody();

	public String serialize() throws HiveException {
        ObjectMapper mapper = new ObjectMapper();

        try {
			return mapper.writer().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			throw new HiveException(e);
		}
	}
}
