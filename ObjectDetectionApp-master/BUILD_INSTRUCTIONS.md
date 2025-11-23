# Build Instructions

## Prerequisites

1. **Android Studio**: Latest version (Hedgehog or later recommended)
2. **JDK**: Version 17 or higher
3. **Android SDK**:
   - Compile SDK: 35
   - Min SDK: 24
   - Build Tools: 35.0.0

## Build Steps

### Using Android Studio

1. **Open Project**

   ```
   File → Open → Select the project root directory
   ```

2. **Sync Gradle**

   - Android Studio will automatically prompt to sync
   - Or click: File → Sync Project with Gradle Files
   - Wait for dependencies to download

3. **Build Project**

   ```
   Build → Make Project
   ```

   Or use keyboard shortcut: `Ctrl+F9` (Windows/Linux) or `Cmd+F9` (Mac)

4. **Run on Device/Emulator**
   ```
   Run → Run 'app'
   ```
   Or use keyboard shortcut: `Shift+F10` (Windows/Linux) or `Ctrl+R` (Mac)

### Using Command Line

1. **Navigate to Project Directory**

   ```powershell
   cd "c:\Users\aksha\Downloads\ObjectDetectionApp-master\ObjectDetectionApp-master"
   ```

2. **Build Debug APK**

   ```powershell
   .\gradlew.bat assembleDebug
   ```

   Output: `app\build\outputs\apk\debug\app-debug.apk`

3. **Build Release APK**

   ```powershell
   .\gradlew.bat assembleRelease
   ```

   Output: `app\build\outputs\apk\release\app-release-unsigned.apk`

4. **Install on Connected Device**

   ```powershell
   .\gradlew.bat installDebug
   ```

5. **Run Tests**

   ```powershell
   # Unit tests
   .\gradlew.bat test

   # Instrumented tests (requires connected device/emulator)
   .\gradlew.bat connectedAndroidTest
   ```

6. **Clean Build**
   ```powershell
   .\gradlew.bat clean
   ```

## Troubleshooting

### Gradle Sync Issues

If Gradle sync fails:

```powershell
# Clean and rebuild
.\gradlew.bat clean build --refresh-dependencies
```

### SDK Issues

If SDK packages are missing:

1. Open Android Studio
2. Go to: Tools → SDK Manager
3. Install missing SDK packages as prompted

### Build Cache Issues

Clear build cache:

```powershell
.\gradlew.bat clean
Remove-Item -Recurse -Force .gradle, app\.gradle, app\build
```

### Kotlin Version Mismatch

If you see Kotlin version errors, ensure:

- Kotlin plugin version in root build.gradle matches
- Android Studio has the Kotlin plugin installed

## Running the App

### Permissions Required

The app requires the following runtime permissions:

- **Camera**: To capture video frames
- **Storage**: For potential data storage (Android 9 and below)

Grant these permissions when prompted on first launch.

### Device Requirements

- **Minimum**: Android 7.0 (API 24)
- **Target**: Android 15 (API 35)
- **Camera**: Device must have a rear-facing camera
- **Storage**: ~50MB free space for the app

## Expected Output

When running successfully:

1. App launches with camera permission request
2. Camera preview appears
3. Objects are detected in real-time
4. Bounding boxes and labels drawn over detected objects
5. Confidence scores displayed (only objects >50% confidence shown)

## Performance Notes

- **GPU Acceleration**: Enabled via TensorFlow Lite GPU delegate
- **Model**: MobileNet SSD for object detection
- **Input Size**: 300x300 pixels
- **Frame Processing**: Runs on background thread to avoid UI lag

## Additional Commands

### Generate APK with Signing

For release builds, you'll need to set up signing:

1. Create keystore (if you don't have one):

   ```powershell
   keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
   ```

2. Add to `app/build.gradle`:

   ```gradle
   android {
       signingConfigs {
           release {
               storeFile file("path/to/my-release-key.jks")
               storePassword "your-store-password"
               keyAlias "my-key-alias"
               keyPassword "your-key-password"
           }
       }
       buildTypes {
           release {
               signingConfig signingConfigs.release
               // ... other release configs
           }
       }
   }
   ```

3. Build signed APK:
   ```powershell
   .\gradlew.bat assembleRelease
   ```

### Generate AAB (Android App Bundle)

For Play Store distribution:

```powershell
.\gradlew.bat bundleRelease
```

Output: `app\build\outputs\bundle\release\app-release.aab`

## Support

If you encounter issues:

1. Check the error logs in Android Studio's Logcat
2. Ensure all prerequisites are met
3. Try invalidating cache: File → Invalidate Caches / Restart
4. Check the MIGRATION_SUMMARY.md for details on changes made

---

**Last Updated**: November 2025
**Kotlin Version**: 1.9.21
**Gradle Version**: 8.5
**Android Gradle Plugin**: 8.2.0
