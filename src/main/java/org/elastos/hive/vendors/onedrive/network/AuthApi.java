package org.elastos.hive.vendors.onedrive.network;


import org.elastos.hive.vendors.onedrive.OneDriveConstance;
import org.elastos.hive.vendors.onedrive.network.Model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApi {
    @FormUrlEncoded
    @POST(OneDriveConstance.TOKEN)
    Call<TokenResponse> getToken(@Field(OneDriveConstance.CLIENT_ID) String clientId,
                                 @Field(OneDriveConstance.CODE) String code,
                                 @Field(OneDriveConstance.REDIRECT_URL) String redirectUrl,
                                 @Field(OneDriveConstance.GRANT_TYPE) String grantType);

    @FormUrlEncoded
    @POST(OneDriveConstance.TOKEN)
    Call<TokenResponse> refreshToken(@Field(OneDriveConstance.CLIENT_ID) String clientId,
                                     @Field(OneDriveConstance.REDIRECT_URL)String redirectUrl,
                                     @Field(OneDriveConstance.REFRESH_TOKEN)String refreshToken,
                                     @Field(OneDriveConstance.GRANT_TYPE)String grantType);


    @GET(OneDriveConstance.LOGOUT)
    Call<String> logout(@Query(OneDriveConstance.LOGOUT_REDIRECT_URL) String redirectUrl);

}
