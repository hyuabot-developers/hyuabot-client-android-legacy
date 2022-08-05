package app.kobuggi.hyuabot.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.CalendarDatabaseItem
import app.kobuggi.hyuabot.databinding.FragmentCalendarBinding
import app.kobuggi.hyuabot.utils.Event
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.MonthScrollListener
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.*

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private val vm by viewModels<CalendarViewModel>()
    private lateinit var binding: FragmentCalendarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = vm
        (binding.calendarView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        binding.calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer>{
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                container.headerTextView.text = requireContext().getString(R.string.month_header, month.year, month.month)
            }
        }
        binding.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.dayTextView.text = day.date.dayOfMonth.toString()
                if (day.owner == DayOwner.THIS_MONTH){
                    container.dayTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.primaryTextColor, null))
                    val eventsOfDay = countScheduleOfDay(day)
                    container.firstSchedule.visibility = if (eventsOfDay.containsKey(1)) View.VISIBLE else View.INVISIBLE
                    container.secondSchedule.visibility = if (eventsOfDay.containsKey(2)) View.VISIBLE else View.INVISIBLE
                    container.thirdSchedule.visibility = if (eventsOfDay.containsKey(3)) View.VISIBLE else View.INVISIBLE
                    container.fourthSchedule.visibility = if (eventsOfDay.containsKey(4)) View.VISIBLE else View.INVISIBLE
                    container.otherSchedule.visibility = if (eventsOfDay.keys.any { it < 1 }) View.VISIBLE else View.INVISIBLE

                    container.dayItem.setOnClickListener {
                        vm.clickedDate.value = day
                        vm.showSchedule.value = eventsOfDay
                        vm.showDaySchedule.value = Event(true)
                    }

                } else {
                    container.dayTextView.setTextColor(Color.GRAY)
                }
            }
        }
        binding.calendarView.monthScrollListener = object : MonthScrollListener {
            override fun invoke(currentMonth: CalendarMonth) {
                vm.onCalendarMonthChanged(currentMonth.yearMonth)
            }
        }

        val eventAdapter = CalendarEventAdapter(requireContext(), arrayListOf(),
            { item : CalendarDatabaseItem -> run {  } },
            { previousPosition: Int, currentPosition: Int -> setSelectedItem(previousPosition, currentPosition) }
        )
        binding.eventListOfMonth.adapter = eventAdapter
        binding.eventListOfMonth.layoutManager = LinearLayoutManager(requireContext())
        vm.eventsOfMonth.observe(viewLifecycleOwner) {
            eventAdapter.setEvents(it)
            binding.calendarView.notifyMonthChanged(vm.currentMonthData.value!!)
        }

        val currentMonth = YearMonth.now()
        val firstMonth = YearMonth.of(if (currentMonth.monthValue > 2) currentMonth.year else currentMonth.year - 1, 3)
        val lastMonth = YearMonth.of(if (currentMonth.monthValue > 2) currentMonth.year + 1 else currentMonth.year, 2)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, resources.getStringArray(R.array.target_grade))
        binding.targetGradeSpinner.adapter = spinnerAdapter
        binding.targetGradeSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
            }
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                vm.filterByGrade(position)
            }
        }
        vm.showDaySchedule.observe(viewLifecycleOwner) {
            if (it.peekContent()){
                val dialog = CalendarDayDialog()
                dialog.show(childFragmentManager, "dialog")
            }
        }
        return binding.root
    }

    private fun setSelectedItem(previousPosition: Int, currentPosition: Int) {
        if(previousPosition != -1) {
            binding.eventListOfMonth.findViewHolderForAdapterPosition(previousPosition)?.itemView!!.findViewById<TextView>(R.id.event_title).isSelected = false
        }
        binding.eventListOfMonth.findViewHolderForAdapterPosition(currentPosition)?.itemView!!.findViewById<TextView>(R.id.event_title).isSelected = true
    }

    private fun countScheduleOfDay(day: CalendarDay): Map<Int, List<CalendarDatabaseItem>> {
        val startOfDay = LocalDateTime.of(day.date, LocalDateTime.MIN.toLocalTime())
        val endOfDay = LocalDateTime.of(day.date, LocalDateTime.MAX.toLocalTime())
        return vm.eventsOfMonth.value?.filter {
            (LocalDateTime.parse(it.startDate!!.split("+")[0]) <= startOfDay && LocalDateTime.parse(it.endDate!!.split("+")[0]) > startOfDay) ||
            (LocalDateTime.parse(it.startDate.split("+")[0]) in startOfDay..endOfDay)
        }?.groupBy { it.targetGrade!! } ?: mapOf()
    }
}