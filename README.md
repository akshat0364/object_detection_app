# Object Detection App - TensorFlow Lite Android

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com/)
[![TensorFlow Lite](https://img.shields.io/badge/TensorFlow-Lite-orange.svg)](https://www.tensorflow.org/lite)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)

## Overview

Real-time object detection Android app using TensorFlow Lite with MobileNet SSD v1 quantized model. Detects 90+ object classes from the COCO dataset with bounding boxes and confidence scores.

**Features:** Real-time detection • Lightweight TFLite inference • GPU acceleration • 90+ COCO classes

## Tech Stack

- **Language:** Kotlin
- **Build:** Gradle 8.5 • Android Gradle Plugin 8.2.0
- **SDK:** Min 24 (Android 7.0) • Target 35 (Android 14) • Compile 35
- **Java:** JDK 17
- **ML:** TensorFlow Lite 2.14.0 (with GPU & Support libraries)
- **AndroidX:** AppCompat 1.6.1 • ConstraintLayout 2.1.4 • Core-KTX 1.12.0
- **Model:** MobileNet SSD v1 Quantized (COCO trained)

## Quick Start

### Prerequisites

- Android Studio (Hedgehog or later)
- JDK 17+
- Android device/emulator with API 24+ and camera

### Prerequisites

- Android Studio (Hedgehog or later)
- JDK 17+
- Android device/emulator with API 24+ and camera

### Setup

**Using Android Studio:**

1. Clone the repository and open in Android Studio
2. Sync Gradle files (File → Sync Project with Gradle Files)
3. Connect device or start emulator
4. Click Run (▶️) or press `Shift+F10`

**Using Command Line:**

```powershell
# Build debug APK
.\gradlew.bat assembleDebug

# Install on device
.\gradlew.bat installDebug
```

### Usage

1. Grant camera permission when prompted
2. Point camera at objects
3. View detected objects with bounding boxes and confidence scores

**Detectable objects:** person, bicycle, car, motorcycle, bottle, cup, fork, knife, banana, laptop, mouse, keyboard, microwave, scissors, and 75+ more COCO classes.

## Architecture

```
MainActivity (Camera2 API)
    ↓
CameraActivity + CameraConnectionFragment (Camera management)
    ↓
MobileNetObjDetector (TFLite Interpreter wrapper)
    ↓
DetectionResult (Class, confidence, bbox)
    ↓
OverlayView (Render bounding boxes)
```

**Model Details:**

- Input: 300×300 RGB image
- Output: Bounding boxes, class labels, confidence scores
- Model files: `app/src/main/assets/detect.tflite` + `labelmap.txt`

**Official Sources:**

- Download: [COCO SSD MobileNet v1](https://storage.googleapis.com/download.tensorflow.org/models/tflite/coco_ssd_mobilenet_v1_1.0_quant_2018_06_29.zip)
- Model Zoo: [TF1 Detection Models](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf1_detection_zoo.md)

## Testing & Building

```powershell
# Run tests
.\gradlew.bat test
.\gradlew.bat connectedAndroidTest

# Clean build
.\gradlew.bat clean build --refresh-dependencies
```

## Troubleshooting

**Gradle sync fails:** Run `.\gradlew.bat clean build --refresh-dependencies`  
**App crashes:** Check camera permissions and verify model files exist in `app/src/main/assets/`  
**Slow performance:** Test on physical device; GPU acceleration is enabled by default

## Additional Documentation

- [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) - Detailed build guide
- [MIGRATION_SUMMARY.md](MIGRATION_SUMMARY.md) - Project update history

## License & Acknowledgments

See [license.txt](license.txt). Based on [TensorFlow Lite Object Detection Example](https://github.com/tensorflow/examples/tree/master/lite/examples/object_detection).

## Sample Results

<img src="results/cups.jpg" width="250" height="544" alt="Dining table with cups detected" /> <img src="results/tv.jpg" width="250" height="544" alt="TV detected" /> <img src="results/microwave.jpg" width="250" height="544" alt="Microwave detected" />
