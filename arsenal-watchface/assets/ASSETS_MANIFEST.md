# Arsenal Watch Face - Visual Assets Manifest
## Complete Asset Specification for Production

---

## 1. Asset Overview

| Category | Count | Total Size Target |
|----------|-------|-------------------|
| Background | 3 | ~300 KB |
| Logo/Crests | 4 | ~50 KB |
| Icons | 15 | ~30 KB |
| Team Badges | 40 | ~200 KB |
| Fonts | 2 | ~150 KB |
| **TOTAL** | **64** | **< 750 KB** |

---

## 2. Background Assets

### 2.1 bg_base_gradient.webp

**Purpose:** Primary background with navy gradient

| Property | Value |
|----------|-------|
| Dimensions | 450 x 450 px |
| Format | WebP (lossy) |
| Quality | 90% |
| Target Size | ~80 KB |

**Gradient Specification:**
```css
background: radial-gradient(
    ellipse 120% 100% at 50% 30%,
    #152238 0%,
    #0A1628 40%,
    #061020 100%
);
```

**Design Notes:**
- Subtle radial gradient creating depth
- Lighter area toward top-center (spotlight effect)
- Darkest at edges for natural vignette base

---

### 2.2 bg_fabric_texture.webp

**Purpose:** Jersey fabric texture overlay

| Property | Value |
|----------|-------|
| Dimensions | 450 x 450 px |
| Format | WebP (lossy) |
| Quality | 85% |
| Target Size | ~100 KB |
| Blend Mode | Overlay @ 15% opacity |

**Texture Specification:**
- Pattern: Diagonal micro-weave
- Angle: 45 degrees
- Line spacing: 2px
- Line weight: 0.5px
- Color: #FFFFFF (white, applied with opacity)

**Design Notes:**
- Seamless tiling pattern
- Mimics premium polyester jersey fabric
- Subtle enough to not interfere with readability
- Should be barely visible - adds sophistication, not distraction

---

### 2.3 bg_vignette.webp

**Purpose:** Edge darkening overlay

| Property | Value |
|----------|-------|
| Dimensions | 450 x 450 px |
| Format | WebP (lossy, transparent) |
| Quality | 90% |
| Target Size | ~40 KB |
| Blend Mode | Multiply @ 40% opacity |

**Gradient Specification:**
```css
background: radial-gradient(
    circle at center,
    transparent 0%,
    transparent 60%,
    rgba(0, 0, 0, 0.6) 100%
);
```

---

## 3. Logo & Crest Assets

### 3.1 cannon_crest_gold.webp

**Purpose:** Main Arsenal cannon logo at 12 o'clock

| Property | Value |
|----------|-------|
| Dimensions | 120 x 80 px (@2x) |
| Display Size | 60 x 40 px |
| Format | WebP (lossy) |
| Quality | 95% |
| Target Size | ~15 KB |

**Metallic Gradient:**
```css
.cannon {
    fill: linear-gradient(
        135deg,
        #E8B87D 0%,    /* Copper Highlight */
        #D4A853 35%,   /* Cannon Gold */
        #B8942D 65%,   /* Mid Gold */
        #8B6914 100%   /* Bronze Shadow */
    );
}
```

