package org.elastos.hive;

import org.elastos.hive.payment.ServiceInfo;
import org.elastos.hive.payment.order.OrderInfo;
import org.elastos.hive.payment.pkg.PackageInfo;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface Payment {

	/**
	 * get vault package information
	 * @return
	 */
	CompletableFuture<PackageInfo> packageInfo();

	/**
	 * start vault service free trial
	 * @return
	 */
	CompletableFuture<Boolean> freeTrial();

	/**
	 * create a package order
	 * @param packageName
	 * @param priceName
	 * @return
	 */
	CompletableFuture<Boolean> createOrder(String packageName, String priceName);

	/**
	 * Pay vault service package order
	 * @param orderId
	 * @param txids
	 * @return
	 */
	CompletableFuture<Boolean> pay(String orderId, List<String> txids);

	/**
	 * Get order information of vault service purchase
	 * @param orderId
	 * @return
	 */
	CompletableFuture<OrderInfo> orderInfo(String orderId);

	/**
	 * Get user order information list of vault service purchase
	 * @return
	 */
	CompletableFuture<List<OrderInfo>> orderList();

	/**
	 * Get user vault service info
	 * @return
	 */
	CompletableFuture<ServiceInfo> serviceInfo();

}
