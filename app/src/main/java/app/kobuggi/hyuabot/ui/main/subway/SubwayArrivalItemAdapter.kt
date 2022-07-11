package app.kobuggi.hyuabot.ui.main.subway

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemSubwayArrivalBinding

class SubwayArrivalItemAdapter(private val context: Context, private var arrivalList: List<SubwayArrivalItem>) : RecyclerView.Adapter<SubwayArrivalItemAdapter.SubwayArrivalViewHolder>() {
    inner class SubwayArrivalViewHolder(private val binding: ItemSubwayArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.subwayHeading.text = arrivalList[position].terminalStation.replace("신인천", "인천")
            binding.subwayDepartureTime.text = context.getString(R.string.subway_arrival_item, arrivalList[position].remainedTime)
            binding.subwayCurrent.text = arrivalList[position].currentStation ?: context.getString(R.string.subway_timetable)
            if(arrivalList[position].currentStation == null){
                binding.subwayCurrent.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.darker_gray, null))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubwayArrivalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_subway_arrival, parent, false)
        return SubwayArrivalViewHolder(ItemSubwayArrivalBinding.bind(view))
    }

    override fun onBindViewHolder(holder: SubwayArrivalViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return arrivalList.size
    }

    fun setArrivalList(arrivalList: List<SubwayArrivalItem>) {
        this.arrivalList = arrivalList
        notifyDataSetChanged()
    }
}