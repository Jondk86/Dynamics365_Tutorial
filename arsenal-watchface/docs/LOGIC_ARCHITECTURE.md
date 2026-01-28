# Match Day Logic Architecture
## Arsenal Watch Face - Dynamic Complication System

---

## 1. System Overview

The Match Day Dynamic Complication is the core intelligent feature of the Arsenal watch face. It automatically detects Arsenal FC match days and transforms the lower section of the watch face to display real-time match information.

### 1.1 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        SYSTEM ARCHITECTURE                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐   │
│  │  PHONE APP      │     │   CLOUD API     │     │  WATCH FACE     │   │
│  │  (Companion)    │     │  (Football-Data)│     │  (WFF Renderer) │   │
│  └────────┬────────┘     └────────┬────────┘     └────────┬────────┘   │
│           │                       │                       │             │
│           │  ┌────────────────────┘                       │             │
│           │  │                                            │             │
│           ▼  ▼                                            │             │
│  ┌─────────────────────────────────────┐                 │             │
│  │     DATA SYNC SERVICE               │                 │             │
│  │   (WorkManager + Firebase)          │◀────────────────┘             │
│  └────────────────┬────────────────────┘                               │
│                   │                                                     │
│                   ▼                                                     │
│  ┌─────────────────────────────────────┐                               │
│  │  COMPLICATION DATA SOURCE           │                               │
│  │  (ArsenalMatchDataSource.kt)        │                               │
│  └────────────────┬────────────────────┘                               │
│                   │                                                     │
│                   ▼                                                     │
│  ┌─────────────────────────────────────┐                               │
│  │  WATCH FACE COMPLICATION SLOT       │                               │
│  │  (slotId: 3 - Match Day Section)    │                               │
│  └─────────────────────────────────────┘                               │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Data Flow

### 2.1 Match Data Pipeline

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│ Football │───▶│  Phone   │───▶│   Wear   │───▶│Complicat-│───▶│  Watch   │
│   API    │    │   App    │    │Data Layer│    │ion Source│    │   Face   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘    └──────────┘
     │               │               │               │               │
     │ JSON          │ Processed     │ DataClient    │ Complication  │ WFF
     │ Response      │ Match Data    │ MessageAPI    │ Data          │ Render
     │               │               │               │               │
     ▼               ▼               ▼               ▼               ▼
  Fixtures       MatchInfo        DataMap       LongTextData    UI Update
  + Live Score   Object          Serialized     with State      on Screen
```

### 2.2 Data Refresh Intervals

| State | Refresh Interval | API Calls | Battery Impact |
|-------|------------------|-----------|----------------|
| No Match (7+ days) | 24 hours | 1/day | Minimal |
| Pre-Match (< 7 days) | 6 hours | 4/day | Low |
| Match Day (pre-kickoff) | 1 hour | 24/day | Moderate |
| Match Live | 60 seconds | ~90/match | Higher |
| Post-Match (< 2 hours) | 5 minutes | 24/period | Moderate |

---

## 3. State Machine Implementation

### 3.1 Match States

```kotlin
enum class MatchState {
    NO_MATCH,           // Default state - show fitness stats
    MATCH_UPCOMING,     // Match today but not started
    MATCH_LIVE,         // Match in progress
    MATCH_HALFTIME,     // Half-time break
    MATCH_ENDED,        // Final whistle (show for 2 hours)
    MATCH_POSTPONED     // Match postponed/cancelled
}
```

### 3.2 State Transition Logic

```kotlin
/**
 * Determines the current match state based on fixture data
 * and current time in user's local timezone
 */
