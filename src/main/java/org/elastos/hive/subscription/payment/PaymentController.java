package org.elastos.hive.subscription.payment;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;

import java.util.List;

public class PaymentController {
	private PaymentAPI paymentAPI;

	public PaymentController(ServiceEndpoint serviceEndpoint) {
		paymentAPI = serviceEndpoint.getConnectionManager().createService(PaymentAPI.class, true);
	}

	public String createOrder(String pricingPlanName, String backupPlanName) throws HiveException {
		try {
			return paymentAPI.createOrder(new PaymentCreateRequestBody(pricingPlanName, backupPlanName))
						.execute()
						.body().getOrderId();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public void payOrder(String orderId, List<String> transIds) throws HiveException {
		 try {
			paymentAPI.payOrder(new PayOrderRequestBody().setOrderId(orderId).setPayTxids(transIds))
						.execute()
						.body();
		 } catch (Exception e) {
			 // TODO:
			 e.printStackTrace();
			 throw new HiveException(e.getMessage());
		 }
	}

	public Order getOrderInfo(String orderId) throws HiveException {
		try {
			return paymentAPI.getOrderInfo(orderId).execute().body().getOrderInfo();
		} catch (Exception e) {
			// TODO:
			e.printStackTrace();
			throw new HiveException(e.getMessage());
		}
	}
}
