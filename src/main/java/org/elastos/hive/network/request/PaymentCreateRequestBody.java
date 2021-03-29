package org.elastos.hive.network.request;

import com.google.gson.annotations.SerializedName;

public class PaymentCreateRequestBody {
    @SerializedName("pricing_name")
    private final String pricingName;
    @SerializedName("backing_name")
    private final String backupName;

    public PaymentCreateRequestBody(String pricingPlanName, String backupPlanName) {
        this.pricingName = pricingPlanName;
        this.backupName = backupPlanName;
    }
}
