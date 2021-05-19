package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.network.response.PaymentPlanResponseBody;
import org.elastos.hive.network.response.VaultCreateResponseBody;
import org.elastos.hive.network.response.VaultInfoResponseBody;
import org.elastos.hive.payment.PricingPlan;

import java.io.IOException;
import java.util.List;

public class SubscriptionServiceRender {
	private ServiceEndpoint serviceEndpoint;

    public SubscriptionServiceRender(ServiceEndpoint serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
    }

    public void subscribe() throws IOException {
        VaultCreateResponseBody body = HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .createVault()
                        .execute()
                        .body());
        if (Boolean.TRUE.equals(body.getExisting())) {
            throw new VaultAlreadyExistException("The vault already exists");
        }
    }

    public void subscribeBackup() throws IOException {
        VaultCreateResponseBody body = HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .createBackupVault()
                        .execute()
                        .body());
        if (Boolean.TRUE.equals(body.getExisting())) {
            throw new VaultAlreadyExistException("The backup vault already exists");
        }
    }

    public void unsubscribe() throws IOException {
        HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .removeVault()
                        .execute()
                        .body());
    }

    public void activate() throws IOException {
        HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .unfreeze()
                        .execute()
                        .body());
    }

    public void deactivate() throws IOException {
        HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .freeze()
                        .execute()
                        .body());
    }

    public VaultInfoResponseBody getVaultInfo() throws IOException {
        return HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .getVaultInfo()
                        .execute()
                        .body());
    }

    public VaultInfoResponseBody getBackupVaultInfo() throws IOException {
        return HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .getBackupVaultInfo()
                        .execute()
                        .body());
    }

    public List<PricingPlan> getPricingPlanList() throws IOException {
        return HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .getPackageInfo()
                        .execute()
                        .body()).getPricingPlans();
    }

    public PricingPlan getPricingPlan(String planName) throws IOException {
        return getPricePlanByResponseBody(HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .getPricingPlan(planName)
                        .execute()
                        .body()));
    }

    public List<PricingPlan> getBackupPlanList() throws IOException {
        return HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
                        .getPackageInfo()
                        .execute()
                        .body()).getBackupPlans();
    }

    public PricingPlan getBackupPlan(String planName) throws IOException {
        return getPricePlanByResponseBody(HiveResponseBody.validateBody(
        		serviceEndpoint.getConnectionManager().getCallAPI()
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
