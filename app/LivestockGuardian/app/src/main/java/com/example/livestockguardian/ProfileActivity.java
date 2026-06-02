package com.example.livestockguardian;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.livestockguardian.util.NavigationHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvProfileFarmerName, tvProfileFarmerPhone;
    private MaterialButton btnEditProfile, btnSettings, btnHelp, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());

            ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        tvProfileFarmerName = findViewById(R.id.tvProfileFarmerName);
        tvProfileFarmerPhone = findViewById(R.id.tvProfileFarmerPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSettings = findViewById(R.id.btnSettings);
        btnHelp = findViewById(R.id.btnHelp);
        btnLogout = findViewById(R.id.btnLogout);

        // Load data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "Farmer");
        String phone = prefs.getString("userPhone", "No Phone");

        if (tvProfileFarmerName != null) tvProfileFarmerName.setText(name);
        if (tvProfileFarmerPhone != null) tvProfileFarmerPhone.setText(phone);

        if (btnLogout != null) btnLogout.setOnClickListener(v -> performLogout());

        if (btnEditProfile != null) btnEditProfile.setOnClickListener(v -> Toast.makeText(this, "Edit Profile coming soon", Toast.LENGTH_SHORT).show());
        if (btnSettings != null) btnSettings.setOnClickListener(v -> Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show());
        if (btnHelp != null) btnHelp.setOnClickListener(v -> Toast.makeText(this, "Help center coming soon", Toast.LENGTH_SHORT).show());

        NavigationHelper.setupBottomNavigation(this, R.id.nav_profile);
    }

    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
