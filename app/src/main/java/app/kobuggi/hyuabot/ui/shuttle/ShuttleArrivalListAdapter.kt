package app.kobuggi.hyuabot.ui.shuttle

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
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShuttleArrivalListAdapter(private val context: Context,
                                private val stopList: List<ShuttleStopInfo>, timetable: List<ShuttleTimetableQuery.Timetable>, val onClickLocationButton: (location: LatLng, titleID: Int) -> Unit, val onClickCard: (stopID: Int, shuttleType: String) -> Unit) : RecyclerView.Adapter<ShuttleArrivalListAdapter.ShuttleArrivalViewHolder>() {
    private var shuttleTimetable: List<ShuttleTimetableQuery.Timetable> = timetable
    private val timeDelta = hashMapOf(
        R.string.dormitory to arrayListOf(-5, -5, -5),
        R.string.shuttlecock_o to arrayListOf(0, 0, 0),
        R.string.station to arrayListOf(10, 0, 10),
        R.string.terminal to arrayListOf(0, 10, 15),
        R.string.shuttlecock_i to arrayListOf(20, 20, 25)
    )
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    inner class ShuttleArrivalViewHolder(private val binding: CardShuttleArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.shuttleStopName.text = context.getString(stopList[position].nameID)

            if (stopList[position].nameID == R.string.station) {
                binding.shuttleDY.visibility = View.GONE
                binding.shuttleStopDivider.visibility = View.GONE
                binding.shuttleTypeDH.text = context.getString(R.string.shuttle_type_D)
            } else if (stopList[position].nameID == R.string.terminal) {
                binding.shuttleDH.visibility = View.GONE
                binding.shuttleStopDivider.visibility = View.GONE
                binding.shuttleTypeDY.text = context.getString(R.string.shuttle_type_D)
            }

            val now = LocalTime.now()
            val timetableByStopDH = if(position == 0){
                shuttleTimetable.filter { it.shuttleType == "DH" && it.startStop == "Dormitory" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) }
            } else {
                shuttleTimetable.filter { it.shuttleType == "DH" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) }
            }
            val timetableByStopDY = if(position == 0){
                shuttleTimetable.filter { it.shuttleType == "DY" && it.startStop == "Dormitory" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) }
            } else {
                shuttleTimetable.filter { it.shuttleType == "DY" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) }
            }
            val timetableByStopC = if(position == 0){
                shuttleTimetable.filter { it.shuttleType == "C" && it.startStop == "Dormitory" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) }
            } else {
                shuttleTimetable.filter { it.shuttleType == "C" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) }
            }
            Log.d("timetable", timetableByStopC.toString())
            val shuttleDHAdapter = ShuttleArrivalTimeAdapter(context, now, timetableByStopDH.subList(0, if(timetableByStopDH.isNotEmpty()) minOf(if (binding.expandButton.isSelected) 5 else 2, timetableByStopDH.size - 1) else 0)){
                onClickCard(stopList[position].nameID, "DH")
            }
            val shuttleDYAdapter = ShuttleArrivalTimeAdapter(context, now, timetableByStopDY.subList(0, if(timetableByStopDY.isNotEmpty()) minOf(if (binding.expandButton.isSelected) 5 else 2, timetableByStopDY.size - 1) else 0)){
                onClickCard(stopList[position].nameID, "DY")
            }
            val shuttleCAdapter = ShuttleArrivalTimeAdapter(context, now, timetableByStopC.subList(0, if(timetableByStopC.isNotEmpty()) minOf(if (binding.expandButton.isSelected) 5 else 2, timetableByStopC.size - 1) else 0)){
                onClickCard(stopList[position].nameID, "C")
            }
            binding.shuttleDHTime.adapter = shuttleDHAdapter
            binding.shuttleDHTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.shuttleDYTime.adapter = shuttleDYAdapter
            binding.shuttleDYTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.shuttleCTime.adapter = shuttleCAdapter
            binding.shuttleCTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            if (timetableByStopDH.isEmpty()){
                binding.shuttleDHNoData.visibility = View.VISIBLE
                binding.shuttleDHTime.visibility = View.GONE
            } else {
                binding.shuttleDHNoData.visibility = View.GONE
                binding.shuttleDHTime.visibility = View.VISIBLE
            }
            if (timetableByStopDY.isEmpty()){
                binding.shuttleDYNoData.visibility = View.VISIBLE
                binding.shuttleDYTime.visibility = View.GONE
            } else {
                binding.shuttleDYNoData.visibility = View.GONE
                binding.shuttleDYTime.visibility = View.VISIBLE
            }
            if (timetableByStopC.isEmpty()){
                binding.shuttleCNoData.visibility = View.VISIBLE
                binding.shuttleCTime.visibility = View.GONE
            } else {
                binding.shuttleCNoData.visibility = View.GONE
                binding.shuttleCTime.visibility = View.VISIBLE
            }

            binding.shuttleStopLocation.setOnClickListener{
                onClickLocationButton(stopList[position].location, stopList[position].nameID)
            }
            binding.expandButton.setOnClickListener {
                binding.expandButton.isSelected = !binding.expandButton.isSelected
                shuttleDHAdapter.setArrivalTimeList(timetableByStopDH.subList(0, if(timetableByStopDH.isNotEmpty()) minOf(if (binding.expandButton.isSelected) 5 else 2, timetableByStopDH.size - 1) else 0))
                shuttleDYAdapter.setArrivalTimeList(timetableByStopDY.subList(0, if(timetableByStopDY.isNotEmpty()) minOf(if (binding.expandButton.isSelected) 5 else 2, timetableByStopDY.size - 1) else 0))
                shuttleCAdapter.setArrivalTimeList(timetableByStopC.subList(0, if(timetableByStopC.isNotEmpty()) minOf(if (binding.expandButton.isSelected) 5 else 2, timetableByStopC.size - 1) else 0))
            }
            binding.shuttleDH.setOnClickListener {
                Log.d("shuttleDH", "clicked")
                onClickCard(stopList[position].nameID, "DH")
            }
            binding.shuttleDY.setOnClickListener {
                onClickCard(stopList[position].nameID, "DY")
            }
            binding.shuttleC.setOnClickListener {
                onClickCard(stopList[position].nameID, "C")
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
        return stopList.size
    }

    fun setShuttleTimetable(shuttleTimetable: List<ShuttleTimetableQuery.Timetable>) {
        this.shuttleTimetable = shuttleTimetable
        notifyItemRangeChanged(0, itemCount)
    }
}