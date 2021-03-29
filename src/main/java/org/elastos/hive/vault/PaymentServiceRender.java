package org.elastos.hive.vault;

import org.elastos.hive.AppContext;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.exception.HiveException;
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
public class PaymentServiceRender {
    private ConnectionManager connectionManager;

    public PaymentServiceRender(AppContext context) {
        this.connectionManager = context.getConnectionManager();
    }

    public List<PricingPlan> getPricingPlanList() throws HiveException {
        try {
            return HiveResponseBody.validateBody(
                    connectionManager.getPaymentApi()
                            .getPackageInfo()
                            .execute()
                            .body()).getPricingPlans();
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public List<PricingPlan> getBackupPlanList() throws HiveException {
        try {
            return HiveResponseBody.validateBody(
                    connectionManager.getPaymentApi()
                            .getPackageInfo()
                            .execute()
                            .body()).getBackupPlans();
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public PricingPlan getPricingPlan(String planName) throws HiveException {
        try {
            return getPricePlanByResponseBody(HiveResponseBody.validateBody(
                    connectionManager.getPaymentApi()
                            .getPricingPlan(planName)
                            .execute()
                            .body()));
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public PricingPlan getBackupPlan(String planName) throws HiveException {
        try {
            return getPricePlanByResponseBody(HiveResponseBody.validateBody(
                    connectionManager.getPaymentApi()
                            .getBackupPlan(planName)
                            .execute()
                            .body()));
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    private PricingPlan getPricePlanByResponseBody(PaymentPlanResponseBody respBody) {
        return new PricingPlan().setAmount(respBody.getAmount())
                .setCurrency(respBody.getCurrency())
                .setServiceDays(respBody.getServiceDays())
                .setMaxStorage(respBody.getMaxStorage())
                .setName(respBody.getName());
    }

    public String createPricingOrder(String planName) throws HiveException {
        return createOrder(planName, null);
    }

    public String createBackupOrder(String planName) throws HiveException {
        return createOrder(null, planName);
    }

    private String createOrder(String pricingPlanName, String backupPlanName) throws HiveException {
        try {
            return HiveResponseBody.validateBody(
                    connectionManager.getPaymentApi()
                            .createOrder(new PaymentCreateRequestBody(pricingPlanName, backupPlanName))
                            .execute()
                            .body()).getOrderId();
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public void payOrder(String orderId, List<String> transIds) throws HiveException {
        try {
            HiveResponseBody.validateBody(
                    connectionManager.getPaymentApi()
                            .payOrder(new PayOrderRequestBody()
                                    .setOrderId(orderId)
                                    .setPayTxids(transIds))
                            .execute()
                            .body());
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }

    public Order getOrderInfo(String orderId) throws HiveException {
        try {
            return HiveResponseBody.validateBody(
                    connectionManager.getPaymentApi()
                            .getOrderInfo(orderId)
                            .execute()
                            .body()).getOrderInfo();
        } catch (IOException | HiveException e) {
            throw new HiveException(e.getMessage());
        }
    }
}
