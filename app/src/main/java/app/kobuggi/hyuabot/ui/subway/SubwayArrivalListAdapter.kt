package app.kobuggi.hyuabot.ui.subway

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.SubwayQuery
import app.kobuggi.hyuabot.databinding.CardBusAdBinding
import app.kobuggi.hyuabot.databinding.CardSubwayAdBinding
import app.kobuggi.hyuabot.databinding.CardSubwayArrivalBinding
import app.kobuggi.hyuabot.ui.bus.BusArrivalListAdapter
import app.kobuggi.hyuabot.ui.subway.SubwayArrivalItemAdapter
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.lang.Integer.parseInt
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

class SubwayArrivalListAdapter(private val context: Context, private var subwayList: List<SubwayRouteItem>, private val onClickArrivalItem: (String, String, String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val routeColor = hashMapOf(
        "4호선" to "#00A5DE",
        "수인분당선" to "#F5A200",
    )
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    inner class SubwayArrivalViewHolder(private val binding: CardSubwayArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(arrivalItem: SubwayQuery.Subway) {
            binding.subwayRouteName.setBackgroundColor(Color.parseColor(routeColor[arrivalItem.routeName] ?: "#000000"))
            binding.subwayRouteName.text = arrivalItem.routeName
            val now = LocalDateTime.now()

            val arrivalListUp = arrayListOf<SubwayArrivalItem>()
            arrivalItem.realtime.filter { it.heading == "up" && Duration.between(LocalDateTime.parse(it.updateTime, formatter), now).toMinutes() < parseInt(it.remainedTime) }.forEach { realtime ->
                val remainedTime = parseInt(realtime.remainedTime) - Duration.between(LocalDateTime.parse(realtime.updateTime, formatter), now).toMinutes().toInt()
                arrivalListUp.add(SubwayArrivalItem(remainedTime, realtime.terminalStation, realtime.currentStation))
            }
            var maxRealtimeMinute = if(arrivalListUp.isEmpty()) 0 else arrivalListUp.maxBy { it.remainedTime }.remainedTime
            arrivalItem.timetable.filter { it.heading == "up" && Duration.between(LocalTime.parse(it.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue > maxRealtimeMinute }.forEach { timetable ->
                val remainedTime = Duration.between(LocalTime.parse(timetable.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue
                arrivalListUp.add(SubwayArrivalItem(remainedTime, timetable.terminalStation, null))
            }

            val arrivalListDown = arrayListOf<SubwayArrivalItem>()
            arrivalItem.realtime.filter { it.heading == "down" && Duration.between(LocalDateTime.parse(it.updateTime, formatter), now).toMinutes() < parseInt(it.remainedTime) }.forEach { realtime ->
                val remainedTime = parseInt(realtime.remainedTime) - Duration.between(LocalDateTime.parse(realtime.updateTime, formatter), now).toMinutes().toInt()

                arrivalListDown.add(SubwayArrivalItem(remainedTime, realtime.terminalStation, realtime.currentStation))
            }
            maxRealtimeMinute = if(arrivalListDown.isEmpty()) 0 else arrivalListDown.maxBy { it.remainedTime }.remainedTime
            arrivalItem.timetable.filter { it.heading == "down" && Duration.between(LocalTime.parse(it.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue > maxRealtimeMinute }.forEach { timetable ->
                val remainedTime = Duration.between(LocalTime.parse(timetable.departureTime), now.toLocalTime()).toMinutes().toInt().absoluteValue
                arrivalListDown.add(SubwayArrivalItem(remainedTime, timetable.terminalStation, null))
            }

            val subwayUPAdapter = SubwayArrivalItemAdapter(context, arrivalListUp.subList(0, arrivalListUp.size.coerceAtMost(5))){
                onClickArrivalItem(arrivalItem.routeName, routeColor[arrivalItem.routeName]!!, "up")
            }
            binding.subwayUpArrivalList.adapter = subwayUPAdapter
            val subwayDownAdapter = SubwayArrivalItemAdapter(context, arrivalListDown.subList(0, arrivalListDown.size.coerceAtMost(5))){
                onClickArrivalItem(arrivalItem.routeName, routeColor[arrivalItem.routeName]!!, "down")
            }
            binding.subwayDownArrivalList.adapter = subwayDownAdapter
            binding.subwayUpArrivalList.layoutManager = LinearLayoutManager(context)
            binding.subwayDownArrivalList.layoutManager = LinearLayoutManager(context)
            binding.expandButton.setOnClickListener {
                binding.expandButton.isSelected = !binding.expandButton.isSelected
                subwayUPAdapter.setExpanded(binding.expandButton.isSelected)
                subwayDownAdapter.setExpanded(binding.expandButton.isSelected)
            }
        }
    }

    inner class SubwayArrivalAdViewHolder(private val binding: CardSubwayAdBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            val adView: NativeAdView = binding.adView
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        }

        fun getADView(): NativeAdView {
            return binding.adView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_subway_arrival, parent, false)
            return SubwayArrivalViewHolder(CardSubwayArrivalBinding.bind(view))
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_subway_ad, parent, false)
        return SubwayArrivalAdViewHolder(CardSubwayAdBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            (holder as SubwayArrivalViewHolder).bind(subwayList[position].arrivalList!!)
        } else {
            val nativeAd = subwayList[position].nativeAd as NativeAd
            populateNativeAD(nativeAd, (holder as SubwayArrivalAdViewHolder).getADView())
        }
    }

    override fun getItemViewType(position: Int): Int = if (subwayList[position].nativeAd == null) 1 else 0

    override fun getItemCount(): Int {
        return subwayList.size
    }

    fun setSubwayData(subwayList: List<SubwayRouteItem>) {
        this.subwayList = subwayList
        notifyItemRangeChanged(0, subwayList.size)
    }

    private fun populateNativeAD(nativeAD: NativeAd, adView: NativeAdView) {
        (adView.headlineView as TextView).text = nativeAD.headline
        (adView.bodyView as TextView).text = nativeAD.body
        (adView.callToActionView as Button).text = nativeAD.callToAction

        val icon = nativeAD.icon
        if (icon != null) {
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            (adView.iconView as ImageView).visibility = View.VISIBLE
        } else {
            adView.iconView?.visibility = View.INVISIBLE
        }
        if (nativeAD.price != null) {
            (adView.priceView as TextView).text = nativeAD.price
            (adView.priceView as TextView).visibility = View.VISIBLE
        } else {
            adView.priceView?.visibility = View.INVISIBLE
        }
        if (nativeAD.store != null) {
            (adView.storeView as TextView).text = nativeAD.store
            (adView.storeView as TextView).visibility = View.VISIBLE
        } else {
            adView.storeView?.visibility = View.INVISIBLE
        }
        if (nativeAD.advertiser != null) {
            (adView.advertiserView as TextView).text = nativeAD.advertiser
            (adView.advertiserView as TextView).visibility = View.VISIBLE
        } else {
            adView.advertiserView?.visibility = View.INVISIBLE
        }
        if (nativeAD.starRating != null) {
            (adView.starRatingView as RatingBar).rating = nativeAD.starRating!!.toFloat()
            (adView.starRatingView as RatingBar).visibility = View.VISIBLE
        } else {
            adView.starRatingView?.visibility = View.INVISIBLE
        }
        adView.setNativeAd(nativeAD)
    }
}