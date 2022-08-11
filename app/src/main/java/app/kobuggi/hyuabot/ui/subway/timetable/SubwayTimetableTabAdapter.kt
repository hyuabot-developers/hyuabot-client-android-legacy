package app.kobuggi.hyuabot.ui.subway.timetable

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.kobuggi.hyuabot.SubwayQuery


class SubwayTimetableTabAdapter(fragment: SubwayTimetableFragment, private val timetable : List<SubwayQuery.Timetable>): FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SubwayTimetableTab(timetable.filter { it.weekday == "weekdays" })
            1 -> SubwayTimetableTab(timetable.filter { it.weekday == "weekends" })
            else -> SubwayTimetableTab(timetable.filter { it.weekday == "weekdays" })
        }
    }
}