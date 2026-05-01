package com.example.myfirsthelloworld;

import android.graphics.Rect;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.annotation.NonNull;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.List;

public class CarSpeedAnalyzer implements ImageAnalysis.Analyzer {

    public interface OnCarDetectedListener {
        void onCarDetected(Rect boundingBox, Integer trackingId, float speedKmh);
    }

    private OnCarDetectedListener listener;
    private Float lastCenterX = null;
    private long lastTimestamp = 0;
    private Integer lastTrackingId = null;

    private ObjectDetectorOptions options;
    private com.google.mlkit.vision.objects.ObjectDetector detector;

    public CarSpeedAnalyzer(OnCarDetectedListener listener) {
        this.listener = listener;

        options = new ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .build();
        detector = ObjectDetection.getClient(options);
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            detector.process(image)
                    .addOnSuccessListener(detectedObjects -> {
                        processObjects(detectedObjects, imageProxy);
                    })
                    .addOnFailureListener(e -> {
                        imageProxy.close();
                    });
        } else {
            imageProxy.close();
        }
    }

    private void processObjects(List<DetectedObject> objects, ImageProxy imageProxy) {
        DetectedObject targetObject = null;
        int maxArea = 0;
        int imageWidth = imageProxy.getWidth();
        int imageHeight = imageProxy.getHeight();
        int minArea = (imageWidth * imageHeight) / 20; // Object must be at least 5% of screen

        for (DetectedObject obj : objects) {
            int area = obj.getBoundingBox().width() * obj.getBoundingBox().height();

            // Skip tiny objects
            if (area < minArea) {
                continue;
            }

            boolean isVehicle = false;
            for (DetectedObject.Label label : obj.getLabels()) {
                String text = label.getText();
                if (text.contains("Car") || text.contains("Vehicle") || text.contains("Truck")
                        || text.contains("Bus") || text.contains("Motorcycle")) {
                    isVehicle = true;
                    break;
                }
            }

            if (area > maxArea) {
                if (isVehicle) {
                    maxArea = area;
                    targetObject = obj;
                } else if (targetObject == null) {
                    // If no vehicle found, track the largest object anyway
                    maxArea = area;
                    targetObject = obj;
                }
            }
        }

        if (targetObject != null) {
            Rect box = targetObject.getBoundingBox();
            Integer currentId = targetObject.getTrackingId();
            long currentTimestamp = System.currentTimeMillis();
            float currentCenterX = box.centerX();

            float speed = 0f;
            if (currentId != null && currentId.equals(lastTrackingId) && lastCenterX != null) {
                float timeSec = (currentTimestamp - lastTimestamp) / 1000f;
                if (timeSec > 0.05f) {
                    float pixelDisplacement = Math.abs(currentCenterX - lastCenterX);
                    float pixelsPerMeter = 50f; // Lower = more sensitive, shows speed for smaller movements
                    float metersTraveled = pixelDisplacement / pixelsPerMeter;
                    float speedMs = metersTraveled / timeSec;
                    speed = speedMs * 3.6f;
                }
            }

            lastCenterX = currentCenterX;
            lastTimestamp = currentTimestamp;
            lastTrackingId = currentId;

            listener.onCarDetected(box, currentId, speed);
        } else {
            listener.onCarDetected(null, null, 0f);
        }

        imageProxy.close();
    }
}