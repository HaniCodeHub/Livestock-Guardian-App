package com.example.livestockguardian.network;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface BiometricApiService {

    @GET("health")
    Call<ResponseBody> health();
    @Multipart
    @POST("biometric/check-duplicate")
    Call<ResponseBody> checkDuplicate(
        @Header("X-API-Key") String apiKey,
        @Part MultipartBody.Part file,
        @Part("records_json") RequestBody recordsJson
    );

    @Multipart
    @POST("biometric/register-muzzle")
    Call<ResponseBody> registerMuzzle(
        @Header("X-API-Key") String apiKey,
        @Part MultipartBody.Part file
    );

    @Multipart
    @POST("biometric/match-muzzle")
    Call<ResponseBody> matchMuzzle(
        @Header("X-API-Key") String apiKey,
        @Part MultipartBody.Part file,
        @Part("records_json") RequestBody recordsJson
    );
}
