package app.kobuggi.hyuabot.ui.calendar

import android.view.View
import app.kobuggi.hyuabot.databinding.ItemDayLayoutBinding
import com.kizitonwose.calendarview.ui.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val dayTextView = ItemDayLayoutBinding.bind(view).calendarDayText
}