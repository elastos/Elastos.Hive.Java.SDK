package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.Result;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order extends Result<Order> {
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("did")
	private String did;
	@JsonProperty("app_id")
	private String appId;
	@JsonProperty("pricing_info")
	private PricingPlan packageInfo;
	@JsonProperty("pay_time")
	private long payTime;
	@JsonProperty("pay_txids")
	private List<String> payTxids;
	@JsonProperty("state")
	private String state;
	@JsonProperty("creat_time")
	private long creatTime;
	@JsonProperty("modify_time")
	private long modifyTime;
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

	public long payTime() {
		return payTime;
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
