package app.kobuggi.hyuabot.ui.calendar

import android.view.View
import app.kobuggi.hyuabot.databinding.ItemCalendarHeaderLayoutBinding
import com.kizitonwose.calendarview.ui.ViewContainer

class MonthViewContainer(view: View) : ViewContainer(view) {
    val headerTextView = ItemCalendarHeaderLayoutBinding.bind(view).calendarHeader
}