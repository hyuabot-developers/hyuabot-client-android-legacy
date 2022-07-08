package app.kobuggi.hyuabot.ui.main.shuttle

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.CardShuttleArrivalBinding
import com.google.android.gms.maps.model.LatLng
import java.lang.Integer.min
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShuttleArrivalListAdapter(private val context: Context, timetable: List<ShuttleTimetableQuery.Timetable>, val onClickLocationButton: (location: LatLng, titleID: Int) -> Unit) : RecyclerView.Adapter<ShuttleArrivalListAdapter.ShuttleArrivalViewHolder>() {
    private val shuttleStopNameList = listOf(R.string.dormitory, R.string.shuttlecock_o, R.string.station, R.string.terminal, R.string.shuttlecock_i)
    private var shuttleTimetable: List<ShuttleTimetableQuery.Timetable> = timetable
    private var closestStopIndex = -1
    private val timeDelta = hashMapOf(
        R.string.dormitory to arrayListOf(-5, -5, -5),
        R.string.shuttlecock_o to arrayListOf(0, 0, 0),
        R.string.station to arrayListOf(10, 0, 10),
        R.string.terminal to arrayListOf(0, 10, 15),
        R.string.shuttlecock_i to arrayListOf(20, 20, 25)
    )
    private val stopLocationList = listOf(
        LatLng(37.29339607529377, 126.83630604103446),
        LatLng(37.29875417910844, 126.83784054072336),
        LatLng(37.308494476826155, 126.85310236423418),
        LatLng(37.31945164682341, 126.8455453372041),
        LatLng(37.29869328231496, 126.8377767466817)
    )
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    inner class ShuttleArrivalViewHolder(private val binding: CardShuttleArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.shuttleStopName.text = context.getString(shuttleStopNameList[position])

            if (shuttleStopNameList[position] == R.string.station) {
                binding.shuttleDY.visibility = View.GONE
                binding.shuttleStopDivider.visibility = View.GONE
            } else if (shuttleStopNameList[position] == R.string.terminal) {
                binding.shuttleDH.visibility = View.GONE
                binding.shuttleStopDivider.visibility = View.GONE
            }

            val now = LocalTime.now()
            val timetableByStop = if(position == 0){
                shuttleTimetable.filter { it.startStop == "Dormitory" }
            } else {
                shuttleTimetable
            }

            Log.d("ShuttleArrivalListAdapter", "timetableByStop: ${timetableByStop.size}")
            val shuttleDHTime = timetableByStop.filter { it.shuttleType == "DH" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![0].toLong()) > now }.map {
                LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![0].toLong())
            }.toList()
            val shuttleDHListAdapter = ShuttleArrivalTimeAdapter(context, now, shuttleDHTime.subList(0, if(binding.expandButton.isSelected) min(shuttleDHTime.size, 5) else min(shuttleDHTime.size, 2)))
            binding.shuttleDHList.adapter = shuttleDHListAdapter
            binding.shuttleDHList.layoutManager = LinearLayoutManager(context)

            val shuttleDYTime = timetableByStop.filter { it.shuttleType == "DY" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![1].toLong()) > now }.map {
                LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![1].toLong())
            }.toList()
            val shuttleDYListAdapter = ShuttleArrivalTimeAdapter(context, now, shuttleDYTime.subList(0, if(binding.expandButton.isSelected) min(shuttleDYTime.size, 5) else min(shuttleDYTime.size, 2)))
            binding.shuttleDYList.adapter = shuttleDYListAdapter
            binding.shuttleDYList.layoutManager = LinearLayoutManager(context)

            val shuttleCTime = timetableByStop.filter { it.shuttleType == "C" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![2].toLong()) > now }.map {
                LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![2].toLong())
            }.toList()
            val shuttleCListAdapter = ShuttleArrivalTimeAdapter(context, now, shuttleCTime.subList(0, if(binding.expandButton.isSelected) min(shuttleCTime.size, 5) else min(shuttleCTime.size, 2)))
            binding.shuttleCList.adapter = shuttleCListAdapter
            binding.shuttleCList.layoutManager = LinearLayoutManager(context)

            binding.shuttleStopLocation.setOnClickListener {
                onClickLocationButton(stopLocationList[position], shuttleStopNameList[position])
            }
            binding.expandButton.setOnClickListener {
                binding.expandButton.isSelected = !binding.expandButton.isSelected
                notifyDataSetChanged()
            }

            if (position == closestStopIndex) {
                binding.homeShuttleArrivalCard.strokeWidth = 3
            } else {
                binding.homeShuttleArrivalCard.strokeWidth = 0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShuttleArrivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_shuttle_arrival, parent, false)
        return ShuttleArrivalViewHolder(CardShuttleArrivalBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleArrivalViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return 5
    }

    fun setShuttleTimetable(shuttleTimetable: List<ShuttleTimetableQuery.Timetable>) {
        this.shuttleTimetable = shuttleTimetable
        notifyDataSetChanged()
    }

    fun setClosestShuttleStop(index: Int) {
        closestStopIndex = index
        notifyDataSetChanged()
    }
}