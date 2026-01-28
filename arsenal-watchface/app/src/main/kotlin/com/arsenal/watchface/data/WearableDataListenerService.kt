package com.arsenal.watchface.data

import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.arsenal.watchface.complications.ArsenalMatchDataSource
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * Listens for data updates from the companion phone app.
 * When match data is synced, triggers a complication update.
 */
class WearableDataListenerService : WearableListenerService() {

    companion object {
        private const val TAG = "WearableDataListener"
        private const val MATCH_DATA_PATH = "/arsenal/match"
        private const val MATCH_UPDATE_PATH = "/arsenal/match/update"
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "Data changed received: ${dataEvents.count} events")

        dataEvents.forEach { event ->
            val uri = event.dataItem.uri
            Log.d(TAG, "Data event path: ${uri.path}")

            if (uri.path == MATCH_DATA_PATH) {
                handleMatchDataUpdate(event.dataItem)
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "Message received: ${messageEvent.path}")

        when (messageEvent.path) {
            MATCH_UPDATE_PATH -> {
                // Force complication refresh
                requestComplicationUpdate()
            }
        }
    }

    private fun handleMatchDataUpdate(dataItem: com.google.android.gms.wearable.DataItem) {
        try {
            val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
            val syncTime = dataMap.getLong("sync_time", 0)

            Log.d(TAG, "Match data updated at: $syncTime")

            // Check if there's match data
            if (dataMap.containsKey("match_id")) {
                val matchId = dataMap.getLong("match_id")
                val homeTeam = dataMap.getString("home_team_short", "")
                val awayTeam = dataMap.getString("away_team_short", "")
                val status = dataMap.getString("status", "SCHEDULED")

                Log.d(TAG, "Match: $homeTeam vs $awayTeam (ID: $matchId, Status: $status)")
            } else {
                Log.d(TAG, "No match data present")
            }

            // Request complication update
            requestComplicationUpdate()

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
