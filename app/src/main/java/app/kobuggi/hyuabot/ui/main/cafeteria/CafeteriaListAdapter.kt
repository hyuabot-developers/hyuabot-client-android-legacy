package app.kobuggi.hyuabot.ui.main.cafeteria

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.CafeteriaMenuQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.CardCafeteriaMenuBinding
import com.google.android.gms.maps.model.LatLng
import java.time.LocalTime

class CafeteriaListAdapter(private val context: Context, private var cafeteriaList: List<CafeteriaMenuQuery.Cafeterium>, val onClickLocationButton: (location: LatLng, title: String) -> Unit) : RecyclerView.Adapter<CafeteriaListAdapter.CafeteriaViewHolder>() {
    lateinit var cafeteriaTimeListAdapter: CafeteriaTimeListAdapter
    private val cafeteriaLocationList = listOf(
        LatLng(37.298236,126.8344932),
        LatLng(37.298236,126.8344932),
        LatLng(37.2912978,126.8364081),
        LatLng(37.298236,126.8344932),
        LatLng(37.2956619,126.837185)
    )
    inner class CafeteriaViewHolder(private val binding: CardCafeteriaMenuBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.cafeteriaName.text = cafeteriaList[position].cafeteriaName

            val menuGroupByTime = cafeteriaList[position].menu.groupBy { it.timeType }
            val timeList = arrayListOf<CafeteriaTimeItem>()

            val now = LocalTime.now()
            val targetTime = if (now.hour < 10 && menuGroupByTime.containsKey("조식")){
                "조식"
            } else if (now.hour > 15 && menuGroupByTime.containsKey("석식")){
                "석식"
            } else {
                "중식"
            }

            for (time in listOf("조식", "중식", "석식")) {
                if(menuGroupByTime.containsKey(time)){
                    timeList.add(CafeteriaTimeItem(time, menuGroupByTime[time]!!, binding.expandButton.isSelected || time == targetTime))
                }
            }
            menuGroupByTime.keys.forEach {
                if(it !in listOf("조식", "중식", "석식")){
                    timeList.add(CafeteriaTimeItem(it, menuGroupByTime[it]!!, binding.expandButton.isSelected || it.contains(targetTime)))
                }
            }

            cafeteriaTimeListAdapter = CafeteriaTimeListAdapter(context, timeList)
            binding.menuList.adapter = cafeteriaTimeListAdapter
            binding.menuList.layoutManager = LinearLayoutManager(context)
            binding.expandButton.setOnClickListener {
                binding.expandButton.isSelected = !binding.expandButton.isSelected
                notifyDataSetChanged()
            }
            binding.cafeteriaLocation.setOnClickListener {
                onClickLocationButton(cafeteriaLocationList[position], cafeteriaList[position].cafeteriaName)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeteriaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_cafeteria_menu, parent, false)
        return CafeteriaViewHolder(CardCafeteriaMenuBinding.bind(view))
    }

    override fun onBindViewHolder(holder: CafeteriaViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return cafeteriaList.size
    }

    fun setCafeteriaList(cafeteriaList: List<CafeteriaMenuQuery.Cafeterium>) {
        this.cafeteriaList = cafeteriaList
        notifyDataSetChanged()
    }
}