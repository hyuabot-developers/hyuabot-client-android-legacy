package app.kobuggi.hyuabot.ui.shuttle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemShuttleArrivalTimeBinding
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class ShuttleArrivalTimeAdapter(private val context: Context, private val currentTime: LocalTime, private var arrivalTimeList: List<LocalTime>, val onClickCard: () -> Unit) : RecyclerView.Adapter<ShuttleArrivalTimeAdapter.ShuttleArrivalTimeViewHolder>() {
    inner class ShuttleArrivalTimeViewHolder(private val binding: ItemShuttleArrivalTimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(arrivalTime: LocalTime) {
            val remainedTime = currentTime.until(arrivalTime, ChronoUnit.MINUTES)
            binding.shuttleArrivalTime.text = context.getString(R.string.shuttle_time, arrivalTime.hour.toString().padStart(2, '0'), arrivalTime.minute.toString().padStart(2, '0'), remainedTime)
            binding.shuttleArrivalTime.setOnClickListener {
                onClickCard()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShuttleArrivalTimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shuttle_arrival_time, parent, false)
        return ShuttleArrivalTimeViewHolder(ItemShuttleArrivalTimeBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleArrivalTimeViewHolder, position: Int) {
        holder.bind(arrivalTimeList[position])
    }

    override fun getItemCount(): Int = arrivalTimeList.size

    fun setArrivalTimeList(arrivalTimeList: List<LocalTime>) {
        this.arrivalTimeList = arrivalTimeList
        notifyDataSetChanged()
    }
}