package org.elastos.hive.vault.database;

import com.google.gson.annotations.SerializedName;

public class Collection {
    private String name;

    @SerializedName("is_encrypt")
    private Boolean isEncrypt;

    @SerializedName("encrypt_method")
    private String encryptMethod;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEncrypt() {
        return isEncrypt;
    }

    public void setEncrypt(Boolean encrypt) {
        isEncrypt = encrypt;
    }

    public String getEncryptMethod() {
        return encryptMethod;
    }

    public void setEncryptMethod(String encryptMethod) {
        this.encryptMethod = encryptMethod;
    }
}