fun determineMatchState(
    fixture: ArsenalFixture?,
    currentTime: ZonedDateTime
): MatchState {
    if (fixture == null) return MatchState.NO_MATCH

    val kickoff = fixture.kickoffTime.atZone(userTimezone)
    val matchEnd = kickoff.plusMinutes(fixture.duration ?: 105) // 90 + 15 extra

    return when {
        // No Arsenal match today
        !fixture.isToday(currentTime) -> MatchState.NO_MATCH

        // Match is today but hasn't started
        currentTime.isBefore(kickoff) -> MatchState.MATCH_UPCOMING

        // Match is live
        fixture.status == "IN_PLAY" -> MatchState.MATCH_LIVE

        // Half-time
        fixture.status == "PAUSED" -> MatchState.MATCH_HALFTIME

        // Match finished (show result for 2 hours)
        fixture.status == "FINISHED" &&
            currentTime.isBefore(matchEnd.plusHours(2)) -> MatchState.MATCH_ENDED

        // Match postponed
        fixture.status in listOf("POSTPONED", "CANCELLED") -> MatchState.MATCH_POSTPONED

        else -> MatchState.NO_MATCH
    }
}
```

### 3.3 WFF Conditional Rendering

```xml
<!--
    WFF Expression-based state switching
    Uses Complication data to determine display state
-->
<Condition>
    <Expressions>
        <!-- Extract state from complication content description -->
        <Expression name="match_state">
            [COMPLICATION.3.CONTENT_DESCRIPTION]
        </Expression>
    </Expressions>

    <!-- STATE: NO_MATCH - Default fitness display -->
    <Compare expression="match_state" value="NO_MATCH">
        <Group name="fitness_stats_layout">
            <!-- Steps, HR, Calendar -->
        </Group>
    </Compare>

    <!-- STATE: MATCH_UPCOMING - Pre-match info -->
    <Compare expression="match_state" value="MATCH_UPCOMING">
        <Group name="match_preview_layout">
            <!-- Team crests, kick-off time -->
        </Group>
    </Compare>

    <!-- STATE: MATCH_LIVE - Live score -->
    <Compare expression="match_state" value="MATCH_LIVE">
        <Group name="match_live_layout">
            <!-- Live indicator, score, minute -->
        </Group>
    </Compare>

    <!-- STATE: MATCH_ENDED - Final result -->
    <Compare expression="match_state" value="MATCH_ENDED">
        <Group name="match_result_layout">
            <!-- FT indicator, final score -->
        </Group>
    </Compare>
</Condition>
```

---

## 4. Complication Data Source Implementation

### 4.1 ArsenalMatchDataSource.kt

```kotlin
package com.arsenal.watchface.complications

import android.content.ComponentName
import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Complication Data Source for Arsenal Match Day information.
 *
 * Provides dynamic match information to the watch face complication slot,
 * automatically switching between fitness stats and match data based on
 * the Arsenal FC fixture schedule.
 */
