package org.elastos.hive.vault.files;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.network.response.HiveResponseBody;

public class FilesHashResponseBody extends HiveResponseBody {
    @SerializedName("SHA256")
    private String sha256;

    public String getSha256() {
        return this.sha256;
    }
}
