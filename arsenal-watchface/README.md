# Arsenal FC 2025/26 Watch Face

**Samsung Galaxy Watch 6 | Wear OS 5/6 | One UI 8**

A premium watch face inspired by the Arsenal FC 2025/2026 Third Kit, featuring an intelligent Match Day Dynamic Complication that automatically transforms on Arsenal match days.

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
├── docs/
│   ├── DESIGN_SPECIFICATION.md    # Complete visual design spec
│   └── LOGIC_ARCHITECTURE.md      # Match day system architecture
├── wff/
│   └── watchface.xml              # Watch Face Format implementation
├── assets/
│   └── ASSETS_MANIFEST.md         # Visual assets specification
├── src/
│   └── ArsenalMatchDataSource.kt  # Complication data source
└── README.md
```

---

## Technical Stack

| Component | Technology |
|-----------|------------|
| Watch Face Format | WFF 2.0+ |
| Platform | Wear OS 5/6 with One UI 8 |
| Resolution | 450 x 450px (Galaxy Watch 6) |
| Match Data | football-data.org API |
| Data Sync | Wear Data Layer API |
| Background Sync | WorkManager |

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

## Setup Requirements

### Development
1. Android Studio with Wear OS SDK
2. Samsung Watch Face Studio (optional, for visual preview)
3. Galaxy Watch 6 or emulator

### API Configuration
1. Register at [football-data.org](https://www.football-data.org/)
2. Obtain API key (free tier: 10 calls/minute)
3. Add to `local.properties`:
   ```
   FOOTBALL_API_KEY=your_api_key_here
   ```

### Building
```bash
./gradlew assembleRelease
```

---

## Documentation

| Document | Description |
|----------|-------------|
| [DESIGN_SPECIFICATION.md](docs/DESIGN_SPECIFICATION.md) | Complete visual design, layout, colors, typography |
| [LOGIC_ARCHITECTURE.md](docs/LOGIC_ARCHITECTURE.md) | Match day system, state machine, data flow |
| [ASSETS_MANIFEST.md](assets/ASSETS_MANIFEST.md) | All required visual assets with specs |

---

## License

Design specification for educational purposes. Arsenal FC trademarks are property of Arsenal Football Club. This project is not affiliated with or endorsed by Arsenal FC.

---

*Created: January 2026*
*Platform: Samsung Galaxy Watch 6*
*Watch Face Format: WFF 2.0*
