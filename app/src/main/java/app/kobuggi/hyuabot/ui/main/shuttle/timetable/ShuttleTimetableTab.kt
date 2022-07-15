package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.FragmentShuttleTimetableBinding
import app.kobuggi.hyuabot.databinding.FragmentShuttleTimetableTabBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class ShuttleTimetableTab(private val timetable : List<ShuttleTimetableQuery.Timetable>, private val timeDelta: Int): Fragment() {
    private lateinit var binding : FragmentShuttleTimetableTabBinding
    constructor() : this(listOf(), 0)
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleTimetableTabBinding.inflate(inflater, container, false)
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val modifiedTimetable = timetable.map {
            LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta.toLong())
        }
        val adapter = ShuttleTimetableAdapter(requireContext(), modifiedTimetable)
        binding.shuttleTimetableList.adapter = adapter
        binding.shuttleTimetableList.layoutManager = LinearLayoutManager(requireContext())
        binding.shuttleTimetableList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        if(modifiedTimetable.isNotEmpty()){
            binding.shuttleTimetableList.smoothScrollToPosition((
                    modifiedTimetable.indexOf(modifiedTimetable.first { it.isAfter(LocalTime.now()) }) + 5).coerceAtMost(modifiedTimetable.size - 1)
            )
        }
        return binding.root
    }
}