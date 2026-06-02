package com.example.livestockguardian;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.livestockguardian.adapters.LivestockAdapter;
import com.example.livestockguardian.models.Livestock;
import com.example.livestockguardian.util.NavigationHelper;
import com.example.livestockguardian.viewmodel.LivestockViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class LivestockListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LivestockAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private EditText etSearch;
    private BottomNavigationView bottomNavigation;
    private LivestockViewModel viewModel;
    private String userId;
    private List<Livestock> fullList = new ArrayList<>();
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_livestock_list);

        viewModel = new ViewModelProvider(this).get(LivestockViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        findViewById(R.id.ivToolbarProfile).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        etSearch = findViewById(R.id.etSearch);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        SharedPreferences prefs = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", "");

        if (userId.isEmpty()) {
            performLogout();
            return;
        }

        setupRecyclerView();
        setupSearch();
        setupFilters();
        NavigationHelper.setupBottomNavigation(this, R.id.nav_livestock);

        swipeRefresh.setOnRefreshListener(this::fetchLivestock);
        fetchLivestock();
    }

    private void setupRecyclerView() {
        adapter = new LivestockAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(livestock -> {
            Intent intent = new Intent(this, AnimalProfileActivity.class);
            intent.putExtra("livestock_id", livestock.getId());
            startActivity(intent);
        });
    }

    private void setupFilters() {
        Chip chipAll = findViewById(R.id.chipAll);
        Chip chipCattle = findViewById(R.id.chipCattle);
        Chip chipSheep = findViewById(R.id.chipSheep);
        Chip chipAlerts = findViewById(R.id.chipAlerts);

        View.OnClickListener filterListener = v -> {
            resetChipColors();
            Chip selected = (Chip) v;
            selected.setChipBackgroundColorResource(R.color.brand_green);
            selected.setTextColor(getColor(R.color.white));
            
            if (v.getId() == R.id.chipAll) currentFilter = "All";
            else if (v.getId() == R.id.chipCattle) currentFilter = "Cattle";
            else if (v.getId() == R.id.chipSheep) currentFilter = "Sheep";
            else if (v.getId() == R.id.chipAlerts) currentFilter = "Alerts";
            
            applyFilters();
        };

        if (chipAll != null) chipAll.setOnClickListener(filterListener);
        if (chipCattle != null) chipCattle.setOnClickListener(filterListener);
        if (chipSheep != null) chipSheep.setOnClickListener(filterListener);
        if (chipAlerts != null) chipAlerts.setOnClickListener(filterListener);
    }

    private void resetChipColors() {
        int[] ids = {R.id.chipAll, R.id.chipCattle, R.id.chipSheep, R.id.chipAlerts};
        for (int id : ids) {
            Chip c = findViewById(id);
            if (c != null) {
                c.setChipBackgroundColorResource(R.color.white);
                c.setTextColor(getColor(R.color.text_grey));
            }
        }
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void applyFilters() {
        String query = etSearch.getText().toString().toLowerCase();
        List<Livestock> filteredList = new ArrayList<>();

        for (Livestock item : fullList) {
            String itemName = item.getName() != null ? item.getName() : "";
            String itemBreed = item.getBreed() != null ? item.getBreed() : "";
            boolean matchesSearch = itemName.toLowerCase().contains(query)
                    || itemBreed.toLowerCase().contains(query);
            
            boolean matchesChip = false;
            if (currentFilter.equals("All")) matchesChip = true;
            else if (currentFilter.equals("Cattle")) {
                String species = item.getSpecies() != null ? item.getSpecies().toLowerCase() : "";
                matchesChip = species.contains("cow") || species.contains("cattle") || species.contains("buffalo");
            } else if (currentFilter.equals("Sheep")) {
                String species = item.getSpecies() != null ? item.getSpecies().toLowerCase() : "";
                matchesChip = species.contains("sheep") || species.contains("goat");
            }
            else if (currentFilter.equals("Alerts")) {
                String status = item.getStatus() != null ? item.getStatus() : "active";
                matchesChip = !"healthy".equalsIgnoreCase(status);
            }

            if (matchesSearch && matchesChip) {
                filteredList.add(item);
            }
        }
        adapter.setLivestockList(filteredList);
    }

    private void fetchLivestock() {
        swipeRefresh.setRefreshing(true);
        // Architectural Alignment: Using ViewModel to observe data
        viewModel.getMyLivestock(userId).observe(this, list -> {
            swipeRefresh.setRefreshing(false);
            if (list != null) {
                fullList = list;
                applyFilters();
            } else {
                Toast.makeText(this, "Failed to load herd data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
