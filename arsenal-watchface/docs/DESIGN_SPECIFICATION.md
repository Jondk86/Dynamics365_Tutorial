# Arsenal FC 2025/26 Third Kit Watch Face
## Samsung Galaxy Watch 6 - Design Specification

**Version:** 1.0.0
**Target Platform:** Wear OS 5/6 with One UI 8
**Watch Face Format:** WFF 2.0+
**Resolution:** 450 x 450px (Galaxy Watch 6)

---

## 1. Design Philosophy

### 1.1 Theme Statement
A premium, minimalist watch face inspired by the leaked Arsenal FC 2025/2026 Third Kit aesthetic. The design balances aggressive sports branding with luxury watch craftsmanship, creating a piece that Arsenal supporters can wear both on match days and in professional settings.

### 1.2 Design Principles
- **Minimalist Aggression**: Clean lines with bold accent punctuation
- **Premium Feel**: Subtle textures and metallic accents
- **Intelligent Adaptivity**: Context-aware complications that transform for match days
- **Battery Conscious**: Optimized AOD mode with OLED-friendly design

---

## 2. Color Palette

### 2.1 Primary Colors

| Color Name | Hex Code | RGB | Usage |
|------------|----------|-----|-------|
| Deep Midnight Navy | `#0A1628` | 10, 22, 40 | Base background, primary surface |
| Arsenal Navy Dark | `#061020` | 6, 16, 32 | AOD background, shadows |
| Midnight Shadow | `#152238` | 21, 34, 56 | Texture overlay, depth layers |

### 2.2 Accent Colors

| Color Name | Hex Code | RGB | Usage |
|------------|----------|-----|-------|
| Vibrant Aqua | `#00D4AA` | 0, 212, 170 | Primary accent, active states |
| Electric Aqua | `#00FFD0` | 0, 255, 208 | Glow effects, highlights |
| Pink Neon | `#FF2D78` | 255, 45, 120 | Secondary accent, alerts |
| Hot Pink | `#FF0066` | 255, 0, 102 | Match live indicator |

### 2.3 Metallic Accents

| Color Name | Hex Code | RGB | Usage |
|------------|----------|-----|-------|
| Cannon Gold | `#D4A853` | 212, 168, 83 | Cannon logo, index markers |
| Copper Highlight | `#E8B87D` | 232, 184, 125 | Logo highlights, shine effects |
| Bronze Shadow | `#8B6914` | 139, 105, 20 | Logo shadows, depth |

### 2.4 Utility Colors

| Color Name | Hex Code | RGB | Usage |
|------------|----------|-----|-------|
| Pure White | `#FFFFFF` | 255, 255, 255 | Time display, primary text |
| Silver Grey | `#A0A8B4` | 160, 168, 180 | Secondary text, complications |
| Dim Grey | `#4A5568` | 74, 85, 104 | Inactive states, AOD elements |

---

## 3. Typography

### 3.1 Font Family: Arsenal Sans (Custom)

Since Arsenal Sans is proprietary, we recommend the following alternatives that match the geometric, bold aesthetic:

**Primary Font Stack:**
```
1. Arsenal Sans (if licensed)
2. Proxima Nova Bold
3. Montserrat Bold
4. Roboto Condensed Bold (fallback)
```

### 3.2 Type Scale

| Element | Size | Weight | Letter Spacing |
|---------|------|--------|----------------|
| Time (Hours) | 72sp | Bold | -2% |
| Time (Minutes) | 72sp | Bold | -2% |
| Time (Seconds) | 24sp | Medium | 0 |
| Date | 14sp | Medium | 5% |
| Complication Title | 10sp | Bold | 10% |
| Complication Value | 16sp | Bold | 0 |
| Match Info | 12sp | Bold | 2% |
| Team Names | 14sp | Bold | 0 |

### 3.3 Text Rendering
- **Anti-aliasing**: Subpixel rendering enabled
- **Shadow**: 1px blur, 50% opacity `#000000` for legibility
- **Ambient mode**: Reduce to outline/hollow rendering

---

## 4. Visual Layout

### 4.1 Active Mode Layout (450 x 450px)

```
┌─────────────────────────────────────────┐
│                                         │
│            ◆ CANNON CREST ◆             │  ← 12 o'clock (40px height)
│               (Gold/Copper)             │
│                                         │
│    ┌─────┐              ┌─────┐         │
│    │WTHR │              │ BAT │         │  ← Complications (50px diameter)
│    └─────┘              └─────┘         │
│         10:37                           │  ← Primary Time Display
│                                         │
│              TUE 28                     │  ← Date Display
│                                         │
│    ┌─────┐                              │
│    │ HR  │                              │  ← Heart Rate Complication
│    └─────┘                              │
│                                         │
│  ╔═══════════════════════════════════╗  │
│  ║    DYNAMIC MATCH DAY SECTION      ║  │  ← Lower third (120px height)
│  ║    ─────────────────────────      ║  │
│  ║    [FITNESS] or [MATCH INFO]      ║  │
│  ╚═══════════════════════════════════╝  │
│                                         │
└─────────────────────────────────────────┘
```

