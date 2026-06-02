package com.example.livestockguardian.util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;

public class PhotoCaptureHelper {

    public interface Callback {
        void onPhotoCaptured(Bitmap bitmap);

        void onError(String message);
    }

    private final AppCompatActivity activity;
    private final Callback callback;
    private Uri cameraOutputUri;

    private final ActivityResultLauncher<String> cameraPermissionLauncher;
    private final ActivityResultLauncher<String> galleryPermissionLauncher;
    private final ActivityResultLauncher<Uri> takePictureLauncher;
    private final ActivityResultLauncher<String> pickImageLauncher;

    public PhotoCaptureHelper(AppCompatActivity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;

        cameraPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        launchCameraCapture();
                    } else {
                        callback.onError("Camera permission is required");
                    }
                }
        );

        galleryPermissionLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        launchGalleryPicker();
                    } else {
                        callback.onError("Gallery permission is required");
                    }
                }
        );

        takePictureLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (!success || cameraOutputUri == null) {
                        callback.onError("Camera capture cancelled");
                        return;
                    }
                    try {
                        Bitmap bitmap = ImageUtils.loadBitmap(activity, cameraOutputUri, 1600);
                        callback.onPhotoCaptured(bitmap);
                    } catch (IOException e) {
                        callback.onError("Failed to read camera photo");
                    }
                }
        );

        pickImageLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) {
                        return;
                    }
                    try {
                        Bitmap bitmap = ImageUtils.loadBitmap(activity, uri, 1600);
                        callback.onPhotoCaptured(bitmap);
                    } catch (IOException e) {
                        callback.onError("Failed to load gallery image");
                    }
                }
        );
    }

    public void showPickerDialog() {
        new MaterialAlertDialogBuilder(activity)
                .setTitle("Add photo")
                .setItems(new CharSequence[]{"Take photo", "Choose from gallery"}, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    public void openCamera() {
        requestCamera();
    }

    public void openGallery() {
        requestGallery();
    }

    private void requestCamera() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCameraCapture();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void requestGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                launchGalleryPicker();
            } else {
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            launchGalleryPicker();
        } else {
            galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void launchCameraCapture() {
        try {
            File imageFile = File.createTempFile("muzzle_", ".jpg", activity.getCacheDir());
            cameraOutputUri = FileProvider.getUriForFile(
                    activity,
                    activity.getPackageName() + ".fileprovider",
                    imageFile
            );
            takePictureLauncher.launch(cameraOutputUri);
        } catch (IOException e) {
            callback.onError("Could not prepare camera file");
        }
    }

    private void launchGalleryPicker() {
        pickImageLauncher.launch("image/*");
    }
}
