package app.kobuggi.hyuabot.ui.bus.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemBusTimetableBinding
import java.time.LocalTime


class BusTimetableAdapter(
    private val context: Context,
    private val timetable: List<LocalTime>,
): RecyclerView.Adapter<BusTimetableAdapter.ShuttleTimetableViewHolder>() {
    inner class ShuttleTimetableViewHolder(private val binding: ItemBusTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.busArrivalTime.text = context.getString(
                R.string.shuttle_timetable, timetable[position].hour.toString().padStart(2, '0'), timetable[position].minute.toString().padStart(2, '0')
            )
            if(timetable[position] < LocalTime.now()) {
                binding.busArrivalTime.setTextColor(context.getColor(android.R.color.darker_gray))
            } else {
                binding.busArrivalTime.setTextColor(context.getColor(R.color.primaryTextColor))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShuttleTimetableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_timetable, parent, false)
        return ShuttleTimetableViewHolder(ItemBusTimetableBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleTimetableViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = timetable.size
}