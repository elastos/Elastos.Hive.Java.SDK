package org.elastos.hive.vault.scripting;

import org.elastos.hive.connection.HiveResponseBody;

public class VaultCreateResponseBody extends HiveResponseBody {
    private Boolean existing;

    public Boolean getExisting() {
        return this.existing;
    }
}
