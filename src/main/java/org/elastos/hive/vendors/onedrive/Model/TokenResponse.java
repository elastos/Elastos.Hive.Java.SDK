package org.elastos.hive.vendors.onedrive.Model;

/*
{
	"token_type": "Bearer",
	"scope": "Files.ReadWrite",
	"expires_in": 3600,
	"ext_expires_in": 3600,
	"access_token": "EwBQA8l6BAAURSN/FHlDW5xN74t6GzbtsBBeBUYAAWnLLA8xDUcS6ud3wgaUgrhTWyh/1ISMrbky6Rz0gSybpXT/jsf3LL8giaJEXexcpHVNmmgQWTp2UbhAOOcHBB8jJZFLT0bMQXtZaNd9g6W5HqhFHekJLFU/Rti/4/H7axWNaiXipPNFhezgfwD7bsJcRmaxsqPrNeiYUtsC/4oy4faEBgnX34euZcW1Imu02q/pf77HHCaAxbQ2cmBpXGzFprySOdVkw6H24bft//5j9BtzeHNL7T8pwoKkqMv4MsMhy7ni7lPlVp9BNh04x+U7i+IsFg0cTRzcuiZ2RH5A+x+dexPG4Iyvu0OY4zBqbwSjUeGGCpN9JHOUMYJEfsADZgAACCuahdtM/a6SIAL0Ib3EUSzQIzuk2fAnr3mIqDbEYXVYlMCgZKOFPaZ/+JDrg2en1DYlaxNPmg6oHEW077IAwqzKJg6DEopz+6TcGAEm8oAY4vXSpszycDuCXlZofMGJFG0OlrA8SmT6wH6gZ+SPoJyjl01V+q0GH/lD9N0aVHvyLqnMBUTv0r9VzEwCM0oZvabDencSnNnYdfnyGB9//iiCsBir5xGF+2f2ZHKi2CpqaoP6nHhyZMZRBACWRTl7wdqCekTEhOHoIrQWjlRl4fw9181U37E6UKuYS3P1BEF+dFpEGrndAJghnA7TrFiWSfezIewjRBnTdL5r+BfxdMLE1fAh+sAxcz55cp/H/lCxsQcrhQyL/rVzqWzX1Y16RZZkuQlqwf56x5d6sR/9pYGuD8m0K/LxFcz9jUZsFkM8ZHLykySaDOQJay7qIaGkKCpfPHCO5mlS9qHtSjihnK8WanvAN9pykY+6N727+xEWKax1GQbXBeqxjB+udkuI3hlX/f7CLLdTet2jmsdsTg0T5UepJ1TiWV4M/exmMY1JbuGEk09gpuO0KQ4j6YhvUiRW4Q8kqS3kEu0GYWFfPpupZo97xQYIEfBaXnYUS022bhVyObIfoZUEIpGiHTzxGv9ZYgQBRYspiS+Mjn/iSU6BmaaWoXl3Sesg6fN7SXG9cB4bS+AxYGbwq6cCRWIeQc6Kj9apgC3uR9r6FYb6P5PKW2qe/DftrbNWYwI=",
	"refresh_token": "MCS!zeseEawU!bKv*qYQn*qpVNllT*KwrcrtU7Irchm9rNmxv5KIBY*21NmgWDFHgohklYxHhgcZLIRFHd76!OevUNY8afrVNuMXTxQH97g*LI35EsVbrAP2LiSoiS5Cmzpd*YJIcTrt0sH42o1Gq!b2IvBgRKCpoMqNksga0Ya4BhRYeHCWTu4nSpsmxvvsBeUWO4lIVCAXSTCkTbbbzCO5Wn6vaolewKEtkqZrdVAzpLs4VTOtLPN5d6027nAs905p6qsrvGysUmRTfek79Hs4GAwTVqwDrjgNnbPlM*aAZ7Flb5InV1cYeAItZJIvPek8nkSLLYXOHVq4fRqvAYYyqTGKOlMRKCrbvSRMv!7BrJ6DP!*uoeDZFv0*6rmJF8A$$"
}
*/
public class TokenResponse {
    private String token_type ;
    private String scope ;
    private int expires_in ;
    private int ext_expires_in ;
    private String access_token ;
    private String refresh_token ;

    public TokenResponse(String token_type, String scope, int expires_in,
            int ext_expires_in, String access_token, String refresh_token) {
        this.token_type = token_type;
        this.scope = scope;
        this.expires_in = expires_in;
        this.ext_expires_in = ext_expires_in;
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public int getExt_expires_in() {
        return ext_expires_in;
    }

    public void setExt_expires_in(int ext_expires_in) {
        this.ext_expires_in = ext_expires_in;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    @Override
    public String toString() {
        return "TokenResponse{" +
                "token_type='" + token_type + '\'' +
                ", scope='" + scope + '\'' +
                ", expires_in=" + expires_in +
                ", ext_expires_in=" + ext_expires_in +
                ", access_token='" + access_token + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }
}
