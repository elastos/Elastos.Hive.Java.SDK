package org.elastos.hive.network.response;

public class PaymentPlanResponseBody extends HiveResponseBody {
    private float amount;
    private String currency;
    private int maxStorage;
    private String name;
    private int serviceDays;

    public float getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public int getMaxStorage() {
        return maxStorage;
    }

    public String getName() {
        return name;
    }

    public int getServiceDays() {
        return serviceDays;
    }
}
