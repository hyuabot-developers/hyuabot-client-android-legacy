package app.kobuggi.hyuabot.ui.shuttle.timetable

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.FragmentShuttleTimetableTabBinding
import app.kobuggi.hyuabot.utils.Event
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class ShuttleTimetableTab(private val timetable : List<ShuttleTimetableQuery.Timetable>, private val stopID: Int, private val shuttleType: Int): Fragment(), DialogInterface.OnDismissListener {
    private val vm by viewModels<ShuttleTimetableTabViewModel>()
    private lateinit var binding : FragmentShuttleTimetableTabBinding
    constructor() : this(listOf(), 0, 0)
    private val timeDeltaIndex = when(shuttleType){
        R.string.shuttle_type_DH -> 0
        R.string.shuttle_type_DY -> 1
        R.string.shuttle_type_C -> 2
        else -> 0
    }

    private val shuttleTypeName = when(shuttleType){
        R.string.shuttle_type_DH -> "DH"
        R.string.shuttle_type_DY -> "DY"
        R.string.shuttle_type_C -> "C"
        else -> "DH"
    }

    private val timeDelta = hashMapOf(
        R.string.dormitory to arrayListOf(-5, -5, -5),
        R.string.shuttlecock_o to arrayListOf(0, 0, 0),
        R.string.station to arrayListOf(10, 0, 10),
        R.string.terminal to arrayListOf(0, 10, 15),
        R.string.shuttlecock_i to arrayListOf(20, 20, 25)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShuttleTimetableTabBinding.inflate(inflater, container, false)
        binding.vm = vm
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val modifiedTimetable = timetable.filter { it.shuttleType == shuttleTypeName }.map {
            LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopID]!![timeDeltaIndex].toLong())
        }
        val adapter = ShuttleTimetableAdapter(requireContext(), modifiedTimetable, timetable.map { it.startStop }) {
            arrivalTime, startStop -> vm.openShuttleRouteDialog(arrivalTime, startStop)
        }
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
        vm.showShuttleRouteDialog.observe(viewLifecycleOwner){
            if(it.peekContent()){
                val dialog = ShuttleTimetableDialog().newInstance(vm.arrivalTime.value!!, vm.startStop.value!!, stopID, shuttleType)
                dialog.show(requireActivity().supportFragmentManager, "ShuttleTimetableMapDialog")
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        vm.showShuttleRouteDialog.value = Event(false)
    }

    override fun onDismiss(p0: DialogInterface?) {
        vm.showShuttleRouteDialog.value = Event(false)
    }
}