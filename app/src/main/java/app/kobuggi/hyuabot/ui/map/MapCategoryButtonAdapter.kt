package app.kobuggi.hyuabot.ui.map

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ButtonMapCategoryBinding

class MapCategoryButtonAdapter(private val context: Context, val onClickItem: (category: Int, categoryKey: String) -> Unit) : RecyclerView.Adapter<MapCategoryButtonAdapter.MapCategoryButtonViewHolder>() {
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


    inner class MapCategoryButtonViewHolder(private val binding: ButtonMapCategoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.mapCategoryButton.text = context.getString(categoryStringID.values.elementAt(position))
            binding.mapCategoryButton.setOnClickListener {
                onClickItem(categoryStringID.values.elementAt(position), categoryStringID.keys.elementAt(position))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MapCategoryButtonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_map_category, parent, false)
        return MapCategoryButtonViewHolder(ButtonMapCategoryBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MapCategoryButtonViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return categoryStringID.keys.size
    }
}