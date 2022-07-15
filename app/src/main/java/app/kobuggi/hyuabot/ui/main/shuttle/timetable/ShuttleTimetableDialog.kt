package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.DialogTimetableBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class ShuttleTimetableDialog(private val arrivalTime: LocalTime, private val startStop: String, private val stopID: Int, private val shuttleType: Int) : DialogFragment() {
    inner class ShuttleDepartureItem(val departureTime: LocalTime, val stopID: Int)

    private lateinit var binding: DialogTimetableBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTimetableBinding.inflate(inflater, container, false)
        val adapter = ShuttleArrivalListAdapter(requireContext(), getArrivalInfoList())
        binding.shuttleArrivalTimeList.adapter = adapter
        binding.shuttleArrivalTimeList.layoutManager = LinearLayoutManager(requireContext())
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
        val shuttlecockTime = when(stopID){
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