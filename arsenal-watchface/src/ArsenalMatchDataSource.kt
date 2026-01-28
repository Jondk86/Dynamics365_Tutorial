package com.arsenal.watchface.complications

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.util.Log
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import kotlinx.coroutines.*
import java.time.*
import java.time.format.DateTimeFormatter

/**
 * Arsenal Match Day Complication Data Source
 *
 * Provides dynamic match information to the watch face complication slot.
 * Automatically switches between fitness stats and match data based on
 * the Arsenal FC fixture schedule.
 *
 * @see LOGIC_ARCHITECTURE.md for detailed system design
 */
class ArsenalMatchDataSource : ComplicationDataSourceService() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var matchRepository: MatchRepository

    companion object {
        private const val TAG = "ArsenalMatchData"
        const val ARSENAL_TEAM_ID = 57L // Arsenal's ID in football-data.org

        /**
         * Creates component name for this data source
         */
        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, ArsenalMatchDataSource::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()
        matchRepository = MatchRepository(applicationContext)
        Log.d(TAG, "ArsenalMatchDataSource created")
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    /**
     * Returns preview data for the complication picker
     */
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.LONG_TEXT -> createPreviewLongText()
            ComplicationType.SHORT_TEXT -> createPreviewShortText()
            else -> null
        }
    }

    private fun createPreviewLongText(): LongTextComplicationData {
        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder("ARS vs LIV").build(),
            contentDescription = PlainComplicationText.Builder("Match preview").build()
        )
            .setTitle(PlainComplicationText.Builder("17:30").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_football)
                ).build()
            )
            .build()
    }

    private fun createPreviewShortText(): ShortTextComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder("2-1").build(),
            contentDescription = PlainComplicationText.Builder("Match score").build()
        )
            .setTitle(PlainComplicationText.Builder("LIVE").build())
            .build()
    }

    /**
     * Main complication request handler
     */
    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        scope.launch {
            try {
                val fixture = matchRepository.getTodayFixture()
                val currentTime = ZonedDateTime.now()
                val state = determineMatchState(fixture, currentTime)

                Log.d(TAG, "Match state: $state, Fixture: ${fixture?.id}")

                val complicationData = when (state) {
                    MatchState.NO_MATCH -> createNoMatchData()
                    MatchState.MATCH_UPCOMING -> createUpcomingMatchData(fixture!!)
                    MatchState.MATCH_LIVE -> createLiveMatchData(fixture!!)
                    MatchState.MATCH_HALFTIME -> createHalftimeData(fixture!!)
                    MatchState.MATCH_ENDED -> createMatchEndedData(fixture!!)
                    MatchState.MATCH_POSTPONED -> createPostponedData(fixture!!)
                }

                listener.onComplicationData(complicationData)
            } catch (e: Exception) {
                Log.e(TAG, "Error creating complication data", e)
                listener.onComplicationData(NoDataComplicationData())
            }
        }
    }

    /**
     * Determines the current match state based on fixture data
     */
    private fun determineMatchState(
        fixture: ArsenalFixture?,
        currentTime: ZonedDateTime
    ): MatchState {
        if (fixture == null) return MatchState.NO_MATCH

        val kickoff = fixture.kickoffTime.atZone(currentTime.zone)
        val estimatedEnd = kickoff.plusMinutes(105) // 90 + 15 extra time buffer

        return when {
            // Not today
            !fixture.isToday(currentTime) -> MatchState.NO_MATCH

            // Match postponed
            fixture.status in listOf("POSTPONED", "CANCELLED", "SUSPENDED") ->
                MatchState.MATCH_POSTPONED

            // Before kickoff
            currentTime.isBefore(kickoff) -> MatchState.MATCH_UPCOMING

            // Match live
            fixture.status == "IN_PLAY" -> MatchState.MATCH_LIVE

            // Half-time
            fixture.status == "PAUSED" -> MatchState.MATCH_HALFTIME

            // Match finished (show result for 2 hours)
            fixture.status == "FINISHED" &&
                currentTime.isBefore(estimatedEnd.plusHours(2)) -> MatchState.MATCH_ENDED

            else -> MatchState.NO_MATCH
        }
    }

    /**
     * Creates complication data for upcoming match
     */
    private fun createUpcomingMatchData(fixture: ArsenalFixture): ComplicationData {
        val kickoffTime = fixture.kickoffTime
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("HH:mm"))

        val matchup = "${fixture.homeTeam.shortName} vs ${fixture.awayTeam.shortName}"

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(matchup).build(),
            contentDescription = PlainComplicationText.Builder("MATCH_UPCOMING").build()
        )
            .setTitle(PlainComplicationText.Builder(kickoffTime).build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_football_aqua)
                ).build()
            )
            .setTapAction(createMatchTapAction(fixture))
            .build()
    }

    /**
     * Creates complication data for live match
     */
    private fun createLiveMatchData(fixture: ArsenalFixture): ComplicationData {
        val score = "${fixture.homeScore ?: 0} - ${fixture.awayScore ?: 0}"
        val minute = "${fixture.minute ?: 0}'"

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(score).build(),
            contentDescription = PlainComplicationText.Builder("MATCH_LIVE").build()
        )
            .setTitle(PlainComplicationText.Builder("LIVE $minute").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_live_indicator)
                ).build()
            )
            .setTapAction(createMatchTapAction(fixture))
            .build()
    }

    /**
     * Creates complication data for half-time
     */
    private fun createHalftimeData(fixture: ArsenalFixture): ComplicationData {
        val score = "${fixture.homeScore ?: 0} - ${fixture.awayScore ?: 0}"

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(score).build(),
            contentDescription = PlainComplicationText.Builder("MATCH_HALFTIME").build()
        )
            .setTitle(PlainComplicationText.Builder("HT").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_football_gold)
                ).build()
            )
            .setTapAction(createMatchTapAction(fixture))
            .build()
    }

    /**
     * Creates complication data for finished match
     */
    private fun createMatchEndedData(fixture: ArsenalFixture): ComplicationData {
        val score = "${fixture.homeScore ?: 0} - ${fixture.awayScore ?: 0}"
        val result = when {
            fixture.isArsenalHome && (fixture.homeScore ?: 0) > (fixture.awayScore ?: 0) -> "WIN"
            !fixture.isArsenalHome && (fixture.awayScore ?: 0) > (fixture.homeScore ?: 0) -> "WIN"
            fixture.homeScore == fixture.awayScore -> "DRAW"
            else -> "LOSS"
        }

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(score).build(),
            contentDescription = PlainComplicationText.Builder("MATCH_ENDED").build()
        )
            .setTitle(PlainComplicationText.Builder("FT - $result").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_football_aqua)
                ).build()
            )
            .setTapAction(createMatchTapAction(fixture))
            .build()
    }

    /**
     * Creates complication data for postponed match
     */
    private fun createPostponedData(fixture: ArsenalFixture): ComplicationData {
        val matchup = "${fixture.homeTeam.shortName} vs ${fixture.awayTeam.shortName}"

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(matchup).build(),
            contentDescription = PlainComplicationText.Builder("MATCH_POSTPONED").build()
        )
            .setTitle(PlainComplicationText.Builder("POSTPONED").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_football_outline)
                ).build()
            )
            .build()
    }

    /**
     * Creates empty complication data (triggers fitness stats display)
     */
    private fun createNoMatchData(): ComplicationData {
        return NoDataComplicationData()
    }

    /**
     * Creates tap action to open match details
     */
    private fun createMatchTapAction(fixture: ArsenalFixture): android.app.PendingIntent? {
        // Opens Arsenal app or browser to match page
        val intent = android.content.Intent(
            android.content.Intent.ACTION_VIEW,
            android.net.Uri.parse("https://www.arsenal.com/matches")
        )
        return android.app.PendingIntent.getActivity(
            this,
            fixture.id.toInt(),
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                android.app.PendingIntent.FLAG_IMMUTABLE
        )
    }
}

