package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.vault.payment.Order;
import org.elastos.hive.vault.payment.PaymentController;

import java.io.IOException;
import java.util.List;

/**
 * Helper class for vault/backup subscription.
 */
public class PaymentServiceRender {
	private PaymentController controller;

    public PaymentServiceRender(ServiceEndpoint serviceEndpoint) {
        controller = new PaymentController(serviceEndpoint);
    }

    public String createPricingOrder(String planName) throws IOException {
        return createOrder(planName, null);
    }

    public String createBackupOrder(String planName) throws IOException {
        return createOrder(null, planName);
    }

    private String createOrder(String pricingPlanName, String backupPlanName) throws IOException {
        return controller.createOrder(pricingPlanName, backupPlanName);
    }

    public void payOrder(String orderId, List<String> transIds) throws IOException {
        controller.payOrder(orderId, transIds);
    }

    public Order getOrderInfo(String orderId) throws IOException {
        return controller.getOrderInfo(orderId);
    }
}
