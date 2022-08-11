package app.kobuggi.hyuabot.ui.menu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.ButtonMenuBinding

class MenuListAdapter(private val context: Context, private val menuList: List<MenuButton>, private val onClickButton: (Int) -> Unit) : RecyclerView.Adapter<MenuListAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ButtonMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.menuButtonText.text = context.getString(menuList[position].stringResourceID)
            binding.menuButtonImage.setImageResource(menuList[position].imageResourceID)
            binding.menuButton.setOnClickListener {
                onClickButton(menuList[position].stringResourceID)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.button_menu, parent, false)
        return ViewHolder(ButtonMenuBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

}