/**
 * Match state enumeration
 */
enum class MatchState {
    NO_MATCH,
    MATCH_UPCOMING,
    MATCH_LIVE,
    MATCH_HALFTIME,
    MATCH_ENDED,
    MATCH_POSTPONED
}

/**
 * Arsenal fixture data model
 */
data class ArsenalFixture(
    val id: Long,
    val competition: Competition,
    val homeTeam: Team,
    val awayTeam: Team,
    val kickoffTime: Instant,
    val status: String,
    val homeScore: Int?,
    val awayScore: Int?,
    val minute: Int?,
    val venue: String?,
    val events: List<MatchEvent> = emptyList()
) {
    val isArsenalHome: Boolean
        get() = homeTeam.id == ArsenalMatchDataSource.ARSENAL_TEAM_ID

    fun isToday(currentTime: ZonedDateTime): Boolean {
        val kickoffDate = kickoffTime.atZone(currentTime.zone).toLocalDate()
        return kickoffDate == currentTime.toLocalDate()
    }
}

data class Team(
    val id: Long,
    val name: String,
    val shortName: String,
    val crestUrl: String? = null
)

data class Competition(
    val id: Long,
    val name: String,
    val emblemUrl: String? = null
)

data class MatchEvent(
    val type: EventType,
    val minute: Int,
    val team: Team,
    val player: String,
    val assist: String? = null
)

