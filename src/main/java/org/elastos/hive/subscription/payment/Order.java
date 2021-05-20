package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
	@SerializedName("order_id")
	private String orderId;
	private String did;
	@SerializedName("app_id")
	private String appId;
	@SerializedName("pricing_info")
	private PricingPlan pricingInfo;
	@SerializedName("pay_txids")
	private List<String> payTxids;
	private String state;
	private String type;
	@SerializedName("creat_time")
	private double createTime;
	@SerializedName("finish_time")
	private double finishTime;

	public String getOrderId() {
		return orderId;
	}

	public String getDid() {
		return did;
	}

	public String getAppId() {
		return appId;
	}

	public PricingPlan getPricingInfo() {
		return pricingInfo;
	}

	public List<String> getPayTxids() {
		return payTxids;
	}

	public String getState() {
		return state;
	}

	public String getType() {
		return type;
	}

	public double getCreateTime() {
		return createTime;
	}

	public double getFinishTime() {
		return finishTime;
	}
}
