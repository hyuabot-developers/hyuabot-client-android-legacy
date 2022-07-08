package app.kobuggi.hyuabot.ui.main.shuttle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemShuttleArrivalBinding
import java.time.Duration
import java.time.LocalTime

class ShuttleArrivalTimeAdapter(private val context: Context, private val now: LocalTime, private val timetable: List<LocalTime>) : RecyclerView.Adapter<ShuttleArrivalTimeAdapter.ShuttleArrivalTimeViewHolder>() {
    inner class ShuttleArrivalTimeViewHolder(private val binding: ItemShuttleArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            val arrivalTime = timetable[position]
            binding.shuttleArrivalTime.text = context.getString(R.string.shuttle_time, arrivalTime.hour.toString().padStart(2, '0'), arrivalTime.minute.toString().padStart(2, '0'), Duration.between(now, arrivalTime).toMinutes())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShuttleArrivalTimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shuttle_arrival, parent, false)
        return ShuttleArrivalTimeViewHolder(ItemShuttleArrivalBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleArrivalTimeViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return timetable.size
    }
}