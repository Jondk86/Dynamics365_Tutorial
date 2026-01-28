package com.arsenal.watchface.data

import java.time.Instant
import java.time.ZonedDateTime

/**
 * Match state enumeration for UI rendering
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
    companion object {
        const val ARSENAL_TEAM_ID = 57L
    }

    val isArsenalHome: Boolean
        get() = homeTeam.id == ARSENAL_TEAM_ID

    val arsenalScore: Int?
        get() = if (isArsenalHome) homeScore else awayScore

    val opponentScore: Int?
        get() = if (isArsenalHome) awayScore else homeScore

    val opponent: Team
        get() = if (isArsenalHome) awayTeam else homeTeam

    fun isToday(currentTime: ZonedDateTime): Boolean {
        val kickoffDate = kickoffTime.atZone(currentTime.zone).toLocalDate()
        return kickoffDate == currentTime.toLocalDate()
    }

    fun getResult(): MatchResult {
        val arsenalGoals = arsenalScore ?: return MatchResult.UNKNOWN
        val opponentGoals = opponentScore ?: return MatchResult.UNKNOWN

        return when {
            arsenalGoals > opponentGoals -> MatchResult.WIN
            arsenalGoals < opponentGoals -> MatchResult.LOSS
            else -> MatchResult.DRAW
        }
    }
}

enum class MatchResult {
    WIN, DRAW, LOSS, UNKNOWN
}

/**
 * Team data model
 */
data class Team(
    val id: Long,
    val name: String,
    val shortName: String,
    val crestUrl: String? = null
)

/**
 * Competition data model
 */
data class Competition(
    val id: Long,
    val name: String,
    val emblemUrl: String? = null
) {
    companion object {
        val PREMIER_LEAGUE = Competition(39, "Premier League")
        val CHAMPIONS_LEAGUE = Competition(2, "Champions League")
        val FA_CUP = Competition(45, "FA Cup")
        val LEAGUE_CUP = Competition(48, "EFL Cup")
    }
}

/**
 * Match event (goals, cards, etc.)
 */
data class MatchEvent(
    val type: EventType,
    val minute: Int,
    val team: Team,
    val player: String,
    val assist: String? = null
)

enum class EventType {
    GOAL,
    OWN_GOAL,
    PENALTY,
    PENALTY_MISS,
    YELLOW_CARD,
    RED_CARD,
    SUBSTITUTION
}

/**
 * Common team definitions for quick lookup
 */
object Teams {
    val ARSENAL = Team(57, "Arsenal FC", "ARS")
    val CHELSEA = Team(61, "Chelsea FC", "CHE")
    val LIVERPOOL = Team(64, "Liverpool FC", "LIV")
    val MAN_CITY = Team(65, "Manchester City FC", "MCI")
    val MAN_UNITED = Team(66, "Manchester United FC", "MUN")
    val TOTTENHAM = Team(73, "Tottenham Hotspur FC", "TOT")
}
