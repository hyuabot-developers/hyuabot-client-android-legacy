package app.kobuggi.hyuabot.ui.main.shuttle.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.ItemShuttleTimetableBinding
import app.kobuggi.hyuabot.ui.main.shuttle.ShuttleArrivalListAdapter
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class ShuttleTimetableAdapter(private val context: Context, private val timetable : List<ShuttleTimetableQuery.Timetable>, private val timeDelta: Int): RecyclerView.Adapter<ShuttleTimetableAdapter.ShuttleTimetableViewHolder>() {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    var index = 0

    inner class ShuttleTimetableViewHolder(private val binding: ItemShuttleTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val arrivalTime = LocalTime.parse(timetable[position].shuttleTime, formatter).plusMinutes(timeDelta.toLong())
            binding.shuttleArrivalTime.text = context.getString(
                R.string.shuttle_timetable, arrivalTime.hour.toString().padStart(2, '0'), arrivalTime.minute.toString().padStart(2, '0')
            )
            if(arrivalTime < LocalTime.now()) {
                binding.shuttleArrivalTime.setTextColor(context.getColor(android.R.color.darker_gray))
            } else {
                binding.shuttleArrivalTime.setTextColor(context.getColor(R.color.primaryTextColor))
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