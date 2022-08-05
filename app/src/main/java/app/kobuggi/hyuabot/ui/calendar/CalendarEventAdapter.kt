package app.kobuggi.hyuabot.ui.calendar

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.CalendarDatabaseItem
import app.kobuggi.hyuabot.databinding.ItemEventBinding
import java.time.LocalDateTime

class CalendarEventAdapter(private val context: Context, private var events: List<CalendarDatabaseItem>, private val onClickItem: (CalendarDatabaseItem) -> Unit, private val onLongClickItem : (Int, Int) -> Unit) :
    RecyclerView.Adapter<CalendarEventAdapter.CalendarEventViewHolder>() {
    private var selectedPosition = -1
    inner class CalendarEventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item: CalendarDatabaseItem){
            binding.eventTitle.text = item.name

            val startDate = LocalDateTime.parse(item.startDate!!.split("+")[0])
            val endDate = LocalDateTime.parse(item.endDate!!.split("+")[0])

            binding.startDate.text = if(startDate.minute > 0) context.getString(R.string.start_date, startDate.monthValue, startDate.dayOfMonth, startDate.hour, startDate.minute) else context.getString(R.string.start_date_without_minute, startDate.monthValue, startDate.dayOfMonth, startDate.hour)
            binding.endDate.text = if(endDate.minute > 0) context.getString(R.string.end_date, endDate.monthValue, endDate.dayOfMonth, endDate.hour, endDate.minute) else context.getString(R.string.end_date_without_minute, endDate.monthValue, endDate.dayOfMonth, endDate.hour)
            binding.eventItem.setOnLongClickListener {
                setSelectedPosition(bindingAdapterPosition)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarEventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarEventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarEventViewHolder, position: Int) {
        holder.bind(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setEvents(events: List<CalendarDatabaseItem>) {
        this.events = events
        notifyDataSetChanged()
    }

    fun setSelectedPosition(adapterPosition: Int) {
        onLongClickItem(selectedPosition, adapterPosition)
        selectedPosition = adapterPosition
    }
}