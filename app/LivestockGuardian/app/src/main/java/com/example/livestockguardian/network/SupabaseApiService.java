package com.example.livestockguardian.network;

import com.example.livestockguardian.models.Livestock;
import java.util.List;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseApiService {
    @GET("rest/v1/livestock")
    Call<List<Livestock>> getLivestock(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Query("owner_id") String ownerId
    );

    @POST("rest/v1/livestock")
    Call<ResponseBody> registerLivestock(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Header("Prefer") String prefer,
        @Body RequestBody body
    );

    @POST("rest/v1/users")
    Call<ResponseBody> registerUser(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Header("Prefer") String prefer,
        @Body RequestBody body
    );

    @GET("rest/v1/users")
    Call<ResponseBody> getUsers(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Query("phone") String phone,
        @Query("password") String password,
        @Query("select") String select
    );

    @GET("rest/v1/embeddings")
    Call<ResponseBody> getEmbeddings(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Query("select") String select
    );

    @POST("rest/v1/embeddings")
    Call<ResponseBody> saveEmbedding(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Body RequestBody body
    );

    @PATCH("rest/v1/livestock")
    Call<ResponseBody> updateLivestockStatus(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Query("id") String id,
        @Body Map<String, Object> updates
    );

    @GET("rest/v1/livestock")
    Call<ResponseBody> getLivestockDetails(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Query("id") String id,
        @Query("select") String select
    );

    @POST("rest/v1/livestock_images")
    Call<ResponseBody> saveLivestockImage(
        @Header("apikey") String apiKey,
        @Header("Authorization") String auth,
        @Header("Prefer") String prefer,
        @Body RequestBody body
    );
}
