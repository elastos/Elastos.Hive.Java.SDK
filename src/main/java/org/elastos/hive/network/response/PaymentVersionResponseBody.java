package org.elastos.hive.network.response;

import org.elastos.hive.connection.HiveResponseBody;

public class PaymentVersionResponseBody extends HiveResponseBody {
    private String version;

    public String getVersion() {
        return this.version;
    }
}
