package com.example.livestockguardian.ai;

public final class AiToolType {
    public static final String EXTRA_TOOL = "tool_type";
    public static final String TOOL_BREED = "breed";
    public static final String TOOL_HEALTH = "health";
    public static final String TOOL_WEIGHT = "weight";
    public static final String TOOL_MARKET = "market";

    private AiToolType() {
    }

    public static String title(String toolType) {
        switch (toolType) {
            case TOOL_BREED:
                return "Breed Analysis";
            case TOOL_HEALTH:
                return "Health & BCD Diagnosis";
            case TOOL_WEIGHT:
                return "Weight Estimation";
            case TOOL_MARKET:
                return "Market Price Estimator";
            default:
                return "AI Analysis";
        }
    }
}
