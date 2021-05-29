package org.elastos.hive.auth.controller;

import com.google.gson.annotations.SerializedName;

class SigninRequest {
	@SerializedName("document")
    private Object didDocument;

    SigninRequest(Object didDocument) {
        this.didDocument = didDocument;
    }
}
