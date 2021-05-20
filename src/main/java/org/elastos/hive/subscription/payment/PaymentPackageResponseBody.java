package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

import java.util.List;

class PaymentPackageResponseBody extends HiveResponseBody {
    private PaymentSettings paymentSettings;
    private String version;
    List<PricingPlan> backupPlans;
    List<PricingPlan> pricingPlans;

    public PaymentSettings getPaymentSettings() {
        return paymentSettings;
    }

    public String getVersion() {
        return version;
    }

    public List<PricingPlan> getBackupPlans() {
        return backupPlans;
    }

    public List<PricingPlan> getPricingPlans() {
        return pricingPlans;
    }

    class PaymentSettings {
        private String receivingELAAddress;
        @SerializedName("wait_payment_timeout")
        private String waitPaymentTimeout;
        @SerializedName("wait_tx_timeout")
        private String waitTxTimeout;
    }
}
