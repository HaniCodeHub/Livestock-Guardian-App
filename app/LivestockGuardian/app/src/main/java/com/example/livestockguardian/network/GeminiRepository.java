package com.example.livestockguardian.network;

import com.example.livestockguardian.BuildConfig;
import com.example.livestockguardian.ai.AiToolType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiRepository {

    private static final String BASE_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String MODEL = "gemini-flash-latest";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public String analyzeImage(String toolType, String base64Jpeg, String mimeType) throws IOException {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IOException("Gemini API key missing. Add GEMINI_API_KEY to local.properties and rebuild.");
        }

        String requestBody;
        try {
            requestBody = buildRequestBody(toolType, base64Jpeg, mimeType);
        } catch (JSONException e) {
            throw new IOException("Failed to prepare Gemini request", e);
        }

        String url = BASE_URL + MODEL + ":generateContent";
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-goog-api-key", apiKey.trim())
                .post(RequestBody.create(requestBody, JSON))
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                throw new IOException("Gemini error " + response.code() + ": " + shorten(responseBody));
            }
            return parseGeminiText(responseBody);
        }
    }

    private static String buildRequestBody(String toolType, String base64Jpeg, String mimeType)
            throws JSONException {
        JSONObject inlineData = new JSONObject();
        inlineData.put("mime_type", mimeType);
        inlineData.put("data", base64Jpeg);

        JSONObject imagePart = new JSONObject();
        imagePart.put("inline_data", inlineData);

        JSONObject textPart = new JSONObject();
        textPart.put("text", promptForTool(toolType));

        JSONArray parts = new JSONArray();
        parts.put(textPart);
        parts.put(imagePart);

        JSONObject content = new JSONObject();
        content.put("parts", parts);

        JSONArray contents = new JSONArray();
        contents.put(content);

        JSONObject body = new JSONObject();
        body.put("contents", contents);
        return body.toString();
    }

    private static String parseGeminiText(String responseBody) throws IOException {
        try {
            JSONObject root = new JSONObject(responseBody);
            if (root.has("error")) {
                JSONObject error = root.getJSONObject("error");
                throw new IOException(error.optString("message", "Gemini API error"));
            }
            JSONArray candidates = root.getJSONArray("candidates");
            if (candidates.length() == 0) {
                throw new IOException("Gemini returned no candidates");
            }
            JSONObject first = candidates.getJSONObject(0);
            if (first.has("content")) {
                JSONObject content = first.getJSONObject("content");
                JSONArray parts = content.getJSONArray("parts");
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < parts.length(); i++) {
                    JSONObject part = parts.getJSONObject(i);
                    if (part.has("text")) {
                        builder.append(part.getString("text"));
                    }
                }
                String text = builder.toString().trim();
                if (!text.isEmpty()) {
                    return text;
                }
            }
            throw new IOException("Gemini returned empty analysis");
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Could not parse Gemini response", e);
        }
    }

    private static String shorten(String text) {
        if (text == null) {
            return "";
        }
        return text.length() > 300 ? text.substring(0, 300) + "..." : text;
    }

    private static String promptForTool(String toolType) {
        switch (toolType) {
            case AiToolType.TOOL_BREED:
                return "You are a livestock expert. Analyze this animal photo. "
                        + "Return: likely species, breed or cross-breed guess, visible markings, "
                        + "and confidence notes. Use short bullet points.";
            case AiToolType.TOOL_HEALTH:
                return "You are a veterinary assistant for farmers. From this livestock photo, "
                        + "describe visible health indicators, body condition score (BCS), and any warning signs. "
                        + "This is educational only, not a medical diagnosis. Use short bullet points.";
            case AiToolType.TOOL_WEIGHT:
                return "Estimate this animal's weight in kilograms from the photo. "
                        + "Give a likely range, assumptions, and limitations. Use short bullet points.";
            case AiToolType.TOOL_MARKET:
                return "Estimate an indicative market price in Pakistani Rupees (PKR) for this animal "
                        + "based on visible breed, size, and condition. State assumptions clearly. "
                        + "Use short bullet points.";
            default:
                return "Describe this livestock animal in useful detail for a farmer. Use short bullet points.";
        }
    }
}
