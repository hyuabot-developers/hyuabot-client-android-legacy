package app.kobuggi.hyuabot.ui.shuttle.timetable

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.DialogTimetableBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ShuttleTimetableDialog : DialogFragment() {
    inner class ShuttleDepartureItem(val departureTime: LocalTime, val stopID: Int)

    private lateinit var binding: DialogTimetableBinding
    private val vm by viewModels<ShuttleTimetableDialogViewModel>()
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")
    private lateinit var arrivalTime: LocalTime
    private lateinit var startStop: String
    private var stopID: Int = 0
    private var shuttleType: Int = 0


    fun newInstance(arrivalTime: LocalTime, startStop: String, stopID: Int, shuttleType: Int): ShuttleTimetableDialog{
        val fragment = ShuttleTimetableDialog()
        val bundle = Bundle(4)
        bundle.putString("arrivalTime", arrivalTime.format(formatter))
        bundle.putString("startStop", startStop)
        bundle.putInt("stopID", stopID)
        bundle.putInt("shuttleType", shuttleType)
        fragment.arguments = bundle
        return fragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrivalTime = LocalTime.parse(arguments?.getString("arrivalTime") ?: "", formatter)
        startStop = arguments?.getString("startStop") ?: ""
        stopID = arguments?.getInt("stopID") ?: 0
        shuttleType = arguments?.getInt("shuttleType") ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (startStop.isNotEmpty()) {
            vm.setData(arrivalTime, startStop, stopID, shuttleType)
        }
        binding = DialogTimetableBinding.inflate(inflater, container, false)
        if (getArrivalInfoList().isEmpty()){
            dismiss()
        }
        val adapter = ShuttleArrivalListAdapter(requireContext(), getArrivalInfoList())
        binding.shuttleArrivalTimeList.adapter = adapter
        binding.shuttleArrivalTimeList.layoutManager = LinearLayoutManager(requireContext())
        binding.vm = vm
        binding.shuttleArrivalTimeList.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
        return binding.root
    }


    override fun onResume() {
        super.onResume()
        dialog!!.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val parentFragment = parentFragment
        if (parentFragment is ShuttleTimetableTab) {
            parentFragment.onDismiss(dialog)
        }
    }

    private fun getArrivalInfoList(): ArrayList<ShuttleDepartureItem> {
        val result = arrayListOf<ShuttleDepartureItem>()
        val shuttlecockTime = when(vm.stopID.value){
            R.string.dormitory -> arrivalTime.plusMinutes(5)
            R.string.shuttlecock_o -> arrivalTime
            R.string.station -> arrivalTime.minusMinutes(10)
            R.string.terminal -> if (shuttleType == R.string.shuttle_type_DY) arrivalTime.minusMinutes(10) else arrivalTime.minusMinutes(15)
            R.string.shuttlecock_i -> if (shuttleType == R.string.shuttle_type_C) arrivalTime.minusMinutes(25) else arrivalTime.minusMinutes(20)
            else -> arrivalTime
        }
        if (startStop == "Dormitory"){
            when(shuttleType){
                R.string.shuttle_type_DH -> {
                    result.add(ShuttleDepartureItem(shuttlecockTime.minusMinutes(5), R.string.dormitory))
                    result.add(ShuttleDepartureItem(shuttlecockTime, R.string.shuttlecock_o))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(10), R.string.station))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(20), R.string.shuttlecock_i))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(25), R.string.dormitory))
                }
                R.string.shuttle_type_DY -> {
                    result.add(ShuttleDepartureItem(shuttlecockTime.minusMinutes(5), R.string.dormitory))
                    result.add(ShuttleDepartureItem(shuttlecockTime, R.string.shuttlecock_o))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(10), R.string.terminal))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(20), R.string.shuttlecock_i))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(25), R.string.dormitory))
                }
                R.string.shuttle_type_C -> {
                    result.add(ShuttleDepartureItem(shuttlecockTime.minusMinutes(5), R.string.dormitory))
                    result.add(ShuttleDepartureItem(shuttlecockTime, R.string.shuttlecock_o))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(10), R.string.station))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(15), R.string.terminal))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(25), R.string.shuttlecock_i))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(30), R.string.dormitory))
                }
            }
        } else {
            when(shuttleType){
                R.string.shuttle_type_DH -> {
                    result.add(ShuttleDepartureItem(shuttlecockTime, R.string.shuttlecock_o))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(10), R.string.station))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(20), R.string.shuttlecock_i))
                }
                R.string.shuttle_type_DY -> {
                    result.add(ShuttleDepartureItem(shuttlecockTime, R.string.shuttlecock_o))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(10), R.string.terminal))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(20), R.string.shuttlecock_i))
                }
                R.string.shuttle_type_C -> {
                    result.add(ShuttleDepartureItem(shuttlecockTime, R.string.shuttlecock_o))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(10), R.string.station))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(15), R.string.terminal))
                    result.add(ShuttleDepartureItem(shuttlecockTime.plusMinutes(25), R.string.shuttlecock_i))
                }
            }
        }
        return result
    }
}