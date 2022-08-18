package app.kobuggi.hyuabot.ui.contact

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.databinding.CardContactAdBinding
import app.kobuggi.hyuabot.databinding.CardSubwayAdBinding
import app.kobuggi.hyuabot.databinding.CardSubwayArrivalBinding
import app.kobuggi.hyuabot.databinding.ItemContactSearchBinding
import app.kobuggi.hyuabot.ui.subway.SubwayArrivalListAdapter
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class ContactListAdapter(private var result: List<ContactItem>, private val onClickItem: (ContactItem) -> Unit, private val onLongClickItem : (Int, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedPosition = -1
    inner class ContactItemViewHolder(private val binding: ItemContactSearchBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactItem) {
            binding.searchResultName.text = item.name
            binding.searchResultPhone.text = item.phone
            binding.searchResultItem.setOnLongClickListener {
                setSelectedPosition(absoluteAdapterPosition)
                true
            }
            binding.searchResultItem.setOnClickListener {
                onClickItem(ContactItem(item.name, item.phone))
            }
        }
    }

    inner class ContactADViewHolder(private val binding: CardContactAdBinding) : RecyclerView.ViewHolder(binding.root){
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact_search, parent, false)
            return ContactItemViewHolder(ItemContactSearchBinding.bind(view))
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_contact_ad, parent, false)
        return ContactADViewHolder(CardContactAdBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            (holder as ContactItemViewHolder).bind(result[position])
        } else {
            val nativeAd = result[position].nativeAd as NativeAd
            populateNativeAD(nativeAd, (holder as ContactADViewHolder).getADView())
        }
    }

    override fun getItemViewType(position: Int): Int = if (result[position].nativeAd == null) 1 else 0

    override fun getItemCount(): Int {
        return result.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setResult(result: List<ContactItem>) {
        this.result = result
        notifyDataSetChanged()
    }

    fun setSelectedPosition(adapterPosition: Int) {
        onLongClickItem(selectedPosition, adapterPosition)
        selectedPosition = adapterPosition
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