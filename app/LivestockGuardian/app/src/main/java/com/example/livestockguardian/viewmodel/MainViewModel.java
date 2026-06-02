package com.example.livestockguardian.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.livestockguardian.models.Livestock;
import com.example.livestockguardian.repository.LivestockRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private final LivestockRepository repository;

    public MainViewModel() {
        repository = new LivestockRepository();
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
}
