package org.elastos.hive.endpoint;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class UsersInfo {
    @SerializedName("users")
    private List<UserDetail> users;

    List<UserDetail> getUsers() {
        return users;
    }
}
