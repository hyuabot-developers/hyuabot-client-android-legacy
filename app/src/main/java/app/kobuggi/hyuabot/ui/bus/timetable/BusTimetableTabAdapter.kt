package app.kobuggi.hyuabot.ui.bus.timetable

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.kobuggi.hyuabot.BusQuery


class BusTimetableTabAdapter(fragment: BusTimetableFragment, private val timetable : List<BusQuery.Timetable>): FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> BusTimetableTab(timetable.filter { it.weekday == "weekdays" })
            1 -> BusTimetableTab(timetable.filter { it.weekday == "saturday" })
            else -> BusTimetableTab(timetable.filter { it.weekday == "sunday" })
        }
    }
}