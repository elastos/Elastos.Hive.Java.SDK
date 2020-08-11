package org.elastos.hive.vendor.vault.network;


import org.elastos.hive.vendor.vault.network.model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface VaultAuthApi {

    @FormUrlEncoded
    @POST(ConnectConstance.TOKEN)
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Call<TokenResponse> getToken(@Field(ConnectConstance.CODE) String code,
                                 @Field(ConnectConstance.CLIENT_ID) String clientId,
                                 @Field(ConnectConstance.CLIENT_SCERET) String clientSecret,
                                 @Field(ConnectConstance.REDIRECT_URI) String redirectUrl,
                                 @Field(ConnectConstance.GRANT_TYPE) String grantType);

    @FormUrlEncoded
    @POST(ConnectConstance.TOKEN)
    @Headers("Content-Type:application/x-www-form-urlencoded; charset=utf-8")
    Call<TokenResponse> refreshToken(@Field(ConnectConstance.CLIENT_ID) String clientId,
                                     @Field(ConnectConstance.REDIRECT_URI) String redirectUrl,
                                     @Field(ConnectConstance.REFRESH_TOKEN) String refreshToken,
                                     @Field(ConnectConstance.GRANT_TYPE) String grantType);
}
