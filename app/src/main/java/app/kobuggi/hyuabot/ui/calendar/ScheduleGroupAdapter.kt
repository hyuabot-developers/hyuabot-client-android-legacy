package app.kobuggi.hyuabot.ui.calendar

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.CalendarDatabaseItem
import app.kobuggi.hyuabot.databinding.ItemCalendarScheduleGroupBinding

class ScheduleGroupAdapter(private val context: Context, private val groupedSchedule: Map<Int, List<CalendarDatabaseItem>>) : RecyclerView.Adapter<ScheduleGroupAdapter.ScheduleGroupViewHolder>() {
    inner class ScheduleGroupViewHolder(val binding: ItemCalendarScheduleGroupBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(key: Int, value: List<CalendarDatabaseItem>) {
            when(key) {
                1 -> {
                    binding.scheduleGroupName.text = context.getString(R.string.target_grade_1)
                    binding.scheduleGroupName.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.hanyang_primary, null))
                }
                2 -> {
                    binding.scheduleGroupName.text = context.getString(R.string.target_grade_2)
                    binding.scheduleGroupName.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.hanyang_secondary, null))
                }
                3 -> {
                    binding.scheduleGroupName.text = context.getString(R.string.target_grade_3)
                    binding.scheduleGroupName.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.hanyang_tertiary, null))
                }
                4 -> {
                    binding.scheduleGroupName.text = context.getString(R.string.target_grade_4)
                    binding.scheduleGroupName.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.hanyang_quaternary, null))
                }
                else -> {
                    binding.scheduleGroupName.text = context.getString(R.string.target_grade_all)
                    binding.scheduleGroupName.setBackgroundColor(ResourcesCompat.getColor(context.resources, R.color.cardBackground, null))
                }
            }
            val itemAdapter = ScheduleItemAdapter(context, value)
            binding.scheduleItemList.adapter = itemAdapter
            binding.scheduleItemList.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleGroupViewHolder {
        val binding = ItemCalendarScheduleGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScheduleGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleGroupViewHolder, position: Int) {
        holder.bind(groupedSchedule.keys.elementAt(position), groupedSchedule.values.elementAt(position))
    }

    override fun getItemCount(): Int = groupedSchedule.keys.size
}