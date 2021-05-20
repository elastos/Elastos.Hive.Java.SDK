package org.elastos.hive.subscription.payment;

import com.google.gson.annotations.SerializedName;

class PaymentCreateRequestBody {
    @SerializedName("pricing_name")
    private final String pricingName;
    @SerializedName("backing_name")
    private final String backupName;

    public PaymentCreateRequestBody(String pricingPlanName, String backupPlanName) {
        this.pricingName = pricingPlanName;
        this.backupName = backupPlanName;
    }
}
