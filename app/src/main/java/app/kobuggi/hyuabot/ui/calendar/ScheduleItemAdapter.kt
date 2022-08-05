package app.kobuggi.hyuabot.ui.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.CalendarDatabaseItem
import app.kobuggi.hyuabot.databinding.ItemCalendarScheduleItemBinding
import java.time.LocalDateTime

class ScheduleItemAdapter(private val context: Context, private val scheduleList: List<CalendarDatabaseItem>) : RecyclerView.Adapter<ScheduleItemAdapter.ScheduleItemViewHolder>() {
    inner class ScheduleItemViewHolder(val binding: ItemCalendarScheduleItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(value: CalendarDatabaseItem) {
            binding.scheduleItemName.text = value.name

            val endDate = LocalDateTime.parse(value.endDate!!.split("+")[0])
            if (endDate.minute > 0){
                binding.scheduleItemEndTime.text = context.getString(R.string.schedule_item_end_time, endDate.year, endDate.monthValue, endDate.dayOfMonth, endDate.hour, endDate.minute)
            } else {
                binding.scheduleItemEndTime.text = context.getString(R.string.schedule_item_end_time_without_minute, endDate.year, endDate.monthValue, endDate.dayOfMonth, endDate.hour)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleItemViewHolder {
        val binding = ItemCalendarScheduleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleItemViewHolder, position: Int) {
        holder.bind(scheduleList[position])
    }

    override fun getItemCount(): Int = scheduleList.size
}