enum class EventType {
    GOAL, OWN_GOAL, PENALTY, PENALTY_MISS,
    YELLOW_CARD, RED_CARD,
    SUBSTITUTION
}

/**
 * Repository for fetching and caching match data
 * In production, this would sync with phone companion app via Data Layer
 */
class MatchRepository(private val context: Context) {

    private var cachedFixture: ArsenalFixture? = null
    private var lastFetchTime: Instant = Instant.EPOCH

    /**
     * Gets today's fixture, using cache if available and fresh
     */
    suspend fun getTodayFixture(): ArsenalFixture? {
        val cacheAge = Duration.between(lastFetchTime, Instant.now())

        // Return cached data if fresh (< 5 minutes for live, < 1 hour otherwise)
        if (cachedFixture != null) {
            val maxAge = when (cachedFixture?.status) {
                "IN_PLAY", "PAUSED" -> Duration.ofMinutes(1)
                else -> Duration.ofHours(1)
            }
            if (cacheAge < maxAge) {
                return cachedFixture
            }
        }

        // Fetch from Data Layer (synced from phone)
        return fetchFromDataLayer()
    }

    fun getCachedFixture(): ArsenalFixture? = cachedFixture

    /**
     * Fetches fixture data from Wear Data Layer
     * Data is synced from phone companion app
     */
    private suspend fun fetchFromDataLayer(): ArsenalFixture? {
        return withContext(Dispatchers.IO) {
            try {
                val dataClient = com.google.android.gms.wearable.Wearable
                    .getDataClient(context)

                val dataItems = com.google.android.gms.tasks.Tasks.await(
                    dataClient.getDataItems(
                        android.net.Uri.parse("wear://*/arsenal/match")
                    )
                )

                dataItems.firstOrNull()?.let { item ->
                    val dataMap = com.google.android.gms.wearable.DataMapItem
                        .fromDataItem(item).dataMap

                    // Parse fixture from DataMap
                    if (dataMap.containsKey("match_id")) {
                        val fixture = ArsenalFixture(
                            id = dataMap.getLong("match_id"),
                            competition = Competition(
                                id = dataMap.getLong("competition_id", 0),
                                name = dataMap.getString("competition", "")
                            ),
                            homeTeam = Team(
                                id = dataMap.getLong("home_team_id", 0),
                                name = dataMap.getString("home_team", ""),
                                shortName = dataMap.getString("home_team_short", "")
                            ),
                            awayTeam = Team(
                                id = dataMap.getLong("away_team_id", 0),
                                name = dataMap.getString("away_team", ""),
                                shortName = dataMap.getString("away_team_short", "")
                            ),
                            kickoffTime = Instant.ofEpochMilli(dataMap.getLong("kickoff")),
                            status = dataMap.getString("status", "SCHEDULED"),
                            homeScore = if (dataMap.containsKey("home_score"))
                                dataMap.getInt("home_score") else null,
                            awayScore = if (dataMap.containsKey("away_score"))
                                dataMap.getInt("away_score") else null,
                            minute = if (dataMap.containsKey("minute"))
                                dataMap.getInt("minute") else null,
                            venue = dataMap.getString("venue")
                        )

                        cachedFixture = fixture
                        lastFetchTime = Instant.now()
                        fixture
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                Log.e("MatchRepository", "Failed to fetch from Data Layer", e)
                cachedFixture // Return cached data on error
            }
        }
    }
}
