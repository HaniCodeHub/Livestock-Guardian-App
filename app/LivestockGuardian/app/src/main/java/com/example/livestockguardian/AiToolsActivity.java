package com.example.livestockguardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.livestockguardian.ai.AiToolType;
import com.example.livestockguardian.util.NavigationHelper;
import com.google.android.material.card.MaterialCardView;

public class AiToolsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_tools);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        findViewById(R.id.ivToolbarProfile).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileActivity.class)));

        MaterialCardView cardBreed = findViewById(R.id.cardBreedAnalysis);
        MaterialCardView cardHealth = findViewById(R.id.cardHealthAnalysis);
        MaterialCardView cardWeight = findViewById(R.id.cardWeightEstimation);
        MaterialCardView cardMarket = findViewById(R.id.cardMarketEstimator);

        if (cardBreed != null) {
            cardBreed.setOnClickListener(v -> openTool(AiToolType.TOOL_BREED));
        }
        if (cardHealth != null) {
            cardHealth.setOnClickListener(v -> openTool(AiToolType.TOOL_HEALTH));
        }
        if (cardWeight != null) {
            cardWeight.setOnClickListener(v -> openTool(AiToolType.TOOL_WEIGHT));
        }
        if (cardMarket != null) {
            cardMarket.setOnClickListener(v -> openTool(AiToolType.TOOL_MARKET));
        }

        NavigationHelper.setupBottomNavigation(this, R.id.nav_ai);
    }

    private void openTool(String toolType) {
        Intent intent = new Intent(this, AiAnalysisActivity.class);
        intent.putExtra(AiToolType.EXTRA_TOOL, toolType);
        startActivity(intent);
    }
}
