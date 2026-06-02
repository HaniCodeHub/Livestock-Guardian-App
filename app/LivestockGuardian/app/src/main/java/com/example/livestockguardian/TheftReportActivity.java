package com.example.livestockguardian;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.livestockguardian.viewmodel.LivestockViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TheftReportActivity extends AppCompatActivity {

    private TextView tvTheftAnimalTag;
    private TextInputEditText etTheftDate, etTheftLocation, etTheftDetails;
    private MaterialButton btnSubmitReport;
    private LivestockViewModel viewModel;
    private String livestockId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_theft_report);

        viewModel = new ViewModelProvider(this).get(LivestockViewModel.class);
        livestockId = getIntent().getStringExtra("livestock_id");

        initViews();

        if (livestockId != null) {
            tvTheftAnimalTag.setText("Reporting ID: #" + (livestockId.length() > 8 ? livestockId.substring(0, 8).toUpperCase() : livestockId));
        }
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvTheftAnimalTag = findViewById(R.id.tvTheftAnimalTag);
        etTheftDate = findViewById(R.id.etTheftDate);
        etTheftLocation = findViewById(R.id.etTheftLocation);
        etTheftDetails = findViewById(R.id.etTheftDetails);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        btnSubmitReport.setOnClickListener(v -> submitTheftReport());
    }

    private void submitTheftReport() {
        String date = etTheftDate.getText().toString().trim();
        String location = etTheftLocation.getText().toString().trim();
        String details = etTheftDetails.getText().toString().trim();

        if (date.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please provide date and location", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitReport.setEnabled(false);
        btnSubmitReport.setText("Broadcasting...");

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "stolen");
        updates.put("notes", "STOLEN ON: " + date + " AT: " + location + ". " + details);

        // Call ViewModel with Retrofit logic
        viewModel.reportTheft("eq." + livestockId, updates, new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        Toast.makeText(TheftReportActivity.this, "RED ALERT BROADCAST SUCCESSFUL", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(TheftReportActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        btnSubmitReport.setEnabled(true);
                        btnSubmitReport.setText("Broadcast Red Alert");
                        Toast.makeText(TheftReportActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                runOnUiThread(() -> {
                    btnSubmitReport.setEnabled(true);
                    btnSubmitReport.setText("Broadcast Red Alert");
                    Toast.makeText(TheftReportActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
