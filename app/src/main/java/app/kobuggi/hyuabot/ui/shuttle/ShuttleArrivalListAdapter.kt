package app.kobuggi.hyuabot.ui.shuttle

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
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ShuttleTimetableQuery
import app.kobuggi.hyuabot.databinding.CardShuttleAdBinding
import app.kobuggi.hyuabot.databinding.CardShuttleArrivalBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.maps.model.LatLng
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ShuttleArrivalListAdapter(private val context: Context,
                                private var stopList: List<ShuttleStopInfo>, timetable: List<ShuttleTimetableQuery.Timetable>, val onClickLocationButton: (location: LatLng, titleID: Int) -> Unit, val onClickCard: (stopID: Int, shuttleType: String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var shuttleTimetable: List<ShuttleTimetableQuery.Timetable> = timetable
    private val timeDelta = hashMapOf(
        R.string.dormitory to arrayListOf(-5, -5, -5),
        R.string.shuttlecock_o to arrayListOf(0, 0, 0),
        R.string.station to arrayListOf(10, 0, 10),
        R.string.terminal to arrayListOf(0, 10, 15),
        R.string.shuttlecock_i to arrayListOf(20, 20, 25)
    )
    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    inner class ShuttleArrivalViewHolder(private val binding: CardShuttleArrivalBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.shuttleStopName.text = context.getString(stopList[position].nameID)

            if (stopList[position].nameID == R.string.station) {
                binding.shuttleDH.visibility = View.VISIBLE
                binding.shuttleDY.visibility = View.GONE
                binding.shuttleStopDivider.visibility = View.GONE
                binding.shuttleTypeDH.text = context.getString(R.string.shuttle_type_D)
            } else if (stopList[position].nameID == R.string.terminal) {
                binding.shuttleDH.visibility = View.GONE
                binding.shuttleDY.visibility = View.VISIBLE
                binding.shuttleStopDivider.visibility = View.GONE
                binding.shuttleTypeDY.text = context.getString(R.string.shuttle_type_D)
            }

            val now = LocalTime.now()
            val timetableByStopDH = if(position == 0){
                shuttleTimetable.filter { it.shuttleType == "DH" && it.startStop == "Dormitory" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) }
            } else {
                shuttleTimetable.filter { it.shuttleType == "DH" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![0].toLong()) }
            }
            val timetableByStopDY = if(position == 0){
                shuttleTimetable.filter { it.shuttleType == "DY" && it.startStop == "Dormitory" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) }
            } else {
                shuttleTimetable.filter { it.shuttleType == "DY" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![1].toLong()) }
            }
            val timetableByStopC = if(position == 0){
                shuttleTimetable.filter { it.shuttleType == "C" && it.startStop == "Dormitory" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) }
            } else {
                shuttleTimetable.filter { it.shuttleType == "C" && LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) > now }.map { LocalTime.parse(it.shuttleTime, formatter).plusMinutes(timeDelta[stopList[position].nameID]!![2].toLong()) }
            }
            val shuttleDHAdapter = ShuttleArrivalTimeAdapter(context, now, timetableByStopDH){
                onClickCard(stopList[position].nameID, "DH")
            }
            val shuttleDYAdapter = ShuttleArrivalTimeAdapter(context, now, timetableByStopDY){
                onClickCard(stopList[position].nameID, "DY")
            }
            val shuttleCAdapter = ShuttleArrivalTimeAdapter(context, now, timetableByStopC){
                onClickCard(stopList[position].nameID, "C")
            }
            binding.shuttleDHTime.adapter = shuttleDHAdapter
            binding.shuttleDHTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.shuttleDYTime.adapter = shuttleDYAdapter
            binding.shuttleDYTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.shuttleCTime.adapter = shuttleCAdapter
            binding.shuttleCTime.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            if (timetableByStopDH.isEmpty()){
                binding.shuttleDHNoData.visibility = View.VISIBLE
                binding.shuttleDHTime.visibility = View.GONE
            } else {
                binding.shuttleDHNoData.visibility = View.GONE
                binding.shuttleDHTime.visibility = View.VISIBLE
            }
            if (timetableByStopDY.isEmpty()){
                binding.shuttleDYNoData.visibility = View.VISIBLE
                binding.shuttleDYTime.visibility = View.GONE
            } else {
                binding.shuttleDYNoData.visibility = View.GONE
                binding.shuttleDYTime.visibility = View.VISIBLE
            }
            if (timetableByStopC.isEmpty()){
                binding.shuttleCNoData.visibility = View.VISIBLE
                binding.shuttleCTime.visibility = View.GONE
            } else {
                binding.shuttleCNoData.visibility = View.GONE
                binding.shuttleCTime.visibility = View.VISIBLE
            }

            binding.shuttleStopLocation.setOnClickListener{
                onClickLocationButton(stopList[position].location, stopList[position].nameID)
            }
            binding.expandButton.setOnClickListener {
                binding.expandButton.isSelected = !binding.expandButton.isSelected
                shuttleDHAdapter.itemCount = if(binding.expandButton.isSelected) 5 else 2
                shuttleDYAdapter.itemCount = if(binding.expandButton.isSelected) 5 else 2
                shuttleCAdapter.itemCount = if(binding.expandButton.isSelected) 5 else 2
            }
            binding.shuttleDH.setOnClickListener {
                onClickCard(stopList[position].nameID, "DH")
            }
            binding.shuttleDY.setOnClickListener {
                onClickCard(stopList[position].nameID, "DY")
            }
            binding.shuttleC.setOnClickListener {
                onClickCard(stopList[position].nameID, "C")
            }
        }
    }

    inner class ShuttleArrivalAdViewHolder(private val binding: CardShuttleAdBinding) : RecyclerView.ViewHolder(binding.root){
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_shuttle_arrival, parent, false)
            return ShuttleArrivalViewHolder(CardShuttleArrivalBinding.bind(view))
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_shuttle_ad, parent, false)
        return ShuttleArrivalAdViewHolder(CardShuttleAdBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            (holder as ShuttleArrivalViewHolder).bind(position)
        } else {
            val nativeAd = stopList[position].nativeAd as NativeAd
            populateNativeAD(nativeAd, (holder as ShuttleArrivalAdViewHolder).getADView())
        }
    }

    override fun getItemCount(): Int {
        return stopList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (timeDelta.containsKey(stopList[position].nameID)) 1 else 0
    }

    fun setShuttleTimetable(shuttleTimetable: List<ShuttleTimetableQuery.Timetable>) {
        this.shuttleTimetable = shuttleTimetable
        notifyItemRangeChanged(0, itemCount)
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

    fun setShuttleArrivalList(list: List<ShuttleStopInfo>?) {
        stopList = list ?: listOf()
        notifyItemRangeChanged(0, itemCount)
    }
}