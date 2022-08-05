package app.kobuggi.hyuabot.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.CalendarDatabaseItem
import app.kobuggi.hyuabot.databinding.FragmentCalendarDayScheduleBinding

class CalendarDayDialog : DialogFragment() {
    private lateinit var binding: FragmentCalendarDayScheduleBinding
    private val parentViewModel : CalendarViewModel by viewModels({requireParentFragment()})

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarDayScheduleBinding.inflate(inflater, container, false)

        val currentDate = parentViewModel.clickedDate.value!!
        binding.calendarDayName.text = getString(R.string.schedule_date, currentDate.date.monthValue, currentDate.date.dayOfMonth)
        val groupedSchedule = mutableMapOf(0 to arrayListOf<CalendarDatabaseItem>(), 1 to arrayListOf(), 2 to arrayListOf(), 3 to arrayListOf(), 4 to arrayListOf())
        parentViewModel.showSchedule.value!!.entries.forEach { entry ->
            if (entry.key in 1..4) {
                groupedSchedule[entry.key]!!.addAll(entry.value)
            } else {
                groupedSchedule[0]!!.addAll(entry.value)
            }
        }
        val groupAdapter = ScheduleGroupAdapter(requireContext(), groupedSchedule.filter { it.value.isNotEmpty() })
        if (groupedSchedule.filter { it.value.isNotEmpty() }.keys.isEmpty()) {
            binding.calendarDayNoData.visibility = View.VISIBLE
            binding.calendarDayScheduleList.visibility = View.GONE
        }
        binding.calendarDayScheduleList.adapter = groupAdapter
        binding.calendarDayScheduleList.layoutManager = LinearLayoutManager(requireContext())
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}