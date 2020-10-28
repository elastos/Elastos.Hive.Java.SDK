package org.elastos.hive.payment.pkg;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

public class PaySetting extends Result<PaySetting> {
	@JsonProperty("receivingELAAddress")
	private String receivingELAAddress;

	public String receivingELAAddress() {
		return receivingELAAddress;
	}

	public static PaySetting deserialize(String content) {
		return deserialize(content, PaySetting.class);
	}
}
