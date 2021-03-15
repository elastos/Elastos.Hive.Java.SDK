package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

import java.util.List;

public class PricingInfo extends Result<PricingInfo> {

	@JsonProperty("pricingPlans")
	private List<PricingPlan> pricingPlans;

	public List<PricingPlan> pricingPlans() {
		return pricingPlans;
	}

	public static PricingInfo deserialize(String content) {
		return deserialize(content, PricingInfo.class);
	}
}
