package com.example.myfirsthelloworld;

import android.graphics.Rect;
import android.util.Log;

import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.List;

@ExperimentalGetImage
public class CarSpeedAnalyzer implements ImageAnalysis.Analyzer {

    public interface OnCarDetectedListener {
        void onCarDetected(Rect boundingBox, Integer trackingId, float speedKmh, boolean isLocked);
    }

    private final OnCarDetectedListener listener;
    private final com.google.mlkit.vision.objects.ObjectDetector detector;

    private Rect fixedBox = null;
    private boolean boxInitialized = false;
    private boolean manualLock = false;

    private float lockedSpeed = 0f;
    private long lastDetectionTime = 0L;
    private long speedUpdateCooldown = 0L;
    private static final long COOLDOWN_MS = 800;
    private static final long HOLD_TIME_MS = 4000;

    private float lastAvgX = 0f;
    private float lastAvgY = 0f;
    private long lastPositionTime = 0L;
    private boolean hasLastPosition = false;

    private static final float PIXELS_PER_METER = 250f;

    public CarSpeedAnalyzer(OnCarDetectedListener listener) {
        this.listener = listener;
        this.detector = ObjectDetection.getClient(
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                        .enableMultipleObjects()
                        .build()
        );
    }

    public void toggleLock() {
        manualLock = !manualLock;
    }

    public boolean isLocked() {
        return manualLock;
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            if (!boxInitialized) {
                int width = imageProxy.getWidth();
                int height = imageProxy.getHeight();

                // Box in EXACT CENTER of screen
                int boxW = (int)(width * 0.65);
                int boxH = (int)(height * 0.45);
                int left = (width - boxW) / 2;
                int top = (height - boxH) / 2;  // This centers it vertically

                fixedBox = new Rect(left, top, left + boxW, top + boxH);
                boxInitialized = true;

                Log.d("SPEED_APP", "Screen: " + width + "x" + height);
                Log.d("SPEED_APP", "Box: left=" + left + " top=" + top + " w=" + boxW + " h=" + boxH);
                Log.d("SPEED_APP", "Box center: x=" + fixedBox.centerX() + " y=" + fixedBox.centerY());
            }

            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            detector.process(image)
                    .addOnSuccessListener(detectedObjects -> processObjects(detectedObjects, imageProxy))
                    .addOnFailureListener(e -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    private void processObjects(List<DetectedObject> objects, ImageProxy imageProxy) {
        long now = System.currentTimeMillis();

        if (!manualLock) {
            float sumX = 0, sumY = 0;
            int count = 0;

            for (DetectedObject obj : objects) {
                Rect objBox = obj.getBoundingBox();
                if (Rect.intersects(objBox, fixedBox)) {
                    sumX += objBox.centerX();
                    sumY += objBox.centerY();
                    count++;
                }
            }

            if (count > 0) {
                float avgX = sumX / count;
                float avgY = sumY / count;
                lastDetectionTime = now;

                if (hasLastPosition && (now - speedUpdateCooldown) > COOLDOWN_MS) {
                    float timeSec = (now - lastPositionTime) / 1000f;
                    if (timeSec > 0.05f && timeSec < 1.0f) {
                        float dx = avgX - lastAvgX;
                        float dy = avgY - lastAvgY;
                        float disp = (float) Math.sqrt((dx * dx) + (dy * dy));

                        if (disp > 5f) {
                            float meters = disp / PIXELS_PER_METER;
                            float newSpeed = (meters / timeSec) * 3.6f;
                            if (newSpeed > 0.5f && newSpeed < 160f) {
                                lockedSpeed = lockedSpeed > 0 ? lockedSpeed * 0.7f + newSpeed * 0.3f : newSpeed;
                                speedUpdateCooldown = now;
                            }
                        }
                    }
                }

                lastAvgX = avgX;
                lastAvgY = avgY;
                lastPositionTime = now;
                hasLastPosition = true;
            } else {
                if (now - lastDetectionTime > HOLD_TIME_MS) {
                    lockedSpeed = lockedSpeed * 0.9f;
                    if (lockedSpeed < 0.3f) lockedSpeed = 0f;
                }
            }
        }

        listener.onCarDetected(fixedBox, null, lockedSpeed, manualLock);
        imageProxy.close();
    }
}