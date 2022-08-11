package app.kobuggi.hyuabot.ui.shuttle

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.CardShuttleArrivalBinding
import com.google.android.gms.maps.model.LatLng
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShuttleArrivalListAdapter(private val context: Context, stopList: List<ShuttleStopInfo> , timetable: List<ShuttleTimetableQuery.Timetable>, val onClickLocationButton: (location: LatLng, titleID: Int) -> Unit, val onClickCard: (stopID: Int, shuttleType: String) -> Unit) : RecyclerView.Adapter<ShuttleArrivalListAdapter.ShuttleArrivalViewHolder>() {
    private var shuttleTimetable: List<ShuttleTimetableQuery.Timetable> = timetable
    private var stopList: List<ShuttleStopInfo> = stopList
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
            val timetableByStop = if(position == 0){
                shuttleTimetable.filter { it.startStop == "Dormitory" }
            } else {
                shuttleTimetable
            }

            binding.shuttleTimeDHFirst.text = "운행 종료"
            binding.shuttleTimeDHSecond.visibility = View.GONE
            binding.shuttleTimeDHThird.visibility = View.GONE
            binding.shuttleTimeDHFourth.visibility = View.GONE
            binding.shuttleTimeDHFifth.visibility = View.GONE
            binding.shuttleTimeDHSecond.text = ""
            binding.shuttleTimeDHThird.text = ""
            binding.shuttleTimeDHFourth.text = ""
            binding.shuttleTimeDHFifth.text = ""

            binding.shuttleTimeDYFirst.text = "운행 종료"
            binding.shuttleTimeDYSecond.visibility = View.GONE
            binding.shuttleTimeDYThird.visibility = View.GONE
            binding.shuttleTimeDYFourth.visibility = View.GONE
            binding.shuttleTimeDYFifth.visibility = View.GONE
            binding.shuttleTimeDYSecond.text = ""
            binding.shuttleTimeDYThird.text = ""
            binding.shuttleTimeDYFourth.text = ""
            binding.shuttleTimeDYFifth.text = ""

            binding.shuttleTimeCFirst.text = "운행 종료"
            binding.shuttleTimeCSecond.visibility = View.GONE
            binding.shuttleTimeCThird.visibility = View.GONE
            binding.shuttleTimeCFourth.visibility = View.GONE
            binding.shuttleTimeCFifth.visibility = View.GONE
            binding.shuttleTimeCSecond.text = ""
            binding.shuttleTimeCThird.text = ""
            binding.shuttleTimeCFourth.text = ""
            binding.shuttleTimeCFifth.text = ""

            Log.d("ShuttleArrivalListAdapter", "timetableByStop: ${timetableByStop.size}")
            timetableByStop.filter { it.shuttleType == "DH" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) > now }.forEachIndexed { index, timetable ->
                val shuttleTime = LocalTime.parse(timetable.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong())
                val remainedTime = Duration.between(now, shuttleTime).toMinutes()
                when (index) {
                    0 -> {
                        binding.shuttleTimeDHFirst.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    1 -> {
                        binding.shuttleTimeDHSecond.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                        binding.shuttleTimeDHSecond.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.shuttleTimeDHThird.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    3 -> {
                        binding.shuttleTimeDHFourth.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    4 -> {
                        binding.shuttleTimeDHFifth.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                }
            }
            timetableByStop.filter { it.shuttleType == "DY" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) > now }.forEachIndexed { index, timetable ->
                val shuttleTime = LocalTime.parse(timetable.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong())
                val remainedTime = Duration.between(now, shuttleTime).toMinutes()
                when (index) {
                    0 -> {
                        binding.shuttleTimeDYFirst.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    1 -> {
                        binding.shuttleTimeDYSecond.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                        binding.shuttleTimeDYSecond.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.shuttleTimeDYThird.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    3 -> {
                        binding.shuttleTimeDYFourth.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    4 -> {
                        binding.shuttleTimeDYFifth.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                }
            }
            timetableByStop.filter { it.shuttleType == "C" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) > now }.forEachIndexed { index, timetable ->
                val shuttleTime = LocalTime.parse(timetable.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong())
                val remainedTime = Duration.between(now, shuttleTime).toMinutes()
                when (index) {
                    0 -> {
                        binding.shuttleTimeCFirst.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    1 -> {
                        binding.shuttleTimeCSecond.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                        binding.shuttleTimeCSecond.visibility = View.VISIBLE
                    }
                    2 -> {
                        binding.shuttleTimeCThird.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    3 -> {
                        binding.shuttleTimeCFourth.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                    4 -> {
                        binding.shuttleTimeCFifth.text = context.getString(R.string.shuttle_time, shuttleTime.hour.toString().padStart(2, '0'), shuttleTime.minute.toString().padStart(2, '0'), remainedTime)
                    }
                }
            }
            binding.shuttleDH.setOnClickListener {
                onClickCard(stopList[position].nameID, "DH")
            }
            binding.shuttleDY.setOnClickListener {
                onClickCard(stopList[position].nameID, "DY")
            }
            binding.shuttleC.setOnClickListener {
                onClickCard(stopList[position].nameID, "C")
            }
            binding.shuttleStopLocation.setOnClickListener {
                onClickLocationButton(stopList[position].location, stopList[position].nameID)
            }
            binding.expandButton.setOnClickListener {
                binding.expandButton.isSelected = !binding.expandButton.isSelected

                val visible = if(binding.expandButton.isSelected){
                    View.VISIBLE
                } else {
                    View.GONE
                }
                binding.shuttleTimeDHThird.visibility = if(binding.shuttleTimeDHThird.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeDHFourth.visibility = if(binding.shuttleTimeDHFourth.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeDHFifth.visibility = if(binding.shuttleTimeDHFifth.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeDYThird.visibility = if(binding.shuttleTimeDYThird.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeDYFourth.visibility = if(binding.shuttleTimeDYFourth.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeDYFifth.visibility = if(binding.shuttleTimeDYFifth.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeCThird.visibility = if(binding.shuttleTimeCThird.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeCFourth.visibility = if(binding.shuttleTimeCFourth.text.isNotEmpty()) visible else View.GONE
                binding.shuttleTimeCFifth.visibility = if(binding.shuttleTimeCFifth.text.isNotEmpty()) visible else View.GONE
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

    fun setShuttleStopList(stopList: List<ShuttleStopInfo>) {
        this.stopList = stopList
        notifyItemRangeChanged(0, itemCount)
    }
}