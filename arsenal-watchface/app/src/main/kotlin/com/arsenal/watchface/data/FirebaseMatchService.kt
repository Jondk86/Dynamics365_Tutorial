package com.arsenal.watchface.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.arsenal.watchface.complications.ArsenalMatchDataSource
import com.arsenal.watchface.utils.AlertType
import com.arsenal.watchface.utils.MatchAlertService
import com.google.android.gms.wearable.*
import kotlinx.coroutines.*
import java.time.Instant

/**
 * Firebase Cloud Messaging handler for push-based match updates.
 *
 * Instead of polling every minute (battery intensive), this service
 * receives push notifications from the companion phone app when:
 * - Match is about to start (5 min warning)
 * - Match kicks off
 * - Goal is scored
 * - Half-time/Full-time
 * - Score changes
 *
 * Battery Impact: Minimal - only wakes when events occur
 */
class FirebaseMatchService : WearableListenerService() {

    companion object {
        private const val TAG = "FirebaseMatchService"

        // Message paths
        const val PATH_MATCH_UPDATE = "/arsenal/match/update"
        const val PATH_GOAL_ALERT = "/arsenal/match/goal"
        const val PATH_KICKOFF = "/arsenal/match/kickoff"
        const val PATH_HALFTIME = "/arsenal/match/halftime"
        const val PATH_FULLTIME = "/arsenal/match/fulltime"
        const val PATH_SCORE_UPDATE = "/arsenal/match/score"

        // Preferences
        private const val PREFS_NAME = "arsenal_watchface_prefs"
        private const val PREF_AUDIO_ENABLED = "match_audio_enabled"
        private const val PREF_LAST_STATE = "last_match_state"
        private const val PREF_LAST_SCORE = "last_match_score"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var alertService: MatchAlertService
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        alertService = MatchAlertService(this)
        prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        alertService.release()
        scope.cancel()
    }

    /**
     * Handles incoming messages from companion phone app.
     * This is the primary method for receiving real-time match updates.
     */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received: ${messageEvent.path}")

        val audioEnabled = prefs.getBoolean(PREF_AUDIO_ENABLED, true)
        val data = String(messageEvent.data)

        when (messageEvent.path) {
            PATH_KICKOFF -> handleKickoff(data, audioEnabled)
            PATH_GOAL_ALERT -> handleGoal(data, audioEnabled)
            PATH_HALFTIME -> handleHalftime(data)
            PATH_FULLTIME -> handleFulltime(data, audioEnabled)
            PATH_SCORE_UPDATE -> handleScoreUpdate(data)
            PATH_MATCH_UPDATE -> handleMatchUpdate(data)
        }

        // Always request complication update after any message
        requestComplicationUpdate()
    }

    /**
     * Handles data layer changes (sync from phone).
     */
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                if (dataItem.uri.path == "/arsenal/match") {
                    processMatchData(dataItem)
                }
            }
        }
    }

    private fun handleKickoff(data: String, audioEnabled: Boolean) {
        Log.d(TAG, "Kick-off received: $data")

        // Update state
        prefs.edit().putString(PREF_LAST_STATE, MatchState.MATCH_LIVE.name).apply()

        // Trigger alert
        alertService.triggerKickoffAlert(audioEnabled)
    }

    private fun handleGoal(data: String, audioEnabled: Boolean) {
        Log.d(TAG, "Goal received: $data")

        // Parse goal data (format: "scorer_name|minute|team_id")
        val parts = data.split("|")
        val scorer = parts.getOrNull(0)
        val minute = parts.getOrNull(1)?.toIntOrNull()
        val teamId = parts.getOrNull(2)?.toLongOrNull()

        // Only alert for Arsenal goals
        if (teamId == ArsenalMatchDataSource.ARSENAL_TEAM_ID) {
            alertService.triggerGoalAlert(scorer, audioEnabled)
        }

        // Update cached score
        val currentScore = parts.getOrNull(3) ?: ""
        prefs.edit().putString(PREF_LAST_SCORE, currentScore).apply()
    }

    private fun handleHalftime(data: String) {
        Log.d(TAG, "Half-time received: $data")

        prefs.edit().putString(PREF_LAST_STATE, MatchState.MATCH_HALFTIME.name).apply()
        alertService.triggerHalftimeAlert(data)
    }

    private fun handleFulltime(data: String, audioEnabled: Boolean) {
        Log.d(TAG, "Full-time received: $data")

        prefs.edit().putString(PREF_LAST_STATE, MatchState.MATCH_ENDED.name).apply()

        // Parse result (format: "home_score-away_score|result")
        val parts = data.split("|")
        val score = parts.getOrNull(0) ?: ""
        val result = parts.getOrNull(1) ?: ""

        alertService.triggerFullTimeAlert(score, result)

        // Check for victory
        if (result.equals("WIN", ignoreCase = true)) {
            alertService.triggerVictoryAlert(score, audioEnabled)
        }
    }

    private fun handleScoreUpdate(data: String) {
        Log.d(TAG, "Score update: $data")
        prefs.edit().putString(PREF_LAST_SCORE, data).apply()
    }

    private fun handleMatchUpdate(data: String) {
        Log.d(TAG, "Match update: $data")
        // Generic update - just refresh complication
    }

    private fun processMatchData(dataItem: DataItem) {
        try {
            val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
            val status = dataMap.getString("status", "")
            val homeScore = dataMap.getInt("home_score", 0)
            val awayScore = dataMap.getInt("away_score", 0)

            Log.d(TAG, "Match data processed: status=$status, score=$homeScore-$awayScore")

            // Store for quick access
            prefs.edit()
                .putString(PREF_LAST_STATE, status)
                .putString(PREF_LAST_SCORE, "$homeScore-$awayScore")
                .apply()

        } catch (e: Exception) {
            Log.e(TAG, "Error processing match data", e)
        }
    }

    private fun requestComplicationUpdate() {
        try {
            val requester = ComplicationDataSourceUpdateRequester.create(
                this,
                ArsenalMatchDataSource.getComponentName(this)
            )
            requester.requestUpdateAll()
            Log.d(TAG, "Complication update requested")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request complication update", e)
        }
    }
}

/**
 * Configuration for the companion phone app to know what events to push.
 *
 * Usage in companion app:
 * ```kotlin
 * // Send kick-off notification
 * val messageClient = Wearable.getMessageClient(context)
 * val nodes = Wearable.getNodeClient(context).connectedNodes.await()
 * nodes.forEach { node ->
 *     messageClient.sendMessage(
 *         node.id,
 *         FirebaseMatchService.PATH_KICKOFF,
 *         "Arsenal vs Liverpool".toByteArray()
 *     )
 * }
 * ```
 */
object MatchEventPaths {
    const val KICKOFF = "/arsenal/match/kickoff"
    const val GOAL = "/arsenal/match/goal"      // Format: "scorer|minute|teamId|score"
    const val HALFTIME = "/arsenal/match/halftime"  // Format: "1-0"
    const val FULLTIME = "/arsenal/match/fulltime"  // Format: "2-1|WIN"
    const val SCORE = "/arsenal/match/score"    // Format: "2-1"
    const val UPDATE = "/arsenal/match/update"  // Generic update trigger
}