### 4.2 Grid System

- **Canvas**: 450 x 450px
- **Safe Zone**: 20px margin from edges
- **Center Point**: 225, 225
- **Complication Ring**: 170px radius from center

### 4.3 Element Positioning

| Element | X | Y | Width | Height |
|---------|---|---|-------|--------|
| Cannon Crest | 225 (center) | 55 | 60 | 40 |
| Weather Comp | 90 | 145 | 50 | 50 |
| Battery Comp | 360 | 145 | 50 | 50 |
| Time Display | 225 (center) | 225 | 280 | 80 |
| Date Display | 225 (center) | 275 | 100 | 20 |
| Heart Rate Comp | 90 | 305 | 50 | 50 |
| Dynamic Section | 225 (center) | 385 | 320 | 100 |

---

## 5. Background Design

### 5.1 Texture Specification

**Layer 1 - Base Gradient:**
```css
background: radial-gradient(
  ellipse at 50% 30%,
  #152238 0%,
  #0A1628 50%,
  #061020 100%
);
```

**Layer 2 - Fabric Texture:**
- Pattern: Diagonal micro-weave (45° angle)
- Line spacing: 2px
- Line color: `#1A2D45` at 15% opacity
- Creates subtle jersey fabric effect

**Layer 3 - Noise Overlay:**
- Monochromatic noise at 3% opacity
- Adds depth and prevents color banding

**Layer 4 - Vignette:**
- Radial gradient from transparent center to `#000000` at 40% opacity
- Radius: 90% of canvas width

### 5.2 Cannon Crest Design

**Dimensions:** 60 x 40px at 12 o'clock
**Style:** Simplified vector with metallic gradient

**Gradient Specification:**
```css
.cannon-crest {
  fill: linear-gradient(
    135deg,
    #E8B87D 0%,    /* Copper Highlight */
    #D4A853 40%,   /* Cannon Gold */
    #8B6914 100%   /* Bronze Shadow */
  );
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.5));
}
```

---

## 6. Circular Complications Design

### 6.1 Complication Style Guide

**Container:**
- Diameter: 50px
- Background: `#0A1628` at 80% opacity
- Border: 1px solid `#00D4AA` at 30% opacity
- Border radius: 50%

**Icon:**
- Size: 18 x 18px
- Color: `#00D4AA` (Vibrant Aqua)
- Position: Centered, top portion

**Value:**
- Font: Arsenal Sans Bold, 14sp
- Color: `#FFFFFF`
- Position: Centered, bottom portion

### 6.2 Weather Complication (Position: 10 o'clock)

```xml
<Complication>
  <Icon resource="weather_icon" />
  <Temperature>
    <Value format="%d°" />
    <Color>#FFFFFF</Color>
  </Temperature>
  <Ring>
    <Progress source="UV_INDEX" max="11" />
    <Color>#00D4AA</Color>
  </Ring>
</Complication>
```

### 6.3 Battery Complication (Position: 2 o'clock)

```xml
<Complication>
  <Icon resource="battery_icon" />
  <Percentage>
    <Value format="%d%%" />
    <Color dynamic="true">
      <Above75>#00D4AA</Above75>
      <Above25>#D4A853</Above25>
      <Below25>#FF2D78</Below25>
    </Color>
  </Percentage>
  <Ring>
    <Progress source="BATTERY_PERCENT" max="100" />
    <GradientColors>#00D4AA, #00FFD0</GradientColors>
  </Ring>
</Complication>
```

### 6.4 Heart Rate Complication (Position: 8 o'clock)

```xml
<Complication>
  <Icon resource="heart_icon" animated="pulse" />
  <BPM>
    <Value format="%d" suffix="BPM" />
    <Color>#FF2D78</Color>
  </BPM>
  <Ring>
    <Progress source="HEART_RATE" min="40" max="200" />
    <Color>#FF2D78</Color>
  </Ring>
</Complication>
```

---

## 7. Dynamic Match Day Section

### 7.1 State Machine

