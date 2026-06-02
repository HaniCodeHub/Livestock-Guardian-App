package com.example.livestockguardian;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.livestockguardian.viewmodel.LivestockViewModel;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnimalProfileActivity extends AppCompatActivity {

    private TextView tvProfileName, tvProfileSpecies, tvProfileBreed, tvProfileAge, tvProfileWeight, tvAnimalIdLabel;
    private View btnReportTheft, btnEditDetails;
    private ProgressBar profileProgressBar;
    private LivestockViewModel viewModel;
    private String livestockId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_animal_profile);

        viewModel = new ViewModelProvider(this).get(LivestockViewModel.class);
        livestockId = getIntent().getStringExtra("livestock_id");

        initViews();

        if (livestockId != null) {
            fetchAnimalDetails();
        } else {
            Toast.makeText(this, "No Animal ID provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvAnimalIdLabel = findViewById(R.id.tvAnimalIdLabel);
        tvProfileName = findViewById(R.id.tvProfileName);
        btnReportTheft = findViewById(R.id.btnReportTheft);
        btnEditDetails = findViewById(R.id.btnEditDetails);
        tvProfileSpecies = findViewById(R.id.tvProfileSpecies);
        tvProfileBreed = findViewById(R.id.tvProfileBreed);
        tvProfileAge = findViewById(R.id.tvProfileAge);
        tvProfileWeight = findViewById(R.id.tvProfileWeight);
        profileProgressBar = findViewById(R.id.profileProgressBar);

        btnReportTheft.setOnClickListener(v -> {
            Intent intent = new Intent(this, TheftReportActivity.class);
            intent.putExtra("livestock_id", livestockId);
            startActivity(intent);
        });

        findViewById(R.id.ivProfileTop).setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
    }

    private void fetchAnimalDetails() {
        profileProgressBar.setVisibility(View.VISIBLE);
        viewModel.getLivestockDetails(livestockId, new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                runOnUiThread(() -> {
                    profileProgressBar.setVisibility(View.GONE);
                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONArray array = new JSONArray(response.body().string());
                            if (array.length() > 0) populateData(array.getJSONObject(0));
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                runOnUiThread(() -> profileProgressBar.setVisibility(View.GONE));
            }
        });
    }

    private void populateData(JSONObject json) {
        try {
            tvProfileName.setText(json.optString("animal_name", json.optString("name", "Unknown")));
            tvProfileSpecies.setText(json.optString("species"));
            tvProfileBreed.setText(json.optString("breed"));
            double age = json.optDouble("age_years", json.optDouble("age", 0));
            double weight = json.optDouble("weight_kg", json.optDouble("weight", 0));
            tvProfileAge.setText(age + " Years");
            tvProfileWeight.setText(weight + " Kg");
            tvAnimalIdLabel.setText("ID: " + livestockId.substring(0, Math.min(8, livestockId.length())).toUpperCase());

            String status = json.optString("status", json.optString("health_status", "active"));
            if ("stolen".equalsIgnoreCase(status)) {
                btnReportTheft.setVisibility(View.GONE);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
