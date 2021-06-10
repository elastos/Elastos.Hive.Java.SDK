package org.elastos.hive.subscription.payment;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.exception.NetworkException;
import org.elastos.hive.exception.NodeRPCException;
import org.elastos.hive.exception.NotImplementedException;
import org.elastos.hive.exception.UnknownServerException;

import java.io.IOException;
import java.util.List;

public class PaymentController {
	private PaymentAPI paymentAPI;

	public PaymentController(ServiceEndpoint serviceEndpoint) {
		paymentAPI = serviceEndpoint.getConnectionManager().createService(PaymentAPI.class, true);
	}

	public Order createOrder(String subscription, String pricingPlan) throws HiveException {
		try {
			return paymentAPI.createOrder(new CreateOrderParams()).execute().body();
		} catch (NodeRPCException e) {
			throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public Receipt payOrder(String orderId, String transIds) throws HiveException {
		try {
			 return paymentAPI.payOrder(new PayOrderParams()).execute().body();
		} catch (NodeRPCException e) {
				throw new UnknownServerException(e);
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	public Order getOrderInfo(String orderId) throws HiveException {
		throw new NotImplementedException();
	}

	public List<Order> getOrders(String subscription) throws HiveException {
		throw new NotImplementedException();
	}

	public List<Receipt> getReceipts(String subscription) throws HiveException {
		throw new NotImplementedException();
	}

	public String getVersion() throws HiveException {
		throw new NotImplementedException();
	}
}
