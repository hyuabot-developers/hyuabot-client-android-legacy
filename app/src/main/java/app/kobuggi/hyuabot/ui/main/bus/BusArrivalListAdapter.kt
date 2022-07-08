package app.kobuggi.hyuabot.ui.main.bus

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemBusArrivalBinding
import java.time.format.DateTimeFormatter

class BusArrivalListAdapter(private val context: Context, private var busList: List<BusQuery.Bus>) : RecyclerView.Adapter<BusArrivalListAdapter.BusArrivalViewHolder>() {
    private val routeColor = hashMapOf(
        "10-1" to "#33cc99",
        "707-1" to "#E60012",
        "3102" to "#E60012",
    )
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    inner class BusArrivalViewHolder(private val binding: ItemBusArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.busRouteName.setTextColor(Color.parseColor(routeColor[busList[position].routeName] ?: "#000000"))
            binding.busRouteName.text = busList[position].routeName
            binding.busStopName.text = busList[position].stopName
            binding.busTerminalStop.text = busList[position].terminalStop
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArrivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bus_arrival, parent, false)
        return BusArrivalViewHolder(ItemBusArrivalBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BusArrivalViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    fun setBusTimetable(busList: List<BusQuery.Bus>) {
        this.busList = busList
        notifyDataSetChanged()
    }
}