**Design Elements:**
- Simplified Arsenal cannon silhouette
- Modern minimal interpretation
- Inner shadow for 3D depth
- Subtle outer glow (#D4A853 @ 20%, 4px blur)

---

### 3.2 cannon_crest_outline.webp

**Purpose:** AOD mode cannon (outline only)

| Property | Value |
|----------|-------|
| Dimensions | 120 x 80 px (@2x) |
| Display Size | 60 x 40 px |
| Format | WebP (lossless, transparent) |
| Target Size | ~5 KB |

**Outline Specification:**
- Stroke color: #4A5568 (Dim Grey)
- Stroke width: 2px
- No fill (transparent interior)
- Anti-aliased edges

**Design Notes:**
- Must be hollow for OLED burn-in prevention
- Simplified detail for AOD legibility
- No glow effects (battery conservation)

---

### 3.3 arsenal_mini.webp

**Purpose:** Small crest for match day display

| Property | Value |
|----------|-------|
| Dimensions | 60 x 60 px (@2x) |
| Display Size | 30 x 30 px |
| Format | WebP (lossy) |
| Quality | 90% |
| Target Size | ~8 KB |

**Design:**
- Simplified shield with cannon
- High contrast for small display
- Arsenal red (#EF0107) and white primary colors

---

### 3.4 arsenal_mini_outline.webp

**Purpose:** AOD mini crest

| Property | Value |
|----------|-------|
| Dimensions | 60 x 60 px (@2x) |
| Display Size | 30 x 30 px |
| Format | WebP (lossless) |
| Target Size | ~3 KB |

---

## 4. Complication Icons

### 4.1 Icon Design System

**Universal Properties:**
| Property | Value |
|----------|-------|
| Canvas Size | 72 x 72 px (@2x) |
| Display Size | 36 x 36 px |
| Icon Area | 64 x 64 px (4px padding) |
| Stroke Width | 2-3px |
| Corner Radius | 2px minimum |
| Format | WebP (lossless, transparent) |

**Color Variants Required:**
- `_aqua` suffix: #00D4AA
- `_pink` suffix: #FF2D78
- `_gold` suffix: #D4A853
- `_white` suffix: #FFFFFF
- `_outline` suffix: #4A5568 (AOD)

---

### 4.2 Icon Specifications

#### ic_weather_clear.webp
**Symbol:** Sun with rays
**Variants:** _aqua, _outline
**Notes:** 8 rays, circular center

#### ic_weather_cloudy.webp
**Symbol:** Cloud
**Variants:** _aqua, _outline
**Notes:** Rounded cumulus shape

#### ic_weather_rain.webp
**Symbol:** Cloud with rain drops
**Variants:** _aqua, _outline
**Notes:** 3 rain drops below cloud

#### ic_weather_snow.webp
**Symbol:** Cloud with snowflakes
**Variants:** _aqua, _outline

#### ic_weather_storm.webp
**Symbol:** Cloud with lightning
**Variants:** _aqua, _outline

#### ic_battery.webp
**Symbol:** Battery with fill indicator
**Variants:** _aqua, _gold, _pink, _outline
**Notes:**
- _aqua: > 75% battery
- _gold: 25-75% battery
- _pink: < 25% battery

#### ic_heart.webp
**Symbol:** Heart shape
**Variants:** _pink, _outline
**Notes:** Solid fill for active, supports pulse animation

#### ic_steps.webp
**Symbol:** Footprint or walking figure
**Variants:** _aqua, _outline

#### ic_calendar.webp
**Symbol:** Calendar page
**Variants:** _aqua, _outline

#### ic_football.webp
**Symbol:** Soccer ball
**Variants:** _aqua, _gold, _outline
**Notes:** Classic pentagon pattern, 24x24 display size

#### ic_football_outline.webp (AOD)
**Symbol:** Soccer ball outline only
**Stroke:** #4A5568, 1.5px

#### ic_live_indicator.webp
**Symbol:** "LIVE" badge or pulsing dot
**Dimensions:** 80 x 32 px (@2x), display 40 x 16 px
**Color:** #FF0066 with glow
**Animation:** Requires pulse keyframe support

#### ic_vs_divider.webp
**Symbol:** "VS" text or graphical divider
**Dimensions:** 48 x 24 px
**Color:** #A0A8B4

---

## 5. Team Badge Assets

### 5.1 Badge Requirements

For the match day feature to display opponent crests, include badges for all teams Arsenal may face:

**Premier League (20 teams):**

| Team | File | ID |
|------|------|-----|
| Arsenal | team_arsenal.webp | 57 |
| Aston Villa | team_aston_villa.webp | 58 |
| Bournemouth | team_bournemouth.webp | 1044 |
| Brentford | team_brentford.webp | 402 |
| Brighton | team_brighton.webp | 397 |
| Chelsea | team_chelsea.webp | 61 |
| Crystal Palace | team_crystal_palace.webp | 354 |
| Everton | team_everton.webp | 62 |
| Fulham | team_fulham.webp | 63 |
| Ipswich Town | team_ipswich.webp | 349 |
| Leicester City | team_leicester.webp | 338 |
| Liverpool | team_liverpool.webp | 64 |
| Man City | team_man_city.webp | 65 |
| Man United | team_man_united.webp | 66 |
| Newcastle | team_newcastle.webp | 67 |
| Nottingham Forest | team_nottingham.webp | 351 |
| Southampton | team_southampton.webp | 340 |
| Tottenham | team_tottenham.webp | 73 |
| West Ham | team_west_ham.webp | 563 |
| Wolves | team_wolves.webp | 76 |

**Champions League (Top 16 likely opponents):**

| Team | File |
|------|------|
| Real Madrid | team_real_madrid.webp |
| Barcelona | team_barcelona.webp |
| Bayern Munich | team_bayern.webp |
| PSG | team_psg.webp |
| Juventus | team_juventus.webp |
| Inter Milan | team_inter.webp |
| AC Milan | team_ac_milan.webp |
| Borussia Dortmund | team_dortmund.webp |
| Atletico Madrid | team_atletico.webp |
| Benfica | team_benfica.webp |
| Porto | team_porto.webp |
| Ajax | team_ajax.webp |
| RB Leipzig | team_rb_leipzig.webp |
| Napoli | team_napoli.webp |
| Sevilla | team_sevilla.webp |
| Sporting CP | team_sporting.webp |

**Badge Specifications:**

| Property | Value |
|----------|-------|
| Dimensions | 60 x 60 px (@2x) |
| Display Size | 30 x 30 px |
| Format | WebP (lossy) |
| Quality | 90% |
| Target Size | ~5 KB each |

**Design Notes:**
- Simplified versions optimized for small display
- High contrast
- Recognizable at a glance
- Consider grayscale AOD variants if needed

---

## 6. Font Assets

### 6.1 arsenal_sans.ttf (Primary)

**Alternative:** If licensing Arsenal Sans is not possible, use:

| Property | Value |
|----------|-------|
| Font Name | Montserrat |
| License | Open Font License |
| Weights | Regular (400), Medium (500), Bold (700) |
| File Size | ~50 KB per weight |

**Character Set Required:**
- Basic Latin (A-Z, a-z)
- Numbers (0-9)
- Punctuation (: . , - ')
- Special (° % ♥)

### 6.2 arsenal_sans_bold.ttf

| Property | Value |
|----------|-------|
| Weight | 700 |
| Usage | Time display, headings, scores |

### 6.3 arsenal_sans_medium.ttf

| Property | Value |
|----------|-------|
| Weight | 500 |
| Usage | Date, secondary text, labels |

---

## 7. Animation Assets

### 7.1 Lottie Animation: live_pulse.json

**Purpose:** Animated "LIVE" indicator

| Property | Value |
|----------|-------|
| Format | Lottie JSON |
| Dimensions | 80 x 32 px |
| Duration | 1000ms |
| Loop | Infinite |

**Animation Keyframes:**
```json
{
  "0%": { "opacity": 0.6, "scale": 1.0 },
  "50%": { "opacity": 1.0, "scale": 1.05 },
  "100%": { "opacity": 0.6, "scale": 1.0 }
}
```

### 7.2 Lottie Animation: heart_beat.json

**Purpose:** Heart rate icon pulse

| Property | Value |
|----------|-------|
| Format | Lottie JSON |
| Dimensions | 36 x 36 px |
| Duration | Sync to HR or 800ms default |
| Loop | Infinite |

---

## 8. Preview & Marketing Assets

### 8.1 preview_watch_face.webp

**Purpose:** Watch face selector thumbnail

| Property | Value |
|----------|-------|
| Dimensions | 300 x 300 px |
| Format | WebP |
| Quality | 95% |

### 8.2 preview_aod.webp

**Purpose:** AOD mode preview

| Property | Value |
|----------|-------|
| Dimensions | 300 x 300 px |
| Format | WebP |
| Quality | 95% |

### 8.3 marketing_hero.webp

**Purpose:** Store listing hero image

| Property | Value |
|----------|-------|
| Dimensions | 1024 x 500 px |
| Format | WebP |
| Quality | 95% |

---

## 9. Asset Checklist

### Required for MVP

- [ ] bg_base_gradient.webp
- [ ] bg_fabric_texture.webp
- [ ] bg_vignette.webp
- [ ] cannon_crest_gold.webp
- [ ] cannon_crest_outline.webp
- [ ] ic_battery_aqua.webp
- [ ] ic_heart_pink.webp
- [ ] ic_steps_aqua.webp
- [ ] ic_calendar_aqua.webp
- [ ] ic_football_aqua.webp
- [ ] ic_football_outline.webp
- [ ] ic_live_indicator.webp
- [ ] arsenal_sans_bold.ttf (or Montserrat)
- [ ] arsenal_sans_medium.ttf (or Montserrat)
- [ ] preview_watch_face.webp

### Required for Match Day Feature

- [ ] arsenal_mini.webp
- [ ] All 20 Premier League team badges
- [ ] ic_vs_divider.webp

### Optional Enhancements

- [ ] Weather icon set (5 variants)
- [ ] Champions League team badges
- [ ] live_pulse.json (Lottie)
- [ ] heart_beat.json (Lottie)

---

## 10. Asset Export Settings

### WebP Export (Photoshop/Figma)

```
Format: WebP
Quality: 85-95% (per spec above)
Metadata: None
Color Profile: sRGB
```

### SVG to WebP Conversion

```bash
# Using ImageMagick
convert -background none -density 144 input.svg -resize 120x80 output.webp

# Using Inkscape
inkscape input.svg --export-type=png --export-width=120
cwebp -q 90 input.png -o output.webp
```

### Font Subsetting

```bash
# Using pyftsubset (fonttools)
pyftsubset Montserrat-Bold.ttf \
    --output-file=arsenal_sans_bold.ttf \
    --unicodes="U+0020-007E,U+00B0,U+0025,U+2665" \
    --layout-features='kern,liga'
```

---

## 11. Directory Structure

```
assets/
├── backgrounds/
│   ├── bg_base_gradient.webp
│   ├── bg_fabric_texture.webp
│   └── bg_vignette.webp
├── logos/
│   ├── cannon_crest_gold.webp
│   ├── cannon_crest_outline.webp
│   ├── arsenal_mini.webp
│   └── arsenal_mini_outline.webp
├── icons/
│   ├── ic_battery_aqua.webp
│   ├── ic_battery_gold.webp
│   ├── ic_battery_pink.webp
│   ├── ic_battery_outline.webp
│   ├── ic_heart_pink.webp
│   ├── ic_heart_outline.webp
│   ├── ic_steps_aqua.webp
│   ├── ic_steps_outline.webp
│   ├── ic_calendar_aqua.webp
│   ├── ic_calendar_outline.webp
│   ├── ic_football_aqua.webp
│   ├── ic_football_outline.webp
│   ├── ic_live_indicator.webp
│   ├── ic_vs_divider.webp
│   └── weather/
│       ├── ic_weather_clear_aqua.webp
│       ├── ic_weather_cloudy_aqua.webp
│       ├── ic_weather_rain_aqua.webp
│       └── ...
├── teams/
│   ├── premier_league/
│   │   ├── team_arsenal.webp
│   │   ├── team_chelsea.webp
│   │   └── ...
│   └── champions_league/
│       ├── team_real_madrid.webp
│       └── ...
├── fonts/
│   ├── arsenal_sans_bold.ttf
│   └── arsenal_sans_medium.ttf
├── animations/
│   ├── live_pulse.json
│   └── heart_beat.json
└── previews/
    ├── preview_watch_face.webp
    ├── preview_aod.webp
    └── marketing_hero.webp
```

---

*Document Version: 1.0.0*
*Last Updated: 2026-01-28*
*Related: DESIGN_SPECIFICATION.md, watchface.xml*