```
┌──────────────────────────────────────────────────────────┐
│                    STATE MACHINE                          │
├──────────────────────────────────────────────────────────┤
│                                                          │
│  ┌─────────────┐                      ┌─────────────┐   │
│  │   DEFAULT   │ ──match_today()───▶  │  MATCH_DAY  │   │
│  │  (Fitness)  │ ◀──no_match()─────   │  (Preview)  │   │
│  └─────────────┘                      └──────┬──────┘   │
│                                              │          │
│                                     match_started()     │
│                                              │          │
│                                              ▼          │
│                                       ┌─────────────┐   │
│                                       │  MATCH_LIVE │   │
│                                       │  (In-Game)  │   │
│                                       └──────┬──────┘   │
│                                              │          │
│                                      match_ended()      │
│                                              │          │
│                                              ▼          │
│                                       ┌─────────────┐   │
│                                       │   RESULT    │   │
│                                       │  (Final)    │   │
│                                       └─────────────┘   │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### 7.2 Non-Match Day Display (DEFAULT State)

**Layout:**
```
╔═══════════════════════════════════════════════════════╗
║  ┌────────────┐   ┌────────────┐   ┌────────────┐    ║
║  │   STEPS    │   │    HR      │   │   EVENT    │    ║
║  │   8,432    │   │   72 BPM   │   │ Meeting    │    ║
║  │   ▓▓▓▓▓░░  │   │   ♥ ~~~    │   │   14:30    │    ║
║  └────────────┘   └────────────┘   └────────────┘    ║
╚═══════════════════════════════════════════════════════╝
```

**Data Sources:**
- Steps: `STEP_COUNT` from Health Services
- Heart Rate: `HEART_RATE` from Health Services
- Calendar: `NEXT_EVENT` from Calendar Provider

### 7.3 Match Day Display (MATCH_DAY State)

**Layout:**
```
╔═══════════════════════════════════════════════════════╗
║                    ⚽ MATCH DAY                        ║
║  ┌─────────────────────────────────────────────────┐  ║
║  │    [ARS CREST]  vs  [LIV CREST]                │  ║
║  │     ARSENAL         LIVERPOOL                   │  ║
║  │                                                  │  ║
║  │         ⏱️ KICK-OFF: 17:30                      │  ║
║  │            Emirates Stadium                      │  ║
║  └─────────────────────────────────────────────────┘  ║
╚═══════════════════════════════════════════════════════╝
```

### 7.4 Live Match Display (MATCH_LIVE State)

**Layout:**
```
╔═══════════════════════════════════════════════════════╗
║  ●  LIVE  ●                           45+2'          ║
║  ┌─────────────────────────────────────────────────┐  ║
║  │    [ARS]            2 - 1           [LIV]       │  ║
║  │   ARSENAL                        LIVERPOOL      │  ║
║  │                                                  │  ║
║  │   ⚽ Saka 23'    │    ⚽ Salah 31'              │  ║
║  │   ⚽ Havertz 44' │                               │  ║
║  └─────────────────────────────────────────────────┘  ║
╚═══════════════════════════════════════════════════════╝
```

**Animation:** "LIVE" indicator pulses with `#FF0066` glow

---

## 8. AOD (Always On Display) Mode

### 8.1 Design Philosophy

- **Ultra-minimalist**: Maximum 15% pixel illumination
- **Burn-in prevention**: Subtle 2px position shift every minute
- **Battery optimized**: No animations, limited colors

### 8.2 AOD Layout

```
┌─────────────────────────────────────────┐
│                                         │
│                                         │
│            ◇ CANNON ◇                   │  ← Outline only, #4A5568
│             (Outline)                   │
│                                         │
│                                         │
│              10:37                      │  ← Hollow text, #A0A8B4
│                                         │
│                                         │
│                                         │
│          ⚽ MATCH TODAY                 │  ← Only if match day
│             17:30                       │     #00D4AA dimmed to 40%
│                                         │
│                                         │
└─────────────────────────────────────────┘
```

### 8.3 AOD Color Restrictions

| Element | Active Color | AOD Color |
|---------|--------------|-----------|
| Cannon Crest | Gradient Gold | Outline `#4A5568` |
| Time | `#FFFFFF` solid | `#A0A8B4` hollow |
| Match Indicator | `#00D4AA` | `#00D4AA` at 40% |
| Background | Navy gradient | Pure `#000000` |

### 8.4 Burn-in Prevention

```xml
<BurnInPrevention>
  <Enabled>true</Enabled>
  <ShiftInterval>60000</ShiftInterval> <!-- 1 minute -->
  <MaxShift>2</MaxShift> <!-- 2 pixels -->
  <Pattern>RANDOM</Pattern>
</BurnInPrevention>
```

---

## 9. Visual Assets Manifest

### 9.1 Required Image Assets

