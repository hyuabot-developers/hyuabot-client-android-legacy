package app.kobuggi.hyuabot.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.databinding.ItemMapSearchBinding
import com.google.android.gms.maps.model.LatLng

class MapSearchResultAdapter(private val context: Context, private var result: List<AppDatabaseItem>, val onClickItem: (location: LatLng, title: String) -> Unit) : RecyclerView.Adapter<MapSearchResultAdapter.MapSearchResultViewHolder>() {

    inner class MapSearchResultViewHolder(private val binding: ItemMapSearchBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.searchResultCategory.visibility = ViewGroup.GONE
            binding.searchResultName.text = result[position].name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapSearchResultViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_map_search, parent, false)
        return MapSearchResultViewHolder(ItemMapSearchBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MapSearchResultViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return result.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSearchResult(result: List<AppDatabaseItem>) {
        this.result = result
        notifyDataSetChanged()
    }
}