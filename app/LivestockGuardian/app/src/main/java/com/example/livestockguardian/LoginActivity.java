package com.example.livestockguardian;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.livestockguardian.models.LoginResult;
import com.example.livestockguardian.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etPhone, etPassword;
    private MaterialButton btnLogin, btnRegisterLink;
    private ProgressBar progressBar;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegisterLink = findViewById(R.id.btnRegisterLink);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> performLogin());

        btnRegisterLink.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        viewModel.getLoginResult().observe(this, this::handleLoginResult);
    }

    private void performLogin() {
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter phone and password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        viewModel.login(phone, password);
    }

    private void handleLoginResult(LoginResult result) {
        if (result == null) {
            return;
        }

        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);

        if (result.isSuccess()) {
            saveUserSession(
                    result.getUserId(),
                    result.getUserName(),
                    result.getEmail(),
                    result.getPhone(),
                    result.getRole()
            );
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            String message = result.getErrorMessage() != null ? result.getErrorMessage() : "Login failed";
            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserSession(String userId, String name, String email, String phone, String role) {
        SharedPreferences sharedPreferences = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.putString("userId", userId);
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.putString("userPhone", phone);
        editor.putString("userRole", role);
        editor.apply();
    }
}
