package app.kobuggi.hyuabot.ui.main.cafeteria

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.CafeteriaMenuQuery
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemCafeteriaMenuBinding

class CafeteriaMenuListAdapter(private val context: Context, private val menuList: List<CafeteriaMenuQuery.Menu>) : RecyclerView.Adapter<CafeteriaMenuListAdapter.MenuItemViewHolder>() {
    inner class MenuItemViewHolder(private val binding: ItemCafeteriaMenuBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.menuDescription.text = menuList[position].menu
            binding.menuPrice.text = menuList[position].price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cafeteria_menu, parent, false)
        return MenuItemViewHolder(ItemCafeteriaMenuBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}