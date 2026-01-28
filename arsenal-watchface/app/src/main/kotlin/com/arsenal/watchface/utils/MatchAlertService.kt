package com.arsenal.watchface.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.arsenal.watchface.R
import com.arsenal.watchface.data.MatchState

/**
 * Service for managing match day audio and haptic alerts.
 *
 * Provides:
 * - Kick-off whistle sound (double whistle)
 * - Goal celebration sound (crowd roar)
 * - Haptic feedback patterns
 * - Respect for user preferences (silent mode, audio toggle)
 */
class MatchAlertService(private val context: Context) {

    companion object {
        private const val TAG = "MatchAlertService"
        private const val CHANNEL_ID = "arsenal_match_alerts"
        private const val NOTIFICATION_ID = 1001

        // Vibration patterns (in milliseconds)
        // Format: [delay, vibrate, delay, vibrate, ...]

        // Kick-off: Two quick strong vibrations
        private val KICKOFF_PATTERN = longArrayOf(0, 200, 100, 200)

        // Goal: Celebration pattern - escalating intensity
        private val GOAL_PATTERN = longArrayOf(0, 100, 50, 150, 50, 200, 50, 300, 100, 400)

        // Victory: Long triumphant vibration
        private val VICTORY_PATTERN = longArrayOf(0, 500, 200, 500, 200, 800)

        // Simple alert
        private val ALERT_PATTERN = longArrayOf(0, 150, 100, 150)
    }

    private var mediaPlayer: MediaPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    init {
        createNotificationChannel()
    }

    /**
     * Triggers kick-off alert with whistle sound and haptic feedback.
     * Called when match state changes from UPCOMING to LIVE.
     */
    fun triggerKickoffAlert(audioEnabled: Boolean) {
        Log.d(TAG, "Triggering kick-off alert (audio: $audioEnabled)")

        // Always do haptic feedback
        performHapticFeedback(KICKOFF_PATTERN, "Kick-off Alert")

        // Play sound only if enabled and not in silent mode
        if (audioEnabled && !isDeviceSilent()) {
            playSound(R.raw.kickoff_whistle)
        }

        // Show notification
        showMatchNotification(
            title = "Kick-Off!",
            message = "The match has started!",
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    /**
     * Triggers goal celebration alert.
     * Called when Arsenal scores.
     */
    fun triggerGoalAlert(scorerName: String?, audioEnabled: Boolean) {
        Log.d(TAG, "Triggering goal alert for: $scorerName (audio: $audioEnabled)")

        // Celebration haptic pattern
        performHapticFeedback(GOAL_PATTERN, "Goal Alert")

        // Play crowd roar if enabled
        if (audioEnabled && !isDeviceSilent()) {
            playSound(R.raw.crowd_roar)
        }

        // Show notification
        val message = if (scorerName != null) {
            "GOAL! $scorerName scores for Arsenal!"
        } else {
            "GOAL! Arsenal have scored!"
        }
        showMatchNotification(
            title = "GOOOAL!",
            message = message,
            priority = NotificationCompat.PRIORITY_MAX
        )
    }

    /**
     * Triggers victory celebration.
     * Called when Arsenal wins (match ends with Arsenal ahead).
     */
    fun triggerVictoryAlert(finalScore: String, audioEnabled: Boolean) {
        Log.d(TAG, "Triggering victory alert: $finalScore (audio: $audioEnabled)")

        // Victory haptic pattern
        performHapticFeedback(VICTORY_PATTERN, "Victory Alert")

        // Play victory sound if enabled
        if (audioEnabled && !isDeviceSilent()) {
            playSound(R.raw.victory_fanfare)
        }

        showMatchNotification(
            title = "Victory!",
            message = "Arsenal win $finalScore! COYG!",
            priority = NotificationCompat.PRIORITY_HIGH
        )
    }

    /**
     * Triggers half-time alert.
     */
    fun triggerHalftimeAlert(score: String) {
        Log.d(TAG, "Triggering half-time alert: $score")

        performHapticFeedback(ALERT_PATTERN, "Half-time Alert")

        showMatchNotification(
            title = "Half-Time",
            message = "Score at half-time: $score",
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    /**
     * Triggers full-time alert.
     */
    fun triggerFullTimeAlert(finalScore: String, result: String) {
        Log.d(TAG, "Triggering full-time alert: $finalScore ($result)")

        performHapticFeedback(ALERT_PATTERN, "Full-time Alert")

        showMatchNotification(
            title = "Full Time - $result",
            message = "Final score: $finalScore",
            priority = NotificationCompat.PRIORITY_DEFAULT
        )
    }

    /**
     * Performs haptic feedback with the specified pattern.
     */
    private fun performHapticFeedback(pattern: LongArray, reason: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Calculate amplitudes for pattern (alternating 0 and max)
                val amplitudes = IntArray(pattern.size) { i ->
                    if (i % 2 == 0) 0 else VibrationEffect.DEFAULT_AMPLITUDE
                }
                val effect = VibrationEffect.createWaveform(pattern, amplitudes, -1)
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
            Log.d(TAG, "Haptic feedback performed: $reason")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to perform haptic feedback", e)
        }
    }

    /**
     * Plays a sound from raw resources.
     */
    private fun playSound(soundResId: Int) {
        try {
            // Release any existing player
            mediaPlayer?.release()

            mediaPlayer = MediaPlayer.create(context, soundResId)?.apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )

                // Set volume to max (respecting system volume)
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION)
                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION)
                setVolume(currentVolume.toFloat() / maxVolume, currentVolume.toFloat() / maxVolume)

                setOnCompletionListener {
                    it.release()
                    mediaPlayer = null
                }

                start()
            }
            Log.d(TAG, "Sound playback started")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play sound", e)
        }
    }

    /**
     * Checks if device is in silent/vibrate mode.
     */
    private fun isDeviceSilent(): Boolean {
        return when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT,
            AudioManager.RINGER_MODE_VIBRATE -> true
            else -> false
        }
    }

    /**
     * Creates notification channel for match alerts.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Arsenal Match Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for Arsenal match events"
                enableVibration(true)
                vibrationPattern = ALERT_PATTERN
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Shows a match notification.
     */
    private fun showMatchNotification(title: String, message: String, priority: Int) {
        try {
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_complication_match)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .build()

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to show notification", e)
        }
    }

    /**
     * Releases resources.
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

/**
 * Extension function to determine if alert should trigger based on state change.
 */
fun MatchState.shouldTriggerAlert(previousState: MatchState): AlertType? {
    return when {
        previousState == MatchState.MATCH_UPCOMING && this == MatchState.MATCH_LIVE -> AlertType.KICKOFF
        previousState == MatchState.MATCH_LIVE && this == MatchState.MATCH_HALFTIME -> AlertType.HALFTIME
        previousState == MatchState.MATCH_HALFTIME && this == MatchState.MATCH_LIVE -> AlertType.SECOND_HALF
        previousState == MatchState.MATCH_LIVE && this == MatchState.MATCH_ENDED -> AlertType.FULLTIME
        else -> null
    }
}

enum class AlertType {
    KICKOFF,
    GOAL,
    HALFTIME,
    SECOND_HALF,
    FULLTIME,
    VICTORY
}
