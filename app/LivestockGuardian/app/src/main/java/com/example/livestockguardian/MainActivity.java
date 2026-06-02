package com.example.livestockguardian;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.livestockguardian.models.Livestock;
import com.example.livestockguardian.util.NavigationHelper;
import com.example.livestockguardian.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcomeName, tvTotalAnimals, tvCattleCount, tvSheepCount, tvHorsesCount, tvAlertCount;
    private View btnQuickRegister, btnQuickScan, btnQuickAI;
    private BottomNavigationView bottomNavigation;
    private MainViewModel viewModel;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Initialize UI components
        tvWelcomeName = findViewById(R.id.tvWelcomeName);
        tvTotalAnimals = findViewById(R.id.tvTotalAnimals);
        tvCattleCount = findViewById(R.id.tvCattleCount);
        tvSheepCount = findViewById(R.id.tvSheepCount);
        tvHorsesCount = findViewById(R.id.tvHorsesCount);
        tvAlertCount = findViewById(R.id.tvAlertCount);
        
        btnQuickRegister = findViewById(R.id.btnQuickRegister);
        btnQuickScan = findViewById(R.id.btnQuickScan);
        btnQuickAI = findViewById(R.id.btnQuickAI);
        
        bottomNavigation = findViewById(R.id.bottomNavigation);

        // Get user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        userName = prefs.getString("userName", "Farmer");

        Log.d("DASHBOARD_DEBUG", "Retrieved User ID from SharedPreferences: " + userId);

        if (userId.isEmpty()) {
            performLogout();
            return;
        }

        if (tvWelcomeName != null) {
            tvWelcomeName.setText(userName);
        }

        // Quick Actions
        if (btnQuickRegister != null) {
            btnQuickRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterLivestockActivity.class)));
        }
        if (btnQuickScan != null) {
            btnQuickScan.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ScanActivity.class)));
        }
        if (btnQuickAI != null) {
            btnQuickAI.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AiToolsActivity.class)));
        }

        findViewById(R.id.ivToolbarProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        NavigationHelper.setupBottomNavigation(this, R.id.nav_home);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getMyLivestock(userId).observe(this, list -> {
            if (list != null) {
                updateDashboardStats(list);
            } else {
                Toast.makeText(this, "Failed to load livestock", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateDashboardStats(List<Livestock> list) {
        int total = list.size();
        int cattle = 0;
        int sheep = 0;
        int horses = 0;

        for (Livestock l : list) {
            if (l.getSpecies() == null) {
                continue;
            }
            String species = l.getSpecies().toLowerCase();
            if (species.contains("cow") || species.contains("buffalo") || species.contains("cattle")) {
                cattle++;
            } else if (species.contains("sheep") || species.contains("goat")) {
                sheep++;
            } else if (species.contains("horse")) {
                horses++;
            }
        }

        if (tvTotalAnimals != null) tvTotalAnimals.setText(String.format(Locale.US, "%,d", total));
        if (tvCattleCount != null) tvCattleCount.setText(String.valueOf(cattle));
        if (tvSheepCount != null) tvSheepCount.setText(String.valueOf(sheep));
        if (tvHorsesCount != null) tvHorsesCount.setText(String.valueOf(horses));
        
        // Simulating alert count for design consistency
        if (tvAlertCount != null) tvAlertCount.setText("2");
    }

    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}
