/*
 * Copyright (c) 2019 Elastos Foundation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
