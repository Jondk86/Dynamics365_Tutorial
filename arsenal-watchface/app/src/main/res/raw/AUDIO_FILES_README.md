# Audio Files for Match Alerts

Place your audio files in this directory (`app/src/main/res/raw/`).

## Required Files

| Filename | Description | Max Duration | Format |
|----------|-------------|--------------|--------|
| `kickoff_whistle.mp3` | Double referee whistle for match start | 1-2 seconds | MP3/OGG |
| `crowd_roar.mp3` | Crowd celebration for goals | 2-3 seconds | MP3/OGG |
| `victory_fanfare.mp3` | Victory celebration sound | 3-5 seconds | MP3/OGG |

## Recommended Sources

1. **Free Sound Effects:**
   - [Freesound.org](https://freesound.org) - Search "referee whistle", "crowd cheer"
   - [Pixabay](https://pixabay.com/sound-effects/) - Free sound effects

2. **Create Your Own:**
   - Record a referee whistle with your phone
   - Edit to 1-2 seconds using Audacity

## Audio Specifications

- **Format:** MP3 or OGG (OGG preferred for smaller size)
- **Sample Rate:** 44100 Hz or 22050 Hz
- **Bitrate:** 128 kbps (MP3) or 96 kbps (OGG)
- **Channels:** Mono (smaller file size, watch speaker is mono)
- **Max File Size:** 50KB per file recommended

## Tips

1. Keep files SHORT - watch speakers are small
2. Normalize audio to max volume
3. Test on watch - tiny speaker means some frequencies don't play well
4. High frequencies (whistles) work better than bass

## Placeholder Files

If you don't add audio files, the app will:
- Still work (alerts will be haptic-only)
- Show a warning in logcat about missing audio

To create silent placeholders (for testing):
```bash
# Using ffmpeg to create 1-second silent MP3
ffmpeg -f lavfi -i anullsrc=r=22050:cl=mono -t 1 -q:a 9 kickoff_whistle.mp3
ffmpeg -f lavfi -i anullsrc=r=22050:cl=mono -t 2 -q:a 9 crowd_roar.mp3
ffmpeg -f lavfi -i anullsrc=r=22050:cl=mono -t 3 -q:a 9 victory_fanfare.mp3
```

## File Naming

Files MUST be named exactly as shown above (lowercase, underscores).
Android resource names must be lowercase alphanumeric with underscores only.
