# Arsenal FC 2025/26 Watch Face

**Samsung Galaxy Watch 6 | Wear OS 5/6 | One UI 8**

A premium watch face inspired by the Arsenal FC 2025/2026 Third Kit, featuring an intelligent Match Day Dynamic Complication that automatically transforms on Arsenal match days.

> **For Personal Use Only** - This project is not affiliated with Arsenal FC.

---

## Quick Start

```bash
# Build the APK
./gradlew assembleDebug

# Install on connected watch
adb install app/build/outputs/apk/debug/app-debug.apk
```

See [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) for detailed setup guide.

---

## Features

### Visual Design
- **Deep Midnight Navy** base with vibrant aqua/pink neon accents
- **Gold/Copper** metallic Cannon crest at 12 o'clock
- Subtle **jersey fabric texture** background
- **Arsenal Sans** inspired typography

### Match Day Intelligence
- **Automatic detection** of Arsenal match days
- **Pre-match**: Shows opponent, kick-off time (auto timezone converted)
- **Live match**: Real-time score, minute indicator, LIVE badge
- **Post-match**: Final score with result indicator

### Non-Match Day
- Fitness stats: Steps, Heart Rate
- Next calendar event
- Seamless transition between modes

### Always On Display (AOD)
- Ultra-minimalist OLED-optimized design
- Cannon crest outline + hollow time
- Match day notification preserved
- Burn-in prevention with pixel shifting

### Complications
- **Weather** (10 o'clock) - Temperature with UV progress ring
- **Battery** (2 o'clock) - Percentage with color-coded status
- **Heart Rate** (8 o'clock) - BPM with pulse animation

---

## Project Structure

```
arsenal-watchface/
├── app/
│   ├── build.gradle.kts              # App build configuration
│   ├── proguard-rules.pro            # ProGuard rules
│   └── src/main/
│       ├── AndroidManifest.xml       # App manifest
│       ├── kotlin/com/arsenal/watchface/
│       │   ├── complications/
│       │   │   └── ArsenalMatchDataSource.kt
│       │   ├── data/
│       │   │   ├── MatchModels.kt
│       │   │   └── WearableDataListenerService.kt
│       │   └── utils/
│       │       └── BootReceiver.kt
│       └── res/
│           ├── drawable/             # Vector icons & backgrounds
│           ├── raw/
│           │   └── watchface.xml     # WFF 2.0 definition
│           ├── values/
│           │   ├── colors.xml        # Color palette
│           │   └── strings.xml       # String resources
│           └── xml/
│               └── watch_face_info.xml
├── docs/
│   ├── DESIGN_SPECIFICATION.md       # Complete visual design spec
│   └── LOGIC_ARCHITECTURE.md         # Match day system architecture
├── assets/
│   └── ASSETS_MANIFEST.md            # Visual assets specification
├── build.gradle.kts                  # Root build config
├── settings.gradle.kts               # Gradle settings
├── gradle.properties                 # Gradle properties
├── BUILD_INSTRUCTIONS.md             # Build & install guide
└── README.md                         # This file
```

---

## Technical Stack

| Component | Technology |
|-----------|------------|
| Watch Face Format | WFF 2.0+ |
| Platform | Wear OS 5/6 with One UI 8 |
| Language | Kotlin 1.9 |
| Min SDK | 30 (Android 11) |
| Target SDK | 34 (Android 14) |
| Resolution | 450 x 450px (Galaxy Watch 6) |

---

## Color Palette

| Name | Hex | Usage |
|------|-----|-------|
| Deep Midnight Navy | `#0A1628` | Primary background |
| Vibrant Aqua | `#00D4AA` | Primary accent |
| Pink Neon | `#FF2D78` | Secondary accent, HR |
| Cannon Gold | `#D4A853` | Logo, indices |

---

## Match Day States

```
NO_MATCH ──────▶ MATCH_UPCOMING ──────▶ MATCH_LIVE
    │                                       │
    │                                       ▼
    │                               MATCH_HALFTIME
    │                                       │
    │                                       ▼
    └◀──────────── MATCH_ENDED ◀───────────┘
```

---

## Installation

### Prerequisites
- Android Studio (Hedgehog+)
- Galaxy Watch 6 with Developer Mode enabled
- ADB installed

### Build & Install

1. **Clone and open in Android Studio**

2. **Build the APK:**
   ```bash
   ./gradlew assembleDebug
   ```

3. **Connect to your watch via ADB:**
   ```bash
   adb connect <watch-ip>:5555
   ```

4. **Install:**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

5. **Select the watch face** in your watch face gallery

For detailed instructions including Google Drive installation, see [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md).

---

## Documentation

| Document | Description |
|----------|-------------|
| [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md) | Step-by-step build and install guide |
| [DESIGN_SPECIFICATION.md](docs/DESIGN_SPECIFICATION.md) | Complete visual design, layout, colors, typography |
| [LOGIC_ARCHITECTURE.md](docs/LOGIC_ARCHITECTURE.md) | Match day system, state machine, data flow |
| [ASSETS_MANIFEST.md](assets/ASSETS_MANIFEST.md) | All required visual assets with specs |

---

## License

**For personal use only.** Arsenal FC trademarks are the property of Arsenal Football Club. This project is not affiliated with or endorsed by Arsenal FC.

---

*Created: January 2026*
*Platform: Samsung Galaxy Watch 6*
*Watch Face Format: WFF 2.0*
