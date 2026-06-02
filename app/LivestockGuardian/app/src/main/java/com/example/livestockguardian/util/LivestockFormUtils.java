package com.example.livestockguardian.util;

import org.json.JSONObject;

public final class LivestockFormUtils {

    private LivestockFormUtils() {
    }

    /** Values allowed by typical livestock.status CHECK constraints in Supabase. */
    public static String normalizeStatus(String status) {
        if (status == null) {
            return "healthy";
        }
        String value = status.trim().toLowerCase();
        if ("active".equals(value)) {
            return "healthy";
        }
        switch (value) {
            case "healthy":
            case "sick":
            case "injured":
            case "dead":
            case "stolen":
            case "sold":
                return value;
            default:
                return "healthy";
        }
    }

    public static String normalizeGender(String gender) {
        if (gender == null) {
            return "male";
        }
        return gender.trim().toLowerCase();
    }

    public static void putIfNotEmpty(JSONObject json, String key, String value) throws Exception {
        if (value != null && !value.trim().isEmpty()) {
            json.put(key, value.trim());
        }
    }
}
