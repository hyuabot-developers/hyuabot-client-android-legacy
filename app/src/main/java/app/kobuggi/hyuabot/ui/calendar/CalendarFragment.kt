package app.kobuggi.hyuabot.ui.calendar

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.FragmentCalendarBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.MonthScrollListener
import dagger.hilt.android.AndroidEntryPoint
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
        vm.getAllEvents()
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
                    container.dayTextView.setTextColor(Color.WHITE)
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

        val eventAdapter = CalendarEventAdapter(requireContext(), arrayListOf())
        binding.eventListOfMonth.adapter = eventAdapter
        binding.eventListOfMonth.layoutManager = LinearLayoutManager(requireContext())
        vm.eventsOfMonth.observe(viewLifecycleOwner) {
            eventAdapter.setEvents(it)
        }

        val currentMonth = YearMonth.now()
        val firstMonth = YearMonth.of(if (currentMonth.monthValue > 2) currentMonth.year else currentMonth.year - 1, 3)
        val lastMonth = YearMonth.of(if (currentMonth.monthValue > 2) currentMonth.year + 1 else currentMonth.year, 2)
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        binding.calendarView.setup(firstMonth, lastMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)
        return binding.root
    }
}