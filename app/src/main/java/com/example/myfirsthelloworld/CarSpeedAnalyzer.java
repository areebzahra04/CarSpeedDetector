//package com.example.myfirsthelloworld;
//
//import android.graphics.Rect;
//import android.util.Log;
//
//import androidx.camera.core.ExperimentalGetImage;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageProxy;
//import androidx.annotation.NonNull;
//
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.objects.DetectedObject;
//import com.google.mlkit.vision.objects.ObjectDetection;
//import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
//
//import java.util.List;
//
//@ExperimentalGetImage
//public class CarSpeedAnalyzer implements ImageAnalysis.Analyzer {
//
//    public interface OnCarDetectedListener {
//        void onCarDetected(Rect boundingBox, Integer trackingId, float speedKmh);
//    }
//
//    private final OnCarDetectedListener listener;
//    private final com.google.mlkit.vision.objects.ObjectDetector detector;
//
//    private Float lastCenterX = null;
//    private Float lastCenterY = null;
//    private long lastTimestamp = 0;
//    private Integer lastTrackingId = null;
//    private float lastSpeed = 0f;
//
//    public CarSpeedAnalyzer(OnCarDetectedListener listener) {
//        this.listener = listener;
//        this.detector = ObjectDetection.getClient(
//                new ObjectDetectorOptions.Builder()
//                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//                        .enableMultipleObjects()
//                        .enableClassification()
//                        .build()
//        );
//    }
//
//    @Override
//    public void analyze(@NonNull ImageProxy imageProxy) {
//        android.media.Image mediaImage = imageProxy.getImage();
//        if (mediaImage != null) {
//            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//            detector.process(image)
//                    .addOnSuccessListener(detectedObjects -> processObjects(detectedObjects, imageProxy))
//                    .addOnFailureListener(e -> imageProxy.close());
//        } else {
//            imageProxy.close();
//        }
//    }
//
//    private void processObjects(List<DetectedObject> objects, ImageProxy imageProxy) {
//        DetectedObject targetObject = null;
//        int maxArea = 0;
//        int imageWidth = imageProxy.getWidth();
//        int imageHeight = imageProxy.getHeight();
//        int minArea = (imageWidth * imageHeight) / 25;
//
//        for (DetectedObject obj : objects) {
//            int area = obj.getBoundingBox().width() * obj.getBoundingBox().height();
//            if (area > maxArea && area > minArea) {
//                maxArea = area;
//                targetObject = obj;
//            }
//        }
//
//        if (targetObject != null) {
//            Rect box = targetObject.getBoundingBox();
//            Integer currentId = targetObject.getTrackingId();
//            long currentTimestamp = System.currentTimeMillis();
//            float currentCenterX = box.centerX();
//            float currentCenterY = box.centerY();
//
//            float speed = 0f;
//
//            if (currentId != null && currentId.equals(lastTrackingId) && lastCenterX != null) {
//                float timeSec = (currentTimestamp - lastTimestamp) / 1000f;
//
//                if (timeSec > 0.02f && timeSec < 1.0f) {
//                    float pixelDisplacementX = currentCenterX - lastCenterX;
//                    float pixelDisplacementY = currentCenterY - lastCenterY;
//                    float totalPixelDisplacement = (float) Math.sqrt(
//                            (pixelDisplacementX * pixelDisplacementX) +
//                                    (pixelDisplacementY * pixelDisplacementY)
//                    );
//
//                    if (totalPixelDisplacement > 5f) {
//                        float pixelsPerMeter = 400f;
//                        float metersTraveled = totalPixelDisplacement / pixelsPerMeter;
//                        float speedMs = metersTraveled / timeSec;
//                        speed = speedMs * 3.6f;
//
//                        if (lastSpeed > 0) {
//                            speed = (lastSpeed * 0.7f) + (speed * 0.3f);
//                        }
//
//                        Log.d("SPEED_APP", String.format(
//                                "MOVING: disp=%.1fpx time=%.3fs speed=%.1f km/h",
//                                totalPixelDisplacement, timeSec, speed
//                        ));
//                    }
//                }
//            } else {
//                Log.d("SPEED_APP", "New object detected, ID: " + currentId);
//            }
//
//            lastCenterX = currentCenterX;
//            lastCenterY = currentCenterY;
//            lastTimestamp = currentTimestamp;
//            lastTrackingId = currentId;
//            lastSpeed = speed;
//
//            listener.onCarDetected(box, currentId, speed);
//        } else {
//            lastTrackingId = null;
//            lastCenterX = null;
//            lastCenterY = null;
//            lastSpeed = 0f;
//            listener.onCarDetected(null, null, 0f);
//        }
//
//        imageProxy.close();
//    }
//}

