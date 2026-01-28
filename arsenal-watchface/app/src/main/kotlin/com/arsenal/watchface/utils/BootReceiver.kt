package com.arsenal.watchface.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceUpdateRequester
import com.arsenal.watchface.complications.ArsenalMatchDataSource

/**
 * Receives boot completed broadcast to refresh complications
 * after device restart.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Boot completed, refreshing complications")

            try {
                // Request update for Arsenal Match complication
                val requester = ComplicationDataSourceUpdateRequester.create(
                    context,
                    ArsenalMatchDataSource.getComponentName(context)
                )
                requester.requestUpdateAll()

                Log.d(TAG, "Complication update requested after boot")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to request complication update", e)
            }
        }
    }
}
