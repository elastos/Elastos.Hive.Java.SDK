package org.elastos.hive.network.response;

import com.google.gson.annotations.SerializedName;
import org.elastos.hive.connection.HiveResponseBody;

public class AuthResponseBody extends HiveResponseBody {
    @SerializedName("access_token")
    private String token;

    public String getToken() {
        return this.token;
    }
}
