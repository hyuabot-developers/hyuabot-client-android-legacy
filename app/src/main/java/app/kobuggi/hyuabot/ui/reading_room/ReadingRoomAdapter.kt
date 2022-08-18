package app.kobuggi.hyuabot.ui.reading_room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.ReadingRoomQuery
import app.kobuggi.hyuabot.databinding.CardReadingRoomAdBinding
import app.kobuggi.hyuabot.databinding.CardReadingRoomBinding
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class ReadingRoomAdapter(private var items: List<ReadingRoomItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val roomNameStringID = mapOf(
        "제1열람실 (2F)" to R.string.room_name_1_2f,
        "제2열람실 (4F)" to R.string.room_name_2_4f,
        "제3열람실 (4F)" to R.string.room_name_3_4f,
        "HOLMZ 열람석" to R.string.holmz_room,
        "법학 제1열람실[3층]" to R.string.law_room_1_3f,
        "법학 제2열람실A[4층]" to R.string.law_room_2a_4f,
        "법학 제2열람실B[4층]" to R.string.law_room_2b_4f,
        "제1열람실[지하1층]" to R.string.room_name_1_underground_1f,
        "제2열람실[지하1층]" to R.string.room_name_2_underground_1f,
        "제3열람실[3층]" to R.string.room_name_3_3f,
    )


    inner class ReadingRoomViewHolder(private val binding: CardReadingRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReadingRoomQuery.ReadingRoom) {
            binding.readingRoomName.text = if(roomNameStringID.containsKey(item.roomName)) {
                binding.root.context.getString(roomNameStringID[item.roomName]!!)
            } else {
                item.roomName
            }
            binding.currentSeat.text = item.availableSeat.toString()
            binding.totalSeat.text = item.activeSeat.toString()
        }
    }

    inner class ReadingRoomADViewHolder(private val binding: CardReadingRoomAdBinding) : RecyclerView.ViewHolder(binding.root){
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.card_reading_room, parent, false)
            return ReadingRoomViewHolder(CardReadingRoomBinding.bind(view))
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_reading_room_ad, parent, false)
        return ReadingRoomADViewHolder(CardReadingRoomAdBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            (holder as ReadingRoomViewHolder).bind(items[position].room!!)
        } else {
            val nativeAd = items[position].nativeAd as NativeAd
            populateNativeAD(nativeAd, (holder as ReadingRoomADViewHolder).getADView())
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].nativeAd == null) 1 else 0
    }

    fun setReadingRooms(items: List<ReadingRoomItem>) {
        this.items = items
        notifyDataSetChanged()
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