package app.kobuggi.hyuabot.ui.subway

data class SubwayArrivalItem(
    val remainedTime: Int,
    val terminalStation: String,
    val currentStation: String?,
)