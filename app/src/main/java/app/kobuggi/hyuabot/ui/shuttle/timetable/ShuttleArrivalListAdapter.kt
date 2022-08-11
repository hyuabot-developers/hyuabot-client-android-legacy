package app.kobuggi.hyuabot.ui.shuttle.timetable

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemShuttleTimetableDialogBinding


class ShuttleArrivalListAdapter(
    private val context: Context,
    private val timetable: List<ShuttleTimetableDialog.ShuttleDepartureItem>,
): RecyclerView.Adapter<ShuttleArrivalListAdapter.ShuttleTimetableViewHolder>() {
    inner class ShuttleTimetableViewHolder(private val binding: ItemShuttleTimetableDialogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.shuttleArrivalTime.text = context.getString(
                R.string.shuttle_timetable, timetable[position].departureTime.hour.toString().padStart(2, '0'), timetable[position].departureTime.minute.toString().padStart(2, '0')
            )
            binding.shuttleStopName.text = context.getString(timetable[position].stopID)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShuttleTimetableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shuttle_timetable_dialog, parent, false)
        return ShuttleTimetableViewHolder(ItemShuttleTimetableDialogBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleTimetableViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = timetable.size
}