//package com.example.myfirsthelloworld;
//
//import android.graphics.Rect;
//import android.util.Log;
//
//import androidx.camera.core.ExperimentalGetImage;
//import androidx.camera.core.ImageAnalysis;
//import androidx.camera.core.ImageProxy;
//import androidx.annotation.NonNull;
//
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.objects.DetectedObject;
//import com.google.mlkit.vision.objects.ObjectDetection;
//import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
//
//import java.util.List;
//
//@ExperimentalGetImage
//public class CarSpeedAnalyzer implements ImageAnalysis.Analyzer {
//
//    public interface OnCarDetectedListener {
//        void onCarDetected(Rect boundingBox, Integer trackingId, float speedKmh);
//    }
//
//    private final OnCarDetectedListener listener;
//    private final com.google.mlkit.vision.objects.ObjectDetector detector;
//
//    private Float lastCenterX = null;
//    private Float lastCenterY = null;
//    private long lastTimestamp = 0;
//    private float lastSpeed = 0f;
//
//    // CALIBRATION - Adjust this number
//    private static final float PIXELS_PER_METER = 1000f;
//
//    public CarSpeedAnalyzer(OnCarDetectedListener listener) {
//        this.listener = listener;
//        this.detector = ObjectDetection.getClient(
//                new ObjectDetectorOptions.Builder()
//                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
//                        .enableMultipleObjects()
//                        .build()
//        );
//    }
//
//    @Override
//    public void analyze(@NonNull ImageProxy imageProxy) {
//        android.media.Image mediaImage = imageProxy.getImage();
//        if (mediaImage != null) {
//            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//            detector.process(image)
//                    .addOnSuccessListener(detectedObjects -> processObjects(detectedObjects, imageProxy))
//                    .addOnFailureListener(e -> {
//                        Log.e("SPEED_APP", "Failed: " + e.getMessage());
//                        imageProxy.close();
//                    });
//        } else {
//            imageProxy.close();
//        }
//    }
//
//    private void processObjects(List<DetectedObject> objects, ImageProxy imageProxy) {
//        DetectedObject targetObject = null;
//        int maxArea = 0;
//
//        // Find largest object
//        for (DetectedObject obj : objects) {
//            int area = obj.getBoundingBox().width() * obj.getBoundingBox().height();
//            if (area > maxArea) {
//                maxArea = area;
//                targetObject = obj;
//            }
//        }
//
//        if (targetObject != null) {
//            Rect box = targetObject.getBoundingBox();
//            long currentTimestamp = System.currentTimeMillis();
//            float currentCenterX = box.centerX();
//            float currentCenterY = box.centerY();
//
//            float speed = 0f;
//
//            // Calculate speed based on position change (IGNORE tracking ID)
//            if (lastCenterX != null && lastCenterY != null && lastTimestamp > 0) {
//                float timeSec = (currentTimestamp - lastTimestamp) / 1000f;
//
//                // Calculate total movement (both X and Y)
//                float dx = currentCenterX - lastCenterX;
//                float dy = currentCenterY - lastCenterY;
//                float totalDisplacement = (float) Math.sqrt((dx * dx) + (dy * dy));
//
//                Log.d("SPEED_APP", String.format(
//                        "Time:%.3fs Disp:%.1fpx dx:%.1f dy:%.1f",
//                        timeSec, totalDisplacement, dx, dy
//                ));
//
//                // Only calculate if enough time passed and object actually moved
//                if (timeSec > 0.03f && timeSec < 1.0f && totalDisplacement > 5f) {
//                    float metersTraveled = totalDisplacement / PIXELS_PER_METER;
//                    float speedMs = metersTraveled / timeSec;
//                    float newSpeed = speedMs * 3.6f;
//
//                    // Filter unrealistic speeds
//                    if (newSpeed > 0.3f && newSpeed < 150f) {
//                        // Smooth the speed
//                        if (lastSpeed > 0) {
//                            speed = (lastSpeed * 0.6f) + (newSpeed * 0.4f);
//                        } else {
//                            speed = newSpeed;
//                        }
//
//                        Log.d("SPEED_APP", ">>> SPEED: " + String.format("%.1f km/h", speed));
//                    }
//                }
//            }
//
//            // Update for next frame
//            lastCenterX = currentCenterX;
//            lastCenterY = currentCenterY;
//            lastTimestamp = currentTimestamp;
//
//            if (speed > 0) {
//                lastSpeed = speed;
//            }
//
//            // Display speed
//            float displaySpeed = (speed > 0) ? speed : lastSpeed;
//            listener.onCarDetected(box, null, displaySpeed);
//
//        } else {
//            // No object - keep showing last speed briefly then fade
//            if (lastSpeed < 0.5f) {
//                listener.onCarDetected(null, null, 0f);
//            } else {
//                lastSpeed = lastSpeed * 0.95f;
//                listener.onCarDetected(null, null, lastSpeed);
//            }
//        }
//
//        imageProxy.close();
//    }
//}



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
        void onCarDetected(Rect boundingBox, Integer trackingId, float speedKmh);
    }

    private final OnCarDetectedListener listener;
    private final com.google.mlkit.vision.objects.ObjectDetector detector;

    private Float lastCenterX = null;
    private Float lastCenterY = null;
    private long lastTimestamp = 0;
    private float lastSpeed = 0f;
    private Rect fixedBox = null;
    private boolean boxInitialized = false;

    private static final float PIXELS_PER_METER = 400f;

    public CarSpeedAnalyzer(OnCarDetectedListener listener) {
        this.listener = listener;
        this.detector = ObjectDetection.getClient(
                new ObjectDetectorOptions.Builder()
                        .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                        .enableMultipleObjects()
                        .build()
        );
    }

    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            if (!boxInitialized) {
                int width = imageProxy.getWidth();
                int height = imageProxy.getHeight();

                int boxSize = Math.min(width, height) * 95 / 100;
                int left = (width / 2) - (boxSize / 2) + 50;
                int top = (height / 2) - (boxSize / 2) + 500;
                int right = left + boxSize;
                int bottom = top + boxSize;

                fixedBox = new Rect(left, top, right, bottom);
                boxInitialized = true;

                Log.d("SPEED_APP", "📦 Screen: " + width + "x" + height);
                Log.d("SPEED_APP", "📦 BOX CENTER: " + left + "," + top + " to " + right + "," + bottom);
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
        float speed = 0f;

        // Find ANY object in the box - track its position
        float totalCenterX = 0;
        float totalCenterY = 0;
        int count = 0;

        for (DetectedObject obj : objects) {
            Rect objBox = obj.getBoundingBox();

            if (Rect.intersects(objBox, fixedBox)) {
                totalCenterX += objBox.centerX();
                totalCenterY += objBox.centerY();
                count++;
            }
        }

        if (count > 0) {
            float avgCenterX = totalCenterX / count;
            float avgCenterY = totalCenterY / count;

            if (lastCenterX != null && lastCenterY != null && lastTimestamp > 0) {
                float timeSec = (now - lastTimestamp) / 1000f;

                float dx = avgCenterX - lastCenterX;
                float dy = avgCenterY - lastCenterY;
                float totalDisplacement = (float) Math.sqrt((dx * dx) + (dy * dy));

                if (timeSec > 0.02f && timeSec < 1.0f && totalDisplacement > 2f) {
                    float metersTraveled = totalDisplacement / PIXELS_PER_METER;
                    float speedMs = metersTraveled / timeSec;
                    float newSpeed = speedMs * 3.6f;

                    if (newSpeed > 0.2f && newSpeed < 200f) {
                        if (lastSpeed > 0) {
                            speed = (lastSpeed * 0.5f) + (newSpeed * 0.5f);
                        } else {
                            speed = newSpeed;
                        }
                        lastSpeed = speed;

                        Log.d("SPEED_APP", "SPEED:" + String.format("%.1f", speed) +
                                " km/h | Disp:" + String.format("%.0f", totalDisplacement) +
                                "px | Time:" + String.format("%.3f", timeSec) + "s");
                    }
                }
            }

            lastCenterX = avgCenterX;
            lastCenterY = avgCenterY;
            lastTimestamp = now;

        } else {
            if (lastSpeed < 0.3f) {
                lastSpeed = 0f;
                lastCenterX = null;
                lastCenterY = null;
            } else {
                lastSpeed = lastSpeed * 0.85f;
            }
        }

        float displaySpeed = (speed > 0) ? speed : lastSpeed;
        listener.onCarDetected(fixedBox, null, displaySpeed);

        imageProxy.close();
    }
}