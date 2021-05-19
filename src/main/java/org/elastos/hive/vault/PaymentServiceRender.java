package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.network.request.PayOrderRequestBody;
import org.elastos.hive.network.request.PaymentCreateRequestBody;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.payment.Order;

import java.io.IOException;
import java.util.List;

/**
 * Helper class for vault/backup subscription.
 */
public class PaymentServiceRender {
	private ServiceEndpoint serviceEndpoint;

    public PaymentServiceRender(ServiceEndpoint serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public String createPricingOrder(String planName) throws IOException {
        return createOrder(planName, null);
    }

    public String createBackupOrder(String planName) throws IOException {
        return createOrder(null, planName);
    }

    private String createOrder(String pricingPlanName, String backupPlanName) throws IOException {
        return HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .createOrder(new PaymentCreateRequestBody(pricingPlanName, backupPlanName))
                        .execute()
                        .body()).getOrderId();
    }

    public void payOrder(String orderId, List<String> transIds) throws IOException {
        HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .payOrder(new PayOrderRequestBody()
                                .setOrderId(orderId)
                                .setPayTxids(transIds))
                        .execute()
                        .body());
    }

    public Order getOrderInfo(String orderId) throws IOException {
        return HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .getOrderInfo(orderId)
                        .execute()
                        .body()).getOrderInfo();
    }
}
