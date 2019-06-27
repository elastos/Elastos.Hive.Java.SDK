package org.elastos.hive.vendors.onedrive.network;


import org.elastos.hive.vendors.onedrive.Constance;
import org.elastos.hive.vendors.onedrive.Model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {
    @FormUrlEncoded
    @POST(Constance.TOKEN)
    Call<TokenResponse> getToken(@Field(Constance.CLIENT_ID) String clientId,
                                 @Field(Constance.CODE) String code,
                                 @Field(Constance.REDIRECT_URL) String redirectUrl,
                                 @Field(Constance.GRANT_TYPE) String grantType);

    @FormUrlEncoded
    @POST(Constance.TOKEN)
    Call<TokenResponse> refreshToken(@Field(Constance.CLIENT_ID) String clientId,
                                     @Field(Constance.REDIRECT_URL)String redirectUrl,
                                     @Field(Constance.REFRESH_TOKEN)String refreshToken,
                                     @Field(Constance.GRANT_TYPE)String grantType);


    @GET(Constance.LOGOUT)
    Call<String> logout(@Query(Constance.LOGOUT_REDIRECT_URL) String redirectUrl);

}
