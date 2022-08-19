package app.kobuggi.hyuabot.ui.cafeteria

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ItemCafeteriaTimeBinding

class CafeteriaTimeListAdapter(private val context: Context, private val menuList: ArrayList<CafeteriaTimeItem>) : RecyclerView.Adapter<CafeteriaTimeListAdapter.MenuListViewHolder>() {
    private val menuType = hashMapOf(
        "조식" to R.string.breakfast,
        "중식" to R.string.lunch,
        "석식" to R.string.dinner
    )
    inner class MenuListViewHolder(private val binding: ItemCafeteriaTimeBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            binding.cafeteriaTime.text = menuType[menuList[position].timeType]?.let { context.getString(it) } ?: menuList[position].timeType

            val cafeteriaMenuListAdapter = CafeteriaMenuListAdapter(context, menuList[position].menuList)
            binding.menuList.adapter = cafeteriaMenuListAdapter
            binding.menuList.layoutManager = LinearLayoutManager(context)

            if (position == menuList.size - 1){
                binding.menuListDivider.visibility = ViewGroup.INVISIBLE
            }

            if (menuList[position].menuList.isEmpty() || menuList[position].isExpand){
                binding.cafeteriaTime.visibility = ViewGroup.VISIBLE
                binding.menuList.visibility = ViewGroup.VISIBLE
            } else {
                binding.cafeteriaTime.visibility = ViewGroup.GONE
                binding.menuList.visibility = ViewGroup.GONE
                binding.menuListDivider.visibility = ViewGroup.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cafeteria_time, parent, false)
        return MenuListViewHolder(ItemCafeteriaTimeBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MenuListViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}