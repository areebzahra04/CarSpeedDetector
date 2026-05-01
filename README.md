# Car Speed Detector

A real-time Android application that detects vehicle speed using the phone camera and Google ML Kit's on-device Object Detection API.

## Features

- Real-time camera feed using CameraX
- Vehicle detection with bounding box overlay
- Speed calculation based on pixel displacement over time
- On-device processing (no internet required)
- Speed display in km/h

## Tech Stack

- **Language:** Java
- **Camera:** Android CameraX
- **Object Detection:** Google ML Kit (Object Detection & Tracking API)
- **Minimum SDK:** API 24 (Android 7.0)

## How It Works

1. CameraX captures a continuous video stream
2. ML Kit's Object Detection API identifies vehicles in each frame using `STREAM_MODE`
3. A tracking ID is assigned to each detected object
4. The app calculates the center position of the bounding box
5. By measuring pixel displacement of the same object across frames and dividing by elapsed time, speed is calculated
6. A calibration factor (`pixelsPerMeter`) converts pixel movement to real-world distance

## How to Run
Build → Run (or press Shift + F10)
