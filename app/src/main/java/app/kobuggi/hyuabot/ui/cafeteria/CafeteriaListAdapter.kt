package app.kobuggi.hyuabot.ui.cafeteria

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.CafeteriaMenuQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.CardBusAdBinding
import app.kobuggi.hyuabot.databinding.CardBusArrivalBinding
import app.kobuggi.hyuabot.databinding.CardCafeteriaAdBinding
import app.kobuggi.hyuabot.databinding.CardCafeteriaMenuBinding
import app.kobuggi.hyuabot.ui.bus.BusArrivalListAdapter
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.maps.model.LatLng
import java.time.LocalTime

class CafeteriaListAdapter(private val context: Context, private var cafeteriaList: List<CafeteriaItem>, val onClickLocationButton: (location: LatLng, title: String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var cafeteriaTimeListAdapter: CafeteriaTimeListAdapter
    private val cafeteriaLocationList = listOf(
        LatLng(37.298236,126.8344932),
        LatLng(37.298236,126.8344932),
        LatLng(37.2912978,126.8364081),
        LatLng(37.298236,126.8344932),
        LatLng(37.2956619,126.837185)
    )
    inner class CafeteriaViewHolder(private val binding: CardCafeteriaMenuBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(cafeteriaItem: CafeteriaMenuQuery.Cafeterium) {
            binding.cafeteriaName.text = cafeteriaItem.cafeteriaName

            val menuGroupByTime = cafeteriaItem.menu.groupBy { it.timeType }
            val timeList = arrayListOf(
                CafeteriaTimeItem(context.getString(R.string.notification), listOf(CafeteriaMenuQuery.Menu(
                    menu = context.getString(R.string.no_cafeteria_menu), timeType = "", price = ""
                )), isExpand = true)
            )

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

            if(timeList.size > 1){
                timeList.removeAt(0)
            }

            cafeteriaTimeListAdapter = CafeteriaTimeListAdapter(context, timeList)
            binding.menuList.adapter = cafeteriaTimeListAdapter
            binding.menuList.layoutManager = LinearLayoutManager(context)
            binding.expandButton.setOnClickListener {
                binding.expandButton.isSelected = !binding.expandButton.isSelected
                notifyItemRangeChanged(0, cafeteriaList.size)
            }
            binding.cafeteriaLocation.setOnClickListener {
                onClickLocationButton(cafeteriaLocationList[position], cafeteriaItem.cafeteriaName)
            }
        }
    }

    inner class CafeteriaADViewHolder(private val binding: CardCafeteriaAdBinding) : RecyclerView.ViewHolder(binding.root){
        init {
            val adView: NativeAdView = binding.adView
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.mediaView = adView.findViewById(R.id.ad_media)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
        }

        fun getADView(): NativeAdView {
            return binding.adView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_cafeteria_menu, parent, false)
            return CafeteriaViewHolder(CardCafeteriaMenuBinding.bind(view))
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_cafeteria_ad, parent, false)
        return CafeteriaADViewHolder(CardCafeteriaAdBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            (holder as CafeteriaViewHolder).bind(cafeteriaList[position].menuList!!)
        } else {
            val nativeAd = cafeteriaList[position].nativeAd as NativeAd
            populateNativeAD(nativeAd, (holder as CafeteriaADViewHolder).getADView())
        }
    }

    override fun getItemCount(): Int {
        return cafeteriaList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (cafeteriaList[position].nativeAd == null) 1 else 0
    }

    fun setCafeteriaList(cafeteriaList: List<CafeteriaItem>) {
        this.cafeteriaList = cafeteriaList
        notifyItemRangeChanged(0, cafeteriaList.size)
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