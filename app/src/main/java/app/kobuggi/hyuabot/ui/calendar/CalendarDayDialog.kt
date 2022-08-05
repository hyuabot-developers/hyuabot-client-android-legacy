package app.kobuggi.hyuabot.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import app.kobuggi.hyuabot.R
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