package com.example.livestockguardian.repository;

import com.example.livestockguardian.Constants;
import com.example.livestockguardian.models.Livestock;
import com.example.livestockguardian.network.BiometricApiService;
import com.example.livestockguardian.network.BiometricRetrofitClient;
import com.example.livestockguardian.network.RetrofitClient;
import com.example.livestockguardian.util.BiometricRequestUtils;
import com.example.livestockguardian.network.SupabaseApiService;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class LivestockRepository {
    private final BiometricApiService biometricApi;
    private final SupabaseApiService supabaseApi;

    public LivestockRepository() {
        biometricApi = BiometricRetrofitClient.getApi();
        supabaseApi = RetrofitClient.getSupabaseApiService();
    }

    // --- Biometric FastAPI Server ---
    public void warmUpBiometricServer(Callback<ResponseBody> callback) {
        biometricApi.health().enqueue(callback);
    }

    public void checkDuplicate(byte[] imageBytes, String recordsJson, Callback<ResponseBody> callback) {
        String normalized = BiometricRequestUtils.normalizeRecordsJson(recordsJson);
        biometricApi.checkDuplicate(
                Constants.BIOMETRIC_API_KEY,
                BiometricRequestUtils.imagePart(imageBytes),
                BiometricRequestUtils.recordsJsonBody(normalized)
        ).enqueue(callback);
    }

    public void enrollMuzzle(byte[] imageBytes, Callback<ResponseBody> callback) {
        biometricApi.registerMuzzle(
                Constants.BIOMETRIC_API_KEY,
                BiometricRequestUtils.imagePart(imageBytes)
        ).enqueue(callback);
    }

    public void matchMuzzle(byte[] imageBytes, String recordsJson, Callback<ResponseBody> callback) {
        String normalized = BiometricRequestUtils.normalizeRecordsJson(recordsJson);
        biometricApi.matchMuzzle(
                Constants.BIOMETRIC_API_KEY,
                BiometricRequestUtils.imagePart(imageBytes),
                BiometricRequestUtils.recordsJsonBody(normalized)
        ).enqueue(callback);
    }

    // --- Supabase PostgreSQL ---
    public void fetchAllEmbeddings(Callback<ResponseBody> callback) {
        supabaseApi.getEmbeddings(Constants.SUPABASE_KEY, "Bearer " + Constants.SUPABASE_KEY, "livestock_id,embedding,confidence")
                .enqueue(callback);
    }

    public void saveLivestock(RequestBody body, Callback<ResponseBody> callback) {
        supabaseApi.registerLivestock(Constants.SUPABASE_KEY, "Bearer " + Constants.SUPABASE_KEY, "return=representation", body).enqueue(callback);
    }

    public void saveEmbedding(RequestBody body, Callback<ResponseBody> callback) {
        supabaseApi.saveEmbedding(Constants.SUPABASE_KEY, "Bearer " + Constants.SUPABASE_KEY, body).enqueue(callback);
    }

    public void saveLivestockImage(RequestBody body, Callback<ResponseBody> callback) {
        supabaseApi.saveLivestockImage(
                Constants.SUPABASE_KEY,
                "Bearer " + Constants.SUPABASE_KEY,
                "return=minimal",
                body
        ).enqueue(callback);
    }

    public void reportTheft(String id, Map<String, Object> updates, Callback<ResponseBody> callback) {
        supabaseApi.updateLivestockStatus(Constants.SUPABASE_KEY, "Bearer " + Constants.SUPABASE_KEY, id, updates).enqueue(callback);
    }

    public void getMyLivestock(String userId, Callback<List<Livestock>> callback) {
        supabaseApi.getLivestock(Constants.SUPABASE_KEY, "Bearer " + Constants.SUPABASE_KEY, "eq." + userId).enqueue(callback);
    }

    public void getLivestockDetails(String livestockId, Callback<ResponseBody> callback) {
        supabaseApi.getLivestockDetails(Constants.SUPABASE_KEY, "Bearer " + Constants.SUPABASE_KEY, "eq." + livestockId, "*,users:owner_id(full_name)")
                .enqueue(callback);
    }
}
