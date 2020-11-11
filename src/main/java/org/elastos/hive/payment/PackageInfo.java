package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

import java.util.List;

public class PackageInfo extends Result<PackageInfo> {

	@JsonProperty("pricingPlans")
	private List<PricingPlan> pricingPlans;

	@JsonProperty("paymentSettings")
	private PaymentSettings paymentSettings;

	public List<PricingPlan> pricingPlans() {
		return pricingPlans;
	}

	public PaymentSettings paymentSettings() {
		return paymentSettings;
	}

	public static PackageInfo deserialize(String content) {
		return deserialize(content, PackageInfo.class);
	}
}
