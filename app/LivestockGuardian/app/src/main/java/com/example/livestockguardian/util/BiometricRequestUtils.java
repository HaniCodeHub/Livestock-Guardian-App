package com.example.livestockguardian.util;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public final class BiometricRequestUtils {

    private static final MediaType TEXT_PLAIN = MediaType.parse("text/plain; charset=utf-8");

    private BiometricRequestUtils() {
    }

    public static MultipartBody.Part imagePart(byte[] imageBytes) {
        return MultipartBody.Part.createFormData(
                "file",
                "muzzle.jpg",
                RequestBody.create(imageBytes, MediaType.parse("image/jpeg"))
        );
    }

    public static RequestBody recordsJsonBody(String recordsJson) {
        String payload = normalizeRecordsJson(recordsJson);
        return RequestBody.create(payload, TEXT_PLAIN);
    }

    /** Supabase rows -> format expected by FastAPI match/duplicate endpoints. */
    public static String normalizeRecordsJson(String recordsJson) {
        if (recordsJson == null || recordsJson.trim().isEmpty()) {
            return "[]";
        }
        try {
            JSONArray input = new JSONArray(recordsJson.trim());
            JSONArray output = new JSONArray();
            for (int i = 0; i < input.length(); i++) {
                JSONObject row = input.getJSONObject(i);
                JSONObject normalized = new JSONObject();
                if (row.has("livestock_id")) {
                    normalized.put("livestock_id", row.getString("livestock_id"));
                }
                if (row.has("embedding")) {
                    normalized.put("embedding", row.get("embedding"));
                } else if (row.has("muzzle_embedding")) {
                    normalized.put("embedding", row.get("muzzle_embedding"));
                }
                if (normalized.length() > 0) {
                    output.put(normalized);
                }
            }
            return output.toString();
        } catch (Exception e) {
            return "[]";
        }
    }

    public static String networkErrorMessage(Throwable t) {
        if (t == null) {
            return "Biometric server unreachable";
        }
        String msg = t.getMessage();
        if (msg == null || msg.isEmpty()) {
            return "Biometric server unreachable: " + t.getClass().getSimpleName();
        }
        if (msg.toLowerCase().contains("timeout") || msg.toLowerCase().contains("timed out")) {
            return "Biometric server is waking up. Please wait and try again.";
        }
        return "Biometric server unreachable: " + msg;
    }
}
