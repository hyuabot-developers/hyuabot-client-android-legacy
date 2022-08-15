package app.kobuggi.hyuabot.ui.shuttle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kobuggi.hyuabot.R
import app.kobuggi.hyuabot.databinding.CardShuttleInformationBinding

class ShuttleInformationAdapter(private var informationList: List<ShuttleInformationItem>) :
    RecyclerView.Adapter<ShuttleInformationAdapter.ShuttleInformationViewHolder>() {
    inner class ShuttleInformationViewHolder(private val binding: CardShuttleInformationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ShuttleInformationItem) {
            binding.shuttleInformationImage.setImageResource(item.imageResourceID)
            binding.shuttleInformationTitle.text = item.title
            binding.shuttleInformationContent.text = item.content
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShuttleInformationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_shuttle_information, parent, false)
        return ShuttleInformationViewHolder(CardShuttleInformationBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ShuttleInformationViewHolder, position: Int) {
        holder.bind(informationList[position])
    }

    override fun getItemCount(): Int = informationList.size

    fun setClosestShuttleStop(closestShuttleStop: String) {
        informationList[0].content = closestShuttleStop
        notifyItemChanged(0)
    }
}