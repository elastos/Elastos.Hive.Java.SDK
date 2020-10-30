package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

import java.util.List;

public class Order extends Result<Order> {
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("did")
	private String did;
	@JsonProperty("app_id")
	private String appId;
	@JsonProperty("package_info")
	private PricingPlan packageInfo;
	@JsonProperty("pay_txids")
	private List<String> payTxids;
	@JsonProperty("state")
	private String state;
	@JsonProperty("creat_time")
	private long creatTime;
	@JsonProperty("finish_time")
	private long finishTime;

	public String orderId() {
		return orderId;
	}

	public String did() {
		return did;
	}

	public String appId() {
		return appId;
	}

	public PricingPlan packageInfo() {
		return packageInfo;
	}

	public List<String> payTxids() {
		return payTxids;
	}

	public String state() {
		return state;
	}

	public long creatTime() {
		return creatTime;
	}

	public long finishTime() {
		return finishTime;
	}

	public static Order deserialize(String content) {
		return deserialize(content, Order.class);
	}
}