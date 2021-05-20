package org.elastos.hive.subscription.payment;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.connection.HiveResponseBody;

import java.io.IOException;
import java.util.List;

public class PaymentController {
	private PaymentAPI paymentAPI;

	public PaymentController(ServiceEndpoint serviceEndpoint) {
		paymentAPI = serviceEndpoint.getConnectionManager().createService(PaymentAPI.class, true);
	}

	public String createOrder(String pricingPlanName, String backupPlanName) throws IOException {
		return HiveResponseBody.validateBody(
				paymentAPI.createOrder(new PaymentCreateRequestBody(pricingPlanName, backupPlanName))
						.execute()
						.body()).getOrderId();
	}

	public void payOrder(String orderId, List<String> transIds) throws IOException {
		HiveResponseBody.validateBody(
				paymentAPI.payOrder(new PayOrderRequestBody().setOrderId(orderId).setPayTxids(transIds))
						.execute()
						.body());
	}

	public Order getOrderInfo(String orderId) throws IOException {
		return HiveResponseBody.validateBody(
				paymentAPI.getOrderInfo(orderId).execute().body()).getOrderInfo();
	}

	public List<PricingPlan> getPricingPlanList() throws IOException {
		return HiveResponseBody.validateBody(
				paymentAPI.getPackageInfo()
						.execute()
						.body()).getPricingPlans();
	}

	public PricingPlan getPricingPlan(String planName) throws IOException {
		return getPricePlanByResponseBody(HiveResponseBody.validateBody(
				paymentAPI.getPricingPlan(planName)
						.execute()
						.body()));
	}

	private PricingPlan getPricePlanByResponseBody(PaymentPlanResponseBody respBody) {
		return new PricingPlan().setAmount(respBody.getAmount())
				.setCurrency(respBody.getCurrency())
				.setServiceDays(respBody.getServiceDays())
				.setMaxStorage(respBody.getMaxStorage())
				.setName(respBody.getName());
	}

	public List<PricingPlan> getBackupPlanList() throws IOException {
		return HiveResponseBody.validateBody(
				paymentAPI.getPackageInfo()
						.execute()
						.body()).getBackupPlans();
	}

	public PricingPlan getBackupPlan(String planName) throws IOException {
		return getPricePlanByResponseBody(HiveResponseBody.validateBody(
				paymentAPI.getBackupPlan(planName)
						.execute()
						.body()));
	}
}
