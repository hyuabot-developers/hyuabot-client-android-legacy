package app.kobuggi.hyuabot.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.data.database.AppDatabaseItem
import app.kobuggi.hyuabot.databinding.ItemMapSearchBinding

class MapSearchResultAdapter(private val context: Context, private var result: List<AppDatabaseItem>, val onClickItem: (item: AppDatabaseItem) -> Unit) : RecyclerView.Adapter<MapSearchResultAdapter.MapSearchResultViewHolder>() {
    private val categoryStringID = hashMapOf(
        "on campus" to R.string.on_campus,
        "fast food" to R.string.fast_food,
        "korean" to R.string.food_korean,
        "cafe" to R.string.cafe,
        "vietnamese" to R.string.food_vietnamese,
        "chinese" to R.string.food_chinese,
        "chicken" to R.string.chicken,
        "bakery" to R.string.bakery,
        "western" to R.string.food_western,
        "other food" to R.string.other_food,
        "japanese" to R.string.food_japanese,
        "pizza" to R.string.pizza,
        "pub" to R.string.pub,
        "meat" to R.string.meat,
        "building" to R.string.building,
    )


    inner class MapSearchResultViewHolder(private val binding: ItemMapSearchBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.searchResultName.text = result[position].name
            if (result[position].category.isEmpty()) {
                binding.searchResultCategory.visibility = ViewGroup.GONE
            } else {
                binding.searchResultCategory.visibility = ViewGroup.VISIBLE
                binding.searchResultCategory.text = context.getString(categoryStringID[result[position].category]!!)
            }
            binding.searchResultItem.setOnClickListener {
                onClickItem(result[position])
            }
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