class ArsenalMatchDataSource : ComplicationDataSourceService() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private lateinit var matchRepository: MatchRepository

    override fun onCreate() {
        super.onCreate()
        matchRepository = MatchRepository(applicationContext)
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.LONG_TEXT -> createPreviewLongText()
            ComplicationType.SHORT_TEXT -> createPreviewShortText()
            else -> null
        }
    }

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        scope.launch {
            val fixture = matchRepository.getTodayFixture()
            val currentTime = ZonedDateTime.now()
            val state = determineMatchState(fixture, currentTime)

            val complicationData = when (state) {
                MatchState.NO_MATCH -> createNoMatchData()
                MatchState.MATCH_UPCOMING -> createUpcomingMatchData(fixture!!)
                MatchState.MATCH_LIVE -> createLiveMatchData(fixture!!)
                MatchState.MATCH_HALFTIME -> createHalftimeData(fixture!!)
                MatchState.MATCH_ENDED -> createMatchEndedData(fixture!!)
                MatchState.MATCH_POSTPONED -> createPostponedData(fixture!!)
            }

            listener.onComplicationData(complicationData)
        }
    }

    /**
     * Creates complication data for upcoming match state
     */
    private fun createUpcomingMatchData(fixture: ArsenalFixture): ComplicationData {
        val kickoffFormatted = fixture.kickoffTime
            .atZone(java.time.ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("HH:mm"))

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                "${fixture.homeTeam.shortName} vs ${fixture.awayTeam.shortName}"
            ).build(),
            contentDescription = PlainComplicationText.Builder("MATCH_UPCOMING").build()
        )
            .setTitle(PlainComplicationText.Builder(kickoffFormatted).build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_football)
                ).build()
            )
            .setTapAction(createMatchTapAction(fixture))
            .build()
    }

    /**
     * Creates complication data for live match state
     */
    private fun createLiveMatchData(fixture: ArsenalFixture): ComplicationData {
        val scoreText = "${fixture.homeScore} - ${fixture.awayScore}"
        val minuteText = "${fixture.minute}'"

        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(scoreText).build(),
            contentDescription = PlainComplicationText.Builder("MATCH_LIVE").build()
        )
            .setTitle(PlainComplicationText.Builder("LIVE $minuteText").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, R.drawable.ic_live_indicator)
                ).build()
            )
            .setTapAction(createMatchTapAction(fixture))
            .build()
    }

    /**
     * Creates empty complication data (triggers fitness stats display)
     */
    private fun createNoMatchData(): ComplicationData {
        return NoDataComplicationData()
    }

    /**
     * Determines appropriate update interval based on match state
     */
    override fun getNextUpdateTime(
        complicationInstanceId: Int
    ): Long {
        val fixture = matchRepository.getCachedFixture()
        val state = determineMatchState(fixture, ZonedDateTime.now())

        return when (state) {
            MatchState.MATCH_LIVE -> 60_000L      // 1 minute
            MatchState.MATCH_HALFTIME -> 60_000L // 1 minute
            MatchState.MATCH_UPCOMING -> {
                // Calculate time until kickoff, then update every minute
                val kickoff = fixture?.kickoffTime?.toEpochMilli() ?: return 3_600_000L
                val now = System.currentTimeMillis()
                val timeUntilKickoff = kickoff - now

                when {
                    timeUntilKickoff < 3_600_000 -> 60_000L    // < 1 hour: every minute
                    timeUntilKickoff < 21_600_000 -> 900_000L  // < 6 hours: every 15 min
                    else -> 3_600_000L                          // Otherwise: every hour
                }
            }
            MatchState.MATCH_ENDED -> 300_000L   // 5 minutes
            else -> 21_600_000L                   // 6 hours (no match)
        }
    }
}
```

### 4.2 Data Models

```kotlin
/**
 * Arsenal fixture data model
 */
data class ArsenalFixture(
    val id: Long,
    val competition: Competition,
    val homeTeam: Team,
    val awayTeam: Team,
    val kickoffTime: Instant,
    val status: String,           // SCHEDULED, IN_PLAY, PAUSED, FINISHED, etc.
    val homeScore: Int?,
    val awayScore: Int?,
    val minute: Int?,
    val venue: String?,
    val events: List<MatchEvent>  // Goals, cards, substitutions
) {
    val isArsenalHome: Boolean
        get() = homeTeam.id == ARSENAL_TEAM_ID

    fun isToday(currentTime: ZonedDateTime): Boolean {
        val kickoffDate = kickoffTime.atZone(currentTime.zone).toLocalDate()
        return kickoffDate == currentTime.toLocalDate()
    }

    companion object {
        const val ARSENAL_TEAM_ID = 57L // Arsenal's ID in football-data.org
    }
}

data class Team(
    val id: Long,
    val name: String,
    val shortName: String,
    val crestUrl: String?
)

data class Competition(
    val id: Long,
    val name: String,            // "Premier League", "Champions League", etc.
    val emblemUrl: String?
)

data class MatchEvent(
    val type: EventType,         // GOAL, YELLOW_CARD, RED_CARD, SUBSTITUTION
    val minute: Int,
    val team: Team,
    val player: String,
    val assist: String?
)

enum class EventType {
    GOAL, OWN_GOAL, PENALTY, PENALTY_MISS,
    YELLOW_CARD, RED_CARD,
    SUBSTITUTION
}
```

---

## 5. Phone Companion App Data Sync

### 5.1 WorkManager Background Sync

```kotlin
/**
 * Background worker that fetches Arsenal fixture data from API
 * and syncs to the watch via Wear Data Layer
 */
class MatchDataSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val apiService = FootballDataApiService.create()
    private val dataClient = Wearable.getDataClient(context)

    override suspend fun doWork(): Result {
        return try {
            // Fetch Arsenal fixtures from API
            val response = apiService.getTeamMatches(
                teamId = ARSENAL_TEAM_ID,
                dateFrom = LocalDate.now().toString(),
                dateTo = LocalDate.now().plusDays(7).toString()
            )

            // Find today's match (if any)
            val todayMatch = response.matches.find { match ->
                match.utcDate.toLocalDate() == LocalDate.now()
            }

            // Sync to watch
            syncToWatch(todayMatch)

            // Schedule next sync based on match proximity
            scheduleNextSync(response.matches)

            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            Result.retry()
        }
    }

    private suspend fun syncToWatch(match: Match?) {
        val putDataRequest = PutDataMapRequest.create("/arsenal/match").apply {
            dataMap.apply {
                if (match != null) {
                    putLong("match_id", match.id)
                    putString("home_team", match.homeTeam.shortName)
                    putString("away_team", match.awayTeam.shortName)
                    putLong("kickoff", match.utcDate.toInstant().toEpochMilli())
                    putString("status", match.status)
                    putInt("home_score", match.score.fullTime.home ?: 0)
                    putInt("away_score", match.score.fullTime.away ?: 0)
                    putInt("minute", match.minute ?: 0)
                    putString("venue", match.venue)
                    putString("competition", match.competition.name)
                }
                putLong("sync_time", System.currentTimeMillis())
            }
        }.asPutDataRequest().setUrgent()

        dataClient.putDataItem(putDataRequest).await()
    }

    private fun scheduleNextSync(upcomingMatches: List<Match>) {
        val workManager = WorkManager.getInstance(applicationContext)

        // Cancel existing scheduled work
        workManager.cancelUniqueWork(SYNC_WORK_NAME)

        // Determine next sync time based on fixture proximity
        val nextMatch = upcomingMatches.firstOrNull()
        val delay = calculateSyncDelay(nextMatch)

        val syncRequest = OneTimeWorkRequestBuilder<MatchDataSyncWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    companion object {
        private const val TAG = "MatchDataSync"
        private const val SYNC_WORK_NAME = "arsenal_match_sync"
        private const val ARSENAL_TEAM_ID = 57L
    }
}
```

### 5.2 API Integration (football-data.org)

```kotlin
/**
 * Retrofit service for football-data.org API
 */
interface FootballDataApiService {

    @GET("v4/teams/{teamId}/matches")
    suspend fun getTeamMatches(
        @Path("teamId") teamId: Long,
        @Query("dateFrom") dateFrom: String,
        @Query("dateTo") dateTo: String,
        @Query("status") status: String? = null
    ): MatchesResponse

    @GET("v4/matches/{matchId}")
    suspend fun getMatchDetails(
        @Path("matchId") matchId: Long
    ): Match

    companion object {
        private const val BASE_URL = "https://api.football-data.org/"

        fun create(): FootballDataApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor { chain ->
                            val request = chain.request().newBuilder()
                                .addHeader("X-Auth-Token", BuildConfig.FOOTBALL_API_KEY)
                                .build()
                            chain.proceed(request)
                        }
                        .build()
                )
                .build()
                .create(FootballDataApiService::class.java)
        }
    }
}
```

---

## 6. Timezone Handling

### 6.1 Automatic Timezone Conversion

```kotlin
/**
 * Converts UTC kickoff time to user's local timezone
 * for display on the watch face
 */
object TimezoneConverter {

