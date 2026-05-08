package com.example.myfirsthelloworld;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
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

                BoundingBoxView boundingBoxView = findViewById(R.id.boundingBoxView);
                TextView speedTextView = findViewById(R.id.speedText);
                lockButton = findViewById(R.id.lockButton);

                analyzer = new CarSpeedAnalyzer((box, id, speed, locked) -> {
                    runOnUiThread(() -> {
                        boundingBoxView.setBoundingBox(box);
                        speedTextView.setText(String.format("%.0f", speed));

                        if (locked) {
                            lockButton.setText("LOCKED");
                            lockButton.setBackgroundColor(0xFF4CAF50);
                        } else {
                            lockButton.setText("LOCK SPEED");
                            lockButton.setBackgroundColor(0xFFFF9800);
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