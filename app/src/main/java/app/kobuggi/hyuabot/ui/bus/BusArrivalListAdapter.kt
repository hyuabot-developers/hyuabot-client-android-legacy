package app.kobuggi.hyuabot.ui.bus

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.CardBusArrivalBinding
import com.google.android.gms.maps.model.LatLng
import java.time.LocalTime

class BusArrivalListAdapter(private val context: Context, private var busList: List<BusQuery.Bus>, private val onClickTimetableButton: (routeName: String, routeColor: String) -> Unit) : RecyclerView.Adapter<BusArrivalListAdapter.BusArrivalViewHolder>() {
    private val routeColor = hashMapOf(
        "10-1" to "#33cc99",
        "707-1" to "#E60012",
        "3102" to "#E60012",
    )
    inner class BusArrivalViewHolder(private val binding: CardBusArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.busRouteName.setBackgroundColor(Color.parseColor(routeColor[busList[position].routeName] ?: "#000000"))
            binding.busRouteName.text = context.getString(R.string.bus_route_name, busList[position].routeName, busList[position].stopName)
            binding.busTerminalStop.text = busList[position].terminalStop

            val now = LocalTime.now()
            val timetable = busList[position].timetable.filter { LocalTime.parse(it.departureTime.toString()).isAfter(now) }
            if (busList[position].realtime.size + timetable.size > 0) {
                val adapter = BusArrivalTimeAdapter(context, busList[position].realtime, timetable){
                    onClickTimetableButton(busList[position].routeName, routeColor[busList[position].routeName]!!)
                }
                binding.busArrivalList.adapter = adapter
                binding.busArrivalList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                binding.busArrivalList.visibility = View.VISIBLE
                binding.busNoData.visibility = View.GONE
            } else {
                binding.busArrivalList.visibility = View.GONE
                binding.busNoData.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusArrivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_bus_arrival, parent, false)
        return BusArrivalViewHolder(CardBusArrivalBinding.bind(view))
    }

    override fun onBindViewHolder(holder: BusArrivalViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    fun setBusTimetable(busList: List<BusQuery.Bus>) {
        this.busList = busList
        notifyItemRangeChanged(0, busList.size)
    }
}