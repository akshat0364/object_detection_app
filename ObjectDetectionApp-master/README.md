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

### Installation & Setup

#### Method 1: Using Android Studio (Recommended)

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd ObjectDetectionApp-master
   ```

2. **Open in Android Studio**

   - Launch Android Studio
   - Select `File → Open`
   - Navigate to and select the project root directory

3. **Sync Gradle**

   - Android Studio will automatically prompt to sync
   - Or manually: `File → Sync Project with Gradle Files`
   - Wait for dependencies to download (first time may take several minutes)

4. **Connect your Android device or start an emulator**

   - Enable USB debugging on your device (Settings → Developer Options)
   - Or create an emulator: `Tools → Device Manager → Create Device`

5. **Run the application**
   - Click the green "Run" button (▶️) in the toolbar
   - Or press `Shift+F10` (Windows/Linux) / `Ctrl+R` (Mac)
   - Select your device/emulator when prompted

#### Method 2: Using Command Line

1. **Navigate to project directory**

   ```powershell
   cd "c:\path\to\ObjectDetectionApp-master"
   ```

2. **Build the debug APK**

   ```powershell
   .\gradlew.bat assembleDebug
   ```

   Output location: `app\build\outputs\apk\debug\app-debug.apk`

3. **Install on connected device**

   ```powershell
   .\gradlew.bat installDebug
   ```

4. **Alternative: Build release APK**
   ```powershell
   .\gradlew.bat assembleRelease
   ```
   Output location: `app\build\outputs\apk\release\app-release-unsigned.apk`

### Running the App

1. **Grant Camera Permission**

   - On first launch, the app will request camera permission
   - Tap "Allow" to enable object detection

2. **Start Detection**

   - Point your camera at objects
   - Detected objects will be highlighted with bounding boxes
   - Confidence scores are displayed for each detection

3. **Supported Objects**
   - 90+ classes including: person, bicycle, car, motorcycle, bottle, cup, fork, knife, banana, apple, laptop, mouse, keyboard, cell phone, microwave, oven, scissors, teddy bear, and more

## 🧪 Testing

Run unit tests:

```powershell
.\gradlew.bat test
```

Run instrumented tests (requires connected device):

```powershell
.\gradlew.bat connectedAndroidTest
```

## 🏗️ Building from Scratch

If you need to clean and rebuild:

```powershell
# Clean previous builds
.\gradlew.bat clean

# Build fresh
.\gradlew.bat assembleDebug

# Or build with dependency refresh
.\gradlew.bat clean build --refresh-dependencies
```

## 🧠 How It Works

### TensorFlow Lite Model

The app uses a pretrained **MobileNet SSD v1** quantized model optimized for mobile devices:

- Model file: `detect.tflite` (stored in `app/src/main/assets`)
- Labels: `labelmap.txt` (90 object classes)
- Input: 300x300 RGB image
- Output: Bounding boxes, class labels, and confidence scores
- **Trained on:** COCO dataset (Common Objects in Context)

**Official Model Sources:**

- Original model download: [COCO SSD MobileNet v1 Quantized](https://storage.googleapis.com/download.tensorflow.org/models/tflite/coco_ssd_mobilenet_v1_1.0_quant_2018_06_29.zip)
- TensorFlow Model Zoo: [TF1 Detection Zoo](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf1_detection_zoo.md)
- More TFLite models: [TensorFlow Lite Models](https://ai.google.dev/edge/litert/models/trained)

TensorFlow Lite provides a lightweight solution for running ML models on mobile devices without requiring a connection to cloud services.

> **Note:** The model files (`detect.tflite` and `labelmap.txt`) are already included in the project's `app/src/main/assets` folder. You only need to download the model if you want to replace it or use a different version.

### Architecture Overview

1. **Camera Feed:** `CameraActivity` and `CameraConnectionFragment` manage real-time camera input

2. **Object Detection:** `MobileNetObjDetector` wraps the TensorFlow Lite Interpreter:

   ```kotlin
   import org.tensorflow.lite.Interpreter

   private val tflite = Interpreter(loadModelFile(assetManager))
   tflite.runForMultipleInputsOutputs(inputArray, outputMap)
   ```

3. **Results Processing:** Converts model output to `DetectionResult` objects containing:

   - Detected class label
   - Confidence score (0-1)
   - Bounding box coordinates

4. **Visualization:** `OverlayView` renders bounding boxes and labels on top of the camera preview

## 🛠️ Troubleshooting

### Gradle Sync Issues

```powershell
.\gradlew.bat clean build --refresh-dependencies
```

### App Crashes on Launch

- Ensure camera permissions are granted
- Verify device meets minimum SDK requirements (API 24+)
- Check that `detect.tflite` and `labelmap.txt` exist in assets folder

### Model Not Found Error

The build script verifies model files exist. If you see this error:

- Ensure `app/src/main/assets/detect.tflite` exists
- Ensure `app/src/main/assets/labelmap.txt` exists

### Performance Issues

- Enable GPU acceleration (already configured in dependencies)
- Test on a physical device rather than emulator for better performance

## 📦 Dependencies

Key dependencies (see `app/build.gradle` for complete list):

- `androidx.appcompat:appcompat:1.6.1`
- `org.tensorflow:tensorflow-lite:2.14.0`
- `org.tensorflow:tensorflow-lite-gpu:2.14.0`
- `org.tensorflow:tensorflow-lite-support:0.4.4`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📝 License

See [license.txt](license.txt) for details.

## 🙏 Acknowledgments

- Based on [TensorFlow Lite Object Detection Example](https://github.com/tensorflow/examples/tree/master/lite/examples/object_detection)
- MobileNet SSD model from [TensorFlow Model Zoo](https://github.com/tensorflow/models/blob/master/research/object_detection/g3doc/tf1_detection_zoo.md)

## 📧 Support

For issues, questions, or contributions, please open an issue on GitHub.

## 📸 Results

Dining Table with Cups<br/>
<img src="results/cups.jpg" width="335" height="730" />

<br/><br/>TV<br/>
<img src="results/tv.jpg" width="335" height="730" />

<br/><br/>Microwave<br/>
<img src="results/microwave.jpg" width="335" height="730" />
