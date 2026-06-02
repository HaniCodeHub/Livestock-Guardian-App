package com.example.livestockguardian;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.livestockguardian.ai.AiToolType;
import com.example.livestockguardian.BuildConfig;
import com.example.livestockguardian.network.GeminiRepository;
import com.example.livestockguardian.util.ImageUtils;
import com.example.livestockguardian.util.NavigationHelper;
import com.example.livestockguardian.util.PhotoCaptureHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiAnalysisActivity extends AppCompatActivity {

    private String toolType;
    private Bitmap animalBitmap;
    private PhotoCaptureHelper photoHelper;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final GeminiRepository geminiRepository = new GeminiRepository();

    private ImageView ivAnimalPhoto;
    private TextView tvPhotoPlaceholder, tvResult, tvToolDescription;
    private MaterialCardView cardResult;
    private ProgressBar progressBar;
    private MaterialButton btnAnalyze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ai_analysis);

        toolType = getIntent().getStringExtra(AiToolType.EXTRA_TOOL);
        if (toolType == null) {
            toolType = AiToolType.TOOL_BREED;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(AiToolType.title(toolType));
        toolbar.setNavigationOnClickListener(v -> finish());

        ivAnimalPhoto = findViewById(R.id.ivAnimalPhoto);
        tvPhotoPlaceholder = findViewById(R.id.tvPhotoPlaceholder);
        tvResult = findViewById(R.id.tvResult);
        tvToolDescription = findViewById(R.id.tvToolDescription);
        cardResult = findViewById(R.id.cardResult);
        progressBar = findViewById(R.id.progressBar);
        btnAnalyze = findViewById(R.id.btnAnalyze);

        tvToolDescription.setText("Take or upload an animal photo, then run Gemini analysis.");

        photoHelper = new PhotoCaptureHelper(this, new PhotoCaptureHelper.Callback() {
            @Override
            public void onPhotoCaptured(Bitmap bitmap) {
                showPhoto(bitmap);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(AiAnalysisActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnCamera).setOnClickListener(v -> photoHelper.openCamera());
        findViewById(R.id.btnGallery).setOnClickListener(v -> photoHelper.openGallery());
        btnAnalyze.setOnClickListener(v -> runAnalysis());

        NavigationHelper.setupBottomNavigation(this, R.id.nav_ai);
    }

    private void showPhoto(Bitmap bitmap) {
        animalBitmap = bitmap;
        tvPhotoPlaceholder.setVisibility(View.GONE);
        ivAnimalPhoto.setVisibility(View.VISIBLE);
        ivAnimalPhoto.setImageTintList(null);
        ivAnimalPhoto.setImageBitmap(bitmap);
        cardResult.setVisibility(View.GONE);
    }

    private void runAnalysis() {
        if (animalBitmap == null) {
            Toast.makeText(this, "Please add an animal photo first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (BuildConfig.GEMINI_API_KEY == null || BuildConfig.GEMINI_API_KEY.trim().isEmpty()) {
            Toast.makeText(this, R.string.gemini_key_hint, Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnAnalyze.setEnabled(false);
        btnAnalyze.setText("Analyzing...");
        cardResult.setVisibility(View.GONE);

        byte[] jpeg = ImageUtils.bitmapToJpegBytes(animalBitmap, 85);
        String base64 = Base64.encodeToString(jpeg, Base64.NO_WRAP);

        executor.execute(() -> {
            try {
                String result = geminiRepository.analyzeImage(toolType, base64, "image/jpeg");
                runOnUiThread(() -> showAnalysisResult(result));
            } catch (Exception e) {
                runOnUiThread(() -> {
                    resetAnalyzeButton();
                    String message = e.getMessage() != null ? e.getMessage() : "Analysis failed";
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void showAnalysisResult(String result) {
        resetAnalyzeButton();
        cardResult.setVisibility(View.VISIBLE);
        tvResult.setText(result);
        cardResult.post(() -> cardResult.requestFocus());
    }

    private void resetAnalyzeButton() {
        progressBar.setVisibility(View.GONE);
        btnAnalyze.setEnabled(true);
        btnAnalyze.setText("Analyze with Gemini");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdownNow();
    }
}
