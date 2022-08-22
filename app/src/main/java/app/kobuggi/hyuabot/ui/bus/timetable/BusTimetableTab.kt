package app.kobuggi.hyuabot.ui.bus.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.databinding.FragmentBusTimetableTabBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class BusTimetableTab(private val timetable : List<BusQuery.Timetable>): Fragment() {
    private lateinit var binding : FragmentBusTimetableTabBinding
    constructor() : this(listOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBusTimetableTabBinding.inflate(inflater, container, false)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val adapter = BusTimetableAdapter(requireContext(), timetable.map { LocalTime.parse(it.departureTime.toString(), formatter) })
        binding.busTimetableList.adapter = adapter
        binding.busTimetableList.layoutManager = LinearLayoutManager(requireContext())
        binding.busTimetableList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        if(timetable.isNotEmpty()){
            binding.busTimetableList.smoothScrollToPosition((
                timetable.indexOf(timetable.first { LocalTime.parse(it.departureTime.toString(), formatter).isAfter(LocalTime.now()) }) + 5).coerceAtMost(timetable.size - 1)
            )
        } else {
            binding.busTimetableList.visibility = View.GONE
            binding.busTimetableNoData.visibility = View.VISIBLE
        }
        return binding.root
    }
}