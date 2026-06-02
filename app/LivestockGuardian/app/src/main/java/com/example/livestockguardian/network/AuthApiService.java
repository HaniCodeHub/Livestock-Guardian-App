package com.example.livestockguardian.network;

import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthApiService {
    @POST("auth/v1/token")
    Call<ResponseBody> login(
        @Header("apikey") String apiKey,
        @Query("grant_type") String grantType,
        @Body Map<String, String> credentials
    );

    @POST("auth/v1/signup")
    Call<ResponseBody> signup(
        @Header("apikey") String apiKey,
        @Body Map<String, String> data
    );
}
