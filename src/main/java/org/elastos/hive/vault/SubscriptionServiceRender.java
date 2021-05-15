package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.elastos.hive.network.response.HiveResponseBody;
import org.elastos.hive.network.response.PaymentPlanResponseBody;
import org.elastos.hive.network.response.VaultCreateResponseBody;
import org.elastos.hive.network.response.VaultInfoResponseBody;
import org.elastos.hive.payment.PricingPlan;

import java.io.IOException;
import java.util.List;

/**
 * Helper class for subscription api.
 */
public class SubscriptionServiceRender extends BaseServiceRender {

    public SubscriptionServiceRender(ServiceEndpoint serviceEndpoint) {
        super(serviceEndpoint);
    }

    public void subscribe() throws IOException {
        VaultCreateResponseBody body = HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .createVault()
                        .execute()
                        .body());
        if (Boolean.TRUE.equals(body.getExisting())) {
            throw new VaultAlreadyExistException("The vault already exists");
        }
    }

    public void subscribeBackup() throws IOException {
        VaultCreateResponseBody body = HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .createBackupVault()
                        .execute()
                        .body());
        if (Boolean.TRUE.equals(body.getExisting())) {
            throw new VaultAlreadyExistException("The backup vault already exists");
        }
    }

    public void unsubscribe() throws IOException {
        HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .removeVault()
                        .execute()
                        .body());
    }

    public void activate() throws IOException {
        HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .unfreeze()
                        .execute()
                        .body());
    }

    public void deactivate() throws IOException {
        HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .freeze()
                        .execute()
                        .body());
    }

    public VaultInfoResponseBody getVaultInfo() throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .getVaultInfo()
                        .execute()
                        .body());
    }

    public VaultInfoResponseBody getBackupVaultInfo() throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .getBackupVaultInfo()
                        .execute()
                        .body());
    }

    public List<PricingPlan> getPricingPlanList() throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .getPackageInfo()
                        .execute()
                        .body()).getPricingPlans();
    }

    public PricingPlan getPricingPlan(String planName) throws IOException {
        return getPricePlanByResponseBody(HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .getPricingPlan(planName)
                        .execute()
                        .body()));
    }

    public List<PricingPlan> getBackupPlanList() throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .getPackageInfo()
                        .execute()
                        .body()).getBackupPlans();
    }

    public PricingPlan getBackupPlan(String planName) throws IOException {
        return getPricePlanByResponseBody(HiveResponseBody.validateBody(
                getConnectionManager().getCallAPI()
                        .getBackupPlan(planName)
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
}
