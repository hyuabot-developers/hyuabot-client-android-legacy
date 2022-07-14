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


class ShuttleTimetableTab(private val timetable : List<ShuttleTimetableQuery.Timetable>, private val timeDelta: Int): Fragment() {
    private lateinit var binding : FragmentShuttleTimetableTabBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleTimetableTabBinding.inflate(inflater, container, false)

        val adapter = ShuttleTimetableAdapter(requireContext(), timetable, timeDelta)
        binding.shuttleTimetableList.adapter = adapter
        binding.shuttleTimetableList.layoutManager = LinearLayoutManager(requireContext())
        binding.shuttleTimetableList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        return binding.root
    }
}