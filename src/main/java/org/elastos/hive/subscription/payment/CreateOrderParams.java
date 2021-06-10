package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;

class CreateOrderParams {
	@SerializedName("subscription")
	private String subscription;

	@SerializedName("pricing_plan")
	private String pricingPlan;

	public CreateOrderParams() {
	}

	public CreateOrderParams(String subscription, String pricingPlan) {
		this.subscription = subscription;
		this.pricingPlan = pricingPlan;
	}
}
