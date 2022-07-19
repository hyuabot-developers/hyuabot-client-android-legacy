package app.kobuggi.hyuabot.ui.subway.timetable

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.SubwayQuery
import app.kobuggi.hyuabot.databinding.FragmentSubwayTimetableTabBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class SubwayTimetableTab(private val timetable : List<SubwayQuery.Timetable>): Fragment() {
    private lateinit var binding : FragmentSubwayTimetableTabBinding
    constructor() : this(listOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubwayTimetableTabBinding.inflate(inflater, container, false)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val adapter = SubwayTimetableAdapter(requireContext(), timetable)
        binding.subwayTimetableList.adapter = adapter
        binding.subwayTimetableList.layoutManager = LinearLayoutManager(requireContext())
        binding.subwayTimetableList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        if(timetable.isNotEmpty()){
            binding.subwayTimetableList.smoothScrollToPosition((
                timetable.indexOf(timetable.first { LocalTime.parse(it.departureTime, formatter).isAfter(LocalTime.now()) }) + 5).coerceAtMost(timetable.size - 1)
            )
        }
        return binding.root
    }
}