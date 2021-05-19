package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.exception.VaultAlreadyExistException;
import org.elastos.hive.vault.payment.PaymentController;
import org.elastos.hive.vault.payment.PricingPlan;
import org.elastos.hive.vault.subscription.SubscriptionController;
import org.elastos.hive.vault.subscription.VaultInfoResponseBody;

import java.io.IOException;
import java.util.List;

public class SubscriptionServiceRender {
	private PaymentController paymentController;
	private SubscriptionController subscriptionController;

    public SubscriptionServiceRender(ServiceEndpoint serviceEndpoint) {
        paymentController = new PaymentController(serviceEndpoint);
        subscriptionController = new SubscriptionController(serviceEndpoint);
    }

    public void subscribe() throws IOException {
        if (Boolean.TRUE.equals(subscriptionController.subscribe())) {
            throw new VaultAlreadyExistException("The vault already exists");
        }
    }

    public void subscribeBackup() throws IOException {
        if (Boolean.TRUE.equals(subscriptionController.subscribeBackup())) {
            throw new VaultAlreadyExistException("The backup vault already exists");
        }
    }

    public void unsubscribe() throws IOException {
        subscriptionController.unsubscribe();
    }

    public void activate() throws IOException {
        subscriptionController.activate();
    }

    public void deactivate() throws IOException {
        subscriptionController.deactivate();
    }

    public VaultInfoResponseBody getVaultInfo() throws IOException {
        return subscriptionController.getVaultInfo();
    }

    public VaultInfoResponseBody getBackupVaultInfo() throws IOException {
        return subscriptionController.getBackupVaultInfo();
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
