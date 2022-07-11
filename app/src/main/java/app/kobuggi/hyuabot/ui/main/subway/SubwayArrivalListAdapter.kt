package app.kobuggi.hyuabot.ui.main.subway

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.SubwayQuery
import app.kobuggi.hyuabot.databinding.CardSubwayArrivalBinding
import java.lang.Integer.parseInt
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class SubwayArrivalListAdapter(private val context: Context, private var subwayList: List<SubwayQuery.Subway>) : RecyclerView.Adapter<SubwayArrivalListAdapter.SubwayArrivalViewHolder>() {
    private val routeColor = hashMapOf(
        "4호선" to "#00A5DE",
        "수인분당선" to "#F5A200",
    )
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    inner class SubwayArrivalViewHolder(private val binding: CardSubwayArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.subwayRouteName.setBackgroundColor(Color.parseColor(routeColor[subwayList[position].routeName] ?: "#000000"))
            binding.subwayRouteName.text = subwayList[position].routeName
            val now = LocalDateTime.now()

            val arrivalListUp = arrayListOf<SubwayArrivalItem>()
            subwayList[position].realtime.filter { it.heading == "up" && Duration.between(LocalDateTime.parse(it.updateTime, formatter), now).toMinutes() < parseInt(it.remainedTime) }.forEach { realtime ->
                val remainedTime = parseInt(realtime.remainedTime) - Duration.between(LocalDateTime.parse(realtime.updateTime, formatter), now).toMinutes().toInt()
                arrivalListUp.add(SubwayArrivalItem(remainedTime, realtime.terminalStation, realtime.currentStation))
            }
            var maxRealtimeMinute = if(arrivalListUp.isEmpty()) 0 else arrivalListUp.maxBy { it.remainedTime }.remainedTime
            subwayList[position].timetable.filter { it.heading == "up" && Duration.between(LocalTime.parse(it.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue > maxRealtimeMinute }.forEach { timetable ->
                val remainedTime = Duration.between(LocalTime.parse(timetable.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue
                arrivalListUp.add(SubwayArrivalItem(remainedTime, timetable.terminalStation, null))
            }

            val arrivalListDown = arrayListOf<SubwayArrivalItem>()
            subwayList[position].realtime.filter { it.heading == "down" && Duration.between(LocalDateTime.parse(it.updateTime, formatter), now).toMinutes() < parseInt(it.remainedTime) }.forEach { realtime ->
                val remainedTime = parseInt(realtime.remainedTime) - Duration.between(LocalDateTime.parse(realtime.updateTime, formatter), now).toMinutes().toInt()

                arrivalListDown.add(SubwayArrivalItem(remainedTime, realtime.terminalStation, realtime.currentStation))
            }
            maxRealtimeMinute = if(arrivalListDown.isEmpty()) 0 else arrivalListDown.maxBy { it.remainedTime }.remainedTime
            subwayList[position].timetable.filter { it.heading == "down" && Duration.between(LocalTime.parse(it.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue > maxRealtimeMinute }.forEach { timetable ->
                val remainedTime = Duration.between(LocalTime.parse(timetable.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue
                arrivalListDown.add(SubwayArrivalItem(remainedTime, timetable.terminalStation, null))
            }

            binding.subwayUpArrivalList.adapter = SubwayArrivalItemAdapter(context, arrivalListUp.subList(0, arrivalListUp.size.coerceAtMost(5)))
            binding.subwayDownArrivalList.adapter = SubwayArrivalItemAdapter(context, arrivalListDown.subList(0, arrivalListDown.size.coerceAtMost(5)))
            binding.subwayUpArrivalList.layoutManager = LinearLayoutManager(context)
            binding.subwayDownArrivalList.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubwayArrivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_subway_arrival, parent, false)
        return SubwayArrivalViewHolder(CardSubwayArrivalBinding.bind(view))
    }

    override fun onBindViewHolder(holder: SubwayArrivalViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return subwayList.size
    }

    fun setSubwayData(subwayList: List<SubwayQuery.Subway>) {
        this.subwayList = subwayList
        notifyDataSetChanged()
    }
}