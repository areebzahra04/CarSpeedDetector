package com.example.myfirsthelloworld;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executors;

@OptIn(markerClass = ExperimentalGetImage.class)
public class MainActivity extends ComponentActivity {
    private PreviewView previewView;
    private CarSpeedAnalyzer analyzer;
    private Button lockButton;
    private TextView speedTextView;
    private ProgressBar speedProgress;
    private TextView avgSpeedText;
    private TextView maxSpeedText;
    private TextView lockedSpeedText;
    private TextView statusText;

    private float maxSpeed = 0f;
    private float avgSpeed = 0f;
    private int speedCount = 0;
    private float speedSum = 0f;

    private final androidx.activity.result.ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startCamera();
                } else {
                    Toast.makeText(MainActivity.this, "Camera permission is required", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        previewView = findViewById(R.id.viewFinder);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(this).get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Connect all UI elements
                speedTextView = findViewById(R.id.speedText);
                speedProgress = findViewById(R.id.speedProgress);
                avgSpeedText = findViewById(R.id.avgSpeedText);
                maxSpeedText = findViewById(R.id.maxSpeedText);
                lockedSpeedText = findViewById(R.id.lockedSpeedText);
                statusText = findViewById(R.id.statusText);
                lockButton = findViewById(R.id.lockButton);

                analyzer = new CarSpeedAnalyzer((box, id, speed, locked) -> {
                    runOnUiThread(() -> {
                        // Update main speed display
                        speedTextView.setText(String.format("%.0f", speed));

                        // Update progress arc (max 240 km/h)
                        int progress = (int) Math.min(speed, 240);
                        speedProgress.setProgress(progress);

                        // Update max speed
                        if (speed > maxSpeed) {
                            maxSpeed = speed;
                        }
                        maxSpeedText.setText(String.format("%.0f", maxSpeed));

                        // Update average speed
                        if (speed > 0.5f) {
                            speedSum += speed;
                            speedCount++;
                            avgSpeed = speedSum / speedCount;
                        }
                        avgSpeedText.setText(String.format("%.0f", avgSpeed));

                        // Update locked speed display
                        if (locked) {
                            lockedSpeedText.setText(String.format("%.0f", speed));
                            lockButton.setText("UNLOCK");
                            lockButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
                            statusText.setText("LOCKED");
                            statusText.setTextColor(0xFFFF9800);
                        } else {
                            lockButton.setText("LOCK SPEED");
                            lockButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF9800));

                            if (speed > 0.5f) {
                                statusText.setText("TRACKING");
                                statusText.setTextColor(0xFF9AE6B4);
                            } else {
                                statusText.setText("WAITING");
                                statusText.setTextColor(0xFF9AE6B4);
                            }
                        }
                    });
                });

                lockButton.setOnClickListener(v -> {
                    if (analyzer != null) {
                        analyzer.toggleLock();
                    }
                });

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
}