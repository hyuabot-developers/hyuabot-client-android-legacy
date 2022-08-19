package app.kobuggi.hyuabot.ui.subway

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemSubwayArrivalBinding
import app.kobuggi.hyuabot.ui.subway.SubwayArrivalItem
import app.kobuggi.hyuabot.utils.LocaleHelper
import java.util.*
import kotlin.math.min

class SubwayArrivalItemAdapter(private val context: Context, private var arrivalList: List<SubwayArrivalItem>, private val onItemClick: () -> Unit) : RecyclerView.Adapter<SubwayArrivalItemAdapter.SubwayArrivalViewHolder>() {
    private var itemCount = arrivalList.filter { it.currentStation != null }.size
    private val terminalStation = hashMapOf(
        "인천" to R.string.incheon,
        "신인천" to R.string.incheon,
        "오이도" to R.string.oido,
        "청량리" to R.string.cheongnyeongli,
        "왕십리" to R.string.wangsimni,
        "죽전" to R.string.jukjeon,
        "고색" to R.string.gosaek,
        "안산" to R.string.ansan,
        "당고개" to R.string.danggogae,
        "노원" to R.string.nowon,
        "한성대" to R.string.hansung_univ,
        "사당" to R.string.sadang,
        "금정" to R.string.geumjeong,
    )

    inner class SubwayArrivalViewHolder(private val binding: ItemSubwayArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.subwayHeading.text = context.getString(R.string.bound_for, terminalStation[arrivalList[position].terminalStation]?.let { context.getString(it) } ?: arrivalList[position].terminalStation)
            binding.subwayDepartureTime.text = context.getString(R.string.subway_arrival_item, arrivalList[position].remainedTime)
            binding.subwayCurrent.text = arrivalList[position].currentStation ?: context.getString(R.string.subway_timetable)
            if(arrivalList[position].currentStation == null){
                binding.subwayCurrent.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.darker_gray, null))
            }
            binding.subwayTimetableItem.setOnClickListener {
                onItemClick()
            }
            Log.d("locale", LocaleHelper.locale.toString())
            Log.d("locale", Locale.getDefault().toString())
            if (LocaleHelper.locale.toString().startsWith("ko") || Locale.getDefault().toString().startsWith("ko")) {
                binding.subwayCurrent.visibility = ViewGroup.VISIBLE
            } else {
                binding.subwayCurrent.visibility = ViewGroup.GONE
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
        return min(itemCount, arrivalList.size)
    }

    fun setExpanded(isExpanded: Boolean) {
        val oldItemCount = itemCount
        this.itemCount = if (isExpanded) arrivalList.size else arrivalList.filter { it.currentStation != null }.size
        if (itemCount > oldItemCount) {
            notifyItemRangeInserted(oldItemCount, itemCount - oldItemCount)
        } else {
            notifyItemRangeRemoved(itemCount, oldItemCount - itemCount)
        }
    }
}