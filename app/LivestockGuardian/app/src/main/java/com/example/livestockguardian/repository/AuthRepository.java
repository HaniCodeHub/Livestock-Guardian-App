package com.example.livestockguardian.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.livestockguardian.Constants;
import com.example.livestockguardian.models.LoginResult;
import com.example.livestockguardian.network.RetrofitClient;
import com.example.livestockguardian.network.SupabaseApiService;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final SupabaseApiService supabaseApi;

    public AuthRepository() {
        supabaseApi = RetrofitClient.getSupabaseApiService();
    }

    public void login(String phone, String password, MutableLiveData<LoginResult> result) {
        String authHeader = "Bearer " + Constants.SUPABASE_KEY;
        supabaseApi.getUsers(
                Constants.SUPABASE_KEY,
                authHeader,
                "eq." + phone,
                "eq." + password,
                "id,full_name,name,email,phone,role"
        ).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    result.postValue(LoginResult.failure("Login failed. Please try again."));
                    return;
                }

                try {
                    String body = response.body().string();
                    JSONArray users = new JSONArray(body);
                    if (users.length() == 0) {
                        result.postValue(LoginResult.failure("Invalid phone or password"));
                        return;
                    }

                    JSONObject user = users.getJSONObject(0);
                    String userId = user.getString("id");
                    String name = user.optString("full_name", user.optString("name", "Farmer"));
                    String email = user.optString("email", "");
                    String userPhone = user.optString("phone", phone);
                    String role = user.optString("role", "farmer");
                    result.postValue(LoginResult.success(userId, name, email, userPhone, role));
                } catch (Exception e) {
                    result.postValue(LoginResult.failure("Could not read login response"));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.postValue(LoginResult.failure("Network error: " + t.getMessage()));
            }
        });
    }

    public LiveData<String> signup(RequestBody body) {
        MutableLiveData<String> result = new MutableLiveData<>();
        
        supabaseApi.registerUser(Constants.SUPABASE_KEY, "Bearer " + Constants.SUPABASE_KEY, "return=representation", body)
                .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    result.setValue("Success");
                } else {
                    result.setValue("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                result.setValue("Signup Failure: " + t.getMessage());
            }
        });
        
        return result;
    }
}
