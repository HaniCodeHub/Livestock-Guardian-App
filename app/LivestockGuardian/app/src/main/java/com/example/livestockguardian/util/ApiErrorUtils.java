package com.example.livestockguardian.util;

import okhttp3.ResponseBody;
import retrofit2.Response;

public final class ApiErrorUtils {

    private ApiErrorUtils() {
    }

    public static String message(Response<?> response, String fallback) {
        if (response == null) {
            return fallback;
        }
        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                String body = errorBody.string().trim();
                if (!body.isEmpty()) {
                    return fallback + " (" + response.code() + "): " + body;
                }
            }
        } catch (Exception ignored) {
        }
        return fallback + " (" + response.code() + ")";
    }
}
