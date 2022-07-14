package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.kobuggi.hyuabot.ShuttleTimetableQuery


class ShuttleTimetableTabAdapter(fragment: ShuttleTimetableFragment, private val timetable : List<ShuttleTimetableQuery.Timetable>, private val timeDelta: Int): FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            ShuttleTimetableTab(timetable.filter { it.weekday == "weekdays" }, timeDelta)
        } else {
            ShuttleTimetableTab(timetable.filter { it.weekday == "weekends" }, timeDelta)
        }
    }
}