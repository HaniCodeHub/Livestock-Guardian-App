package com.example.livestockguardian.network;

import com.example.livestockguardian.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Biometric API client with long timeouts for Render cold starts and ONNX inference.
 */
public final class BiometricRetrofitClient {

    private static Retrofit biometricRetrofit;

    private BiometricRetrofitClient() {
    }

    public static BiometricApiService getApi() {
        if (biometricRetrofit == null) {
            biometricRetrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BIOMETRIC_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(buildClient())
                    .build();
        }
        return biometricRetrofit.create(BiometricApiService.class);
    }

    private static OkHttpClient buildClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        return new OkHttpClient.Builder()
                .connectTimeout(90, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .callTimeout(240, TimeUnit.SECONDS)
                .addInterceptor(new RetryOnFailureInterceptor(2))
                .addInterceptor(logging)
                .build();
    }

    private static final class RetryOnFailureInterceptor implements Interceptor {
        private final int maxAttempts;

        RetryOnFailureInterceptor(int maxAttempts) {
            this.maxAttempts = maxAttempts;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            IOException lastError = null;
            Request request = chain.request();

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                try {
                    return chain.proceed(request);
                } catch (IOException e) {
                    lastError = e;
                    if (attempt < maxAttempts) {
                        try {
                            Thread.sleep(2500L);
                        } catch (InterruptedException interrupted) {
                            Thread.currentThread().interrupt();
                            throw e;
                        }
                    }
                }
            }
            throw lastError;
        }
    }
}
