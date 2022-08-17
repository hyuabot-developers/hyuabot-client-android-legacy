package app.kobuggi.hyuabot.ui.shuttle

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.CardShuttleInformationBinding

class ShuttleInformationAdapter(private val context: Context, private var informationList: List<ShuttleInformationItem>) :
    RecyclerView.Adapter<ShuttleInformationAdapter.ShuttleInformationViewHolder>() {
    inner class ShuttleInformationViewHolder(private val binding: CardShuttleInformationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShuttleInformationItem) {
            binding.shuttleInformationImage.setImageResource(item.imageResourceID)
            binding.shuttleInformationTitle.text = item.title
            binding.shuttleInformationContent.text = item.content
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShuttleInformationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_shuttle_information, parent, false)
        return ShuttleInformationViewHolder(CardShuttleInformationBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleInformationViewHolder, position: Int) {
        holder.bind(informationList[position])
    }

    override fun getItemCount(): Int = informationList.size

    fun setClosestShuttleStop(closestShuttleStop: String) {
        informationList[0].content = closestShuttleStop
        notifyItemChanged(0)
    }

    fun setShuttleFirstLastBus(list: List<ShuttleTimetableQuery.Timetable>?) {
        if (list != null && list.isNotEmpty()) {
            informationList[1].content = context.getString(
                R.string.shuttle_first_last_bus_time,
                list.first().shuttleTime.split(":")[0],
                list.first().shuttleTime.split(":")[1],
                list.last().shuttleTime.split(":")[0],
                list.last().shuttleTime.split(":")[1],
            )
        } else {
            informationList[1].content = context.getString(R.string.shuttle_first_last_bus_time, "0", "0", "0", "0")
        }
        notifyItemChanged(1)
    }

    private fun getPaddingString(number: Int) = number.toString().padStart(2, '0')
}