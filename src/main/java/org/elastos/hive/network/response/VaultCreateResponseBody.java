package org.elastos.hive.network.response;

import org.elastos.hive.connection.HiveResponseBody;

public class VaultCreateResponseBody extends HiveResponseBody {
    private Boolean existing;

    public Boolean getExisting() {
        return this.existing;
    }
}
