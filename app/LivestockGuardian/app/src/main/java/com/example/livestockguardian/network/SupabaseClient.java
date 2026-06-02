package com.example.livestockguardian.network;

import com.example.livestockguardian.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

public class SupabaseClient {
    private static OkHttpClient client = null;

    public static OkHttpClient getClient() {
        if (client == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("apikey", Constants.SUPABASE_KEY)
                                .header("Authorization", "Bearer " + Constants.SUPABASE_KEY)
                                .build();
                        return chain.proceed(request);
                    })
                    .build();
        }
        return client;
    }
}
