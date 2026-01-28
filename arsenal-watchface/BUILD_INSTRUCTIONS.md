# Build Instructions
## Arsenal FC 2025/26 Watch Face for Samsung Galaxy Watch 6

This guide walks you through building and installing the watch face on your personal device.

---

## Prerequisites

### 1. Install Android Studio

Download and install [Android Studio](https://developer.android.com/studio) (Hedgehog or newer).

### 2. Install Required SDK Components

In Android Studio, go to **Tools > SDK Manager** and install:

- **SDK Platforms:**
  - Android 14.0 (API 34)
  - Android 13.0 (API 33) - for Wear OS

- **SDK Tools:**
  - Android SDK Build-Tools 34
  - Android SDK Command-line Tools
  - Android SDK Platform-Tools

### 3. Install Wear OS Emulator (Optional)

In **Tools > Device Manager**, create a new device:
- Select **Wear OS** category
- Choose **Wear OS Large Round** (API 33+)

---

## Building the Watch Face

### Step 1: Open Project

1. Open Android Studio
2. Select **File > Open**
3. Navigate to the `arsenal-watchface` folder
4. Click **Open**

### Step 2: Sync Gradle

Android Studio should automatically sync Gradle files. If not:
- Click **File > Sync Project with Gradle Files**
- Wait for the sync to complete

### Step 3: Build APK

**Option A: Debug Build (for testing)**
```bash
./gradlew assembleDebug
```

**Option B: Release Build (for installation)**
```bash
./gradlew assembleRelease
```

The APK will be generated at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Step 4: Sign the APK (Release builds only)

For release builds, you need to sign the APK:

1. Generate a keystore (first time only):
```bash
keytool -genkey -v -keystore arsenal-watchface.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias arsenal
```

2. Sign the APK:
```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 -keystore arsenal-watchface.keystore app/build/outputs/apk/release/app-release-unsigned.apk arsenal
```

3. Align the APK:
```bash
zipalign -v 4 app/build/outputs/apk/release/app-release-unsigned.apk arsenal-watchface.apk
```

---

## Installing on Galaxy Watch 6

### Method 1: ADB Sideload (Recommended)

#### Enable Developer Options on Watch

1. On your watch, go to **Settings > About watch > Software**
2. Tap **Software version** 5 times
3. Developer mode is now enabled

#### Enable ADB Debugging

1. Go to **Settings > Developer options**
2. Enable **ADB debugging**
3. Enable **Debug over Wi-Fi** (note the IP address shown)

#### Connect and Install

1. On your computer, connect via ADB:
```bash
adb connect <watch-ip-address>:5555
```

2. Verify connection:
```bash
adb devices
```

3. Install the APK:
```bash
adb install arsenal-watchface.apk
```

4. The watch face will appear in your watch face gallery.

### Method 2: Wireless Debugging (Android 11+)

1. Enable **Wireless debugging** in Developer options
2. On the watch, tap **Pair new device**
3. On your computer:
```bash
adb pair <watch-ip>:<pairing-port>
# Enter the pairing code shown on watch

adb connect <watch-ip>:<connection-port>
adb install arsenal-watchface.apk
```

### Method 3: Via Phone (Galaxy Wearable App)

1. Transfer the APK to your phone
2. Open a file manager and locate the APK
3. The Galaxy Wearable app should offer to install it on your watch
4. Accept the installation prompt on your watch

---

## Selecting the Watch Face

After installation:

1. Long-press on your current watch face
2. Swipe to find **Arsenal 25/26**
3. Tap to select
4. Customize complications as desired

---

## Setting Up Match Day Feature

The Match Day Dynamic Complication requires a companion phone app to sync match data. For personal use without the companion app:

### Option 1: Manual Complication

The watch face will show fitness stats (Steps, Heart Rate) by default when no match data is available.

### Option 2: Use Third-Party Football App

Configure the match section complication slot to use a third-party football scores app that provides Wear OS complications.

### Option 3: Build Companion App (Advanced)

See `docs/LOGIC_ARCHITECTURE.md` for the companion app architecture. You would need to:

1. Create an Android phone app
2. Register for [football-data.org](https://www.football-data.org/) API (free tier)
3. Implement the data sync service
4. Install both apps

---

## Troubleshooting

### Build Errors

**"SDK location not found"**
- Create a `local.properties` file in the project root:
```properties
sdk.dir=/path/to/your/Android/Sdk
```

**"Failed to find target"**
- Install the required SDK versions via SDK Manager

**Gradle sync fails**
- Try **File > Invalidate Caches and Restart**
- Delete `.gradle` folder and re-sync

### Installation Errors

**"INSTALL_FAILED_UPDATE_INCOMPATIBLE"**
- Uninstall the previous version first:
```bash
adb uninstall com.arsenal.watchface
```

**"INSTALL_FAILED_VERIFICATION_FAILURE"**
- Disable app verification:
```bash
adb shell settings put global verifier_verify_adb_installs 0
```

**Watch face doesn't appear**
- Reboot the watch
- Check that the APK installed successfully:
```bash
adb shell pm list packages | grep arsenal
```

### Watch Face Issues

**Time not updating**
- Force stop and restart the watch face service
- Reboot the watch

**Complications not loading**
- Grant necessary permissions (Body Sensors, Activity Recognition)
- Re-add the complication in watch face settings

---

## Updating the Watch Face

To install an updated version:

```bash
adb install -r arsenal-watchface.apk
```

The `-r` flag replaces the existing installation while preserving data.

---

## Sharing via Google Drive

To install on your watch using Google Drive:

1. Upload `arsenal-watchface.apk` to Google Drive
2. On your phone, open Google Drive and download the APK
3. Use the Galaxy Wearable app or Wear Installer app to push to watch

Alternatively, use [Wear OS APK Installer](https://play.google.com/store/apps/details?id=com.paget96.wearinstaller) app.

---

## File Structure Reference

```
arsenal-watchface/
├── app/
│   ├── build.gradle.kts          # App build config
│   ├── proguard-rules.pro        # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml   # App manifest
│       ├── kotlin/               # Kotlin source files
│       │   └── com/arsenal/watchface/
│       │       ├── complications/
│       │       │   └── ArsenalMatchDataSource.kt
│       │       ├── data/
│       │       │   ├── MatchModels.kt
│       │       │   └── WearableDataListenerService.kt
│       │       └── utils/
│       │           └── BootReceiver.kt
│       └── res/
│           ├── drawable/         # Vector assets
│           ├── raw/
│           │   └── watchface.xml # WFF definition
│           ├── values/
│           │   ├── colors.xml
│           │   └── strings.xml
│           └── xml/
│               └── watch_face_info.xml
├── build.gradle.kts              # Root build config
├── settings.gradle.kts           # Gradle settings
├── gradle.properties             # Gradle properties
└── BUILD_INSTRUCTIONS.md         # This file
```

---

*Build Instructions v1.0*
*For personal use only*
