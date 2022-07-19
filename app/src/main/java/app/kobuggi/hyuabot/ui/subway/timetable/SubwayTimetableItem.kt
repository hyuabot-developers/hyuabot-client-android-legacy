package app.kobuggi.hyuabot.ui.subway.timetable

import java.time.LocalDateTime

data class SubwayTimetableItem(
    val terminalStation: String,
    val departureTime: LocalDateTime,
)
