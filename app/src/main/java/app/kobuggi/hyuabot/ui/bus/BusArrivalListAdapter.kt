package app.kobuggi.hyuabot.ui.bus

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.BusQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.CardBusAdBinding
import app.kobuggi.hyuabot.databinding.CardBusArrivalBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import java.time.LocalTime

class BusArrivalListAdapter(private val context: Context, private var busList: List<BusRouteItem>, private val onClickTimetableButton: (routeName: String, routeColor: String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val routeColor = hashMapOf(
        "10-1" to "#33cc99",
        "707-1" to "#E60012",
        "3102" to "#E60012",
    )
    inner class BusArrivalViewHolder(private val binding: CardBusArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(arrivalList: BusQuery.Bus) {
            binding.busRouteName.setBackgroundColor(Color.parseColor(routeColor[arrivalList.routeName] ?: "#000000"))
            binding.busRouteName.text = context.getString(R.string.bus_route_name, arrivalList.routeName, arrivalList.stopName)
            binding.busTerminalStop.text = arrivalList.terminalStop

            val now = LocalTime.now()
            val timetable = arrivalList.timetable.filter { LocalTime.parse(it.departureTime.toString()).isAfter(now) }
            val adapter = BusArrivalTimeAdapter(context, arrivalList.realtime, timetable){
                onClickTimetableButton(arrivalList.routeName, routeColor[arrivalList.routeName]!!)
            }
            binding.busArrivalList.adapter = adapter
            binding.busArrivalList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            if (arrivalList.realtime.size + timetable.size > 0) {
                binding.busArrivalList.visibility = View.VISIBLE
                binding.busNoData.visibility = View.GONE
            } else {
                binding.busArrivalList.visibility = View.GONE
                binding.busNoData.visibility = View.VISIBLE
            }
        }
    }

    inner class BusArrivalAdViewHolder(private val binding: CardBusAdBinding) : RecyclerView.ViewHolder(binding.root){
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_bus_arrival, parent, false)
            return BusArrivalViewHolder(CardBusArrivalBinding.bind(view))
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_bus_ad, parent, false)
        return BusArrivalAdViewHolder(CardBusAdBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            (holder as BusArrivalViewHolder).bind(busList[position].arrivalList!!)
        } else {
            val nativeAd = busList[position].nativeAd as NativeAd
            populateNativeAD(nativeAd, (holder as BusArrivalAdViewHolder).getADView())
        }
    }

    override fun getItemCount(): Int {
        return busList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (busList[position].nativeAd == null) 1 else 0
    }

    fun setBusTimetable(busList: List<BusRouteItem>) {
        this.busList = busList
        notifyItemRangeChanged(0, busList.size)
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