| Asset ID | Filename | Dimensions | Format | Description |
|----------|----------|------------|--------|-------------|
| cannon_crest | cannon_crest.webp | 120x80 | WebP | Gold gradient cannon logo |
| cannon_crest_aod | cannon_crest_aod.webp | 120x80 | WebP | Outline version for AOD |
| background_texture | bg_texture.webp | 450x450 | WebP | Fabric texture background |
| weather_clear | ic_weather_clear.webp | 36x36 | WebP | Weather icon - clear |
| weather_cloudy | ic_weather_cloudy.webp | 36x36 | WebP | Weather icon - cloudy |
| weather_rain | ic_weather_rain.webp | 36x36 | WebP | Weather icon - rain |
| battery_icon | ic_battery.webp | 36x36 | WebP | Battery indicator |
| heart_icon | ic_heart.webp | 36x36 | WebP | Heart rate icon |
| arsenal_mini_crest | arsenal_mini.webp | 30x30 | WebP | Mini crest for match display |
| match_ball | ic_football.webp | 24x24 | WebP | Football icon |
| live_indicator | ic_live.webp | 40x16 | WebP | Animated live badge |

### 9.2 Vector Assets

| Asset ID | Filename | Description |
|----------|----------|-------------|
| hour_indices | indices.svg | 12 tick marks around dial |
| complication_ring | comp_ring.svg | Progress ring for complications |
| match_divider | divider.svg | VS divider graphic |

### 9.3 Font Assets

| Font | Filename | Weights |
|------|----------|---------|
| Primary | ArsenalSans.ttf | Regular, Medium, Bold |
| Fallback | Montserrat.ttf | Regular, Medium, Bold |

---

## 10. Animation Specifications

### 10.1 Time Transition

```yaml
Animation:
  Type: CrossFade
  Duration: 300ms
  Easing: EaseInOutQuad
  Trigger: MINUTE_CHANGE
```

### 10.2 Live Indicator Pulse

```yaml
Animation:
  Type: Pulse
  Duration: 1000ms
  Easing: EaseInOutSine
  Loop: INFINITE
  Properties:
    - opacity: 0.6 → 1.0 → 0.6
    - scale: 1.0 → 1.05 → 1.0
    - glow_radius: 2px → 4px → 2px
```

### 10.3 State Transition

```yaml
Animation:
  Type: SlideVertical
  Duration: 400ms
  Easing: EaseOutBack
  Trigger: STATE_CHANGE
```

### 10.4 Heart Rate Pulse

```yaml
Animation:
  Type: Scale
  Duration: 800ms
  Easing: EaseOutElastic
  Loop: SYNC_TO_HEARTRATE
  Properties:
    - scale: 1.0 → 1.15 → 1.0
```

---

## 11. Interaction Design

### 11.1 Touch Targets

| Element | Action | Result |
|---------|--------|--------|
| Time Display | Single Tap | Open World Clock |
| Weather Comp | Single Tap | Open Weather App |
| Battery Comp | Single Tap | Open Battery Settings |
| Heart Rate Comp | Single Tap | Open Samsung Health |
| Match Section | Single Tap | Open Arsenal App / Browser |
| Cannon Crest | Long Press | Open Watch Face Settings |

### 11.2 Haptic Feedback

| Event | Haptic Pattern |
|-------|----------------|
| Match Kickoff | DOUBLE_CLICK + TICK |
| Goal Scored | SUCCESS (3x rapid) |
| Match End | HEAVY_CLICK |
| State Change | LIGHT_CLICK |

---

## 12. Performance Guidelines

### 12.1 Battery Optimization

- **Refresh Rate**:
  - Active: Update complications every 15 minutes
  - Match Live: Update every 60 seconds
  - AOD: Update every minute (time only)

- **Animation Budget**:
  - Maximum 2 concurrent animations
  - Disable all animations when battery < 15%

### 12.2 Memory Management

- **Total Asset Size**: Target < 2MB
- **WebP Compression**: Quality 85%
- **Lazy Loading**: Load match assets only on match day

### 12.3 Render Optimization

- Pre-render static elements to bitmap
- Use hardware layers for animated elements
- Implement dirty rectangle updates

---

## 13. Accessibility

### 13.1 Screen Reader Support

```xml
<Accessibility>
  <TimeAnnouncement format="It is %H:%M" />
  <ComplicationAnnouncement template="%label is %value" />
  <MatchAnnouncement template="Arsenal %action at %time" />
</Accessibility>
```

### 13.2 High Contrast Mode

- Increase text contrast ratio to 7:1 minimum
- Add white outline to all colored elements
- Disable gradient backgrounds

---

## 14. Localization

### 14.1 Supported Regions

- Time format: 12h/24h based on system preference
- Date format: Regional variants
- Kick-off time: Auto-converted to local timezone

### 14.2 Text Strings

| Key | EN | Value |
|-----|-----|-------|
| match_day | Match Day | MATCH DAY |
| kick_off | Kick-off | KICK-OFF |
| live | Live | LIVE |
| full_time | Full Time | FT |
| half_time | Half Time | HT |
| vs | Versus | vs |

---

*Document Version: 1.0.0*
*Last Updated: 2026-01-28*
*Platform: Samsung Galaxy Watch 6 / Wear OS 5-6 / One UI 8*