    /**
     * Formats kickoff time for display
     * @param utcTime Kickoff time in UTC
     * @return Formatted time string in user's local timezone
     */
    fun formatKickoffTime(utcTime: Instant): String {
        val userZone = ZoneId.systemDefault()
        val localTime = utcTime.atZone(userZone)

        return when {
            // Today - just show time
            localTime.toLocalDate() == LocalDate.now() -> {
                localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            // Tomorrow
            localTime.toLocalDate() == LocalDate.now().plusDays(1) -> {
                "Tomorrow ${localTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
            }
            // Within this week
            else -> {
                localTime.format(DateTimeFormatter.ofPattern("EEE HH:mm"))
            }
        }
    }

    /**
     * Calculates time until kickoff for countdown display
     */
    fun timeUntilKickoff(utcTime: Instant): String {
        val duration = Duration.between(Instant.now(), utcTime)

        return when {
            duration.isNegative -> "Started"
            duration.toHours() >= 24 -> "${duration.toDays()}d"
            duration.toHours() >= 1 -> "${duration.toHours()}h ${duration.toMinutesPart()}m"
            duration.toMinutes() >= 1 -> "${duration.toMinutes()}m"
            else -> "Now!"
        }
    }
}
```

---

## 7. WFF Expression Reference

### 7.1 Available Expressions for Match Data

```xml
<!--
    Custom complication expressions available in WFF
    when using the ArsenalMatchDataSource
-->

<!-- State identifier (used for conditional rendering) -->
[COMPLICATION.3.CONTENT_DESCRIPTION]
<!-- Values: "NO_MATCH", "MATCH_UPCOMING", "MATCH_LIVE", "MATCH_ENDED" -->

<!-- Main text (score or matchup) -->
[COMPLICATION.3.TEXT]
<!-- Examples: "2 - 1", "ARS vs LIV" -->

<!-- Title text (time or status) -->
[COMPLICATION.3.TITLE]
<!-- Examples: "17:30", "LIVE 45'" -->

<!-- Icon availability -->
[COMPLICATION.3.MONOCHROMATIC_IMAGE != null]

<!-- Data type check -->
[COMPLICATION.3.TYPE]
<!-- Values: LONG_TEXT, SHORT_TEXT, NO_DATA -->
```

### 7.2 Combining with System Data Sources

```xml
<!--
    Fallback to fitness data when no match
-->
<Condition>
    <Expressions>
        <Expression name="has_match">
            [COMPLICATION.3.TYPE != NO_DATA]
        </Expression>
    </Expressions>

    <Compare expression="has_match" value="false">
        <!-- Use system step count -->
        <PartText>
            <Text>
                <Template>
                    <Parameter expression="[STEP_COUNT]" /> steps
                </Template>
            </Text>
        </PartText>
    </Compare>
</Condition>
```

---

## 8. Error Handling & Fallbacks

### 8.1 Network Failure Handling

```kotlin
/**
 * Fallback behavior when API is unavailable
 */
sealed class MatchDataResult {
    data class Success(val fixture: ArsenalFixture?) : MatchDataResult()
    data class Cached(val fixture: ArsenalFixture?, val age: Duration) : MatchDataResult()
    object NoData : MatchDataResult()
    data class Error(val exception: Exception) : MatchDataResult()
}

fun handleDataResult(result: MatchDataResult): ComplicationData {
    return when (result) {
        is MatchDataResult.Success -> createMatchData(result.fixture)
        is MatchDataResult.Cached -> {
            // Use cached data with "stale" indicator if > 1 hour old
            if (result.age.toHours() > 1) {
                createMatchDataWithStaleIndicator(result.fixture)
            } else {
                createMatchData(result.fixture)
            }
        }
        is MatchDataResult.NoData,
        is MatchDataResult.Error -> NoDataComplicationData() // Triggers fitness stats
    }
}
```

### 8.2 Complication Placeholder

```xml
<!--
    Placeholder shown while complication loads
-->
<ComplicationSlot
    slotId="3"
    ...>
    <Placeholder>
        <PartText x="165" y="360" width="120" height="30">
            <Text align="CENTER">
                <Font family="@font/arsenal_sans_medium" size="14">
                    <Localization>Loading...</Localization>
                </Font>
                <TextColor color="#4A5568" />
            </Text>
        </PartText>
    </Placeholder>
</ComplicationSlot>
```

---

## 9. Battery Optimization Strategies

### 9.1 Adaptive Polling

```kotlin
/**
 * Adjusts update frequency based on device state
 */
class AdaptiveUpdateScheduler(private val context: Context) {

    fun getOptimalInterval(state: MatchState): Long {
        val batteryManager = context.getSystemService(BatteryManager::class.java)
        val batteryPercent = batteryManager.getIntProperty(
            BatteryManager.BATTERY_PROPERTY_CAPACITY
        )
        val isCharging = batteryManager.isCharging

        // Base intervals by state
        val baseInterval = when (state) {
            MatchState.MATCH_LIVE -> 60_000L      // 1 min
            MatchState.MATCH_HALFTIME -> 60_000L  // 1 min
            MatchState.MATCH_UPCOMING -> 300_000L // 5 min
            else -> 3_600_000L                     // 1 hour
        }

        // Adjust based on battery
        return when {
            isCharging -> baseInterval // Normal when charging
            batteryPercent > 50 -> baseInterval
            batteryPercent > 20 -> baseInterval * 2 // Double interval
            else -> baseInterval * 4 // Low battery mode
        }
    }
}
```

### 9.2 Conditional Feature Disable

```kotlin
/**
 * Disables non-essential features in low battery mode
 */
object BatteryAwareFeatures {

