package app.kobuggi.hyuabot.ui.main.subway

data class SubwayArrivalItem(
    val remainedTime: Int,
    val terminalStation: String,
    val currentStation: String?,
)