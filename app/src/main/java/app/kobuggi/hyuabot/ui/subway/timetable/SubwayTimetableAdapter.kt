package app.kobuggi.hyuabot.ui.subway.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemSubwayTimetableBinding
import java.time.LocalDateTime


class SubwayTimetableAdapter(
    private val context: Context,
    private val timetable: List<SubwayTimetableItem>,
): RecyclerView.Adapter<SubwayTimetableAdapter.SubwayTimetableViewHolder>() {
    inner class SubwayTimetableViewHolder(private val binding: ItemSubwayTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.subwayTimetableTerminalStation.text = timetable[position].terminalStation.replace("신인천", "인천")
            binding.subwayTimetableDepartureTime.text = context.getString(
                R.string.shuttle_timetable, timetable[position].departureTime.hour.toString().padStart(2, '0'), timetable[position].departureTime.minute.toString().padStart(2, '0')
            )
            if(timetable[position].departureTime < LocalDateTime.now()) {
                binding.subwayTimetableTerminalStation.setTextColor(context.getColor(android.R.color.darker_gray))
                binding.subwayTimetableDepartureTime.setTextColor(context.getColor(android.R.color.darker_gray))
            } else {
                binding.subwayTimetableTerminalStation.setTextColor(context.getColor(R.color.primaryTextColor))
                binding.subwayTimetableDepartureTime.setTextColor(context.getColor(R.color.primaryTextColor))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubwayTimetableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subway_timetable, parent, false)
        return SubwayTimetableViewHolder(ItemSubwayTimetableBinding.bind(view))
    }

    override fun onBindViewHolder(holder: SubwayTimetableViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = timetable.size
}