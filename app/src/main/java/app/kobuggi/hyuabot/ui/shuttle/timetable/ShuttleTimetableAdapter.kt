package app.kobuggi.hyuabot.ui.shuttle.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemShuttleTimetableBinding
import java.time.LocalTime


class ShuttleTimetableAdapter(
    private val context: Context,
    private val timetable: List<LocalTime>,
    private val startStopList: List<String>,
    private val openShuttleRouteDialog: (arrivalTime: LocalTime, startStop: String) -> Unit
): RecyclerView.Adapter<ShuttleTimetableAdapter.ShuttleTimetableViewHolder>() {
    inner class ShuttleTimetableViewHolder(private val binding: ItemShuttleTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.shuttleArrivalTime.text = context.getString(
                R.string.shuttle_timetable, timetable[position].hour.toString().padStart(2, '0'), timetable[position].minute.toString().padStart(2, '0')
            )
            if(timetable[position] < LocalTime.now()) {
                binding.shuttleArrivalTime.setTextColor(context.getColor(android.R.color.darker_gray))
            } else {
                binding.shuttleArrivalTime.setTextColor(context.getColor(R.color.primaryTextColor))
            }
            binding.shuttleArrivalTime.setOnClickListener {
                openShuttleRouteDialog(timetable[position], startStopList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShuttleTimetableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shuttle_timetable, parent, false)
        return ShuttleTimetableViewHolder(ItemShuttleTimetableBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleTimetableViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = timetable.size
}