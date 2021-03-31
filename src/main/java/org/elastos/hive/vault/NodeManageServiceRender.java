package org.elastos.hive.vault;

import org.elastos.hive.Vault;
import org.elastos.hive.network.response.HiveResponseBody;

import java.io.IOException;

public class NodeManageServiceRender extends HiveVaultRender {
    public NodeManageServiceRender(Vault vault) {
        super(vault);
    }

    public String getVersion() throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getNodeManagerApi()
                        .version()
                        .execute()
                        .body()).getVersion();
    }

    public String getCommitHash() throws IOException {
        return HiveResponseBody.validateBody(
                getConnectionManager().getNodeManagerApi()
                        .commitHash()
                        .execute()
                        .body()).getCommitHash();
    }
}
