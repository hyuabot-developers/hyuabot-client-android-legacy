package app.kobuggi.hyuabot.ui.bus

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemBusArrivalTimeBinding
import java.time.LocalTime
import kotlin.math.min

class BusArrivalTimeAdapter(private val context: Context, private var realtimeList: List<BusQuery.Realtime>, private var timetableList: List<BusQuery.Timetable>, val onClickCard: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val arrivalTimeList = mutableListOf<Any>()
    private var itemCount = 2
    init {
        arrivalTimeList.addAll(realtimeList)
        arrivalTimeList.addAll(timetableList)
    }

    inner class BusRealtimeViewHolder(private val binding: ItemBusArrivalTimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(arrivalTime: BusQuery.Realtime) {
            if(arrivalTime.remainedSeat >= 0) {
                binding.busArrivalTime.text = context.getString(R.string.bus_realtime_item, arrivalTime.remainedTime, arrivalTime.remainedStop, arrivalTime.remainedSeat)
            } else {
                binding.busArrivalTime.text = context.getString(R.string.bus_realtime_item_without_seat, arrivalTime.remainedTime, arrivalTime.remainedStop)
            }
            binding.busArrivalTime.setOnClickListener {
                onClickCard()
            }
        }
    }

    inner class BusTimetableViewHolder(private val binding: ItemBusArrivalTimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(arrivalTime: BusQuery.Timetable) {
            binding.busArrivalTime.text = context.getString(R.string.bus_timetable_item, LocalTime.parse(arrivalTime.departureTime.toString()).hour.toString().padStart(2, '0'), LocalTime.parse(arrivalTime.departureTime.toString()).minute.toString().padStart(2, '0'))
            binding.busArrivalTime.setOnClickListener {
                onClickCard()
            }
        }
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_arrival_time, parent, false)
        if (viewType == 0){
            return BusRealtimeViewHolder(ItemBusArrivalTimeBinding.bind(view))
        }
        return BusTimetableViewHolder(ItemBusArrivalTimeBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (arrivalTimeList[position] is BusQuery.Realtime){
            (holder as BusRealtimeViewHolder).bind(arrivalTimeList[position] as BusQuery.Realtime)
        } else {
            (holder as BusTimetableViewHolder).bind(arrivalTimeList[position] as BusQuery.Timetable)
        }
    }

    override fun getItemCount(): Int = min(realtimeList.size + timetableList.size, itemCount)

    override fun getItemViewType(position: Int): Int {
        return if (arrivalTimeList[position] is BusQuery.Realtime){
            0
        } else {
            1
        }
    }

    fun setItemCount(itemCount: Int) {
        val oldItemCount = this.itemCount
        this.itemCount = itemCount
        if (itemCount > oldItemCount) {
            notifyItemRangeInserted(oldItemCount, itemCount - oldItemCount)
        } else {
            notifyItemRangeRemoved(itemCount, oldItemCount - itemCount)
        }
    }
}