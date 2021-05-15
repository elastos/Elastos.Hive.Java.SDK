package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.network.request.PayOrderRequestBody;
import org.elastos.hive.network.request.PaymentCreateRequestBody;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.PaymentPlanResponseBody;
import org.elastos.hive.payment.Order;
import org.elastos.hive.payment.PricingPlan;

import java.io.IOException;
import java.util.List;

/**
 * Helper class for vault/backup subscription.
 */
public class PaymentServiceRender extends BaseServiceRender {

    public PaymentServiceRender(ServiceEndpoint serviceEndpoint) {
        super(serviceEndpoint);
    }

    public String createPricingOrder(String planName) throws IOException {
        return createOrder(planName, null);
    }

    public String createBackupOrder(String planName) throws IOException {
        return createOrder(null, planName);
    }

    private String createOrder(String pricingPlanName, String backupPlanName) throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .createOrder(new PaymentCreateRequestBody(pricingPlanName, backupPlanName))
                        .execute()
                        .body()).getOrderId();
    }

    public void payOrder(String orderId, List<String> transIds) throws IOException {
        HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .payOrder(new PayOrderRequestBody()
                                .setOrderId(orderId)
                                .setPayTxids(transIds))
                        .execute()
                        .body());
    }

    public Order getOrderInfo(String orderId) throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .getOrderInfo(orderId)
                        .execute()
                        .body()).getOrderInfo();
    }
}
