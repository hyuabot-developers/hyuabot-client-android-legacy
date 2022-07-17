package app.kobuggi.hyuabot.ui.main.shuttle

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.ItemShuttleArrivalBinding
import com.google.android.gms.maps.model.LatLng
import java.time.Duration
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShuttleArrivalListAdapter(private val context: Context, timetable: List<ShuttleTimetableQuery.Timetable>, val onClickLocationButton: (location: LatLng, titleID: Int) -> Unit, val onClickCard: (stopID: Int, shuttleType: String) -> Unit) : RecyclerView.Adapter<ShuttleArrivalListAdapter.ShuttleArrivalViewHolder>() {
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
    inner class ShuttleArrivalViewHolder(private val binding: ItemShuttleArrivalBinding) : RecyclerView.ViewHolder(binding.root){
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
            timetableByStop.filter { it.shuttleType == "DH" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![0].toLong()) > now }.forEachIndexed { index, timetable ->
                val shuttleTime = LocalTime.parse(timetable.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![0].toLong())
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
            timetableByStop.filter { it.shuttleType == "DY" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![1].toLong()) > now }.forEachIndexed { index, timetable ->
                val shuttleTime = LocalTime.parse(timetable.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![1].toLong())
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
            timetableByStop.filter { it.shuttleType == "C" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![2].toLong()) > now }.forEachIndexed { index, timetable ->
                val shuttleTime = LocalTime.parse(timetable.shuttleTime, formatter).plusMinutes(timeDelta[shuttleStopNameList[position]]!![2].toLong())
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
                onClickCard(shuttleStopNameList[position], "DH")
            }
            binding.shuttleDY.setOnClickListener {
                onClickCard(shuttleStopNameList[position], "DY")
            }
            binding.shuttleC.setOnClickListener {
                onClickCard(shuttleStopNameList[position], "C")
            }
            binding.shuttleStopLocation.setOnClickListener {
                onClickLocationButton(stopLocationList[position], shuttleStopNameList[position])
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

            if (position == closestStopIndex) {
                binding.homeShuttleArrivalCard.strokeWidth = 3
            } else {
                binding.homeShuttleArrivalCard.strokeWidth = 0
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShuttleArrivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shuttle_arrival, parent, false)
        return ShuttleArrivalViewHolder(ItemShuttleArrivalBinding.bind(view))
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