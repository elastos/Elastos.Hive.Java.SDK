package org.elastos.hive.vault;

import org.elastos.hive.ServiceEndpoint;
import org.elastos.hive.network.response.HiveResponseBody;

import java.io.IOException;

public class NodeManageServiceRender extends HiveVaultRender {
    public NodeManageServiceRender(ServiceEndpoint serviceEndpoint) {
        super(serviceEndpoint);
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
