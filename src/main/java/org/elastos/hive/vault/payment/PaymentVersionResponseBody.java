package org.elastos.hive.vault.payment;

import org.elastos.hive.connection.HiveResponseBody;

class PaymentVersionResponseBody extends HiveResponseBody {
    private String version;

    public String getVersion() {
        return this.version;
    }
}
