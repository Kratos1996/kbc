# Android Build Instructions (Run Locally)

Prerequisites:
- JDK 17 (e.g., Microsoft Build of OpenJDK 17, Temurin 17, or Oracle JDK 17)
- Android SDK (API 34 / Android 14) — installed via Android Studio or `sdkmanager`
- `ANDROID_HOME` or `ANDROID_SDK_ROOT` environment variable set to your SDK path

## One-time setup

```powershell
# From the android/ folder
cd android

# Generate Gradle wrapper (once)
gradle wrapper --gradle-version 8.9

# Create local.properties with your SDK path
# Example paths:
#   Windows: C:\Users\<you>\AppData\Local\Android\Sdk
#   macOS:   ~/Library/Android/sdk
#   Linux:   ~/Android/Sdk
echo "sdk.dir=C:\Users\$env:USERNAME\AppData\Local\Android\Sdk" > local.properties
# OR edit local.properties manually

# Optional: override API base URL for emulator (default is http://10.0.2.2:4000)
# echo "API_BASE_URL=http://10.0.2.2:4000" >> local.properties
```

## Build & Run

```powershell
cd android

# Debug APK (installable on device/emulator)
.\gradlew.bat :app:assembleDebug

# Run lint
.\gradlew.bat :app:lintDebug

# Install on connected device/emulator
.\gradlew.bat :app:installDebug
```

## Open in Android Studio

1. File → Open → select the `android/` folder
2. Let Gradle sync finish
3. Run ▶ on an emulator or physical device

## Notes

- The app expects the backend running at `http://10.0.2.2:4000` (emulator localhost tunnel) by default.
- For a physical device, change `API_BASE_URL` in `local.properties` to your machine's LAN IP (e.g., `http://192.168.1.42:4000`).
- Firebase `google-services.json` is already at `app/google-services.json` (gitignored).
- Release signing: create `keystore.properties` with `storeFile`, `storePassword`, `keyAlias`, `keyPassword` and uncomment the `signingConfigs` block in `app/build.gradle.kts`.
