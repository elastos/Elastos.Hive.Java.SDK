package org.elastos.hive.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OrderList {
	@JsonProperty("order_info_list")
	private List<Order> orderInfoList;

	public List<Order> orderInfoList() {
		return orderInfoList;
	}
}
