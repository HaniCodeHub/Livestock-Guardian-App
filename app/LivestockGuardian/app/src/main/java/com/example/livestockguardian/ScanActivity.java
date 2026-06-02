package com.example.livestockguardian;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.livestockguardian.util.ApiErrorUtils;
import com.example.livestockguardian.util.BiometricRequestUtils;
import com.example.livestockguardian.util.ImageUtils;
import com.example.livestockguardian.viewmodel.LivestockViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = "ScanActivity";

    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private ProgressBar scanProgressBar;
    private MaterialCardView cardResult;
    private TextView tvResultName, tvResultOwner, tvConfidence, tvStolenAlert;
    private Chip chipMatchStatus;
    private MaterialButton btnViewProfile, btnScanAgain, btnScan, btnGallery;
    private View layoutControls;

    private LivestockViewModel viewModel;
    private String matchedLivestockId;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    startCamera();
                } else {
                    Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = ImageUtils.loadBitmap(this, imageUri, 1600);
                        startVerificationFlow(ImageUtils.bitmapToJpegBytes(bitmap, 90));
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scan);

        viewModel = new ViewModelProvider(this).get(LivestockViewModel.class);
        initViews();
        ensureCameraPermission();
    }

    private void initViews() {
        viewFinder = findViewById(R.id.viewFinder);
        scanProgressBar = findViewById(R.id.scanProgressBar);
        cardResult = findViewById(R.id.cardResult);
        tvResultName = findViewById(R.id.tvResultName);
        tvResultOwner = findViewById(R.id.tvResultOwner);
        tvConfidence = findViewById(R.id.tvConfidence);
        tvStolenAlert = findViewById(R.id.tvStolenAlert);
        chipMatchStatus = findViewById(R.id.chipMatchStatus);
        btnViewProfile = findViewById(R.id.btnViewProfile);
        btnScanAgain = findViewById(R.id.btnScanAgain);
        btnScan = findViewById(R.id.btnScan);
        btnGallery = findViewById(R.id.btnGallery);
        layoutControls = findViewById(R.id.layoutControls);

        btnScan.setOnClickListener(v -> takePhoto());
        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });
        btnScanAgain.setOnClickListener(v -> {
            cardResult.setVisibility(View.GONE);
            tvStolenAlert.setVisibility(View.GONE);
            layoutControls.setVisibility(View.VISIBLE);
            chipMatchStatus.setText("");
        });
        btnViewProfile.setOnClickListener(v -> {
            if (matchedLivestockId != null) {
                Intent intent = new Intent(ScanActivity.this, AnimalProfileActivity.class);
                intent.putExtra("livestock_id", matchedLivestockId);
                startActivity(intent);
            }
        });
    }

    private void ensureCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture
                );
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera initialization failed", e);
                Toast.makeText(this, "Camera failed to start", Toast.LENGTH_SHORT).show();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void takePhoto() {
        if (imageCapture == null) {
            Toast.makeText(this, "Camera not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        scanProgressBar.setVisibility(View.VISIBLE);
        btnScan.setEnabled(false);

        File outputFile = new File(getCacheDir(), "scan_" + System.currentTimeMillis() + ".jpg");
        ImageCapture.OutputFileOptions options = new ImageCapture.OutputFileOptions.Builder(outputFile).build();

        imageCapture.takePicture(
                options,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        try {
                            Bitmap bitmap = ImageUtils.loadBitmap(
                                    ScanActivity.this,
                                    Uri.fromFile(outputFile),
                                    1600
                            );
                            startVerificationFlow(ImageUtils.bitmapToJpegBytes(bitmap, 90));
                        } catch (Exception e) {
                            handleError("Could not process captured image");
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        handleError("Capture failed");
                    }
                }
        );
    }

    private void startVerificationFlow(byte[] imageBytes) {
        runOnUiThread(() -> {
            scanProgressBar.setVisibility(View.VISIBLE);
            layoutControls.setVisibility(View.GONE);
            Toast.makeText(this, "Connecting to biometric server...", Toast.LENGTH_SHORT).show();
        });

        viewModel.warmUpBiometricServer(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                fetchEmbeddingsAndMatch(imageBytes);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                fetchEmbeddingsAndMatch(imageBytes);
            }
        });
    }

    private void fetchEmbeddingsAndMatch(byte[] imageBytes) {
        viewModel.fetchAllEmbeddings(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        handleError(ApiErrorUtils.message(response, "Could not load embeddings"));
                        return;
                    }
                    performMatch(imageBytes, response.body().string());
                } catch (Exception e) {
                    handleError("Database error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                handleError("Network error");
            }
        });
    }

    private void performMatch(byte[] imageBytes, String recordsJson) {
        viewModel.matchMuzzle(imageBytes, recordsJson, new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        handleError(ApiErrorUtils.message(response, "Match failed"));
                        return;
                    }
                    JSONObject result = new JSONObject(response.body().string());
                    double confidence = result.optDouble("confidence", 0.0);
                    String id = result.optString("livestock_id", null);
                    if (id != null && !id.isEmpty() && !"null".equals(id)) {
                        fetchMatchDetails(id, confidence);
                    } else {
                        runOnUiThread(() -> showNoMatch());
                    }
                } catch (Exception e) {
                    handleError("Match parse error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                handleError(BiometricRequestUtils.networkErrorMessage(t));
            }
        });
    }

    private void fetchMatchDetails(String id, double confidence) {
        matchedLivestockId = id;
        viewModel.getLivestockDetails(id, new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        handleError("Could not load animal details");
                        return;
                    }
                    JSONArray arr = new JSONArray(response.body().string());
                    if (arr.length() > 0) {
                        JSONObject animal = arr.getJSONObject(0);
                        String name = animal.optString("animal_name", animal.optString("name", "Unknown"));
                        String status = animal.optString("status", animal.optString("health_status", "active"));
                        String owner = "Verified Owner";
                        if (animal.has("users") && !animal.isNull("users")) {
                            JSONObject users = animal.getJSONObject("users");
                            owner = users.optString("full_name", owner);
                        }
                        String finalOwner = owner;
                        runOnUiThread(() -> showResult(name, finalOwner, confidence, status));
                    } else {
                        runOnUiThread(() -> showNoMatch());
                    }
                } catch (Exception e) {
                    handleError("Details error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                handleError("Fetch failed");
            }
        });
    }

    private void showResult(String name, String owner, double confidence, String status) {
        scanProgressBar.setVisibility(View.GONE);
        btnScan.setEnabled(true);
        cardResult.setVisibility(View.VISIBLE);
        tvResultName.setText(name);
        tvResultOwner.setText("Owner: " + owner);
        int percentage = (int) (confidence * 100);
        tvConfidence.setText("Match confidence: " + percentage + "%");
        chipMatchStatus.setText(status.toUpperCase());

        if ("stolen".equalsIgnoreCase(status)) {
            tvStolenAlert.setVisibility(View.VISIBLE);
        } else {
            tvStolenAlert.setVisibility(View.GONE);
        }
    }

    private void showNoMatch() {
        scanProgressBar.setVisibility(View.GONE);
        btnScan.setEnabled(true);
        layoutControls.setVisibility(View.VISIBLE);
        Toast.makeText(this, "No matching animal found", Toast.LENGTH_LONG).show();
    }

    private void handleError(String msg) {
        runOnUiThread(() -> {
            scanProgressBar.setVisibility(View.GONE);
            btnScan.setEnabled(true);
            layoutControls.setVisibility(View.VISIBLE);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
    }
}