    fun shouldShowAnimations(context: Context): Boolean {
        val batteryPercent = getBatteryPercent(context)
        return batteryPercent > 15
    }

    fun shouldPollLiveScore(context: Context): Boolean {
        val batteryPercent = getBatteryPercent(context)
        return batteryPercent > 10
    }

    fun getRefreshStrategy(context: Context): RefreshStrategy {
        val batteryPercent = getBatteryPercent(context)
        return when {
            batteryPercent > 50 -> RefreshStrategy.NORMAL
            batteryPercent > 20 -> RefreshStrategy.REDUCED
            else -> RefreshStrategy.MINIMAL
        }
    }
}
```

---

## 10. Testing & Debugging

### 10.1 Debug Overlay

```xml
<!--
    Debug overlay for development (remove in production)
-->
<Condition>
    <Expressions>
        <Expression name="debug_mode">[CONFIGURATION.debug_enabled]</Expression>
    </Expressions>
    <Compare expression="debug_mode" value="true">
        <Group name="debug_overlay">
            <PartText x="10" y="420" width="200" height="20">
                <Text align="LEFT">
                    <Font size="8">
                        <Template>
                            State: <Parameter expression="[COMPLICATION.3.CONTENT_DESCRIPTION]" />
                        </Template>
                    </Font>
                    <TextColor color="#FF0000" />
                </Text>
            </PartText>
        </Group>
    </Compare>
</Condition>
```

### 10.2 Mock Data Provider

```kotlin
/**
 * Mock data source for testing match states
 */
class MockArsenalMatchDataSource : ComplicationDataSourceService() {

    // Cycle through states for testing
    private val testStates = listOf(
        MatchState.NO_MATCH,
        MatchState.MATCH_UPCOMING,
        MatchState.MATCH_LIVE,
        MatchState.MATCH_HALFTIME,
        MatchState.MATCH_ENDED
    )

    private var currentStateIndex = 0

    override fun onComplicationRequest(
        request: ComplicationRequest,
        listener: ComplicationRequestListener
    ) {
        val state = testStates[currentStateIndex]
        currentStateIndex = (currentStateIndex + 1) % testStates.size

        val mockFixture = createMockFixture(state)
        // ... create complication data based on state
    }

    private fun createMockFixture(state: MatchState): ArsenalFixture {
        return ArsenalFixture(
            id = 12345L,
            competition = Competition(39, "Premier League", null),
            homeTeam = Team(57, "Arsenal FC", "ARS", null),
            awayTeam = Team(64, "Liverpool FC", "LIV", null),
            kickoffTime = when (state) {
                MatchState.MATCH_UPCOMING -> Instant.now().plusSeconds(3600)
                MatchState.MATCH_LIVE -> Instant.now().minusSeconds(2700)
                else -> Instant.now()
            },
            status = when (state) {
                MatchState.MATCH_LIVE -> "IN_PLAY"
                MatchState.MATCH_HALFTIME -> "PAUSED"
                MatchState.MATCH_ENDED -> "FINISHED"
                else -> "SCHEDULED"
            },
            homeScore = if (state in listOf(MatchState.MATCH_LIVE, MatchState.MATCH_ENDED)) 2 else null,
            awayScore = if (state in listOf(MatchState.MATCH_LIVE, MatchState.MATCH_ENDED)) 1 else null,
            minute = if (state == MatchState.MATCH_LIVE) 67 else null,
            venue = "Emirates Stadium",
            events = emptyList()
        )
    }
}
```

---

*Document Version: 1.0.0*
*Last Updated: 2026-01-28*
*Related: DESIGN_SPECIFICATION.md, watchface.xml*
