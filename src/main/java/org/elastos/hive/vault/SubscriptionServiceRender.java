package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.elastos.hive.connection.HiveResponseBody;
import org.elastos.hive.vault.payment.PaymentController;
import org.elastos.hive.network.response.VaultCreateResponseBody;
import org.elastos.hive.network.response.VaultInfoResponseBody;
import org.elastos.hive.vault.payment.PricingPlan;

import java.io.IOException;
import java.util.List;

public class SubscriptionServiceRender {
	private ServiceEndpoint serviceEndpoint;
	private PaymentController paymentController;

    public SubscriptionServiceRender(ServiceEndpoint serviceEndpoint) {
        this.serviceEndpoint = serviceEndpoint;
        this.paymentController = new PaymentController(serviceEndpoint);

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
        return paymentController.getPricingPlanList();
    }

    public PricingPlan getPricingPlan(String planName) throws IOException {
        return paymentController.getPricingPlan(planName);
    }

    public List<PricingPlan> getBackupPlanList() throws IOException {
        return paymentController.getBackupPlanList();
    }

    public PricingPlan getBackupPlan(String planName) throws IOException {
        return paymentController.getPricingPlan(planName);
    }
}
