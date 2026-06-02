package com.example.livestockguardian;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.livestockguardian.util.ApiErrorUtils;
import com.example.livestockguardian.util.BiometricRequestUtils;
import com.example.livestockguardian.util.ImageUtils;
import com.example.livestockguardian.util.LivestockFormUtils;
import com.example.livestockguardian.util.PhotoCaptureHelper;

import com.example.livestockguardian.viewmodel.LivestockViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterLivestockActivity extends AppCompatActivity {

    private TextInputEditText etAnimalName, etBreed, etAge, etWeight, etColor, etNotes;
    private Spinner spinnerSpecies, spinnerStatus;
    private RadioGroup rgGender;
    private ImageView ivMuzzlePhoto, ivMuzzlePlaceholder;
    private LinearLayout layoutExtraPhotoThumbs;
    private MaterialButton btnTakePhoto, btnGalleryMuzzle, btnSubmitRegistration;
    private MaterialButton btnAddLeftSide, btnAddRightSide, btnAddBody;
    private ProgressBar loadingOverlay;
    private TextView tvOwnerName;

    private Bitmap muzzleBitmap;
    private final Map<String, Bitmap> additionalPhotos = new LinkedHashMap<>();
    private LivestockViewModel viewModel;
    private String userId;
    private String ownerName;
    private PhotoCaptureHelper muzzlePhotoHelper;
    private PhotoCaptureHelper extraPhotoHelper;
    private String pendingExtraImageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_livestock);

        viewModel = new ViewModelProvider(this).get(LivestockViewModel.class);
        SharedPreferences prefs = getSharedPreferences("LivestockGuardianPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        ownerName = prefs.getString("userName", "Farmer");

        initViews();
        setupPhotoHelpers();
        setupSpinners();

        if (userId.isEmpty()) {
            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvOwnerName.setText("Owner: " + ownerName);

        btnTakePhoto.setOnClickListener(v -> muzzlePhotoHelper.openCamera());
        btnGalleryMuzzle.setOnClickListener(v -> muzzlePhotoHelper.openGallery());
        btnAddLeftSide.setOnClickListener(v -> pickExtraPhoto("left_side"));
        btnAddRightSide.setOnClickListener(v -> pickExtraPhoto("right_side"));
        btnAddBody.setOnClickListener(v -> pickExtraPhoto("full_body"));
        btnSubmitRegistration.setOnClickListener(v -> validateAndSubmit());
    }

    private void initViews() {
        etAnimalName = findViewById(R.id.etAnimalName);
        spinnerSpecies = findViewById(R.id.spinnerSpecies);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        etBreed = findViewById(R.id.etBreed);
        rgGender = findViewById(R.id.rgGender);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etColor = findViewById(R.id.etColor);
        etNotes = findViewById(R.id.etNotes);
        ivMuzzlePhoto = findViewById(R.id.ivMuzzlePhoto);
        ivMuzzlePlaceholder = findViewById(R.id.ivMuzzlePlaceholder);
        layoutExtraPhotoThumbs = findViewById(R.id.layoutExtraPhotoThumbs);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnGalleryMuzzle = findViewById(R.id.btnGalleryMuzzle);
        btnAddLeftSide = findViewById(R.id.btnAddLeftSide);
        btnAddRightSide = findViewById(R.id.btnAddRightSide);
        btnAddBody = findViewById(R.id.btnAddBody);
        btnSubmitRegistration = findViewById(R.id.btnSubmitRegistration);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        tvOwnerName = findViewById(R.id.tvOwnerName);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void setupPhotoHelpers() {
        muzzlePhotoHelper = new PhotoCaptureHelper(this, new PhotoCaptureHelper.Callback() {
            @Override
            public void onPhotoCaptured(Bitmap bitmap) {
                showMuzzlePreview(bitmap);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RegisterLivestockActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        extraPhotoHelper = new PhotoCaptureHelper(this, new PhotoCaptureHelper.Callback() {
            @Override
            public void onPhotoCaptured(Bitmap bitmap) {
                if (pendingExtraImageType != null) {
                    additionalPhotos.put(pendingExtraImageType, bitmap);
                    refreshExtraPhotoThumbs();
                    pendingExtraImageType = null;
                }
            }

            @Override
            public void onError(String message) {
                pendingExtraImageType = null;
                Toast.makeText(RegisterLivestockActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickExtraPhoto(String imageType) {
        pendingExtraImageType = imageType;
        extraPhotoHelper.showPickerDialog();
    }

    private void refreshExtraPhotoThumbs() {
        layoutExtraPhotoThumbs.removeAllViews();
        for (Map.Entry<String, Bitmap> entry : additionalPhotos.entrySet()) {
            ImageView thumb = new ImageView(this);
            int size = (int) (96 * getResources().getDisplayMetrics().density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMarginEnd((int) (8 * getResources().getDisplayMetrics().density));
            thumb.setLayoutParams(params);
            thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            thumb.setImageBitmap(entry.getValue());
            thumb.setContentDescription(entry.getKey());
            layoutExtraPhotoThumbs.addView(thumb);
        }
    }

    private void setupSpinners() {
        String[] species = {"Cow", "Buffalo", "Sheep", "Goat", "Horse"};
        spinnerSpecies.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, species));

        String[] statuses = {"healthy", "sick", "injured", "dead", "stolen", "sold"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
    }

    private void validateAndSubmit() {
        if (muzzleBitmap == null) {
            Toast.makeText(this, "Please capture or upload a muzzle photo", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = etAnimalName.getText().toString().trim();
        String species = spinnerSpecies.getSelectedItem().toString();
        String breed = etBreed.getText().toString().trim();
        int genderId = rgGender.getCheckedRadioButtonId();
        String age = etAge.getText().toString().trim();
        String weight = etWeight.getText().toString().trim();
        String status = LivestockFormUtils.normalizeStatus(spinnerStatus.getSelectedItem().toString());
        String color = etColor.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (name.isEmpty() || breed.isEmpty() || age.isEmpty() || weight.isEmpty() || genderId == -1) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = LivestockFormUtils.normalizeGender(
                ((RadioButton) findViewById(genderId)).getText().toString()
        );
        showLoading(true);

        byte[] imageBytes = ImageUtils.bitmapToJpegBytes(muzzleBitmap, 90);
        startRegistrationFlow(name, species, breed, gender, age, weight, status, color, notes, imageBytes);
    }

    private void showMuzzlePreview(Bitmap bitmap) {
        muzzleBitmap = bitmap;
        ivMuzzlePlaceholder.setVisibility(View.GONE);
        ivMuzzlePhoto.setVisibility(View.VISIBLE);
        ivMuzzlePhoto.setImageTintList(null);
        ivMuzzlePhoto.setImageBitmap(bitmap);
    }

    private void startRegistrationFlow(
            String name,
            String species,
            String breed,
            String gender,
            String age,
            String weight,
            String status,
            String color,
            String notes,
            byte[] imageBytes
    ) {
        Toast.makeText(this, "Connecting to biometric server (first use may take 1–2 min)...", Toast.LENGTH_LONG).show();

        viewModel.warmUpBiometricServer(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                loadEmbeddingsAndCheckDuplicate(name, species, breed, gender, age, weight, status, color, notes, imageBytes);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                loadEmbeddingsAndCheckDuplicate(name, species, breed, gender, age, weight, status, color, notes, imageBytes);
            }
        });
    }

    private void loadEmbeddingsAndCheckDuplicate(
            String name,
            String species,
            String breed,
            String gender,
            String age,
            String weight,
            String status,
            String color,
            String notes,
            byte[] imageBytes
    ) {
        viewModel.fetchAllEmbeddings(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    String recordsJson = "[]";
                    if (response.isSuccessful() && response.body() != null) {
                        recordsJson = response.body().string();
                        if (recordsJson.trim().isEmpty()) {
                            recordsJson = "[]";
                        }
                    }
                    runDuplicateCheck(
                            name, species, breed, gender, age, weight, status, color, notes,
                            imageBytes,
                            recordsJson
                    );
                } catch (Exception e) {
                    handleError("Could not load existing records");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                runDuplicateCheck(
                        name, species, breed, gender, age, weight, status, color, notes,
                        imageBytes,
                        "[]"
                );
            }
        });
    }

    private void runDuplicateCheck(
            String name,
            String species,
            String breed,
            String gender,
            String age,
            String weight,
            String status,
            String color,
            String notes,
            byte[] imageBytes,
            String recordsJson
    ) {
        viewModel.checkDuplicate(
                imageBytes,
                recordsJson,
                new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        try {
                            if (!response.isSuccessful() || response.body() == null) {
                                handleError(ApiErrorUtils.message(response, "Duplicate check failed"));
                                return;
                            }
                            JSONObject json = new JSONObject(response.body().string());
                            if (json.optBoolean("is_duplicate", false)) {
                                runOnUiThread(() -> new AlertDialog.Builder(RegisterLivestockActivity.this)
                                        .setTitle("Duplicate Found")
                                        .setMessage("This muzzle is already registered in the system.")
                                        .setPositiveButton("OK", null)
                                        .show());
                                showLoading(false);
                            } else {
                                enrollMuzzle(name, species, breed, gender, age, weight, status, color, notes, imageBytes);
                            }
                        } catch (Exception e) {
                            handleError("Duplicate check error");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        Toast.makeText(
                                RegisterLivestockActivity.this,
                                "Duplicate check skipped, enrolling muzzle...",
                                Toast.LENGTH_SHORT
                        ).show();
                        enrollMuzzle(name, species, breed, gender, age, weight, status, color, notes, imageBytes);
                    }
                }
        );
    }

    private void enrollMuzzle(
            String name,
            String species,
            String breed,
            String gender,
            String age,
            String weight,
            String status,
            String color,
            String notes,
            byte[] imageBytes
    ) {
        viewModel.enrollMuzzle(imageBytes, new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        handleError(ApiErrorUtils.message(response, "Muzzle enrollment failed"));
                        return;
                    }
                    JSONObject json = new JSONObject(response.body().string());
                    Object embeddingValue = json.get("embedding");
                    double confidence = json.optDouble("confidence", 0.0);
                    saveToSupabase(name, species, breed, gender, age, weight, status, color, notes, embeddingValue, confidence);
                } catch (Exception e) {
                    handleError("Could not parse embedding from server");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                handleError(BiometricRequestUtils.networkErrorMessage(t));
            }
        });
    }

    private void saveToSupabase(
            String name,
            String species,
            String breed,
            String gender,
            String age,
            String weight,
            String status,
            String color,
            String notes,
            Object embedding,
            double confidence
    ) {
        String animalId = UUID.randomUUID().toString();
        try {
            JSONObject livestock = new JSONObject();
            livestock.put("id", animalId);
            livestock.put("animal_name", name);
            livestock.put("species", species);
            livestock.put("breed", breed);
            livestock.put("gender", gender);
            livestock.put("age_years", Double.parseDouble(age));
            livestock.put("weight_kg", Double.parseDouble(weight));
            livestock.put("status", LivestockFormUtils.normalizeStatus(status));
            livestock.put("owner_id", userId);
            LivestockFormUtils.putIfNotEmpty(livestock, "color", color);
            LivestockFormUtils.putIfNotEmpty(livestock, "notes", notes);

            RequestBody body = RequestBody.create(
                    livestock.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            viewModel.saveLivestock(body, new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        saveEmbedding(animalId, embedding, confidence);
                    } else {
                        handleError(ApiErrorUtils.message(response, "Could not save animal"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    handleError("Database connection failed");
                }
            });
        } catch (Exception e) {
            handleError("Invalid age or weight");
        }
    }

    private void saveEmbedding(String animalId, Object embedding, double confidence) {
        try {
            JSONObject embeddingObj = new JSONObject();
            embeddingObj.put("livestock_id", animalId);
            embeddingObj.put("confidence", confidence);
            if (embedding instanceof JSONArray) {
                embeddingObj.put("embedding", embedding);
            } else if (embedding instanceof String) {
                String value = ((String) embedding).trim();
                if (value.startsWith("[")) {
                    embeddingObj.put("embedding", new JSONArray(value));
                } else {
                    embeddingObj.put("embedding", value);
                }
            } else {
                embeddingObj.put("embedding", String.valueOf(embedding));
            }

            RequestBody body = RequestBody.create(
                    embeddingObj.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            viewModel.saveEmbedding(body, new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        saveAdditionalImages(animalId, 0);
                    } else {
                        handleError(ApiErrorUtils.message(response, "Embedding save failed"));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    handleError("Embedding save network error");
                }
            });
        } catch (Exception e) {
            handleError("Invalid embedding format");
        }
    }

    private void saveAdditionalImages(String animalId, int index) {
        if (additionalPhotos.isEmpty()) {
            finishSuccessfully();
            return;
        }

        String[] keys = additionalPhotos.keySet().toArray(new String[0]);
        if (index >= keys.length) {
            finishSuccessfully();
            return;
        }

        String imageType = keys[index];
        Bitmap bitmap = additionalPhotos.get(imageType);
        try {
            JSONObject imageRow = new JSONObject();
            imageRow.put("id", UUID.randomUUID().toString());
            imageRow.put("livestock_id", animalId);
            imageRow.put("image_type", imageType);
            imageRow.put("image_url", ImageUtils.bitmapToDataUrl(bitmap, 80));

            RequestBody body = RequestBody.create(
                    imageRow.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            viewModel.saveLivestockImage(body, new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    saveAdditionalImages(animalId, index + 1);
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    finishSuccessfully();
                }
            });
        } catch (Exception e) {
            saveAdditionalImages(animalId, index + 1);
        }
    }

    private void finishSuccessfully() {
        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(RegisterLivestockActivity.this, "Animal enrolled successfully", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
            btnSubmitRegistration.setEnabled(!show);
        });
    }

    private void handleError(String message) {
        runOnUiThread(() -> {
            showLoading(false);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}
