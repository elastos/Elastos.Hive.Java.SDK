package org.elastos.hive.endpoint;

import java.util.List;

public class DeleteBackupsParams extends DeleteVaultsParams {
    public DeleteBackupsParams(List<String> userDids) {
        super(userDids);
    }
}
