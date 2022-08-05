package app.kobuggi.hyuabot.ui.calendar

import android.view.View
import app.kobuggi.hyuabot.databinding.ItemDayLayoutBinding
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val dayTextView = ItemDayLayoutBinding.bind(view).calendarDayText
    val firstSchedule = ItemDayLayoutBinding.bind(view).calendarDayScheduleFirst
    val secondSchedule = ItemDayLayoutBinding.bind(view).calendarDayScheduleSecond
    val thirdSchedule = ItemDayLayoutBinding.bind(view).calendarDayScheduleThird
    val fourthSchedule = ItemDayLayoutBinding.bind(view).calendarDayScheduleFourth
    val otherSchedule = ItemDayLayoutBinding.bind(view).calendarDayScheduleOther
}
