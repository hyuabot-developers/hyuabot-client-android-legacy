package app.kobuggi.hyuabot.ui.subway.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.SubwayQuery
import app.kobuggi.hyuabot.databinding.ItemSubwayTimetableBinding
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class SubwayTimetableAdapter(
    private val context: Context,
    private val timetable: List<SubwayQuery.Timetable>,
): RecyclerView.Adapter<SubwayTimetableAdapter.SubwayTimetableViewHolder>() {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    inner class SubwayTimetableViewHolder(private val binding: ItemSubwayTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val departureTime = LocalTime.parse(timetable[position].departureTime, formatter)
            binding.subwayTimetableTerminalStation.text = timetable[position].terminalStation
            binding.subwayTimetableDepartureTime.text = context.getString(
                R.string.shuttle_timetable, departureTime.hour.toString().padStart(2, '0'), departureTime.minute.toString().padStart(2, '0')
            )
            if(departureTime < LocalTime.now()) {
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