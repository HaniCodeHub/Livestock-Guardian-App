package com.example.livestockguardian.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.livestockguardian.models.LoginResult;
import com.example.livestockguardian.repository.AuthRepository;

import okhttp3.RequestBody;

public class LoginViewModel extends ViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LoginViewModel() {
        this.repository = new AuthRepository();
    }

    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String phone, String password) {
        repository.login(phone, password, loginResult);
    }

    public LiveData<String> signup(RequestBody body) {
        return repository.signup(body);
    }
}
