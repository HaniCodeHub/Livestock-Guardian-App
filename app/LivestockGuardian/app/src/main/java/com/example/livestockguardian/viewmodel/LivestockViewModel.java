package com.example.livestockguardian.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.livestockguardian.models.Livestock;
import com.example.livestockguardian.repository.LivestockRepository;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LivestockViewModel extends ViewModel {
    private final LivestockRepository repository;

    public LivestockViewModel() {
        this.repository = new LivestockRepository();
    }

    public void warmUpBiometricServer(Callback<ResponseBody> callback) {
        repository.warmUpBiometricServer(callback);
    }

    public void checkDuplicate(byte[] imageBytes, String recordsJson, Callback<ResponseBody> callback) {
        repository.checkDuplicate(imageBytes, recordsJson, callback);
    }

    public void enrollMuzzle(byte[] imageBytes, Callback<ResponseBody> callback) {
        repository.enrollMuzzle(imageBytes, callback);
    }

    public void saveLivestock(RequestBody body, Callback<ResponseBody> callback) {
        repository.saveLivestock(body, callback);
    }

    public void saveEmbedding(RequestBody body, Callback<ResponseBody> callback) {
        repository.saveEmbedding(body, callback);
    }

    public void saveLivestockImage(RequestBody body, Callback<ResponseBody> callback) {
        repository.saveLivestockImage(body, callback);
    }

    public void matchMuzzle(byte[] imageBytes, String recordsJson, Callback<ResponseBody> callback) {
        repository.matchMuzzle(imageBytes, recordsJson, callback);
    }

    public void reportTheft(String id, Map<String, Object> updates, Callback<ResponseBody> callback) {
        repository.reportTheft(id, updates, callback);
    }
    
    public void fetchAllEmbeddings(Callback<ResponseBody> callback) {
        repository.fetchAllEmbeddings(callback);
    }

    public LiveData<List<Livestock>> getMyLivestock(String userId) {
        MutableLiveData<List<Livestock>> data = new MutableLiveData<>();
        repository.getMyLivestock(userId, new Callback<List<Livestock>>() {
            @Override
            public void onResponse(Call<List<Livestock>> call, Response<List<Livestock>> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                } else {
                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Livestock>> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public void getLivestockDetails(String livestockId, Callback<ResponseBody> callback) {
        repository.getLivestockDetails(livestockId, callback);
    }
}
