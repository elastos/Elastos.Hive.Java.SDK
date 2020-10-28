package org.elastos.hive.payment.order;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.elastos.hive.database.Result;

import java.util.List;

public class OrderInfo extends Result<OrderInfo> {
	@JsonProperty("order_id")
	private String orderId;
	@JsonProperty("did")
	private String did;
	@JsonProperty("app_id")
	private String appId;
	@JsonProperty("package_info")
	private PackageInfo packageInfo;
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

	public PackageInfo packageInfo() {
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

	public static OrderInfo deserialize(String content) {
		return deserialize(content, OrderInfo.class);
	}
}
