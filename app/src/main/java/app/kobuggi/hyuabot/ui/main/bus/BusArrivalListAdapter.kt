package app.kobuggi.hyuabot.ui.main.bus

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemBusArrivalBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class BusArrivalListAdapter(private val context: Context, private var busList: List<BusQuery.Bus>) : RecyclerView.Adapter<BusArrivalListAdapter.BusArrivalViewHolder>() {
    private val routeColor = hashMapOf(
        "10-1" to "#33cc99",
        "707-1" to "#E60012",
        "3102" to "#E60012",
    )
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    inner class BusArrivalViewHolder(private val binding: ItemBusArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.busRouteName.setBackgroundColor(Color.parseColor(routeColor[busList[position].routeName] ?: "#000000"))
            binding.busRouteName.text = "${busList[position].routeName} (${busList[position].stopName})"
            binding.busTerminalStop.text = busList[position].terminalStop
            val now = LocalTime.now()
            busList[position].realtime.forEachIndexed { index, realtime ->
                when (index){
                    0 ->{
                        if(realtime.remainedSeat >= 0) {
                            binding.busArrivalFirst.text = context.getString(R.string.bus_realtime_item, realtime.remainedTime, realtime.remainedStop, realtime.remainedSeat)
                        } else {
                            binding.busArrivalFirst.text = context.getString(R.string.bus_realtime_item_without_seat, realtime.remainedTime, realtime.remainedStop)
                        }
                    }
                    1 ->{
                        if(realtime.remainedSeat >= 0) {
                            binding.busArrivalSecond.text = context.getString(R.string.bus_realtime_item, realtime.remainedTime, realtime.remainedStop, realtime.remainedSeat)
                        } else {
                            binding.busArrivalSecond.text = context.getString(R.string.bus_realtime_item_without_seat, realtime.remainedTime, realtime.remainedStop)
                        }
                    }
                    2 ->{
                        if(realtime.remainedSeat >= 0) {
                            binding.busArrivalThird.text = context.getString(R.string.bus_realtime_item, realtime.remainedTime, realtime.remainedStop, realtime.remainedSeat)
                        } else {
                            binding.busArrivalThird.text = context.getString(R.string.bus_realtime_item_without_seat, realtime.remainedTime, realtime.remainedStop)
                        }
                    }
                    3 ->{
                        if(realtime.remainedSeat >= 0) {
                            binding.busArrivalFourth.text = context.getString(R.string.bus_realtime_item, realtime.remainedTime, realtime.remainedStop, realtime.remainedSeat)
                        } else {
                            binding.busArrivalFourth.text = context.getString(R.string.bus_realtime_item_without_seat, realtime.remainedTime, realtime.remainedStop)
                        }
                    }
                    4 ->{
                        if(realtime.remainedSeat >= 0) {
                            binding.busArrivalFifth.text = context.getString(R.string.bus_realtime_item, realtime.remainedTime, realtime.remainedStop, realtime.remainedSeat)
                        } else {
                            binding.busArrivalFifth.text = context.getString(R.string.bus_realtime_item_without_seat, realtime.remainedTime, realtime.remainedStop)
                        }
                    }
                }
            }
            if(busList[position].realtime.size < 5){
                busList[position].timetable.filter { LocalTime.parse(it.departureTime.toString()).isAfter(now) }.forEachIndexed { index, timetable ->
                    when (busList[position].realtime.size + index){
                        0 ->{
                            binding.busArrivalFirst.text = context.getString(R.string.bus_timetable_item, LocalTime.parse(timetable.departureTime.toString()).hour.toString().padStart(2, '0'), LocalTime.parse(timetable.departureTime.toString()).minute.toString().padStart(2, '0'))
                        }
                        1 ->{
                            binding.busArrivalSecond.text = context.getString(R.string.bus_timetable_item, LocalTime.parse(timetable.departureTime.toString()).hour.toString().padStart(2, '0'), LocalTime.parse(timetable.departureTime.toString()).minute.toString().padStart(2, '0'))
                        }
                        2 ->{
                            binding.busArrivalThird.text = context.getString(R.string.bus_timetable_item, LocalTime.parse(timetable.departureTime.toString()).hour.toString().padStart(2, '0'), LocalTime.parse(timetable.departureTime.toString()).minute.toString().padStart(2, '0'))
                        }
                        3 ->{
                            binding.busArrivalFourth.text = context.getString(R.string.bus_timetable_item, LocalTime.parse(timetable.departureTime.toString()).hour.toString().padStart(2, '0'), LocalTime.parse(timetable.departureTime.toString()).minute.toString().padStart(2, '0'))
                        }
                        4 ->{
                            binding.busArrivalFifth.text = context.getString(R.string.bus_timetable_item, LocalTime.parse(timetable.departureTime.toString()).hour.toString().padStart(2, '0'), LocalTime.parse(timetable.departureTime.toString()).minute.toString().padStart(2, '0'))
                        }
                    }
                }
            }
            Log.d("BusArrivalListAdapter", "${busList[position].realtime.size}  ${busList[position].timetable.size}")
            when(busList[position].realtime.size + busList[position].timetable.filter { LocalTime.parse(it.departureTime.toString()).isAfter(now) }.size){
                0 -> {
                    binding.busArrivalFirst.text = context.getString(R.string.out_of_service)
                    binding.busArrivalSecond.visibility = View.GONE
                    binding.busArrivalThird.visibility = View.GONE
                    binding.busArrivalFourth.visibility = View.GONE
                    binding.busArrivalFifth.visibility = View.GONE
                }
                1 -> {
                    binding.busArrivalSecond.visibility = View.GONE
                    binding.busArrivalThird.visibility = View.GONE
                    binding.busArrivalFourth.visibility = View.GONE
                    binding.busArrivalFifth.visibility = View.GONE
                }
                2 -> {
                    binding.busArrivalThird.visibility = View.GONE
                    binding.busArrivalFourth.visibility = View.GONE
                    binding.busArrivalFifth.visibility = View.GONE
                }
                3 -> {
                    binding.busArrivalFourth.visibility = View.GONE
                    binding.busArrivalFifth.visibility = View.GONE
                }
                4 -> {
                    binding.busArrivalFifth.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArrivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_arrival, parent, false)
        return BusArrivalViewHolder(ItemBusArrivalBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BusArrivalViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    fun setBusTimetable(busList: List<BusQuery.Bus>) {
        this.busList = busList
        notifyDataSetChanged()
    }
}