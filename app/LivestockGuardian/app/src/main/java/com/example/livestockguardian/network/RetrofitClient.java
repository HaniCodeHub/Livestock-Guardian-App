package com.example.livestockguardian.network;

import com.example.livestockguardian.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit biometricRetrofit = null;
    private static Retrofit supabaseRetrofit = null;

    private static OkHttpClient getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
    }

    public static BiometricApiService getBiometricApiService() {
        if (biometricRetrofit == null) {
            biometricRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BIOMETRIC_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
        return biometricRetrofit.create(BiometricApiService.class);
    }

    public static SupabaseApiService getSupabaseApiService() {
        if (supabaseRetrofit == null) {
            supabaseRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.SUPABASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
        return supabaseRetrofit.create(SupabaseApiService.class);
    }

    public static AuthApiService getAuthApiService() {
        if (supabaseRetrofit == null) {
            supabaseRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.SUPABASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getClient())
                    .build();
        }
        return supabaseRetrofit.create(AuthApiService.class);
    }
}
