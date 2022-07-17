package app.kobuggi.hyuabot.ui.cafeteria

import app.kobuggi.hyuabot.CafeteriaMenuQuery

data class CafeteriaTimeItem(
    val timeType: String,
    val menuList: List<CafeteriaMenuQuery.Menu>,
    val isExpand: Boolean,
)